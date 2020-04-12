import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dennis Eddington
 *
 * Drone class that represents the Drone object in the Star Search Simulation
 */
public class Drone implements java.io.Serializable {

    private int droneID;
    private Orientation droneOrientation;
    private int droneX;
    private int droneY;
    private int strategy;

    /**
     * Constructor for the Drone object that handles setting the object
     *
     * @param droneID   Auto incrementing ID number that is unique to each Drone object
     * @param droneX    X coordinate for Drone
     * @param droneY    Y Coordinate for Drone
     * @param direction Pulls from the Orientation class to line up the proper direction for the Drone to be facing
     * @param strategy  Determines which coordinator strategy will be utilized to determine best move for this Drone
     * @see Orientation
     * @see Coordinator
     */
    public Drone(int droneID, int droneX, int droneY, String direction, int strategy) {
        this.droneID = droneID;
        this.droneX = droneX;
        this.droneY = droneY;
        this.strategy = strategy;

        switch (direction) {
            case "north":
                droneOrientation = Orientation.NORTH;
                break;
            case "northeast":
                droneOrientation = Orientation.NORTHEAST;
                break;
            case "east":
                droneOrientation = Orientation.EAST;
                break;
            case "southeast":
                droneOrientation = Orientation.SOUTHEAST;
                break;
            case "south":
                droneOrientation = Orientation.SOUTH;
                break;
            case "southwest":
                droneOrientation = Orientation.SOUTHWEST;
                break;
            case "west":
                droneOrientation = Orientation.WEST;
                break;
            case "northwest":
                droneOrientation = Orientation.NORTHWEST;
                break;
        }
    }

    /**
     * Getter method for Strategy value
     *
     * @return Integer representing the strategy to be utilized for this Drone
     */
    public int getStrategy() {
        return this.strategy;
    }

    /**
     * Details an algorithm to execute the Thrust capability as requested from the Simulation class
     *
     * @param distance  The distance to be thrusted
     * @param baseMap   A copy of the baseMap
     * @param virtualizedMap    A copy of the virtualizedMap
     * @param droneRecord   A copy of the drone record (recording of active drones)
     * @throws ArrayIndexOutOfBoundsException Exception thrown for indexing out of bounds - helps determine impossible
     * actions
     */
    public void thrust(int distance, SpaceRegion baseMap, SpaceRegion virtualizedMap, HashMap<Integer, DroneStatus> droneRecord) throws ArrayIndexOutOfBoundsException {

        int modified_x = droneX;
        int modified_y = droneY;

        int colLimit = baseMap.getSpaceLayout()[0].length - 2;
        int rowLimit = baseMap.getSpaceLayout().length - 2;

        StarField observableFieldBase = baseMap.getSpaceLayout()[rowLimit - modified_y + 1][modified_x];
        StarField observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - modified_y + 1][modified_x];

