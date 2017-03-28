package edu.ucdenver;

import com.sun.org.apache.xpath.internal.operations.Mult;
import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import org.jblas.DoubleMatrix;
import org.jblas.ranges.IntervalRange;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that wraps the MVS class in order to segment it properly
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/27/17
 */
public class SegmenterMVSWrapper extends MultivariateSpatiotemporalSequence {
    private ArrayList<DoubleMatrix[]> segmentedData;

    private SegmenterMVSWrapper () {}

    /**
     * Create a new SegmentedMVSWrapper from an existing MVS
     *
     * @param mvs The MVS to wrap
     */
    public SegmenterMVSWrapper (MultivariateSpatiotemporalSequence mvs) {
        this.data = mvs.getData();
        this.label = mvs.getLabel();

        this.segmentedData = new ArrayList<>();
    }

    /**
     * Finds the local maximum of a specific sequence and uses
     * that as a segmentation index
     *
     * @param segmentationIndex The sequence to use for segmentation
     * @param dimension The dimension to check for maximum
     */
    public void produceAllSegments (int segmentationIndex, int dimension, int minimumSize) {
        this.segmentedData = segment(segmentationIndex, dimension, minimumSize, this.data);
    }

    private ArrayList<DoubleMatrix[]> segment (int segmentationIndex, int dimension, int minimumSize, DoubleMatrix[] data) {
        ArrayList<DoubleMatrix[]> result = new ArrayList<>();

        int argmax = this.getLocalMaximumIndex(data[dimension].getRow(segmentationIndex).toArray(), minimumSize);

        // Base case
        if (argmax == 0) {
            result.add(data);
            return result;
        }

        // Split the matrix at the maximum
        DoubleMatrix[][] split = new DoubleMatrix[2][data.length];
        for (int depth = 0; depth < data.length; ++depth) {
            split[0][depth] = data[depth].getColumns(new IntervalRange(0, argmax));
            split[1][depth] = data[depth].getColumns(new IntervalRange(argmax, data[depth].getColumns()));
        }

        // Recurse on the smaller segments
        result.add(split[0]);
        result.addAll(this.segment(segmentationIndex, dimension, minimumSize, split[1]));

        return result;
    }

    public MultivariateSpatiotemporalSequence[] getSegments () {
        MultivariateSpatiotemporalSequence[] results = new MultivariateSpatiotemporalSequence[this.segmentedData.size()];

        for (int i = 0; i < this.segmentedData.size(); ++i) {
            results[i] = new MultivariateSpatiotemporalSequence(this.segmentedData.get(i), this.label);
        }

        return results;
    }

    private int getLocalMaximumIndex (double[] sequence, int distance) {
        double previousValue = Double.MIN_VALUE;

        for (int i = distance; i < sequence.length; ++i) {
            if (previousValue > sequence[i]) {
                return i;
            }

            previousValue = sequence[i];
        }

        return 0;
    }
}
