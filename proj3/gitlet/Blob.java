package gitlet;
import java.io.Serializable;
import java.io.File;

/** Class to store file.
 * @author Simon Zhan
 *   */
public class Blob implements  Serializable {
    /** Name of the file.**/
    private String _head;
    /** Hashcode of the content.**/
    private String _hashcode;
    /** A list to store content in byte form. **/
    private byte[] _content;
    /** String detail about the content. **/
    private String _contentstring;

    /** Constructor of the blob class.
     * @param temp file passed in. **/
    Blob(File temp) {
        _head = temp.getName();
        _content = Utils.readContents(temp);
        _contentstring = Utils.readContentsAsString(temp);
        _hashcode = Utils.sha1(_contentstring);

    }

    /** Method to return hashcode of the blob.**/
    String hashcodereturn() {
        return _hashcode;
    }

    /** Method to return content of the blob.**/
    byte[] contentreturn() {
        return _content;
    }

    /** Method to return content in the string form. **/
    String contentinstring() {
        return _contentstring;
    }

    /** Method to return name of the blob.**/
    String namereturn() {
        return _head;
    }
}
