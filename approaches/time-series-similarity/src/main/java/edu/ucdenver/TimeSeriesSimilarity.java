/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import edu.ucdenver.data.DataClass;
import edu.ucdenver.data.MultivariateSpatiotemporalSequence;
import edu.ucdenver.data.ResultSet;
import edu.ucdenver.util.MathUtil;
import org.jblas.DoubleMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author siddh
 */
public class TimeSeriesSimilarity extends IApproach {

    private HashMap<String, HashMap<String, Double>> outputData;

    public TimeSeriesSimilarity () {
        this.loadConfigurationFile(this.getName());
    }

    @Override
    public void train (DataClass[] trainingDataSet) {
        for (DataClass dc : trainingDataSet) {
            for (int i = 0; i < dc.getNumberOfInstances(); ++i) {
                try {
                    dc.getInstance(i).save(this.getWorkingDirectory(), dc.getLabel() + "." + i);
                } catch (IOException e) {
                    System.err.println("Failed to write training data to file: " + dc.getLabel() + "." + i);
                }
            }
        }
    }

    @Override
    public ResultSet test (DataClass[] testingDataSet) {
        ResultSet results = new ResultSet();
        this.outputData = new HashMap<>(testingDataSet.length);

        for (DataClass dc : testingDataSet) {
            // Because it's so slow, we need some indication it's working
            System.out.println("Finding match for: " + dc.getLabel());

            // Get the files to work with
            File workingDir = new File(this.getWorkingDirectory());
            File[] trainedFiles = workingDir.listFiles(new MVSFilenameFilter());

            this.outputData.put(dc.getLabel(), new HashMap<>(trainedFiles.length));

            // Loop through files to compare
            double minimumDistance = Double.MAX_VALUE;
            String predictedLabel = null;
            for (File trainedFile : trainedFiles) {
                // Load the MVS
                MultivariateSpatiotemporalSequence mvs;
                try {
                    mvs = MultivariateSpatiotemporalSequence.load(trainedFile);
                } catch (IOException e) {
                    System.err.println("Failed to load mvs file: " + trainedFile);
                    e.printStackTrace(System.err);
                    continue;
                }

                // Calculate the difference
                double difference;
                if (this.config.getBoolean("UseDynamicTimeWarping")) {
                    difference = MathUtil.calculateDTWDistanceBetweenMVSs(dc.getInstance(0), mvs);
                } else {
                    difference = MathUtil.calculateEuclideanDistanceBetweenMVSs(dc.getInstance(0), mvs);
                }

                // Check if a better solution was found
                if (difference < minimumDistance) {
                    minimumDistance = difference;
                    predictedLabel = mvs.getLabel();
                }

                // Add the data to the output information
                HashMap<String, Double> temp = this.outputData.get(dc.getLabel());
                temp.put(trainedFile.getName().substring(0, trainedFile.getName().length()-4), difference);
                this.outputData.replace(dc.getLabel(), temp);
            }

            // Add the new prediction
            results.predict(dc.getLabel(), predictedLabel);
        }

        return results;
    }

    @Override
    public String getName() {
        return "TimeSeriesSimilarity";
    }

    @Override
    public void writeDetails (String filename) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        // Write the first line
        out.write("DistanceFrom,DistanceTo,Distance");
        out.newLine();

        for (String outerKey : this.outputData.keySet()) {
            for (String innerKey : this.outputData.get(outerKey).keySet()) {
                out.write(outerKey + "," + innerKey + "," + this.outputData.get(outerKey).get(innerKey));
                out.newLine();
            }
        }

        out.close();
    }

    private class MVSFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File path, String filename) {
            return filename.endsWith(".mvs");
        }
    }
}
