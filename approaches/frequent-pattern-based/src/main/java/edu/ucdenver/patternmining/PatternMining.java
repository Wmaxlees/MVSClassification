package edu.ucdenver.patternmining;

import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import java.util.Arrays;

/**
 * A class used to mine patterns from a set of MVSs. It mostly contains
 * utility function.
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/14/17.
 */
public class PatternMining {

    /**
     * Convert the real numbered values of the input MVS to a grid style
     * integer valued mvs
     *
     * @param input The MVS input
     * @return A new mvs with integer values
     */
    public static MultivariateSpatiotemporalSequence convertMVSToBlocks (MultivariateSpatiotemporalSequence input, double[] sizeOfBlocks) {
        // Check if the sizeOfBlocks has enough values
        if (sizeOfBlocks.length != input.getDepth()) {
            System.err.println("Attempting to convert MVS to Blocks without proper dimension subdivision configuration");
            System.err.println("Dimensions are mismatched");
            return null;
        }

        // Create the new blocked matrices
        double[][] blockedMatrix = new double[input.getNumberOfSequences()][input.getNumberOfFrames()];
        for (int sequence = 0; sequence < input.getNumberOfSequences(); ++sequence) {
            blockedMatrix[sequence] = new double[input.getNumberOfFrames()];
            for (int frame = 0; frame < blockedMatrix[0].length; ++frame) {
                blockedMatrix[sequence][frame] = 0.0D;
            }
        }

        for (int i = 0; i < input.getNumberOfSequences(); ++i) {
            DoubleMatrix[] column = input.getSequence(i);

            for (int depth = 0; depth < input.getDepth(); ++depth) {
                blockedMatrix[i] = PatternMining.addVectors(blockedMatrix[i],
                        MatrixFunctions.floor(column[depth].div(sizeOfBlocks[depth])).add(10.0D*depth).toArray());
            }
        }

        DoubleMatrix[] result = {new DoubleMatrix(blockedMatrix)};

        return MultivariateSpatiotemporalSequence.fromMatrices(input.getLabel(), result);
    }

    /**
     * Add two vectors together
     *
     * @param vector1 A vector
     * @param vector2 A vector
     * @return The result of element-wise addition
     */
    private static double[] addVectors (double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            System.err.println("Trying to add vectors of different sizes");
            System.out.println(Arrays.toString(vector1));
            System.out.println(Arrays.toString(vector2));
            return null;
        }

        for (int i = 0; i < vector1.length; ++i) {
            vector1[i] += vector2[i];
        }

        return vector1;
    }

}
