import java.io.IOException;
import java.net.URL;
import javax.sound.midi.*;

public class MidiEngine {
    private static final int MAX_SEQUENCE = 256;
    private static final int END_OF_TRACK_MESSAGE = 47;

    private static Sequence[] sequences = new Sequence[MAX_SEQUENCE];
    private static Sequencer sequencer;
    private static int counter = 0;
    private static int playSequenceNo = -1;
    private static long startTick = 0;

    public static void load(URL url) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        if (sequencer == null) {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(new MyMetaEventListener());
        }
        sequences[counter] = MidiSystem.getSequence(url);
        counter++;
    }

    public static void load(String filename) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        URL url = MidiEngine.class.getResource(filename);
        load(url);
    }

    public static void play(int no) {
        if (sequences[no] == null) {
            return;
        }
        if (playSequenceNo == no) {
            return;
        }

        // stop current midi sequence
        stop();

        try {
            sequencer.setSequence(sequences[no]);
            playSequenceNo = no;
            startTick = sequencer.getMicrosecondPosition();
            sequencer.start();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
    }

    // for loop
    private static class MyMetaEventListener implements MetaEventListener {
        public void meta(MetaMessage meta) {
            if (meta.getType() == END_OF_TRACK_MESSAGE) {
                if (sequencer != null && sequencer.isOpen()) {
                    sequencer.setMicrosecondPosition(startTick);
                    sequencer.start();
                }
            }
        }
    }
}
