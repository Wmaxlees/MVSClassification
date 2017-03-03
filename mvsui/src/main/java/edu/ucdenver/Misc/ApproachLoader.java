package edu.ucdenver.Misc;

import edu.ucdenver.IApproachInterface;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by max on 3/2/17.
 */
public class ApproachLoader {
    public int getNumberOfApproaches () {
        return this.m_approaches.size();
    }

    public ArrayList<IApproachInterface> getApproaches() {
        return this.m_approaches;
    }

    private ArrayList<IApproachInterface> m_approaches;

    public ApproachLoader () {
        this.m_approaches = new ArrayList<>();

        // Get access to the class loader
        try {
            // Load the algorithm classes
            File algorithmsFolder = new File(System.getProperty("user.dir") + "/bin");
            File[] filesInFolder = algorithmsFolder.listFiles();
            ArrayList<URL> jars = new ArrayList<>();
            for (File jarFile : filesInFolder) {
                if (jarFile.getAbsolutePath().contains("README.md")) {
                    continue;
                }

                URL jarURL = new URL("jar", "", jarFile.toURI().toURL() + "!/");
                jars.add(jarURL);
            }

            URL[] urlArray = new URL[jars.size()];
            jars.toArray(urlArray);

            URLClassLoader urlClassLoader = URLClassLoader.newInstance(urlArray, Thread.currentThread().getContextClassLoader());
            ServiceLoader<IApproachInterface> sl = ServiceLoader.load(IApproachInterface.class, urlClassLoader);

            Iterator<IApproachInterface> it = sl.iterator();
            while (it.hasNext()) {
                IApproachInterface approach = it.next();
                System.out.println("Loading Approach: " + approach.getName() + "...");
                this.m_approaches.add(approach);
            }

        } catch (MalformedURLException e) {
            System.err.println("Failed to load algorithms");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
