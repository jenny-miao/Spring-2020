package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Alphabet class.
 *  @author Jenny Miao
 */
public class AlphabetTest {
    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Alphabet letters;
    private String alpha = UPPER_STRING;

    /**
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
    Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet()
     *
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
    Alphabet getNewAlphabet() {
        return new Alphabet();
    }

    /* ***** TESTS ***** */
    @Test(expected = EnigmaException.class)
    public void testDuplicateAlphabet() {
        Alphabet a = getNewAlphabet("ABCA");
    }

    @Test(expected = EnigmaException.class)
    public void testNullAlphabet() {
        Alphabet a = getNewAlphabet("");
    }

    @Test(expected = EnigmaException.class)
    public void testInvalidAlphabet() {
        Alphabet a = getNewAlphabet("AB*C)");
    }

    @Test
    public void testAlphabetSize() {
        Alphabet a = getNewAlphabet("ABCD");
        Alphabet b = getNewAlphabet("O");
        Alphabet c = getNewAlphabet("!@H");
        assertEquals(4, a.size());
        assertEquals(1, b.size());
        assertEquals(3, c.size());
    }

    @Test
    public void testAlphabetContains() {
        Alphabet a = getNewAlphabet("ABCD");
        Alphabet b = getNewAlphabet("O");
        Alphabet c = getNewAlphabet("!@H");
        assertTrue(a.contains('D'));
        assertFalse(a.contains('E'));
        assertTrue(b.contains('O'));
        assertFalse(b.contains('!'));
        assertTrue(c.contains('@'));
        assertFalse(c.contains('.'));
    }

    @Test(expected = EnigmaException.class)
    public void testOutOfBoundsToChar() {
        Alphabet a = getNewAlphabet("ABC");
        a.toChar(4);
    }

    @Test(expected = EnigmaException.class)
    public void testNonExistentChar() {
        Alphabet a = getNewAlphabet("ABC");
        a.toInt('D');
    }

    @Test
    public void testAlphabetToChar() {
        Alphabet a = getNewAlphabet("ABCD");
        Alphabet b = getNewAlphabet("O");
        Alphabet c = getNewAlphabet("!@H");
        assertEquals('A', a.toChar(0));
        assertFalse(a.contains('E'));
        assertTrue(b.contains('O'));
        assertFalse(b.contains('!'));
        assertTrue(c.contains('@'));
        assertFalse(c.contains('.'));
    }

    @Test
    public void testAlphabetToInt() {
        Alphabet a = getNewAlphabet("ABCD");
        Alphabet b = getNewAlphabet("O");
        Alphabet c = getNewAlphabet("!@H");
        assertEquals(1, a.toInt('B'));
        assertEquals(0, a.toInt('A'));
        assertEquals(0, b.toInt('O'));
        assertEquals(0, c.toInt('!'));
        assertEquals(1, c.toInt('@'));
    }
}
