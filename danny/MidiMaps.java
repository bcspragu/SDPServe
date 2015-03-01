import java.util.HashMap;
import java.util.Map;

public class MidiMaps {
  private static Map<String, Map<String, int[]>> tChords;
  private static Map<String, Map<String, int[]>> pChords;

  public static Map<String, Map<String, int[]>> tunedMap(){
    if (tChords == null) {
      tChords = new HashMap<String, Map<String, int[]>>();
      
      // Initialize our sub-hashes
      tChords.put("CMajor", new HashMap<String, int[]>());
      tChords.put("DMajor", new HashMap<String, int[]>());
      tChords.put("FMajor", new HashMap<String, int[]>());
      tChords.put("GMajor", new HashMap<String, int[]>());

      // tChords in C Major
      tChords.get("CMajor").put("C", new int[]{48,60,64,67});
      tChords.get("CMajor").put("dm", new int[]{50,57,62,65});
      tChords.get("CMajor").put("em", new int[]{52,59,64,67});
      tChords.get("CMajor").put("F", new int[]{53,57,60,65});
      tChords.get("CMajor").put("G", new int[]{55,59,62,67});
      tChords.get("CMajor").put("am", new int[]{45,57,60,64});
      tChords.get("CMajor").put("B", new int[]{47,59,62,65});

      // tChords in D Major
      tChords.get("DMajor").put("D", new int[]{50,57,62,66});
      tChords.get("DMajor").put("em", new int[]{52,59,64,67});
      tChords.get("DMajor").put("Fsharpm", new int[]{54,57,61,66});
      tChords.get("DMajor").put("G", new int[]{55,59,62,67});
      tChords.get("DMajor").put("A", new int[]{45,57,61,64});
      tChords.get("DMajor").put("bm", new int[]{47,59,62,66});
      tChords.get("DMajor").put("Csharp", new int[]{49,61,64,67});

      // tChords in F Major
      tChords.get("FMajor").put("F", new int[]{53,57,60,65});
      tChords.get("FMajor").put("gm", new int[]{55,58,62,67});
      tChords.get("FMajor").put("am", new int[]{45,57,60,64});
      tChords.get("FMajor").put("Bflat", new int[]{46,58,62,65});
      tChords.get("FMajor").put("C", new int[]{48,60,64,67});
      tChords.get("FMajor").put("dm", new int[]{50,57,62,65});
      tChords.get("FMajor").put("E", new int[]{52,58,64,67});

      // tChords in G Major
      tChords.get("GMajor").put("G", new int[]{55,59,62,67});
      tChords.get("GMajor").put("am", new int[]{45,57,60,64});
      tChords.get("GMajor").put("bm", new int[]{47,59,62,66});
      tChords.get("GMajor").put("C", new int[]{48,60,64,67});
      tChords.get("GMajor").put("D", new int[]{50,57,62,66});
      tChords.get("GMajor").put("em", new int[]{52,59,64,67});
      tChords.get("GMajor").put("Fsharp", new int[]{54,57,60,66});
    }

    return tChords;
  }
  

  
  public static Map<String, Map<String, int[]>> percussionMap(){
    if (pChords == null) {
      pChords = new HashMap<String, Map<String, int[]>>();
      
      // Initialize our sub-hashes
      pChords.put("HiHat", new HashMap<String, int[]>());
      pChords.put("TomDrum", new HashMap<String, int[]>());
      
      // HiHat Sounds
      pChords.get("HiHat").put("CCCC", new int[]{42,42,42,42});		//Closed x4
      pChords.get("HiHat").put("OCOC", new int[]{46,42,46,42});		//Open Closed x2
      pChords.get("HiHat").put("COCO", new int[]{42,46,42,46});		//Closed Open x2
      pChords.get("HiHat").put("CCOO", new int[]{42,42,46,46});		//Closed x2 Open x2
      pChords.get("HiHat").put("OOCC", new int[]{46,46,42,42});		//Open x2 Closed x2
      pChords.get("HiHat").put("OOOO", new int[]{46,46,46,46});		//Open x4
      
      // Tom Drum Sounds
      pChords.get("TomDrum").put("CCCC", new int[]{60,60,60,60});	
      pChords.get("TomDrum").put("OCOC", new int[]{50,50,50,50});  
      pChords.get("TomDrum").put("COCO", new int[]{70,70,70,70});
      pChords.get("TomDrum").put("CCOO", new int[]{40,60,60,40});	
      pChords.get("TomDrum").put("OOCC", new int[]{50,60,50,60});  
      pChords.get("TomDrum").put("OOOO", new int[]{70,40,70,40});
    }

    return pChords;
  }
}
