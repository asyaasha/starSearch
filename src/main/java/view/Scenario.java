package view;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private static final int TAKEN_CODE = 1;
    private static final int MAX_WIDTH = 20;
    private static final int MAX_HEIGHT = 15;
    private static final int MAX_DRONES = 10;
    private static final int MIN_SUNS = 0;
    private static final int MIN_DRONES = 1;
    private static final int MIN_SQ = 1;

    private Integer [][] trackLocations;

    private List<String> dronesList;
    private List<String> sunsList;
    private int numOfDrones;
    private int numOfSuns;
    private int width;
    private int height;
    private String turns;

    public Scenario() {
        dronesList = new ArrayList<>();
        sunsList = new ArrayList<>();
        trackLocations = new Integer[MAX_WIDTH][MAX_HEIGHT];
    }

    public Scenario(List dronesList, List sunsList, int numOfDrones, int numOfSuns,
                    int width, int height, String turns) {
        this.dronesList = dronesList;
        this.sunsList = sunsList;
        this.numOfDrones = numOfDrones;
        this.numOfSuns = numOfSuns;
        this.width = width;
        this.height = height;
        this.turns = turns;
    }

    // Getters
    public List getDronesList() {
        return this.dronesList;
    }
    public List getSunsList() {
        return this.sunsList;
    }
    public int getNumOfDrones() {
        return this.numOfDrones;
    }
    public int getNumOfSuns() {
        return this.numOfSuns;
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
    public String getTurns() {
        return this.turns;
    }
    int dronesMax;
    int sunsMax;

    // Setters
    public void setRegion(int width, int height) {
        int i, j;
        int regionSize = width * height;

        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                trackLocations[i][j] = 0;
            }
        }

        dronesMax = regionSize > MAX_DRONES ? MAX_DRONES : regionSize;
        sunsMax = regionSize / 2;

        this.width = width;
        this.height = height;
    }
    public void setDronesList(List<String> dronesList){
        this.dronesList = dronesList;
    }
    public void setNumOfSuns(int numOfSuns){
        this.numOfSuns = numOfSuns;
    }
    public void setNumOfDrones(int numOfDrones){
        this.numOfDrones = numOfDrones;
    }
    public void setSunsList(List<String> sunsList){
        this.sunsList = sunsList;
    }
    public void setTurns(String turns){
        this.turns = turns;
    }
    public void addToDronesList(String droneConfig){
        dronesList.add(droneConfig);
    }
    public void addToSunsList(String sunConfig){
        sunsList.add(sunConfig);
    }

    // HELPERS TO CHECK THAT INPUTS ARE VALID
    public Boolean checkRegion(int proposedWidth, int proposedHeight) {
       if (proposedWidth < MIN_SQ || proposedWidth > MAX_WIDTH || proposedHeight < MIN_SQ
               || proposedHeight > MAX_HEIGHT  ) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    // implement checks
    public Boolean checkDronesNum() {
        if (numOfDrones < MIN_DRONES || numOfDrones > dronesMax) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    public Boolean checkSuns() {
        if (numOfSuns < MIN_SUNS || numOfSuns > sunsMax) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    public Boolean checkTurns() {
        if (Integer.parseInt(turns) < 1 || Integer.parseInt(turns)  > 200) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}

