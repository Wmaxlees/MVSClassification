/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import java.util.HashMap;

/**
 *
 * @author Siddhant Kulkarni
 */
public abstract class IApproachInterface {
    protected Configuration config;
    protected void loadConfigurationFile (String approachName) {
        // Load the algorithm config file
        String configFilePath = System.getProperty("user.dir") + "/config/" + approachName + ".properties";
        this.config = new Configuration(configFilePath);
    }

    /**
     * Every approach must return this array to
     * represent the classification result
     *
     * @return Results
     */
    public abstract String[][] getActualAndPredicted ();

    /**
     * If an approach is being executed and you
     * want to write something to a file with the same
     * name as the approach name return it or return
     * empty String
     *
     * @return File contents
     */
    public abstract String getFeaturesToWrite ();

    /**
     * store the learning model in the form of a <key,value>
     * pair. Size of this Hashmap will be used
     *
     * @param trainingDataSet The dataset to train on
     *
     * @return ????
     */
    public abstract HashMap trainApproach (Individual[] trainingDataSet);

    /**
     * Return accuracy object for system to handle representation
     *
     * @param hm ????
     * @param testingDataSet The dataset to test with
     *
     * @return ????
     */
    public abstract Accuracy testDataSetUsingApproach (HashMap hm, Individual[] testingDataSet);

    /**
     * Get the name of the implementation
     *
     * @return String name
     */
    public abstract String getName();
}

