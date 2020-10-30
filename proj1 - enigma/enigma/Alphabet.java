package enigma;

import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Jenny Miao
 */
class Alphabet {

    /** A new alphabet containing CHARACTERS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String characters) {
        this.chars = characters;
        if (chars.length() == 0) {
            throw error("alphabet is empty", chars);
        }
        for (int i = 0; i < chars.length(); i++) {
            for (int j = i + 1; j < chars.length(); j++) {
                if (chars.charAt(i) == chars.charAt(j)) {
                    throw error("duplicate characters in alphabet", chars);
                } else if (chars.charAt(i) == '*' || chars.charAt(i) == '('
                        || chars.charAt(i) == ')') {
                    throw error("illegal inputs *() in alphabet", chars);
                }
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return chars.indexOf(ch) >= 0;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw error("Index out of bounds", index);
        }
        return chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!this.contains(ch)) {
            throw error("Character not in alphabet", ch);
        }
        return chars.indexOf(ch);
    }
    /** The string of chars this Alphabet contains. */
    private String chars;

    /** Returns chars of this Alphabet. */
    String getChars() {
        return chars;
    }
}
