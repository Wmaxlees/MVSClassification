package edu.ucdenver;

import edu.ucdenver.data.DataClass;

import java.io.*;

/**
 * Created by max on 3/8/17.
 */
public class FeatureVector {
    double[] vector;

    private FeatureVector () {}

    /**
     * Generate the FeatureVector of a data class
     *
     * @param dc The data class to generate the feature vector from
     * @param useRange Whether to calculate the range
     * @param useMean Whether to use the mean
     * @param useStandardDeviation Whether to use the standard deviation
     * @return A new feature vector
     */
    public static FeatureVector getFeaturesOfDataClass (DataClass dc, boolean useRange, boolean useMean, boolean useStandardDeviation) {
        // Figure out how much space we need
        int featuresToConsider = 0;
        if (useRange) {
            ++featuresToConsider;
        }
        if (useMean) {
            ++featuresToConsider;
        }
        if (useStandardDeviation) {
            ++featuresToConsider;
        }

        // Space to hold all the features
        FeatureVector features = new FeatureVector();
        features.vector = new double[dc.getDepth() * dc.getNumberOfSequences() * featuresToConsider];

        int index = 0;

        // Put the ranges in the feature vector
        if (useRange) {
            for (double[] layer : MathUtilities.getRanges(dc)) {
                for (double datum : layer) {
                    features.vector[index++] = datum;
                }
            }
        }

        // Put the means in the feature vector
        if (useMean) {
            for (double[] layer : MathUtilities.getMeans(dc)) {
                for (double datum : layer) {
                    features.vector[index++] = datum;
                }
            }
        }

        // Put the standard deviations in the feature vector
        if (useStandardDeviation) {
            for (double[] layer : MathUtilities.getStandardDeviations(dc)) {
                for (double datum : layer) {
                    features.vector[index++] = datum;
                }
            }
        }

        return features;
    }

    /**
     * Load the FeatureVector from a file
     *
     * @param file The file to load the vector from
     * @return The new feature vector
     * @throws IOException Thrown if the file operations fail
     */
    public static FeatureVector loadFromFile (File file) throws IOException {
        FeatureVector features = new FeatureVector();

        DataInputStream is = new DataInputStream(new FileInputStream(file));

        int size = is.readInt();
        features.vector = new double[size];

        for (int i = 0; i < size; ++i) {
            features.vector[i] = is.readDouble();
        }

        is.close();

        return features;
    }

    /**
     * Get the euclidean distance with another FeatureVector
     *
     * @param other The other FeatureVector to compare
     * @return The distance or NaN if the vectors are of different sizes
     */
    public double getEuclideanDistance (FeatureVector other) {
        // Return NaN if the vectors are of different length
        if (this.vector.length != other.vector.length) {
            System.err.println("Attempting to calculate euclidean distance of vectors of different size");
            return Double.NaN;
        }

        double diff = 0.0;
        for (int i = 0; i < this.vector.length; i++) {
            diff += ((this.vector[i] - other.vector[i]) * (this.vector[i] - other.vector[i]));
        }

        return Math.sqrt(diff);
    }

    /**
     * Get the manhattan distance compared to another FeatureVector
     *
     * @param other The vector to compare to
     * @return The distance or NoN if the vectors are of different sizes
     */
    public double getManhattanDistance (FeatureVector other) {
        // Return NaN if the vectors are of different length
        if (this.vector.length != other.vector.length) {
            System.err.println("Attempting to calculate manhattan distance of vectors of different size");
            return Double.NaN;
        }

        double diff = 0.0;
        for (int i = 0; i < this.vector.length; ++i) {
            diff += Math.abs(this.vector[i] - other.vector[i]);
        }

        return diff;
    }

    /**
     * Save the feature vector to a file
     *
     * @param file The file to save the vector to
     * @throws IOException Thrown if the file operations fail
     */
    public void saveToFile (File file) throws IOException {
        DataOutputStream os = new DataOutputStream(new FileOutputStream(file));

        os.writeInt(this.vector.length);
        for (int i = 0; i < this.vector.length; ++i) {
            os.writeDouble(this.vector[i]);
        }

        os.close();
    }

    public String toCSV () {
        String result = "";
        for (double val : this.vector) {
            result += val + ",";
        }

        return result;
    }
}
