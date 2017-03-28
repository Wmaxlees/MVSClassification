package ${groupId};

import edu.ucdenver.data.ResultSet;
import edu.ucdenver.data.DataClass;
import edu.ucdenver.IApproach;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Approach extends IApproach {
    public Approach () {
        this.loadConfigurationFile(this.getName());
    }

    @Override
    public void train (DataClass[] trainingDataSet) {}

    @Override
    public ResultSet test (DataClass[] testingDataSet) {
        return new ResultSet();
    }

    @Override
    public String getName() {
        return "ApproachName";
    }

    @Override
    public void writeDetails (String filename) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        out.close();
    }
}
