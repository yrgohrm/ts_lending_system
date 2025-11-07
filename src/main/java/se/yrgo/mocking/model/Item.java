package se.yrgo.mocking.model;

import java.util.*;

/**
 * Represents an item in the lending system.
 * 
 */
public class Item {
    private final int id;
    private final String name;
    private final String modelName;
    private User borrower;

    public Item(int id, String name, String modelName) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(modelName);

        this.id = id;
        this.name = name;
        this.modelName = modelName;
        this.borrower = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isAvailable() {
        return borrower == null;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        if (borrower != null && this.borrower != null && !this.borrower.getUserId().equals(borrower.getUserId())) {
            throw new IllegalStateException("Already borrowed by someone else!");
        }

        this.borrower = borrower;
    }

    @Override
    public String toString() {
        return String.format("%s - %s, %s [%s]", 
            id, name, modelName, borrower == null ? "Available" : "Borrowed");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
