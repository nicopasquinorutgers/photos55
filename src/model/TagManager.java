package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A utility class that manages the preset list of tag types.
 * Users can add custom tag types and, from that point on, those types will 
 * be available in the drop-down menus throughout the application.
 * This list is stored in a static ObservableList so that it is shared across
 * all parts of the application.
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class TagManager {
    
    // Default tag types.
    private static ObservableList<String> tagTypes = FXCollections.observableArrayList("Location", "Person", "Event");
    
    /**
     * Returns the global list of tag types.
     * 
     * @return an ObservableList of tag types.
     */
    public static ObservableList<String> getTagTypes() {
        return tagTypes;
    }
    
    /**
     * Adds a new tag type to the global list if it is not already present.
     * 
     * @param tagType the new tag type to add.
     */
    public static void addTagType(String tagType) {
        if (tagType != null && !tagType.trim().isEmpty() && !tagTypes.contains(tagType)) {
            tagTypes.add(tagType);
        }
    }
}
