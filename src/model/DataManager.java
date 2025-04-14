package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A utility class for saving and loading User data to and from disk.
 * Uses Java Serialization (ObjectOutputStream/ObjectInputStream) for persistence.
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class DataManager {

    /**
     * Saves the specified User object to the given file path.
     * 
     * @param user     the User object to save
     * @param filePath the file path where the user data should be stored
     * @throws IOException if an I/O error occurs during saving
     */
    public static void saveUser(User user, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(user);
        }
    }

    /**
     * Loads and returns a User object from the specified file path.
     * 
     * @param filePath the file path from which to load the user data
     * @return the User object loaded from disk
     * @throws IOException if an I/O error occurs during loading
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public static User loadUser(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fis)) {
            return (User) in.readObject();
        }
    }
}