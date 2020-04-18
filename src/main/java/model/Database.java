package model;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;

public class Database {
    private final String DATABASE_NAME = "star_search";
    private final String SIMULATION_METADATA_COLLECTION = "simulation_meta_data";
    private final String SIMULATION_STATE_COLLECTION = "simulation_state";
    private final String USER_FIELD = "user";
    private final String SNAPSHOT_ID_FIELD = "snapshot_ids";
    private final String SNAPSHOT_ID_INDEX_FIELD = "snapshot_id_index";

    private final String LOCAL_STATE_FILE_NAME = "simulation.ser";
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

    private void uploadStateToBucket(String userId) {
        try {
            InputStream streamToUploadFrom = new FileInputStream(new File(LOCAL_STATE_FILE_NAME));

            GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(UPLOAD_SIZE)
                .metadata(new Document("type", "presentation"));

            String snapshotFileName = userId + "_snapshot";

            ObjectId uploadedState = gridFSBucket.uploadFromStream(snapshotFileName, streamToUploadFrom, options);

            String snapshotId = uploadedState.toHexString();

            System.out.println("The id of the uploaded file is: " + snapshotId);

            uploadSnapshotToMetadata(userId, snapshotId);

        } catch (Exception ignored) {}
    }

    private void uploadSnapshotToMetadata(String userId, String snapshotId) {
        Document userMetaData = collection.find(eq(USER_FIELD, userId)).first();

        ArrayList<String> snapshotIdList;

        //userId does not exist
        if (userMetaData == null) {
            snapshotIdList = new ArrayList<>();
            snapshotIdList.add(snapshotId);
            //Create new doc
            Document doc = new Document(USER_FIELD, userId)
                .append(SNAPSHOT_ID_FIELD, snapshotIdList)
                .append(SNAPSHOT_ID_INDEX_FIELD, 0);
            collection.insertOne(doc);
        } else {
            //Update doc with curr and prev
            snapshotIdList = (ArrayList<String>) userMetaData.get(SNAPSHOT_ID_FIELD);
            snapshotIdList.add(snapshotId);
            int newIndex = (int) userMetaData.get(SNAPSHOT_ID_INDEX_FIELD);
            newIndex += 1;

            collection.updateOne(eq(USER_FIELD, userId), new Document("$set", new Document(SNAPSHOT_ID_FIELD, snapshotIdList)));
            collection.updateOne(eq(USER_FIELD, userId), new Document("$set", new Document(SNAPSHOT_ID_INDEX_FIELD, newIndex)));
        }
    }

    public Simulation loadSimulationState(String userId, boolean loadPreviousState) throws IOException {

        ObjectId fileId = getStateObjectId(userId, loadPreviousState);

        FileOutputStream streamToDownloadTo = new FileOutputStream(LOCAL_STATE_FILE_NAME);
        gridFSBucket.downloadToStream(fileId, streamToDownloadTo);
        streamToDownloadTo.close();

        Simulation simulation;
        try {
            FileInputStream fileIn = new FileInputStream(LOCAL_STATE_FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            simulation = (Simulation) in.readObject();
            in.close();
            fileIn.close();
            return simulation;
        } catch (ClassNotFoundException c) {
            System.out.println("Serialized information not located");
            c.printStackTrace();
            return null;
        }
    }

    private ObjectId getStateObjectId(String userId, boolean loadPreviousState) throws IllegalArgumentException {
        // ADD NULL CASE HANDLING
        Document metaData = collection.find(eq("user", userId)).first();

        ArrayList<String> snapshotIdList;
        try {
            snapshotIdList = (ArrayList<String>) metaData.get(SNAPSHOT_ID_FIELD);
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("User Not Found: User id = %s is not found", userId));
        }

        int snapshotIdIndex = (int) metaData.get(SNAPSHOT_ID_INDEX_FIELD);

        if (loadPreviousState) {
            if (snapshotIdList.size() <= 1 || snapshotIdIndex == 0) {
                System.out.println("INVALID INPUT: Current state is already at the initial state.");
            } else {
                snapshotIdList.remove(snapshotIdList.size() - 1);
                snapshotIdIndex -= 1;
                collection.updateOne(eq(USER_FIELD, userId), new Document("$set", new Document(SNAPSHOT_ID_FIELD, snapshotIdList)));
                collection.updateOne(eq(USER_FIELD, userId), new Document("$set", new Document(SNAPSHOT_ID_INDEX_FIELD, snapshotIdIndex)));
            }
        }

        return new ObjectId(snapshotIdList.get(snapshotIdIndex));
    }
}
