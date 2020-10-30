package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Jenny Miao
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        if (!this.permutation().derangement()) {
            throw error("A reflector must be a derangement.");
        }
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    @Override
    void set(char cposn) {
        if (alphabet().toInt(cposn) != 0) {
            throw error("Reflector can only have one position at 0");
        }
    }

    @Override
    boolean reflecting() {
        return true;
    }
}
