package edu.ucdenver;

import com.sun.org.apache.xpath.internal.operations.Mult;
import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import edu.ucdenver.data.ResultSet;
import edu.ucdenver.data.DataClass;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LinearWarp extends IApproach {
    private WarperMVSWrapper referenceSequence;

    public LinearWarp () {
        this.loadConfigurationFile(this.getName());
        this.referenceSequence = null;
    }

    @Override
    public void train (DataClass[] trainingDataSet) {
        if (this.referenceSequence == null) {
            ReferenceSequence ref = new ReferenceSequence(
                    this.config.getInt("DataDepth"),
                    this.config.getInt("NumberOfFrames"),
                    this.config.getInt("NumberOfSequences")
            );
            this.referenceSequence = ref.build();
            this.referenceSequence.calculateAllDistances();

        }

        for (DataClass dc : trainingDataSet) {
            for (int i = 0; i < dc.getNumberOfInstances(); ++i) {
                MultivariateSpatiotemporalSequence instance = dc.getInstance(i);

                // Rotate and center the input
                instance = Utils.rotateAndCenter(
                        instance,
                        this.config.getInt("CenterSequence"),
                        this.config.getInt("RightSequence"),
                        this.config.getInt("LeftSequence")
                );

                // Segment the mvs
                SegmenterMVSWrapper segmentedInstance = new SegmenterMVSWrapper(instance);
                segmentedInstance.produceAllSegments(
                        this.config.getInt("SegmentationIndex"),
                        this.config.getInt("SegmentationDimension"),
                        this.config.getInt("SegmentationMinimumSize")
                );
                MultivariateSpatiotemporalSequence[] segments = segmentedInstance.getSegments();

                // Apply the warping
                for (MultivariateSpatiotemporalSequence segment : segments) {
                    WarperMVSWrapper warpedInstance = new WarperMVSWrapper(segment);
                    warpedInstance.calculateAllDistances();
                    segment = warpedInstance.alignWith(this.referenceSequence);

                    // REMOVE POORLY FORMED SEGMENTS USING SVM

                    // TO CONSIDER
                    // CALCULATE DISTANCE FROM SOME PREDEFINED REFERENCE POSITION
                    // AND USE THAT AS AN INDEX FOR CLASSIFICATION
                    // SAVE THE COMPARISON BETWEEN ALL
                }
            }
        }
    }

    @Override
    public ResultSet test (DataClass[] testingDataSet) {
        return new ResultSet();
    }

    @Override
    public String getName() {
        return "LinearWarp";
    }

    @Override
    public void writeDetails (String filename) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        out.close();
    }
}
