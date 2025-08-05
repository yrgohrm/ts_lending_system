package se.yrgo.mocking.repository;

import java.util.*;

import se.yrgo.mocking.model.*;

/**
 * Repository interface for managing users.
 * 
 */
public interface UserRepository {
    
    /**
     * Save a user to the repository.
     * 
     * @param user The user to save
     * @return The saved user
     */
    User save(User user);
    
    /**
     * Find a user by their ID.
     * 
     * @param userId The user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(String userId);
    
    /**
     * Find all users in the repository.
     * 
     * @return List of all users
     */
    List<User> findAll();
    
    /**
     * Find a user by their email.
     * 
     * @param email The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
}
