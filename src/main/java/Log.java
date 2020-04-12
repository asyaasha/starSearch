import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dennis Eddington
 *
 * Logging object - handles tracking number of turns as well as reporting overall statistics (Space Region Size, Total
 * Safe Space, Explored Safe Space, as well as Turns Elapsed)
 */
public class Log implements java.io.Serializable {

    private ArrayList<String> fullLog = new ArrayList<>();
    private HashMap<String, String> summaryReport;
    private String spaceRegionSize;
    private String totalSafeSpace;
    private String exploredSafeSpaceRegion;
    private int turns;

    /**
     * Constructor for Log
     */
    public Log() {
        this.summaryReport = new HashMap<>();
    }

    /**
     * Method that simply increments the total number of turns executed within the Star Search Simulation
     */
    public void incrementTurn() {
        this.turns = this.turns + 1;
    }

    /**
     * Method called in order to aggregate data inside of Log class and then export it to console for further analysis
     *
     * @param virtualizedMap A copy of the SpaceRegion detailing the virtualizedMap (Map observable by Drones)
     * @param baseMap A copy of the SpaceRegion detailing the baseMap (Fully revealed Map)
     * @return Returns a HashMap<String, String> containing the data aggregated by the Log and it's inputs
     */
    public HashMap<String, String> exportSummaryReport(SpaceRegion virtualizedMap, SpaceRegion baseMap) {

        int totalSpaces = 0;
        int exploredSpace = 0;
        int validEmpty = 0;

        int row = baseMap.getSpaceLayout().length;
        int col = baseMap.getSpaceLayout()[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (virtualizedMap.getSpaceLayout()[i][j].getExplorationStatus() && baseMap.getSpaceLayout()[i][j].getStarFieldContents() != Content.BARRIER && baseMap.getSpaceLayout()[i][j].getStarFieldContents() != Content.SUN) {
                    //System.out.println(i + " " + j);
                    exploredSpace++;
                }
                if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() != Content.BARRIER) {
                    totalSpaces++;
                }
                if (baseMap.getSpaceLayout()[i][j].getStarFieldContents() != Content.BARRIER &&
                        baseMap.getSpaceLayout()[i][j].getStarFieldContents() != Content.SUN &&
                        baseMap.getSpaceLayout()[i][j].getStarFieldContents() != Content.STARS) {
                    validEmpty++;
                }
            }
        }

        this.summaryReport.put("SpaceRegionSize", Integer.toString(totalSpaces));
        this.summaryReport.put("TotalSafeSpace", Integer.toString(validEmpty));
        this.summaryReport.put("ExploredSafeSpace", Integer.toString(exploredSpace));
        this.summaryReport.put("TurnsElapsed", Integer.toString(this.turns));

        return this.summaryReport;
    }

    /**
     * Retrieves number of turns as captured by Logging
     *
     * @return Integer tracking the number of turns the Log has successfully recorded
     */
    public int getTurns() {
        return this.turns;
    }

    /**
     * Submits an entry to the LogRecord
     *
     * @param entry
     */
    public void submitLogRecord(String entry) {
        this.fullLog.add(entry);
    }

    /**
     * Recieves the full log
     *
     * @return
     */
    public ArrayList<String> getFullLog() {
        return this.fullLog;
    }
}
