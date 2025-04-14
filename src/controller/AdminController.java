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

public class AdminController {
    @FXML
    private ListView<String> userListView;
    
    @FXML
    private void initialize() {
        loadUserList();
    }
    
    @FXML
    private void handleListUsers() {
        loadUserList();
    }
    
    private void loadUserList() {
        userListView.getItems().clear();
        File dataDir = new File("data");
        if(dataDir.exists() && dataDir.isDirectory()){
            for (File file : dataDir.listFiles()) {
                if (file.getName().endsWith(".dat") &&
                    !file.getName().equalsIgnoreCase("stock.dat")) {
                    userListView.getItems().add(file.getName().replace(".dat", ""));
                }
            }
        }
    }
    
    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText("Enter new username");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            if(username.trim().isEmpty()){
                showAlert("Error", "Username cannot be empty.");
                return;
            }
            File userFile = new File("data/" + username + ".dat");
            if(userFile.exists()){
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
    
    @FXML
    private void handleDeleteUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            showAlert("Error", "Please select a user to delete.");
            return;
        }
        if(selectedUser.equalsIgnoreCase("stock") || selectedUser.equalsIgnoreCase("admin")){
            showAlert("Error", "Cannot delete protected user.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + selectedUser + "?");
        Optional<ButtonType> confirmation = confirm.showAndWait();
        if(confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            File userFile = new File("data/" + selectedUser + ".dat");
            if(userFile.delete()){
                loadUserList();
                showAlert("Success", "User deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete user file.");
            }
        }
    }
    
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

    
    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}