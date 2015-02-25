//import javax.sound.midi.MidiChannel;
//import javax.sound.midi.MidiSystem;
//import javax.sound.midi.Synthesizer;
import javax.sound.midi.*;

public class MusicApp {
	
	public static MidiChannel[] channels;
	
	public static int columnHeight = 12;
	public static int rowLength = 12;
	
	//Declare arrays for instrument grids
	public static boolean[][] percussion1 = new boolean[rowLength][columnHeight];
	public static boolean[][] percussion2 = new boolean[rowLength][columnHeight];
	public static boolean[][] piano = new boolean[rowLength][columnHeight];
	public static boolean[][] guitar = new boolean[rowLength][columnHeight];
	
	

	public static void main( String[] args ) throws MidiUnavailableException, InvalidMidiDataException {
		
		//Instantiate necessary music creating objects and variables
		int velocity = 100; 					// between 0 and 127
		int duration = 1000; 				// in milliseconds
		
		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
			Soundbank sbank = synth.getDefaultSoundbank();
			synth.loadAllInstruments(sbank);
			
			Instrument inst[] = synth.getLoadedInstruments();		//Acquire an array of all instruments in this sound bank
			
			/*Instrument Setup*/
			Instrument pianoInst = inst[0];			//Piano 1
			Instrument guitarInst = inst[28];		//Clean Gt.
//			Instrument bongo = null;			//Haven't decided on instrument yet
//			Instrument cymbal = null;			//Haven't decided on instrument yet
			boolean isSetupSuccess = false;
			isSetupSuccess = setInstruments(pianoInst, guitarInst);
			
			if (!isSetupSuccess) {
				System.out.println("Instrument setup failed!");	//This represents an instrument setup failure
				System.exit(0);
			}
			else {
				Patch pianoPatch = pianoInst.getPatch();				//Retrieves sound bank and program number
				Patch guitarPatch = guitarInst.getPatch();
				
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
//				}
				/*End of Main Program Loop*/

				
				/*Test Block*/
				GenericPlayer p1 = new GenericPlayer(channels, new int[]{0,1,2,3});
			    GenericPlayer g1 = new GenericPlayer(channels, new int[]{4,5,6,7});
			    
			    System.out.println("About to play a chord");
			    
			    p1.playChord("CMajor", "C", velocity, duration);
			    //g1.playChord("CMajor", "em", velocity, duration);
			    
			    System.out.println("Played the chords");
				
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
	
	public static String determineMajorKey() {
		//First column of piano grid will determine Major scale of our grids
		String key = null;
		int counter = 0;
		for (int i = 0; i < columnHeight; i++) {
			if (piano[0][i]) {			//If this cell contains a true, increment our counter
				counter++;
			}
		}
		
		if (counter == 0 || counter == 4 || counter == 8) {
			key = "CMajor";
		}
		else if (counter == 1 || counter == 5 || counter == 9) {
			key = "GMajor";
		} 
		else if (counter == 2 || counter == 6 || counter == 10) {
			key = "DMajor";
		}
		else if (counter == 3 || counter == 7 || counter == 11) {
			key = "FMajor";
		}
		return key;
	}
		
	
}


