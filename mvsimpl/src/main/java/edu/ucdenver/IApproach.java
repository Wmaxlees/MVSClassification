/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import edu.ucdenver.data.DataClass;
import edu.ucdenver.data.ResultSet;
import edu.ucdenver.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Siddhant Kulkarni
 */
public abstract class IApproach implements Serializable {
    protected Configuration config;
    protected int expectedDepth;
    protected int expectedNumberOfSequences;

    /**
     * This function will be called in order to perform any necessary
     * initialization for the approach.
     *
     * @param depth The depth of the input data
     * @param numberOfSequences The number of sequences to expect in the MVS
     */
    public void initialize (int depth, int numberOfSequences) {
        this.expectedDepth = depth;
        this.expectedNumberOfSequences = numberOfSequences;
    }

    /**
     * Must be called in the constructor if the approach will use a config file
     *
     * @param approachName The name of the approach
     */
    protected void loadConfigurationFile (String approachName) {
        // Load the algorithm config file
        String configFilePath = System.getProperty("user.dir") + "/config/" + approachName + ".properties";
        this.config = new Configuration(configFilePath);
    }

    /**
     * Get the directory that the algorithm can use to store any necessary data
     *
     * @return The string name of the directory to be used
     */
    protected String getWorkingDirectory () {
        String configFilePath = System.getProperty("user.dir") + "/temp/" + getName();

        // Make sure the folder exists
        File directory = new File(System.getProperty("user.dir") + "/temp");
        if (!directory.exists()) {
            directory.mkdir();
        }
        directory = new File(configFilePath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        return configFilePath;
    }

    /**
     * Implementations of this function should train the algorithm
     * based on the input data set
     *
     * @param trainingDataSet The dataset to train with
     */
    public abstract void train (DataClass[] trainingDataSet);

    /**
     * Run the algorithm on the testing data set and return the
     * results of the test
     *
     * @param testingDataSet The dataset to test with
     *
     * @return ResultSet of the approach
     */
    public abstract ResultSet test (DataClass[] testingDataSet);

    /**
     * Get the name of the implementation
     *
     * @return String name
     */
    public abstract String getName();

    /**
     * Write the details of the approach to the file
     *
     * @param filename The file to write to
     * @throws IOException Thrown if errors occur with the file IO
     */
    public abstract void writeDetails (String filename) throws IOException;
}

