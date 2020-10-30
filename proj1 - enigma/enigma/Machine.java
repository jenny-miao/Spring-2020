package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jenny Miao
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < ROTORS rotor slots,
     *  and 0 <= NUMPAWLS < _NUMROTORS pawls.  THEROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int rotors, int numPawls,
            Collection<Rotor> theRotors) {
        _alphabet = alpha;
        this.numRotors = rotors;
        this.pawls = numPawls;
        this.allRotors = new ArrayList<Rotor>(theRotors);
        this.activeRotors = new ArrayList<Rotor>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        activeRotors = new ArrayList<Rotor>();
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < allRotors.size(); j++) {
                if (rotors[i].equals(allRotors.get(j).name())) {
                    activeRotors.add(allRotors.get(j));
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw error("Number of characters in setting "
                    + "does not match number of rotors");
        }
        for (int i = 1; i < numRotors(); i++) {
            char setter = setting.charAt(i - 1);
            activeRotors.get(i).set(setter);
        }
    }

    /** Set the plugboard to PLUG. */
    void setPlugboard(Permutation plug) {
        String characters = plug.cycles().replaceAll("\\(", "");
        characters = characters.replaceAll("\\)", "");
        for (int i = 0; i < characters.length(); i++) {
            if (!plug.alphabet().contains(characters.charAt(i))) {
                throw error("Plugboard contains chars not in Alphabet");
            }
        }
        this.plugboard = plug;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        if (c < 0 || c >= _alphabet.size()) {
            throw error("Index of character is not in range of alphabet size");
        }
        boolean[] advances = new boolean[activeRotors.size()];
        for (int i = 0; i < activeRotors.size(); i++) {
            if (activeRotors.get(i).atNotch()
                    && activeRotors.get(i - 1).rotates()) {
                advances[i] = true;
                if (i > 0) {
                    advances[i - 1] = true;
                }
            } else if (i == (activeRotors.size() - 1)) {
                advances[i] = true;
            }
        }
        for (int i = 0; i < activeRotors.size(); i++) {
            if (advances[i]) {
                activeRotors.get(i).advance();
            }
        }
        int change = c;
        if (plugboard.inPerm(change)) {
            change = plugboard.permute(change);
        }
        for (int i = activeRotors.size() - 1; i >= 0; i--) {
            change = activeRotors.get(i).convertForward(change);
        }
        for (int i = 1; i < activeRotors.size(); i++) {
            change = activeRotors.get(i).convertBackward(change);
        }
        if (plugboard.inPerm(change)) {
            change = plugboard.invert(change);
        }
        return change;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String str = new String("");
        for (int i  = 0; i < msg.length(); i++) {
            int a = _alphabet.toInt(msg.charAt(i));
            char b = _alphabet.toChar(convert(a));
            String bStr = Character.toString(b);
            str = str.concat(bStr);
        }
        return str;
    }

    /** Returns my alphabet. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Returns number of rotors. */
    int getNumRotors() {
        return numRotors;
    }

    /** Returns my number of pawls. */
    int getPawls() {
        return pawls;
    }

    /** Returns all my rotors. */
    ArrayList<Rotor> getAllRotors() {
        return allRotors;
    }

    /** Returns my active rotors. */
    ArrayList<Rotor> getActiveRotors() {
        return activeRotors;
    }

    /** Returns my plugboard permutation. */
    Permutation getPlugboard() {
        return plugboard;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of Rotors in this Machine. */
    private int numRotors;

    /** Number of pawls in this machine. */
    private int pawls;

    /** Arraylist of all rotors in this machine. */
    private ArrayList<Rotor> allRotors;

    /** Permutation of this machine's plugboard. */
    private Permutation plugboard;

    /** Arraylist of the active rotors being used. */
    private ArrayList<Rotor> activeRotors;
}
