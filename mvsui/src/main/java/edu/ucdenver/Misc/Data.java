/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.Misc;

import edu.ucdenver.Configuration;
import edu.ucdenver.Individual;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author siddh
 */
public class Data {

    private Individual[] trainingSet;
    private Individual[] testingSet;

    private Configuration config;

    public Data () {
        this.config = GlobalConfig.getInstance();
    }

//    public void loadDataSet () {
//        // Getting folders in data path
//        System.out.println("Loading data from: " + this.config.getString("DataPath"));
//        File[] labelFolders;
//        labelFolders = new File(this.config.getString("DataPath")).listFiles();
//
//        // Creating arrays to hold data
//        this.trainingSet = new Individual[this.config.getInt("TrainingSetSize") * labelFolders.length];
//        this.testingSet = new Individual[this.config.getInt("TestingSetSize") * labelFolders.length];
//
//        for (File folder : labelFolders) {
//
//        }
//    }

    public void getDataSet() throws FileNotFoundException {
        trainingSet = new Individual[this.config.getInt("TrainingSetSize")];
        testingSet = new Individual[this.config.getInt("TestingSetSize")];

        System.out.println("Path to Data = " + this.config.getString("DataPath"));
        File[] foldersForIndividuals = new File(this.config.getString("DataPath")).listFiles();

        boolean flag = false;
        int trainingDone = 0, testingDone = 0;
        while (!flag) {
            int selected = (int) Math.abs(Math.random() * foldersForIndividuals.length);
            if(!foldersForIndividuals[selected].isDirectory()) {
                continue;
            }
            if(trainingDone >= GlobalConfig.getInstance().getInt("TrainingSetSize")) {
                break;
            }

            trainingSet[trainingDone]=new Individual(foldersForIndividuals[selected].getAbsolutePath(), GlobalConfig.numberOfInstancesPerIndividual, this.config.getInt("SequencesPerMVS"));

            trainingDone++;
            if(testingDone< GlobalConfig.getInstance().getInt("TestingSetSize")){
                testingSet[testingDone]=new Individual(foldersForIndividuals[selected].getAbsolutePath(), 1, this.config.getInt("SequencesPerMVS"));
                testingDone++;
            }
        }

    }

    public Individual[] getTrainingSet () {
        return this.trainingSet;
    }

    public Individual[] getTestingSet () {
        return this.testingSet;
    }

    public void printDataSet(){
        System.out.println("*****Printing Training Dataset****");
        for(int i=0;i<trainingSet.length;i++){
            System.out.println(""+trainingSet[i].getName());
        }
        System.out.println("*****Printing Testing Dataset****");
        for(int i=0;i<testingSet.length;i++){
            System.out.println(""+testingSet[i].getName());
            System.out.println(""+testingSet[i].getNumberOfFrames());
         /*   for(int j=0;j<testingIndividuals[i].frames.size();j++){
                System.out.println(""+testingIndividuals[i].frames.get(j).toString());
            }*/
        }
    }
}

