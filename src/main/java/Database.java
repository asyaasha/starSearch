import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.*;

import javax.xml.crypto.Data;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Database {
    private MongoDatabase database;
    private GridFSBucket gridFSBucket;
    //FOR TESTING
    String USER_ID = "008";

    public Database() {
        MongoClient mongoClient = MongoClients.create(
                "mongodb+srv://admin_one:gdPXGf5dCVXR3zB@master-hhsfd.mongodb.net/test?retryWrites=true&w=majority");
        database = mongoClient.getDatabase("star_search");
        gridFSBucket = GridFSBuckets.create(database, "files");
    }


    public void serializeAndExport(Simulation simulation) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("simulation.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(simulation);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }

        ObjectId fileId = null;
        try {
            InputStream streamToUploadFrom = new FileInputStream(new File("simulation.ser"));
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(1024)
                    .metadata(new Document("type", "presentation"));

            fileId = gridFSBucket.uploadFromStream("serial_" + USER_ID, streamToUploadFrom, options);
            System.out.println("The fileId of the uploaded file is: " + fileId.toHexString());
        } catch (Exception e) {

        }

        MongoCollection<Document> collection = database.getCollection("star_file");
        //Check if USER_ID exists already

        Document myResult = collection.find(eq("user", USER_ID)).first();
        if (myResult == null) {
            //Create new doc
            Document doc = new Document("user", USER_ID)
                    .append("current_serialization", fileId.toHexString())
                    .append("previous_serialization", "N/A");
            collection.insertOne(doc);

        } else {
            //Update doc with curr and prev
            String prev_id = (String) myResult.get("current_serialization");
            collection.updateOne(eq("user", USER_ID), new Document("$set", new Document("current_serialization", fileId.toHexString())));
            collection.updateOne(eq("user", USER_ID), new Document("$set", new Document("previous_serialization", prev_id)));
        }
    }

    public ObjectId getCurrentDoc(String user) {
        MongoCollection<Document> collection = database.getCollection("star_file");
        Document myResult = collection.find(eq("user", USER_ID)).first();
        ObjectId targetFile = new ObjectId((String) myResult.get("current_serialization"));
        return targetFile;
    }

    public ObjectId getPrevDoc(String user) {
        MongoCollection<Document> collection = database.getCollection("star_file");
        Document myResult = collection.find(eq("user", USER_ID)).first();
        ObjectId targetFile = new ObjectId((String) myResult.get("previous_serialization"));
        return targetFile;
    }

    public Simulation loadSerialization(ObjectId fileId) {

        try {
            FileOutputStream streamToDownloadTo = new FileOutputStream("simulation.ser");
            gridFSBucket.downloadToStream(fileId, streamToDownloadTo);
            streamToDownloadTo.close();
        } catch (Exception e) {

        }

        Simulation simulation;
        try {
            FileInputStream fileIn = new FileInputStream("simulation.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            simulation = (Simulation) in.readObject();
            in.close();
            fileIn.close();
            return simulation;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Serialized information not located");
            c.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
//        Database db = new Database();
//        db.serializeAndExport();

    }
}
