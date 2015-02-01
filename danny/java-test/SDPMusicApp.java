import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;

public class SDPMusicApp {

  public static MidiChannel[] channels;

	public static void main( String[] args ) {

		int volume = 80; // between 0 et 127
		int duration = 200; // in milliseconds

		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();

			// --------------------------------------
			// Play a few notes.
			// The two arguments to the noteOn() method are:
			// "MIDI note number" (pitch of the note),
			// and "velocity" (i.e., volume, or intensity).
			// Each of these arguments is between 0 and 127.
      playShit(60, duration, volume); // c note
      playShit(62, duration, volume); // D note
      playShit(64, duration, volume); // E note

      Thread.sleep( 500 );

      // --------------------------------------
      // Play a C major chord.
      playShitChord(new int[]{60, 64, 67}, 3000, volume);
      Thread.sleep( 500 );

			synth.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

  // Play on two different channels for now
  public static void playShit(int note, int duration, int volume) throws InterruptedException {
    channels[0].noteOn( note, volume );
    channels[1].noteOn( note + 2, volume );
    Thread.sleep( duration );
    channels[0].noteOff( note );
    channels[1].noteOff( note + 2);
  }

  public static void playShitChord(int[] notes, int duration, int volume) throws InterruptedException {
    for (int note : notes) {
      channels[0].noteOn( note, volume );
      channels[1].noteOn( note + 2, volume );
    }
    Thread.sleep( duration );
    for (int note : notes) {
      channels[0].noteOff( note );
      channels[1].noteOff( note + 2);
    }
  }

}

