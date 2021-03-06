import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GridReader {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      Object obj = JSONValue.parse(jsonText);
      return (JSONObject)obj;
    } finally {
      is.close();
    }
  }

  public static Grids getCurrentState() {
    try {
      JSONObject data = readJsonFromUrl("http://localhost:8080/grids.json");
      //JSONObject data = readJsonFromUrl("http://192.168.2.1:8080/grids.json");
      return new Grids(data);
    } catch (IOException e) {
      return new Grids(new JSONObject());
    }
  }

  public static Management getMgmt() {
    try {
      JSONObject data = readJsonFromUrl("http://localhost:8080/management.json");
      //JSONObject data = readJsonFromUrl("http://192.168.2.1:8080/mgmt.json");
      return new Management(data);
    } catch (IOException e) {
      return new Management(new JSONObject());
    }
  }
}
