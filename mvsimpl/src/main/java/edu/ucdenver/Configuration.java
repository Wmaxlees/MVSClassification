package edu.ucdenver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by max on 3/1/17.
 */
public class Configuration {
    HashMap<String, Object> values;

    public Configuration (String configurationFilename) {
        this.values = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(configurationFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLines = line.split(",");
                String key = splitLines[0];
                String type = splitLines[1];
                String value = splitLines[2];

                switch (type) {
                    case "boolean":
                        System.out.println(key + " : " + Boolean.valueOf(value));
                        values.put(key, Boolean.valueOf(value));
                        break;
                    case "int":
                        System.out.println(key + " : " + Integer.valueOf(value));
                        values.put(key, Integer.valueOf(value));
                        break;
                    case "string":
                        System.out.println(key + " : " + value);
                        values.put(key, value);
                        break;
                    default:
                        throw new ClassNotFoundException();
                }
            }
        } catch (IOException | IndexOutOfBoundsException |
                ClassNotFoundException | NumberFormatException e) {
            System.out.println("Badly formatted configuration file: " + configurationFilename);
            System.exit(-1);
        }
    }

    public boolean getBoolean (String key) {
        return (Boolean)values.get(key);
    }

    public void setBoolean (String key, boolean value) {
        if (!values.containsKey(key)) {
            values.put(key, value);
        } else {
            values.replace(key, value);
        }
    }

    public int getInt (String key) {
        return (Integer)values.get(key);
    }

    public void setInt (String key, int value) {
        if (!values.containsKey(key)) {
            values.put(key, value);
        } else {
            values.replace(key, value);
        }
    }

    public String getString (String key) {
        return (String)values.get(key);
    }

    public void setString (String key, String value) {
        if (!values.containsKey(key)) {
            values.put(key, value);
        } else {
            values.replace(key, value);
        }
    }

}
