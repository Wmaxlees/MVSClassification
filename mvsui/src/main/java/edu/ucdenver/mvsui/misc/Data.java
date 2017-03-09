/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.mvsui.misc;

import edu.ucdenver.data.DataClass;
import edu.ucdenver.data.MultivariateSpatiotemporalSequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 *
 * @author siddh
 */
public class Data {
    private DataClass[] trainingSet;
    private DataClass[] testingSet;

    private int depth;
    private int numberOfSequences;

    public Data (int expectedDepth, int expectedNumberOfSequences) {
        this.depth = expectedDepth;
        this.numberOfSequences = expectedNumberOfSequences;
    }

    public void loadDataSet(String dataPath, int trainingSetSize, int testingSetSize) throws FileNotFoundException {
        // Get the data class folders
        File[] foldersForDataClasses = new File(dataPath).listFiles();

        // Make enough space to hold all the training data
        trainingSet = new DataClass[foldersForDataClasses.length];
        // Make enough space to hold the testing data
        testingSet = new DataClass[testingSetSize];
        int[] testFolderIndices = new int[testingSet.length];

        // Get random test data folders
        for (int i = 0; i < testingSet.length; ++i) {
            int randomFolderIndex = (int)Math.floor(Math.random() * foldersForDataClasses.length);

            // Check for repeats
            boolean repeating = false;
            for (int j = 0; j < i; ++j) {
                if (testFolderIndices[j] == randomFolderIndex) {
                    --i; // Repeat the last loop iteration
                    repeating = true;
                    break;
                }
            }

            if (!repeating) {
                testFolderIndices[i] = randomFolderIndex;
            }
        }

        // Load the data
        int testingSetIndex = 0;
        for (int folderIndex = 0; folderIndex < foldersForDataClasses.length; ++folderIndex) {
            // Get all the data files
            File[] dataFiles = foldersForDataClasses[folderIndex].listFiles(new MVSFilenameFilter());

            // Skip empty folders
            if (dataFiles.length == 0) {
                continue;
            }

            // Get the class label
            String label = this.getClassLabelFromDirectory(foldersForDataClasses[folderIndex]);

            // Check if we are using this folder for testing as well
            boolean testingFolder = false;
            for (int testFolderIndex : testFolderIndices) {
                if (folderIndex == testFolderIndex) {
                    testingFolder = true;
                    break;
                }
            }

            // Initialize the data class
            this.trainingSet[folderIndex] = new DataClass(label);
            if (testingFolder) {
                this.testingSet[testingSetIndex] = new DataClass(label);
            }

            // Load the files
            try {
                int fileIndex = 0;

                // Add the testing MVS if needed
                if (testingFolder) {
                    MultivariateSpatiotemporalSequence mvs = MultivariateSpatiotemporalSequence.load(dataFiles[fileIndex]);
                    ++fileIndex;
                    this.testingSet[testingSetIndex].addInstance(mvs);
                    ++testingSetIndex;
                }

                // Loop through files and load them as MVSs
                for (; fileIndex < trainingSetSize && fileIndex < dataFiles.length; ++fileIndex) {
                    MultivariateSpatiotemporalSequence mvs = MultivariateSpatiotemporalSequence.load(dataFiles[fileIndex]);
                    this.trainingSet[folderIndex].addInstance(mvs);
                }
            } catch (IOException e) {
                System.err.println("Failed to load data files for: " + label);
                e.printStackTrace(System.err);
            }
        }

    }

    public DataClass[] getTrainingSet () {
        return this.trainingSet;
    }

    public DataClass[] getTestingSet () {
        return this.testingSet;
    }

    public int getDepth () {
        return this.depth;
    }

    public int getNumberOfSequences () {
        return this.numberOfSequences;
    }

    private String getClassLabelFromDirectory (File directory) {
        String[] absolutePath = directory.getAbsolutePath().split("/");

        return absolutePath[absolutePath.length-1];
    }

    private class MVSFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            if (name.endsWith(".mvs")) {
                return true;
            }

            return false;
        }
    }
}

