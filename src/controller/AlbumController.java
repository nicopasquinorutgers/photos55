package controller;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import model.Album;
import model.DataManager;
import model.User;

public class AlbumController {
    @FXML
    private Label userLabel;
    @FXML
    private ListView<String> albumListView;
    @FXML
    private Button addAlbumButton;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        userLabel.setText("Welcome, " + user.getUsername());
        refreshAlbumList();
    }

    private void refreshAlbumList() {
        albumListView.getItems().clear();
        for(Album album : currentUser.getAlbums()){
            albumListView.getItems().add(album.getName());
        }
    }

    @FXML
    private void handleAddAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Create a New Album");
        dialog.setContentText("Album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if(name.trim().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Album Name");
                alert.setContentText("Album name cannot be empty.");
                alert.showAndWait();
            } else {
                boolean duplicate = currentUser.getAlbums().stream()
                        .anyMatch(album -> album.getName().equalsIgnoreCase(name.trim()));
                if(duplicate) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Duplicate Album");
                    alert.setHeaderText("Album name already exists");
                    alert.setContentText("Please choose a different name.");
                    alert.showAndWait();
                } else {
                    Album newAlbum = new Album(name.trim());
                    currentUser.addAlbum(newAlbum);
                    refreshAlbumList();
                    try {
                        DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Save Error");
                        alert.setHeaderText("Failed to save user data");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });
    }
}
