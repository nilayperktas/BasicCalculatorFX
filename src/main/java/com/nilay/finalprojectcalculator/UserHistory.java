package com.nilay.finalprojectcalculator;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage user-specific calculation history
 */
public class UserHistory {
    private static final String HISTORY_FILE = "user_history.dat";
    private static Map<String, String> userHistories = new HashMap<>();

    /**
     * Load all user histories from file
     */
    public static void loadAllHistories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            userHistories = (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            // If file doesn't exist or there's an error, start with empty map
            userHistories = new HashMap<>();
        }
    }

    public static void saveAllHistories() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(userHistories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHistoryForUser(String username) {
        return userHistories.getOrDefault(username, "");
    }

    public static void updateHistoryForUser(String username, String history) {
        userHistories.put(username, history);
        saveAllHistories();
    }
}