package ${groupId};

import edu.ucdenver.Accuracy;
import edu.ucdenver.IApproachInterface;
import edu.ucdenver.Individual;

import java.util.HashMap;

public class Approach extends IApproachInterface {
    public Approach () {
        this.loadConfigurationFile(this.getName());
    }

    public String[][] getActualAndPredicted () {
        return new String[1][1];
    }

    public String getFeaturesToWrite () {
        return new String();
    }

    public HashMap trainApproach (Individual[] trainingDataSet) {
        return new HashMap();
    }

    public Accuracy testDataSetUsingApproach (HashMap hm, Individual[] testingDataSet) {
        return new Accuracy(new String[1][1]);
    }

    public String getName() {
        return new String();
    }


}
