package se.yrgo.mocking.service.impl;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.*;
import java.util.*;

import se.yrgo.mocking.service.*;

/**
 * Notification using the web service https://yrgo-web-services.netlify.app/tsnotification
 * to send email notifications (it does actually not send e-mails for real).
 * 
 */
public class NotificationServiceImpl implements NotificationService {
    private static final URI API_ENDPOINT = URI.create("https://yrgo-web-services.netlify.app/tsnotification");

    private HttpClient client;

    public NotificationServiceImpl(HttpClient client) {
        Objects.requireNonNull(client);

        this.client = client;
    }

    @Override
    public boolean sendNotification(String userEmail, String subject, String message) {
        return send("notification", userEmail, subject, message);
    }

    @Override
    public boolean sendReminder(String userEmail, String subject, String message) {
        return send("reminder", userEmail, subject, message);
    }

    private boolean send(String kind, String userEmail, String subject, String message) {
        if (userEmail == null || !userEmail.contains("@")) {
            throw new IllegalArgumentException("userEmail must be a valid e-mail address");
        }

        Objects.requireNonNull(kind);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(message);

        // Types for the web service post data:
        // type NotificationKind = "notification" | "reminder";
        // type NotificationData = { kind: NotificationKind, recipient: string; subject: string; message: string };

        String requestTemplate = """
                { "kind": "%s", "recipient": "%s", "subject": "%s", "message": "%s" }
                """;

        String requestData = String.format(requestTemplate, kind, userEmail, subject, message);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                                        .uri(API_ENDPOINT)
                                        .POST(BodyPublishers.ofString(requestData))
                                        .build();
    
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("ok");
        }
        catch (IOException ex) {
            return false;
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
