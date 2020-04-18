package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dennis Eddington
 */
public class Simulation implements java.io.Serializable {
    private SpaceRegion baseMap;
    private SpaceRegion virtualizedMap;
    private ArrayList<Drone> activeDrones;
    private Coordinator activeCoordinator;
    private int turnLimit;
    private HashMap<Integer, DroneStatus> droneRecord;
    private boolean humanControlled = false; // Defaulting for testing
    public String status = "CONTINUE_SIMULATION";
    private String[] parsedResponse = { ""};
    private ArrayList<String> stepProgress = new ArrayList<>();

    /**
     * Constructor for model.Simulation class
     *
     * @param instructionSet
     */
    public Simulation(HashMap<String,String[]> instructionSet) {
        this.simulationSetup(instructionSet);
    }

    public SpaceRegion getBaseMap() {
        return this.baseMap;
    }
    public ArrayList getStepProgress() {
        return this.stepProgress;
    }

    public SpaceRegion getVirtualizedMap() {
        return this.virtualizedMap;
    }
    public int getTurnLimit() {
        return this.turnLimit;
    }
    //GENERAL SIMULATION NOTE...... OBJECT COORDS DIFFER BY 1,1 FROM POSITION IN SPACEREGION

    /**
     * Uses the instruction set to setup all needed components
     *
     * @param instructionSet
     */
    private void simulationSetup(HashMap<String,String[]> instructionSet) {
        activeDrones = new ArrayList<>();
        String[] droneLocations = instructionSet.get("DroneLocations");
        this.droneSetup(droneLocations);

        //Acquire Map Dimensions
        String[] dimensions = instructionSet.get("Dimensions");
        int width = Integer.parseInt(dimensions[0]);
        int length = Integer.parseInt(dimensions[1]);

        //Acquire Map Obstacles
        String[] obstacleList = instructionSet.get("ObstacleLocations");
        this.populateSpaceLayoutBase(width, length, obstacleList);
        this.populateSpaceLayoutVirtualized(width, length);

        //Setup model.Coordinator
        activeCoordinator = new Coordinator(virtualizedMap);

        //Set turn limit
        turnLimit = Integer.parseInt(instructionSet.get("MaxTurns")[0]);
    }

    /**
     * Step model.Simulation
     */
    public void stepSimulation() {
        stepProgress = new ArrayList<>();
        if (!checkCompletionOfSimulation(baseMap, virtualizedMap) && !checkTurnCompletion()) {
            activeCoordinator.incrementLogs();
            for (Drone currDrone:activeDrones) {
                //Verify model.Drone is still active
                int droneID = currDrone.getDroneID();

                if (droneRecord.get(droneID) == DroneStatus.ACTIVE) {
                    System.out.println("droneID stepSimulation");
                    System.out.println(droneID);
                    //visualizeVirtualizedMap();
                    String response = activeCoordinator.coordinateBestAction(currDrone, virtualizedMap, currDrone.getStrategy());
                    this.executeCoordinatorResponse(currDrone, response, baseMap, virtualizedMap);
                }
                activeCoordinator.syncVirtualizedMap(virtualizedMap);
            }
            //Cross reference drone record
            for (Integer curr_droneID:droneRecord.keySet()) {
                boolean droneExists = false;
                DroneStatus status = droneRecord.get(curr_droneID);
                if (status == DroneStatus.ACTIVE) {
                    droneExists = true;
                } else if (status == DroneStatus.INACTIVE){
                    droneExists = false;
                }
                if (droneExists == false) {
                    int deletion_candidate = -1;
                    for (int i = 0; i < activeDrones.size(); i++) {
                        if (activeDrones.get(i).getDroneID() == curr_droneID) {
                            deletion_candidate = i;
                        }
                    }
                    if (deletion_candidate != -1) {
                        activeDrones.remove(deletion_candidate);
                    }
                }
            }
            if (activeDrones.size() == 0) {
                status = "END_SIMULATION";
            }
            status =  "CONTINUE_SIMULATION";
        } else {
            status =  "END_SIMULATION";
        }
    }

    /**
     * Begins the model.Simulation
     */
    public void runSimulation() {
        while (!checkCompletionOfSimulation(baseMap, virtualizedMap) && !checkTurnCompletion()) {
            activeCoordinator.incrementLogs();
            for (Drone currDrone:activeDrones) {
                //Verify model.Drone is still active
                int droneID = currDrone.getDroneID();
                if (droneRecord.get(droneID) == DroneStatus.ACTIVE) {
                    //visualizeVirtualizedMap();
                    String response = activeCoordinator.coordinateBestAction(currDrone, virtualizedMap, currDrone.getStrategy());
                    this.executeCoordinatorResponse(currDrone, response, baseMap, virtualizedMap);
                }
                activeCoordinator.syncVirtualizedMap(virtualizedMap);
            }
            //Cross reference drone record
            for (Integer curr_droneID:droneRecord.keySet()) {
                boolean droneExists = false;
                DroneStatus status = droneRecord.get(curr_droneID);
                if (status == DroneStatus.ACTIVE) {
                    droneExists = true;
                } else if (status == DroneStatus.INACTIVE){
                    droneExists = false;
                }
                if (droneExists == false) {
                    int deletion_candidate = -1;
                    for (int i = 0; i < activeDrones.size(); i++) {
                        if (activeDrones.get(i).getDroneID() == curr_droneID) {
                            deletion_candidate = i;
                        }
                    }
                    if (deletion_candidate != -1) {
                        activeDrones.remove(deletion_candidate);
                    }
                }
            }
            if (activeDrones.size() == 0) {
                return;
            }
        }
    }

