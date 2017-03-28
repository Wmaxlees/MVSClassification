package edu.ucdenver.patternmining;

import java.util.Arrays;

/**
 * Holds the indices of a pattern
 *
 * @author W. Max Lees [max.lees@gmail.com]
 * Created on 3/10/17.
 */
public class Metadata {
    private int[] value;

    private static final int SIZE_OF_PATTERN    = 0;
    private static final int INSTANCE_INDEX     = 1;
    private static final int SEQUENCE_INDEX     = 2;
    private static final int FRAME_INDEX        = 3;

    public Metadata (int size, int instance, int sequence, int frame) {
        this.value = new int[]{size, instance, sequence, frame};
    }

    @Override
    public boolean equals (Object other) {
        return this.getClass().equals(other.getClass()) &&
                Arrays.equals(this.value, ((Metadata) other).value);
    }

    public int getSizeOfPattern () {
        return this.value[Metadata.SIZE_OF_PATTERN];
    }

    public int getInstance () {
        return this.value[Metadata.INSTANCE_INDEX];
    }

    public int getSequence () {
        return this.value[Metadata.SEQUENCE_INDEX];
    }

    public int getFrame () {
        return this.value[Metadata.FRAME_INDEX];
    }

    public int compareTo (Metadata other) {
        for (int i = 0; i < 4; ++i) {
            if (this.value[i] > other.value[i]) {
                return 1;
            }

            if (this.value[i] < other.value[i]) {
                return -1;
            }
        }

        return 0;
    }

    @Override
    public String toString () {
        return Arrays.toString(this.value);
    }
}
