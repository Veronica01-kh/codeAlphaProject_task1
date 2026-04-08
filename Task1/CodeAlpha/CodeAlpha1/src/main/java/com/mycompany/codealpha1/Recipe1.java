/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.codealpha1;



public class Recipe1 {
    private String name;
    private String category;
    private String ingredients;

    // Constructor
    public Recipe1(String name, String category, String ingredients) {
        this.name = name;
        this.category = category;
        this.ingredients = ingredients;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getIngredients() { return ingredients; }

    /**
     * This method creates a unique identifier to check for redundancy.
     * If two recipes have the same name (ignoring case), we consider them redundant.
     */
    public String getRedundancyKey() {
        return name.toLowerCase().trim();
    }
}