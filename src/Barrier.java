/**
 * Created by Dennis Eddington
 *
 * Class extending Obstacle class that represents a type of Obstacle - Barriers
 */
public class Barrier extends Obstacle {

    private String obstacleName = "barrier";
    private int obstacleX;
    private int obstacleY;

    /**
     * Constructor for Barrier object
     * @param x x coordinate of Barrier
     * @param y y coordinate of Barrier
     */
    public Barrier(int x, int y) {
        this.obstacleX = x;
        this.obstacleY = y;
    }

    /**
     * Returns the name of the Barrier
     *
     * @return String containing the obstacle's name - Barrier
     */
    public String getObstacleName() {
        return obstacleName;
    }

    /**
     * Returns X coordinate of Barrier object
     *
     * @return Integer detailing the X coordinate of the obstacle
     */
    public int getObstacleX() {
        return obstacleX;
    }

    /**
     * Returns Y coordinate of the Barrier object
     *
     * @return Integer detailing the Y coordinate of the obstacle
     */
    public int getObstacleY() {
        return obstacleY;
    }
}
