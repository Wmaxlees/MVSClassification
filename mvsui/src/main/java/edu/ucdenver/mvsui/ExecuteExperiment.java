/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.mvsui;

import edu.ucdenver.IApproach;
import edu.ucdenver.mvsui.misc.*;
import edu.ucdenver.util.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 *
 * @author siddh
 */
public class ExecuteExperiment {

    private static ApproachLoader approachLoader;
    private static Data data;
    private static Configuration config;

    public static void main (String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        System.out.println("Starting Evaluation\n------------------\n");

        initializeExperiments();
        ResultContainer rc = executeExperiments();
        reportResults(rc);
        cleanup();
    }

    public static void initializeExperiments () {
        System.out.println("Attempting to load global config file...");
        config = new Configuration(System.getProperty("user.dir") + "/config/global.properties");

        // Make the temporary folder
        new File(System.getProperty("user.dir") + "/temp").mkdir();

        System.out.println("Searching for approaches...\n");
        approachLoader = new ApproachLoader();

        try {
            String dataPath = config.getString("DataPath");
            int trainingSetSize = config.getInt("TrainingSetSize");
            int testingSetSize = config.getInt("TestingSetSize");

            System.out.println("\nLoading data set from: " + dataPath);
            data = new Data(config.getInt("DataDepth"), config.getInt("NumberOfSequences"));
            data.loadDataSet(dataPath, trainingSetSize, testingSetSize);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load data set");
            e.printStackTrace(System.err);
        }

        for (IApproach approach : approachLoader.getApproaches()) {
            approach.initialize(data.getDepth(), data.getNumberOfSequences());
        }

    }

    public static ResultContainer executeExperiments () throws IOException {
        ResultContainer results = new ResultContainer(approachLoader.getNumberOfApproaches());

        // Perform execution of approaches
        for (int i = 0; i < approachLoader.getNumberOfApproaches(); ++i) {
            IApproach approach = approachLoader.getApproach(i);

            System.out.println("\n\nExecuting: " + approach.getName());
            System.out.println("Training...");
            results.setLabel(i, approach.getName());

            // Run training and get the training time
            long tStart = System.currentTimeMillis();
            approach.train(data.getTrainingSet());
            results.setTrainingTime(i, System.currentTimeMillis() - tStart);

            // Get the size of the model
            System.out.println("Calculating memory footprint...");
            results.setSize(i, UtilityClass.getMemoryRequiredByLearningModule(approach));

            // Run the test and get the timing
            System.out.println("Testing...");
            tStart = System.currentTimeMillis();
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

    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            // Get files
            File[] files = directory.listFiles();

            // Check if it has any subfiles
            if (files != null){

                for (int i = 0; i < files.length; ++i) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }

        return directory.delete();
    }
}
