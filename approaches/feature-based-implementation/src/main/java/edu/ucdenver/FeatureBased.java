/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author siddh
 */
public class FeatureBased extends IApproachInterface {
    public String toWrite;

    public FeatureBased () {
        this.loadConfigurationFile(this.getName());
    }

    public double[] getMeanOfPointLocations(Individual ind) {
        double[] means = new double[ind.getFrame(0).keySet().toArray().length * 3];
        for (int i = 0; i < means.length; i++) {
            means[i] = 0.0;
        }

        for (int i = 0; i < ind.getNumberOfFrames(); i++) {
            Object[] points = ind.getFrame(i).keySet().toArray();
            for (int j = 0; j < points.length; j++) {
                //System.out.println(""+points[j]);
                String[] vals = (ind.getFrame(i).get(points[j]) + "").split(";");
                means[(j * 3)] += Double.parseDouble(vals[0]);
                means[(j * 3) + 1] += Double.parseDouble(vals[1]);
                means[(j * 3) + 2] += Double.parseDouble(vals[2]);
            }
        }

        for (int i = 0; i < means.length; i++) {
            means[i] = (double) (means[i] / ind.getNumberOfFrames());
        }
        return means;
    }

    public double[] getStdDevPointLocations(Individual ind) {
        double[] means = getMeanOfPointLocations(ind);
        double[] stddev = new double[ind.getFrame(0).keySet().toArray().length * 3];
        for (int i = 0; i < stddev.length; i++) {
            stddev[i] = 0.0;
        }

        for (int i = 0; i < ind.getNumberOfFrames(); i++) {
            Object[] points = ind.getFrame(i).keySet().toArray();
            for (int j = 0; j < points.length; j++) {
                //System.out.println(""+points[j]);
                String[] vals = (ind.getFrame(i).get(points[j]) + "").split(";");
                stddev[(j * 3)] = (means[(j * 3)] - Double.parseDouble(vals[0])) * (means[(j * 3)] - Double.parseDouble(vals[0]));
                stddev[(j * 3) + 1] = (means[(j * 3) + 1] - Double.parseDouble(vals[1])) * (means[(j * 3) + 1] - Double.parseDouble(vals[1]));
                stddev[(j * 3) + 2] = (means[(j * 3) + 2] - Double.parseDouble(vals[2])) * (means[(j * 3) + 2] - Double.parseDouble(vals[2]));
            }
        }

        for (int i = 0; i < means.length; i++) {
            stddev[i] = Math.sqrt((double) (stddev[i] / ind.getNumberOfFrames()));
            //System.out.println(""+stddev[i]);
        }
        return stddev;
    }

    public double getHorizontalRange(Individual ind) {
        double hormin = Double.MAX_VALUE, hormax = Double.MIN_VALUE;
        for (int i = 0; i < ind.getNumberOfFrames(); i++) {
            Object[] datapoints = ind.getFrame(i).keySet().toArray();
            for (int j = 0; j < datapoints.length; j++) {
                double tempX = Double.parseDouble(("" + ind.getFrame(i).get(datapoints[j])).split(";")[0]);
                if (tempX < hormin) {
                    hormin = tempX;
                }
                if (tempX > hormax) {
                    hormax = tempX;
                }
            }
        }
        return hormax - hormin;
    }

    public double getVerticalRange(Individual ind) {
        double vermin = Double.MAX_VALUE, vermax = Double.MIN_VALUE;
        for (int i = 0; i < ind.getNumberOfFrames(); i++) {
            Object[] datapoints = ind.getFrame(i).keySet().toArray();
            for (int j = 0; j < datapoints.length; j++) {
                double tempX = Double.parseDouble(("" + ind.getFrame(i).get(datapoints[j])).split(";")[1]);
                if (tempX < vermin) {
                    vermin = tempX;
                }
                if (tempX > vermax) {
                    vermax = tempX;
                }
            }
        }
        return vermax - vermin;
    }

