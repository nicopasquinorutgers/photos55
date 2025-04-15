package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;

/**
 * Controller for the album view.
 * 
 * <p>This controller is responsible for album management functions:
 * displaying the list of albums (including their photo counts and date ranges),
 * creating, deleting, and renaming albums, and managing photos within albums
 * (adding, removing, moving, and copying photos). It also supports a scalability
 * test by populating a test album with duplicate photos.</p>
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class AlbumController {

    @FXML
    private javafx.scene.control.Label userLabel;
    @FXML
    private ListView<String> albumListView;
    @FXML
    private ListView<Photo> photoListView;

    private User currentUser;
    private Album selectedAlbum;

    /**
     * Initializes the album view by setting up event handlers for album and photo selection.
     */
    @FXML
    private void initialize() {
        albumListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && currentUser != null) {
                String selectedAlbumItem = albumListView.getSelectionModel().getSelectedItem();
                if (selectedAlbumItem != null) {
                    String selectedAlbumName = selectedAlbumItem.split(" \\(")[0];
                    selectedAlbum = currentUser.getAlbums().stream()
                            .filter(album -> album.getName().equals(selectedAlbumName))
                            .findFirst().orElse(null);
                    if (selectedAlbum != null) {
                        loadPhotosForAlbum(selectedAlbum);
                    }
                }
            }
        });

        photoListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handlePhotoClick();
            }
        });
    }

    /**
     * Sets the current user and refreshes the album list.
     * 
     * @param user the logged-in user.
     */
    public void setUser(User user) {
        this.currentUser = user;
        userLabel.setText("Welcome, " + user.getUsername());
        refreshAlbumList();
    }

    /**
     * Refreshes the album list with album names, photo counts, and date ranges.
     */
    private void refreshAlbumList() {
        albumListView.getItems().clear();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Album album : currentUser.getAlbums()) {
            int count = album.getPhotos().size();
            String dateRange = "";
            if (count > 0) {
                LocalDateTime minDate = album.getPhotos().stream()
                        .map(Photo::getDateTaken)
                        .min(LocalDateTime::compareTo)
                        .orElse(null);
                LocalDateTime maxDate = album.getPhotos().stream()
                        .map(Photo::getDateTaken)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
                if (minDate != null && maxDate != null) {
                    dateRange = dtf.format(minDate) + " to " + dtf.format(maxDate);
                }
            }
            String details = album.getName() + " (" + count + " photos" + (dateRange.isEmpty() ? "" : ", " + dateRange) + ")";
            albumListView.getItems().add(details);
        }
    }

    /**
     * Handles creating a new album.
     */
    @FXML
    private void handleAddAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Create a New Album");
        dialog.setContentText("Album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Album Name");
                alert.setContentText("Album name cannot be empty.");
                alert.showAndWait();
            } else {
                boolean duplicate = currentUser.getAlbums().stream()
                        .anyMatch(album -> album.getName().equalsIgnoreCase(name.trim()));
                if (duplicate) {
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

    /**
     * Handles deleting the selected album.
     */
    @FXML
    private void handleDeleteAlbum() {
        String selectedAlbumItem = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbumItem == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an album to delete.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete album: " + selectedAlbumItem + "?");
        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            String albumName = selectedAlbumItem.split(" \\(")[0];
            Album albumToDelete = currentUser.getAlbums().stream()
                    .filter(album -> album.getName().equals(albumName))
                    .findFirst().orElse(null);
            if (albumToDelete != null) {
                currentUser.removeAlbum(albumToDelete);
                refreshAlbumList();
                try {
                    DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Failed to save user data after deletion.").showAndWait();
                }
            }
        }
    }

    /**
     * Handles renaming the selected album.
     */
    @FXML
    private void handleRenameAlbum() {
        String selectedAlbumItem = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbumItem == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an album to rename.").showAndWait();
            return;
        }
        String currentName = selectedAlbumItem.split(" \\(")[0];
        TextInputDialog dialog = new TextInputDialog(currentName);
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Enter new album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (newName.trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Album name cannot be empty.").showAndWait();
                return;
            }
            boolean duplicate = currentUser.getAlbums().stream()
                    .anyMatch(album -> album.getName().equalsIgnoreCase(newName.trim()));
            if (duplicate) {
                new Alert(Alert.AlertType.ERROR, "Album name already exists.").showAndWait();
                return;
            }
            Album albumToRename = currentUser.getAlbums().stream()
                    .filter(album -> album.getName().equals(currentName))
                    .findFirst().orElse(null);
            if (albumToRename != null) {
                albumToRename.setName(newName.trim());
                refreshAlbumList();
                try {
                    DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Failed to save user data after renaming.").showAndWait();
                }
            }
        });
    }

    /**
     * Handles adding a photo to the selected album.
     */
    @FXML
    private void handleAddPhoto() {
        if (selectedAlbum == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Album Selected");
            alert.setHeaderText("Please select an album first.");
            alert.setContentText("Double-click an album from the list to view its photos, then add a photo.");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) albumListView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Photo newPhoto = new Photo(file.getAbsolutePath());
            selectedAlbum.addPhoto(newPhoto);
            loadPhotosForAlbum(selectedAlbum);
            refreshAlbumList();
            try {
                DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Error");
                alert.setHeaderText("Could not save photo");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    /**
     * Helper method to populate the currently selected album with duplicate copies 
     * of a sample photo for scalability testing.
     * 
     * @param count the number of copies to add.
     */
    private void populateTestAlbum(int count) {
        if (selectedAlbum == null) {
            if (!currentUser.getAlbums().isEmpty()) {
                selectedAlbum = currentUser.getAlbums().get(0);
            } else {
                selectedAlbum = new Album("Test Album");
                currentUser.addAlbum(selectedAlbum);
                refreshAlbumList();
            }
        }
        String samplePath = "data/test/sample.jpg"; // Ensure this file exists.
        File sampleFile = new File(samplePath);
        if (!sampleFile.exists()) {
            System.out.println("Test image not found: " + samplePath);
            return;
        }
        for (int i = 0; i < count; i++) {
            selectedAlbum.addPhoto(new Photo(sampleFile.getAbsolutePath()));
        }
        loadPhotosForAlbum(selectedAlbum);
        refreshAlbumList();
        try {
            DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
        } catch (IOException e) {
            System.out.println("Error saving test album: " + e.getMessage());
        }
    }

    /**
     * Temporary handler to trigger test album population.
     */
    @FXML
    private void handlePopulateTestAlbum() {
        populateTestAlbum(50);
    }

    /**
     * Loads photos from the specified album into the photoListView using a custom cell factory.
     * 
     * @param album the album from which to load photos.
     */
    private void loadPhotosForAlbum(Album album) {
        photoListView.setItems(FXCollections.observableArrayList(album.getPhotos()));
        photoListView.setCellFactory(list -> new ListCell<Photo>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    File photoFile = new File(photo.getFilePath());
                    if (!photoFile.exists()) {
                        System.out.println("Debug: Photo file not found: " + photo.getFilePath());
                        setText("File not found: " + photo.getFilePath());
                        setGraphic(null);
                    } else {
                        Image thumbnail = new Image(photoFile.toURI().toString(), 100, 0, true, true);
                        imageView.setImage(thumbnail);
                        String displayText = (photo.getCaption() == null || photo.getCaption().trim().isEmpty())
                                ? photo.getFilePath()
                                : photo.getCaption();
                        setText(displayText);
                        setGraphic(imageView);
                    }
                }
            }
        });
    }

    /**
     * Handles removing the selected photo from the album.
     */
    @FXML
    private void handleRemovePhoto() {
        if (selectedAlbum == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an album first.").showAndWait();
            return;
        }
        Photo photoToRemove = photoListView.getSelectionModel().getSelectedItem();
        if (photoToRemove == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a photo to remove.").showAndWait();
            return;
        }
        selectedAlbum.removePhoto(photoToRemove);
        loadPhotosForAlbum(selectedAlbum);
        refreshAlbumList();
        try {
            DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save user data after removing photo.").showAndWait();
        }
    }

    /**
     * Handles copying a photo from the selected album to another album.
     */
    @FXML
    private void handleCopyPhoto() {
        if (selectedAlbum == null) {
            new Alert(Alert.AlertType.WARNING, "Select an album to copy from").showAndWait();
            return;
        }
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a photo to copy.").showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Enter target album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(targetAlbumName -> {
            Album targetAlbum = currentUser.getAlbums().stream()
                    .filter(a -> a.getName().equalsIgnoreCase(targetAlbumName))
                    .findFirst().orElse(null);
            if (targetAlbum != null) {
                targetAlbum.addPhoto(selectedPhoto); // copying the reference
                try {
                    DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
                    refreshAlbumList();
                    new Alert(Alert.AlertType.INFORMATION, "Photo copied successfully.").showAndWait();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Save error: " + e.getMessage()).showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Target album not found.").showAndWait();
            }
        });
    }

    /**
     * Handles moving a photo from the selected album to another album.
     */
    @FXML
    private void handleMovePhoto() {
        if (selectedAlbum == null) {
            new Alert(Alert.AlertType.WARNING, "Select an album to move from").showAndWait();
            return;
        }
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a photo to move.").showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Enter target album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(targetAlbumName -> {
            Album targetAlbum = currentUser.getAlbums().stream()
                    .filter(a -> a.getName().equalsIgnoreCase(targetAlbumName))
                    .findFirst().orElse(null);
            if (targetAlbum != null) {
                targetAlbum.addPhoto(selectedPhoto);
                selectedAlbum.removePhoto(selectedPhoto);
                loadPhotosForAlbum(selectedAlbum);
                try {
                    DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
                    refreshAlbumList();
                    new Alert(Alert.AlertType.INFORMATION, "Photo moved successfully.").showAndWait();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Save error: " + e.getMessage()).showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Target album not found.").showAndWait();
            }
        });
    }

    /**
     * Opens the photo detail view for the selected photo.
     */
    @FXML
    private void handlePhotoClick() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null && selectedAlbum != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
                Parent root = loader.load();
                PhotoController photoController = loader.getController();
                photoController.setUser(currentUser);  // Pass the current user so that user-specific tag types are used.
                int index = selectedAlbum.getPhotos().indexOf(selectedPhoto);
                photoController.setPhotoData(selectedAlbum.getPhotos(), index);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Photo Detail");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to open photo detail: " + e.getMessage()).showAndWait();
            }
        }
    }

    /**
     * Handles logout from the current user session, returning to the login view.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Photo App Login");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load login screen.").showAndWait();
        }
    }

    /**
     * Opens the search view.
     */
    @FXML
    private void handleSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/search.fxml"));
            Parent searchRoot = loader.load();
            SearchController searchController = loader.getController();
            searchController.setUser(currentUser);
            Stage stage = new Stage();
            stage.setScene(new Scene(searchRoot));
            stage.setTitle("Search Photos");
            stage.show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load search view: " + e.getMessage()).showAndWait();
        }
    }
}