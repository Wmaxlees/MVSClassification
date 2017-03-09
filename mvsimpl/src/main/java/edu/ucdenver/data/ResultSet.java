/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.data;

import java.util.ArrayList;

/**
 *
 * @author siddh
 */
public class ResultSet {
    private int accurate;
    private int total;
    private ArrayList<Prediction> details;

    /**
     * Create a new empty results set
     */
    public ResultSet () {
        this.details = new ArrayList<>();
        this.accurate = 0;
        this.total = 0;
    }

    /**
     * Create a new results set based on an existing set
     * of predictions
     *
     * @param predictions The list of predictions
     */
    public ResultSet (ArrayList<Prediction> predictions) {
        this.details = predictions;
        this.total = predictions.size();

        // Count the number of accurate predictions
        this.accurate = 0;
        for (Prediction prediction : predictions) {
            if (prediction.getActualLabel().equals(prediction.getPredictedLabel())) {
                ++this.accurate;
            }
        }
    }

    /**
     * Add an existing prediction and update the accuracy value
     *
     * @param prediction The prediction to add to the result set
     */
    public void addPrediction(Prediction prediction) {
        this.details.add(prediction);
        ++this.total;
        if (prediction.getActualLabel().equals(prediction.getPredictedLabel())) {
            ++this.accurate;
        }
    }

    /**
     * Calculate the accuracy
     *
     * @return float The accuracy
     */
    public double calculateAccuracy () {
        return ((double)accurate / (double)total)*100.0D;
    }

    /**
     * Get the details of the predictions
     *
     * @return A list of all predictions
     */
    public ArrayList<Prediction> getDetails () {
        return this.details;
    }

    /**
     * Add a prediction to the result set
     *
     * @param label The actual label of the datum
     * @param prediction The predicted label of the datum
     */
    public void predict (String label, String prediction) {
        // The -1.0D is a flag indicating it isn't being used
        this.predict(label, prediction, -1.0D);
    }

    /**
     * Add a prediction to the result set
     *
     * @param label The actual label of the datum
     * @param prediction The predicted label of the datum
     * @param confidence The confidence % for the prediction
     */
    public void predict (String label, String prediction, double confidence) {
        this.details.add(Prediction.makePrediction(label, prediction, confidence));
        ++this.total;

        if (label.equals(prediction)) {
            ++this.accurate;
        }
    }
}
