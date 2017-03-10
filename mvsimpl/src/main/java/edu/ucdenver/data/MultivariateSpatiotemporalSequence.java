package edu.ucdenver.data;

import java.io.*;

import org.jblas.DoubleMatrix;

/**
 * Created by max on 3/3/17.
 */
public class MultivariateSpatiotemporalSequence {
    private DoubleMatrix[]  data;
    private String          label;

    private MultivariateSpatiotemporalSequence () {}

    /**
     * Create a brand new MultivariateSpatiotemporalSequence from a '.mvs' file specified
     * by the filename.
     *
     * @param filename The '.mvs' file path
     * @return The new MulitvariateSpatiotemporalSequence object
     * @throws IOException Thrown if any of the files cannot be accessed or read
     */
    public static MultivariateSpatiotemporalSequence load (String filename) throws IOException {
        return MultivariateSpatiotemporalSequence.load(new File(filename));
    }

    /**
     * Create a brand new MultivariateSpatiotemporalSequence from a '.mvs' file
     *
     * @param file The '.mvs' file
     * @return The new MultivariateSpatiotemporalSequence object
     * @throws IOException Thrown if any of the files cannot be accessed or read
     */
    public static MultivariateSpatiotemporalSequence load (File file) throws IOException {
        MultivariateSpatiotemporalSequence result = new MultivariateSpatiotemporalSequence();

        DataInputStream in = new DataInputStream(new FileInputStream(file));
        String folder = file.getParentFile().getAbsolutePath();

        int depth = in.readInt();
        String[] layerFilenames = new String[depth];
        for (int i = 0; i < depth; ++i) {
            int filenameLength = in.readInt();
            String filename = folder + "/";
            for (int j = 0; j < filenameLength; ++j) {
                filename += in.readChar();
            }

            layerFilenames[i] = filename;
        }

        // Read in the label
        int labelSize = in.readInt();
        result.label = "";
        for (int i = 0; i < labelSize; ++i) {
            result.label += in.readChar();
        }

        // Close the metadata file
        in.close();

        // Load the data from the layer files
        result.data = new DoubleMatrix[depth];
        for (int i = 0; i < depth; ++i) {
            result.data[i] = new DoubleMatrix(layerFilenames[i]);
        }

        return result;
    }

    /**
     * Get the depth of the data
     *
     * @return The depth
     */
    public int getDepth () {
        return this.data.length;
    }

    /**
     * Get the same frame for each layer in the depth
     *
     * @param frameNumber The frame number to access
     * @return DoubleMatrix[] Column matrix which is the frame values
     */
    public DoubleMatrix[] getFrame (int frameNumber) {
        DoubleMatrix[] result = new DoubleMatrix[this.data.length];

        for (int i = 0; i < this.data.length; ++i) {
            result[i] = this.data[i].getColumn(frameNumber);
        }

        return result;
    }

    /**
     * Returns the label of the data class
     *
     * @return String label
     */
    public String getLabel () {
        return this.label;
    }

    /**
     * Get a specific layer
     *
     * @param layer The layer to access
     * @return The layer or null if that layer doesn't exist
     */
    public DoubleMatrix getLayer (int layer) {
        if (layer > this.getDepth()) {
            return null;
        }

        return this.data[layer];
    }

    public int getNumberOfFrames () {
        return this.data[0].columns;
    }

    /**
     * Get the number of sequences in the mvs
     *
     * @return The number of sequences
     */
    public int getNumberOfSequences () {
        return this.data[0].rows;
    }

    /**
     * Write the MVS to a set of files that can the be reloaded. The
     * format is as follows:
     *
     * There will be one .mvs file
     *
     * int - depth of data (ex. 3 for 3D data) There should be an equal number of files
     *
     * ** Repeat for each layer
     * int - length of filename of layer file
     * char+ - layer file name
     * **
     *
     * int - number of chars in label
     * char+ - The label (the length should match the previous int)
     *
     * There will be several layer files per .mvs file based on the number of layers
     * in the MVS.
     *
     * @param baseFilename
     * @throws IOException
     */
    public void save (String folder, String baseFilename) throws IOException {
        // Generate the filenames
        String[] filenames = new String[this.getDepth()];
        for (int i = 0; i < filenames.length; ++i) {
            filenames[i] = baseFilename + "." + i;
        }

        // Save the layer files
        for (int i = 0; i < this.getDepth(); ++i) {
            this.data[i].save(folder + "/" + filenames[i]);
        }

        // Write the metadata file
        File metadataFile = new File(folder + "/" + baseFilename + ".mvs");
        DataOutputStream metadataStream = new DataOutputStream(new FileOutputStream(metadataFile));

        // Write the mvs depth
        metadataStream.writeInt(this.getDepth());

        // Write the filenames
        for (String filename : filenames) {
            metadataStream.writeInt(filename.length());
            metadataStream.writeChars(filename);
        }

        // Write the label
        metadataStream.writeInt(label.length());
        metadataStream.writeChars(label);

        metadataStream.close();
    }
}
