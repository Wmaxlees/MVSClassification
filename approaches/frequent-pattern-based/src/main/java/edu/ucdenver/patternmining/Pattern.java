package edu.ucdenver.patternmining;

import java.io.IOException;
import java.util.Arrays;

/**
 * The class to hold information about a single pattern
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/10/17.
 */
public class Pattern implements IPattern, Comparable<Pattern> {
    private int[] symbols;

    public Pattern (int[] symbols) {
        this.symbols = symbols;
    }

    @Override
    public int hashCode () {
        return Arrays.hashCode(this.symbols);
    }

    @Override
    public boolean equals (Object other) {
        return other.getClass().equals(this.getClass()) &&
                Arrays.equals(this.symbols, ((Pattern)other).symbols);
    }

    /**
     * Check if other can be appended to this pattern. Will fail if
     * the tail section is larger than the head section.
     *
     * @param other
     * @return
     */
    public boolean isHorizontallyJoinableWith (Pattern other) {
        int[] thisBody = this.getBody();
        int[] otherBody = other.getBody();

        if (otherBody.length != thisBody.length) {
            return false;
        }

        return Arrays.equals(thisBody, otherBody);
    }

    /**
     * Appends the tail of other to this pattern.
     *
     * WARNING: Doesn't check if they are actually joinable first
     *
     * @param other
     * @return
     */
    public Pattern horizontalJoin (Pattern other) {
        int[] newPattern = Arrays.copyOf(this.symbols, this.symbols.length+1);
        newPattern[this.symbols.length] = other.getTail();

        return new Pattern(newPattern);
    }

    private int getTail () {
        return this.symbols[this.symbols.length-1];
    }

    private int[] getBody () {
        // Handle if there is only one or two symbols
        if (this.symbols.length < 3) {
            return new int[]{};
        }

        return Arrays.copyOfRange(this.symbols, 1, this.symbols.length-2);
    }

    @Override
    public String toString () {
        String result = "";

        for (int symbol : this.symbols) {
            result += symbol + " ";
        }

        return result.trim();
    }

    @Override
    public int compareTo (Pattern other) {
        if (this.symbols.length != other.symbols.length) {
            return this.symbols.length - other.symbols.length;
        }

        for (int i = 0; i < this.symbols.length; ++i) {
            if (this.symbols[i] != other.symbols[i]) {
                return this.symbols[i] - other.symbols[i];
            }
        }

        return 0;
    }
}
