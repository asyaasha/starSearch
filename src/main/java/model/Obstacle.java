package model;

/**
 * Created by Dennis Eddington
 */
public abstract class Obstacle implements java.io.Serializable {
    public abstract String getObstacleName();
    public abstract int getObstacleX();
    public abstract  int getObstacleY();
}
