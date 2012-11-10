import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class WaveEngine {
    private static final int MAX_CLIPS = 256;

    private static DataClip[] clips = new DataClip[MAX_CLIPS];
    private static SourceDataLine[] lines = new SourceDataLine[MAX_CLIPS];
    private static int counter = 0;
    private static long last;

    public static void load(URL url) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(url);
        AudioFormat format = ais.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, AudioSystem.NOT_SPECIFIED);

        DataClip clip = new DataClip(ais);

        // register sound clip
        clips[counter] = clip;
        lines[counter] = (SourceDataLine)AudioSystem.getLine(info);
        lines[counter].open(format);
        counter++;
    }

    public static void load(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        URL url = WaveEngine.class.getResource(filename);
        load(url);
    }

    public static void play(int no) {
        if (clips[no] == null) {
            return;
        }

        clips[no].index = 0;
        clips[no].running = true;

        lines[no].flush();
        lines[no].start();
    }

    public static void stop(int no) {
        if (clips[no] == null) {
            return;
        }
        clips[no].running = false;
        lines[no].stop();
    }

    public static void render() {
        long current = System.currentTimeMillis();
        int difference = (int)(current - last);

        for (int i=0; i<counter; i++) {
            if (!clips[i].running) {
                continue;
            }
            clips[i].calculateSampleRate(difference);
            int bytes = Math.min(clips[i].sampleRate, clips[i].data.length - clips[i].index);
            if (bytes > 0) {
                lines[i].write(clips[i].data, clips[i].index, bytes);
                clips[i].index += bytes;
            }
            if (clips[i].index >= clips[i].data.length) {
                stop(i);
            }
        }
        last = current;
    }
}
