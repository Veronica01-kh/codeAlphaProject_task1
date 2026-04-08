package com.mycompany.dataleak_detection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.io.*;
import java.net.*;

/**
 * Project: Data Leak Detection System
 * Purpose: Secure cloud data management with AES-256 and Injection Detection.
 */
public class DataLeak_Detection {

    public static void main(String[] args) {
        System.out.println("--- 🛡️ SECURE DATA LEAK DETECTION SYSTEM ---");
        
        // Step 1: Initialize a demo user in the cloud
        // This ensures there is data to find when we test the server
        insertSecureUser("Vero_Admin_Secure", "MyInternshipPassword2026", "CAP_LEVEL_5");

        // Step 2: Start the Internet-Accessible Interface
        // This opens port 8080 to listen for remote security requests
        startCloudInterface(8080);
    }

    /**
     * LAYER 1: Malicious Pattern Detection (Data Leak Prevention)
     * Requirement: Provide a security protocol to prevent data leaks.
     * This scans input BEFORE it touches the database.
     */
    public static boolean isInputSafe(String input) {
        if (input == null) return false;
        
        // Patterns commonly used in NoSQL/SQL injection attacks
        String[] forbiddenPatterns = {"$where", "$gt", "{", "}", "||", "==", "script", "select", "drop"};
        
        for (String pattern : forbiddenPatterns) {
            if (input.toLowerCase().contains(pattern)) {
                return false; 
            }
        }
        return true; 
    }

    /**
     * Requirement: Use AES-256 encryption to securely store credentials.
     * Encrypts the sensitive data before sending it to MongoDB Atlas.
     */
    public static void insertSecureUser(String user, String pass, String capCode) {
        try (MongoClient mongoClient = DatabaseConfig.connectDB()) {
            if (mongoClient != null) {
                MongoDatabase database = mongoClient.getDatabase("SecureSystem");
                MongoCollection<Document> collection = database.getCollection("Users");

                try {
                    // 1. Encrypt password using AES-256 from EncryptionUtils
                    String encryptedPassword = EncryptionUtils.encrypt(pass);

                    // 2. Prepare the secure document
                    Document newUser = new Document("username", user)
                            .append("password", encryptedPassword) 
                            .append("capability_code", capCode)
                            .append("status", "PROTECTED");

                    // 3. Insert into the cloud
                    collection.insertOne(newUser);
                    System.out.println("✅ Cloud Sync: User " + user + " encrypted and stored successfully.");

                } catch (Exception e) {
                    System.err.println("❌ Encryption Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Database Connection Failed. Ensure your Cluster is Resumed.");
        }
    }

    /**
     * THE GATEKEEPER: Secure Search & Decryption
     * Requirement: Capability code mechanism to control server access.
     * Requires the exact username AND the secret capability code to unlock data.
     */
    public static String performSecureSearch(String user, String providedCapCode) {
        try (MongoClient mongoClient = DatabaseConfig.connectDB()) {
            if (mongoClient != null) {
                MongoDatabase database = mongoClient.getDatabase("SecureSystem");
                MongoCollection<Document> collection = database.getCollection("Users");

                // Security check logic
                Document query = new Document("username", user)
                                .append("capability_code", providedCapCode);

                Document result = collection.find(query).first();

                if (result != null) {
                    // DECRYPTION LAYER
                    String encryptedData = result.getString("password");
                    String decryptedData = EncryptionUtils.decrypt(encryptedData);
                    
                    return "🔓 ACCESS GRANTED.\n" +
                           "   User Verified: " + result.getString("username") + "\n" +
                           "   Decrypted Info: " + decryptedData;
                }
            }
        } catch (Exception e) {
            return "❌ SYSTEM ERROR during decryption: " + e.getMessage();
        }
        return "🚫 ACCESS DENIED: Invalid Credentials or Capability Code.";
    }

    /**
     * INTERNET ACCESSIBILITY: Lightweight Socket Server
     * Requirement: Accessible over the internet without heavy requirements.
     * Listen for connections via network sockets.
     */
    public static void startCloudInterface(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🌐 Server running on Port: " + port);
            System.out.println("Waiting for remote security requests...");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println("--- Welcome to the Secure Cloud Portal ---");
                    out.println("Enter Username:");
                    String user = in.readLine();

                    out.println("Enter Capability Code:");
                    String code = in.readLine();

                    // --- DOUBLE-LAYER SECURITY CHECK ---
                    // Check Layer 1: Input Pattern Validation
                    if (!isInputSafe(user) || !isInputSafe(code)) {
                        out.println("⚠️ ALERT: Malicious activity detected. Connection Terminated.");
                        System.err.println("⚠️ BLOCKED ATTACK from IP: " + socket.getInetAddress());
                    } else {
                        // Check Layer 2: Capability Code Database Match
                        String response = performSecureSearch(user, code);
                        out.println(response);
                    }
                    out.println("-------------------------------------------");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Server Failed: " + e.getMessage());
        }
    }
}