        int[] guidance = new int[]{1, 2, 3};
        switch (droneOrientation) {
            case NORTH:
                //Iterate north of current position ( -y)
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x;
                    int observableY = modified_y + (guidance[delta]);

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {

                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x;
                        observableY = modified_y + (guidance[delta - 1]);

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    //Mark space explored
                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case NORTHEAST:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x + guidance[delta];
                    int observableY = modified_y + (guidance[delta]);

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x + guidance[delta - 1];
                        observableY = modified_y + (guidance[delta - 1]);

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case EAST:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x + guidance[delta];
                    int observableY = modified_y;

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x + guidance[delta - 1];
                        observableY = modified_y;

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case SOUTHEAST:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x + guidance[delta];
                    int observableY = modified_y + -guidance[delta];

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x + guidance[delta - 1];
                        observableY = modified_y + (-guidance[delta - 1]);

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case SOUTH:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x;
                    int observableY = modified_y + -guidance[delta];

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x;
                        observableY = modified_y + (-guidance[delta - 1]);

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case SOUTHWEST:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x + (-guidance[delta]);
                    int observableY = modified_y + -guidance[delta];

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x + (-guidance[delta - 1]);
                        observableY = modified_y + (-guidance[delta - 1]);

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case WEST:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x + (-guidance[delta]);
                    int observableY = modified_y;

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x + (-guidance[delta - 1]);
                        observableY = modified_y;

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1 || distance == 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
            case NORTHWEST:
                for (int delta = 0; delta < distance; delta++) {
                    int observableX = modified_x + (-guidance[delta]);
                    int observableY = modified_y + (guidance[delta]);

                    observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                    if (observableFieldBase.getStarFieldContents() == Content.BARRIER) {
                        if (delta == 0) {
                            break;
                        }

                        observableX = modified_x + (-guidance[delta - 1]);
                        observableY = modified_y + (guidance[delta - 1]);

                        observableFieldBase = baseMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                        observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];

                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;

                        break;
                    }

                    if (!observableFieldVirtualized.getExplorationStatus()) {
                        observableFieldVirtualized.toggleExplorationStatus();
                    }

                    if (observableFieldBase.getStarFieldContents() == Content.SUN ||observableFieldBase.getStarFieldContents() == Content.DRONE) {
                        vaporize(baseMap, virtualizedMap, observableFieldBase, observableFieldVirtualized, modified_x + 1, rowLimit - modified_y, droneRecord);
                        break;
                    }

                    observableFieldVirtualized.updateStarFieldContents(observableFieldBase.getStarFieldContents());
                    observableFieldVirtualized.setOccupantObstacle(observableFieldBase.getOccupantObstacle());

                    if (delta == distance - 1) {
                        StarField observableFieldBase_source = baseMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];
                        StarField observableFieldVirtualized_source = virtualizedMap.getSpaceLayout()[rowLimit - modified_y][modified_x + 1];

                        observableFieldBase.setOccupantDrone(observableFieldBase_source.getOccupantDrone());
                        observableFieldVirtualized.setOccupantDrone(observableFieldVirtualized_source.getOccupantDrone());

                        observableFieldBase_source.removeDroneOccupant();
                        observableFieldVirtualized_source.removeDroneOccupant();

                        observableFieldBase_source.updateStarFieldContents(Content.EMPTY);
                        observableFieldVirtualized_source.updateStarFieldContents(Content.EMPTY);

                        observableFieldBase.updateStarFieldContents(Content.DRONE);
                        observableFieldVirtualized.updateStarFieldContents(Content.DRONE);

                        this.droneX = observableX;
                        this.droneY = observableY;
                    }
                }
                break;
        }
    }

    /**
     * Executes the Steer action for a given Drone
     *
     * @param newOrientation
     * @param baseMap
     * @param virtualizedMap
     */
    public void steer(String newOrientation, SpaceRegion baseMap, SpaceRegion virtualizedMap) {
        newOrientation = newOrientation.toLowerCase();
        int modified_x = droneX + 1;
        int modified_y = droneY + 1;

        int colLimit = baseMap.getSpaceLayout()[0].length - 2;
        int rowLimit = baseMap.getSpaceLayout().length - 2;

        StarField observableFieldBase = baseMap.getSpaceLayout()[rowLimit - modified_y + 1][modified_x];
        StarField observableFieldVirtualized = virtualizedMap.getSpaceLayout()[rowLimit - modified_y + 1][modified_x];

        switch (newOrientation) {
            case "north":
                droneOrientation = Orientation.NORTH;
                break;
            case "northeast":
                droneOrientation = Orientation.NORTHEAST;
                break;
            case "east":
                droneOrientation = Orientation.EAST;
                break;
            case "southeast":
                droneOrientation = Orientation.SOUTHEAST;
                break;
            case "south":
                droneOrientation = Orientation.SOUTH;
                break;
            case "southwest":
                droneOrientation = Orientation.SOUTHWEST;
                break;
            case "west":
                droneOrientation = Orientation.WEST;
                break;
            case "northwest":
                droneOrientation = Orientation.NORTHWEST;
                break;
        }
    }

    /**
     * Executes the Scan action for a given Drone
     * @param baseMap
     * @param virtualizedMap
     * @return
     */
    public ArrayList<Content> scan(SpaceRegion baseMap, SpaceRegion virtualizedMap) {

        int modified_x = droneX + 1;
        int modified_y = droneY + 1;

        int colLimit = baseMap.getSpaceLayout()[0].length - 2;
        int rowLimit = baseMap.getSpaceLayout().length - 2;

        ArrayList<Content> surroundings = new ArrayList<>();

        int[][] guidance = {{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1}};
        for (int[] currGuidance:guidance) {
            int x_guidance = currGuidance[0];
            int y_guidance = currGuidance[1];

            int observable_x = modified_x + x_guidance;
            int observable_y = modified_y + y_guidance;

            StarField observableField = baseMap.getSpaceLayout()[rowLimit - observable_y + 1][observable_x];
            StarField virtualizedField = virtualizedMap.getSpaceLayout()[rowLimit - observable_y + 1][observable_x];

            if (observableField.getStarFieldContents() != Content.BARRIER) {
                virtualizedMap.getSpaceLayout()[rowLimit - observable_y + 1][observable_x].updateStarFieldContents(observableField.getStarFieldContents());
            }
            Content toAdd = null;
            if (virtualizedField.getStarFieldContents() == Content.EMPTY && virtualizedField.getExplorationStatus() == false) {
                surroundings.add(Content.STARS);
            } else {
                surroundings.add(observableField.getStarFieldContents());
            }
        }
        return surroundings;
    }

    /**
     * Pass method for Drone
     */
    public void pass() {
        //Do nothing :)
        return;
    }

    /**
     * Vaporize method for Drone
     *
     * @param baseMap
     * @param virtualizedMap
     * @param observableFieldBase
     * @param observableFieldVirtualized
     * @param x
     * @param y
     * @param droneRecord
     */
    public void vaporize(SpaceRegion baseMap, SpaceRegion virtualizedMap, StarField observableFieldBase, StarField observableFieldVirtualized, int x, int y, HashMap<Integer, DroneStatus> droneRecord) {
        if (observableFieldBase.getStarFieldContents() == Content.DRONE) {

            Drone temp = observableFieldBase.getOccupantDrone();
            virtualizedMap.getSpaceLayout()[y][x].removeDroneOccupant();
            virtualizedMap.getSpaceLayout()[y][x].updateStarFieldContents(Content.EMPTY);
            baseMap.getSpaceLayout()[y][x].removeDroneOccupant();
            baseMap.getSpaceLayout()[y][x].updateStarFieldContents(Content.EMPTY);

            observableFieldBase.removeDroneOccupant();
            observableFieldBase.updateStarFieldContents(Content.EMPTY);

            observableFieldVirtualized.updateStarFieldContents(Content.EMPTY);
            observableFieldVirtualized.removeDroneOccupant();

            droneRecord.put(this.droneID, DroneStatus.INACTIVE);
            droneRecord.put(temp.getDroneID(), DroneStatus.INACTIVE);

        } else if (observableFieldBase.getStarFieldContents() == Content.SUN) {
            baseMap.getSpaceLayout()[y][x].removeDroneOccupant();
            baseMap.getSpaceLayout()[y][x].updateStarFieldContents(Content.EMPTY);
            virtualizedMap.getSpaceLayout()[y][x].removeDroneOccupant();
            virtualizedMap.getSpaceLayout()[y][x].updateStarFieldContents(Content.EMPTY);

            droneRecord.put(this.droneID, DroneStatus.INACTIVE);
        }
    }

    /**
     * Getter for Drone ID
     *
     * @return
     */
    public int getDroneID() {return droneID;}

    /**
     * Getter for Drone X Coordinate
     *
     * @return
     */
    public int getDroneX() {
        return droneX;
    }

    /**
     * Getter for Drone Y Coordinate
     *
     * @return
     */
    public int getDroneY() {
        return droneY;
    }

    public Orientation getDroneOrientation() {return droneOrientation;}
}
