/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver;

/**
 *
 * @author siddh
 */
public class Accuracy {
    public Accuracy (String [][] results) {
        // This hashmap will have 2 columns Actual Name of individual,
        // identified by your approach
        total = results.length;

        for (String[] result : results) {
            if (result[0].equals(result[1])) {
                accurate++;
            }
        }

    }

    /**
     * Calculate the accuracy
     *
     * @return float The accuracy
     */
    public double calculate () {
        return ((double)accurate / (double)total)*100.0D;
    }

    private int accurate = 0;
    private int total = 0;
}
