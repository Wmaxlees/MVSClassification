package edu.ucdenver.patternmining;

import edu.ucdenver.data.DataClass;

import java.io.*;
import java.util.*;

/**
 * A class that contains a set of patterns and their
 * locations in the MVS
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/10/17.
 */
public class HorizontalPatternSet {
    private Pattern[][][][] patterns;
    private HashMap<Pattern, ArrayList<Index>> indices;
    private int currentPatternSize;
    private int minimumSupport;
    private int maxPatternSize;
    private int numberOfInstances;
    private int numberOfSequences;

    /**
     * Create a new pattern set
     *
     * @param maxPatternSize The maximum pattern size
     * @param numberOfInstances The number of instances in the data class
     * @param numberOfSequences The number of sequences in the data set
     * @param numberOfFrames The number of frames in the largest data class instance
     */
    public HorizontalPatternSet (int maxPatternSize, int numberOfInstances, int numberOfSequences, int numberOfFrames, int minimumSupport) {
        this.patterns = new Pattern[maxPatternSize+1][numberOfInstances][numberOfSequences][numberOfFrames];
        this.indices = new HashMap<>();

        this.currentPatternSize = 0;
        this.minimumSupport = minimumSupport;
        this.maxPatternSize = maxPatternSize;
        this.numberOfInstances = numberOfInstances;
        this.numberOfSequences = numberOfSequences;
    }

    public void generateAllHorizontalPatterns () {
        for (int i = 0; i < this.maxPatternSize; ++i) {
            if (!this.generateNextSetOfPatterns() || !this.cullPatternsWithLowSupport()) {
                return;
            }
        }
    }

    /**
     * Add a pattern to the pattern set
     *
     * @param pattern A pattern to add to the set
     * @param metadata The metadata about the pattern
     */
    private void addPattern (Pattern pattern, Metadata metadata) {
        this.patterns[metadata.getSizeOfPattern()]
                     [metadata.getInstance()]
                     [metadata.getSequence()]
                     [metadata.getFrame()] = pattern;

        ArrayList<Index> indexList;
        if (this.indices.containsKey(pattern)) {
            indexList = this.indices.get(pattern);
            this.indices.remove(pattern);
        } else {
            indexList = new ArrayList<>();
        }
        indexList.add(new Index(metadata));
        this.indices.put(pattern, indexList);
    }

    /**
     * Remove the patterns with a low support
     */
    private boolean cullPatternsWithLowSupport () {
        boolean patternsLeft = false;

        // Delete all patterns without minimum support
        ArrayList<Pattern> toDelete = new ArrayList<>();
        this.indices.forEach( (pattern, indices) -> {
            if (indices.size() < this.minimumSupport) {
                for (Index index : indices) {
                    this.patterns[this.currentPatternSize][index.getInstance()][index.getSequence()][index.getFrame()] = null;
                    toDelete.add(pattern);
                }
            }
        });
        for (Pattern pattern : toDelete) {
            this.indices.remove(pattern);
        }

        if (!this.indices.isEmpty()) {
            patternsLeft = true;
        }
        this.indices = new HashMap<>();

        return patternsLeft;
    }

    /**
     * Generate the patterns that are one token long and meet the minimum
     * support required to be a 'frequent' pattern
     *
     * @param dc The blocked mvs
     */
    public void initialize (DataClass dc) {
        this.currentPatternSize = 0;

        for (int instance = 0; instance < dc.getNumberOfInstances(); ++instance) {
            int[][] mvs = dc.getInstance(instance).getLayer(0).toIntArray2();

            // Get all possible patterns of size 1
            for (int sequence = 0; sequence < mvs.length; ++sequence) {
                for (int frame = 0; frame < mvs[0].length; ++frame) {
                    Metadata metadata = new Metadata(this.currentPatternSize, instance, sequence, frame);
                    Pattern pattern = new Pattern(new int[]{mvs[sequence][frame]});

                    this.addPattern(pattern, metadata);
                }
            }
        }

        // Remove low support patterns
        this.cullPatternsWithLowSupport();
    }

