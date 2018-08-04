
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class RLECodingProssesor implements CodingProcessor {

    private final List<Byte> rawSamples;

    public RLECodingProssesor() {
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

    BitArray constructDictionaryData(Map<Byte, String> dictionary) {
        BitArray toReturn = new BitArray();

        // TODO: Implement.
        return toReturn;
    }

    BitArray constructActualData(String actualData) {
        BitArray toReturn = new BitArray();

        // TODO: Implement.
        return toReturn;
    }

    private byte[] convertToBytes(Pair<String, Map<Byte, String>> compressionResults) {
        return constructDictionaryData(compressionResults.getValue()).add(constructActualData(compressionResults.getKey())).toByteArray();
    }

    private Pair<Integer, Map<Byte, String>> constructDictionary(BitArray compressionResultsBits) {
        Map<Byte, String> toReturnDict = new HashMap<>();
        int toReturnIdx = -1;

        // TODO: Implement.
        return new Pair<>(toReturnIdx, toReturnDict);
    }

    private String constructData(BitArray compressionResultsBits, int idx) {
        String toReturn = "";

        // TODO: Implement.
        return toReturn;
    }

    private Pair<String, Map<Byte, String>> convertToCompressionResults(byte[] compressionResults) {
        BitArray compressionResultsBits = new BitArray(compressionResults);

        Pair<Integer, Map<Byte, String>> dictionaryData = constructDictionary(compressionResultsBits);
        return new Pair<>(
                constructData(compressionResultsBits, dictionaryData.getKey()),
                dictionaryData.getValue()
        );
    }

    @Override
    public byte[] compress() {
        String toReturn = "";
        List<Byte> currentSamples = new ArrayList<>();
        Map<Byte, String> codewordsMap = new HashMap<>();
        int counter = 0;
        for (Byte sample : rawSamples) {
            if (currentSamples.isEmpty() || sample.equals(currentSamples.get(0))) {
                currentSamples.add(sample);
            } else {
                String currentCodeword;
                if (!codewordsMap.containsKey(currentSamples.get(0))) {
                    currentCodeword = Integer.toHexString(counter);
                    codewordsMap.put(currentSamples.get(0), currentCodeword);
                    counter++;
                } else {
                    currentCodeword = codewordsMap.get(currentSamples.get(0));
                }
                if (!"".equals(toReturn)) {
                    toReturn += ",";
                }
                if (currentSamples.size() > 1) {
                    toReturn += currentCodeword + ":" + Integer.toString(currentSamples.size());
                } else {
                    toReturn += currentCodeword;
                }
                currentSamples = new ArrayList<>();
                currentSamples.add(sample);
            }
        }
        if (currentSamples.size() > 1) {
            String currentCodeword;
            if (!codewordsMap.containsKey(currentSamples.get(0))) {
                currentCodeword = Integer.toHexString(counter);
                codewordsMap.put(currentSamples.get(0), currentCodeword);
                counter++;
            } else {
                currentCodeword = codewordsMap.get(currentSamples.get(0));
            }
            if (!"".equals(toReturn)) {
                toReturn += ",";
            }
            if (currentSamples.size() > 1) {
                toReturn += currentCodeword + ":" + Integer.toString(currentSamples.size());
            } else {
                toReturn += currentCodeword;
            }
        }
        return convertToBytes(new Pair<>(toReturn, codewordsMap));
    }

    @Override
    public List<Byte> decompress(byte[] compressionResultsAsBytes) {
        Pair<String, Map<Byte, String>> compressionResults = convertToCompressionResults(compressionResultsAsBytes);
        Map<String, Byte> reversedMap = compressionResults.getValue().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        String encodedData = compressionResults.getKey();
        clearSamples();
        List<String> collections = new ArrayList<>(Arrays.asList(encodedData.split(",")));
        collections.forEach((collection) -> {
            if (collection.contains(":")) {
                List<String> parts = new ArrayList<>(Arrays.asList(collection.split(":")));
                String hexCodeword = parts.get(0);
                byte sample = reversedMap.get(hexCodeword);
                int freq = Integer.parseInt(parts.get(1));
                for (int i = 0; i < freq; i++) {
                    addSample(sample);
                }
            } else {
                addSample(reversedMap.get(collection));
            }
        });
        return rawSamples;
    }
}
