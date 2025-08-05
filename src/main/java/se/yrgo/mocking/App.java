package se.yrgo.mocking;

import java.util.*;

import se.yrgo.mocking.model.*;
import se.yrgo.mocking.repository.*;
import se.yrgo.mocking.service.*;

/**
 * Simple console-based Lending Management System.
 * 
 */
public class App {
    
    private final LendingService lendingService;
    private final Scanner scanner;
    
    public App(LendingService lendingService) {
        this.lendingService = lendingService;
        System.out.println(System.in.getClass().getCanonicalName());
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        // In a real application, these would be injected or configured
        ItemRepository itemRepo = new InMemoryItemRepository();
        UserRepository userRepo = new InMemoryUserRepository();
        NotificationService notificationService = new ConsoleNotificationService();
        
        LendingService service = new LendingService(itemRepo, userRepo, notificationService);
        App app = new App(service);
        
        app.run();
    }
    
    public void run() {
        System.out.println("=== Welcome to the Lending Management System ===");
        
        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1" -> addItem();
                    case "2" -> registerUser();
                    case "3" -> borrowItem();
                    case "4" -> returnItem();
                    case "5" -> searchItems();
                    case "6" -> listAvailableItems();
                    case "0" -> {
                        System.out.println("Thank you for using the Library Management System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            System.out.println(); // Add spacing
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add Item");
        System.out.println("2. Register User");
        System.out.println("3. Borrow Item");
        System.out.println("4. Return Item");
        System.out.println("5. Search Items by Name");
        System.out.println("6. List Available Items");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void addItem() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter model: ");
        String model = scanner.nextLine().trim();
        
        Item item = new Item(id, name, model);
        Item added = lendingService.addItem(item);
        System.out.println("Item added successfully: " + added);
    }
    
    private void registerUser() {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        
        User user = new User(userId, name, email);
        User registered = lendingService.registerUser(user);
        System.out.println("User registered successfully: " + registered);
    }
    
    private void borrowItem() {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine().trim();

        System.out.print("Enter item ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        boolean success = lendingService.borrowItem(userId, id);
        if (success) {
            System.out.println("Item borrowed successfully!");
        } else {
            System.out.println("Failed to borrow item. Check if user exists, item exists, and item is available.");
        }
    }
    
    private void returnItem() {
        System.out.print("Enter item ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        boolean success = lendingService.returnItem(id);
        if (success) {
            System.out.println("Item returned successfully!");
        } else {
            System.out.println("Failed to return item. Check if item exists and was actually borrowed.");
        }
    }
    
    private void searchItems() {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine().trim();
        
        List<Item> items = lendingService.searchItemsByName(name);
        if (items.isEmpty()) {
            System.out.println("No items found by name: " + name);
        } else {
            System.out.println("Items (" + name + "):");
            items.forEach(System.out::println);
        }
    }
    
    private void listAvailableItems() {
        List<Item> items = lendingService.getAvailableItems();
        if (items.isEmpty()) {
            System.out.println("No items are currently available.");
        } else {
            System.out.println("Available items:");
            items.forEach(System.out::println);
        }
    }
    
    // Simple in-memory implementations for demonstration
    private static class InMemoryItemRepository implements ItemRepository {
        private final Map<Integer, Item> items = new HashMap<>();
        
        @Override
        public Item save(Item item) {
            items.put(item.getId(), item);
            return item;
        }
        
        @Override
        public Optional<Item> findById(int id) {
            return Optional.ofNullable(items.get(id));
        }
        
        @Override
        public List<Item> findAll() {
            return new ArrayList<>(items.values());
        }
        
        @Override
        public boolean deleteById(int id) {
            return items.remove(id) != null;
        }
        
        @Override
        public List<Item> findByName(String name) {
            return items.values().stream()
                    .filter(item -> item.getModelName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }
    }
    
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();
        
        @Override
        public User save(User user) {
            users.put(user.getUserId(), user);
            return user;
        }
        
        @Override
        public Optional<User> findById(String userId) {
            return Optional.ofNullable(users.get(userId));
        }
        
        @Override
        public List<User> findAll() {
            return new ArrayList<>(users.values());
        }
        
        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();
        }
    }
    
    private static class ConsoleNotificationService implements NotificationService {
        @Override
        public boolean sendNotification(String userEmail, String subject, String message) {
            System.out.println("[NOTIFICATION] To: " + userEmail);
            System.out.println("[NOTIFICATION] Subject: " + subject);
            System.out.println("[NOTIFICATION] Message: " + message);
            return true;
        }
        
        @Override
        public boolean sendReminder(String userEmail, String itemName) {
            System.out.println("[REMINDER] To: " + userEmail);
            System.out.println("[REMINDER] Item: " + itemName + " is due.");
            return true;
        }
    }
}
