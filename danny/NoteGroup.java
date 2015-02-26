public class NoteGroup {
  private int[] notes;
  private int lengthDivider;

  // A one note NoteGroup
  public NoteGroup(int note, int lengthDivider) {
    this.notes = new int[]{note};
    this.lengthDivider = lengthDivider;
  }

  // A mutli-note NoteGroup
  public NoteGroup(int[] notes, int lengthDivider) {
    this.notes = notes;
    this.lengthDivider = lengthDivider;
  }

  public int[] getNotes() {
    return notes;
  }

  public int getDivider() {
    return lengthDivider;
  }
}
