package model;

/**
 * Created by Dennis Eddington
 *
 * Enumerated class that details the various types of model.Content that can be found in a model.StarField
 */
public enum Content implements java.io.Serializable {
    STARS,
    EMPTY,
    SUN,
    BARRIER,
    DRONE,
    UNKNOWN
}
