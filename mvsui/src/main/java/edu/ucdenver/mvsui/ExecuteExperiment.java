/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.mvsui;

import edu.ucdenver.IApproach;
import edu.ucdenver.mvsui.misc.*;
import edu.ucdenver.util.Configuration;
import edu.ucdenver.mvsui.misc.Data.DataBuilder;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 *
 * @author siddh
 * @author W. Max Lees
 */
public class ExecuteExperiment {

    private static ApproachLoader approachLoader;
    private static Data data;
    private static Configuration config;

    public static void main (String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        System.out.println("Starting Evaluation\n------------------\n");

        cleanup();
        initializeExperiments();
        ResultContainer rc = executeExperiments();
        reportResults(rc);
    }

    public static void initializeExperiments () {
        System.out.println("Attempting to load global config file...");
        config = new Configuration(System.getProperty("user.dir") + "/config/global.properties");

        // Make the temporary folder
        new File(System.getProperty("user.dir") + "/temp").mkdir();

        System.out.println("Searching for approaches...\n");
        approachLoader = new ApproachLoader();

        String dataPath = config.getString("DataPath");
        System.out.println("\nSetting data set path: " + dataPath);
        data = new DataBuilder()
                .setDataPath(dataPath)
                .setBatchSize(config.getInt("BatchSize"))
                .setDepth(config.getInt("DataDepth"))
                .setNumberOfSequences(config.getInt("NumberOfSequences"))
                .setTestingSetSize(config.getInt("TestSetSize"))
                .build();

        for (IApproach approach : approachLoader.getApproaches()) {
            approach.initialize(data.getDepth(), data.getNumberOfSequences());
        }

    }

    public static ResultContainer executeExperiments () throws IOException {
        ResultContainer results = new ResultContainer(approachLoader.getNumberOfApproaches());

        // Perform execution of approaches
        for (int i = 0; i < approachLoader.getNumberOfApproaches(); ++i) {
            IApproach approach = approachLoader.getApproach(i);

            System.out.println("\n\nTraining: " + approach.getName());
            results.setLabel(i, approach.getName());

            long trainingTime = 0;
            int iteration = 1;
            while (ExecuteExperiment.data.loadNextTrainingBatch()) {
                System.out.println("Batch [" + iteration++ + "]");

                // Run training and get the training time
                long tStart = System.currentTimeMillis();
                approach.train(data.getCurrentTrainingSet());
                trainingTime += System.currentTimeMillis() - tStart;
            }
            results.setTrainingTime(i, trainingTime);

            // Get the size of the model
            System.out.println("Calculating memory footprint...");
            results.setSize(i, UtilityClass.getMemoryRequiredByLearningModule(approach));

            // Reset the training data
            data.rewind();
        }

        System.out.println("\n\nLoading testing set...");
        data.loadTestingSet();

        for (int i = 0; i < approachLoader.getNumberOfApproaches(); ++i) {
            IApproach approach = approachLoader.getApproach(i);

            // Run the test and get the timing
            System.out.println("\n\nTesting: " + approach.getName());
            long tStart = System.currentTimeMillis();
            results.setResults(i, approach.test(data.getTestingSet()));
            results.setTestingTime(i, System.currentTimeMillis() - tStart);
        }

        return results;
    }

    public static void reportResults (ResultContainer results) {
        System.out.println("\n\nGenerate result files");
        try {
            results.writeToFilesWithConfig(config.toCSV());

            String resultsFolderPath = System.getProperty("user.dir") + "/results";
            for (IApproach approach : approachLoader.getApproaches()) {
                System.out.println("Writing approach details: " + resultsFolderPath + "/" + approach.getName() + ".csv");
                approach.writeDetails(resultsFolderPath + "/" + approach.getName() + ".csv");
            }
        } catch (IOException e) {
            System.err.println("Failed to access report files.");
            e.printStackTrace(System.err);
        }
    }

    public static void cleanup () {
        // Clear out the temporary folder
        try {
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/temp"));
        } catch (IOException e) {
            System.err.println("Failed to clear temp directory");
        }
    }
}
