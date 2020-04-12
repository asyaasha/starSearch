import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Dennis Eddington
 *
 * Coordinator class is called upon to view the current status of the virtualizedField with a given selected drone and
 * executes an algorithm based on the strategy value to decide the proper action.
 */
public class Coordinator implements java.io.Serializable {

    private Drone activeDrone;
    private SpaceRegion virtualizedMap;
    private int strategy;
    private Log logBook;
    private HashMap<Orientation, Object[]> directionCollection;
    private int RETRY_LIMIT = 100;

    //Note: Consider best way to handle human controlled actions

    /**
     * Constructer for Coordinator class
     *
     * @param virtualizedMap Copy of the SpaceRegion that is not fully actualized for the coordinator to base its
     *                       decision from
     */
    public Coordinator(SpaceRegion virtualizedMap) {
        this.virtualizedMap = virtualizedMap;
        this.logBook = new Log();
        this.directionCollection = new HashMap<>();
    }

    /**
     * This method simply makes a call to the Log class in order to increment its internal clock.
     *
     * @see Log
     */
    public void incrementLogs() {
        logBook.incrementTurn();
    }

    /**
     * This method serves to take various input from the Simulation class and then synthesizes that information to pick
     * the proper action for the selected drone based on its strategy value.
     *
     * Strategy 0 = Random choice
     * Strategy 1 = Optimal choice
     * Strategy 2 code has been redacted from this submitted version of the code
     *
     * @param selectedDrone This parameter represents the current Drone object that will be recieving the action that
     *                      is determined by the Coordinator
     * @param virtualizedMap    Copy of the current SpaceRegion that is visible to Drones
     * @param strategy  A value varying between 0 and 1 that will decide the algorithm that is utilized in selected the
     *                  Drone's next move
     * @return  A String containing the proper decision from the Coordinator
     * @see Simulation
     */
    public String coordinateBestAction(Drone selectedDrone, SpaceRegion virtualizedMap, int strategy) {

        this.strategy = strategy;
        this.activeDrone = selectedDrone;
        this.virtualizedMap = virtualizedMap;

        int colLimit = virtualizedMap.getSpaceLayout()[0].length - 2;
        int rowLimit = virtualizedMap.getSpaceLayout().length - 2;


        if (strategy == 0) {
            String[] actions = new String[]{"thrust", "scan", "pass", "steer"};
            Random rand = new Random();
            String selectedAction = actions[rand.nextInt(actions.length)];

            if (selectedAction.equals("scan")) {
                return "scan";
            } else if (selectedAction.equals("pass")) {
                return "pass";
            } else if (selectedAction.equals("steer")) {
                Orientation[] orientations = new Orientation[]{Orientation.NORTH, Orientation.NORTHEAST, Orientation.NORTHWEST, Orientation.SOUTH, Orientation.SOUTHEAST, Orientation.SOUTHWEST, Orientation.EAST, Orientation.WEST};
                Orientation selectedOrientation = orientations[rand.nextInt(orientations.length)];
                return "steer," + selectedOrientation;
            } else if (selectedAction.equals("thrust")) {
                int[] moveOptions = new int[]{1,2,3};
                int selectedMove = moveOptions[rand.nextInt(moveOptions.length)];
                return "thrust," + selectedDrone.getDroneOrientation() + "," + Integer.toString(selectedMove);
            }
        }


        int droneX = selectedDrone.getDroneX();
        int droneY = selectedDrone.getDroneY();


        //Check to see if 50% of surroundings is revealed
        int[] guidance = new int[]{-1,1,0};
        int squareAvailable = 0;
        int exploredNeighbors = 0;

        for (int i = 0; i < guidance.length; i++) {
            for (int j = 0; j < guidance.length;j++) {
                if (guidance[i] == 0 && guidance[j] == 0) {
                    continue;
                } else {
                    int observableX = droneX + guidance[i];
                    int observableY = droneY + guidance[j];

                    StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                    if (observableField.getStarFieldContents() != Content.BARRIER) {
                        squareAvailable++;
                    }
                    if (observableField.getStarFieldContents() != Content.UNKNOWN && observableField.getStarFieldContents() != Content.BARRIER) {
                        exploredNeighbors++;
                    }
                }
            }
        }

        if ((double) exploredNeighbors/(double) squareAvailable >= 1.00) {
            //Enough information to make an action

            Orientation direction = selectedDrone.getDroneOrientation();

            Object[] currDirection_distance = new Object[3];
            Object[] bestDirection_distance = new Object[]{Orientation.NORTH, 0, false};

            currDirection_distance[0] = direction;
            currDirection_distance[1] = 0;
            currDirection_distance[2] = false;

            Orientation[] directionList = new Orientation[]{Orientation.NORTH, Orientation.NORTHEAST, Orientation.NORTHWEST, Orientation.SOUTH, Orientation.SOUTHEAST, Orientation.SOUTHWEST, Orientation.EAST, Orientation.WEST};

            for (Orientation currDirection:directionList) {
                int distance = 0;
                boolean containsUnexplored = false;


                    guidance = new int[]{1, 2, 3};
                    switch (currDirection) {
                        case NORTH:
                            //Iterate north of current position ( -y)
                            for (int delta = 1; delta < 4; delta++) {
                                int observableX = droneX;
                                int observableY = droneY + (delta);

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }

                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                        case NORTHEAST:
                            for (int delta = 0; delta < 3; delta++) {
                                int observableX = droneX + guidance[delta];
                                int observableY = droneY + (guidance[delta]);

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }
                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }  currDirection_distance[1] = distance;

                            break;
                        case EAST:
                            for (int delta = 1; delta < 4; delta++) {
                                int observableX = droneX + delta;
                                int observableY = droneY;

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }
                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                        case SOUTHEAST:
                            for (int delta = 0; delta < 3; delta++) {
                                int observableX = droneX + guidance[delta];
                                int observableY = droneY + -guidance[delta];

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }
                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                        case SOUTH:
                            for (int delta = 1; delta < 4; delta++) {
                                int observableX = droneX;
                                int observableY = droneY + -delta;

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }
                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                        case SOUTHWEST:
                            for (int delta = 0; delta < 3; delta++) {
                                int observableX = droneX + (-guidance[delta]);
                                int observableY = droneY + -guidance[delta];

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }
                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                        case WEST:
                            for (int delta = 1; delta < 4; delta++) {
                                int observableX = droneX + (-delta);
                                int observableY = droneY;

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }
                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                        case NORTHWEST:
                            for (int delta = 0; delta < 3; delta++) {
                                int observableX = droneX + (-guidance[delta]);
                                int observableY = droneY + (guidance[delta]);

                                StarField observableField = virtualizedMap.getSpaceLayout()[rowLimit - observableY][observableX + 1];
                                if (observableField.getStarFieldContents() == Content.UNKNOWN || observableField.getStarFieldContents() == Content.BARRIER || observableField.getStarFieldContents() == Content.DRONE || observableField.getStarFieldContents() == Content.STARS || observableField.getStarFieldContents() == Content.SUN) {
                                    //If barrier or unknown is hit - compare length and see if this is a candidate for best direction
                                    break;
                                }
                                if (!observableField.getExplorationStatus()) {
                                    containsUnexplored = true;
                                }
                                distance++;
                            }

                            directionCollection.put(currDirection, new Object[]{currDirection, distance, containsUnexplored});

                            if((boolean) bestDirection_distance[2] == true && containsUnexplored == false) {
                                break;
                            }

                            if ((boolean) bestDirection_distance[2] == false && containsUnexplored == true) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            } else if ((int) bestDirection_distance[1] < distance) {
                                bestDirection_distance[0] = currDirection;
                                bestDirection_distance[1] = distance;
                                bestDirection_distance[2] = containsUnexplored;
                            }
                            if (currDirection == direction) {
                                currDirection_distance[1] = distance;
                                currDirection_distance[2] = containsUnexplored;
                            }
                            break;
                    }
            }

