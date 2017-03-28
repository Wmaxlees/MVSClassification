package edu.ucdenver;

import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import edu.ucdenver.exceptions.MismatchedDataException;
import org.jblas.DoubleMatrix;

/**
 * A class used to calculate the manifold warp value
 * of an MVS
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/27/17
 */
public class WarperMVSWrapper extends MultivariateSpatiotemporalSequence {
    private double[] distances;

    private WarperMVSWrapper () {}

    /**
     * Create a new WarperMVSWrapper object from an existing MVS
     *
     * @param mvs The mvs to wrap
     */
    public WarperMVSWrapper (MultivariateSpatiotemporalSequence mvs) {
        this.data = mvs.getData();
        this.label = mvs.getLabel();

        this.distances = new double[mvs.getNumberOfFrames()];
    }

    /**
     * Will find the best match between the current MVS
     * and the target MVS. calculateAllDistances() must be called
     * for this to work properly
     *
     * @param target To target to align to
     * @returns A new MVS that is aligned
     */
    public MultivariateSpatiotemporalSequence alignWith (WarperMVSWrapper target) {
        if (target.getNumberOfFrames() > this.getNumberOfFrames()) {
            System.err.println("The reference frame must have fewer frames");
            throw new MismatchedDataException();
        }

        // Find closest matching indices
        int[] indices = new int[target.getNumberOfFrames()];
        int lastMatchedIndex = 0;
        for (int frame = 0; frame < target.getNumberOfFrames(); ++frame) {
            double targetDistance = target.distances[frame];

            for (int i = lastMatchedIndex; i < this.getNumberOfFrames(); ++i) {
                if (this.distances[i] > targetDistance) {
                    indices[frame] = i-1;
                    lastMatchedIndex = i-1;

                    break;
                }
            }
        }

        // Create the resulting matrix
        DoubleMatrix[] alignedMatrix = new DoubleMatrix[this.getDepth()];
        for (int i = 0; i < alignedMatrix.length; ++i) {
            alignedMatrix[i] = new DoubleMatrix(this.getNumberOfSequences(), target.getNumberOfFrames());
        }

        // Populate the new aligned matrix
        for (int depth = 0; depth < alignedMatrix.length; ++depth) {
            for (int frame = 0; frame < target.getNumberOfFrames(); ++frame) {
                alignedMatrix[depth].putColumn(frame, this.data[depth].getColumn(indices[frame]));
            }
        }

        return new MultivariateSpatiotemporalSequence(alignedMatrix, this.label);
    }

    /**
     * Calculate all of the distances for each frame
     */
    public void calculateAllDistances () {
        this.distances[0] = 0.0D;

        double[] previousFrame = this.flattenFrame(this.getFrame(0));
        for (int frame = 1; frame < this.getNumberOfFrames(); ++frame) {
            double[] currentFrame = this.flattenFrame(this.getFrame(frame));

            this.distances[frame] = this.calculateEuclideanDistance(previousFrame, currentFrame);
            previousFrame = currentFrame;
        }

        double totalDistance = this.distances[this.distances.length-1];

        for (int i = 0; i < this.distances.length; ++i) {
            this.distances[i] = this.distances[i]/totalDistance;
        }
    }

    /**
     * Takes in a single frame and flattens it into a
     * single vertex
     *
     * @param frame The frame to flatten
     * @return The newly flattened frame
     */
    private double[] flattenFrame (DoubleMatrix[] frame) {
        double[] result = new double[this.getDepth()*this.getNumberOfSequences()];

        for (int depth = 0; depth < frame.length; ++depth) {
            for (int sequence = 0; sequence < this.getNumberOfSequences(); ++sequence) {
                result[(depth*this.getNumberOfSequences()) + sequence] = frame[depth].get(sequence);
            }
        }

        return result;
    }

    /**
     * Calculate the euclidean distance between two vectors
     *
     * @param a A vector
     * @param b A vector
     * @return The euclidean distance
     */
    private double calculateEuclideanDistance (double[] a, double[] b) {
        if (a.length != b.length) {
            throw new MismatchedDataException();
        }

        double result = 0.0D;
        for (int i = 0; i < a.length; ++i) {
            result += (a[i] - b[i])*(a[i]-b[i]);
        }

        result = Math.sqrt(result);

        result = result / a.length;

        return result;
    }
}