                /*
                Parse Guide:
                X,Y,DIRECTION

                Because of border padding for barrier we add 1 to both
                EXAMPLE:
                Assume ^ represents drone
                model.Drone spec details drone should be at 2,2
                Dims are 4x4 matrix

                ---ORIG---
                OOOO
                OOOO
                OO^O
                OOOO

                ---MODIFIED---
                **Dims must become 6x6 to account for barrier padding**
                XXXXXX
                XOOOOX
                XOOOOX
                XOO^OX
                XOOOOX
                XXXXXX

                Let it be noticed that drone at 2,2 has been repositioned to location 3,3
                (similar parse guide for obstacles!)


             */

    /**
     * Setups the model.Drone List
     *
     * @param droneLocations
     */
    private void droneSetup(String[] droneLocations) {
        droneRecord = new HashMap<>();
        int droneID = 0;
        for (String droneSpecs:droneLocations) {
            String[] parsedSpecs = droneSpecs.split(",");
            String orientationSpec = parsedSpecs[2];
            String strategySpec = parsedSpecs[3];

            Drone currDrone = new Drone(droneID, Integer.parseInt(parsedSpecs[0]), Integer.parseInt(parsedSpecs[1]), orientationSpec, Integer.parseInt(strategySpec));
            activeDrones.add(currDrone);

            //Add into droneRecord
            droneRecord.put(droneID, DroneStatus.ACTIVE);

            //Step DroneID
            droneID++;
        }
    }

    /**
     * Builds a Space Region
     *
     * @param width
     * @param length
     * @param obstacleList
     */
    private void populateSpaceLayoutBase(int width, int length, String[] obstacleList) {
        baseMap = new SpaceRegion(width, length);
        baseMap.generateSpaceRegion(activeDrones, obstacleList);
    }

    /**
     * Builds Space Region
     *
     * @param width
     * @param length
     */
    private void populateSpaceLayoutVirtualized(int width, int length) {
        virtualizedMap = new SpaceRegion(width, length);
        virtualizedMap.generateSpaceRegion(activeDrones);
    }

    /**
     * Executes the coordinator response
     *
     * @param selectedDrone
     * @param coordinatorResponse
     * @param baseMap
     * @param virtualizedMap
     */
    private void executeCoordinatorResponse(Drone selectedDrone, String coordinatorResponse, SpaceRegion baseMap, SpaceRegion virtualizedMap) {
        parsedResponse = coordinatorResponse.split(",");
        String detail = "";
        String currStatus = "ok";

        SpaceRegion currentBase = baseMap;
        SpaceRegion currentVirtual = virtualizedMap;

        if (parsedResponse[0].equals("scan")) {
            activeCoordinator.getLogBook().submitLogRecord("d" + Integer.toString(selectedDrone.getDroneID()) +",scan");
            ArrayList<Content> surroundings = selectedDrone.scan(baseMap, virtualizedMap);
            StringBuilder searchResults = new StringBuilder();
            for (Content surrounding:surroundings) {
                searchResults.append(surrounding.toString().toLowerCase());
                searchResults.append(',');
            }
            searchResults.deleteCharAt(searchResults.length() - 1);
            activeCoordinator.getLogBook().submitLogRecord(searchResults.toString());
        } else if (parsedResponse[0].equals("steer")) {
            detail = parsedResponse[1];
            selectedDrone.steer(parsedResponse[1], baseMap, virtualizedMap);
            activeCoordinator.getLogBook().submitLogRecord("d" + Integer.toString(selectedDrone.getDroneID()) +",steer," + parsedResponse[1].toLowerCase());
            activeCoordinator.getLogBook().submitLogRecord("ok");
        } else if (parsedResponse[0].equals("thrust")) {
            detail = parsedResponse[2];
            if (Integer.parseInt(parsedResponse[2]) == 0 || Integer.parseInt(parsedResponse[2]) >= 4) {
                activeCoordinator.getLogBook().submitLogRecord("d" + Integer.toString(selectedDrone.getDroneID()) +",thrust," + parsedResponse[2]);
                activeCoordinator.getLogBook().submitLogRecord("action_not_recognized");
                return;
            }
            try {
                activeCoordinator.getLogBook().submitLogRecord("d" + Integer.toString(selectedDrone.getDroneID()) +",thrust," + parsedResponse[2]);
                selectedDrone.thrust(Integer.parseInt(parsedResponse[2]), baseMap, virtualizedMap, droneRecord);
                if (droneRecord.get(selectedDrone.getDroneID()) == DroneStatus.ACTIVE) {
                    activeCoordinator.getLogBook().submitLogRecord("ok");
                } else {
                    activeCoordinator.getLogBook().submitLogRecord("crash");
                    currStatus = "crash";
                }
            } catch (Exception e) {
                activeCoordinator.getLogBook().submitLogRecord("action_not_recognized");
                this.virtualizedMap = currentVirtual;
                this.baseMap = currentBase;
            }
        } else if (parsedResponse[0].equals("pass")) {
            activeCoordinator.getLogBook().submitLogRecord("d" + selectedDrone.getDroneID() + ",pass");
            selectedDrone.pass();
            activeCoordinator.getLogBook().submitLogRecord("ok");
        }

        stepProgress.add("Drone ID: " + selectedDrone.getDroneID() + " " + parsedResponse[0] + " " + detail + " " + currStatus);
    }

