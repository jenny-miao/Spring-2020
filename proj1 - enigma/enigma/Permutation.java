package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jenny Miao
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        mappedInverse = new HashMap<Character, Character>();
        mappedPermute = new HashMap<Character, Character>();
        deranged = true;

        String[] splitCycle = (_cycles.replaceAll("\\s+", "")).split("\\(");
        for (int i = 1; i < splitCycle.length; i++) {
            for (int j = 0; j < splitCycle[i].length() - 1; j++) {
                if (j == splitCycle[i].length() - 2) {
                    mappedPermute.put(splitCycle[i].charAt(j),
                            splitCycle[i].charAt(0));
                } else {
                    mappedPermute.put(splitCycle[i].charAt(j),
                            splitCycle[i].charAt(j + 1));
                }
            }
            for (int j = 0; j < splitCycle[i].length() - 1; j++) {
                if (j == 0) {
                    mappedInverse.put(splitCycle[i].charAt(j),
                            splitCycle[i].charAt(splitCycle[i].length() - 2));
                } else {
                    mappedInverse.put(splitCycle[i].charAt(j),
                            splitCycle[i].charAt(j - 1));
                }
            }
        }
        for (int i = 0; i < _alphabet.size(); i++) {
            char alpha = _alphabet.toChar(i);
            if (mappedPermute.get(alpha) == null) {
                deranged = false;
                mappedPermute.put(alpha, alpha);
                mappedInverse.put(alpha, alpha);
            }
        }
        if (_alphabet.size() == 1) {
            deranged = false;
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (i == cycle.length() - 1) {
                mappedPermute.put(cycle.charAt(i), cycle.charAt(0));
            } else {
                mappedPermute.put(cycle.charAt(i), cycle.charAt(i + 1));
            }
        }
        for (int i = 1; i < cycle.length(); i++) {
            if (i == 1) {
                mappedInverse.put(cycle.charAt(i),
                        cycle.charAt(cycle.length() - 1));
            } else {
                mappedInverse.put(cycle.charAt(i), cycle.charAt(i - 1));
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char permutable = _alphabet.toChar(wrap(p));
        char permuted = (char) mappedPermute.get(permutable);
        return _alphabet.toInt(permuted);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char invertable = _alphabet.toChar(wrap(c));
        char inverted = (char) mappedInverse.get(invertable);
        return _alphabet.toInt(inverted);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (mappedPermute.containsKey(p)) {
            return (char) mappedPermute.get(p);
        } else {
            throw error("Character is not in the alphabet, "
                    + "cannot be permuted", p);
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (mappedInverse.containsKey(c)) {
            return (char) mappedInverse.get(c);
        } else {
            throw error("Character is not in the alphabet,"
                    + " cannot be inverted", c);
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return the cycles used to initialize this Permutation. */
    String cycles() {
        return _cycles;
    }

    /** Return whether char at index A is in this Permutation. */
    boolean inPerm(int a) {
        boolean in = false;
        String characters = cycles().replaceAll("\\(", "");
        characters = characters.replaceAll("\\)", "");
        for (int i = 0; i < characters.length(); i++) {
            if (characters.charAt(i) == alphabet().toChar(a)) {
                in = true;
            }
        }
        return in;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return deranged;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String of the cycles of this permutation. */
    private String _cycles;

    /** Contains mapping of permutations. */
    private HashMap<Character, Character> mappedPermute;
    /** Contains mapping of inverses. */
    private HashMap<Character, Character> mappedInverse;
    /** Whether the permutation is a derangement. */
    private boolean deranged;
}
