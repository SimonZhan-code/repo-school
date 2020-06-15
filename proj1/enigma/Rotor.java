package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Simon Zhan
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _position = 0;
        _ringsetting = 0;
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
        return _position;
    }

    /** Return my Ringsetting. */
    int ringsetting() {
        return _ringsetting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _position = posn;
    }

    /** Set the ringsetting of a rotor.
     * @param ring The index of ringsetting. */
    void settingring(int ring) {
        _ringsetting = ring;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        int position = alphabet().toInt(cposn);
        set(position);
    }

    /** Set ringsetting to character.
     * @param setup The character used for ringsetting.*/
    void setring(char setup) {
        int position = alphabet().toInt(setup);
        settingring(position);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int temp1 = _permutation.wrap(p + setting() - ringsetting());
        int temp2 = _permutation.permute(temp1);
        int result = _permutation.wrap(temp2 - setting() + ringsetting());
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int temp1 = _permutation.wrap(e + setting() - ringsetting());
        int temp2 = _permutation.invert(temp1);
        int result = _permutation.wrap(temp2 - setting() + ringsetting());
        return result;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    /** Set the pawl to the target rotor.*/
    public void setPawl() {
        hasPawl = true;
    }

    /** Returning function for the pawl status.*/
    public boolean getPawl() {
        return this.hasPawl;
    }

    /** Returning setting of rotor.
     * @return _position integer of the setting.*/
    public int getPosition() {
        return _position;
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** The position of the rotor in series of enigma.*/
    private int _position;

    /** Boolean showing whether or not there is a Pawl on the rotor.*/
    private boolean hasPawl;

    /** Ring setting of the rotor. */
    private int _ringsetting;
}
