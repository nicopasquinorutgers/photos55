package controller;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Photo;
import model.Tag;

/**
 * Controller for the photo detail view.
 * 
 * <p>This controller manages displaying a photo in full, editing and removing its caption,
 * handling tags (adding, removing, and allowing custom tag types), and navigating through
 * an album of photos.</p>
 * 
 * @author Sara Annamraju
 * @version 1.0
 */
public class PhotoController {

    @FXML private ImageView photoView;
    @FXML private Label captionLabel;
    @FXML private TextField captionField;
    @FXML private Label dateLabel;
    @FXML private ListView<String> tagListView;
    @FXML private ComboBox<String> tagKeyComboBox;
    @FXML private TextField tagValueField;

    private Photo photo;
    private List<Photo> photoList;  // List for slideshow navigation
    private int currentIndex;       // Current index in the photo list

    /**
     * Initializes the controller by setting preset tag types.
     */
    @FXML
    private void initialize() {
        tagKeyComboBox.setItems(FXCollections.observableArrayList("Location", "Person", "Event"));
    }

    /**
     * Sets the current photo and updates the view.
     *
     * @param photo the photo to display.
     */
    public void setPhoto(Photo photo) {
        this.photo = photo;
        updateView();
    }
    
    /**
     * Sets the list of photos and the current index, then displays the photo at that index.
     *
     * @param photos the list of photos in the album.
     * @param index the current photo index.
     */
    public void setPhotoData(List<Photo> photos, int index) {
        this.photoList = photos;
        this.currentIndex = index;
        setPhoto(photos.get(index));
    }
    
    /**
     * Updates the view with the photo image, caption, date, and tags.
     */
    private void updateView() {
        File file = new File(photo.getFilePath());
        if (!file.exists()) {
            System.out.println("Debug: Photo file not found in PhotoController: " + photo.getFilePath());
            captionLabel.setText("Image file not found");
            photoView.setImage(null);
        } else {
            Image image = new Image(file.toURI().toString());
            photoView.setImage(image);
            captionLabel.setText("Caption: " + (photo.getCaption() != null && !photo.getCaption().isEmpty() ? photo.getCaption() : "No caption"));
        }
        dateLabel.setText("Date Taken: " + photo.getDateTaken().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        refreshTags();
    }
    
    /**
     * Refreshes the ListView displaying the photo's tags.
     */
    private void refreshTags() {
        tagListView.getItems().clear();
        for (Tag tag : photo.getTags()) {
            tagListView.getItems().add(tag.getKey() + ": " + tag.getValue());
        }
    }
    
    /**
     * Handles adding a tag to the current photo.
     */
    @FXML
    private void handleAddTag() {
        String key = tagKeyComboBox.getValue();
        if (key == null || key.isEmpty()) {
            showAlert("Tag Error", "Please select a tag type.");
            return;
        }
        String value = tagValueField.getText().trim();
        if (value.isEmpty()) {
            showAlert("Tag Error", "Value cannot be empty.");
            return;
        }

        // Enforce only one location tag per photo.
        if (key.equalsIgnoreCase("location")) {
        // Check if a location tag already exists.
            Optional<Tag> existingLocationTag = photo.getTags().stream().filter(tag -> tag.getKey().equalsIgnoreCase("location")) .findFirst();
            if (existingLocationTag.isPresent()) {
                // Option: Remove the existing location tag (effectively updating it).
                photo.removeTag(existingLocationTag.get());
            }
        }

        // Add the new tag
        photo.addTag(new Tag(key, value));
        refreshTags();
        tagKeyComboBox.getSelectionModel().clearSelection();
        tagValueField.clear();
    }

    
    /**
     * Handles removing the selected tag from the current photo.
     */
    @FXML
    private void handleRemoveTag() {
        String selected = tagListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Tag Error", "No tag selected.");
            return;
        }
        String[] parts = selected.split(": ");
        photo.removeTag(new Tag(parts[0], parts[1]));
        refreshTags();
    }
    
    /**
     * Saves the new caption entered by the user.
     */
    @FXML
    private void handleSaveCaption() {
        String newCaption = captionField.getText().trim();
        photo.setCaption(newCaption);
        captionLabel.setText("Caption: " + newCaption);
    }
    
    /**
     * Removes the caption from the photo.
     */
    @FXML
    private void handleRemoveCaption() {
        photo.setCaption("");
        captionLabel.setText("Caption: No caption");
        captionField.clear();
    }
    
    /**
     * Navigates to the previous photo in the album and updates the stage layout.
     */
    @FXML
    private void handlePrevious() {
        if (photoList == null || photoList.isEmpty()) return;
        currentIndex = (currentIndex - 1 + photoList.size()) % photoList.size();
        setPhoto(photoList.get(currentIndex));
        // Force layout update to ensure navigation buttons remain visible.
        Stage stage = (Stage) photoView.getScene().getWindow();
        stage.sizeToScene();
    }

    /**
     * Navigates to the next photo in the album and updates the stage layout.
     */
    @FXML
    private void handleNext() {
        if (photoList == null || photoList.isEmpty()) return;
        currentIndex = (currentIndex + 1) % photoList.size();
        setPhoto(photoList.get(currentIndex));
        // Force layout update to ensure navigation buttons remain visible.
        Stage stage = (Stage) photoView.getScene().getWindow();
        stage.sizeToScene();
    }

    /**
     * Closes the photo detail view.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) photoView.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Utility method to display an alert with a title and message.
     *
     * @param title the alert title.
     * @param content the alert message content.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Sets the tag key ComboBox to "location".
     */
    @FXML
    private void handlePresetTagLocation() {
        tagKeyComboBox.setValue("location");
    }
    
    /**
     * Sets the tag key ComboBox to "person".
     */
    @FXML
    private void handlePresetTagPerson() {
        tagKeyComboBox.setValue("person");
    }
    
    /**
     * Sets the tag key ComboBox to "event".
     */
    @FXML
    private void handlePresetTagEvent() {
        tagKeyComboBox.setValue("event");
    }
    
    /**
     * Allows the user to add a custom tag type to the preset list.
     */
    @FXML
    private void handleAddTagType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Tag Type");
        dialog.setHeaderText("Enter new tag type:");
        dialog.setContentText("Tag type:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newTagType = result.get().trim();
            if (!newTagType.isEmpty() && !tagKeyComboBox.getItems().contains(newTagType)) {
                tagKeyComboBox.getItems().add(newTagType);
            } else {
                showAlert("Error", "Invalid or duplicate tag type.");
            }
        }
    }
}