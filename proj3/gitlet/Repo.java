package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

/** Repo class used to carry out the whole gitlet system.
 * @author Simon Zhan
 *  */
public class Repo implements Serializable {


    /** Keep track of branch name and its pointer. **/
    private HashMap<String, Branch> _branchmap;
    /** Keep track of commit ID and itself. **/
    private HashMap<String, Commit> _commitmap;
    /** Arraylist to keep track of each node. **/
    private ArrayList<Node> _node;
    /** Pointer to the farest commit in the branch. **/
    private Commit _far;
    /** New directory of files. **/
    private File gitlet;
    /** Link between stage file's name and its content. **/
    private HashMap<String, String> _staging;
    /** Folder for staging area in .gitlet.**/
    private File stage;
    /** Folder content the removing file. **/
    private File removing;
    /** Maps between removing files to its toString value. **/
    private HashSet<String> _toremove;
    /** Remote directory to store remote data. */
    private HashMap<String, String> _remoteDir;

    /** Constructor for the git system. */
    public Repo() {
        File git = new File(".gitlet");
        gitlet = new File("./.gitlet");
        boolean t = gitlet.mkdir();
        stage = new File("./.gitlet/.staging/");
        boolean s = stage.mkdirs();
        _branchmap = new HashMap<>();
        _commitmap = new HashMap<>();
        _toremove = new HashSet<>();
        Commit first = new Commit();
        Branch branch = new Branch(first, "master");
        branch.changetoCurr();
        _far = branch.getbyfar();
        _commitmap.put(first.gethashid(), first);
        _branchmap.put(branch.getname(), branch);
        _staging = new HashMap<>();
        _remoteDir = new HashMap<>();
    }

