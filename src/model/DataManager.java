package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class DataManager {

    public static void saveUser(User user, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(user);
        }
    }

    public static User loadUser(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fis)) {
            return (User) in.readObject();
        }
    }
}

