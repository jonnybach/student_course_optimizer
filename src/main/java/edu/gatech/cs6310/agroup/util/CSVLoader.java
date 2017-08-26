package edu.gatech.cs6310.agroup.util;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility base class for model objects that are read from CSV files
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public abstract class CSVLoader {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(CSVLoader.class);

    public void loadFile(String objectsFile) {
        File file = new File(objectsFile);

        if (!file.isFile()) {
            logger.error("Objects file does not exist => " + objectsFile);
            return;
        }

        try {
            logger.info("Loading data from file => " + objectsFile);
            BufferedReader br = Files.newBufferedReader(Paths.get(objectsFile));
            loadCSV(br);

        } catch (IOException e) {
            logger.error("Failed to read objects file => " + objectsFile + "; " + e.toString());
        }
    }

    public void loadResource(String objectsResource) {
        InputStream is = this.getClass().getResourceAsStream(objectsResource);

        if (is == null) {
            logger.error("Objects resource does not exist => " + objectsResource);
            return;
        }

        try {
            logger.info("Loading data from resource => " + objectsResource);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            loadCSV(br);

        } catch (IOException e) {
            logger.error("Failed to read objects resource => " + objectsResource + "; " + e.toString());
        }
    }

    public void loadCSV(BufferedReader br) throws IOException {
        if (br == null) return;

        String line;
        br.readLine(); // skip header; maybe do some analysis to see if header is missing
        int count = 0;

        while ((line = br.readLine()) != null) {
            //System.out.println("CSV line => " + line);
            //String[] parts = line.split(",\\s*");
            String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            if (parts.length > 0) {
                addObject(parts);
                ++count;
            }
        }

        logger.info(count + " objects were loaded");
    }

    public abstract void addObject(String[] parts);
}
