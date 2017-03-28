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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to hold all of the data
 *
 * @author siddh
 * @author W. Max Lees
 */
public class Data {
    private DataClass[] trainingSet;
    private DataClass[] testingSet;

    private String dataPath;

    private int depth;
    private int numberOfSequences;
    private int batchSize;
    private int batchNumber;

    private int[] testFolderIndices;

    private Data (String dataPath, int expectedDepth, int expectedNumberOfSequences, int batchSize, int testingSetSize) {
        this.dataPath = dataPath;
        this.depth = expectedDepth;
        this.numberOfSequences = expectedNumberOfSequences;
        this.batchSize = batchSize;
        this.batchNumber = 0;

        // Initialize the test set stuff
        this.testingSet = new DataClass[testingSetSize];
        this.testFolderIndices = new int[testingSet.length];

        // Get random test data folders
        File[] foldersForDataClasses = new File(dataPath).listFiles();
        for (int i = 0; i < this.testingSet.length; ++i) {
            int randomFolderIndex = (int)Math.floor(Math.random() * foldersForDataClasses.length);

            // Check for repeats
            boolean repeating = false;
            for (int j = 0; j < i; ++j) {
                if (this.testFolderIndices[j] == randomFolderIndex) {
                    --i; // Repeat the last loop iteration
                    repeating = true;
                    break;
                }
            }

            if (!repeating) {
                this.testFolderIndices[i] = randomFolderIndex;
            }
        }

        Arrays.sort(this.testFolderIndices);
    }

    /**
     *
     *
     * @return False if there are no more files to load
     * @throws FileNotFoundException
     */
    public boolean loadNextTrainingBatch () throws FileNotFoundException {
        // Get the data class folders
        File[] foldersForDataClasses = new File(dataPath).listFiles();

        // Check if there are no more files to check
        if ((this.batchSize * this.batchNumber) >= foldersForDataClasses.length) {
            return false;
        }

        // Make enough space to hold all the training data
        ArrayList<DataClass> trainingSetHolder = new ArrayList<>();
        trainingSet = new DataClass[this.batchSize];

        // Load the data
        int offset = (this.batchSize * this.batchNumber);
        for (int folderIndex = 0; folderIndex < this.batchSize; ++folderIndex) {
            // Get all the data files
            File[] dataFiles = foldersForDataClasses[folderIndex].listFiles(new MVSFilenameFilter());

            // Skip empty folders
            if (dataFiles.length == 0) {
                continue;
            }

            // Get the class label
            String label = this.getClassLabelFromDirectory(foldersForDataClasses[folderIndex]);

            // Check if we are using this folder for testing as well
            int index = Arrays.binarySearch(this.testFolderIndices, offset + folderIndex);
            boolean testingFolder = (index > -1 && this.testFolderIndices[index] == offset + folderIndex);

            // Initialize the data class
            this.trainingSet[folderIndex] = new DataClass(label);

            // Load the files
            try {
                int fileIndex = !testingFolder ? 0 : 1;

                // Loop through files and load them as MVSs
                for (; fileIndex < dataFiles.length; ++fileIndex) {
                    MultivariateSpatiotemporalSequence mvs = MultivariateSpatiotemporalSequence.load(dataFiles[fileIndex]);
                    this.trainingSet[folderIndex].addInstance(mvs);
                }
            } catch (IOException e) {
                System.err.println("Failed to load data files for: " + label);
                e.printStackTrace(System.err);
            }
        }

        ++this.batchNumber;

        return true;
    }

    public void rewind () {
        this.batchNumber = 0;
    }

    public void loadTestingSet () {
        // Get the data class folders
        File[] foldersForDataClasses = new File(dataPath).listFiles();

        for (int i = 0; i < testingSet.length; ++i) {
            File testFile = foldersForDataClasses[testFolderIndices[i]].listFiles(new MVSFilenameFilter())[0];
            try {
                MultivariateSpatiotemporalSequence mvs = MultivariateSpatiotemporalSequence.load(testFile);
                this.testingSet[i] = new DataClass(mvs.getLabel());
                this.testingSet[i].addInstance(mvs);
            } catch (IOException e) {
                System.err.println("Failed to load test data: " + testFile);
            }
        }
    }

    public DataClass[] getCurrentTrainingSet () {
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

    public static class DataBuilder {
        private String dataPath;
        private int depth;
        private int numberOfSequences;
        private int batchSize;
        private int testingSetSize;

        public DataBuilder setDataPath (String dataPath) {
            this.dataPath = dataPath;

            return this;
        }

        public DataBuilder setDepth (int depth) {
            this.depth = depth;

            return this;
        }

        public DataBuilder setNumberOfSequences (int numberOfSequences) {
            this.numberOfSequences = numberOfSequences;

            return this;
        }

        public DataBuilder setBatchSize (int batchSize) {
            this.batchSize = batchSize;

            return this;
        }

        public DataBuilder setTestingSetSize (int testingSetSize) {
            this.testingSetSize = testingSetSize;

            return this;
        }

        public Data build () {
            if (this.dataPath.isEmpty()) {
                System.err.println("Data path must be set before calling build() method on DataBuilder");
                return null;
            }

            if (this.depth == 0) {
                System.err.println("Depth must be set before calling build() method on DataBuilder");
                return null;
            }

            if (this.numberOfSequences == 0) {
                System.err.println("Number of sequences must be set before calling build() method on DataBuilder");
                return null;
            }

            if (this.batchSize == 0) {
                System.err.println("Batch size must be set before calling build() method on DataBuilder");
                return null;
            }

            if (this.testingSetSize == 0) {
                System.err.println("Testing set size needs to be set before calling build() method on DataBuilder");
                return null;
            }

            return new Data(this.dataPath, this.depth, this.numberOfSequences, this.batchSize, this.testingSetSize);
        }
    }
}

