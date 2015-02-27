import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Progression {
  private Map<String, Map<String, int[]>> chords;
  private long duration;
  private List<NoteGroup> prog = new ArrayList<NoteGroup>();
  private int len = 0;

  public Progression (int duration) {
    this.duration = (long)duration;
  }

  public Progression (Map<String, Map<String, int[]>> chords, int duration) {
    this.chords = chords;
    this.duration = (long)duration;
  }

  // Adding a chord
  public void add(int[] notes) {
    prog.add(new NoteGroup(notes, 1));
    len++;
  }
  
  // Adding a chord
  public void add(String scale, String chord) {
    if (chords == null) {
      // You never specified chords, so we're just going to return because
      // exceptions are gross to handle
      return;
    }
    int[] notes = chords.get(scale).get(chord);
    prog.add(new NoteGroup(notes, 1));
    len++;
  }

  // Adding notes of a chord to be played in succession
  public void add(int[] notes, int[] offsets) {
    for (int offset : offsets) {
      prog.add(new NoteGroup(notes[offset], offsets.length));
    }
    len++;
  }

  public void add(String scale, String chord, int[] offsets) {
    if (chords == null) {
      // You never specified chords, so we're just going to return because
      // exceptions are gross to handle
      return;
    }
    int[] notes = chords.get(scale).get(chord);
    for (int offset : offsets) {
      prog.add(new NoteGroup(notes[offset], offsets.length));
    }
    len++;
  }

  public List<NoteGroup> getProg() {
    return prog;
  }

  public long noteLength() {
    return duration/len;
  }
}
