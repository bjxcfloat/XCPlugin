package xc.lib.host.parser.ext;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

public class LookupTranslator extends CharSequenceTranslator {

    private final HashMap<String, CharSequence> lookupMap;
    private final int shortest;
    private final int longest;

    /**
     * Define the lookup table to be used in translation
     * <p>
     * Note that, as of Lang 3.1, the key to the lookup table is converted to a
     * java.lang.String, while the value remains as a java.lang.CharSequence.
     * This is because we need the key to support hashCode and equals(Object),
     * allowing it to be the key for a HashMap. See LANG-882.
     *
     * @param lookup CharSequence[][] table of size [*][2]
     */
    public LookupTranslator(final CharSequence[]... lookup) {
        lookupMap = new HashMap<>();
        int shortest = Integer.MAX_VALUE;
        int longest = 0;
        if (lookup != null) {
            for (final CharSequence[] seq : lookup) {
                this.lookupMap.put(seq[0].toString(), seq[1]);
                final int sz = seq[0].length();
                if (sz < shortest) {
                    shortest = sz;
                }
                if (sz > longest) {
                    longest = sz;
                }
            }
        }
        this.shortest = shortest;
        this.longest = longest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        int max = longest;
        if (index + longest > input.length()) {
            max = input.length() - index;
        }
        // descend so as to get a greedy algorithm
        for (int i = max; i >= shortest; i--) {
            final CharSequence subSeq = input.subSequence(index, index + i);
            final CharSequence result = lookupMap.get(subSeq.toString());
            if (result != null) {
                out.write(result.toString());
                return i;
            }
        }
        return 0;
    }
}
