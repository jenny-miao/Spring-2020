package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Jenny Miao
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
    Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

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


    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation permut, Alphabet alph) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, permut.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, permut.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, permut.invert(e));
            int ci = alph.toInt(c), ei = alph.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, permut.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, permut.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        Alphabet alph = getNewAlphabet();
        Permutation permut = getNewPermutation("", alph);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, permut, alph);
    }

    @Test
    public void testInvertChar() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        Permutation p2 = getNewPermutation("(BCA) (DF)",
                getNewAlphabet("ABCDEF"));
        Permutation p3 = getNewPermutation("(!@)(13)  ",
                getNewAlphabet("1!3@"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
        assertEquals('A', p2.invert('B'));
        assertEquals('D', p2.invert('F'));
        assertEquals('F', p2.invert('D'));
        assertEquals('!', p3.invert('@'));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        p.invert('F');
        p.permute('F');
    }

    @Test
    public void testPermuteChar() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        Permutation p2 = getNewPermutation("(BCA) (DF)",
                getNewAlphabet("ABCDEF"));
        Permutation p3 = getNewPermutation("(BCA)(DF)",
                getNewAlphabet("ABCDEF"));
        assertEquals('B', p.permute('D'));
        assertEquals('D', p.permute('C'));
        assertEquals('C', p2.permute('B'));
        assertEquals('B', p2.permute('A'));
        assertEquals('F', p2.permute('D'));
        assertEquals('C', p3.permute('B'));
        assertEquals('B', p3.permute('A'));
        assertEquals('F', p3.permute('D'));
        Permutation p4 = getNewPermutation("(!@)(13)", getNewAlphabet("1!3@"));
        assertEquals('!', p4.permute('@'));
    }

    @Test
    public void testDerangement() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCDE"));
        Permutation p2 = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        Permutation p3 = getNewPermutation("", getNewAlphabet("AB"));
        Permutation p5 = getNewPermutation("(!@)(13)", getNewAlphabet("1!3@"));
        Permutation p6 = getNewPermutation("(A)", getNewAlphabet("A"));
        assertFalse(p6.derangement());
        assertFalse(p.derangement());
        assertTrue(p2.derangement());
        assertFalse(p3.derangement());
        assertTrue(p5.derangement());
    }

    @Test
    public void testPermuteInt() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals(1, p.permute(3));
        assertEquals(3, p.permute(2));
        Permutation p2 = getNewPermutation("(BCA) (DF)",
                getNewAlphabet("ABCDEF"));
        assertEquals(2, p2.permute(1));
        assertEquals(1, p2.permute(0));
        assertEquals(5, p2.permute(3));
        Permutation p3 = getNewPermutation("(BCA)(DF)",
                getNewAlphabet("ABCDEF"));
        assertEquals(2, p3.permute(1));
        assertEquals(1, p3.permute(0));
        assertEquals(5, p3.permute(3));
        Permutation p4 = getNewPermutation("(!@)(13)",
                getNewAlphabet("1!3@"));
        assertEquals(0, p4.permute(2));
        assertEquals(1, p4.permute(-1));
    }

    @Test
    public void testInvertInt() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        Permutation p2 = getNewPermutation("(BCA) (DF)",
                getNewAlphabet("ABCDEF"));
        Permutation p3 = getNewPermutation("(BCA)(DF)",
                getNewAlphabet("ABCDEF"));
        assertEquals(1, p.invert(0));
        assertEquals(3, p.invert(1));
        assertEquals(0, p2.invert(1));
        assertEquals(3, p2.invert(5));
        assertEquals(5, p2.invert(3));
        assertEquals(0, p3.invert(1));
        assertEquals(3, p3.invert(5));
        assertEquals(5, p3.invert(3));
        Permutation p4 = getNewPermutation("(!@)(13)",
                getNewAlphabet("1!3@"));
        assertEquals(3, p4.invert(1));
        assertEquals(0, p4.invert(-2));
    }
}
