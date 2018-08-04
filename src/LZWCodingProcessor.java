
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class LZWCodingProcessor implements CodingProcessor {

    private final List<Byte> rawSamples;

    public LZWCodingProcessor() {
        rawSamples = new ArrayList<>();
    }

    @Override
    public List<Byte> getSamples() {
        return rawSamples;
    }

    @Override
    public void clearSamples() {
        rawSamples.clear();
    }

    @Override
    public void addSample(byte sample) {
        rawSamples.add(sample);
    }

    @Override
    public void addSamples(byte[] samples) {
        for (byte sample : samples) {
            this.rawSamples.add(sample);
        }
    }

    @Override
    public void addSamples(List<Byte> samples) {
        samples.forEach((sample) -> {
            this.rawSamples.add(sample);
        });
    }

    @Override
    public void setSamples(byte[] samples) {
        clearSamples();
        addSamples(samples);
    }

    @Override
    public void setSamples(List<Byte> samples) {
        clearSamples();
        addSamples(samples);
    }

    public Map<String, Integer> getInitialDictionary() {
        Map<String, Integer> toReturn = new ConcurrentHashMap<>();
        for (int i = 0; i < 256; i++) {
            toReturn.put(Integer.toString(i),  i);
        }
        return toReturn;
    }
    
    BitArray constructDictionaryData(Map<Integer, String> dictionary) {
        BitArray toReturn = new BitArray();

        BitArray dictSize = new BitArray();
        String dictSizeAsBits = Integer.toBinaryString(dictionary.size());
        for (int i = 0; i < dictSizeAsBits.length(); i += 8) {
            int j;
            int bitsSet = 0;
            for (j = i; j < dictSizeAsBits.length() && j < 8; j++) {
                dictSize.add(dictSizeAsBits.charAt(j) == '1');
                bitsSet++;
            }
            // Append trailing 0's.
            while (bitsSet != 8) {
                dictSize.add(false);
                bitsSet++;
            }
            // Add check if more bytes to read.
            if (j < dictSizeAsBits.length() - 1) {
                dictSize.add(true);
            } else {
                dictSize.add(false);
            }
        }
        
        return toReturn;
    }


    @Override
    public byte[] compress() {
        Map<String, Integer> codeWordsDictionary = getInitialDictionary();

        String p = "";
        BitArray finalOutput = new BitArray();
        for (int i = 0; i < rawSamples.size(); i++) {
            String c = Integer.toUnsignedString(rawSamples.get(i) & 0xFF);
            String currentCodeword;
            if (!p.equals("")) {
                currentCodeword = p + "|" + c;
            } else {
                currentCodeword = c;
            }
            if (codeWordsDictionary.containsKey(currentCodeword)) {
                p = currentCodeword;
            } else {
                finalOutput.add(BitArray.intToBitArray(codeWordsDictionary.get(p)));
                codeWordsDictionary.put(currentCodeword, (int) codeWordsDictionary.size());
                p = c;
            }
        }
        if (!rawSamples.isEmpty()) {
            finalOutput.add(BitArray.intToBitArray(codeWordsDictionary.get(p)));
        }
        return finalOutput.toByteArray();
    }
    
    private void output(String cW) {
        String[] parts = cW.split("\\|");
        for (String part : parts) {
            addSample((byte) Integer.parseInt(part));
        }
    }

    @Override
    public List<Byte> decompress(byte[] compressionResultsAsBytes) {
        if (compressionResultsAsBytes.length == 0) {
            clearSamples();
            return getSamples();
        }
        Map<Integer, String> reversedCodeWordsDict = getInitialDictionary().entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        
        BitArray data = new BitArray(compressionResultsAsBytes);
        if (data.size() == 0) {
            return null;
        }
        clearSamples();
        try {
            Pair<Integer, Integer> cW = BitArray.bitArrayToInt(data, 0);
            output(reversedCodeWordsDict.get(cW.getKey()));
            String pW = reversedCodeWordsDict.get(cW.getKey());
            while (cW.getValue() < data.size()) {
                cW = BitArray.bitArrayToInt(data, cW.getValue());
                String entry;
                if (reversedCodeWordsDict.containsKey(cW.getKey())) {
                    entry = reversedCodeWordsDict.get(cW.getKey());
                } else {
                    entry = pW + "|" + pW.split("\\|")[0];
                }
                output(entry);
                reversedCodeWordsDict.put(reversedCodeWordsDict.size(), pW + "|" + entry.split("\\|")[0]);
                pW = entry;
            }
        } catch (Exception e) {
        }
        
        
        return getSamples();
    }

}
