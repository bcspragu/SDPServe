import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Management {
    int duration;

    Map<String, int> idNums = new HashMap<String, int>();
    Map<String, int> volumes = new HashMap<String, int>();
    Map<String, int> isTuned = new HashMap<String, int>();

    public MgmtReader(JSONObject obj) {
        duration = obj.getInt("Duration");
        jsonArrayToMaps(obj.getJSONArray("Instruments"));
    }

    private static void jsonArrayToMaps(JSONArray mgmt) {
        JSONArray inst0 = (JSONArray)mgmt.get(0);       //Grid1
        JSONArray inst1 = (JSONArray)mgmt.get(1);       //Grid2
        JSONArray inst2 = (JSONArray)mgmt.get(2);       //Grid3
        JSONArray inst3 = (JSONArray)mgmt.get(3);       //Grid4

        /*ID Numbers for Instruments*/
        idNums.put("grid1", inst0.getInt("id"));
        idNums.put("grid2", inst1.getInt("id"));
        idNums.put("grid3", inst2.getInt("id"));
        /*Volumes for Instruments*/
        volumes.put("grid1", inst0.getInt("velocity"));
        volumes.put("grid2", inst1.getInt("velocity"));
        volumes.put("grid3", inst2.getInt("velocity"));
        volumes.put("grid4", inst3.getInt("velocity"));
        /*Tuning Info for Instruments*/
        isTuned.put("grid1", inst0.getInt("tuned"));
        isTuned.put("grid2", inst1.getInt("tuned"));
        isTuned.put("grid3", inst2.getInt("tuned"));
    }

    public int getID(String grid) {
        return idNums.get(grid);
    }

    public int getVolume(String grid) {
        return volumes.get(grid);
    }

    public int getTuned(String grid) {
        return isTuned.get(grid);
    }

    public int getDuration() {
        return duration;
    }
}
