import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import javax.sound.midi.MidiChannel;

public class GenericPlayer {
  private MidiChannel[] channels;
  public Map<String, Map<String, int[]>> chords = new HashMap<String, Map<String, int[]>>();
	public int[] useableChannels;

  private class NotePlayer extends Thread {
    private int[] notes;
    private int velocity;
    private long duration;

    public NotePlayer(int[] notes, int velocity, int duration) {
      super("ThreadName" + notes);
      this.notes = notes;
      this.velocity = velocity;
      this.duration = (long) duration;
    }

    @Override
    public void run() {
      try {
        playChord();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private void playChord() throws InterruptedException {
      int index = 0;
      for (int channel : useableChannels) {
        channels[channel].noteOn(notes[index], velocity);
        index++;
      }
      Thread.sleep(duration);
      index = 0;
      for (int channel : useableChannels) {
        channels[channel].noteOff(notes[index]);
        index++;
      }
    }

  }

  public GenericPlayer(MidiChannel[] channels, int[] useableChannels) {
    this.channels = channels;
    this.useableChannels = useableChannels;
    //
    // Initialize our sub-hashes
    chords.put("CMajor", new HashMap<String, int[]>());
    chords.put("DMajor", new HashMap<String, int[]>());
    chords.put("FMajor", new HashMap<String, int[]>());
    chords.put("GMajor", new HashMap<String, int[]>());

    // Chords in C Major
    chords.get("CMajor").put("C", new int[]{48,60,64,67});
    chords.get("CMajor").put("dm", new int[]{50,57,62,65});
    chords.get("CMajor").put("em", new int[]{52,59,64,67});
    chords.get("CMajor").put("F", new int[]{53,57,60,65});
    chords.get("CMajor").put("G", new int[]{55,59,62,67});
    chords.get("CMajor").put("am", new int[]{45,57,60,64});
    chords.get("CMajor").put("B", new int[]{47,59,62,65});

    // Chords in D Major
    chords.get("DMajor").put("D", new int[]{50,57,62,66});
    chords.get("DMajor").put("em", new int[]{52,59,64,67});
    chords.get("DMajor").put("Fsharpm", new int[]{54,57,61,66});
    chords.get("DMajor").put("G", new int[]{55,59,62,67});
    chords.get("DMajor").put("A", new int[]{45,57,61,64});
    chords.get("DMajor").put("bm", new int[]{47,59,62,66});
    chords.get("DMajor").put("Csharp", new int[]{49,61,64,67});

    // Chords in F Major
    chords.get("FMajor").put("F", new int[]{53,57,60,65});
    chords.get("FMajor").put("gm", new int[]{55,58,62,67});
    chords.get("FMajor").put("am", new int[]{45,57,60,64});
    chords.get("FMajor").put("Bflat", new int[]{46,58,62,65});
    chords.get("FMajor").put("C", new int[]{48,60,64,67});
    chords.get("FMajor").put("dm", new int[]{50,57,62,65});
    chords.get("FMajor").put("E", new int[]{52,58,64,67});

    // Chords in G Major
    chords.get("GMajor").put("G", new int[]{55,59,62,67});
    chords.get("GMajor").put("am", new int[]{45,57,60,64});
    chords.get("GMajor").put("bm", new int[]{47,59,62,66});
    chords.get("GMajor").put("C", new int[]{48,60,64,67});
    chords.get("GMajor").put("D", new int[]{50,57,62,66});
    chords.get("GMajor").put("em", new int[]{52,59,64,67});
    chords.get("GMajor").put("Fsharp", new int[]{54,57,60,66});
  }


  public void playChord(String scale, String chord, int velocity, int duration) throws Exception{
    int[] notes = chords.get(scale).get(chord);
    new NotePlayer(notes, velocity, duration).start();
  }

}
