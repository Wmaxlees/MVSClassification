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

    private GlobalConfig() {}

    public static Configuration getInstance () {
        if (GlobalConfig.s_config == null) {
            String globalConfigFilename = System.getProperty("user.dir") + "/config/global.properties";
            GlobalConfig.s_config = new Configuration(globalConfigFilename);
        }

        return GlobalConfig.s_config;
    }
    
    public static int numberOfInstancesPerIndividual = 2;
    public static HashMap extraParameters=new HashMap();
}
