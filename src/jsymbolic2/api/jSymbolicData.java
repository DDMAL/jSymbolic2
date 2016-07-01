package jsymbolic2.api;

import ace.datatypes.DataBoard;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;

import java.io.File;

/**
 * A data storage object for keeping all of jSymbolics data in order to be returned together
 * through the jSymbolic API.
 *
 * @author Tristano Tenaglia
 */
public class jSymbolicData {
    private MeiSpecificStorage meiSpecificStorage;
    private File aceXMLFeatureValue;
    private File aceXMLFeatureDefinition;
    private File arffFile;
    private File csvArffFile;
    private DataBoard featureValuesDataBoard;

    /**
     * Stores the data that is extracted by jSymboic for API use.
     * @param meiSpecificStorage The mei specific data.
     * @param aceXMLFeatureValue The file for the ACE XML feature values.
     * @param aceXMLFeatureDefinition The file for the ACE XML feature definitions.
     * @param arffFile The file for the ARFF conversion. This can be null if no ARFF file is desired.
     * @param csvArffFile The file for the CSV conversion. This can be null if no CSV file is desired.
     * @throws Exception Thrown if the ACE XML could not be parsed into an ACE Databoard.
     */
    public jSymbolicData(MeiSpecificStorage meiSpecificStorage,
                         File aceXMLFeatureValue,
                         File aceXMLFeatureDefinition,
                         File arffFile,
                         File csvArffFile)
            throws Exception
    {
        this.meiSpecificStorage = meiSpecificStorage;
        this.aceXMLFeatureValue = aceXMLFeatureValue;
        this.aceXMLFeatureDefinition = aceXMLFeatureDefinition;
        this.arffFile = arffFile;
        this.csvArffFile = csvArffFile;

        // Parse the ACE XML file
        try {
            String[] input_files = new String[1];
            input_files[0] = aceXMLFeatureValue.getAbsolutePath();
            featureValuesDataBoard = new ace.datatypes.DataBoard(null, null, input_files, null);
        } catch (Exception e) {
            System.err.println("ERROR: Could not succesfully parse the file : " + aceXMLFeatureValue.getName() +
                    ". Perhaps this file does not exist, or is not a valid ACE XML Feature Values file?");
            throw e;
        }
    }

    /**
     *
     * @return The mei specific data storage.
     */
    public MeiSpecificStorage getMeiSpecificStorage() {
        return meiSpecificStorage;
    }

    /**
     *
     * @return The file with the ACE XML feature values.
     */
    public File getAceXMLFeatureValue() {
        return aceXMLFeatureValue;
    }

    /**
     *
     * @return The file with the ACE XML feature definitions.
     */
    public File getAceXMLFeatureDefinition() {
        return aceXMLFeatureDefinition;
    }

    /**
     *
     * @return The ACE Databoard parsed from the ACE XML feature values file.
     */
    public DataBoard getFeatureValuesDataBoard() {
        return featureValuesDataBoard;
    }

    /**
     *
     * @return The ARFF version of the ACE XML feature values file.
     */
    public File getArffFile() {
        return arffFile;
    }

    /**
     *
     * @return The CSV version of the ACE XML feature values file.
     */
    public File getCsvArffFile() {
        return csvArffFile;
    }

    /**
     * Setter for the CSV ARFF file.
     * @param csvArffFile The CSV ARFF file to be set to.
     */
    public void setCsvArffFile(File csvArffFile) {
        this.csvArffFile = csvArffFile;
    }

    /**
     * Setter for the ARFF file.
     * @param arffFile The ARFF file to be set to.
     */
    public void setArffFile(File arffFile) {
        this.arffFile = arffFile;
    }
}
