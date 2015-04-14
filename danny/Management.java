import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Management {
    int duration;
    static Map<String, Integer> idNums;
    static Map<String, Integer> volumes;
    static Map<String, Boolean> isTuned;

    public Management(JSONObject obj) {
        duration = ((Number)(obj.get("Duration"))).intValue();
        idNums = new HashMap<String, Integer>();
        volumes = new HashMap<String, Integer>();
        isTuned = new HashMap<String, Boolean>();

        jsonArrayToMaps((JSONArray)obj.get("Instruments"));
    }

    public static void jsonArrayToMaps(JSONArray mgmt) {
        JSONObject inst0 = (JSONObject)(mgmt.get(0));       //Grid1
        JSONObject inst1 = (JSONObject)(mgmt.get(1));       //Grid2
        JSONObject inst2 = (JSONObject)(mgmt.get(2));       //Grid3

        /*ID Numbers for Instruments*/
        idNums.put("Red", ((Number)(inst0.get("ID"))).intValue());
        idNums.put("Orange", ((Number)(inst1.get("ID"))).intValue());
        idNums.put("Blue", ((Number)(inst2.get("ID"))).intValue());
        /*Volumes for Instruments*/
        volumes.put("Red", ((Number)(inst0.get("Velocity"))).intValue());
        volumes.put("Orange", ((Number)(inst1.get("Velocity"))).intValue());
        volumes.put("Blue", ((Number)(inst2.get("Velocity"))).intValue());
        /*Tuning Info for Instruments*/
        isTuned.put("Red", ((Boolean)(inst0.get("Tuned"))));
        isTuned.put("Orange", ((Boolean)(inst1.get("Tuned"))));
        isTuned.put("Blue", ((Boolean)(inst2.get("Tuned"))));
    }

    public int getID(String grid) {
        return idNums.get(grid);
    }

    public int getVolume(String grid) {
        return volumes.get(grid);
    }

    public Boolean getTuned(String grid) {
        return isTuned.get(grid);
    }

    public int getDuration() {
        return duration;
    }
}
