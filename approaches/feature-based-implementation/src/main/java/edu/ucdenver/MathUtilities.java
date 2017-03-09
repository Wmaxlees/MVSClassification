package edu.ucdenver;

import edu.ucdenver.data.DataClass;
import org.jblas.DoubleMatrix;

/**
 * Created by max on 3/8/17.
 */
public class MathUtilities {

    /**
     * Get the means for each sequence in the mvs for the entire
     * set of 'instances'
     *
     * @param dc The vector class to run the mean on
     * @return The means for each layer for each sequence
     */
    public static double[][] getMeans (DataClass dc) {
        // Initialize the necessary variables to 0
        double[][] means = new double[dc.getDepth()][dc.getNumberOfSequences()];
        int[][] count = new int[dc.getDepth()][dc.getNumberOfSequences()];
        for (int i = 0; i < means.length; ++i) {
            for (int j = 0; j < means[0].length; ++j) {
                means[i][j] = 0.0D;
                count[i][j] = 0;
            }
        }

        // Add up all the rows of all the instances and tally the total number of columns
        for (int i = 0; i < dc.getNumberOfInstances(); ++i) {
            for (int layer = 0; layer < dc.getDepth(); ++layer) {
                DoubleMatrix sums = dc.getInstance(i).getLayer(layer).rowSums();

                for (int sequence = 0; sequence < dc.getNumberOfSequences(); ++sequence) {
                    means[layer][sequence] += sums.get(sequence);
                    count[layer][sequence] += dc.getInstance(i).getNumberOfFrames();
                }
            }
        }

        // Calculate the actual means
        for (int i = 0; i < means.length; ++i) {
            for (int j = 0; j < means[0].length; ++j) {
                means[i][j] = means[i][j]/count[i][j]; // May have precision errors if means gets really high
            }
        }

        return means;
    }

    /**
     * Get the range of each sequence for each dimension
     *
     * @param dc The vector class to find ranges
     * @return The ranges for each sequence
     */
    public static double[][] getRanges (DataClass dc) {
        double min[][] = new double[dc.getDepth()][dc.getNumberOfSequences()];
        double max[][] = new double[dc.getDepth()][dc.getNumberOfSequences()];

        for (int i = 0; i < min.length; ++i) {
            for (int j = 0; j< min[0].length; ++j) {
                min[i][j] = Double.MAX_VALUE;
                max[i][j] = Double.MIN_VALUE;
            }
        }

        for (int i = 0; i < dc.getNumberOfInstances(); ++i) {
            for (int layer = 0; layer < dc.getDepth(); ++layer) {
                for (int sequence = 0; sequence < dc.getNumberOfSequences(); ++sequence) {
                    double tempMax = dc.getInstance(i).getLayer(layer).getRow(sequence).max();
                    if (tempMax > max[layer][sequence]) {
                        max[layer][sequence] = tempMax;
                    }

                    double tempMin = dc.getInstance(i).getLayer(layer).getRow(sequence).min();
                    if (tempMin < min[layer][sequence]) {
                        min[layer][sequence] = tempMin;
                    }
                }
            }
        }

        for (int layer = 0; layer < dc.getDepth(); ++layer) {
            for (int sequence = 0; sequence < max.length; ++sequence) {
                max[layer][sequence] -= min[layer][sequence];
            }
        }

        return max;
    }

    /**
     * Get the standard deviation for each sequence in the mvs for the entire
     * set of 'instances'
     *
     * @param dc The vector class to calculate standard deviation for
     * @return The standard deviation for each layer for each sequence
     */
    public static double[][] getStandardDeviations (DataClass dc) {
        double[][] means = MathUtilities.getMeans(dc);

        double[][] standardDeviations = new double[dc.getDepth()][dc.getNumberOfSequences()];
        int[][] count = new int[dc.getDepth()][dc.getNumberOfSequences()];
        for (int i = 0; i < standardDeviations.length; ++i) {
            for (int j = 0; j < standardDeviations[0].length; ++j) {
                standardDeviations[i][j] = 0.0D;
                count[i][j] = 0;
            }
        }

        for (int i = 0; i < dc.getNumberOfInstances(); ++i) {
            for (int layer = 0; layer < dc.getDepth(); ++layer) {
                for (int sequence = 0; sequence < dc.getNumberOfSequences(); ++sequence) {
                    // Subtract the mean
                    DoubleMatrix row = dc.getInstance(i).getLayer(layer).getRow(sequence).sub(means[layer][sequence]);
                    // Square the result
                    row = row.mul(row);
                    // Sum the result
                    standardDeviations[layer][sequence] += row.sum();
                    count[layer][sequence] += row.columns;
                }
            }
        }

        // Calculate the actual standard deviation
        for (int i = 0; i < standardDeviations.length; ++i) {
            for (int j = 0; j < standardDeviations[0].length; ++j) {
                standardDeviations[i][j] = Math.sqrt(standardDeviations[i][j]/count[i][j]); // May have precision errors if means gets really high
            }
        }

        return standardDeviations;
    }
}
