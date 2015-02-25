//import javax.sound.midi.MidiChannel;
//import javax.sound.midi.MidiSystem;
//import javax.sound.midi.Synthesizer;
import javax.sound.midi.*;

public class MusicApp {
	
	public static MidiChannel[] channels;
	
	//Declare arrays for instrument grids
	int[][] percussion = new int[12][12];
	int[][] piano = new int[12][12];
	int[][] instrument1 = new int[12][12];
	int[][] instrument2 = new int[12][12];

	public static void main( String[] args ) throws MidiUnavailableException, InvalidMidiDataException {
		
		//Instantiate necessary music creating objects and variables
		int volume = 100; 					// between 0 and 127
		int duration = 1000; 				// in milliseconds
		
		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
			Soundbank sbank = synth.getDefaultSoundbank();
			synth.loadAllInstruments(sbank);
			
			Instrument inst[] = synth.getLoadedInstruments();		//Acquire an array of all instruments in this sound bank
			
			/*Instrument Setup*/
			Instrument piano = inst[0];			//Piano 1
			Instrument guitar = inst[28];		//Clean Gt.
//			Instrument bongo = null;			//Haven't decided on instrument yet
//			Instrument cymbal = null;			//Haven't decided on instrument yet
			boolean isSetupSuccess = false;
			isSetupSuccess = setInstruments(piano, guitar);
			
			if (!isSetupSuccess) {
				System.out.println("Instrument setup failed!");	//This represents an instrument setup failure
				System.exit(0);
			}
			else {
				Patch pianoPatch = piano.getPatch();				//Retrieves sound bank and program number
				Patch guitarPatch = guitar.getPatch();
				
				/*Four Channels for Piano Notes*/
				channels[0].programChange(pianoPatch.getBank(), pianoPatch.getProgram());
				channels[1].programChange(pianoPatch.getBank(), pianoPatch.getProgram());
				channels[2].programChange(pianoPatch.getBank(), pianoPatch.getProgram());
				channels[3].programChange(pianoPatch.getBank(), pianoPatch.getProgram());
				/*Four Channels for Guitar Notes*/
				channels[4].programChange(guitarPatch.getBank(), guitarPatch.getProgram());
				channels[5].programChange(guitarPatch.getBank(), guitarPatch.getProgram());
				channels[6].programChange(guitarPatch.getBank(), guitarPatch.getProgram());
				channels[7].programChange(guitarPatch.getBank(), guitarPatch.getProgram());
				//Percussion is on channel[9] automatically
				
				/*Main Program Loop*/
//				while (true) {
//					//updateGrids();			//We want latest grid implementation. Do this with Brandon
//					
//					//Object method for interpreting Grid1
//					//Object method for interpreting Grid2
//					//Object method for interpreting Grid3
//					//Object method for interpreting Grid4
//					
//					//Verify all tracks equal length. Important because game is periodic sweep of the grids.
//					
//					//Prepare 12 Note Sequences. Methods will contain noteOn events, but no sleeps or note offs. All channels will have noteOff events applied by whatever the universal kill method is
//				}
				/*End of Main Program Loop*/
//				
				/*Test Block*/
				playNotes(new int[]{48,60,64,67}, volume, duration, channels);
				/*End of Test Block*/
				
				synth.close();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean updateGrids() {
		//fill in later, for loops to update each value
		return true;
	}

	public static void printInstruments(Instrument inst[]) {
		for (int i = 0; i < inst.length; i++) {
			System.out.println(inst[i].getName());
		}
	}

	public static boolean setInstruments(Instrument inst1, Instrument inst2) {
		return inst1 != null && inst2 != null;
	}

	public static void playNotes(int notes[], int velocity, int duration, MidiChannel[] chans) throws InterruptedException {
			/*Experiment method when testing different channels. Remove in final copy*/
		
			//Piano
//			chans[0].noteOn( notes[0], velocity );
//			chans[1].noteOn( notes[1], velocity );
//			chans[2].noteOn( notes[2], velocity );
//			chans[3].noteOn( notes[3], velocity );
//			Thread.sleep( duration );
			//Guitar
//			chans[4].noteOn( 50, velocity );
//			chans[5].noteOn( 57, velocity );
//			chans[6].noteOn( 62, velocity );
//			chans[7].noteOn( 65, velocity );
//			Thread.sleep( duration );
			//Drums
			chans[9].noteOn(70, velocity);
			chans[9].noteOff(70, velocity);
			chans[9].noteOn(43, velocity);
			chans[9].noteOff(43, velocity);
			Thread.sleep(duration);
//			channels[0].noteOff( notes[0] );
//			channels[1].noteOff( notes[1] );
//			channels[2].noteOff( notes[2] );
//			channels[3].noteOff( notes[3] );
	}
	
}


