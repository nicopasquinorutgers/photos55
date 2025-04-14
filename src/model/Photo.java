package model;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo with a file path, a caption, a "date taken" (derived from the file's last modified time),
 * and a collection of tags.
 * This class implements Serializable for persistence.
 * 
 * The "date taken" is obtained via the Java API by reading the file's last modified timestamp.
 * 
 * @author Sara Annamraju
 * @version 1.0
 */
public class Photo implements Serializable {
    private String filePath;
    private LocalDateTime dateTaken;
    private String caption;
    private List<Tag> tags;

    /**
     * Constructs a Photo with the specified file path.
     * The dateTaken is set based on the file's last modified timestamp.
     * Initializes an empty list of tags.
     *
     * @param filePath the absolute file path of the photo
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        this.dateTaken = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(file.lastModified()),
            ZoneId.systemDefault()
        );
        this.tags = new ArrayList<>();
    }

    /**
     * Returns the file path of the photo.
     * 
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the date and time the photo was taken.
     * This value is derived from the file's last modified timestamp.
     * 
     * @return the date taken
     */
    public LocalDateTime getDateTaken() {
        return dateTaken;
    }

    /**
     * Returns the caption of the photo.
     * 
     * @return the caption, or null if not set
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption for the photo.
     * 
     * @param caption the new caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns the list of tags associated with the photo.
     * 
     * @return the list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the photo.
     * If an identical tag (case-insensitive) already exists, it is not added.
     * 
     * @param tag the tag to add
     */
    public void addTag(Tag tag) {
        for (Tag t : tags) {
            if (t.getKey().equalsIgnoreCase(tag.getKey()) && t.getValue().equalsIgnoreCase(tag.getValue())) {
                return;
            }
        }
        tags.add(tag);
    }

    /**
     * Removes the specified tag from the photo.
     * 
     * @param tag the tag to remove
     */
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
}