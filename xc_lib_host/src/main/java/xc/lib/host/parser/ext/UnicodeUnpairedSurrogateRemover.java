package xc.lib.host.parser.ext;

import java.io.IOException;
import java.io.Writer;

public class UnicodeUnpairedSurrogateRemover  extends CodePointTranslator {
    /**
     * Implementation of translate that throws out unpaired surrogates.
     * {@inheritDoc}
     */
    @Override
    public boolean translate(int codepoint, Writer out) throws IOException {
        if (codepoint >= Character.MIN_SURROGATE && codepoint <= Character.MAX_SURROGATE) {
            // It's a surrogate. Write nothing and say we've translated.
            return true;
        } else {
            // It's not a surrogate. Don't translate it.
            return false;
        }
    }
}
