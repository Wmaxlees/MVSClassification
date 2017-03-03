/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import edu.ucdenver.Misc.ApproachLoader;
import edu.ucdenver.Misc.GlobalConfig;
import edu.ucdenver.Misc.Data;
import edu.ucdenver.Misc.UtilityClass;

import java.io.*;
import java.util.*;

/**
 *
 * @author siddh
 */
public class ExecuteExperiment {

    private static ApproachLoader approachLoader;
    private static Data data;

    public static void main (String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        System.out.println("Starting Evaluation\n------------------\n");

        initializeExperiments();
        executeExperiments();
    }

    public static void initializeExperiments () {
        System.out.println("Attempting to load global config file...");
        GlobalConfig.getInstance();

        System.out.println("Attempting to load approaches and their config files...\n");
        approachLoader = new ApproachLoader();

        try {
            System.out.println("\nLoading data set...\n");
            data = new Data();
            data.getDataSet();
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load data set");
            e.printStackTrace(System.err);
        }
    }

    public static void executeExperiments() throws IOException {
        HashMap<String, String> tempHm = new HashMap<>();

        // Perform execution of approaches
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            System.out.println("Executing " + approach.getName());
            String temp;
            long tStart = System.currentTimeMillis();
            HashMap learningModel = approach.trainApproach(data.getTrainingSet());
            double learningTime = System.currentTimeMillis() - tStart;
            double sizeOfModel = UtilityClass.getMemoryRequiredByLearningModule(learningModel);
            tStart = System.currentTimeMillis();
            Accuracy tempAcc = approach.testDataSetUsingApproach(learningModel, data.getTestingSet());
            double testingTime = System.currentTimeMillis() - tStart;
            temp = "" + tempAcc.calculate() + "," + sizeOfModel + "," + learningTime + "," + testingTime;
            tempHm.put(approach.getName(), temp);
        }


        System.out.println("All approaches have finished execution and evaluation. Generating Results file.");
        String resultsFolderPath = System.getProperty("user.dir") + "/results";
        PrintWriter pw = new PrintWriter(new File(resultsFolderPath + "/results.csv"));
        pw.write("Approaches,");
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            pw.write(approach.getName() + ",");
        }
        pw.write("\r\n");
        pw.write("Accuracy,");
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[0] + ",");
        }
        pw.write("\r\n");
        pw.write("MemoryRequired,");
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[1] + ",");
        }
        pw.write("\r\n");
        pw.write("TimeToTrain,");
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[2] + ",");
        }
        pw.write("\r\n");
        pw.write("TimeToTest,");
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[3] + ",");
        }
        pw.write("\r\n");
        pw.write("ExperimentConfiguration,\r\nApproach Class Names,\r\n");
        for (IApproachInterface approach : approachLoader.getApproaches()) {
            pw.write(approach.getName() + ",\r\n");
        }
        pw.write("End of Approaches,\r\n");
        Object[] keys = GlobalConfig.extraParameters.keySet().toArray();
        for (Object key : keys) {
            pw.write(key + "," + GlobalConfig.extraParameters.get(key) + ",\r\n");
        }
        pw.write("EndOfConfiguration,\r\n");
        pw.write("ClassificationResults,\r\n");
        for (IApproachInterface a : approachLoader.getApproaches()) {
            pw.write(a.getName() + ",\r\n");
            String[][] results = a.getActualAndPredicted();
            for (String[] result : results) {

                pw.write(result[0] + "->" + result[1] + ",\r\n");
            }
            pw.write("EndOfResultsFor:" + a.getName() + ",\r\n");
        }
        pw.write("EndOfClassificationResults,\r\n");

        pw.close();
        for (IApproachInterface temp : approachLoader.getApproaches()) {
            System.out.println("Writing results: " + resultsFolderPath + "/" + temp.getName() + ".csv");
            pw = new PrintWriter(new File(resultsFolderPath + "/" + temp.getName() + ".csv"));
            pw.write(temp.getFeaturesToWrite());
            pw.close();
        }

    }
}
