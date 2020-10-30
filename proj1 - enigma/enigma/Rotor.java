package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Jenny Miao
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        setting = 0;
        ring = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        setting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        if (p >= 0 && p <= size() - 1) {
            int perm = _permutation.permute(p + setting() - ring);
            return _permutation.wrap(_permutation.wrap(perm)
                    - setting() + ring);
        } else {
            throw error("integer not in range of permutation", p);
        }
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        if (e >= 0 && e <= size() - 1) {
            int a = _permutation.invert(e + setting() - ring);
            return _permutation.wrap(_permutation.wrap(a) - setting() + ring);
        } else {
            throw error("integer not in range of permutation", e);
        }
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** The setting number of this rotor. */
    private int setting;

    /** EC: The ring setting of this rotor. */
    private int ring;

    /** EC: Sets the ring setting to int R. */
    void setRing(int r) {
        ring = r;
    }
}
