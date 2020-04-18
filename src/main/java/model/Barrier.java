package model;

/**
 * Created by Dennis Eddington
 *
 * Class extending model.Obstacle class that represents a type of model.Obstacle - Barriers
 */
public class Barrier extends Obstacle implements java.io.Serializable {

    private String obstacleName = "barrier";
    private int obstacleX;
    private int obstacleY;

    /**
     * Constructor for model.Barrier object
     * @param x x coordinate of model.Barrier
     * @param y y coordinate of model.Barrier
     */
    public Barrier(int x, int y) {
        this.obstacleX = x;
        this.obstacleY = y;
    }

    /**
     * Returns the name of the model.Barrier
     *
     * @return String containing the obstacle's name - model.Barrier
     */
    public String getObstacleName() {
        return obstacleName;
    }

    /**
     * Returns X coordinate of model.Barrier object
     *
     * @return Integer detailing the X coordinate of the obstacle
     */
    public int getObstacleX() {
        return obstacleX;
    }

    /**
     * Returns Y coordinate of the model.Barrier object
     *
     * @return Integer detailing the Y coordinate of the obstacle
     */
    public int getObstacleY() {
        return obstacleY;
    }
}
