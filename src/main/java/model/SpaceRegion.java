package model;

import java.util.ArrayList;

/**
 * Created by Admin on 2/17/2020.
 */
public class SpaceRegion implements java.io.Serializable {

    private int width;
    private int length;
    private StarField[][] spaceLayout;

    public SpaceRegion (int width, int length) {
        this.width = width;
        this.length = length;
    }

    public int getWidth() {
        return this.width;
    }

    public int getLength() {
        return this.length;
    }

    //We generate barrier zones.... SO a location with a dimension of say 3x3
    /*
        OOO
        OOO
        OOO

        actually generates as

        XXXXX
        XOOOX
        XOOOX
        XOOOX
        XXXXX

        so 3x3 becomes 5x5

        likewise an area that is 6x3

        OOOOOO
        OOOOOO
        OOOOOO

        becomes...

        XXXXXXXX
        XOOOOOOX
        XOOOOOOX
        XOOOOOOX
        XXXXXXXX

        8x5!


     */
    public void generateSpaceRegion(ArrayList<Drone> allDrones) {

        //Account for model.Barrier padding
        int modifiedWidth = width + 2;
        int modifiedLength = length + 2;

        StarField[][] tempLayout = new StarField[modifiedLength][modifiedWidth];

        //Setup Region and Barriers
        for (int row = 0; row < modifiedLength; row++) {
            for (int col = 0; col < modifiedWidth; col++) {
                if (row == 0 || row == modifiedLength - 1) {
                    //All Barriers will be tagged with X/Y coords of -1, -1
                    StarField tempStarField = new StarField(-1,-1);
                    Obstacle barrier = new Barrier(-1, -1);
                    tempStarField.setOccupantObstacle(barrier);
                    tempLayout[row][col] = tempStarField;
                } else if (col == 0 || col == modifiedWidth - 1) {
                    StarField tempStarField = new StarField(-1,-1);
                    Obstacle barrier = new Barrier(-1, -1);
                    tempStarField.setOccupantObstacle(barrier);
                    tempLayout[row][col] = tempStarField;
                } else {
                    StarField tempStarField = new StarField(row - 1, col - 1);
                    tempLayout[row][col] = tempStarField;
                }
            }
        }

        //Setup Drones

        int colLimit = tempLayout[0].length - 2;
        int rowLimit = tempLayout.length - 2;

        for (Drone currDrone:allDrones) {
            int initX = currDrone.getDroneX();
            int initY = currDrone.getDroneY();

            //Offset drones by 1,1 in order to place properly
            tempLayout[rowLimit - initY][initX + 1].setOccupantDrone(currDrone);
            tempLayout[rowLimit - initY][initX + 1].updateStarFieldContents(Content.DRONE);
            tempLayout[rowLimit - initY][initX + 1].toggleExplorationStatus();
        }

        spaceLayout = tempLayout;

    }

    public void generateSpaceRegion(ArrayList<Drone> allDrones, String[] obstaclesList) {

        //Account for model.Barrier padding
        int modifiedWidth = width + 2;
        int modifiedLength = length + 2;

        StarField[][] tempLayout = new StarField[modifiedLength][modifiedWidth];

        //Setup Region and Barriers
        for (int row = 0; row < modifiedLength; row++) {
            for (int col = 0; col < modifiedWidth; col++) {
                if (row == 0 || row == modifiedLength - 1) {
                    //All Barriers will be tagged with X/Y coords of -1, -1
                    StarField tempStarField = new StarField(-1,-1);
                    Obstacle barrier = new Barrier(-1, -1);
                    tempStarField.setOccupantObstacle(barrier);
                    tempLayout[row][col] = tempStarField;
                } else if (col == 0 || col == modifiedWidth - 1) {
                    StarField tempStarField = new StarField(-1,-1);
                    Obstacle barrier = new Barrier(-1, -1);
                    tempStarField.setOccupantObstacle(barrier);
                    tempLayout[row][col] = tempStarField;
                } else {
                    StarField tempStarField = new StarField(row - 1, col - 1);
                    //No starfield is truly UNKNOWN in baseMap - Reflect that this is the case
                    tempStarField.updateStarFieldContents(Content.EMPTY);
                    tempLayout[row][col] = tempStarField;
                }
            }
        }

        //Setup Drones

        int colLimit = tempLayout[0].length - 2;
        int rowLimit = tempLayout.length - 2;

        for (Drone currDrone:allDrones) {
            int initX = currDrone.getDroneX();
            int initY = currDrone.getDroneY();

            //Offset drones by 1,1 in order to place properly
            tempLayout[rowLimit - initY][initX + 1].setOccupantDrone(currDrone);
            tempLayout[rowLimit - initY][initX + 1].updateStarFieldContents(Content.DRONE);
            tempLayout[rowLimit - initY][initX + 1].toggleExplorationStatus();
        }

        //Setup obstacles (If they exist)

        if (obstaclesList != null) {
            //We have Obstacles - must parse array
            for (String obstacleLocation:obstaclesList) {
                String[] coords = obstacleLocation.split(",");
                Obstacle sun = new Sun(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

                tempLayout[rowLimit - Integer.parseInt(coords[1])][Integer.parseInt(coords[0]) + 1].setOccupantObstacle(sun);
                //default to sun
                tempLayout[rowLimit - Integer.parseInt(coords[1])][Integer.parseInt(coords[0]) + 1].updateStarFieldContents(Content.SUN);
            }
        }

        spaceLayout = tempLayout;

    }

    public StarField[][] getSpaceLayout() {
        return this.spaceLayout;
    }

    public static void main(String[] args) {
        SpaceRegion test = new SpaceRegion(3,3);
        test.generateSpaceRegion(null);
    }
}
