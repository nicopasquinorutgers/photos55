package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a tag associated with a photo.
 * A tag consists of a key and a value (for example, ("location", "New Brunswick")).
 * Two tags are considered equal if both the key and the value are equal, ignoring case.
 * This class implements Serializable for persistence.
 * 
 * @author Nico Pasquino
 * @version 1.0
 */
public class Tag implements Serializable {
    private String key;
    private String value;

    /**
     * Constructs a Tag with the specified key and value.
     * 
     * @param key the tag key (e.g., "location")
     * @param value the tag value (e.g., "New Brunswick")
     */
    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of this tag.
     * 
     * @return the tag key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value of this tag.
     * 
     * @return the tag value
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return key.equalsIgnoreCase(tag.key) && value.equalsIgnoreCase(tag.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(key.toLowerCase(), value.toLowerCase());
    }
}
