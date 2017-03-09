/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucdenver.mvsui.misc;

import edu.ucdenver.IApproach;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Siddhant Kulkarni
 */
public class UtilityClass {
    public static long getMemoryRequiredByLearningModule(IApproach approach) throws IOException{
        long size = sizeof(approach);

        String filename = System.getProperty("user.dir") + "/temp/" + approach.getName();
        size += FileUtils.sizeOfDirectory(new File(filename));

        return size;
    }

    public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }
}
