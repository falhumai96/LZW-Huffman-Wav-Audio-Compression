
import java.util.List;

public interface CodingProcessor {

    public List<Byte> getSamples();

    public void clearSamples();

    public void addSample(byte sample);

    public void addSamples(byte[] samples);

    public void addSamples(List<Byte> samples);

    public void setSamples(byte[] samples);

    public void setSamples(List<Byte> samples);

    public byte[] compress();

    public List<Byte> decompress(byte[] compressionResultsAsBytes);
}