    public ArrayList<Double> getFeatureForIndividual(Individual ind) throws FileNotFoundException {
        toWrite += ind.getName() + ",";
        ArrayList<Double> features = new ArrayList<>();
        double tempFeature = 0;
        if (this.config.getBoolean("IsHorizontalRangeSelected")) {
            tempFeature=getHorizontalRange(ind);
            features.add(tempFeature);
            toWrite+=tempFeature+",";
        }
        if (this.config.getBoolean("IsVerticalRangeSelected")) {
            tempFeature=getVerticalRange(ind);
            features.add(tempFeature);
            toWrite+=tempFeature+",";
        }
        if (this.config.getBoolean("IsMeanSelected")) {
            double[] meanvals = getMeanOfPointLocations(ind);
            for (int j = 0; j < meanvals.length; j++) {
                features.add(meanvals[j]);
                toWrite+=meanvals[j]+",";
            }
        }
        if (this.config.getBoolean("IsStdDevSelected")) {
            double[] stddevvals = getStdDevPointLocations(ind);
            for (int j = 0; j < stddevvals.length; j++) {
                features.add(stddevvals[j]);
                toWrite+=stddevvals[j]+",";
            }

        }
        toWrite+="\r\n";
        return features;
    }

    @Override
    public HashMap trainApproach(Individual[] trainingDataSet) {
        toWrite = buildFirstLine();

        HashMap featureVectors = new HashMap();

        for (Individual individual : trainingDataSet) {
            try {
                featureVectors.put(individual.getName(), getFeatureForIndividual(individual));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return featureVectors;
    }

    public double getManhattanDistance(ArrayList<Double> f1, ArrayList<Double> f2) {

        double diff = 0.0;
        for (int i = 0; i < f1.size(); i++) {
            diff += Math.abs(f1.get(i) - f2.get(i));
        }
        return diff;
    }

    public double getEuclidDistance(ArrayList<Double> f1, ArrayList<Double> f2) {
        double diff = 0.0;
        for (int i = 0; i < f1.size(); i++) {
            diff += ((f1.get(i) - f2.get(i)) * (f1.get(i) - f2.get(i)));
        }
        return Math.sqrt(diff);
    }

    public String[][] results;

    @Override
    public Accuracy testDataSetUsingApproach(HashMap hm, Individual[] testingDataSet) {
        results = new String[testingDataSet.length][2];

        System.out.println("Tested using Feature Based");
        HashMap distances = new HashMap();
        for (int i = 0; i < testingDataSet.length; i++) {
            ArrayList<Double> features=null;
            try {
                features = getFeatureForIndividual(testingDataSet[i]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Object[] trainedInds = hm.keySet().toArray();
            double mindist = Double.MAX_VALUE;
            String predicted = null;
            for (int j = 0; j < trainedInds.length; j++) {
                if (this.config.getBoolean("IsEDSelectedForFeatureBased")) {
                    double tempdist = getEuclidDistance(features, (ArrayList<Double>) hm.get(trainedInds[j]));
                    if (tempdist < mindist) {
                        mindist = tempdist;
                        predicted = trainedInds[j] + "";
                    }
                } else {
                    double tempdist = getManhattanDistance(features, (ArrayList<Double>) hm.get(trainedInds[j]));
                    if (tempdist < mindist) {
                        mindist = tempdist;
                        predicted = trainedInds[j] + "";
                    }
                }

            }
            results[i][0] = testingDataSet[i].getName();
            results[i][1] = predicted;

        }

        for (String[] result : results) {
            System.out.println("---------" + result[0] + "-----" + result[1]);
        }
        return new Accuracy(results);
    }

    @Override
    public String getName() {
        return "FeatureBased";
    }

    @Override
    public String[][] getActualAndPredicted() {
        return results;
    }

    private String buildFirstLine() {
        String line = "Individual,";

        if (this.config.getBoolean("IsHorizontalRangeSelected")) {
            line += "HorizontalRange,";
        }
        if (this.config.getBoolean("IsVerticalRangeSelected")) {
            line += "VerticalRange,";
        }

        int length = config.getInt("NumberOfSequences");
        if (this.config.getBoolean("IsMeanSelected")) {
            for (int i = 0; i < length; i++) {
                line += "MeanOfDataPoint" + i + "(X),";
                line += "MeanOfDataPoint" + i + "(Y),";
                line += "MeanOfDataPoint" + i + "(Z),";
            }
        }
        if (this.config.getBoolean("IsStdDevSelected")) {
            for (int i = 0; i < length; i++) {
                line += "StdDevOfDataPoint" + i + "(X),";
                line += "StdDevOfDataPoint" + i + "(Y),";
                line += "StdDevOfDataPoint" + i + "(Z),";
            }
        }
        line+="\r\n";
        return line;
    }

    @Override
    public String getFeaturesToWrite() {
        return toWrite;
    }
}