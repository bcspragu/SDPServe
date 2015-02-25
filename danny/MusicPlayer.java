import java.util.Map;
import java.util.HashMap;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;

public class MusicPlayer {
  public static void main(String args[]) throws Exception {
    MidiChannel[] channels = MidiSystem.getSynthesizer().getChannels();
    GenericPlayer piano = new GenericPlayer(channels, new int[]{0,1,2,3});
    GenericPlayer guitar = new GenericPlayer(channels, new int[]{4,5,6,7});
  }
}

