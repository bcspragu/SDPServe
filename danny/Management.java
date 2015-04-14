import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Management {
    int duration;
    Map<String, Integer> idNums;
    Map<String, Integer> volumes;
    Map<String, Integer> isTuned;

    public Management(JSONObject obj) {
        duration = obj.getInt("Duration");
        idNums = new HashMap<String, Integer>();
        volumes = new HashMap<String, Integer>();
        isTuned = new HashMap<String, Integer>();

        jsonArrayToMaps((JSONArray)obj.get("Instruments"));
    }

    public static void jsonArrayToMaps(JSONArray mgmt) {
        JSONArray inst0 = (JSONArray)mgmt.get(0);       //Grid1
        JSONArray inst1 = (JSONArray)mgmt.get(1);       //Grid2
        JSONArray inst2 = (JSONArray)mgmt.get(2);       //Grid3
        JSONArray inst3 = (JSONArray)mgmt.get(3);       //Grid4

        /*ID Numbers for Instruments*/
        idNums.put("grid1", (int)inst0.get("id"));
        idNums.put("grid2", (int)inst1.get("id"));
        idNums.put("grid3", (int)inst2.get("id"));
        /*Volumes for Instruments*/
        volumes.put("grid1", (int)inst0.get("velocity"));
        volumes.put("grid2", (int)inst1.get("velocity"));
        volumes.put("grid3", (int)inst2.get("velocity"));
        volumes.put("grid4", (int)inst3.get("velocity"));
        /*Tuning Info for Instruments*/
        isTuned.put("grid1", (int)inst0.get("tuned"));
        isTuned.put("grid2", (int)inst1.get("tuned"));
        isTuned.put("grid3", (int)inst2.get("tuned"));
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
