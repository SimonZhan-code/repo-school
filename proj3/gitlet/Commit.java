package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;

/** Commit class written.
 * @author Simon Zhan */
public class Commit implements Serializable {

    /** The Hashing code of this commit. **/
    private String _hashid;
    /** Calender used for each commit. **/
    private Calendar _calendar;
    /** Formatter of the date. **/
    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
    /** Hashmap to store hashing. **/
    private HashMap<String, String> _store;
    /** Timestamp on each commit. **/
    private Date _timestamp;
    /** Parent of each commit. **/
    private String _parent;
    /** Date of each commit. **/
    private Date _date;
    /** Message for each commit. **/
    private String _message;
    /** Arraylist of children's ID. **/
    private ArrayList<String> _child;
    /** Other oarent of each commit.**/
    private String _parent2;

    /** Initial constructor. */
    public Commit() {
        _store = new HashMap<>();
        _parent = "";
        _message = "initial commit";
        _calendar = Calendar.getInstance();
        _date = _calendar.getTime();
        _hashid = Utils.sha1(_message);
        _timestamp = new Date(0);
        _child = new ArrayList<>();
    }


    /** Constructor for the developed Commit.
     * @param msg string.
     * @param date time used.
     * @param parenthashid string.
     * @param tracking string.*/
    public Commit(String msg, String parenthashid,
                  HashMap<String, String> tracking, Date date) {
        _message = msg;
        _parent = parenthashid;
        _calendar = Calendar.getInstance();
        _date = _calendar.getTime();
        _timestamp = date;
        _store = tracking;
        _child = new ArrayList<>();
        _hashid = sHAvalue();
    }


    /** Another constructor for Commit.
     * @param parent string.
     * @param message string.
     * @param parent2 string.
     * @param time time.
     * @param trackedFiles Hashmap.*/
    Commit(HashMap<String, String> trackedFiles, String parent,
           String parent2, String message, Date time) {
        _parent = parent;
        _parent2 = parent2;
        _timestamp = time;
        _message = message;
        _store = trackedFiles;
        _child = new ArrayList<>();
        _hashid = sHAvalue();
    }


    /** Returning storing content of each commit.
     * @return _store string.*/
    HashMap<String, String> getstore() {
        return _store;
    }

    /** Returning parent id of each commit.
     * @return String parent.*/
    String getparent() {
        return _parent;
    }

    /** Returning parent id of each commit.
     * @return String parent. */
    String getparent2() {
        return _parent2;
    }

    /** Return all the parents.
     * @return ArrayList return. */
    ArrayList<String> getParents() {
        ArrayList<String> p = new ArrayList<>();
        if (_parent != null) {
            p.add(_parent);
        }
        if (_parent2 != null) {
            p.add(_parent2);
        }
        return p;
    }


    /** Returning hashing id of each commit.
     * @return String return.*/
    String gethashid() {
        return _hashid;
    }

    /** Return child.
     * @return ArrayList return.*/
    ArrayList getchild() {
        return _child;
    }

    /** Returning message of each commit.
     * @return String return. */
    String getmessage() {
        return _message;
    }

    /** Return blob's id given by blob's name.
     * @param name string.
     * @return String content. */
    String getcontent(String name) {
        return _store.get(name);
    }

    /** Add child.
     * @param id ID.*/
    void addchild(String id) {
        _child.add(id);
    }

    /** Return time.
     * @return String name. */
    String gettime() {
        if (_timestamp == null) {
            return "Date: Thu Jan 1 00:00:00 1970 -0800";
        }
        String temp = FORMAT.format(_timestamp);
        return "Date: " + temp;
    }

    /** Boolean to judge whether this commit has parent.
     * @return boolean return. */
    boolean isParent() {
        return _message.equals("initial commit");
    }

    /** Return the hashing value of the commit.
     * @return String hashcode. */
    String sHAvalue() {
        String result = "";
        StringBuilder temp = new StringBuilder();
        for (String keyset : _store.keySet()) {
            temp.append(keyset);
        }
        result = temp.toString();
        result += _parent + _message + gettime();
        return Utils.sha1(result);
    }
}
