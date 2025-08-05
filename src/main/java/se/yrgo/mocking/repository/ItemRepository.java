package se.yrgo.mocking.repository;

import java.util.*;

import se.yrgo.mocking.model.*;

/**
 * Repository interface for managing items.
 *
 */
public interface ItemRepository {
    
    /**
     * Save an item to the repository.
     * 
     * @param item The item to save
     * @return The saved item
     */
    Item save(Item item);
    
    /**
     * Find an item by its ID.
     * 
     * @param id The ID to search for
     * @return Optional containing the item if found, empty otherwise
     */
    Optional<Item> findById(int id);
    
    /**
     * Find all items in the repository.
     * 
     * @return List of all items
     */
    List<Item> findAll();
    
    /**
     * Delete a item by its ID.
     * 
     * @param id The ID of the item to delete
     * @return true if the item was deleted, false if not found
     */
    boolean deleteById(int id);
    
    /**
     * Find items by their name.
     * 
     * @param name The name to search for
     * @return List of items by the model
     */
    List<Item> findByName(String name);
}
