/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import edu.ucdenver.data.DataClass;
import edu.ucdenver.data.ResultSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author siddh
 */
public class FeatureBased extends IApproach {
    private String workingDirectory;
    private int dataDepth;
    private int numberOfSequences;

    /**
     * Constructor
     *
     * Loads the config file
     */
    public FeatureBased () {
        this.loadConfigurationFile(this.getName());
        this.workingDirectory = this.getWorkingDirectory();
    }

    @Override
    public void initialize (int depth, int numberOfSequences) {
        this.dataDepth = depth;
        this.numberOfSequences = numberOfSequences;
    }

    @Override
    public void train (DataClass[] trainingDataSet) {
        boolean useRange = this.config.getBoolean("UseRange");
        boolean useMean = this.config.getBoolean("UseMean");
        boolean useStandardDeviation = this.config.getBoolean("UseStandardDeviation");

        for (DataClass dc : trainingDataSet) {
            // Make sure the data matches what is expected
            if (dc.getDepth() != this.dataDepth || dc.getNumberOfSequences() != numberOfSequences) {
                System.out.println("Input data does not match expected dimensions: " + dc.getLabel());
                continue;
            }

            File featureFile = new File(workingDirectory + "/" + dc.getLabel() + ".ftr");
            try {
                FeatureVector.getFeaturesOfDataClass(dc, useRange, useMean, useStandardDeviation)
                        .saveToFile(featureFile);
            } catch (IOException e) {
                System.err.println("Failed writing feature vector to file: " + featureFile.getAbsolutePath());
                e.printStackTrace(System.err);
            }
        }
    }

    @Override
    public ResultSet test (DataClass[] testingDataSet) {
        ResultSet results = new ResultSet();

        // Get options
        boolean useRange = this.config.getBoolean("UseRange");
        boolean useMean = this.config.getBoolean("UseMean");
        boolean useStandardDeviation = this.config.getBoolean("UseStandardDeviation");
        boolean useEuclideanDistance = this.config.getBoolean("UseEuclideanDistance");

        // Walk through all test cases
        for (int i = 0; i < testingDataSet.length; i++) {

            // Get the features for the test case
            FeatureVector features = FeatureVector.getFeaturesOfDataClass(testingDataSet[i], useRange, useMean, useStandardDeviation);

            // Iterate through trained features
            String currentMatch = "";
            double currentMatchDistance = Double.MAX_VALUE;
            File featuresDirectory = new File(this.workingDirectory);
            for (File featureFile : featuresDirectory.listFiles()) {
                try {
                    FeatureVector testFeatures = FeatureVector.loadFromFile(featureFile);
                    double tempDistance;
                    if (useEuclideanDistance) {
                        tempDistance = testFeatures.getEuclideanDistance(features);
                    } else {
                        tempDistance = testFeatures.getManhattanDistance(features);
                    }

                    if (tempDistance < currentMatchDistance) {
                        currentMatch = featureFile.getName().split("\\.")[0];
                        currentMatchDistance = tempDistance;
                    }
                } catch (IOException e) {
                    System.out.println("Failed to load feature file");
                    e.printStackTrace(System.err);
                }
            }

            // Closest match was found
            results.predict(testingDataSet[i].getLabel(), currentMatch);
        }

        return results;
    }

    @Override
    public String getName() {
        return "FeatureBased";
    }

    private String buildFirstLine() {
        String line = "DataClass,";

        if (this.config.getBoolean("UseRange")) {
            for (int sequence = 0; sequence < this.numberOfSequences; ++sequence) {
                for (int depth = 0; depth < this.dataDepth; ++depth) {
                    line += "Range_" + sequence + "_(" + depth + "),";
                }
            }
        }

        if (this.config.getBoolean("UseMean")) {
            for (int sequence = 0; sequence < this.numberOfSequences; ++sequence) {
                for (int depth = 0; depth < this.dataDepth; ++depth) {
                    line += "Mean_" + sequence + "_(" + depth + "),";
                }
            }
        }

        if (this.config.getBoolean("UseStandardDeviation")) {
            for (int sequence = 0; sequence < this.numberOfSequences; ++sequence) {
                for (int depth = 0; depth < this.dataDepth; ++depth) {
                    line += "StandardDeviation_" + sequence + "_(" + depth + "),";
                }
            }
        }

        return line;
    }

    @Override
    public void writeDetails (String filename) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        // Write the first line
        out.write(this.buildFirstLine());
        out.newLine();

        File featuresDirectory = new File(this.workingDirectory);
        for (File featureFile : featuresDirectory.listFiles()) {
            FeatureVector testFeatures = FeatureVector.loadFromFile(featureFile);
            out.write(featureFile.getName().split("\\.")[0] + ",");
            out.write(testFeatures.toCSV());
            out.newLine();
        }

        out.close();
    }
}