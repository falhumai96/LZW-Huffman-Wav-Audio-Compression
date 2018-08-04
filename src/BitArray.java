
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

public final class BitArray {

    private final List<Boolean> vals;

    public BitArray() {
        vals = new ArrayList<>();
    }

    public BitArray(int n) {
        vals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            vals.add(false);
        }
    }

    public BitArray(byte[] bits) {
        vals = new ArrayList<>();
        add(bits);
    }

    public BitArray add(boolean bit) {
        vals.add(bit);
        return this;
    }

    public BitArray add(BitArray bits) {
        for (int i = 0; i < bits.size(); i++) {
            add(bits.get(i));
        }
        return this;
    }

    public BitArray add(byte[] bits) {
        for (int i = 0; i < (bits.length * 8); i++) {
            int posByte = i / 8;
            int posBit = i % 8;
            byte valByte = bits[posByte];
            int valInt = valByte >> (8 - (posBit + 1)) & 0x0001;
            vals.add(valInt != 0);
        }
        return this;
    }

    public boolean get(int idx) {
        return vals.get(idx);
    }

    public int size() {
        return vals.size();
    }

    public BitArray set(int idx, boolean val) {
        vals.set(idx, val);
        return this;
    }

    public BitArray clear() {
        vals.clear();
        return this;
    }

    public byte[] toByteArray() {
        if (size() == 0) {
            return new byte[0];
        }
        int numBytes = (int) Math.ceil(((double) vals.size()) / ((double) 8));
        byte[] toReturn = new byte[numBytes];
        Arrays.fill(toReturn, (byte) 0);
        for (int i = 0; i < vals.size(); i++) {
            int posByte = i / 8;
            int posBit = i % 8;
            byte oldByte = toReturn[posByte];
            oldByte = (byte) (((0xFF7F >> posBit) & oldByte) & 0x00FF);
            byte newByte = (byte) (((vals.get(i) == true ? 1 : 0) << (8 - (posBit + 1))) | oldByte);
            toReturn[posByte] = newByte;
        }
        return toReturn;
    }

    public String toByteString() {
        byte[] byteArray = toByteArray();
        String newString = "";
        for (byte b : byteArray) {
            if (!newString.equals("")) {
                newString += " ";
            }
            newString += Integer.toBinaryString(b & 255 | 256).substring(1);
        }
        return newString;
    }

    public String toBitString() {
        String newString = "";
        newString = vals.stream().map((bit) -> bit ? "1" : "0").reduce(newString, String::concat);
        return newString;
    }

    public static BitArray intToBitArray(int val) {
        boolean isNegative = val < 0;
        val = Math.abs(val);
        String valAsString = Integer.toBinaryString(val);
        int zerosToAdd = 6 - (valAsString.length() % 7);
        for (int i = 0; i < zerosToAdd; i++) {
            valAsString = "0" + valAsString;
        }
        BitArray toReturn = new BitArray();
        BitArray current = new BitArray(8);
        current.set(6, isNegative); // Add sign flag to the first block.
        int currentStringIdx = 0;
        boolean first = true;
        while (true) {
            for (int i = 0; i < (first ? 6 : 7) && currentStringIdx < valAsString.length(); i++) {
                current.set(i, valAsString.charAt(currentStringIdx) == '1');
                currentStringIdx++;
            }
            boolean willEnd = false;
            if (currentStringIdx < valAsString.length()) {
                current.set(7, false); // To indicate there is more to read.
            } else {
                current.set(7, true); // To indicate the ending flag.
                willEnd = true;
            }
            
            if (first) {
                first = false;
            }
            toReturn.add(current);
            current = new BitArray(8);

            if (willEnd) {
                break;
            }
        }
        return new BitArray(toReturn.toByteArray());
    }

    public static Pair<Integer, Integer> bitArrayToInt(BitArray val, int idx) throws Exception {
        if ((val.size() - (val.size() - idx)) % 8 != 0) {
            throw new Exception("Not a valid index!");
        }
        boolean first = true;
        boolean isNegative = false;
        int toReturn;
        int i;
        String toReturnAsUnsignedString = "";
        for (i = idx; i < val.size(); i += 8) {
            String current = "";
            for (int j = i; j < (first ? (i + 6) : (i + 7)) && j < val.size(); j++) {
                current += val.get(j) ? "1" : "0";
            }
            if (first) {
                isNegative = val.get(i + 6);
            }
            if (val.get(i + 7)) {
                i += 8; // Mark the current block as read.
                toReturnAsUnsignedString += current;
                break;
            } else {
                toReturnAsUnsignedString += current;
            }
            if (first) {
                first = false;
            }
        }
        toReturn = Integer.parseInt(toReturnAsUnsignedString, 2);
        if (isNegative) {
            toReturn = toReturn * -1;
        }
        return new Pair<>(toReturn, i);
    }
}
