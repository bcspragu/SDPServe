//import javax.sound.midi.MidiChannel;
//import javax.sound.midi.MidiSystem;
//import javax.sound.midi.Synthesizer;
import javax.sound.midi.*;

import java.util.HashMap;
import java.util.Map;

public class MusicApp {
	
	public static MidiChannel[] channels;
	
	public static int columnHeight = 12;
	public static int rowLength = 12;
	
	//Declare arrays for instrument grids
	public static boolean[][] percussion1;
	public static boolean[][] percussion2;
	public static boolean[][] piano;
	public static boolean[][] guitar;
	
	

	public static void main( String[] args ) throws MidiUnavailableException, InvalidMidiDataException {
    Grids gridStates = GridReader.getCurrentState();

    percussion1 = gridStates.getGrid("Drum1");
    percussion2 = gridStates.getGrid("Drum2");
    piano = gridStates.getGrid("Piano");
    guitar = gridStates.getGrid("Guitar");
		
		//Instantiate necessary music creating objects and variables
		int velocity = 100; 					// between 0 and 127
		int duration = 3000; 				// in milliseconds
		
		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
			Soundbank sbank = synth.getDefaultSoundbank();
			synth.loadAllInstruments(sbank);
			
			Instrument inst[] = synth.getLoadedInstruments();		//Acquire an array of all instruments in this sound bank
			
			/*Instrument Setup*/
			Instrument pianoInst = inst[0];			//Piano 1
			Instrument guitarInst = inst[27];		//Clean Gt.
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
				
				/*Create Generic Players for Piano and Guitar*/
				GenericPlayer piano = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{0,1,2,3});
			    GenericPlayer guitar = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{4,5,6,7});
			    GenericPlayer hihat = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{9});
				
				/*Main Program Loop*/
//				while (true) {
//					//updateGrids();			//We want latest grid implementation. Do this with Brandon
//				}
				/*End of Main Program Loop*/

				
				/*Test Block*/
			    piano.playNotesFromChord("CMajor", "C", velocity, duration);
          guitar.play("CMajor", "C", velocity, duration);
          hihat.playNotesFromChord("HiHat", "COCO", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "em", velocity, duration);
          guitar.play("CMajor", "em", velocity, duration);
          hihat.playNotesFromChord("HiHat", "OCOC", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "dm", velocity, duration);
          guitar.play("CMajor", "dm", velocity, duration);
          hihat.playNotesFromChord("HiHat", "CCOO", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "F", velocity, duration);
          guitar.play("CMajor", "F", velocity, duration);
          hihat.playNotesFromChord("HiHat", "OOOO", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "C", velocity, duration);
          guitar.play("CMajor", "C", velocity, duration);
          hihat.playNotesFromChord("HiHat", "OOCC", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "em", velocity, duration);
          guitar.play("CMajor", "em", velocity, duration);
          hihat.playNotesFromChord("HiHat", "CCCC", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "dm", velocity, duration);
          guitar.play("CMajor", "dm", velocity, duration);
          hihat.playNotesFromChord("HiHat", "OCOC", velocity, duration);
          Thread.sleep(duration);
          piano.playNotesFromChord("CMajor", "am", velocity, duration);
          guitar.play("CMajor", "am", velocity, duration);
          hihat.playNotesFromChord("HiHat", "OCOC", velocity, duration);
				
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
	
	public static void getPianoNotes(String key) {
		//Subsequent columns determine chords within the major scale
		int numTrueCells = 0;
		for (int j = 1; j < columnHeight; j++) {
			for (int i = 0; i < rowLength; i++) {
				if (piano[i][j]) {
					numTrueCells++;
				}
			}
			//mapCellsToNote(numTrueCells, key);		//This returns the specific chord as a String within a specified key.
			//Encapsulate chord within a Note, then add it to a Song/Track
			numTrueCells = 0;
		}
		
		//return a track containing piano notes
	}
	
	public static void getGuitarNotes(String key) {
		//Key controlled by piano, only tempo and note duration varies on guitar. Guitar decomposes chords played on piano
		
	}
	
	public static String mapCellsToNote(int cellCount, String key) {
		String chord = null;
		
		switch (key) {
		case "CMajor":
			if (cellCount == 0 || cellCount == 3 || cellCount == 8) {
				chord = "C";
			}
			else if (cellCount == 1) {
				chord = "dm";
			}
			else if (cellCount == 2 || cellCount == 4) {
				chord = "em";
			}
			else if (cellCount == 5) {
				chord = "F";
			}
			else if (cellCount == 6 || cellCount == 11) {
				chord = "G";
			}
			else if (cellCount == 10) {
				chord = "am";
			}
			else if (cellCount == 7 || cellCount == 9) {
				chord = "B";
			}
			break;
		case "GMajor":
			if (cellCount == 0 || cellCount == 3 || cellCount == 8) {
				chord = "G";
			}
			else if (cellCount == 1) {
				chord = "am";
			}
			else if (cellCount == 2 || cellCount == 4) {
				chord = "bm";
			}
			else if (cellCount == 5) {
				chord = "C";
			}
			else if (cellCount == 6 || cellCount == 11) {
				chord = "D";
			}
			else if (cellCount == 10) {
				chord = "em";
			}
			else if (cellCount == 7 || cellCount == 9) {
				chord = "Fsharp";
			}
			break;
		case "DMajor":
			if (cellCount == 0 || cellCount == 3 || cellCount == 8) {
				chord = "D";
			}
			else if (cellCount == 1) {
				chord = "em";
			}
			else if (cellCount == 2 || cellCount == 4) {
				chord = "Fsharpm";
			}
			else if (cellCount == 5) {
				chord = "G";
			}
			else if (cellCount == 6 || cellCount == 11) {
				chord = "A";
			}
			else if (cellCount == 10) {
				chord = "bm";
			}
			else if (cellCount == 7 || cellCount == 9) {
				chord = "Csharp";
			}
			break;
		case "FMajor":
			if (cellCount == 0 || cellCount == 3 || cellCount == 8) {
				chord = "F";
			}
			else if (cellCount == 1) {
				chord = "gm";
			}
			else if (cellCount == 2 || cellCount == 4) {
				chord = "am";
			}
			else if (cellCount == 5) {
				chord = "Bflat";
			}
			else if (cellCount == 6 || cellCount == 11) {
				chord = "C";
			}
			else if (cellCount == 10) {
				chord = "dm";
			}
			else if (cellCount == 7 || cellCount == 9) {
				chord = "E";
			}
			break;
		default:
			break;
		}
		return chord;
	}
		
	
}


