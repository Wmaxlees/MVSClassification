/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.Misc;

import edu.ucdenver.Configuration;
import edu.ucdenver.IApproachInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Singleton wrapper for the Configuration class
 *
 * @author Siddhant Kulkarni
 */
public class GlobalConfig {
    private static Configuration s_config;

    private GlobalConfig() {
        System.out.println("Reading general config file");

        File configFile = new File(System.getProperty("user.dir") + "/config/global.csv");
        try (Scanner sc = new Scanner(configFile)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String[] values = line.split(",");
                if (values.length < 2) {
                    continue;
                }
                System.out.println(values[0] + "=" + values[1]);
                GlobalConfig.extraParameters.put(values[0], values[1]);
            }
            System.out.println("" + GlobalConfig.extraParameters);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load config file");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public static Configuration getInstance () {
        if (GlobalConfig.s_config == null) {
            String globalConfigFilename = System.getProperty("user.dir") + "/config/global.csv";
            GlobalConfig.s_config = new Configuration(globalConfigFilename);
        }

        return GlobalConfig.s_config;
    }

    public static DatasetHolder dataset;

    //Evaluation Techniques
    
    public static int numberOfInstancesPerIndividual = 2;
    public static int totalNumberOfSeries = 15;
    public static ArrayList<IApproachInterface> approaches=new ArrayList<>();
    public static int frameStepRate=1;
    //Feature based
    public static boolean useStrideLength = true;
    public static boolean useGaitCycleTime = true;
    public static boolean useVelocity = true;
    public static boolean useHeight = true;
    public static boolean useStdDeviation = true;
    public static boolean useEuclidFeatureBased = true;
    //Pattern based
    public static int sizeOfWindow = 20;
    public static int minItemSetSize = 4;
    public static int x = 10, y = 10, z = 10;
    //Similarity based
    public static boolean useEuclidSimilarityBased=true;
    public static boolean useFirstFrames=true;
    //More
    public static HashMap extraParameters=new HashMap();
    
    public static void applyHashMap(HashMap hm){
        Object[] keys=hm.keySet().toArray();
        for(Object key:keys){
            switch (key.toString()){
                case "Instances":
                    numberOfInstancesPerIndividual=Integer.parseInt(""+hm.get(key));
                    break;
                case "IsStrideLengthSelected":
                    useStrideLength=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "IsGaitCycleTimeSelected":
                    useGaitCycleTime=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "IsVelocitySelected":
                    useVelocity=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "IsHeightSelected":
                    useHeight=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "IsStdDevSelected":
                    useStdDeviation=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "IsEDSelectedForFeatureBased":
                    useEuclidFeatureBased=Boolean.parseBoolean(""+hm.get(key));
                    break;
                
                case "IsEDSelectedForTimeSeries":
                    useEuclidSimilarityBased=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "IsFirstNSelectedForTimeSeries":
                    useFirstFrames=Boolean.parseBoolean(""+hm.get(key));
                    break;
                case "SlidingWinSize":
                    sizeOfWindow=Integer.parseInt(""+hm.get(key));
                    break;
                case "ItemSetSize":
                    minItemSetSize=Integer.parseInt(""+hm.get(key));
                    break;
                case "X":
                    x=Integer.parseInt(""+hm.get(key));
                    break;
                case "Y":
                    y=Integer.parseInt(""+hm.get(key));
                    break;
                case "Z":
                    z=Integer.parseInt(""+hm.get(key));
                    break;
                    
                
                        
                default:
                    break;
                    
            }
                    
        }
    }
}
