package edu.ucdenver.patternmining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

/**
 * A container to hold MultivariatePatterns
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/24/17
 */
public class MVSPatternSet {
    TreeSet<MultivariatePattern> patterns;

    public MVSPatternSet () {
        this.patterns = new TreeSet<>();
    }

    public void addPattern (MultivariatePattern pattern) {
        this.patterns.add(pattern);
    }

    public void addSet (TreeSet<MultivariatePattern> newPatterns) {
        this.patterns.addAll(newPatterns);
    }

    public void addPatterns (TreeSet<Pattern> newPatterns, int sequence) {
        for (Pattern pattern : newPatterns) {
            this.patterns.add(new MultivariatePattern(pattern, sequence));
        }
    }

    public void appendToFile (File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        for (MultivariatePattern pattern : this.patterns) {
            writer.write(pattern.toString() + ",");
            writer.newLine();
        }

        writer.close();
    }
}
