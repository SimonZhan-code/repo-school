package gitlet;

/** Node class passed in.
 * @author Simon Zhan */
public class Node {

    /** content at this Node. **/
    private Commit _content;
    /** content of the mother node. **/
    private Commit _parent;

    /** Constructor of Node class.
     * @param content commit passed in.
     * @param parent commit passed in.*/
    Node(Commit parent, Commit content) {
        _parent = parent;
        _content = content;
    }

    /** Return parent. **/
    Commit getparent() {
        return _parent;
    }

    /** Return current content. **/
    Commit getcontent() {
        return _content;
    }
}
