package model;

/**
 * Created by Dennis Eddington
 */
public class Sun extends Obstacle implements java.io.Serializable {


    private String obstacleName = "sun";
    private int obstacleX;
    private int obstacleY;

    public Sun(int x, int y) {
        this.obstacleX = x;
        this.obstacleY = y;
    }

    public String getObstacleName() {
        return obstacleName;
    }

    public int getObstacleX() {
        return obstacleX;
    }

    public int getObstacleY() {
        return obstacleY;
    }
}
