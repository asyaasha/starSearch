package model;

/**
 * Created by Dennis Eddington
 *
 * This class represents the model.StarField object. This object serves as the representation for individual squares
 * comprising the Space Region in the model.Star Search model.Simulation.
 */
public class StarField implements java.io.Serializable {

    private int x;
    private int y;
    private Drone occupantDrone;
    private Obstacle occupantObstacle = null; // Changed to occupant obstacle.... I am making the assumption that only one obstacle will occupy any given space
    private Content starFieldContents;
    private boolean starFieldExplorationStatus;


    /**
     * Constructor for the model.StarField Object
     *
     * @param x x coordinate for model.StarField
     * @param y y coordinate for model.StarField
     */
    public StarField(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupantDrone = null;
        this.starFieldContents = Content.UNKNOWN;
        this.starFieldExplorationStatus = false;
    }

    /**
     * Obtains the X coordinate for the model.StarField
     *
     * This method is no longer a suitable way to acquire the coordinates for a model.StarField
     *
     * @return Integer detailing X coordinate
     */
    @Deprecated
    public int getX() {
        return x;
    }

    /**
     * Obtains the Y coordinate for the model.StarField
     *
     * This method is no longer a suitable way to acquire the coordinates for a model.StarField.
     *
     * @return Integer detailing Y coordinate
     */
    @Deprecated
    public int getY() {
        return y;
    }

    /**
     * Takes a model.Drone object and sets it as the occupant of the current model.StarField.
     *
     * @param currentDrone model.Drone that should be added into this model.StarField
     */
    public void setOccupantDrone(Drone currentDrone) {
        this.occupantDrone = currentDrone;
    }

    public void removeDroneOccupant() {
        this.occupantDrone = null;
    }

    /**
     * Checks the current model.StarField to see whether or not it contains a model.Drone object.
     *
     * This method is no longer a suitable way check whether or not a model.Drone occupies a model.StarField.
     *
     * @return Boolean value detailing whether or not there is a model.Drone in this model.StarField
     */
    @Deprecated
    public boolean checkDroneOccupant() {
        return occupantDrone == null;
    }

    /**
     * Returns a reference to the model.Drone object that is contained in this model.StarField.
     *
     * @return Returns a reference to the model.Drone that is currently occupying the model.StarField queried
     */
    public Drone getOccupantDrone() {
        return occupantDrone;
    }

    /**
     * Used to initially wire obstacles and instantiate them as part of a given model.StarField upon creation. It is
     * important to note that this method will also handle swapping the model.Content for you upon usage.
     *
     * @param obstacle The desired obstacle to add to the model.StarField
     */
    public void setOccupantObstacle(Obstacle obstacle) {
        this.occupantObstacle = obstacle;

        if (obstacle == null) {
            this.starFieldContents = Content.EMPTY;
            return;
        }

        String obstacleName = obstacle.getObstacleName();

        switch(obstacleName) {
            case "star":
                this.starFieldContents = Content.STARS;
                break;
            case "barrier":
                this.starFieldContents = Content.BARRIER;
                break;
            case "drone":
                this.starFieldContents = Content.DRONE;
                break;
            case "sun":
                this.starFieldContents = Content.SUN;
                break;
        }
    }

    /**
     * Returns the obstacle that is located inside of the current model.StarField. Values range from model.Star, model.Sun, model.Barrier.
     * Will return null value if space currently contains no obstacle.
     *
     * @return  Returns the obstacle object that is placed in the model.StarField
     */
    public Obstacle getOccupantObstacle() {
        return this.occupantObstacle;
    }

    /**
     * Returns the Contents of the model.StarField. See model.Content Enumeration file in order to find the full list of values.
     *
     * @return  Returns the Contents of the model.StarField
     * @see Content
     */
    public Content getStarFieldContents() {
        return starFieldContents;
    }

    /**
     * Serves to update the Contents of the model.StarField. This method is utilized throughout various classes in order to
     * Sync the content of the current (this._____) model.StarField with the contents of some other omniscient (baseMap).
     *
     * @param discoveredContent Input content discovered from some external source to update the content of the
     *                          currently explored model.StarField
     */
    public void updateStarFieldContents(Content discoveredContent) {
        this.starFieldContents = discoveredContent;
    }

    /**
     * Will return the current exploration status (whether or not the model.StarField has been explored).
     *
     * @return Boolean value that displays whether the model.StarField in question has been explored by a model.Drone or not
     */
    public boolean getExplorationStatus() {
        return starFieldExplorationStatus;
    }

    /**
     * Toggles the exploration status from false to true/true to false
     */
    public void toggleExplorationStatus() {
        starFieldExplorationStatus = !starFieldExplorationStatus;
    }
}
