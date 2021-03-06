package lexer;
import bison.*;
/**
 * Class representing terminal (token).
 * This base class is used for one-character tokens (i.e. ; = < >).
 */
public class Token extends bison.wrappers.Token {
    private final Tag tag;

    public Token(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Token(" + tag + ")";
    }

    public Tag getTag() {
        return tag;
    }

    public String returnLexeme(){return null;}
}