    public String[] getParsedResponse(){
        return this.parsedResponse;
    }

    /**
     * Check to see if all squares have been marked as explored
     * @param baseMap
     * @param virtualizedMap
     * @return
     */
    private boolean checkCompletionOfSimulation(SpaceRegion baseMap, SpaceRegion virtualizedMap) {
        int row = baseMap.getSpaceLayout().length;
        int col = baseMap.getSpaceLayout()[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.UNKNOWN) {
                    return false;
                }
                if (virtualizedMap.getSpaceLayout()[i][j].getExplorationStatus() == false) {
                    if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.BARRIER || virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.STARS || virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.SUN) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Method that checks to see if the turn limit has been met or exceeded
     * @return
     */
    private boolean checkTurnCompletion() {
        if (activeCoordinator.getLogBook().getTurns() >= turnLimit) {
            return true;
        }
        return false;
    }

    /**
     * Ends the model.Simulation and return String
     */
    public ArrayList<String> endSimulation() {
        HashMap<String, String> simulationDetails = activeCoordinator.getLogBook().exportSummaryReport(this.virtualizedMap, this.baseMap);
        StringBuilder report = new StringBuilder();
        report.append(simulationDetails.get("SpaceRegionSize"));
        report.append(',');
        report.append(simulationDetails.get("TotalSafeSpace"));
        report.append(',');
        report.append(simulationDetails.get("ExploredSafeSpace"));
        report.append(',');
        report.append(simulationDetails.get("TurnsElapsed"));
        activeCoordinator.getLogBook().submitLogRecord(report.toString());
        return activeCoordinator.getLogBook().getFullLog();
    }

    /****************TEST CODE*********************/

    /**
     * Will visualized (Create GUI) for baseMap
     */
    public void visualizeBaseMap() {
        int row = baseMap.getSpaceLayout().length;
        int col = baseMap.getSpaceLayout()[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.BARRIER) {
                    System.out.print("B");
                } else if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.DRONE) {
                    System.out.print("D");
                } else if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.EMPTY) {
                    System.out.print("E");
                } else if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.STARS) {
                    System.out.print("*");
                } else if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.SUN) {
                    System.out.print("S");
                } else if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.UNKNOWN) {
                    System.out.print("U");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Will visualize (Create GUI) for virtualizedMap
     */
    public void visualizeVirtualizedMap() {
        int row = virtualizedMap.getSpaceLayout().length;
        int col = virtualizedMap.getSpaceLayout()[0].length;
        System.out.println(row);
        System.out.println(col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.BARRIER) {
                    System.out.print("B");
                } else if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.DRONE) {
                    System.out.print(virtualizedMap.getSpaceLayout()[i][j].getOccupantDrone().getDroneID());
                } else if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.EMPTY) {
                    System.out.print("E");
                } else if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.STARS) {
                    System.out.print("*");
                } else if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.SUN) {
                    System.out.print("S");
                } else if (virtualizedMap.getSpaceLayout()[i][j].getStarFieldContents() == Content.UNKNOWN) {
                    System.out.print("U");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Shows the current status of the virtualizedMap
     */
    public void showExplorationStatus() {
        int row = virtualizedMap.getSpaceLayout().length;
        int col = virtualizedMap.getSpaceLayout()[0].length;
        System.out.println(row);
        System.out.println(col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (virtualizedMap.getSpaceLayout()[i][j].getExplorationStatus() == true) {
                    System.out.print("X");
                } else {
                    System.out.print("O");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Counts total number of tiles marked as Explored
     * @return
     */
    public int countExploredTiles() {
        int row = virtualizedMap.getSpaceLayout().length;
        int col = virtualizedMap.getSpaceLayout()[0].length;
        int count = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (virtualizedMap.getSpaceLayout()[i][j].getExplorationStatus()) {
                    count++;
                }
            }
        }
        return count;
    }
}
