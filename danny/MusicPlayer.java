import java.util.Map;
import java.util.HashMap;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

public class MusicPlayer {
  public static void main(String args[]) throws Exception {
    try {
      Synthesizer  synth = MidiSystem.getSynthesizer();
      synth.open();
      MidiChannel[] channels = synth.getChannels();

      GenericPlayer piano = new GenericPlayer(channels, new int[]{0,1,2,3});
      GenericPlayer guitar = new GenericPlayer(channels, new int[]{4,5,6,7});

      piano.playNotesFromChord("CMajor", "C", 100, 3000);
      guitar.playChord("CMajor", "C", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "em", 100, 3000);
      guitar.playChord("CMajor", "em", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "dm", 100, 3000);
      guitar.playChord("CMajor", "dm", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "F", 100, 3000);
      guitar.playChord("CMajor", "F", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "C", 100, 3000);
      guitar.playChord("CMajor", "C", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "em", 100, 3000);
      guitar.playChord("CMajor", "em", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "dm", 100, 3000);
      guitar.playChord("CMajor", "dm", 100, 3000);
      Thread.sleep(3000);
      piano.playNotesFromChord("CMajor", "am", 100, 3000);
      guitar.playChord("CMajor", "am", 100, 3000);
      
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
