package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jenny Miao
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
        Machine m = readConfig();
        if (!_input.hasNext("\\*")) {
            throw error("string configuration must start with *");
        }
        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            if (line.equals("")) {
                _output.println();
            }
            if (line.contains("*")) {
                setUp(m, line);
                String msg = new String("");
                while (_input.hasNext("[^\\*]*")) {
                    msg = _input.nextLine().replaceAll("\\s+", "");
                    String convertedMsg = m.convert(msg);
                    printMessageLine(convertedMsg);
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (_config.hasNext("[^\\*\\(\\)]+")) {
                _alphabet = new Alphabet(_config.next());
            } else {
                throw error("invalid alphabet input");
            }

            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            Collection<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            if (name.contains("*") || name.contains("(")
                    || name.contains(")")) {
                throw error("name contains invalid chars");
            }
            String settings = _config.next();
            if (settings.contains("*") || settings.contains("(")
                    || settings.contains(")")) {
                throw error("settings contains invalid chars");
            }
            String perms = new String("");
            while (_config.hasNext("\\([^\\*]+\\)")) {
                perms = perms.concat(_config.next("\\([^\\*]+\\)"));
            }
            Rotor rotor = new Rotor(name, new Permutation(perms, _alphabet));
            if (settings.charAt(0) == 'M') {
                String notches = settings.substring(1);
                rotor = new MovingRotor(name,
                        new Permutation(perms, _alphabet), notches);
            } else if (settings.charAt(0) == 'N') {
                rotor =  new FixedRotor(name,
                        new Permutation(perms, _alphabet));
            } else if (settings.charAt(0) == 'R') {
                rotor = new Reflector(name,
                        new Permutation(perms, _alphabet));
            }
            return rotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw error("Setting must start with an asterisk.");
        }
        String[] rotors = settings.split("\\s");
        if (rotors.length <= M.numRotors() + 1) {
            throw error("wrong number of settings");
        }
        String[] useRotors = new String[M.getNumRotors()];
        int index = 0;
        for (int i = 1; i <= M.numRotors(); i++) {
            if (rotors[i].charAt(0) != '(' && rotors[i].charAt(0) != '*') {
                useRotors[index] = rotors[i];
                index++;
            }
        }
        M.insertRotors(useRotors);
        if (M.numRotors() - 1 < M.numPawls()) {
            throw error("Too many pawls");
        }
        String rotorSetting = rotors[M.numRotors() + 1];
        if (rotorSetting.length() != M.numRotors() - 1) {
            throw error("rotor setting string does not match number of rotors");
        }
        M.setRotors(rotorSetting);
        ArrayList<String> seenRotors = new ArrayList<String>();
        for (int i = 0; i < useRotors.length; i++) {
            if (seenRotors.contains(useRotors[i])) {
                throw error("Can't have repeated rotors in the setting");
            } else {
                seenRotors.add(useRotors[i]);
            }
        }
        if (!M.getActiveRotors().get(0).reflecting()) {
            throw error("First rotor must be a reflector");
        }
        for (int i = 1; i < M.numRotors(); i++) {
            if (M.getActiveRotors().get(i).reflecting()) {
                throw error("Reflectors can only be the first rotor");
            }
        }
        String plugPerm = new String("");
        for (int i = M.numRotors() + 2; i < rotors.length; i++) {
            if (i == M.numRotors() + 2 && !rotors[i].contains("(")) {
                ring(M, rotors[i]);
            } else {
                plugPerm = plugPerm.concat(rotors[i]);
            }
        }
        M.setPlugboard(new Permutation(plugPerm, _alphabet));
    }

    /** Extra credit for implementation of Ring.
     * Takes in the string of RING and machine M. */
    private void ring(Machine M, String ring) {
        ArrayList<Rotor> rotors = M.getActiveRotors();
        for (int i = 1; i < M.getNumRotors(); i++) {
            int ringIndex = M.alphabet().toInt(ring.charAt(i - 1));
            M.getActiveRotors().get(i).setRing(ringIndex);
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String newMsg = msg.replaceAll("\\s+", "");
        if (newMsg.length() <= 5) {
            _output.println(newMsg);
        } else {
            _output.print(newMsg.charAt(0));
            for (int i = 1; i < newMsg.length(); i++) {
                if (i % 5 == 0) {
                    _output.print(" ");
                    _output.print(newMsg.charAt(i));
                } else {
                    _output.print(newMsg.charAt(i));
                }
            }
            _output.println();
        }

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
