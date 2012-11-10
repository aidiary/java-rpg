import java.io.*;
import java.util.*;
import javax.sound.midi.*;

public class MidiEngine implements MetaEventListener {
    private static final int END_OF_TRACK = 47;

    private Sequencer sequencer;
    private Synthesizer synthesizer;

    // BGM name -> MIDI sequence
    private HashMap<String, Sequence> midiMap;

    private int maxSequences;
    private int counter = 0;
    String currentSequenceName = "";

    public MidiEngine() {
        this(256);
    }

    public MidiEngine(int maxSequences) {
        this.maxSequences = maxSequences;
        midiMap = new HashMap<String, Sequence>(maxSequences);
        initSequencer();
    }

    private void initSequencer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(this);
            if (!(sequencer instanceof Synthesizer)) {  // after J2SE1.5
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                Receiver synthReceiver = synthesizer.getReceiver();
                Transmitter seqTransmitter = sequencer.getTransmitter();
                seqTransmitter.setReceiver(synthReceiver);
            } else { // before J2SE 1.4.2
                synthesizer = (Synthesizer) sequencer;
            }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void load(String name, String filename) {
        if (counter == maxSequences) {
            System.out.println("ERROR: cannot load a sequence any more.");
            return;
        }

        try {
            Sequence seq = MidiSystem.getSequence(
                    getClass().getResource(filename));
            midiMap.put(name, seq);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(String name) {
        if (currentSequenceName.equals(name)) {
            return;
        }
        stop();
        Sequence seq = (Sequence)midiMap.get(name);
        if (sequencer != null && seq != null) {
            try {
                sequencer.setSequence(seq);
                sequencer.start();
                currentSequenceName = name;
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
    }

    public void close() {
        stop();
        sequencer.removeMetaEventListener(this);
        sequencer.close();
        sequencer = null;
    }

    public void meta(MetaMessage meta) {
        if (meta.getType() == END_OF_TRACK) {
            if (sequencer != null && sequencer.isOpen()) {
                sequencer.setMicrosecondPosition(0);
                sequencer.start();
            }
        }
    }
}
