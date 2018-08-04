
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class HuffmanCodingProcessor implements CodingProcessor {

    private final List<Byte> rawSamples;

    public HuffmanCodingProcessor() {
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

    private Map<Byte, Integer> getCounts() {
        Map<Byte, Integer> counts = new ConcurrentHashMap<>();
        rawSamples.forEach((sample) -> {
            if (!counts.containsKey(sample)) {
                counts.put(sample, 1);
            } else {
                counts.put(sample, counts.get(sample) + 1);
            }
        });
        return counts;
    }

    public static class HuffmanNode {

        private final int value;
        private final byte sample;
        private final boolean isLeafNode;
        private final HuffmanNode childNode1, childNode2;

        public HuffmanNode(int value, HuffmanNode childNode1, HuffmanNode childNode2) {
            this.value = value;
            this.childNode1 = childNode1;
            this.childNode2 = childNode2;
            this.isLeafNode = false;
            this.sample = -1;
        }

        public HuffmanNode(int value, byte sample) {
            this.value = value;
            this.childNode1 = null;
            this.childNode2 = null;
            this.isLeafNode = true;
            this.sample = sample;
        }

        public boolean isLeafNode() {
            return isLeafNode;
        }

        public byte getSample() {
            return sample;
        }

        public int getValue() {
            return value;
        }

        public HuffmanNode getChildNode1() {
            return childNode1;
        }

        public HuffmanNode getChildNode2() {
            return childNode2;
        }
    }

    private Pair<List<HuffmanNode>, Set<Byte>> getInitialNodesList() {
        Map<Byte, Integer> counts = getCounts();
        List<HuffmanNode> initialNodes = new ArrayList<>();
        counts.keySet().forEach((key) -> {
            initialNodes.add(new HuffmanNode(
                    counts.get(key) / rawSamples.size(),
                    key)
            );
        });
        return new Pair<>(initialNodes, counts.keySet());
    }

    private Pair<HuffmanNode, Set<Byte>> getHuffmanTreeRootNode() {
        Pair<List<HuffmanNode>, Set<Byte>> initialData = getInitialNodesList();
        List<HuffmanNode> topNodes = initialData.getKey();
        topNodes.sort(Comparator.comparing(HuffmanNode::getValue));
        Set<Byte> samples = initialData.getValue();
        while (topNodes.size() > 1) {
            HuffmanNode n1 = topNodes.get(0);
            HuffmanNode n2 = topNodes.get(1);
            topNodes.remove(n1);
            topNodes.remove(n2);
            topNodes.add(new HuffmanNode(n1.getValue() + n2.getValue(), n1, n2));
            topNodes.sort(Comparator.comparing(HuffmanNode::getValue));
        }
        return new Pair<>(topNodes.get(0), samples);
    }

    String getCode(int sample, String currentCode, HuffmanNode currentNode) {
        if (currentNode.isLeafNode() && sample == currentNode.getSample()) {
            return currentCode;
        } else if (currentNode.isLeafNode()) {
            return null;
        }
        String leftCode = getCode(sample, currentCode + "1", currentNode.getChildNode1());
        if (leftCode != null) {
            return leftCode;
        }
        return getCode(sample, currentCode + "0", currentNode.getChildNode2());
    }

    private Map<Byte, String> getCodes(Pair<HuffmanNode, Set<Byte>> huffmanData) {
        HuffmanNode rootNode = huffmanData.getKey();
        Set<Byte> samples = huffmanData.getValue();
        Map<Byte, String> toReturn = new ConcurrentHashMap<>();
        samples.forEach((sample) -> {
            toReturn.put(sample, getCode(sample, "", rootNode));
        });
        return toReturn;
    }

    private String getFinalCode(Map<Byte, String> codes) {
        String finalCode = "";
        for (Byte sample : rawSamples) {
            finalCode = finalCode.concat(codes.get(sample));
        }
        return finalCode;
    }

    BitArray constructDictionaryData(Map<Byte, String> dictionary) {
        BitArray toReturn = new BitArray();

        toReturn.add(BitArray.intToBitArray(dictionary.size())); // Append the size.
        // Append the dictionary key-value pairs.

        dictionary.entrySet().stream().map((entrySet) -> {
            toReturn.add(BitArray.intToBitArray((int) entrySet.getKey())); // Add the key.
            return entrySet;
        }).map((entrySet) -> {
            toReturn.add(BitArray.intToBitArray(Integer.parseInt(entrySet.getValue(), 2))); // Add the value.
            return entrySet;
        }).forEachOrdered((entrySet) -> {
            String converted = Integer.toBinaryString(Integer.parseInt(entrySet.getValue(), 2));
            toReturn.add(BitArray.intToBitArray(entrySet.getValue().length() - converted.length())); // Add number of missing 0 bits.
        });

        return toReturn;
    }

    BitArray constructActualData(String actualData) {
        BitArray toReturn = new BitArray();

        // Append the real number of bits.
        toReturn.add(BitArray.intToBitArray(actualData.length()));

        // Add all the bits.
        for (int i = 0; i < actualData.length(); i++) {
            char currentChar = actualData.charAt(i);
            toReturn.add(currentChar == '1');
        }

        return toReturn;
    }

    private byte[] convertToBytes(Pair<String, Map<Byte, String>> compressionResults) {
        return constructDictionaryData(compressionResults.getValue()).add(constructActualData(compressionResults.getKey())).toByteArray();
    }

    private Pair<Integer, Map<Byte, String>> constructDictionary(BitArray compressionResultsBits) {
        try {
            Map<Byte, String> toReturnDict = new ConcurrentHashMap<>();

            if (compressionResultsBits.size() == 0) {
                return null;
            }

            // Get the size.
            Pair<Integer, Integer> sizeData = BitArray.bitArrayToInt(compressionResultsBits, 0);

            if (sizeData.getKey() == 0) {
                return new Pair<>(sizeData.getValue(), toReturnDict);
            }

            // Add all key-value pairs.
            Pair<Integer, Integer> currentKey = BitArray.bitArrayToInt(compressionResultsBits, sizeData.getValue());
            Pair<Integer, Integer> currentValue = BitArray.bitArrayToInt(compressionResultsBits, currentKey.getValue());
            Pair<Integer, Integer> currentMissingBits = BitArray.bitArrayToInt(compressionResultsBits, currentValue.getValue());
            String actualValue = Integer.toBinaryString(currentValue.getKey());
            for (int i = 0; i < currentMissingBits.getKey(); i++) {
                actualValue = '0' + actualValue;
            }
            toReturnDict.put((byte) currentKey.getKey().intValue(), actualValue);
            for (int i = 1; i < sizeData.getKey(); i++) {
                currentKey = BitArray.bitArrayToInt(compressionResultsBits, currentMissingBits.getValue());
                currentValue = BitArray.bitArrayToInt(compressionResultsBits, currentKey.getValue());
                currentMissingBits = BitArray.bitArrayToInt(compressionResultsBits, currentValue.getValue());
                actualValue = Integer.toBinaryString(currentValue.getKey());
                for (int j = 0; j < currentMissingBits.getKey(); j++) {
                    actualValue = '0' + actualValue;
                }
                toReturnDict.put((byte) currentKey.getKey().intValue(), actualValue);
            }

            return new Pair<>(currentMissingBits.getValue(), toReturnDict);
        } catch (Exception ex) {
            Logger.getLogger(HuffmanCodingProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String constructData(BitArray compressionResultsBits, int idx) {
        try {
            String toReturn = "";

            Pair<Integer, Integer> numberOfBits = BitArray.bitArrayToInt(compressionResultsBits, idx);

            for (int i = numberOfBits.getValue(); i < (numberOfBits.getKey() + numberOfBits.getValue()); i++) {
                toReturn += compressionResultsBits.get(i) ? '1' : '0';
            }

            return toReturn;
        } catch (Exception ex) {
            Logger.getLogger(HuffmanCodingProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
        if (rawSamples.isEmpty()) {
            return null;
        }
        Map<Byte, String> codes = getCodes(getHuffmanTreeRootNode());
        return convertToBytes(new Pair<>(getFinalCode(codes), codes));
    }

    @Override
    public List<Byte> decompress(byte[] compressionResultsAsBytes) {
        Pair<String, Map<Byte, String>> compressionResults = convertToCompressionResults(compressionResultsAsBytes);
        Map<Byte, String> codesAsIntegers = compressionResults.getValue();
        Map<String, Byte> codesAsStrings
                = codesAsIntegers.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        String compressedData = compressionResults.getKey();

        clearSamples();
        String currentCodeWord = "";
        for (char c : compressedData.toCharArray()) {
            currentCodeWord += c;
            if (codesAsStrings.containsKey(currentCodeWord)) {
                addSample(codesAsStrings.get(currentCodeWord));
                currentCodeWord = "";
            }
        }
        return getSamples();
    }

}
