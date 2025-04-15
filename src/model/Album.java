package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album that contains a collection of photos.
 * An album has a name and a list of Photo objects.
 * This class implements Serializable so that album data can be saved and loaded.
 * 
 * @author Nico Pasquino / Sara Annamraju
 * @version 1.0
 */
public class Album implements Serializable {
    private String name;
    private List<Photo> photos;

    /**
     * Constructs a new Album with the specified name.
     * Initializes an empty list of photos.
     * 
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Returns the name of the album.
     * 
     * @return the album name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets a new name for the album.
     * 
     * @param newName the new album name
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Returns the list of photos in the album.
     * 
     * @return the list of photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Adds a photo to the album if a photo with the same file path is not already present.
     * Duplicate photos (based on file path) are not allowed.
     * 
     * @param photo the Photo to add
     */
    public boolean addPhoto(Photo photo) {
        // Check each photo already in the album. If one has the same file path, do not add the duplicate.
        for (Photo p : photos) {
            if (p.getFilePath().equalsIgnoreCase(photo.getFilePath())) {
                // Duplicate found: do not add
                return false;
            }
        }
        photos.add(photo);
        return true;
    }

    /**
     * Removes a photo from the album.
     * 
     * @param photo the Photo to remove
     */
    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }
}