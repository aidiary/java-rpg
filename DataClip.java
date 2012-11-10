import java.io.*;
import javax.sound.sampled.*;

public class DataClip {
    public byte[] data;
    public int index;
    public AudioFormat format;
    public boolean running = false;
    public int sampleRate;

    public DataClip(byte[] data, AudioFormat format) {
        this.data = data;
        this.index = 0;
        this.format = format;
    }

    public DataClip(AudioInputStream audioStream) throws IOException {
        index = 0;
        format = audioStream.getFormat();
        int length = (int)(audioStream.getFrameLength() * format.getFrameSize());
        data = new byte[length];
        DataInputStream is = new DataInputStream(audioStream);
        is.readFully(data);
    }

    public void calculateSampleRate(int milliseconds) {
        sampleRate = (int)((milliseconds * (format.getChannels() * format.getSampleRate() * format.getSampleSizeInBits() / 8)) / 1000);
    }
}
