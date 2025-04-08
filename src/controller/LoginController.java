package controller;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DataManager;
import model.User;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        if(username.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Username Required");
            alert.setContentText("Please enter a username.");
            alert.showAndWait();
            return;
        }
        
        String userFile = "data/" + username + ".dat";
        File file = new File(userFile);
        User user = null;
        if(file.exists()){
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

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album.fxml"));
            Parent albumRoot = loader.load();
            
            AlbumController albumController = loader.getController();
            albumController.setUser(user);
            
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(albumRoot));
            stage.setTitle("Albums for " + username);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load album view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}

