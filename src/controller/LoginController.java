package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.User;

/**
 * Controller for the login view.
 * 
 * <p>This controller handles user login. Depending on the username entered, it
 * either loads the admin view, stock user view, or a normal user view.</p>
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    /**
     * Handles the login action when the user presses the login button.
     *
     * @param event the ActionEvent triggered by the login button.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Username Required");
            alert.setContentText("Please enter a username.");
            alert.showAndWait();
            return;
        }
        
        // Handle admin login.
        if (username.equalsIgnoreCase("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
                Parent adminRoot = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(adminRoot));
                stage.setTitle("Admin Panel");
                return;
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Could not load admin view");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return;
            }
        }
        
        // Handle stock user login.
        String userFile = "data/" + username + ".dat";
        File file = new File(userFile);
        User user = null;
        if (username.equalsIgnoreCase("stock")) {
            if (!file.exists()) {
                user = new User("stock");
                Album stockAlbum = new Album("stock");
                stockAlbum.addPhoto(new model.Photo("data/stock/photo1.jpg"));
                stockAlbum.addPhoto(new model.Photo("data/stock/photo2.jpg"));
                stockAlbum.addPhoto(new model.Photo("data/stock/photo3.jpg"));
                stockAlbum.addPhoto(new model.Photo("data/stock/photo4.jpg"));
                stockAlbum.addPhoto(new model.Photo("data/stock/photo5.jpg"));
                user.addAlbum(stockAlbum);
                try {
                    DataManager.saveUser(user, "data/stock.dat");
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Save Error");
                    alert.setHeaderText("Failed to initialize stock user");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return;
                }
            } else {
                try {
                    user = DataManager.loadUser(userFile);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Load Error");
                    alert.setHeaderText("Failed to load stock user data");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
        } else {
            // Handle normal user login.
            if (file.exists()) {
                try {
                    user = DataManager.loadUser(userFile);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Load Error");
                    alert.setHeaderText("Failed to load user data");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return;
                }
            } else {
                user = new User(username);
            }
        }

        try {
            URL albumUrl = getClass().getResource("/view/album.fxml");
            if (albumUrl == null) {
                System.out.println("Debug: album.fxml not found in /view");
            } else {
                System.out.println("Debug: album.fxml found at: " + albumUrl.toString());
            }
            
            FXMLLoader loader = new FXMLLoader(albumUrl);
            Parent albumRoot = loader.load();
            
            controller.AlbumController albumController = loader.getController();
            albumController.setUser(user);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(albumRoot));
            stage.setTitle("Albums for " + username);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load album view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}