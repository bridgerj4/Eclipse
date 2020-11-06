package tones;


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Tone implements Runnable {

    // Mary had a little lamb

    private final AudioFormat af;    
    private final String myName;
    private final Thread t;
    private boolean myTurn = false;
    private NoteLength noteLength;
    private Note note;
    private List<BellNote> song;

    Tone(AudioFormat af, Note n, String name) {
        this.af = af;
        this.myName = name;
        note = n;
        t = new Thread(this);
        t.start();
        
    }
    
    public void stopTone() {
        t.interrupt();
    }
    
    public void killTone() {
	     try {
	         t.join();
//	         System.out.println("Player "+myName + " is dead.");
	     } catch (InterruptedException e) {
//	         System.err.println("Interrupted while trying to kill Player "+myName);
	     }
	 }
    
    public void giveTurn(NoteLength l) { // usually, the main thread runs this to set private data myTurn for this player thread
   	 
        synchronized (this) {
            if (myTurn) {
                throw new IllegalStateException("Attempt to give a turn to a player who's hasn't completed the current turn");
            }
            System.out.println("(giveTurn:) "+Thread.currentThread()+" is setting Player " + myName + " to take a turn");
            song = Stream.of(
                    new BellNote(note, l)
            		).collect(Collectors.toList());
            myTurn = true;            	
            // I have set this players's myTurn so now tell it to go (or eventually, go)
            
            notify();  
            if (myTurn) { // if player thread is not done yet, 
            			  // this thread (probably main) should wait
                try {
                    System.out.println("(giveTurn:) Now "+Thread.currentThread()+ " is waiting.");              	
                    wait();
                } catch (InterruptedException exc) {
                	System.out.println("(giveTurn:) Interrupted while waiting for "+myName+" to finish turn.");
                }
                //eventually will be notified and can finish and return
            }
        }
    }

    public void run() {
        synchronized (this) {
        	while (true) { // go until interrrupted
        		try {
                    // Wait for my turn to begin   
        			while (!myTurn) {
//                        System.out.println("(run:) "+Thread.currentThread()+" for player "+ myName + " is waiting.");              	
                        wait();
                    }
                    // My turn!
                    doTurn();

                    // Done, finished turn and now wake up one waiting thread
                    myTurn = false;
                    notify();
                } catch (InterruptedException exc) {
                	System.out.println("(run:) Interrupted "+myName);
                	break;
                } catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
    
    private void doTurn() throws LineUnavailableException {
        System.out.println("(doTurn:) Player[" + myName);
        this.playSong();
    }
    
	
	
    
    void playSong() throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();

            for (BellNote bn: song) {
                playNote(line, bn);
                System.out.println(bn.note+" "+bn.length);
            }
            line.drain();
        }
    }

    private void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int actualLength = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, actualLength);
        line.write(Note.REST.sample(), 0, 50);
        
    }
    
    public static void main(String[] args) throws Exception {
    	Dictionary<String,Tone> myDictionary = new Hashtable(); // put(), elements(), get(), isEmpty(), keys() 
    	LinkedList<LinkedList> songs = new LinkedList();
    	final int numNotes = Note.values().length;
    	final int numNoteLengths= NoteLength.values().length;
    	final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
    	
    	
//    	System.out.println("Opening and reading from file");
    	
    	String songList ="";
    	
    	boolean go = true;
		while(go) {
			
			Scanner scan = new Scanner(System.in);
			System.out.println("Create a song list by entering a number followed by a comma (ex 1 or 1,2,3)");
			System.out.println("1 for Mary Had a Little Lamb");
			System.out.println("2 for My Song");
			System.out.println("3 for Sample");
			System.out.println("q to quit without playing anything");
			System.out.println("Enter list here: ");
			String input = scan.next();
			
			if(input.contains("1")){
				songList += "MaryHadALittleLamb.txt,";
				go = false;
			}
			if(input.contains("2")){
				songList += "MySong.txt,";
				go = false;
			}
			if(input.contains("3")){
				songList += "Sample.txt,";
				go= false;
			}
			if(input.toLowerCase().contains("q")) {
				songList ="";
				go = false;
			}
			if(go) {
				System.out.println("You have not entered a valid song list or 0");
			}
		}
	
		String[] songNameArray = songList.split(",");
		if(songNameArray.length !=0) {
	    	for(Note name : Note.values()) {
	    		String toneName = name.toString();
//	    		System.out.println(toneName);
				Tone t = new Tone(af, name, toneName);
//				System.out.println("Adding "+toneName);
				myDictionary.put(toneName, t);
//				System.out.println("Added "+toneName);
			} 
		}
		
    	for(int i=0;i<songNameArray.length;i++) {
    		File myfile = new File(songNameArray[i]);
        	
        	Scanner fileScan;
        	Scanner lineScanner;
        	String newSongCharacter ="-";
        	try 
        	{
        		fileScan = new Scanner(myfile);
        		int iteration =0;
        		String line = "";
        		while(fileScan.hasNextLine())
        		{
        			if(iteration==0) {
        				line = fileScan.nextLine();
        				iteration=1;
        			};
//            			System.out.println(newSongCharacter+"  "+line.equals(newSongCharacter));
        			if(line.equals(newSongCharacter)) {			
        				
        				LinkedList<String> temp = new LinkedList();
        				line =fileScan.nextLine();
        				temp.add(line);
        				line = fileScan.nextLine();
//            				System.out.println("Adding "+line+" "+(!line.equals(newSongCharacter)));
        				while((!line.equals(newSongCharacter)) & fileScan.hasNextLine() ) {
        					temp.add(line);	
        					line = fileScan.nextLine();
        				}
        			songs.add(temp);	
        			}
        		}
        	}catch (FileNotFoundException e) 
        	{
        		System.out.println("There is no File");
        	}
    	}
    	
        	
		
		for(int i=0; i<songs.size();i++) {
			System.out.println("Now Playing: "+songs.get(i).getFirst());
			System.out.println();
			System.out.println();
			for(int j=1; j<songs.get(i).size();j++) {
				
				String[] values = ((String)songs.get(i).get(j)).split(",");
				try {
//					System.out.println("about to give turn to "+values[1]);
					myDictionary.get(values[0]).giveTurn(NoteLength.valueOf(values[1]));
				}catch(NullPointerException  | IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
					int lineNum=2;
					for(int x=1; x<=i;x++) {
						
						lineNum = lineNum+songs.get(i).size()+1;
					}
					lineNum = lineNum+j;
					System.out.println("Note Doesn't Exist: Line "+lineNum);
					System.out.println();
					System.out.println();
					
				}
				
			}
		}
		System.out.println("Threads are getting stopped");
		for(Note name : Note.values()) {
    		String toneName = name.toString();
    		System.out.println(toneName+"Is getting stopped");
//			System.out.println("Adding "+toneName);
			myDictionary.get(toneName).stopTone();;
//			System.out.println("Added "+toneName);
		}
		System.out.println("Threads are stopped");
		System.out.println();
		System.out.println("Threads are getting killed");
		for(Note name : Note.values()) {
    		String toneName = name.toString();
//    		System.out.println(toneName+"Is getting killed");
//			System.out.println("Adding "+toneName);
			myDictionary.get(toneName).killTone();;
//			System.out.println("Added "+toneName);
		}
		System.out.println("Threads are killed");
    }
}
//		String s = "WHOLE";
//    	myDictionary.get("A5").giveTurn(NoteLength.valueOf(s));
//    	myDictionary.get("A4").giveTurn(NoteLength.WHOLE);
//    	myDictionary.get("A5").giveTurn(NoteLength.WHOLE);
//    	myDictionary.get("A5").stopTone();
//    	myDictionary.get("A5").killTone();
		
   
    


class BellNote {
    final Note note;
    final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}

enum NoteLength {
    WHOLE(1.0f),
    HALF(0.5f),
    QUARTER(0.25f),
    EIGHTH(0.125f);

    private final int timeMs;

    private NoteLength(float length) {
        timeMs = (int)(length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    public int timeMs() {
        return timeMs;
    }
}

enum Note {
    // REST Must be the first 'Note'
    REST,
    A4,
    A4S,
    B4,
    C4,
    C4S,
    D4,
    D4S,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5;

    public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
    public static final int MEASURE_LENGTH_SEC = 1;

    // Circumference of a circle divided by # of samples
    private static final double step_alpha = (2.0 * Math.PI) / SAMPLE_RATE;

    private final double FREQUENCY_A_HZ = 440.0;
    private final double MAX_VOLUME = 127.0;

    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    private Note() {
        int n = this.ordinal();
        if (n > 0) {
            // Calculate the frequency!
            final double halfStepUpFromA = n - 1;
            final double exp = halfStepUpFromA / 12.0;
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0, exp);

            // Create sinusoidal data sample for the desired frequency
            final double sinStep = freq * step_alpha;
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte)(Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    public byte[] sample() {
        return sinSample;
    }
}