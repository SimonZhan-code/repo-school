package gitlet;

import java.io.File;
import java.io.IOException;


/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Simon Zhan
 */
public class Main {
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        if (args[0].equals("init")) {
            if (args.length > 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            File directory = new File(".gitlet");
            if (directory.isDirectory() && directory.exists()) {
                System.out.println("A gitlet version-control "
                        + "system already exists"
                        + " in the current directory.");
            } else {
                directory.mkdir();
                Repo git = new Repo();
                Repo.save(git);
            }
        } else if (args[0].equals("add")) {
            add(args);
        } else if (args[0].equals("commit")) {
            commit(args);
        } else if (args[0].equals("log")) {
            log(args);
        } else if (args[0].equals("global-log")) {
            globallog(args);
        } else if (args[0].equals("find")) {
            find(args);
        } else if (args[0].equals("checkout")) {
            checkout(args);
        } else if (args[0].equals("status")) {
            status(args);
        } else if (args[0].equals("branch")) {
            branch(args);
        } else if (args[0].equals("rm")) {
            rm(args);
        } else if (args[0].equals("rm-branch")) {
            rmbranch(args);
        } else if (args[0].equals("reset")) {
            reset(args);
        } else if (args[0].equals("merge")) {
            merge(args);
        } else if (args[0].equals("pull")) {
            pull(args);
        } else if (args[0].equals("fetch")) {
            fetch(args);
        } else if (args[0].equals("push")) {
            push(args);
        } else if (args[0].equals("add-remote")) {
            addremote(args);
        } else if (args[0].equals("rm-remote")) {
            rmRemote(args);
        } else {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }

    /** rm-remote method.
     * @param args file. */
    static void rmRemote(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String remotename = args[1];
        Repo git = Repo.read();
        git.rmRemote(remotename);
        Repo.save(git);
    }

    /** add-remote method.
     * @param args file. */
    static void addremote(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String remotename = args[1];
        String dir = args[2];
        Repo git = Repo.read();
        git.addRemote(remotename, dir);
        Repo.save(git);
    }

    /** push method.
     * @param args file. */
    static void push(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String remotename = args[1];
        String remotebranch = args[2];
        Repo git = Repo.read();
        git.push(remotename, remotebranch);
        Repo.save(git);
    }

    /** fetch method.
     * @param args file. */
    static void fetch(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String remotename = args[1];
        String remotebranch = args[2];
        Repo git = Repo.read();
        git.fetch(remotename, remotebranch);
        Repo.save(git);
    }

    /** Pull method.
     * @param args file.*/
    static void pull(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String remotename = args[1];
        String remotebranch = args[2];
        Repo git = Repo.read();
        git.pull(remotename, remotebranch);
        Repo.save(git);
    }

    /** Merge method implimentation.
     * @param args file.*/
    static void merge(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String temp = args[1];
            git.merge(temp);
            Repo.save(git);
        }
    }

    /** reset method implimentation.
     * @param args file.*/
    static void reset(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String temp = args[1];
            git.reset(temp);
            Repo.save(git);
        }
    }

    /** rm-branch method implimentation.
     * @param args file.*/
    static void rmbranch(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String temp = args[1];
            git.rmBranch(temp);
            Repo.save(git);
        }
    }

    /** rm method implimentation.
     * @param args file.*/
    static void rm(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String temp = args[1];
            git.rm(temp);
            Repo.save(git);
        }
    }

    /** branch method implimentation.
     * @param args file.*/
    static void branch(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String temp = args[1];
            git.branch(temp);
            Repo.save(git);
        }
    }



    /** status method implimentation.
     * @param args file. */
    static void status(String[] args) {
        if (args.length > 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            git.status();
            Repo.save(git);
        }
    }

    /** Checkout methods.
     * @param args file.*/
    static void checkout(String[] args) {
        int check = args.length - 1;
        if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else if (check == 2) {
            String temp1 = args[1];
            String temp2 = args[2];
            if (!temp1.equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            Repo git = Repo.read();
            git.checkoutfile(temp2);
            Repo.save(git);
        } else if (check == 3) {
            String commitid = args[1];
            String temp2 = args[2];
            String filename = args[3];
            if (!temp2.equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            Repo git = Repo.read();
            git.checkoutID(commitid, filename);
            Repo.save(git);
        } else if (check == 1) {
            String temp1 = args[1];
            Repo git = Repo.read();
            git.checkoutbranch(temp1);
            Repo.save(git);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** find method implimentation.
     * @param args file. */
    static void find(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String op = args[1];
            git.find(op);
            Repo.save(git);
        }
    }

    /** global log method implimentation.
     * @param args file. */
    static void globallog(String[] args) {
        if (args.length > 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            git.globalLog();
            Repo.save(git);
        }
    }

    /** log method implimentation.
     * @param args file. */
    static void log(String[] args) {
        if (args.length > 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            git.log();
            Repo.save(git);
        }
    }

    /** commit method implimentation.
     * @param args file. */
    static void commit(String[] args) throws IOException {
        if (args.length == 1 || args[1].equals("")) {
            System.out.println("Please enter a commit messages.");
        } else if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String temp = args[1];
            git.commit(temp);
            Repo.save(git);
        }
    }

    /** add method implimentation.
     * @param args file. */
    static void add(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (!isInitialize()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            Repo git = Repo.read();
            String filename = args[1];
            git.add(filename);
            Repo.save(git);
        }
    }

    /** Returns true if there exists .gitlet .
     * @return boolean return. */
    static boolean isInitialize() {
        File temp = new File("./.gitlet");
        return temp.exists();
    }
}

