import javax.sound.midi.MidiChannel;

public class Guitar {
	public static MidiChannel[] channels;		//Channels the guitar will play on
	
	public static void play_Chord(int[] notes, int velocity, int duration) throws InterruptedException {
			
		//Play 4 note chords
		channels[4].noteOn( notes[0], velocity );
		channels[5].noteOn( notes[1], velocity );
		channels[6].noteOn( notes[2], velocity );
		channels[7].noteOn( notes[3], velocity );
//		Thread.sleep( duration );
//		channels[4].noteOff( notes[0] );
//		channels[5].noteOff( notes[1] );
//		channels[6].noteOff( notes[2] );
//		channels[7].noteOff( notes[3] );
	}
	
	public void setupMidiChannels(MidiChannel[] chans) {
		channels = chans;
	}
	
	/*Chords in C Major*/
	public void play_CMajor_C(int velocity, int duration) throws InterruptedException {		//C C E G
		play_Chord(new int[]{48,60,64,67}, velocity, duration);
		
	}

	public void play_CMajor_dm(int velocity, int duration) throws InterruptedException {		//D A D F
		play_Chord(new int[]{50,57,62,65}, velocity, duration);
	}
	
	public void play_CMajor_em(int velocity, int duration) throws InterruptedException {		//E B E G
		play_Chord(new int[]{52,59,64,67}, velocity, duration);
	}
	
	public void play_CMajor_F(int velocity, int duration) throws InterruptedException {		//F A C F
		play_Chord(new int[]{53,57,60,65}, velocity, duration);
	}
	
	public void play_CMajor_G(int velocity, int duration) throws InterruptedException {		//G B D G
		play_Chord(new int[]{55,59,62,67}, velocity, duration);
	}
	
	public void play_CMajor_am(int velocity, int duration) throws InterruptedException {		//A A C E
		play_Chord(new int[]{45,57,60,64}, velocity, duration);
	}
	
	public void play_CMajor_B(int velocity, int duration) throws InterruptedException {		//B B D F
		play_Chord(new int[]{47,59,62,65}, velocity, duration);
	}
	
	/*Chords in G Major*/
	public void play_GMajor_G(int velocity, int duration) throws InterruptedException {		//G B D G
		play_Chord(new int[]{55,59,62,67}, velocity, duration);
	}
	
	public void play_GMajor_am(int velocity, int duration) throws InterruptedException {	//A A C E
		play_Chord(new int[]{45,57,60,64}, velocity, duration);
	}
	
	public void play_GMajor_bm(int velocity, int duration) throws InterruptedException {	//B B D F#
		play_Chord(new int[]{47,59,62,66}, velocity, duration);
	}
	
	public void play_GMajor_C(int velocity, int duration) throws InterruptedException {		//C C E G
		play_Chord(new int[]{48,60,64,67}, velocity, duration);
	}
	
	public void play_GMajor_D(int velocity, int duration) throws InterruptedException {		//D A D F#
		play_Chord(new int[]{50,57,62,66}, velocity, duration);
	}
	
	public void play_GMajor_em(int velocity, int duration) throws InterruptedException {	//E B E G
		play_Chord(new int[]{52,59,64,67}, velocity, duration);
	}
	
	public void play_GMajor_Fsharp(int velocity, int duration) throws InterruptedException {	//F# A C F#
		play_Chord(new int[]{54,57,60,66}, velocity, duration);
	}
	
	/*Chords in D Major*/
	public void play_DMajor_D(int velocity, int duration) throws InterruptedException {		//D A D F#
		play_Chord(new int[]{50,57,62,66}, velocity, duration);
	}
	
	public void play_DMajor_em(int velocity, int duration) throws InterruptedException {		//E B E G
		play_Chord(new int[]{52,59,64,67}, velocity, duration);
	}
	
	public void play_DMajor_Fsharpm(int velocity, int duration) throws InterruptedException {	//F# A C# F#
		play_Chord(new int[]{54,57,61,66}, velocity, duration);
	}
	
	public void play_DMajor_G(int velocity, int duration) throws InterruptedException {		//G B D G
		play_Chord(new int[]{55,59,62,67}, velocity, duration);
	}
	
	public void play_DMajor_A(int velocity, int duration) throws InterruptedException {		//A A C# E
		play_Chord(new int[]{45,57,61,64}, velocity, duration);
	}
	
	public void play_DMajor_bm(int velocity, int duration) throws InterruptedException {		//B B D F#
		play_Chord(new int[]{47,59,62,66}, velocity, duration);
	}
	
	public void play_DMajor_Csharp(int velocity, int duration) throws InterruptedException {	//C# C# E G
		play_Chord(new int[]{49,61,64,67}, velocity, duration);
	}
	
	/*Chords in F Major*/
	public void play_FMajor_F(int velocity, int duration) throws InterruptedException {		//F A C F
		play_Chord(new int[]{53,57,60,65}, velocity, duration);
	}
	
	public void play_FMajor_gm(int velocity, int duration) throws InterruptedException {		//G Bb D G
		play_Chord(new int[]{55,58,62,67}, velocity, duration);
	}
	
	public void play_FMajor_am(int velocity, int duration) throws InterruptedException {		//A A C E
		play_Chord(new int[]{45,57,60,64}, velocity, duration);
	}
	
	public void play_FMajor_Bflat(int velocity, int duration) throws InterruptedException {		//Bb Bb D F
		play_Chord(new int[]{46,58,62,65}, velocity, duration);
	}
	
	public void play_FMajor_C(int velocity, int duration) throws InterruptedException {		//C C E G
		play_Chord(new int[]{48,60,64,67}, velocity, duration);
	}
	
	public void play_FMajor_dm(int velocity, int duration) throws InterruptedException {		//D A D F
		play_Chord(new int[]{50,57,62,65}, velocity, duration);
	}
	
	public void play_FMajor_E(int velocity, int duration) throws InterruptedException {		//E Bb E G
		play_Chord(new int[]{52,58,64,67}, velocity, duration);
	}
}
