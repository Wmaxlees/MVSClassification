package edu.ucdenver.util;

import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import org.jblas.DoubleMatrix;

/**
 * Created by max on 3/9/17.
 */
public class MathUtil {
    public static double calculateEuclideanDistanceBetweenMVSs (MultivariateSpatiotemporalSequence mvs1, MultivariateSpatiotemporalSequence mvs2) {
        // Check if the depths are the same
        // NOTE: # of frames doesn't have to be the same
        if (mvs1.getDepth() != mvs2.getDepth()) {
            System.err.println("Attempting to calculate euclidean distance between two MVSs of different depth");
        }

        // Figure out how many frames to compare
        int numberOfFrames =
                mvs1.getNumberOfFrames() > mvs2.getNumberOfFrames() ?
                        mvs1.getNumberOfFrames() : mvs2.getNumberOfFrames();

        // Create the result array
        double[][] result = new double[numberOfFrames][mvs1.getDepth()];

        for (int i = 0; i < numberOfFrames; ++i) {
            result[i] = MathUtil.calculateEuclideanDistanceBetweenFrames(mvs1.getFrame(i), mvs2.getFrame(i));
        }

        return MathUtil.sumMatrix(result);
    }

    public static double calculateDTWDistanceBetweenMVSs (MultivariateSpatiotemporalSequence mvs1, MultivariateSpatiotemporalSequence mvs2) {
        // Check if the depths are the same && the number of sequences are the same
        // NOTE: # of frames doesn't have to be the same
        if (mvs1.getDepth() != mvs2.getDepth() || mvs1.getNumberOfSequences() != mvs2.getNumberOfSequences()) {
            System.err.println("Attempting to calculate dynamic time warp distance between two MVSs of different shape");
        }

        double[][] result = new double[mvs1.getDepth()][mvs1.getNumberOfSequences()];
        for (int depth = 0; depth < mvs1.getDepth(); ++depth) {
            DoubleMatrix layer1 = mvs1.getLayer(depth);
            DoubleMatrix layer2 = mvs2.getLayer(depth);

            for (int sequence = 0; sequence < mvs1.getNumberOfSequences(); ++sequence) {
                DoubleMatrix row1 = layer1.getRow(sequence);
                DoubleMatrix row2 = layer2.getRow(sequence);

                // Initialize the matrix
                double[][] dtwMatrix = new double[row1.columns][row2.columns];
                for (int i = 0; i < row1.columns; ++i) {
                    dtwMatrix[i][0] = Double.POSITIVE_INFINITY;
                }
                for (int i = 0; i < row2.columns; ++i) {
                    dtwMatrix[0][i] = Double.POSITIVE_INFINITY;
                }
                dtwMatrix[0][0] = 0.0D;

                // Calculate the distance
                for (int row = 1; row < row1.columns; ++row) {
                    for (int column = 1; column < row2.columns; ++column) {
                        double cost = Math.abs(row1.get(row) - row2.get(column));
                        double adder = Math.min(dtwMatrix[row-1][column], dtwMatrix[row][column-1]);
                        adder = Math.min(adder, dtwMatrix[row-1][column-1]);

                        dtwMatrix[row][column] = cost + adder;
                    }
                }

                // Add the calculated value to the results
                result[depth][sequence] = dtwMatrix[row1.columns-1][row2.columns-1];
            }
        }

        return MathUtil.sumMatrix(result);
    }

    private static double sumMatrix(double[][] matrix) {
        double result = 0.0D;
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                result += matrix[i][j];
            }
        }

        return result;
    }

    private static double[] calculateEuclideanDistanceBetweenFrames (DoubleMatrix[] frame1, DoubleMatrix[] frame2) {
        if (frame1.length != frame2.length) {
            System.err.println("Attempting to calculate the euclidean distance between vectors of different size");
            return null;
        }

        double[] result = new double[frame1.length];
        for (int i = 0; i < frame1.length; ++i) {
            DoubleMatrix difference = frame1[i].sub(frame2[i]);
            difference = difference.mul(difference);
            result[i] = difference.sum();
            result[i] = Math.sqrt(result[i]);
        }

        return result;
    }

    public static double[][] transpose (double[][] matrix) {
        double[][] result = new double[matrix[0].length][matrix.length];

        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                result[i][j] = matrix[j][i];
            }
        }

        return result;
    }

}
