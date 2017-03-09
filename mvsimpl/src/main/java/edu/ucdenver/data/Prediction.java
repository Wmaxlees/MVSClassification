package edu.ucdenver.data;

/**
 * Created by max on 3/7/17.
 */
public class Prediction {
    private String actualLabel;
    private String predictedLabel;
    private double confidence;

    private Prediction () {}

    /**
     * Used to generate new Prediction objects
     *
     * @param label The actual label for the object
     * @param prediction The prediction made by a given algorithm
     * @return The new Prediction object
     */
    public static Prediction makePrediction (String label, String prediction) {
        // The -1.0 indicates it's not being used
        return Prediction.makePrediction(label, prediction, -1.0d);
    }

    /**
     * Creates a new Prediction object with the correct label and the prediction. The confidence
     * is also included. If confidence is not used by your algorithm, either use a -1.0d to flag
     * the value as not being used or use the 2 parameter makePrediction static function.
     *
     * @param label
     * @param prediction
     * @param confidence
     * @return
     */
    public static Prediction makePrediction (String label, String prediction, double confidence) {
        Prediction result = new Prediction();

        result.actualLabel = label;
        result.predictedLabel = prediction;
        result.confidence = confidence;

        return result;
    }

    /**
     * Get the actual label of the datum
     *
     * @return The actual label
     */
    public String getActualLabel () {
        return this.actualLabel;
    }

    /**
     * Get the predicted label of the datum based on the algorithm used
     *
     * @return The predicted label
     */
    public String getPredictedLabel () {
        return this.predictedLabel;
    }

    /**
     * Get the confidence of the prediction
     *
     * @return The confidence value
     */
    public double getConfidence () {
        return this.confidence;
    }
}
