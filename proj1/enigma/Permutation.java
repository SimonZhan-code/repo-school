package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Simon Zhan
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
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        _cycles = _cycles + cycle;
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
        int tempInt1 = wrap(p);
        char temp1 = _alphabet.toChar(tempInt1);
        char temp2 = permute(temp1);
        int tempInt2x = _alphabet.toInt(temp2);
        return tempInt2x;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int tempInt1 = wrap(c);
        char temp1 = _alphabet.toChar(tempInt1);
        char temp2 = invert(temp1);
        int tempInt2y = _alphabet.toInt(temp2);
        return tempInt2y;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        char result = p;
        if (_cycles.equals("") || !_alphabet.contains(p)) {
            return result;
        }
        int index = _cycles.indexOf(String.valueOf(p));
        if (index != -1) {
            int nextInt = index + 1;
            if (_cycles.charAt(nextInt) != ')') {
                result = _cycles.charAt(nextInt);
            } else {
                int forward = index;
                while (_cycles.charAt(forward) != '(') {
                    forward -= 1;
                }
                result = _cycles.charAt(forward + 1);
            }
        }
        return result;
    }

    /** Return the result of applying the inverse of this permutation to C. */

    char invert(char c) {
        char result = c;
        if (_cycles.equals("") || !_alphabet.contains(c)) {
            return result;
        }
        int index = _cycles.indexOf(String.valueOf(c));
        if (index != -1) {
            int back = index - 1;
            if (_cycles.charAt(back) != '(') {
                result = _cycles.charAt(back);
            } else {
                int counter = index;
                while (_cycles.charAt(counter) != ')') {
                    counter += 1;
                }
                result = _cycles.charAt(counter - 1);
            }
        }
        return result;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        String withoutwhite = _cycles.replace("(", "").
                replace(")", "").replace(" ", "");
        for (int i = 0; i < withoutwhite.length(); i++) {
            if (withoutwhite.charAt(i) == permute(withoutwhite.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** The cycle of permutation of each permutation class.*/
    private String _cycles;
}
