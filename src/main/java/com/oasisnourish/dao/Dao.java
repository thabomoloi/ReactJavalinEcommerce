package com.oasisnourish.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object (DAO) interface for performing CRUD operations.
 * This interface defines common methods for interacting with database entities.
 *
 * @param <T> the type of the entity that this DAO will manage.
 */
public interface Dao<T> {

    /**
     * Finds an entity by its ID.
     *
     * @param id the ID of the entity to find.
     * @return an {@link Optional} containing the entity if found, or an empty
     *         {@link Optional} if not found.
     */
    Optional<T> find(int id);

    /**
     * Retrieves all entities of type T from the database.
     *
     * @return a List of all entities.
     */
    List<T> findAll();

    /**
     * Saves a new entity of type T to the database.
     *
     * @param t the entity to save.
     */
    void save(T t);

    /**
     * Updates an existing entity of type T in the database.
     *
     * @param t the entity to update, which must already exist in the database.
     */
    void update(T t);

    /**
     * Deletes an entity from the database by its ID.
     *
     * @param id the ID of the entity to delete.
     */
    void delete(int id);
}