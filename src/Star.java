/**
 * Created by Dennis Eddington
 */
public class Star extends Obstacle {

    private String obstacleName = "star";
    private int obstacleX;
    private int obstacleY;

    public Star(int x, int y) {
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
