package edu.ucdenver;

import edu.ucdenver.data.DataClass;
import edu.ucdenver.data.ResultSet;
import edu.ucdenver.patternmining.HorizontalPatternSet;
import edu.ucdenver.patternmining.PatternMining;

import java.io.File;
import java.io.IOException;

public class FrequentPatternBased extends IApproach {
    public FrequentPatternBased () {
        this.loadConfigurationFile(this.getName());
    }

    @Override
    public void train (DataClass[] trainingDataSet) {
        for (DataClass dc : trainingDataSet) {
            System.out.println("Processing " + dc.getLabel());

            DataClass blockedDC = new DataClass(dc.getLabel());
            int maxFrames = 0;
            for (int i = 0; i < dc.getNumberOfInstances(); ++i) {
                if (maxFrames < dc.getInstance(i).getNumberOfFrames()) {
                    maxFrames = dc.getInstance(i).getNumberOfFrames();
                }
                blockedDC.addInstance(PatternMining.convertMVSToBlocks(dc.getInstance(i), new double[]{0.1D, 0.1D, 0.1D}));
            }

            HorizontalPatternSet patternSet =
                    new HorizontalPatternSet(this.config.getInt("MaxPatternSize"),
                            blockedDC.getNumberOfInstances(),
                            blockedDC.getNumberOfSequences(),
                            maxFrames, this.config.getInt("MinimumSupport"));

            patternSet.initialize(blockedDC);

            patternSet.generateAllHorizontalPatterns();

            String filename = this.getWorkingDirectory() + "/" + blockedDC.getLabel();
            try {
                patternSet.writeAsMVSPatternSet(new File(filename));
            } catch (IOException e) {
                System.err.println("Failed to write pattern data to file: " + filename);
            }
        }
    }

    @Override
    public ResultSet test (DataClass[] testingDataSet) {
        return new ResultSet();
    }

    @Override
    public String getName() {
        return "FrequentPatternBased";
    }

    @Override
    public void writeDetails (String filename) {
        return;
    }


}
