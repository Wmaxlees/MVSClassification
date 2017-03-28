package edu.ucdenver.patternmining;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A multivariate pattern that contains several Pattern objects
 * as well as their indices within the containing MVS
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/24/17
 */
public class MultivariatePattern implements Comparable<MultivariatePattern> {
    private TreeSet<PatternPair> patterns;

    public MultivariatePattern () {
        this.patterns = new TreeSet<>();
    }

    public MultivariatePattern (Pattern pattern, int sequence) {
        this.patterns = new TreeSet<>();
        this.patterns.add(new PatternPair(pattern, sequence));
    }

    public void addPattern (Pattern pattern, int sequence) {
        this.patterns.add(new PatternPair(pattern, sequence));
    }

    public void addPatterns (MultivariatePattern other) {
        this.patterns.addAll(other.patterns);
    }

    @Override
    public String toString () {
        String result = "{";

        for (PatternPair pattern : patterns) {
            result += pattern.toString() + " ";
        }

        return result.trim() + "}";
    }

    @Override
    public int compareTo (MultivariatePattern other) {
        if (this.patterns.size() != other.patterns.size()) {
            return this.patterns.size() - other.patterns.size();
        }

        Iterator<PatternPair> thisIt = this.patterns.iterator();
        Iterator<PatternPair> otherIt = other.patterns.iterator();
        while (thisIt.hasNext()) {
            int comparison = thisIt.next().compareTo(otherIt.next());
            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }

    private class PatternPair implements Comparable<PatternPair> {
        public Pattern pattern;
        public int sequence;

        public PatternPair (Pattern pattern, int sequence) {
            this.pattern = pattern;
            this.sequence = sequence;
        }

        @Override
        public int compareTo (PatternPair other) {
            if (other.sequence != this.sequence) {
                return this.sequence - other.sequence;
            }

            return this.pattern.compareTo(other.pattern);
        }

        @Override
        public String toString () {
            return sequence + "-" + pattern.toString();
        }
    }
}