    /** Method to save git.
     * @param toSave save*/
    static void save(Repo toSave) {
        File serialized = new File("./.gitlet/serialized");
        try {
            ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(serialized));
            output.writeObject(toSave);
            output.close();
        } catch (IOException | ClassCastException excp) {
            int i = 1;
        }
    }

    /** Method to read git.
     * @return Repo git retrieved.*/
    static Repo read() {
        Repo deserialize = new Repo();
        File tempfile = new File(".gitlet/serialized");
        try {
            ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(tempfile));
            deserialize = (Repo) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException exp) {
            int i = 1;
        }
        return deserialize;
    }

    /** add method.
     * @param file content added.*/
    void add(String file) throws IOException {
        File tempfile = new File(file);
        if (!tempfile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob temp = new Blob(tempfile);
        if (_staging.containsKey(file)) {
            if (_staging.get(file).equals(temp.hashcodereturn())) {
                if (_far.getstore().get(file) != null
                        && _far.getstore().get(file).
                        equals(temp.hashcodereturn())) {
                    File staging = new File(".gitlet/.staging/"
                            + temp.hashcodereturn());
                    if (staging.exists()) {
                        boolean t = staging.delete();
                    }
                    _staging.remove(file);
                    return;
                }
            }
            if (_far.getstore().get(file) != null
                    && _far.getstore().get(file).
                    equals(temp.hashcodereturn())) {
                File tempstaging = new File(".gitlet/.staging/"
                        + temp.hashcodereturn());
                if (tempstaging.exists()) {
                    boolean a = tempstaging.delete();
                }
                _staging.remove(file);
            }
            String oldhashID = _staging.get(file);
            File oldF = new File(".gitlet/.staging/" + oldhashID);
            boolean b = oldF.delete();
            _staging.remove(file);
            File newF = new File(".gitlet/.staging/"
                    + temp.hashcodereturn());
            boolean c = newF.createNewFile();
            Utils.writeContents(newF, temp.contentinstring());
            _staging.put(file, temp.hashcodereturn());
        } else {
            if (_far.getstore().get(file) != null
                    && _far.getstore().get(file).
                    equals(temp.hashcodereturn())) {
                File f = new File(".gitlet/.staging/" + temp.hashcodereturn());
                if (f.exists()) {
                    boolean d = f.delete();
                }
            } else {
                File tempstaging3 = new File(".gitlet/.staging/"
                        + temp.hashcodereturn());
                boolean e = tempstaging3.createNewFile();
                Utils.writeContents(tempstaging3, temp.contentinstring());
                _staging.put(file, temp.hashcodereturn());
            }
        }
        if (_toremove.contains(file)) {
            _toremove.remove(file);
        }
    }

    /** Commit the changes in the file.
     * @param msg message.*/
    void commit(String msg) throws IOException {
        if (_staging.isEmpty() && _toremove.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {
            HashMap<String, String> tracking = new HashMap<String, String>();
            tracking.putAll(_staging);
            for (String tem : _far.getstore().keySet()) {
                if (!tracking.containsKey(tem)) {
                    String contents = _far.getstore().get(tem);
                    tracking.put(tem, contents);
                }
                if (_toremove.contains(tem)) {
                    tracking.remove(tem);
                }
            }
            Date now = new Date();
            Commit commit = new Commit(msg, _far.gethashid(), tracking, now);
            _far.addchild(commit.gethashid());
            _commitmap.put(commit.gethashid(), commit);
            getBranch().changebyfar(commit);
            _far = commit;
            _staging.clear();
            _toremove.clear();
        }
    }

    /** Unstaged the given file. The rm command in general.
     * @param filename filename. */
    void rm(String filename) {
        File remove = new File(filename);

        if (!remove.exists()) {
            if (_far.getstore().containsKey(filename)) {
                String contents = _far.getcontent(filename);
                _toremove.add(filename);
                return;
            }
        }
        Blob toremove = new Blob(remove);
        if (_staging.containsKey(filename)) {
            _staging.remove(filename);
            if (_far.getstore().containsKey(filename)) {
                _toremove.add(filename);
            }
        } else if (_far.getstore().containsKey(filename)) {
            if (remove.exists()) {
                remove.delete();
            }
            _toremove.add(filename);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }


    /** Get the current branch.
     * @retrun Branch branch return. */
    Branch getBranch() {
        for (String key : _branchmap.keySet()) {
            if (_branchmap.get(key).getcurrent()) {
                return _branchmap.get(key);
            }
        }
        return null;
    }

    /** Check out command. Take current file to the working directory.
     * Overwriting if it already exists.
     * New version is not in staging anymore.
     * @param filename filename. */
    void checkoutfile(String filename) {
        if (!_far.getstore().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            File file = new File(filename);
            if (file.exists()) {
                String contents = _far.getcontent(filename);
                File file1 = new File(".gitlet/.staging/" + contents);
                Blob temp1 = new Blob(file1);
                Utils.writeContents(file, temp1.contentinstring());
            } else {
                try {
                    boolean a = file.createNewFile();
                    String contents = _far.getcontent(filename);
                    File file2 = new File(".gitlet/.staging/" + contents);
                    Blob temp = new Blob(file2);
                    Utils.writeContents(file, temp.contentinstring());
                } catch (IOException e) {
                    return;
                }
            }
        }
    }

    /** Check out command. Take current file with given
     * id to the working directory.
     * Overwriting if it already exists. New version
     * is not in staging anymore.
     * @param filename filename.
     * @param iD ID*/
    void checkoutID(String iD, String filename) {
        String abrev = "empty";
        for (String id : _commitmap.keySet()) {
            if (id.startsWith(iD)) {
                abrev = id;
            }
        }
        if (!_commitmap.containsKey(abrev)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (!_commitmap.get(abrev).getstore().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            String hashcode = _commitmap.get(abrev).getcontent(filename);
            File tempfile = new File(".gitlet/.staging/" + hashcode);
            Blob temp = new Blob(tempfile);
            File file = new File(filename);
            if (file.exists()) {
                Utils.writeContents(file, temp.contentinstring());
            } else {
                try {
                    boolean a = file.createNewFile();

                } catch (IOException e) {
                    System.exit(0);
                }
                Utils.writeContents(file, temp.contentinstring());
            }
        }
    }

    /** Check out all files in the commit at the head of the given branch.
     * Take the files into the working directory.
     * @param branchname branchname. */
    void checkoutbranch(String branchname) {
        if (!_branchmap.containsKey(branchname)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (_branchmap.get(branchname).getcurrent()) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        } else {
            Commit branchHead = _branchmap.get(branchname).getbyfar();
            HashMap<String, String> currTracking = _far.getstore();
            HashMap<String, String> branchTracking = branchHead.getstore();
            for (String temp : branchTracking.keySet()) {
                if (!currTracking.containsKey(temp)) {
                    File currFile = new File(temp);
                    if (currFile.exists()) {
                        System.out.println("There is an untracked "
                                + "file in the way; "
                                + "delete it or add it first.");
                        System.exit(0);
                    }
                }
                checkoutID(branchHead.gethashid(), temp);
            }
            for (String file : currTracking.keySet()) {
                if (!branchTracking.containsKey(file)) {
                    File cFile = new File(file);
                    if (cFile.exists()) {
                        boolean t = cFile.delete();
                    }
                }
            }
            getBranch().changetoPast();
            _branchmap.get(branchname).changetoCurr();
            _far = branchHead;
            _staging.clear();
        }
    }

    /** Display information about each commit. **/
    void log() {
        Commit temp = _far;
        while (temp != null && !temp.isParent()) {
            System.out.println("===");
            System.out.println("commit " + temp.gethashid());
            if (temp.getParents().size() == 2) {
                System.out.println("Merge: " + temp.getparent().substring(0, 7)
                        + " " + temp.getparent2().substring(0, 7));
            }
            System.out.println(temp.gettime());
            System.out.println(temp.getmessage());
            System.out.println();
            temp = _commitmap.get(temp.getparent());
        }
        System.out.println("===");
        System.out.println("commit " + temp.gethashid());
        if (temp.getParents().size() == 2) {
            System.out.println("Merge: " + temp.getparent().substring(0, 7)
                    + " " + temp.getparent2().substring(0, 7));
        }
        System.out.println(temp.gettime());
        System.out.println(temp.getmessage());
    }

    /** Global log command to printout all commits. **/
    void globalLog() {
        for (String i : _commitmap.keySet()) {
            Commit temp = _commitmap.get(i);
            System.out.println("===");
            System.out.println("commit " + temp.gethashid());
            System.out.println(temp.gettime());
            System.out.println(temp.getmessage());
            System.out.println();
        }
    }

    /** branch method to create new branch in current
     * gitlet system with given name.
     * Set the current branch to the head note commit.
     * @param name name.*/
    void branch(String name) {
        if (_branchmap.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        } else {
            Branch temp = new Branch(_far, name);
            _branchmap.put(name, temp);
        }
    }

    /** Delete the current branch, without deleting all the commits under it.
     * @param name name.*/
    public void rmBranch(String name) {
        if (_branchmap.containsKey(name)) {
            Branch delete = _branchmap.get(name);
            if (delete.equals(getBranch())) {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            } else {
                _branchmap.remove(name);
                delete.remove();
            }
        } else {
            System.out.println("A branch  with that name does not exist.");
            System.exit(0);
        }
    }

    /** Reset the whole Git version controlled system.
     * @param commit commit.*/
    public void reset(String commit) {
        String abrev = "empty";
        for (String iD : _commitmap.keySet()) {
            if (iD.startsWith(commit)) {
                abrev = iD;
            }
        }
        if (!_commitmap.containsKey(abrev)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<String, String> commits = _commitmap.get(abrev).getstore();
        File allfile = new File("./");
        File[] allFiles = allfile.listFiles();

        for (File file : allFiles) {
            String name = file.getName();
            if (!_far.getstore().containsKey(name)
                    && commits.containsKey(name)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                System.exit(0);
            }
        }
        for (String name : commits.keySet()) {
            checkoutID(abrev, name);
        }
        for (String name : _far.getstore().keySet()) {
            if (!commits.containsKey(name)) {
                File tmp = new File(name);
                if (tmp.exists()) {
                    boolean a = tmp.delete();
                }
            }
        }
        getBranch().changebyfar(_commitmap.get(abrev));
        _far = getBranch().getbyfar();
        _staging.clear();
        _toremove.clear();
    }

    /** Find all the commits with given messages.
     * @param msg message. */
    void find(String msg) {
        if (_commitmap.isEmpty()) {
            System.out.println("Found no commit with that message.");
        }
        boolean flag = false;
        for (String temp : _commitmap.keySet()) {
            Commit check = _commitmap.get(temp);
            if (msg.equals(check.getmessage())) {
                System.out.println(temp);
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Helper function for the status function.
     * @param file File.
     * @return String name return.*/
    String statushelper1(File file) {
        String state = "";
        String name = file.getName();
        if (_far.getstore().containsKey(name)) {
            Blob temp = new Blob(file);
            if (!_far.getcontent(temp.namereturn()).
                    equals(temp.hashcodereturn())) {
                if (!_staging.containsKey(temp.namereturn())) {
                    state = "modified";
                }
            }
        }
        if (_staging.containsKey(name)) {
            Blob temp = new Blob(file);
            if (!_staging.get(temp.namereturn()).
                    equals(temp.hashcodereturn())) {
                state = "modified";
            }
        }

        return state;
    }

    /** Another status helper function.
     * @return String return. */
    String statushelper2() {
        String state = "";
        for (String nameF : _staging.keySet()) {
            File temp = new File(nameF);
            if (!temp.exists()) {
                state = temp.getName();
            }
        }
        for (String nameF : _far.getstore().keySet()) {
            if (!_toremove.contains(nameF)) {
                File temp = new File(nameF);
                if (!temp.exists()) {
                    state = temp.getName();
                }
            }
        }
        return state;
    }

    /** Print out the status of current branch and other branch.
     * * the current branch.*/
    void status() {
        String[] allbranch = new String[_branchmap.size()];
        int counter1 = 0;
        for (String temp : _branchmap.keySet()) {
            allbranch[counter1] = temp;
            counter1 += 1;
        }
        Arrays.sort(allbranch, 0, counter1);
        System.out.println("=== Branches ===");
        for (int i = 0; i < _branchmap.size(); i++) {
            if (allbranch[i].equals(getBranch().getname())) {
                System.out.println("*" + allbranch[i]);
            } else {
                System.out.println(allbranch[i]);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        String[] stagedfile = new String[_staging.size()];
        int counter2 = 0;
        for (String temp1 : _staging.keySet()) {
            stagedfile[counter2] = temp1;
            counter2 += 1;
        }
        Arrays.sort(stagedfile, 0, counter2);
        for (int k = 0; k < _staging.size(); k++) {
            System.out.println(stagedfile[k]);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        String[] removedfile = new String[_toremove.size()];
        int counter3 = 0;
        for (String temp2 : _toremove) {
            removedfile[counter3] = temp2;
            counter3 += 1;
        }
        Arrays.sort(removedfile, 0, counter3);
        for (int j = 0; j < _toremove.size(); j++) {
            System.out.println(removedfile[j]);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        File allfile = new File("./");
        for (File file : allfile.listFiles()) {
            if (!file.isDirectory()
                    && !file.getName().equals(".DS_Store")) {
                if (statushelper1(file).equals("modified")) {
                    System.out.println(file.getName() + " (modified)");
                }

            }
        }
        if (!statushelper2().equals("")) {
            System.out.println(statushelper2() + " (deleted)");
        }
        helper(allfile);
    }

    /** helper function.
     * @param allfile file.*/
    void helper(File allfile) {
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (File file : allfile.listFiles()) {
            if (!file.isDirectory()
                    && !file.getName().equals(".DS_Store")) {
                if (!_staging.containsKey(file.getName())
                        && !_far.getstore().containsKey(file.getName())) {
                    System.out.println(file.getName());
                }
            }
        }
        System.out.println();
    }

    /** helper funtion to print error.
     * @param branch Branch. */
    void helper2(String branch) {
        if (!_branchmap.containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (!_staging.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!_toremove.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (_branchmap.get(branch).getcurrent()) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /** Helper function.
     * @param branch branch.
     * @param branchTracking hashmap.
     * @param currTracking hashmap.
     * @param splitPoint hashmap. */
    void helper3(HashMap<String, String> currTracking,
                 HashMap<String, String> branchTracking,
                 Commit splitPoint, String branch) {
        Branch tempB = _branchmap.get(branch);
        for (String tem : branchTracking.keySet()) {
            if (!currTracking.containsKey(tem)) {
                File tempfile1 = new File(tem);
                if (tempfile1.exists()) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it or add it first.");
                    System.exit(0);
                }
            }
        }
        if (splitPoint.gethashid().equals(_far.gethashid())) {
            System.out.println("Current branch fast-forwarded.");
            for (String name : tempB.getbyfar().getstore().keySet()) {
                checkoutID(tempB.getbyfar().gethashid(), name);
            }
            for (String file : getBranch().getbyfar().getstore().keySet()) {
                if (!tempB.getbyfar().getstore().containsKey(file)) {
                    File tempfile = new File(file);
                    if (tempfile.exists()) {
                        boolean a = tempfile.delete();
                    }
                }
            }
            getBranch().changebyfar(_branchmap.get(branch).getbyfar());
            System.exit(0);
        } else if (splitPoint.gethashid().equals(_branchmap.
                get(branch).getbyfar().gethashid())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            _branchmap.get(branch).changebyfar(_far);
            System.exit(0);
        }
    }

    /** Helper function.
     * @param currTracking hashmap.
     * @param branchTracking hashmap.
     * @param branch branch.
     * @param isConflict boolean.
     * @param spTracking hashmap.
     * @return boolean a boolean value return. */
    boolean helper4(HashMap<String, String> currTracking,
                 HashMap<String, String> branchTracking,
                 String branch, boolean isConflict,
                 HashMap<String, String> spTracking) throws IOException {
        for (String str : branchTracking.keySet()) {
            if (!spTracking.containsKey(str)) {
                if (currTracking.containsKey(str)) {
                    if (!currTracking.get(str).
                            equals(branchTracking.get(str))) {
                        conflict(str, branch);
                        isConflict = true;
                    }
                } else if (!currTracking.containsKey(str)) {
                    checkoutID(_branchmap.get(branch).
                            getbyfar().gethashid(), str);
                    _staging.put(str, branchTracking.get(str));
                }
            }
        }
        return isConflict;
    }

    /** Merges files from the given branch
     * into the current branch.
     * @param branch branch. */
    void merge(String branch) throws IOException {
        boolean isConflict = false;
        helper2(branch);
        Commit head = _branchmap.get(branch).getbyfar();
        Commit splitPoint = splitpointFunc(_branchmap.get(branch));
        HashMap<String, String> currTracking = _far.getstore();
        HashMap<String, String> branchTracking = head.getstore();
        HashMap<String, String> spTracking = splitPoint.getstore();
        helper3(currTracking, branchTracking, splitPoint, branch);
        for (String fileName : spTracking.keySet()) {
            if (branchTracking.containsKey(fileName)
                    && currTracking.containsKey(fileName)) {
                if (!branchTracking.get(fileName).
                        equals(spTracking.get(fileName))
                        && currTracking.get(fileName).
                        equals(spTracking.get(fileName))) {
                    checkoutID(_branchmap.get(branch).
                            getbyfar().gethashid(), fileName);
                    _staging.put(fileName,
                            branchTracking.get(fileName));
                } else if (!branchTracking.get(fileName).
                        equals(spTracking.get(fileName))
                    && (!currTracking.get(fileName).
                        equals(spTracking.get(fileName)))) {
                    if (!currTracking.get(fileName).
                            equals(branchTracking.get(fileName))) {
                        conflict(fileName, branch);
                        isConflict = true;
                    }
                }
            }  else if (!branchTracking.containsKey(fileName)
                    && currTracking.containsKey(fileName)) {
                if (spTracking.get(fileName).
                        equals(currTracking.get(fileName))) {
                    File toDelete = new File(fileName);
                    if (toDelete.exists()) {
                        boolean b = toDelete.delete();
                    }
                    _toremove.add(fileName);
                } else {
                    conflict(fileName, branch);
                    isConflict = true;
                }
            } else if (branchTracking.containsKey(fileName)
                    && (!currTracking.containsKey(fileName))) {
                if (!spTracking.get(fileName).
                        equals(branchTracking.get(fileName))) {
                    conflict(fileName, branch);
                    isConflict = true;
                }
            }
        }
        isConflict = helper4(currTracking, branchTracking,
                branch, isConflict, spTracking);
        mergedFunc(branch, isConflict);
    }

    /** Function to deal with commit problem after merging.
     * @param conflict boolean.
     * @param targetBranch targetbranch. */
    void mergedFunc(String targetBranch, boolean conflict) {
        if (_staging.isEmpty() && _toremove.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {
            HashMap<String, String> currTracking
                    = new HashMap<String, String>();
            currTracking.putAll(_staging);
            for (String name : _far.getstore().keySet()) {
                if (!currTracking.containsKey(name)) {
                    String contents = _far.getstore().get(name);
                    currTracking.put(name, contents);
                }
            }
            for (String name : _far.getstore().keySet()) {
                if (_toremove.contains(name)) {
                    currTracking.remove(name);
                }
            }
            String msg = "Merged " + targetBranch
                    + " into " + getBranch().getname() + ".";
            String hashcode1 = _far.gethashid();
            String hashcode2 = _branchmap.get(targetBranch).
                    getbyfar().gethashid();
            Date now = new Date();
            Commit commit = new Commit(currTracking, hashcode1,
                    hashcode2, msg, now);
            _far.addchild(commit.gethashid());
            _commitmap.put(commit.gethashid(), commit);
            getBranch().changebyfar(commit);
            _far = commit;
            _staging.clear();
            _toremove.clear();

            if (conflict) {
                System.out.println("Encountered a merge conflict.");
            }
        }
    }

    /** Function to deal with conflict problem.
     * @param target string
     * @param filename name*/
    void conflict(String filename,
                        String target) throws IOException {
        String oldinfo = "";
        String newinfo = "";
        File toWrite = new File(filename);
        String content = "";
        String oldhash = _far.getcontent(filename);
        File readFrom = new File(".gitlet/.staging/" + oldhash);
        String newhash = _branchmap.get(target).
                getbyfar().getcontent(filename);
        File readTo = new File(".gitlet/.staging/" + newhash);
        content += ("<<<<<<< HEAD\n");
        if (readFrom.exists() && readTo.exists()) {
            oldinfo = Utils.readContentsAsString(readFrom);
            newinfo = Utils.readContentsAsString(readTo);
            content += oldinfo;
            content += "=======\n";
            content += newinfo;
        } else if (readFrom.exists()) {
            oldinfo = Utils.readContentsAsString(readFrom);
            content += oldinfo;
            content += "=======\n";
        } else if (readTo.exists()) {
            newinfo = Utils.readContentsAsString(readTo);
            content += "=======\n";
            content += newinfo;
        }
        content += (">>>>>>>\n");
        if (toWrite.exists()) {
            boolean t = toWrite.delete();
        }
        boolean a = toWrite.createNewFile();
        Utils.writeContents(toWrite, content);
        String work = Utils.readContentsAsString(toWrite);
        File staging = new File(".gitlet/.staging/" + Utils.sha1(work));
        boolean c = staging.createNewFile();
        Utils.writeContents(staging, work);
        _staging.put(filename, Utils.sha1(work));
    }

    /** Helper function to splitpoint in a merge process.
     * @param br branch.
     * @return Commit return a head. */
    Commit splitpointFunc(Branch br) {
        ArrayList<String> allparents = new ArrayList<>();
        String commit = br.getbyfar().gethashid();
        Commit temp = _commitmap.get(commit);
        Stack<Commit> tempstack = new Stack<Commit>();
        tempstack.push(temp);
        while (!tempstack.empty()) {
            Commit top = tempstack.pop();
            allparents.add(top.gethashid());
            for (String parent : top.getParents()) {
                if (!allparents.contains(parent) && (!parent.equals(""))) {
                    tempstack.push(_commitmap.get(parent));
                }
            }
        }
        LinkedList<Commit> theList = new LinkedList<Commit>();
        theList.add(_far);
        while (!theList.isEmpty()) {
            Commit top = theList.poll();
            if (allparents.contains(top.gethashid())) {
                return top;
            }
            ArrayList<String> allParents = top.getParents();
            for (String par : allParents) {
                theList.add(_commitmap.get(par));
            }
        }
        throw new GitletException("No split point found!");
    }

    /** Command to find all the parents of a commit.
     * A helper function for splitpoint finder.
     * @param commit Commit.
     * @return allparent Arraylist as return. */
    ArrayList<String> findParents(String commit) {
        ArrayList<String> allparent = new ArrayList<>();
        Commit temp = _commitmap.get(commit);
        Stack<Commit> curr = new Stack<Commit>();
        curr.push(temp);
        while (!curr.empty()) {
            Commit top = curr.pop();
            allparent.add(top.gethashid());
            for (String parent : top.getParents()) {
                if (!allparent.contains(parent) && (!parent.equals(""))) {
                    curr.push(_commitmap.get(parent));
                }
            }
        }
        return allparent;
    }

    /** Remote add method for ec.
     * @param filename string.
     * @param directory string. */
    void addRemote(String filename, String directory) {
        if (_remoteDir.containsKey(filename)) {
            System.out.println("A remote with that name already exists.");
            return;
        } else {
            _remoteDir.put(filename, directory);
        }
    }

    /** Deleting a remote.
     * @param filename string.*/
    void rmRemote(String filename) {
        if (!_remoteDir.containsKey(filename)) {
            System.out.println("A remote with that name does not exist.");
            return;
        } else {
            _remoteDir.remove(filename);
        }
    }


    /** Remote push method.
     * @param branchremote string.
     * @param remotename string. */
    void push(String remotename, String branchremote) throws IOException {
        File remoteFile = new File(_remoteDir.get(remotename));
        if (!remoteFile.exists()) {
            System.out.println("Remote directory not found.");
            return;
        } else {
            String address = _remoteDir.get(remotename)
                    + "/serialized";
            File gitletRepo = new File(address);
            Repo git = Utils.readObject(gitletRepo, Repo.class);
            Commit branchByfar = git
                    ._branchmap.get(branchremote).getbyfar();
            ArrayList<String> parents = findParents(_far.gethashid());
            if (!parents.contains(branchByfar.gethashid())) {
                System.out.println("Please pull down "
                        + "remote changes before pushing.");
                System.exit(0);
            } else {
                Commit temp = _far;
                while (!temp.gethashid().equals(branchByfar.gethashid())) {
                    git._commitmap.put(temp.gethashid(), temp);
                    for (String hash : temp.getstore().values()) {
                        File cFile =
                                new File("./.gitlet/.staging/" + hash);
                        File targetFile =
                                new File(_remoteDir.get(remotename)
                                        + "/.staging/" + hash);
                        String streaming = Utils.readContentsAsString(cFile);
                        if (!targetFile.exists()) {
                            boolean t = targetFile.createNewFile();
                        }
                        Utils.writeContents(targetFile, streaming);
                    }
                    temp = _commitmap.get(temp.getparent());
                }
                Branch newbranch =
                        new Branch(git.getBranch().getbyfar(), branchremote);
                git._branchmap.put(branchremote, newbranch);
                Commit now = git.
                        _commitmap.get(_far.gethashid());
                git._branchmap.
                        get(branchremote).changebyfar(now);
                git._far = git.
                        _branchmap.get(branchremote).getbyfar();
                Utils.writeObject(gitletRepo, git);
            }
        }
    }

    /** Fetch method for extra credit.
     * @param remotename string.
     * @param remoteBranch string. */
    void fetch(String remotename, String remoteBranch) throws IOException {
        String remote = _remoteDir.get(remotename);
        File file = new File(remote);
        if (!file.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        } else {
            File gitletRepo = new File(remote + "/serialized");
            Repo git = Utils.readObject(gitletRepo, Repo.class);
            if (!git._branchmap.containsKey(remoteBranch)) {
                System.out.println("That remote does not have that branch.");
                System.exit(0);
            } else {
                Commit currBranch = git
                        ._branchmap.get(remoteBranch).getbyfar();
                Commit temp = currBranch;
                while (!_commitmap.containsKey(temp.gethashid())) {
                    this._commitmap.put(temp.gethashid(), temp);
                    for (String hash : temp.getstore().values()) {
                        File tFile =
                                new File(remote + "/.staging/" + hash);
                        File currFile =
                                new File("./.gitlet/.staging/" + hash);
                        String streaming = Utils.readContentsAsString(tFile);
                        if (!currFile.exists()) {
                            boolean a = currFile.createNewFile();
                        }
                        Utils.writeContents(currFile, streaming);
                    }
                    temp = git._commitmap.get(temp.getparent());
                }
                String name = remotename + "/" + remoteBranch;
                Branch newbranch =
                        new Branch(_commitmap.
                                get(currBranch.gethashid()), name);
                this._branchmap.put(name, newbranch);
            }
        }
    }


    /** Remote Pull instruction.
     * @param remoteBranch string.
     * @param remotename string.*/
    void pull(String remotename, String remoteBranch) throws IOException {
        String branchname = remotename + "/" + remoteBranch;
        fetch(remotename, remoteBranch);
        merge(branchname);
    }
}
