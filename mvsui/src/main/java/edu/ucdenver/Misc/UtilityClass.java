/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.Misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Siddhant Kulkarni
 */
public class UtilityClass {

    public static void createAndDisplayBarGraph(String frameTitle, String graphTitle, String xAxisTitle, String yAxisTitle, ParamTypeEnum param) {
      
        
    }
    
    public static long getMemoryRequiredByLearningModule(HashMap hm) throws IOException{
        return sizeof(hm);
    }
     public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }

    public static void displaySimpleMessageBox(String message){
        JOptionPane.showMessageDialog(null, message, "InfoBox", JOptionPane.INFORMATION_MESSAGE);
    }
    
//    public static void loadSelectedSeries (String list){
//        String[] arr = list.split("-");
//        for(String str:arr){
//            GlobalConfig.selectedSeries.add(Integer.parseInt(str));
//        }
//    }
    
    public static void loadExtraParameters(){
        try {
            GlobalConfig.numberOfInstancesPerIndividual=Integer.parseInt(""+ GlobalConfig.extraParameters.get("Instances"));
            GlobalConfig.frameStepRate=Integer.parseInt(""+ GlobalConfig.extraParameters.get("FrameStepRate"));
            //System.out.println(""+GlobalConfig.frameStepRate);
        } catch(Exception ex){
            System.err.println("OOOPS!");
        }
    }
}