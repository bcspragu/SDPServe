import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Management {
    int duration;
    static Map<String, Integer> idNums;
    static Map<String, Integer> volumes;
    static Map<String, Integer> isTuned;

    public Management(JSONObject obj) {
        duration = (int)(obj.get("Duration"));
        idNums = new HashMap<String, Integer>();
        volumes = new HashMap<String, Integer>();
        isTuned = new HashMap<String, Integer>();

        jsonArrayToMaps((JSONObject)obj.get("Instruments"));
    }

    public static void jsonArrayToMaps(JSONObject mgmt) {
        JSONObject inst0 = (JSONObject)(mgmt.get(0));       //Grid1
        JSONObject inst1 = (JSONObject)(mgmt.get(1));       //Grid2
        JSONObject inst2 = (JSONObject)(mgmt.get(2));       //Grid3
        JSONObject inst3 = (JSONObject)(mgmt.get(3));       //Grid4

        /*ID Numbers for Instruments*/
        idNums.put("Red", (int)(inst0.get("ID")));
        idNums.put("Orange", (int)(inst1.get("ID")));
        idNums.put("Blue", (int)(inst2.get("ID")));
        /*Volumes for Instruments*/
        volumes.put("Red", (int)(inst0.get("Velocity")));
        volumes.put("Orange", (int)(inst1.get("Velocity")));
        volumes.put("Blue", (int)(inst2.get("Velocity")));
        volumes.put("Green", (int)(inst3.get("Velocity")));
        /*Tuning Info for Instruments*/
        isTuned.put("Red", (int)(inst0.get("Tuned")));
        isTuned.put("Orange", (int)(inst1.get("Tuned")));
        isTuned.put("Blue", (int)(inst2.get("Tuned")));
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
