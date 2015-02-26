import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;


public class GenericPlayer {
  private MidiChannel[] channels;
  public Map<String, Map<String, int[]>> chords;
	public int[] useableChannels;

  private class NotePlayer extends Thread {
    private int[] notes;
    private int velocity;
    private long duration;
    private Progression progression;
    private boolean isProg;

    public NotePlayer(int[] notes, int velocity, int duration) {
      super("ThreadName" + notes);
      this.notes = notes;
      this.velocity = velocity;
      this.duration = (long) duration;
      this.isProg = false;
    }
    
    public NotePlayer(Progression prog) {
      super("ThreadName" + prog);
      this.progression = prog;
      this.isProg = true;
    }

    @Override
    public void run() {
      try {
        if (isProg) {
          playProg();
        } else {
          playChord();
        }
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
    
    private void playProg() throws InterruptedException {
      for (NoteGroup nGroup : progression.getProg()) {
        int index = 0;
        for (int note : nGroup.getNotes()) {
          channels[useableChannels[index]].noteOn(note, velocity);
          index++;
        }

        long hold = progression.noteLength()/nGroup.getDivider();
        Thread.sleep(hold);

        index = 0;
        for (int note : nGroup.getNotes()) {
          channels[useableChannels[index]].noteOff(note);
        }
      }
    }

  }

  public GenericPlayer(Map<String, Map<String, int[]>> chords, MidiChannel[] channels, int[] useableChannels) {
    this.chords = chords;
    this.channels = channels;
    this.useableChannels = useableChannels;
    
    
  }


  public void play(String scale, String chord, int velocity, int duration) throws Exception{
    int[] notes = chords.get(scale).get(chord);
    new NotePlayer(notes, velocity, duration).start();
  }

  public void play(Progression progression, int velocity) throws Exception{
    new NotePlayer(progression).start();
  }

  public void playNotesFromChord(String scale, String chord, int velocity, int duration) {
    int[] notes = chords.get(scale).get(chord);
    Progression prog = new Progression(duration);
    int[] offset = new int[notes.length];
    for (int i = 0; i < notes.length; i++) {
      offset[i] = i;
    }
    prog.add(notes, offset);
    new NotePlayer(prog).start();
  }

}
