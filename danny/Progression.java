import java.util.ArrayList;
import java.util.List;

public class Progression {
  private long duration;
  private List<NoteGroup> prog = new ArrayList<NoteGroup>();

  public Progression (int duration) {
    this.duration = (long)duration;
  }

  // Adding a chord
  public void add(int[] notes) {
    prog.add(new NoteGroup(notes, 1));
  }

  // Adding notes of a chord to be played in succession
  public void add(int[] notes, int[] offsets) {
    for (int offset : offsets) {
      prog.add(new NoteGroup(notes[offset], offsets.length));
    }
  }

  public List<NoteGroup> getProg() {
    return prog;
  }

  public long noteLength() {
    return duration/prog.size();
  }
}
