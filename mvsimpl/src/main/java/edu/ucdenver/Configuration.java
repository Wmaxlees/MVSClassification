package edu.ucdenver;

import java.io.*;
import java.util.Properties;

/**
 * Created by max on 3/1/17.
 */
public class Configuration {
    private Properties values;

    public Configuration (String configurationFilename) {
        this.values = new Properties();

        try (InputStream inputStream = new FileInputStream(configurationFilename)) {
            if (inputStream != null) {
                this.values.load(inputStream);
            } else {
                System.err.println("Failed to load config file: " + configurationFilename);
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.println("Failed to load config file: " + configurationFilename);
            System.exit(-1);
        }
    }

    public boolean getBoolean (String key) {
        return Boolean.parseBoolean(values.getProperty(key));
    }

    public void setBoolean (String key, boolean value) {
        if (!values.containsKey(key)) {
            values.put(key, value);
        } else {
            values.replace(key, value);
        }
    }

    public int getInt (String key) {
        return Integer.parseInt(values.getProperty(key));
    }

    public void setInt (String key, int value) {
        if (!values.containsKey(key)) {
            values.put(key, value);
        } else {
            values.replace(key, value);
        }
    }

    public String getString (String key) {
        return values.getProperty(key);
    }

    public void setString (String key, String value) {
        if (!values.containsKey(key)) {
            values.put(key, value);
        } else {
            values.replace(key, value);
        }
    }

}
