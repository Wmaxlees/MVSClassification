package edu.ucdenver.util;

import org.jblas.DoubleMatrix;

/**
 * Created by max on 3/9/17.
 */
public class MathUtil {
    public static double[] calculateEuclideanDistanceBetweenFrames (DoubleMatrix[] frame1, DoubleMatrix[] frame2) {
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

}
