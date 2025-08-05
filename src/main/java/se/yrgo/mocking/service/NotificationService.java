package se.yrgo.mocking.service;

/**
 * Service for sending notifications to users.
 * 
 */
public interface NotificationService {
    
    /**
     * Send a notification to a user.
     * 
     * @param userEmail The email address of the user
     * @param subject The subject of the notification
     * @param message The message content
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendNotification(String userEmail, String subject, String message);
    
    /**
     * Send a reminder notification.
     * 
     * @param userEmail The email address of the user
     * @param itemTitle The title of the item
     * @param dueDate The due date for returning the item
     * @return true if the reminder was sent successfully, false otherwise
     */
    boolean sendReminder(String userEmail, String itemTitle);
}
