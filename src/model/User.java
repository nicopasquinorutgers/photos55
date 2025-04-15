package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the photo application.
 * A user has a username, a collection of albums, and a list of custom tag types.
 * This class implements Serializable for persistence.
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class User implements Serializable {
    private String username;
    private List<Album> albums;
    private List<String> customTagTypes;  // User-specific custom tag types

    /**
     * Constructs a new User with the specified username.
     * Initializes an empty list of albums and default tag types.
     * 
     * @param username the username for the user
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
        this.customTagTypes = new ArrayList<>();
        // Initialize with default tag types.
        // These defaults are part of every user's tag list.
        customTagTypes.add("Location");
        customTagTypes.add("Person");
        customTagTypes.add("Event");
    }

    /**
     * Returns the username of the user.
     * 
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the list of albums for the user.
     * 
     * @return the list of albums.
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds an album to the user's collection.
     * 
     * @param album the album to add.
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }

    /**
     * Removes an album from the user's collection.
     * 
     * @param album the album to remove.
     */
    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    /**
     * Returns the list of custom tag types for this user.
     * 
     * @return the list of custom tag types.
     */
    public List<String> getCustomTagTypes() {
        return customTagTypes;
    }

    /**
     * Adds a new custom tag type for this user if it is not already present.
     * 
     * @param tagType the custom tag type to add.
     * @return true if the tag type was added; false if it was already present or invalid.
     */
    public boolean addCustomTagType(String tagType) {
        String trimmed = tagType.trim();
        if (!trimmed.isEmpty() && !customTagTypes.contains(trimmed)) {
            customTagTypes.add(trimmed);
            return true;
        }
        return false;
    }
}