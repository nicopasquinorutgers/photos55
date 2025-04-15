package controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.DataManager;
import model.User;

/**
 * Controller for the admin view.
 * 
 * <p>This controller is responsible for managing user accounts by displaying a list of users,
 * creating new users, and deleting selected users from the data directory. It also facilitates
 * logging out and returning to the login screen.</p>
 * 
 * @author Sara Annamraju
 * @version 1.0
 */
public class AdminController {
    @FXML
    private ListView<String> userListView;
    
    /**
     * Initializes the admin view by loading the user list.
     */
    @FXML
    private void initialize() {
        loadUserList();
    }
    
    /**
     * Refreshes and displays the list of users.
     */
    @FXML
    private void handleListUsers() {
        loadUserList();
    }
    
    /**
     * Loads the user list by scanning the "data" directory for user data files.
     * Only files with the ".dat" extension are added, excluding protected files like "stock.dat".
     */
    private void loadUserList() {
        userListView.getItems().clear();
        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            for (File file : dataDir.listFiles()) {
                if (file.getName().endsWith(".dat") &&
                    !file.getName().equalsIgnoreCase("stock.dat")) {
                    userListView.getItems().add(file.getName().replace(".dat", ""));
                }
            }
        }
    }
    
    /**
     * Handles the creation of a new user.
     * Prompts the admin for a username using a text input dialog, validates the input,
     * and saves the new user if the username is non-empty and not already in use.
     */
    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText("Enter new username");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            if (username.trim().isEmpty()) {
                showAlert("Error", "Username cannot be empty.");
                return;
            }
            File userFile = new File("data/" + username + ".dat");
            if (userFile.exists()) {
                showAlert("Error", "User already exists.");
                return;
            }
            User newUser = new User(username);
            try {
                DataManager.saveUser(newUser, "data/" + username + ".dat");
                loadUserList();
                showAlert("Success", "User created successfully.");
            } catch (IOException e) {
                showAlert("Error", "Failed to create user: " + e.getMessage());
            }
        });
    }
    
    /**
     * Handles the deletion of a selected user.
     * Checks that a user is selected and is not a protected user (e.g., "stock" or "admin"),
     * then requests confirmation before deleting the corresponding user file.
     */
    @FXML
    private void handleDeleteUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete.");
            return;
        }
        if (selectedUser.equalsIgnoreCase("stock") || selectedUser.equalsIgnoreCase("admin")) {
            showAlert("Error", "Cannot delete protected user.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + selectedUser + "?");
        Optional<ButtonType> confirmation = confirm.showAndWait();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            File userFile = new File("data/" + selectedUser + ".dat");
            if (userFile.delete()) {
                loadUserList();
                showAlert("Success", "User deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete user file.");
            }
        }
    }
    
    /**
     * Handles logging out by loading the login screen.
     * If the login screen fails to load, an error alert is shown.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) userListView.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Photo App Login");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Logout Error");
            alert.setHeaderText("Failed to load login screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Displays an informational alert dialog with a specified title and message.
     *
     * @param title   the title of the alert dialog.
     * @param message the message content of the alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}