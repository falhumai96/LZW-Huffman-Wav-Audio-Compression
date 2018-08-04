
public class AudioInfo {

    /*
    
    WAV File header (Toal = 44 bytes):
    
    [Format: (start_at_idx, type)]
    - 'RIFF': RIFF file identification (0 - int) 
    - <length>: length field (4 - int)
    -'WAVE': WAVE chunk identification (8 - int) 
    - 'fmt': format sub-chunk identification  (12 - int)
    - flength: length of format sub-chunk (16 - int)
    - format: format specifier (20 - short) 
    - chans: number of channels (22 - short)
    - sampsRate: sample rate in Hz (24 - int)
    - bpsec: bytes per second (28 - int)
    - bpsample: bytes per sample (32 - short) 
    - bpchan: bits per channel (34 - short)
    - 'data': data sub-chunk identification  (36 - int)
    - dlength: length of data sub-chunk (40 - int)
    
     */
    private int[][] samplesContainer;
    protected double biggestSample;
    private final byte[] byteArray;
    private int sampleMax = 0;
    private int sampleMin = 0;
    private byte[] dataByteArray;

    public AudioInfo(byte[] byteArray) {
        this.byteArray = byteArray.clone();
        createSampleArrayCollection();
    }

    public int getNumberOfChannels() {
        return getShort(byteArray[23], byteArray[22]);
    }

    public double getBiggestSample() {
        return biggestSample;
    }

    private short getBytesPerFrame() {
        return getShort(byteArray[33], byteArray[32]);
    }

    // I.e. data length / number of bytes per sample frame.
    private int getLength() {
        int dataLength = getInt(byteArray[43], byteArray[42], byteArray[41], byteArray[40]);
        return dataLength / getBytesPerFrame();
    }

    private void createSampleArrayCollection() {
        try {
            int length = getLength();
            int bytesPerfFrame = getBytesPerFrame();

            // Create the data bytes.
            dataByteArray = new byte[length * bytesPerfFrame];
            

            int j = 0;
            for (int i = 44; i < byteArray.length; i++) {
                dataByteArray[j] = byteArray[i];
                j++;
            }

            // Collect the samples.
            samplesContainer = getSampleArray(dataByteArray);

            biggestSample = sampleMax > sampleMin ? sampleMax : Math.abs(((double) sampleMin));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public byte[] getDataByteArray() {
        return dataByteArray;
    }

    protected int[][] getSampleArray(byte[] eightBitByteArray) {
        int[][] toReturn = new int[getNumberOfChannels()][eightBitByteArray.length / (2 * getNumberOfChannels())];
        int i = 0;

        // Collect all samples from the loaded file.
        for (int t = 0; t < eightBitByteArray.length;) {
            boolean acceptValue = true;
            int low = (int) eightBitByteArray[t++];
            int high = (int) eightBitByteArray[t++];
            if (((high << 16) + (low & 0x00ff)) < sampleMin && acceptValue) {
                sampleMin = ((high << 16) + (low & 0x00ff));
            } else if (((high << 16) + (low & 0x00ff)) > sampleMax && acceptValue) {
                sampleMax = ((high << 16) + (low & 0x00ff));
            }
            toReturn[0][i] = ((high << 16) + (low & 0x00ff));
            i++;
        }

        return toReturn;
    }

    public double getXScaleFactor(int panelWidth) {
        return (panelWidth / ((double) samplesContainer[0].length));
    }

    public double getYScaleFactor(int panelHeight) {
        return (panelHeight / (biggestSample * 2 * 1.2));
    }

    public int[] getAudio(int channel) {
        return samplesContainer[channel];
    }

    protected int getIncrement(double xScale) {
        if ((samplesContainer[0].length * xScale) == 0) {
            return -1;
        }

        return (int) (samplesContainer[0].length / (samplesContainer[0].length * xScale));
    }

    // b4: Start byte.
    private int getInt(byte b1, byte b2, byte b3, byte b4) {
        return ((0xFF & b1) << 24) | ((0xFF & b2) << 16)
                | ((0xFF & b3) << 8) | (0xFF & b4);
    }

    // b2: Start byte.
    private short getShort(byte b1, byte b2) {
        return (short) ((b1 << 8) + b2);
    }
}
