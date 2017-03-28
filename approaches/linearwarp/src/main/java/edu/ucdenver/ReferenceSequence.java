package edu.ucdenver;

import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import org.jblas.DoubleMatrix;

/**
 * A reference sequence used for global alignment
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/24/17
 */
public class ReferenceSequence {
    private DoubleMatrix[] reference;

    public ReferenceSequence (int depth, int numberOfFrames, int numberOfSequences) {
        this.reference = new DoubleMatrix[depth];

        this.reference[0] = DoubleMatrix.ones(numberOfSequences, numberOfFrames);

        for (int i = 0; i < numberOfSequences; ++i) {
            for (int j = 0; j < numberOfFrames; ++j) {
                this.reference[0].put(i, j, i);
            }
        }

        for (int i = 1; i < depth; ++i) {
            this.reference[i] = this.reference[0];
        }
    }

    public WarperMVSWrapper build () {
        return new WarperMVSWrapper(new MultivariateSpatiotemporalSequence(this.reference, "reference"));
    }
}
