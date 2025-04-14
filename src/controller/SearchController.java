package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;

/**
 * Controller for the search view.
 * 
 * <p>This controller provides functionality to search for photos by a date range or by tag conditions.
 * It supports an advanced search using two tag criteria with an operator (AND/OR) and allows creating 
 * a new album based on the search results.</p>
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class SearchController {
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private TextField tagKeyField, tagValueField;
    @FXML
    private ComboBox<String> operatorComboBox;
    @FXML
    private TextField tagKeyField2, tagValueField2;
    @FXML
    private ListView<String> resultListView;
    
    private User currentUser;
    private List<Photo> searchResults = new ArrayList<>();
    
    /**
     * Initializes the search view by setting available operators for advanced tag search.
     */
    @FXML
    private void initialize() {
        operatorComboBox.setItems(FXCollections.observableArrayList("AND", "OR"));
    }
    
    /**
     * Sets the current user for the search session.
     * 
     * @param user the logged-in user.
     */
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Searches for photos by a date range.
     */
    @FXML
    private void handleSearchByDate() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            showAlert("Error", "Both start and end dates must be selected.");
            return;
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        searchResults.clear();
        for (Album album : currentUser.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                if (photo.getDateTaken().isAfter(startDateTime) && photo.getDateTaken().isBefore(endDateTime)) {
                    searchResults.add(photo);
                }
            }
        }
        displayResults();
    }
    
    /**
     * Searches for photos by tag conditions.
     * Supports advanced search if a second tag condition and operator are provided.
     */
    @FXML
    private void handleSearchByTag() {
        String key1 = tagKeyField.getText().trim();
        String value1 = tagValueField.getText().trim();
        if (key1.isEmpty() || value1.isEmpty()) {
            showAlert("Error", "Tag key and value must be provided for first condition.");
            return;
        }
        String key2 = tagKeyField2.getText().trim();
        String value2 = tagValueField2.getText().trim();
        String operator = operatorComboBox.getValue();
        
        searchResults.clear();
        for (Album album : currentUser.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                boolean cond1 = photo.getTags().stream().anyMatch(tag -> tag.getKey().equalsIgnoreCase(key1) && tag.getValue().equalsIgnoreCase(value1));
                if (key2.isEmpty() || value2.isEmpty() || operator == null || operator.isEmpty()) {
                    if (cond1) {
                        searchResults.add(photo);
                    }
                } else {
                    boolean cond2 = photo.getTags().stream().anyMatch(tag -> tag.getKey().equalsIgnoreCase(key2) && tag.getValue().equalsIgnoreCase(value2));
                    if ("AND".equalsIgnoreCase(operator)) {
                        if (cond1 && cond2) {
                            searchResults.add(photo);
                        }
                    } else if ("OR".equalsIgnoreCase(operator)) {
                        if (cond1 || cond2) {
                            searchResults.add(photo);
                        }
                    }
                }
            }
        }
        displayResults();
    }
    
    /**
     * Displays the search results in the resultListView.
     */
    private void displayResults() {
        resultListView.getItems().clear();
        for (Photo photo : searchResults) {
            resultListView.getItems().add(photo.getFilePath());
        }
        if (searchResults.isEmpty()) {
            showAlert("Info", "No photos found matching the criteria.");
        }
    }
    
    /**
     * Creates a new album from the current search results.
     */
    @FXML
    private void handleCreateAlbumFromResults() {
        if (searchResults.isEmpty()) {
            showAlert("Error", "No search results to create album.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album from Search Results");
        dialog.setHeaderText("Enter a name for the new album:");
        dialog.setContentText("Album name:");
        dialog.showAndWait().ifPresent(albumName -> {
            if (albumName.trim().isEmpty()) {
                showAlert("Error", "Album name cannot be empty.");
                return;
            }
            Album newAlbum = new Album(albumName.trim());
            for (Photo photo : searchResults) {
                newAlbum.addPhoto(photo);
            }
            currentUser.addAlbum(newAlbum);
            try {
                DataManager.saveUser(currentUser, "data/" + currentUser.getUsername() + ".dat");
                showAlert("Success", "Album created from search results.");
            } catch (IOException e) {
                showAlert("Error", "Failed to save album: " + e.getMessage());
            }
        });
    }
    
    /**
     * Closes the search view.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) resultListView.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Utility method to show an alert with the specified title and message.
     * 
     * @param title the alert title
     * @param msg the alert message content
     */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}