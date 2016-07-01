package jsymbolic2.processing;

import java.util.List;

/**
 * A convenience class to allow features to be converted from one format to another.
 * In particular, currently this class can convert a list of feature names to an array
 * of boolean values. This array will be in the correct order, corresponding to what
 * jSymbolic needs to perform its processing.
 *
 * @author Tristano Tenaglia
 */
public class FeatureConversion {
    /**
     * Processes the given saveFeatures list and returns a boolean array appropriately
     * indexed based on the FeatureExtractorAccess indexing, where the index of features
     * that need to be saved are marked as true, otherwise index is false.
     * @param saveFeatures Features that need to be saved.
     * @return The appropriately indexed feature extractor boolean array.
     * @throws Exception This is thrown if a feature name in the input list is invalid.
     */
    public static boolean[] processFeaturesToSave(List<String> saveFeatures)
            throws Exception
    {
        List<String> allFeatures = FeatureExtractorAccess.getFeatureNameList();
        boolean[] tempSave = new boolean[allFeatures.size()];
        tempSave = initializeArrayFalse(tempSave);
        for(String feature : saveFeatures) {
            if(!allFeatures.contains(feature)) {
                throw new Exception(feature + " is not an existing feature in jSymbolic.");
            }
            int featureIndex = allFeatures.lastIndexOf(feature);
            tempSave[featureIndex] = true;
        }
        return tempSave;
    }

    /**
     * Convenience method to initialize a boolean array to false.
     * @param tempSave The array to be initialized to false.
     * @return The tempSave array with all values initialized to false.
     */
    public static boolean[] initializeArrayFalse(boolean[] tempSave) {
        for(int i = 0; i < tempSave.length; i++) {
            tempSave[i] = false;
        }
        return tempSave;
    }
}
