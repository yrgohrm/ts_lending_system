package se.yrgo.mocking.service;

import java.util.*;

import se.yrgo.mocking.model.*;
import se.yrgo.mocking.repository.*;

/**
 * Service class for managing lending operations.
 * 
 */
public class LendingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Create a new LendingService given a repository for items,
     * a repository for users and a service for notifiactions.
     * 
     * @param itemRepository the item repository to use
     * @param userRepository the user repository to use
     * @param notificationService the notification service
     * 
     * @throws NullPointerException if any of the parameters are null
     */
    public LendingService(ItemRepository itemRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        
        Objects.requireNonNull(itemRepository);
        Objects.requireNonNull(userRepository);
        Objects.requireNonNull(notificationService);

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Add a new item to the lending.
     * 
     * @param item The item to add
     * @return The added item
     * @throws IllegalArgumentException if item with same ID already exists
     */
    public Item addItem(Item item) {
        Objects.requireNonNull(item);

        if (itemRepository.findById(item.getId()).isPresent()) {
            throw new IllegalArgumentException("Item with ID " + item.getId() + " already exists");
        }

        return itemRepository.save(item);
    }

    /**
     * Register a new user.
     * 
     * @param user The user to register
     * @return The registered user
     * @throws IllegalArgumentException if user with same ID or email already exists
     */
    public User registerUser(User user) {
        Objects.requireNonNull(user);

        if (userRepository.findById(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("User with ID " + user.getUserId() + " already exists");
        }
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        return userRepository.save(user);
    }

    /**
     * Borrow an item for a user.
     * 
     * @param userId The ID of the user borrowing the item
     * @param itemId The ID of the item to borrow
     * @return true if the item was successfully borrowed, false otherwise
     */
    public boolean borrowItem(String userId, int itemId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Item> itemOpt = itemRepository.findById(itemId);

        if (userOpt.isEmpty() || itemOpt.isEmpty()) {
            return false;
        }

        Item item = itemOpt.get();
        User user = userOpt.get();

        if (!item.isAvailable()) {
            return false;
        }

        item.setBorrower(user);
        itemRepository.save(item);

        // Send notification to the borrower
        notificationService.sendNotification(
                user.getEmail(),
                "Item Borrowed",
                "You have successfully borrowed: " + item.getName());

        return true;
    }

    /**
     * Return an item.
     * 
     * @param isbn The ID of the item to return
     * @return true if the item was successfully returned, false otherwise
     */
    public boolean returnItem(int id) {
        Optional<Item> itemOpt = itemRepository.findById(id);

        if (itemOpt.isEmpty()) {
            return false;
        }

        Item item = itemOpt.get();
        if (item.isAvailable()) {
            return false;
        }

        User borrower = item.getBorrower();

        item.setBorrower(null);
        itemRepository.save(item);

        // Send notification to the borrower
        notificationService.sendNotification(
                borrower.getEmail(),
                "Item Returned",
                "You have successfully returned: " + item.getName());

        return true;
    }

    /**
     * Search for items by name.
     * 
     * @param name The name to search for
     * @return List of items with the given name
     */
    public List<Item> searchItemsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        return itemRepository.findByName(name);
    }

    /**
     * Get all available items.
     * 
     * @return List of available items
     */
    public List<Item> getAvailableItems() {
        return itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .toList();
    }

    /**
     * Send reminders for items.
     * 
     * @param items List of items to get reminded of
     */
    public void sendReminders(List<Integer> items) {
        for (int itemId : items) {
            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isPresent() && !itemOpt.get().isAvailable()) {
                Item item = itemOpt.get();
                User user = item.getBorrower();

                notificationService.sendReminder(
                        user.getEmail(),
                        item.getName());
            }
        }
    }
}
