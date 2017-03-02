/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.*;

public class Individual {
    public Individual (String folder, int fileCount, int numberOfJoints) throws FileNotFoundException {
        frames = new ArrayList<>();

        File[] files = new File(folder).listFiles();
        name = new File(folder).getName();

        for (int i = 0; i < fileCount; i++) {
            int selected = (int) Math.abs(Math.random() * files.length);
            addFramesForThisIndividual(files[selected]);
        }

        this.numberOfJoints = numberOfJoints;
    }

    public void addFramesForThisIndividual (File selectedFile) throws FileNotFoundException {
        Scanner sc = new Scanner(selectedFile);
        HashMap<String, String> hm = new HashMap<>();
        int counter = 0;
        String[] temp;

        while (sc.hasNext()) {
            if (counter >= this.numberOfJoints) {
                this.frames.add(hm);
                counter = 0;
                hm = new HashMap<>();
            }

            temp = sc.nextLine().split(";");
            hm.put(temp[0], temp[1] + ";" + temp[2] + ";" + temp[3]);
            counter++;
        }

        sc.close();
    }

    public String getName () {
        return this.name;
    }

    public HashMap getFrame (int frame) {
        return this.frames.get(frame);
    }

    public ArrayList<HashMap> getFrames () {
        return this.frames;
    }

    public int getNumberOfFrames () {
        return this.frames.size();
    }

    private String name;
    private ArrayList<HashMap> frames;
    private int numberOfJoints;
}
