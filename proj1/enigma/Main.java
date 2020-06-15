package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Simon Zhan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }
        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        try {
            Machine machine = readConfig();
            String input = _input.nextLine();
            if (!input.startsWith("*")) {
                throw error("Wrong character to start with.");
            }
            setUp(machine, input);
            while (_input.hasNextLine()) {
                String newln = _input.nextLine();
                if (newln.startsWith("*")) {
                    setUp(machine, newln);
                } else {
                    printMessageLine(machine.convert(
                            newln.replaceAll(" ", "")));
                }
            }
        } catch (EnigmaException exception) {
            throw exception;
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.next();
            _alphabet = new Alphabet(alphabet);
            int numrotors = _config.nextInt();
            int pawls = _config.nextInt();
            _config.nextLine();
            ArrayList<Rotor> allrotors = new ArrayList<Rotor>();
            int i = 0;
            while (_config.hasNext() && _config.hasNextLine()) {
                String information = _config.nextLine();
                Scanner checking = new Scanner(information);
                if (checking.next().startsWith("(")) {
                    allrotors.get(i - 1).permutation().addCycle(information);
                } else {
                    allrotors.add(readRotor(information));
                    i += 1;
                }

            }
            return new Machine(_alphabet, numrotors, pawls, allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }

    }

    /** Return a rotor, reading its description from _config.
     * @param information  The string input to form a Rotor. */
    private Rotor readRotor(String information) {
        String rotorName;
        String rotorInfo;
        String rotorType;
        String rotorNotches;
        String rotorCycle;
        try {
            Scanner readingRotor = new Scanner(information);
            rotorName = readingRotor.next().toUpperCase();
            rotorInfo = readingRotor.next();
            rotorType = rotorInfo.substring(0, 1);
            rotorNotches = rotorInfo.substring(1);
            rotorCycle = readingRotor.next();
            if (!rotorCycle.substring(rotorCycle.length() - 1).equals(")")) {
                throw error(
                        "The ending of cycle should be a closed permutation.");
            }
            while (readingRotor.hasNext()) {
                rotorCycle += (" " + readingRotor.next());
                if (!rotorCycle.
                        substring(rotorCycle.length() - 1).equals(")")) {
                    throw error(
                            "Cycle should be a closed permutation.");
                }
            }
            if (rotorType.equals("M")) {
                MovingRotor m = new MovingRotor(rotorName,
                        new Permutation(rotorCycle, _alphabet), rotorNotches);
                return m;
            } else if (rotorType.equals("N")) {
                FixedRotor f = new FixedRotor(rotorName,
                        new Permutation(rotorCycle, _alphabet));
                return f;
            }
            Reflector re = new Reflector(rotorName,
                    new Permutation(rotorCycle, _alphabet));
            return re;

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Helper function for the setup method.
     * @param M Machine passed in.
     * @param rotorsName String Array of all the rotor name list.
     * @param setup Scanner used to scan each line.*/
    void helper(Machine M, String[] rotorsName, Scanner setup) {
        for (int i = 0; i < rotorsName.length; i++) {
            String nextrotorname = setup.next().toUpperCase();
            boolean flag = false;
            for (Rotor rotor : M.giveRotor()) {
                if (rotor.name().equals(nextrotorname)) {
                    if (i == 0 && !rotor.reflecting()) {
                        throw error("Rotor should be a reflector.");
                    } else if (i == M.numRotors() - M.numPawls() - 1
                            && rotor.rotates()) {
                        throw error(
                                "Number of fixed rotor is false.");
                    } else {
                        rotorsName[i] = nextrotorname;
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                throw error("Rotor not in the list.");
            }
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        try {
            Scanner setup = new Scanner(settings);
            setup.next();
            String[] rotorsName = new String[M.numRotors()];
            helper(M, rotorsName, setup);
            for (int k = 0; k < M.numRotors() - 1; k++) {
                for (int j = k + 1; j < M.numRotors(); j++) {
                    if (rotorsName[k].equals(rotorsName[j])) {
                        throw error("Repeated rotor input into Machine.");
                    }
                }
            }
            String setting = setup.next();
            String setring = ""; String plugboard = "";
            if (setting.length() != M.numRotors() - 1) {
                throw error("Wrong setting about rotors");
            }
            for (Rotor rotor : M.giveRotor()) {
                if (setting.equals(rotor.name())) {
                    throw error("Wrong number of rotors have been set.");
                }
            }
            if (setup.hasNext()) {
                String line = setup.next();
                if (line.substring(0, 1).equals("(")) {
                    plugboard = line;
                    while (setup.hasNext()) {
                        plugboard += " " + setup.next();
                    }
                } else {
                    setring = line;
                    if (setup.hasNext()) {
                        plugboard = setup.next();
                        while (setup.hasNext()) {
                            plugboard += " " + setup.next();
                        }
                    }
                }
            }
            M.insertRotors(rotorsName);
            if (setring.equals("")) {
                M.setRotors(setting);
            } else {
                M.setRotors(setting);
                M.setboth(setring);
            }
            if (!plugboard.equals("")) {
                M.setPlugboard(new Permutation(plugboard, _alphabet));
            }
        } catch (EnigmaException exception) {
            throw exception;
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String result = "";
        String[] message = msg.split("");
        for (int i = 0; i < message.length; i++) {
            if (i % 5 == 0 && i > 0) {
                result += " ";
            }
            result += message[i];
        }
        _output.println(result);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
