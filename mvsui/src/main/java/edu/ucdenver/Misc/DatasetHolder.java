/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.Misc;

import edu.ucdenver.Individual;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author siddh
 */
public class DatasetHolder {

    public static Individual[] trainingIndividuals, testingIndividuals;


    public static void getDataSet() throws FileNotFoundException {
        trainingIndividuals = new Individual[GlobalConfig.getInstance().getInt("TrainingSetSize")];
        testingIndividuals = new Individual[GlobalConfig.getInstance().getInt("TestingSetSize")];

        System.out.println("Path to Dataset = " + GlobalConfig.getInstance().getString("DatasetPath"));
        File[] foldersForIndividuals = new File(GlobalConfig.getInstance().getString("DatasetPath")).listFiles();

        boolean flag = false;
        int trainingDone = 0, testingDone = 0;
        System.out.println("Total train to get:" + GlobalConfig.getInstance().getInt("TrainingSetSize"));
        System.out.println("Total test to get:" + GlobalConfig.getInstance().getInt("TestingSetSize"));
        while (!flag) {
            int selected = (int) Math.abs(Math.random() * foldersForIndividuals.length);
            if(!foldersForIndividuals[selected].isDirectory()) {
                continue;
            }
            if(trainingDone >= GlobalConfig.getInstance().getInt("TrainingSetSize")) {
                break;
            }

            trainingIndividuals[trainingDone]=new Individual(foldersForIndividuals[selected].getAbsolutePath(), GlobalConfig.numberOfInstancesPerIndividual, GlobalConfig.getInstance().getInt("SequencesPerMVS"));

            trainingDone++;
            if(testingDone< GlobalConfig.getInstance().getInt("TestingSetSize")){
                testingIndividuals[testingDone]=new Individual(foldersForIndividuals[selected].getAbsolutePath(), 1, GlobalConfig.getInstance().getInt("SequencesPerMVS"));
                testingDone++;
            }
        }

    }

    public static void printDataSet(){
        System.out.println("*****Printing Training Dataset****");
        for(int i=0;i<trainingIndividuals.length;i++){
            System.out.println(""+trainingIndividuals[i].getName());
        }
        System.out.println("*****Printing Testing Dataset****");
        for(int i=0;i<testingIndividuals.length;i++){
            System.out.println(""+testingIndividuals[i].getName());
            System.out.println(""+testingIndividuals[i].getNumberOfFrames());
         /*   for(int j=0;j<testingIndividuals[i].frames.size();j++){
                System.out.println(""+testingIndividuals[i].frames.get(j).toString());
            }*/
        }
    }
}

