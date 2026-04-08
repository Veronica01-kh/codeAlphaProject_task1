package com.mycompany.dataleak_detection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;
import org.bson.Document;

/**
 * Configuration for MongoDB Atlas Cloud Connection.
 * This class establishes the link between your Java app and the remote cluster.
 */
public class DatabaseConfig {

    /**
     * CONNECTION STRING: 
     * Uses your specific cluster URI. 
     * IMPORTANT: Ensure you replaced 'MySecurePassword123!' with your database user password.
     */
    private static final String URI = "mongodb+srv://veroelkhoury1_db_user:MySecurePassword@cluster0.ptzvgic.mongodb.net/?appName=Cluster0";

    /**
     * Method to establish and verify connection to the cloud database.
     * @return MongoClient instance if successful, null otherwise.
     */
    public static MongoClient connectDB() {
        try {
            // 1. Create the client using the URI
            MongoClient mongoClient = MongoClients.create(URI);
            
            // 2. Access a database to check if the connection is alive
            MongoDatabase database = mongoClient.getDatabase("SecureSystem");
            
            // 3. The 'ping' command is the standard way to verify a live handshake
            database.runCommand(new Document("ping", 1));
            
            System.out.println("✅ CONNECTION STATUS: Successfully connected to MongoDB Atlas Cloud Cluster.");
            return mongoClient;
            
        } catch (MongoException e) {
            System.err.println("❌ CONNECTION ERROR: Could not reach the cloud. Details: " + e.getMessage());
            return null;
        }
    }

    /**
     * Test the connection independently from the rest of the system.
     */
    public static void main(String[] args) {
        System.out.println("--- Testing Cloud Database Configuration ---");
        connectDB();
    }
}