package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Simon Zhan
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        if (notches == null || notches.equals(" ")) {
            return;
        }
        _notches = new int[notches.length()];
        for (int i = 0; i < notches.length(); i++) {
            _notches[i] = alphabet().toInt(notches.charAt(i));
        }
    }

    @Override
    boolean atNotch() {
        if (_notches == null) {
            return false;
        }
        for (int i : _notches) {
            if (setting() == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    /** Array of notches as integer.*/
    private int[] _notches;
}
