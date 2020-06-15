package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Simon Zhan
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _rotorcollection = new Rotor[numRotors];
        _pawls = pawls;
        _allrotors = allRotors;
        _numRotors = numRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        try {
            for (int i = 0; i < rotors.length; i++) {
                for (Rotor rotor : _allrotors) {
                    if (rotor.name().equals(rotors[i])) {
                        if (i == 0 && !rotor.reflecting()) {
                            throw error("First one should be a reflector");
                        }
                        rotor.set(0);
                        _rotorcollection[i] = rotor;
                    }
                }
                if (_rotorcollection[i] == null) {
                    throw error("Wrong number of rotors in the list");
                }
            }
            int lastindex = _rotorcollection.length;
            for (int j = _pawls; j > 0; j--) {
                _rotorcollection[lastindex - 1].setPawl();
                lastindex -= 1;
            }
        } catch (EnigmaException except) {
            throw except;
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            _rotorcollection[i + 1].set(setting.charAt(i));
        }
    }

    /** Set up both rotor collection and its ring setting.
     * @param settingring The ring setting of rotor.*/
    void setboth(String settingring) {
        for (int i = 0; i < settingring.length(); i++) {
            _rotorcollection[i + 1].setring(settingring.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** All Rotors advance according to the rule.*/
    void moveadvance() {
        Rotor lastrotor = _rotorcollection[_numRotors - 1];
        boolean[] recorder = new boolean[_numRotors];
        for (int i = 0; i < _rotorcollection.length; i++) {
            recorder[i] = false;
        }
        for (int i = 1; i < _numRotors - 2; i++) {
            if (_rotorcollection[i + 1].atNotch()
                    && _rotorcollection[i].rotates()
                    && _rotorcollection[i].getPawl()) {
                if (!recorder[i]) {
                    _rotorcollection[i].advance();
                    _rotorcollection[i + 1].advance();
                } else {
                    _rotorcollection[i + 1].advance();
                }
                recorder[i + 1] = true;
            }
        }
        if (lastrotor.atNotch()
                && _rotorcollection[_numRotors - 2].rotates()
                && lastrotor.getPawl()) {
            if (!recorder[_numRotors - 2]) {
                this._rotorcollection[_numRotors - 2].advance();
                lastrotor.advance();
            } else {
                lastrotor.advance();
            }
        } else {
            lastrotor.advance();
        }
    }
    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        moveadvance();
        int valuetakein = c;
        if (_plugboard != null) {
            valuetakein = _plugboard.permute(valuetakein);
        }
        boolean direction = true;
        int position = _numRotors - 1;
        while (true) {
            if (position == _numRotors) {
                if (_plugboard != null) {
                    valuetakein = _plugboard.invert(valuetakein);
                }
                break;
            }
            if (_rotorcollection[position].reflecting()) {
                direction = !direction;
            }
            if (direction) {
                valuetakein = _rotorcollection[position].
                        convertForward(valuetakein);
                position -= 1;
            }
            if (!direction) {
                valuetakein = _rotorcollection[position].
                        convertBackward(valuetakein);
                position += 1;
            }
        }
        return valuetakein;

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (char message: msg.toCharArray()) {
            char change = _alphabet.toChar(convert(_alphabet.toInt(message)));
            result = result + Character.toString(change);
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** number of rotors in a machine.*/
    private int _numRotors;

    /** number of pawls in a machine.*/
    private int _pawls;

    /** Array collection of Rotors in the machine.*/
    private Rotor[] _rotorcollection;

    /** Arraylist collect all the rotors.*/
    private Collection<Rotor> _allrotors;

    /** Plugeboard used to copy the original plugeboard.*/
    private Permutation _plugboard;

    /** Return all rotors of the machine. */
    public Collection<Rotor> giveRotor() {
        return _allrotors;
    }
}
