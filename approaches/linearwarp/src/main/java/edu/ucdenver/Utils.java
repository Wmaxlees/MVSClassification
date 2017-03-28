package edu.ucdenver;

import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import org.jblas.DoubleMatrix;

import java.util.Arrays;

/**
 * Utilities for modifying a sequence input
 *
 * @author max [max.lees@gmail.com]
 * Created on 3/24/17
 */
public class Utils {

    public static MultivariateSpatiotemporalSequence rotateAndCenter (
            MultivariateSpatiotemporalSequence in,
            int centerSequenceIndex,
            int rightIndex,
            int leftIndex
    ) {
        for (int frameNumber = 0; frameNumber < in.getNumberOfFrames(); ++frameNumber) {
            // Get the frame
            DoubleMatrix[] frame = in.getFrame(frameNumber);

            // Rotate the matrix to be a sequences x depth shape
            // rather than sequences x 1 x depth shape
            DoubleMatrix rotatedFrame = new DoubleMatrix(in.getNumberOfSequences(), frame.length);
            for (int depth = 0; depth < frame.length; ++depth) {
                rotatedFrame.putColumn(depth, frame[depth]);
            }

            // Get the center
            DoubleMatrix center = rotatedFrame.getRow(centerSequenceIndex);

            // Get the right and left
            double[] right = rotatedFrame.getRow(rightIndex).toArray();
            double[] left = rotatedFrame.getRow(leftIndex).toArray();

            // Calculate the transform matrix
            double x_d = right[0] - left[0];
            double z_d = right[2] - left[2];
            DoubleMatrix transform = new DoubleMatrix(new double[][]{
                    {  x_d, 0, z_d },
                    {    0, 1,   0 },
                    { -z_d, 0, x_d }
            });

            // Apply the transform
            rotatedFrame = rotatedFrame.subRowVector(center).transpose();
            rotatedFrame = transform.mmul(rotatedFrame).transpose();

            // Rotate the frame back
            for (int depth = 0; depth < frame.length; ++depth) {
                frame[depth] = rotatedFrame.getColumn(depth);
            }

            // Update the actual frame value
            in.setFrame(frameNumber, frame);
        }

        return in;
    }
}
