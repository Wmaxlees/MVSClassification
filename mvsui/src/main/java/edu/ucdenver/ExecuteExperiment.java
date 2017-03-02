/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import edu.ucdenver.Misc.ApproachLoader;
import edu.ucdenver.Misc.GlobalConfig;
import edu.ucdenver.Misc.DatasetHolder;
import edu.ucdenver.Misc.UtilityClass;
import edu.ucdenver.UI.MainWindowAttemptOne;

import java.io.*;
import java.util.*;

/**
 *
 * @author siddh
 */
public class ExecuteExperiment {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        //Arguments
        /*
         1. Whether UI or NONUI
         2. CSV configuration file
         */
        if (args.length != 1) {
            System.out.println("Incorrect number of arguments");
            System.out.println("Argument 1 should indicate whether or not UI is to be instantiated. Value must be \"UI\" or \"NOUI\"");
            System.out.println("Please specify the correct arguments and try again.");
        } else {
            if (args[0].equals("UI")) {
                System.out.println("Launching UI");
                MainWindowAttemptOne.dir = args[1];
                MainWindowAttemptOne.main(args);
            } else {
                System.out.println("Starting Evaluation");

                System.out.println("Attempting to load approaches");
                ApproachLoader.getInstance();

                System.out.println("Attempting to load global config file");
                GlobalConfig.getInstance();

                executeExperiments();
            }
        }

    }

    public static void executeExperiments() throws IOException {
        GlobalConfig.applyHashMap(GlobalConfig.extraParameters);
        HashMap<String, String> tempHm = new HashMap<>();
        //--------------------------------------------------------------------------------------
//        UtilityClass.loadSelectedSeries((String) GlobalConfig.extraParameters.get("SeriesToConsider"));
        UtilityClass.loadExtraParameters();
        //--------------------------------------------------------------------------------------
        DatasetHolder.getDataSet();

        // Perform execution of approaches
        for (IApproachInterface approach : ApproachLoader.getInstance().getApproaches()) {
            System.out.println("Executing " + approach.getName());
            String temp;
            long tStart = System.currentTimeMillis();
            HashMap learningModel = approach.trainApproach(DatasetHolder.trainingIndividuals);
            double learningTime = System.currentTimeMillis() - tStart;
            double sizeOfModel = UtilityClass.getMemoryRequiredByLearningModule(learningModel);
            tStart = System.currentTimeMillis();
            Accuracy tempAcc = approach.testDataSetUsingApproach(learningModel, DatasetHolder.testingIndividuals);
            double testingTime = System.currentTimeMillis() - tStart;
            temp = "" + tempAcc.calculate() + "," + sizeOfModel + "," + learningTime + "," + testingTime;
            tempHm.put(approach.getName(), temp);
        }


        System.out.println("All approaches have finished execution and evaluation. Generating Results file.");
        PrintWriter pw = new PrintWriter(new File("d:/Results"));
        pw.write("Approaches,");
        for (IApproachInterface approach : GlobalConfig.approaches) {
            pw.write(approach.getName() + ",");
        }
        pw.write("\r\n");
        pw.write("Accuracy,");
        for (IApproachInterface approach : GlobalConfig.approaches) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[0] + ",");
        }
        pw.write("\r\n");
        pw.write("MemoryRequired,");
        for (IApproachInterface approach : GlobalConfig.approaches) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[1] + ",");
        }
        pw.write("\r\n");
        pw.write("TimeToTrain,");
        for (IApproachInterface approach : GlobalConfig.approaches) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[2] + ",");
        }
        pw.write("\r\n");
        pw.write("TimeToTest,");
        for (IApproachInterface approach : GlobalConfig.approaches) {
            pw.write(((String) tempHm.get(approach.getName())).split(",")[3] + ",");
        }
        pw.write("\r\n");
        pw.write("ExperimentConfiguration,\r\nApproach Class Names,\r\n");
        for (IApproachInterface approach : GlobalConfig.approaches) {
            pw.write(approach.getName() + ",\r\n");
        }
        pw.write("End of Approaches,\r\n");
        Object[] keys = GlobalConfig.extraParameters.keySet().toArray();
        for (Object key : keys) {
            pw.write(key + "," + GlobalConfig.extraParameters.get(key) + ",\r\n");
        }
        pw.write("EndOfConfiguration,\r\n");
        pw.write("ClassificationResults,\r\n");
        for (IApproachInterface a : GlobalConfig.approaches) {
            pw.write(a.getName() + ",\r\n");
            String[][] results = a.getActualAndPredicted();
            for (String[] result : results) {

                pw.write(result[0] + "->" + result[1] + ",\r\n");
            }
            pw.write("EndOfResultsFor:" + a.getName() + ",\r\n");
        }
        pw.write("EndOfClassificationResults,\r\n");

        pw.close();
        for (IApproachInterface temp : GlobalConfig.approaches) {
            System.out.println("Writing " + temp.getName() + ".csv at " + new File(temp.getName() + ".csv").getAbsolutePath());
            pw = new PrintWriter(new File(temp.getName() + ".csv"));
            pw.write(temp.getFeaturesToWrite());
            pw.close();
        }

    }
}
