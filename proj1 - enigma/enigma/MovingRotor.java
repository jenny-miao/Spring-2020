package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jenny Miao
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHNAMES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notchNames) {
        super(name, perm);
        this.notches = notchNames;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        boolean notch = false;
        for (int i = 0; i < notches.length(); i++) {
            if (alphabet().toChar(permutation().wrap(setting()))
                    == notches.charAt(i)) {
                notch = true;
            }
        }
        return notch;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }

    /** Notch names for this rotor. */
    private String notches;
}
