package com.mycompany.codealpha1;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class CodeAlpha1 {

    public static void main(String[] args) {
       
String connectionString = "mongodb+srv://veroelkhoury1_db_user:Tokyo$2024@cluster0.ptzvgic.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            // Accessing the database and collection
            MongoDatabase database = mongoClient.getDatabase("recipe_db");
            MongoCollection<Document> collection = database.getCollection("recipes");

            // --- TEST DATA SECTION ---
            // Change the ingredients below to test "False Positive" classification
            Recipe1 testRecipe = new Recipe1("Pasta Carbonara", "Italian", "Tuna,Pasta, Garlic");

            System.out.println("Processing: " + testRecipe.getName() + "...");

            // 1. VALIDATION MECHANISM: Check if data with this name exists
            Document existingRecipe = collection.find(Filters.eq("name", testRecipe.getName())).first();

            if (existingRecipe != null) {
                // 2. IDENTIFY & CLASSIFY
                String existingIngredients = existingRecipe.getString("ingredients");
                
                if (existingIngredients.equalsIgnoreCase(testRecipe.getIngredients())) {
                    // CASE A: REDUNDANT DATA
                    System.out.println("-------------------------------------------------");
                    System.out.println("CLASSIFICATION: [REDUNDANT]");
                    System.out.println("Reason: Exact match found in database.");
                    System.out.println("Action: Entry BLOCKED to ensure accuracy.");
                    System.out.println("-------------------------------------------------");
                } else {
                    // CASE B: FALSE POSITIVE
                    System.out.println("-------------------------------------------------");
                    System.out.println("CLASSIFICATION: [FALSE POSITIVE]");
                    System.out.println("Reason: Same name but different ingredients detected.");
                    
                    // Action: Append as a unique verified version
                    String newVersionName = testRecipe.getName() + "_v2";
                    Document v2Doc = new Document("name", newVersionName)
                            .append("category", testRecipe.getCategory())
                            .append("ingredients", testRecipe.getIngredients())
                            .append("status", "verified_unique_version");

                    collection.insertOne(v2Doc);
                    System.out.println("Action: APPENDED as unique version: " + newVersionName);
                    System.out.println("-------------------------------------------------");
                }
            } else {
                // 3. APPEND ONLY UNIQUE DATA
                Document newDoc = new Document("name", testRecipe.getName())
                        .append("category", testRecipe.getCategory())
                        .append("ingredients", testRecipe.getIngredients())
                        .append("status", "original");

                collection.insertOne(newDoc);
                System.out.println("SUCCESS: Unique entry '" + testRecipe.getName() + "' added to database.");
            }

        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }
}