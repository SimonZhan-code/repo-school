package enigma;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

/** The suite of all unit testing of Machine Class.
 * @author Simon Zhan */

public class MachineTesting {
    public Alphabet _alph = new Alphabet();
    public Rotor first = new MovingRotor("First",
            new Permutation("(AELTPHQXRU) (BKNW) (CMOY) "
                    + "(DFG) (IV) (JZ) (S)", _alph), "Q");
    public Rotor second = new MovingRotor("Second",
            new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) "
                    + "(BJ) (GR) (NT) (A) (Q)", _alph), "E");
    public Rotor third = new FixedRotor("Third",
            new Permutation("(ALBEVFCYODJWUGNMQTZSKPR)", _alph));
    public Rotor last = new Reflector("Last",
            new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) "
                    + "(HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", _alph));
    ArrayList<Rotor> allRo = new ArrayList<Rotor>();


    @Test
    public void checkAdvance() {
        allRo.add(first);
        allRo.add(second);
        allRo.add(third);
        allRo.add(last);
        Machine newMachine = new Machine(_alph, 4, 2, allRo);
        String[] rolist = {"Last", "Third", "Second", "First"};
        newMachine.insertRotors(rolist);
        newMachine.setRotors("AAQ");
        newMachine.moveadvance();
        assertEquals(17, first.setting());
        assertEquals(1, second.setting());
        assertEquals(0, third.setting());
    }

    @Test
    public void checkTranslate() {
        allRo.add(first);
        allRo.add(second);
        allRo.add(third);
        allRo.add(last);
        Machine newMachine = new Machine(_alph, 4, 2, allRo);
        String[] rolist = {"Last", "Third", "Second", "First"};
        newMachine.insertRotors(rolist);
        newMachine.setRotors("AAQ");
        assertEquals("X", newMachine.convert("H"));
    }

    @Test
    public void checkPlugboard() {
        allRo.add(first);
        allRo.add(second);
        allRo.add(third);
        allRo.add(last);
        Machine newMachine = new Machine(_alph, 4, 2, allRo);
        String[] rolist = {"Last", "Third", "Second", "First"};
        newMachine.insertRotors(rolist);
        newMachine.setRotors("AAQ");
        newMachine.setPlugboard(new Permutation("(XP)", _alph));
        assertEquals("P", newMachine.convert("H"));
        assertEquals("K", newMachine.convert("P"));
    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new Alphabet("ABCD");
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);

        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('g');
        mach.convert('b');
        mach.convert('e');
        assertEquals("AABA", getSetting(ac, machineRotors));
    }



    /** Helper method to get the String
     * representation of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }

}
