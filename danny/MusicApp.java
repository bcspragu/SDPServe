import javax.sound.midi.*;

public class MusicApp {
    
    public static MidiChannel[] channels;
    
    public static int columnHeight = 12;
    public static int rowLength = 12;
    
    //Declare arrays for instrument grids
    public static boolean[][] grid1;
    public static boolean[][] grid2;
    public static boolean[][] grid3;
    public static boolean[][] grid4;
    
    public static void main( String[] args ) throws MidiUnavailableException, InvalidMidiDataException {
        
        try {
            //Synth instantiation only required once
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            Soundbank sbank = synth.getDefaultSoundbank();
            synth.loadAllInstruments(sbank);
            Instrument inst[] = synth.getLoadedInstruments();       //Acquire an array of all instruments in this sound bank
            
            //printInstruments(inst);
            while (true) {
                Grids gridStates = GridReader.getCurrentState();
                grid1 = gridStates.getGrid("Red");          //Names may change
                grid2 = gridStates.getGrid("Orange");          //Names may change
                grid3 = gridStates.getGrid("Blue");          //Names may change
                grid4 = gridStates.getGrid("Green");         //Names may change

                Instrument[] gridInstruments = new Instrument[3];       //Only 3 instruments b/c percussion is built into channel 9
                Management mgmt = GridReader.getMgmt();                 //Acquire management info

                /*Acquire IDs for each instrument*/
                gridInstruments[0] = inst[mgmt.getID("Red")];
                gridInstruments[1] = inst[mgmt.getID("Orange")];
                gridInstruments[2] = inst[mgmt.getID("Blue")];
                /*Get global duration*/
                int duration = (int)((100 - mgmt.getDuration()) * 120 + 4000);


                //Setup three instruments according to data from Management console
                boolean isSetupSuccess = checkInstruments(gridInstruments[0], gridInstruments[1], gridInstruments[2]);
                if (!isSetupSuccess) {
                System.out.println("Instrument setup failed!"); //This represents an instrument setup failure
                System.exit(0);
                }
                else {
                    Patch gridPatch1 = gridInstruments[0].getPatch();                //Retrieves sound bank and program number
                    Patch gridPatch2 = gridInstruments[1].getPatch();
                    Patch gridPatch3 = gridInstruments[2].getPatch();

                    //Instantiate Generic Players
                    GenericPlayer gp1;
                    GenericPlayer gp2;
                    GenericPlayer gp3;
                    GenericPlayer gp4;

                    /*Determine Channels for Grid1. Need to know whether instrument is tuned from Management.*/
                    if (mgmt.getTuned("Red")) {
                        //Tuned requires 4 channels
                        channels[0].programChange(gridPatch1.getBank(), gridPatch1.getProgram());
                        channels[1].programChange(gridPatch1.getBank(), gridPatch1.getProgram());
                        channels[2].programChange(gridPatch1.getBank(), gridPatch1.getProgram());
                        channels[3].programChange(gridPatch1.getBank(), gridPatch1.getProgram());
                        gp1 = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{0,1,2,3});
                    } else {
                        //Untuned requires 1 channel
                        channels[0].programChange(gridPatch1.getBank(), gridPatch1.getProgram());
                        gp1 = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{0});
                    }
                    /*Determine Channels for Grid2. Need to know whether instrument is tuned from Management.*/
                    if (mgmt.getTuned("Orange")) {
                        //Tuned requires 4 channels
                        channels[4].programChange(gridPatch2.getBank(), gridPatch2.getProgram());
                        channels[5].programChange(gridPatch2.getBank(), gridPatch2.getProgram());
                        channels[6].programChange(gridPatch2.getBank(), gridPatch2.getProgram());
                        channels[7].programChange(gridPatch2.getBank(), gridPatch2.getProgram());
                        gp2 = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{4,5,6,7});
                    } else {
                        //Untuned requires 1 channel
                        channels[4].programChange(gridPatch2.getBank(), gridPatch2.getProgram());
                        gp2 = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{4});
                    }
                    /*Determine Channels for Grid3. Need to know whether instrument is tuned from Management.*/
                    if (mgmt.getTuned("Blue")) {
                        //Tuned requires 4 channels
                        channels[8].programChange(gridPatch3.getBank(), gridPatch3.getProgram());
                        channels[10].programChange(gridPatch3.getBank(), gridPatch3.getProgram());
                        channels[11].programChange(gridPatch3.getBank(), gridPatch3.getProgram());
                        channels[12].programChange(gridPatch3.getBank(), gridPatch3.getProgram());
                        gp3 = new GenericPlayer(MidiMaps.tunedMap(), channels, new int[]{8,10,11,12});
                    } else {
                        //Untuned requires 1 channel
                        channels[8].programChange(gridPatch3.getBank(), gridPatch3.getProgram());
                        gp3 = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{8});
                    }

                    //Many percussion instruments on channel[9] automatically
                    gp4 = new GenericPlayer(MidiMaps.percussionMap(), channels, new int[]{9});      
                    
                    Progression[] progs;
                    String key = determineMajorKey();
                    progs = getProgressions(key, duration);

                    /*Play each progression*/
                    gp1.play(progs[0], (int)((1.27)*mgmt.getVolume("Red")));
                    gp2.play(progs[1], (int)((1.27)*mgmt.getVolume("Orange")));
                    gp3.play(progs[2], (int)((1.27)*mgmt.getVolume("Blue")));
                    gp4.play(progs[3], 127);
                    Thread.sleep(duration);
                }
            }   
            /*End of Main Program Loop*/
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
            for (int j = 0; j < rowLength; j++) {
                if (grid1[i][j]) {
                    counter += i + j;
                }
            }
        }
        
        int keyModulus = counter % 4;   //4 Major Chords available
        switch (keyModulus) {
        case 0:
            key = "CMajor";
            break;
        case 1:
            key = "GMajor";
            break;
        case 2:
            key = "DMajor";
            break;
        case 3:
            key = "FMajor";
            break;
        default:
            break;
        }

        return key;
    }
    
    public static Progression[] getProgressions(String key, int duration) {
        
        Progression[] band = new Progression[4];
        Progression[] tunedProgressions = new Progression[2];
        tunedProgressions = getTunedProgressions(grid1, grid2, key, duration);
        band[0] = tunedProgressions[0];                             
        band[1] = tunedProgressions[1];                             
        band[2] = getHiHatProgression(grid3, duration);   
        band[3] = getTomProgression(grid4,duration);      
        
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
            else if (cellCount == 10 || cellCount == 12) {
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
            else if (cellCount == 10 || cellCount == 12) {
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
            else if (cellCount == 10 || cellCount == 12) {
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
            else if (cellCount == 10 || cellCount == 12) {
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
    
    public static Progression[] getTunedProgressions(boolean[][] tuned1, boolean[][] tuned2, String key, int duration) {
        
        int[] quarterNotes = {0,1,2,3};
        int[] halfNotes = {0,2};
        
        /*Make Progression*/
        Progression[] tunedProg = new Progression[2];
        tunedProg[0] =  new Progression(MidiMaps.tunedMap(), duration);     //Piano
        tunedProg[1] =  new Progression(MidiMaps.tunedMap(), duration);     //Guitar
        
        int numTrueCellsInColumn = 0;
        int firstCellIndex = -1;
        int lastCellIndex = -1;
        
        String chord = null;
        String noteLengthPiano;
        String noteLengthGuitar;
        
        /*Parse Piano Grid*/
        for (int i = 0; i < rowLength; i++) {
            firstCellIndex = -1;
            lastCellIndex = -1;
            for (int j = 0; j < columnHeight; j++) {
                if (tuned1[i][j]) {
                    numTrueCellsInColumn++;                      //Count number of true cells in column. Used to determine duration
                    
                    if (firstCellIndex == -1) {                 //First cell we've seen
                        firstCellIndex = j;
                        lastCellIndex = j;
                    } else {                                    //A new last cell has been found. Document it
                        lastCellIndex = j;
                    }
                }
            }
            int distance = lastCellIndex - firstCellIndex;                      //Calculate distance
            chord = mapCellsToNote(distance, key);                              //This returns the specific chord as a String within a specified key.
            noteLengthPiano = mapCellsToNoteLength(numTrueCellsInColumn);       //Picks a note length used in the progression

            if (numTrueCellsInColumn == 0) {
                noteLengthPiano = "rest";                                       //We want a rest here
            }
            
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
            case "rest":
                tunedProg[0].add();
                break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
        }
        
        /*Parse Guitar Grid*/
        for (int i = 0; i < rowLength; i++) {
            firstCellIndex = -1;
            lastCellIndex = -1;
            for (int j = 0; j < columnHeight; j++) {
                if (tuned2[i][j]) {
                    numTrueCellsInColumn++;                         //Count number of true cells in column
                }
                if (tuned1[i][j]) {     
                        if (firstCellIndex == -1) {                 //First cell we've seen
                            firstCellIndex = j;
                            lastCellIndex = j;
                        } else {                                    //A new last cell has been found. Document it
                            lastCellIndex = j;
                        }
                }
            }
            int distance = lastCellIndex - firstCellIndex;                      //Calculate distance
            chord = mapCellsToNote(distance, key);                          //Pick same chord as piano
            noteLengthGuitar = mapCellsToNoteLength(numTrueCellsInColumn);    //Picks a note length used in the progression
            

            if (numTrueCellsInColumn == 0) {
                noteLengthGuitar = "rest";                           //We want a rest here
            }
            
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
            case "rest":
                tunedProg[1].add();
                break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
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
                    numTrueCellsInRow++;                         //Count number of true cells in row
                }
                if (grid[j][i]) {
                    numTrueCellsInColumn++;                            //Count number of true cells in column. Used to determine chord/half/quarter notes
                }
            }
            noteLengthHiHat = mapCellsToNoteLength(numTrueCellsInRow);    //Picks a note length used in the progression
            rhythm = mapCellsToPercussion(numTrueCellsInColumn);

            if (numTrueCellsInColumn == 0) {
                noteLengthHiHat = "rest";
            }
            
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
            case "rest":
                hiHat.add();
                break;
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
                if (grid[j][i]) {
                    numTrueCellsInColumn++;                         //Count number of true cells in column
                }
                if (grid[i][j]) {
                    numTrueCellsInRow++;                            //Count number of true cells in row. Used to determine chord/half/quarter notes
                }
            }
            noteLengthTom = mapCellsToNoteLength(numTrueCellsInRow);      //Picks a note length used in the progression
            rhythm = mapCellsToPercussion(numTrueCellsInColumn);

            if (numTrueCellsInColumn == 0) {
                noteLengthTom = "rest";
            }
            
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
            case "rest":
                tom.add();
                break;
            default:
                break;
            }
            numTrueCellsInColumn = 0;
            numTrueCellsInRow = 0;
        }
        return tom;
    }
}
