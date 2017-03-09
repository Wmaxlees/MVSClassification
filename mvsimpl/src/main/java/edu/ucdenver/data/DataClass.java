/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.data;

import edu.ucdenver.exceptions.MismatchedDataException;

import java.util.ArrayList;

public class DataClass {
    private String label;
    private int depth;
    private int sequences;
    private ArrayList<MultivariateSpatiotemporalSequence> instances;

    private DataClass () {}

    /**
     * Create a new DataClass with the specified label
     *
     * @param label The label to represent the data class
     */
    public DataClass (String label) {
        this.instances = new ArrayList<>();
        this.label = label;
        this.depth = 0;
    }

    /**
     * Add a MultivariateSpatiotemporalSequence to the data class. There is no check
     * to make sure a matching MVS is not already in the data class.
     *
     * @param mvs The MVS to add to the data class
     * @throws MismatchedDataException Thrown if depth of sequence doesn't match depth of data class
     */
    public void addInstance (MultivariateSpatiotemporalSequence mvs) {
        if (this.depth == 0 && this.sequences == 0) {
            this.depth = mvs.getDepth();
            this.sequences = mvs.getNumberOfSequences();
        } else if (this.depth != mvs.getDepth() || this.sequences != mvs.getNumberOfSequences()) {
            throw new MismatchedDataException();
        }

        this.instances.add(mvs);
    }

    /**
     * Get the depth of the data class
     *
     * @return The depth
     */
    public int getDepth () {
        return this.depth;
    }

    /**
     * Get a MultivariateSpatiotemporalSequence from the data class based on the
     * passed index.
     *
     * @param instanceIndex The index to access
     * @return The MVS at the specified index
     * @throws IndexOutOfBoundsException Thrown if the data class does not have the index specified
     */
    public MultivariateSpatiotemporalSequence getInstance (int instanceIndex) throws IndexOutOfBoundsException {
        if (this.instances.size() <= instanceIndex) {
            throw new IndexOutOfBoundsException();
        }

        return this.instances.get(instanceIndex);
    }

    /**
     * Returns the label of the data class
     *
     * @return The label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Get the number of instances associated with the given data class
     *
     * @return The number of instances
     */
    public int getNumberOfInstances () {
        return this.instances.size();
    }

    /**
     * Get the number of sequences to expect in the each MVS
     *
     * @return The number of sequences
     */
    public int getNumberOfSequences () {
        return this.sequences;
    }
}