    private boolean generateNextSetOfPatterns () {
        boolean patternFound = false;

        for (int instance = 0; instance < this.patterns[this.currentPatternSize].length; ++instance) {
            for (int sequence = 0; sequence < this.patterns[this.currentPatternSize][instance].length; ++sequence) {
                for (int frame = 0; frame < this.patterns[this.currentPatternSize][instance][sequence].length; ++frame) {
                    Pattern headPattern = this.patterns[this.currentPatternSize][instance][sequence][frame];
                    if (headPattern == null) {
                        continue;
                    }

                    int end = Math.min(this.patterns[this.currentPatternSize][instance][sequence].length, this.maxPatternSize+frame);
                    for (int tail = frame+1; tail < end; ++tail) {
                        Pattern tailPattern = this.patterns[this.currentPatternSize][instance][sequence][tail];
                        if (tailPattern != null && headPattern.isHorizontallyJoinableWith(tailPattern)) {
                            patternFound = true;
                            Metadata metadata = new Metadata(this.currentPatternSize+1, instance, sequence, frame);
                            this.addPattern(headPattern.horizontalJoin(tailPattern), metadata);
                        }
                    }
                }
            }
        }

        ++this.currentPatternSize;

        return patternFound;
    }

    public TreeSet<Pattern> gatherPatterns (int instance, int sequence, int size) {
        TreeSet<Pattern> result = new TreeSet();

        for (int frame = 0; frame < this.patterns[size][instance][sequence].length; ++frame) {
            Pattern pattern = this.patterns[size][instance][sequence][frame];

            if (pattern != null) {
                result.add(pattern);
            }
        }

        return result;
    }

    public void writeAsMVSPatternSet (File file) throws IOException {
        for (int size = 0; size < this.maxPatternSize; ++size) {
            for (int i = 0; i < this.numberOfInstances; ++i) {
                System.out.println("Instance: " + i);
                MVSPatternSet patterns = this.getAllPatternsInRange(i, size, 0, this.numberOfSequences - 1);
                patterns.appendToFile(file);
            }
        }
    }

    private MVSPatternSet getAllPatternsInRange (int instance, int size, int first, int last) {
        MVSPatternSet result = new MVSPatternSet();

        // Base case
        if (first == last) {
            TreeSet<Pattern> patterns = this.gatherPatterns(instance, first, size);
            result.addPatterns(patterns, first);

        } else {
            MVSPatternSet subPatterns = this.getAllPatternsInRange(instance, size, first, last-1);
            MVSPatternSet tailPatterns = this.getAllPatternsInRange(instance, size, last, last);
            result.addSet(subPatterns.patterns);
            result.addSet(tailPatterns.patterns);
            result.addSet(this.combine(subPatterns, tailPatterns).patterns);
        }

        return result;
    }

    private MVSPatternSet combine (MVSPatternSet a, MVSPatternSet b) {
        MVSPatternSet result = new MVSPatternSet();

        for (MultivariatePattern patternA : a.patterns) {
            for (MultivariatePattern patternB : b.patterns) {
                MultivariatePattern newPattern = new MultivariatePattern();
                newPattern.addPatterns(patternA);
                newPattern.addPatterns(patternB);

                result.addPattern(newPattern);
            }
        }

        return result;
    }

    private class Index {
        private int[] indices;

        public Index (Metadata metadata) {
            this.indices = new int[3];
            this.indices[0] = metadata.getInstance();
            this.indices[1] = metadata.getSequence();
            this.indices[2] = metadata.getFrame();
        }

        public int getInstance () {
            return this.indices[0];
        }

        public int getSequence () {
            return this.indices[1];
        }

        public int getFrame () {
            return this.indices[2];
        }
    }

    private void DEBUG_printTreeSet (TreeSet<?> in) {
        for (Object obj : in) {
            System.out.println(obj.toString());
        }

        System.out.println();
    }
}
