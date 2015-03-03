//import javax.sound.midi.MidiChannel;
//import javax.sound.midi.MidiSystem;
//import javax.sound.midi.Synthesizer;
import javax.sound.midi.*;

//import java.util.HashMap;
//import java.util.Map;

public class MusicApp {
    
    public static MidiChannel[] channels;
    
    public static int columnHeight = 12;
    public static int rowLength = 12;
    
    //Declare arrays for instrument grids
    public static boolean[][] percussionGrid1;
    public static boolean[][] percussionGrid2;
    public static boolean[][] pianoGrid;
    public static boolean[][] guitarGrid;
    
    

    public static void main( String[] args ) throws MidiUnavailableException, InvalidMidiDataException {
        //Instantiate necessary music creating objects and variables
        int velocityTom = 100;                     // between 0 and 127
        int velocity = 75;                     // between 0 and 127
        int duration = 14000;                   // in milliseconds
        
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            Soundbank sbank = synth.getDefaultSoundbank();
            synth.loadAllInstruments(sbank);
            Instrument inst[] = synth.getLoadedInstruments();       //Acquire an array of all instruments in this sound bank
            
            /*Instrument Setup*/
            Instrument pianoInst = inst[0];         //Piano 1
            Instrument guitarInst = inst[27];      //Clean Gt is 27, 192 is Orchestra!
            Instrument tomDrumInst = inst[117];     //Tom Drum
            boolean isSetupSuccess = false;
            isSetupSuccess = checkInstruments(pianoInst, guitarInst, tomDrumInst);
            
            if (!isSetupSuccess) {
                System.out.println("Instrument setup failed!"); //This represents an instrument setup failure
                System.exit(0);
            }
            else {
                Patch pianoPatch = pianoInst.getPatch();                //Retrieves sound bank and program number
                Patch guitarPatch = guitarInst.getPatch();
                Patch tomDrumPatch = tomDrumInst.getPatch();
                
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
                /*One Channel for Tom Drum*/
                channels[8].programChange(tomDrumPatch.getBank(), tomDrumPatch.getProgram());
                
                
                /*Create Generic Players for Piano and Guitar*/
                GenericPlayer piano = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{0,1,2,3});
                GenericPlayer guitar = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{4,5,6,7});
                GenericPlayer hihat = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{9});      //Many percussion instruments on channel[9] automatically
                GenericPlayer tomDrum = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{8});
                
                /*Initialize GridReader*/
                Grids gridStates;
                Progression[] progs;
                
                /*Main Program Loop*/
              while (true) {
                  gridStates = GridReader.getCurrentState();
                  percussionGrid1 = gridStates.getGrid("Drum1");
                  percussionGrid2 = gridStates.getGrid("Drum2");
                  pianoGrid = gridStates.getGrid("Piano");
                  guitarGrid = gridStates.getGrid("Guitar");

                
                    String key = determineMajorKey();
                    progs = getProgressions(key, duration);
                    
                    piano.play(progs[0], velocity);
                    guitar.play(progs[1], velocity);
                    hihat.play(progs[2], velocity);
                    tomDrum.play(progs[3], velocityTom);
                    Thread.sleep(duration);
              }
                /*End of Main Program Loop*/
  
                //synth.close();
            }
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    

    public static void printInstruments(Instrument inst[]) {
        for (int i = 0; i < inst.length; i++) {
            System.out.println(i + " " + inst[i].getName());
        }
    }

    public static boolean checkInstruments(Instrument inst1, Instrument inst2, Instrument inst3) {
        return inst1 != null && inst2 != null && inst3 != null;
    }
    
    public static String determineMajorKey() {
        //First column of piano grid will determine Major scale of our grids
        String key = null;
        int counter = 0;
        for (int i = 0; i < columnHeight; i++) {
            if (pianoGrid[0][i]) {          //If this cell contains a true, increment our counter
                counter++;
            }
        }
        if (counter == 0 || counter == 4 || counter == 8 || counter == 12) {
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
    
    public static Progression[] getProgressions(String key, int duration) {
        
        Progression[] band = new Progression[4];
        Progression[] tunedProgressions = new Progression[2];
        tunedProgressions = getTunedProgressions(pianoGrid, guitarGrid, key, duration);
        band[0] = tunedProgressions[0];                             //Piano
        band[1] = tunedProgressions[1];                             //Guitar
        band[2] = getHiHatProgression(percussionGrid1, duration);   //Hi Hat
        band[3] = getTomProgression(percussionGrid2,duration);      //Tom Drum
        
        return band;
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
      
    public static String mapCellsToNoteLength(int cellCount) {
        String noteType = null;
        
        switch (cellCount) {
        case 0:
            noteType = "chord";
            break;
        case 1:
            noteType = "quarter";
            break;
        case 2:
            noteType = "half";
            break;
        case 3:
            noteType = "chord";
            break;
        case 4:
            noteType = "quarter";
            break;
        case 5:
            noteType = "half";
            break;
        case 6:
            noteType = "chord";
            break;
        case 7:
            noteType = "quarter";
            break;
        case 8:
            noteType = "half";
            break;
        case 9:
            noteType = "chord";
            break;
        case 10:
            noteType = "quarter";
            break;
        case 11:
            noteType = "half";
            break;
        case 12:
            noteType = "chord";
            break;
        default:
            noteType = "quarter";
            break;
        }
        
        return noteType;
    }
    
    public static Progression[] getTunedProgressions(boolean[][] grid1, boolean[][] grid2, String key, int duration) {
        
        int[] quarterNotes = {0,1,2,3};
        int[] halfNotes = {0,2};
        
        /*Make Progression*/
        Progression[] tunedProg = new Progression[2];
        tunedProg[0] =  new Progression(MidiMaps.tunedMap(), duration);     //Piano
        tunedProg[1] =  new Progression(MidiMaps.tunedMap(), duration);     //Guitar
        
        int numTrueCellsInColumn = 0;
        int numTrueCellsInRow = 0;
        String chord = null;
        String noteLengthPiano;
        String noteLengthGuitar;
        
        /*Parse Piano Grid*/
        for (int j = 0; j < columnHeight; j++) {
            for (int i = 0; i < rowLength; i++) {
                if (grid1[i][j]) {
                    numTrueCellsInColumn++;                         //Count number of true cells in column
                }
                if (grid1[j][i]) {
                    numTrueCellsInRow++;                            //Count number of true cells in row. Used to determine chord/half/quarter notes
                }
            }
            chord = mapCellsToNote(numTrueCellsInColumn, key);        //This returns the specific chord as a String within a specified key.
            noteLengthPiano = mapCellsToNoteLength(numTrueCellsInRow);    //Picks a note length used in the progression

            // if (numTrueCellsInColumn == 0) {
            //     noteLengthPiano = "rest";                           //We want a rest here
            // }
            
            switch (noteLengthPiano) {
            case "chord":
                tunedProg[0].add(key, chord);
                break;
            case "quarter":
                tunedProg[0].add(key, chord, quarterNotes);
                break;
            case "half":
                tunedProg[0].add(key, chord, halfNotes);
                break;
            // case "rest":
            //     tunedProg[0].add();
            //     break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
            numTrueCellsInRow = 0;
        }
        
        /*Parse Guitar Grid*/
        int numTruePianoCells = 0;                                  //Used to set chords for guitar
        for (int j = 0; j < columnHeight; j++) {
            for (int i = 0; i < rowLength; i++) {
                if (grid2[i][j]) {
                    numTrueCellsInColumn++;                         //Count number of true cells in column
                }
                if (grid1[i][j]) {
                    numTruePianoCells++;                            //Count number of true cells in column
                }
            }
            noteLengthGuitar = mapCellsToNoteLength(numTrueCellsInColumn);    //Picks a note length used in the progression
            chord = mapCellsToNote(numTruePianoCells, key);                   //Pick same chord as piano

            // if (numTrueCellsInColumn == 0) {
            //     noteLengthGuitar = "rest";                           //We want a rest here
            // }
            
            switch (noteLengthGuitar) {
            case "chord":
                tunedProg[1].add(key, chord);
                break;
            case "quarter":
                tunedProg[1].add(key, chord, quarterNotes);
                break;
            case "half":
                tunedProg[1].add(key, chord, halfNotes);
                break;
            // case "rest":
            //     tunedProg[1].add();
            //     break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
            numTrueCellsInRow = 0;
            numTruePianoCells = 0;
        }
        
        return tunedProg;
    }
    
    public static Progression getHiHatProgression(boolean[][] grid, int duration) {
        Progression hiHat = new Progression(MidiMaps.percussionMap(), duration);
        
        int[] quarterNotes = {0,1,2,3};
        int[] halfNotes = {0,2};
        int numTrueCellsInColumn = 0;
        int numTrueCellsInRow = 0;
        String noteLengthHiHat;
        String rhythm;
        
        for (int j = 0; j < columnHeight; j++) {
            for (int i = 0; i < rowLength; i++) {
                if (grid[i][j]) {
                    numTrueCellsInColumn++;                         //Count number of true cells in column
                }
                if (grid[j][i]) {
                    numTrueCellsInRow++;                            //Count number of true cells in row. Used to determine chord/half/quarter notes
                }
            }
            noteLengthHiHat = mapCellsToNoteLength(numTrueCellsInRow);    //Picks a note length used in the progression
            rhythm = mapCellsToPercussion(numTrueCellsInColumn);

            // if (numTrueCellsInColumn == 0) {
            //     noteLengthHiHat = "rest";
            // }
            
            switch (noteLengthHiHat) {
            case "chord":
                hiHat.add("HiHat", rhythm);
                break;
            case "quarter":
                hiHat.add("HiHat", rhythm, quarterNotes);
                break;
            case "half":
                hiHat.add("HiHat", rhythm, halfNotes);
                break;
            // case "rest":
            //     hiHat.add();
            //     break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
            numTrueCellsInRow = 0;
        }
        return hiHat;
    }
    
    public static String mapCellsToPercussion(int cellCount) {
        String rhythm;
        
        switch (cellCount) {
        case 0:
            rhythm = "CCCC";
            break;
        case 1:
            rhythm = "OCOC";
            break;
        case 2:
            rhythm = "COCO";
            break;
        case 3:
            rhythm = "CCOO";
            break;
        case 4:
            rhythm = "OOCC";
            break;
        case 5:
            rhythm = "OOOO";
            break;
        case 6:
            rhythm = "CCCC";
            break;
        case 7:
            rhythm = "OCOC";
            break;
        case 8:
            rhythm = "COCO";
            break;
        case 9:
            rhythm = "CCOO";
            break;
        case 10:
            rhythm = "OOCC";
            break;
        case 11:
            rhythm = "OOOO";
            break;
        case 12:
            rhythm = "CCCC";
            break;
        default:
            rhythm = "COCO";
            break;
        }
        
        return rhythm;
    }
      
    public static Progression getTomProgression(boolean[][] grid, int duration) {
        Progression tom = new Progression(MidiMaps.percussionMap(), duration);
        
        int[] quarterNotes = {0,1,2,3};
        int[] halfNotes = {0,2};
        int numTrueCellsInColumn = 0;
        int numTrueCellsInRow = 0;
        String noteLengthTom;
        String rhythm;
        
        for (int j = 0; j < columnHeight; j++) {
            for (int i = 0; i < rowLength; i++) {
                if (grid[i][j]) {
                    numTrueCellsInColumn++;                         //Count number of true cells in column
                }
                if (grid[j][i]) {
                    numTrueCellsInRow++;                            //Count number of true cells in row. Used to determine chord/half/quarter notes
                }
            }
            noteLengthTom = mapCellsToNoteLength(numTrueCellsInRow);      //Picks a note length used in the progression
            rhythm = mapCellsToPercussion(numTrueCellsInColumn);

            // if (numTrueCellsInColumn == 0) {
            //     noteLengthTom = "rest";
            // }
            
            switch (noteLengthTom) {
            case "chord":
                tom.add("TomDrum", rhythm);
                break;
            case "quarter":
                tom.add("TomDrum", rhythm, quarterNotes);
                break;
            case "half":
                tom.add("TomDrum", rhythm, halfNotes);
                break;
            // case "rest":
            //     tom.add();
            //     break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
            numTrueCellsInRow = 0;
        }
        return tom;
    }
}
