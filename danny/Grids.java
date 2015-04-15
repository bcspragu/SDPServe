import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Grids {
  Map<String, boolean[][]> grids = new HashMap<String, boolean[][]>();

  public Grids (JSONObject obj) {
    Iterator iter = obj.entrySet().iterator();
    while(iter.hasNext()){
      Map.Entry entry = (Map.Entry)iter.next();
      JSONArray arr = ((JSONArray)(((JSONObject)entry.getValue()).get("Grid")));
      grids.put((String)entry.getKey(), jsonArrayToGrid(arr));
    }
  }

  public boolean[][] getGrid(String grid) {
    return grids.get(grid);
  }

  private boolean[][] jsonArrayToGrid(JSONArray grid) {
    boolean[][] result = new boolean[grid.size()][((JSONArray)grid.get(0)).size()];
    for (int i = 0; i < grid.size(); i++) {
      JSONArray row = (JSONArray)grid.get(i);
      for (int j  = 0; j < row.size(); j++) {
        result[i][j] = (boolean)row.get(j);
      }
    }
    return result;
  }
}
