package gitlet;

import java.io.Serializable;

/** Class to store branch.
 * @author Simon Zhan */
public class Branch implements Serializable {

    /** Name of the branch. **/
    private String _name;
    /** boolean value to judge whether this branch is current branch. **/
    private boolean _current;
    /** furtherest part of the commit in this branch. **/
    private Commit _byfar;

    /** Constructor to a branch.
     * @param temp Commit.
     * @param name String. */
    public Branch(Commit temp, String name) {
        _name = name;
        _byfar = temp;
        _current = false;
    }


    /** Return name of the branch. **/
    public String getname() {
        return _name;
    }

    /** Return is this branch the current branch. **/
    public boolean getcurrent() {
        return _current;
    }

    /** Return the furtherest commit in the branch. **/
    public Commit getbyfar() {
        return _byfar;
    }

    /** Change the furtherest commit in the branch.
     * @param temp string.*/
    void change(Commit temp) {
        _byfar = temp;
    }

    /** Change current state.**/
    void changetoCurr() {
        _current = true;
    }

    /** Change current state.**/
    void changetoPast() {
        _current = false;
    }

    /** Chnage the byfar commit.
     * @param temp string.*/
    void changebyfar(Commit temp) {
        _byfar = temp;
    }

    /** Remove the current branch. **/
    void remove() {
        _name = null;
        _byfar = null;
    }

}