            StringBuilder action = new StringBuilder();

            if ((boolean) currDirection_distance[2] == false && (boolean) bestDirection_distance[2] == true) {
                if (bestDirection_distance[0] == currDirection_distance[0]) {
                    action.append("thrust,");
                    action.append(bestDirection_distance[0]);
                    action.append(',');
                    action.append(bestDirection_distance[1]);
                    return action.toString();
                } else {
                    action.append("steer,");
                    action.append(bestDirection_distance[0]);
                    return action.toString();
                }

            } else if ((boolean) currDirection_distance[2] == false && (boolean) bestDirection_distance[2] == false) {
                //Explore randomly
                List<Orientation> keysAsArray = new ArrayList<Orientation>(directionCollection.keySet());
                keysAsArray.add((Orientation) currDirection_distance[0]);
                keysAsArray.add((Orientation) currDirection_distance[0]);
                Random rand = new Random();

                Object[] randomSelect = directionCollection.get(keysAsArray.get(rand.nextInt(keysAsArray.size())));

                int retries = 0;
                while (retries < RETRY_LIMIT) {
                    if ((int) randomSelect[1] == 0) {
                        randomSelect = directionCollection.get(keysAsArray.get(rand.nextInt(keysAsArray.size())));
                    } else {
                        break;
                    }
                    retries++;
                }


                if ( (Orientation) randomSelect[0] != currDirection_distance[0]) {
                    action.append("steer,");
                    action.append(randomSelect[0]);
                    return action.toString();
                } else {
                    action.append("thrust,");
                    action.append(randomSelect[0]);
                    action.append(',');
                    action.append(randomSelect[1]);
                    return action.toString();
                }
            }

            if (currDirection_distance[0] != bestDirection_distance[0]) {
                if ((int) bestDirection_distance[1] - (int) currDirection_distance[1] >= 2) {
                    action.append("steer,");
                    action.append(bestDirection_distance[0]);
                    return action.toString();
                } else {
                    // Thrust in curr direction anyways
                    if ((int) bestDirection_distance[1] > (int) currDirection_distance[1]) {
                        action.append("steer,");
                        action.append(bestDirection_distance[0]);
                        return action.toString();
                    }
                    action.append("thrust,");
                    action.append(currDirection_distance[0]);
                    action.append(',');
                    action.append(currDirection_distance[1]);
                    return action.toString();
                }
            } else {
                if ((int) bestDirection_distance[1] > (int) currDirection_distance[1]) {
                    action.append("thrust,");
                    action.append(bestDirection_distance[0]);
                    action.append(',');
                    action.append(bestDirection_distance[1]);
                    return action.toString();
                } else {
                    action.append("thrust,");
                    action.append(currDirection_distance[0]);
                    action.append(',');
                    action.append(currDirection_distance[1]);
                    return action.toString();
                }
            }
        } else {
            return "scan";
        }
    }

    /**
     * This method syncs the SpaceRegion contained in the Coordinator's attributes with the updated map version after a
     * Drone executes its requested action.
     *
     * @param virtualizedMap Copy of the SpaceRegion to be synced from the Simulation to the Coordinator
     */
    public void syncVirtualizedMap(SpaceRegion virtualizedMap) {
        this.virtualizedMap = virtualizedMap;
    }

    /**
     * Returns a reference to the Log class that has been maintained by this Coordinator
     *
     * @return A Log object
     */
    public Log getLogBook() {
        return this.logBook;
    }

}
