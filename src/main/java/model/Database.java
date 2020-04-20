package model;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import static com.mongodb.client.model.Filters.*;
import static model.Simulation.START_STATUS;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;

public class Database {
    private final String DATABASE_NAME = "star_search";
    private final String SIMULATION_METADATA_COLLECTION = "simulation_meta_data";
    private final String SIMULATION_STATE_COLLECTION = "simulation_state";
    private final String SNAPSHOT_ID_FIELD = "snapshot_ids";
    private final String SNAPSHOT_ID_INDEX_FIELD = "snapshot_id_index";

    public static final String USER_FIELD = "user";

    private final String LOCAL_STATE_FILE_NAME = "serialization.ser";
    private final int UPLOAD_SIZE = 1024;

    private MongoDatabase database;
    private GridFSBucket gridFSBucket;
    private MongoCollection<Document> collection;

    public Database() {
        MongoClient mongoClient = MongoClients.create(
                "mongodb+srv://admin_one:gdPXGf5dCVXR3zB@master-hhsfd.mongodb.net/test?retryWrites=true&w=majority"
        );
        database = mongoClient.getDatabase(DATABASE_NAME);
        gridFSBucket = GridFSBuckets.create(database, SIMULATION_STATE_COLLECTION);
        collection =  database.getCollection(SIMULATION_METADATA_COLLECTION);
    }

    public void saveAndUploadState(Simulation simulation, String userId) throws IOException {
        saveStateAsFile(simulation);
        uploadStateToBucket(userId);
    }

    private void saveStateAsFile(Simulation simulation) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(LOCAL_STATE_FILE_NAME);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(simulation);
        out.close();
        fileOut.close();
    }

    private String uploadStateToBucket(String userId) throws FileNotFoundException {
        InputStream streamToUploadFrom = new FileInputStream(new File(LOCAL_STATE_FILE_NAME));

        GridFSUploadOptions options = new GridFSUploadOptions()
            .chunkSizeBytes(UPLOAD_SIZE)
            .metadata(new Document("type", "presentation"));

        String snapshotFileName = userId + "_snapshot";

        ObjectId uploadedState = gridFSBucket.uploadFromStream(snapshotFileName, streamToUploadFrom, options);

        String snapshotId = uploadedState.toHexString();

        System.out.println("The id of the uploaded file is: " + snapshotId);

        uploadSnapshotToMetadata(userId, snapshotId);

        return snapshotId;
    }

    private void uploadSnapshotToMetadata(String userId, String snapshotId) {
        Document userMetaData = collection.find(eq(USER_FIELD, userId)).first();

        ArrayList<String> snapshotIdList;

        if (userMetaData == null) {
            snapshotIdList = new ArrayList<>();
            snapshotIdList.add(snapshotId);
            Document doc = new Document(USER_FIELD, userId)
                .append(SNAPSHOT_ID_FIELD, snapshotIdList)
                .append(SNAPSHOT_ID_INDEX_FIELD, 0);

            collection.insertOne(doc);

        } else {
            snapshotIdList = (ArrayList<String>) userMetaData.get(SNAPSHOT_ID_FIELD);
            snapshotIdList.add(snapshotId);
            int index = (int) userMetaData.get(SNAPSHOT_ID_INDEX_FIELD);

            updateUserDBData(userId, snapshotIdList, index + 1);
        }
    }

    public Simulation loadSimulationState(String userId, boolean loadPreviousState) throws IllegalStateException {

        ArrayList<String> snapshotIdList = getSnapshotIdList(userId, loadPreviousState);
        String snapshotId = getLastId(snapshotIdList);

        Simulation simulation = loadDBSimulationSnapshot(snapshotId);

        System.out.println("snapshot id list = " + snapshotIdList.toString());

        if (snapshotIdList.size() == 1) {
            simulation.status = START_STATUS;
        }

        return simulation;
    }

    public Simulation resetSimulationState(String userId) throws ClassNotFoundException {
        Document metaData = collection.find(eq(USER_FIELD, userId)).first();
        ArrayList<String> snapshotIdList = (ArrayList<String>) metaData.get(SNAPSHOT_ID_FIELD);
        String snapshotId = snapshotIdList.get(0);

        if (snapshotIdList.size() > 1) {
            ArrayList<String> emptiedList = new ArrayList<>();
            emptiedList.add(snapshotId);
            updateUserDBData(userId, emptiedList, 0);
        }

        Simulation simulation = loadDBSimulationSnapshot(snapshotId);
        simulation.status = START_STATUS;

        return simulation;
    }

    public void uploadNewSimulation(String userId) throws FileNotFoundException {
        String newSnapshotId = uploadStateToBucket(userId);

        System.out.println(String.format("New simulation: snapshotId = %s", newSnapshotId));

        ArrayList<String> newSnapshotIdList = new ArrayList<>();
        newSnapshotIdList.add(newSnapshotId);

        updateUserDBData(userId, newSnapshotIdList, 0);
    }

    private ArrayList<String> getSnapshotIdList(String userId, boolean loadPreviousState) throws IllegalStateException {
        Document metaData = collection.find(eq(USER_FIELD, userId)).first();

        if (metaData == null) {
            throw new IllegalStateException(String.format("User Not Found: User id = %s is not found", userId));
        }

        ArrayList<String> snapshotIdList = (ArrayList<String>) metaData.get(SNAPSHOT_ID_FIELD);
        int snapshotIdIndex = (int) metaData.get(SNAPSHOT_ID_INDEX_FIELD);

        if (loadPreviousState) {
            if (snapshotIdList.size() <= 1) {
                System.out.println("INVALID INPUT: Current state is already at the initial state.");
            } else {
                snapshotIdList.remove(snapshotIdList.size() - 1);
                updateUserDBData(userId, snapshotIdList, snapshotIdIndex - 1);
            }
        }

        return snapshotIdList;
    }

    private void updateUserDBData(String userId, ArrayList<String> snapshotIdList, int snapshotIdIndex) {
        collection.updateOne(eq(USER_FIELD, userId), new Document("$set", new Document(SNAPSHOT_ID_FIELD, snapshotIdList)));
        collection.updateOne(eq(USER_FIELD, userId), new Document("$set", new Document(SNAPSHOT_ID_INDEX_FIELD, snapshotIdIndex)));
    }

    private Simulation loadDBSimulationSnapshot(String snapshotId) throws IllegalStateException {
        try {
            ObjectId fileId = new ObjectId(snapshotId);
            FileOutputStream downloadedState = new FileOutputStream(LOCAL_STATE_FILE_NAME);
            gridFSBucket.downloadToStream(fileId, downloadedState);
            downloadedState.close();
            System.out.println(String.format("The id of the downloaded file is: %s", snapshotId));

            return loadStateFromLocalSnapshot();

        } catch (IOException ex) {
            throw new IllegalStateException(String.format("Failed to open file %s", LOCAL_STATE_FILE_NAME), ex);
        }
    }

    private Simulation loadStateFromLocalSnapshot() throws IOException {
        try {
            FileInputStream fileIn = new FileInputStream(LOCAL_STATE_FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Simulation simulation = (Simulation) in.readObject();
            in.close();
            fileIn.close();
            return simulation;
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("State stored in DB is corrupted", ex);
        }
    }

    private String getLastId(ArrayList<String> idList) {
        return idList.get(idList.size() - 1);
    }
}
