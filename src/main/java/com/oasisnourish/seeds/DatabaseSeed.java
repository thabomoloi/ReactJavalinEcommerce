package com.oasisnourish.seeds;

/**
 * The {@code DatabaseSeed} interface defines a contract for database seeding
 * operations. Implementing classes should provide the logic for seeding the
 * database with initial data necessary for the application to function
 * properly.
 */
public interface DatabaseSeed {

    /**
     * Seeds the database with initial data.
     * 
     * This method should be implemented to contain the specific logic for inserting
     * the necessary records into the database, such as default users, roles, or
     * other entities required by the application.
     */
    void seed();
}
