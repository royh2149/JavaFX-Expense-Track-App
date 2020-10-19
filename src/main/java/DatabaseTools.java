import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Sorts;
import com.mongodb.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;




public class DatabaseTools {

    private static Consumer<Document> printCostumer = new Consumer<Document>() {
        @Override
        public void accept(Document document) {
            System.out.println(document.toJson());
        }
    };


    public static void createDB(){ // only to check connection works properly
        getDB();

        System.out.println("Connected Successfully!");
    }

    /**
     * Adds a user to the database. Registers him
     * @param username user's username
     * @param passwd user's password
     * @param balance user's balance
     * @return true if user was added successfully, false otherwise
     */
    public static boolean addDoc(String username, String passwd, float balance){ // returns true if user was added successfully
        if (usernameAlreadyExists(username)){ // make sure usernames wont collide
            System.out.println("UserName Already exits!");
            return false;
        }

        MongoDatabase database = getDB();
        MongoCollection<Document> collection = database.getCollection("users"); // get the users collection

        // create the document with the necessary information
        Document doc1 = new Document("username", username).append("passwd", passwd).append("balance", balance).
                append("actions", new ArrayList<Document>()).append("joined", LocalDateTime.now());
        collection.insertOne(doc1);

        return true;
    }

    /**
     * Authenticate the user
     * @param username - username
     * @param pass - password
     */
    public static Document Authenticate(String username, String pass){
        MongoDatabase db = getDB(); // get the database object
        MongoCollection<Document> collection = db.getCollection("users"); // use the right collection

        // search whether username already exists
        FindIterable<Document> results = collection.find(Filters.eq("username",username));
        Iterator iterator = results.iterator(); // create iterator to check if password matches

        if (!iterator.hasNext()){ // handle the case in which username doesn't exist
            System.out.println("ERROR");
            return null;
        }

        System.out.println("AUTH...");
        // the iterator holds a BSon document
        Document doc = (Document) iterator.next();
        if(doc.get("passwd").equals(pass)){ // verify the password
            return doc;
        }

        System.out.println("BAD PASSWD");
        return null; // indicate that wrong credentials were entered
    }

    public static boolean register(String user, String pass){
        try {
            // register the user
            addDoc(user, pass, 0);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void insertAction(String username, Document action){
        MongoDatabase db = getDB(); // get the database object
        MongoCollection<Document> collection = db.getCollection("users"); // use the right collection

        // search whether username already exists
        FindIterable<Document> results = collection.find(Filters.eq("username",username));
        Iterator iterator = results.iterator(); // create iterator to check if password matches

        Document doc = (Document) iterator.next(); // get the document

        ArrayList<Document> curActions = (ArrayList<Document>) doc.get("actions"); // get current actions
        curActions.add(action); // add the new action

        // calculate the new balance
        double newBalance = (Double) doc.get("balance");
        if(action.get("type").equals("income")){
            newBalance += (Double) action.get("sum");
        }else {
            newBalance -= (Double) action.get("sum");
        }

        collection.updateOne(Filters.eq("username", username), // update the appropriate user
                Updates.set("actions", curActions)); // add the new action

        collection.updateOne(Filters.eq("username", username), // update the appropriate user
                Updates.set("balance", newBalance)); // update the balance

    }

    public static void resetAccount(String username){
        MongoDatabase db = getDB(); // get the database object
        MongoCollection<Document> collection = db.getCollection("users"); // use the right collection

        collection.updateOne(Filters.eq("username", username), // update the appropriate user
                Updates.set("actions", new ArrayList<Document>())); // reset the actions

        collection.updateOne(Filters.eq("username", username), // update the appropriate user
                Updates.set("balance", 0.0)); // reset the balance
    }

    public static boolean changeUsername(String old, String newOne){
        MongoDatabase db = getDB(); // get the database object
        MongoCollection<Document> collection = db.getCollection("users"); // use the right collection

        if (usernameAlreadyExists(newOne)){
            return false;
        }else {
            collection.updateOne(Filters.eq("username", old), Updates.set("username", newOne));
            return true;
        }
    }

    public static boolean changePassword(String username, String newPass){
        MongoDatabase db = getDB(); // get the database object
        MongoCollection<Document> collection = db.getCollection("users"); // use the right collection

        try {
            collection.updateOne(Filters.eq("username", username), Updates.set("passwd", newPass));
            return true; // return true if operation succeeded
        }catch (Exception e){
            e.printStackTrace();
        }
        return false; // return false in case of error
    }

    // returns true if username received already exists in the database
    private static boolean usernameAlreadyExists(String username){
        MongoDatabase db = getDB(); // get the database object
        MongoCollection<Document> collection = db.getCollection("users"); // use the right collection

        // search whether username already exists
        FindIterable<Document> results = collection.find(Filters.eq("username",username));
        Iterator iterator = results.iterator(); // create iterator to check if empty
        return iterator.hasNext(); // if the iterator is empty (has nothing next) it means username is available
    }

    // return the Database object
    private static MongoDatabase getDB(){ // returns the database object
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"); // connect to the mongoDB server
        MongoDatabase database = mongoClient.getDatabase("project_db"); // create database if doesn't exist
        return database;
    }


}