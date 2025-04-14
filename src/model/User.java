package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the photo application.
 * A user has a username and a collection of albums.
 * This class implements Serializable for persistence.
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class User implements Serializable {
    private String username;
    private List<Album> albums;

    /**
     * Constructs a new User with the specified username.
     * Initializes an empty list of albums.
     * 
     * @param username the username for the user
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }

    /**
     * Returns the username of the user.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the list of albums for the user.
     * 
     * @return the list of albums
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds an album to the user's collection.
     * 
     * @param album the album to add
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }

    /**
     * Removes an album from the user's collection.
     * 
     * @param album the album to remove
     */
    public void removeAlbum(Album album) {
        albums.remove(album);
    }
}