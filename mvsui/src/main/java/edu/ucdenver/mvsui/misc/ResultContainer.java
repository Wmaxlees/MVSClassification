package edu.ucdenver.mvsui.misc;

import edu.ucdenver.data.Prediction;
import edu.ucdenver.data.ResultSet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by max on 3/8/17.
 */
public class ResultContainer {
    private String[] labels;
    private ResultSet[] results;
    private long[] size;
    private long[] trainingTime;
    private long[] testingTime;

    /**
     * Create a new ResultContainer
     *
     * @param size The number of approaches to contain results for
     */
    public ResultContainer (int size) {
        this.labels = new String[size];
        this.results = new ResultSet[size];
        this.size = new long[size];
        this.trainingTime = new long[size];
        this.testingTime = new long[size];
    }

    /**
     * Set the labels for the specific index
     *
     * @param index The index to set
     * @param label The labels to save
     */
    public void setLabel (int index, String label) {
        if (this.labels.length < index) {
            throw new IndexOutOfBoundsException();
        }

        this.labels[index] = label;
    }

    /**
     * Set the results for the specific index
     *
     * @param index The index to set
     * @param results The results to set
     */
    public void setResults (int index, ResultSet results) {
        if (this.results.length < index) {
            throw new IndexOutOfBoundsException();
        }

        this.results[index] = results;
    }

    /**
     * Set the size for the specific index
     *
     * @param index The index to set
     * @param size The size to set
     */
    public void setSize (int index, long size) {
        if (this.size.length < index) {
            throw new IndexOutOfBoundsException();
        }

        this.size[index] = size;
    }

    /**
     * Set the training time for the specific index
     *
     * @param index The index to set
     * @param time The time to set
     */
    public void setTrainingTime (int index, long time) {
        if (this.trainingTime.length < index) {
            throw new IndexOutOfBoundsException();
        }

        this.trainingTime[index] = time;
    }

    /**
     * Set the testing time for the specific index
     *
     * @param index The index to set
     * @param time The time to set
     */
    public void setTestingTime (int index, long time) {
        if (this.testingTime.length < index) {
            throw new IndexOutOfBoundsException();
        }

        this.testingTime[index] = time;
    }

    public void writeToFilesWithConfig (String configCSV) throws IOException {
        String generalFilename = System.getProperty("user.dir") + "/results/results.csv";
        BufferedWriter out = new BufferedWriter(new FileWriter(generalFilename));

        // Write the approach names
        out.write("Approaches,");
        for (String label : this.labels) {
            out.write(label + ",");
        }
        out.newLine();

        // Write the accuracy
        out.write("Accuracy,");
        for (ResultSet resultSet : this.results) {
            out.write(resultSet.calculateAccuracy() + ",");
        }
        out.newLine();

        // Write the memory requirements
        out.write("MemoryRequired,");
        for (long memory : this.size) {
            out.write(memory + ",");
        }
        out.newLine();

        // Write training time
        out.write("TimeToTrain,");
        for (long time : this.trainingTime) {
            out.write(time + ",");
        }
        out.newLine();

        // Write the testing time
        out.write("TimeToTest,");
        for (long time : this.testingTime) {
            out.write(time + ",");
        }
        out.newLine();

        // Write the approach names
        out.write("Experimental Configuration,");
        out.newLine();
        out.write("Approach Class Names,");
        out.newLine();
        for (String label : this.labels) {
            out.write(label + ",");
            out.newLine();
        }
        out.write("End of Approaches,");
        out.newLine();

        // Write the config info to the file
        out.write(configCSV);
        out.write("EndOfConfiguration,");
        out.newLine();

        // Write the individual results per approach
        out.write("ClassificationResults,");
        out.newLine();
        for (int i = 0; i < this.results.length; ++i) {
            out.write(this.labels[i] + ",");
            out.newLine();
            for (Prediction prediction : this.results[i].getDetails()) {
                out.write(prediction.getActualLabel() + "->" + prediction.getPredictedLabel());
                out.newLine();
            }
            out.write("EndOfResultFor:" + this.labels[i] + ",");
            out.newLine();
        }
        out.write("EndOfClassificationResults,");
        out.newLine();

        out.close();
    }


}
