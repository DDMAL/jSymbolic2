package jsymbolic2.configuration;

import jsymbolic2.featureutils.FeatureExtractorAccess;

import java.util.Arrays;
import java.util.List;

/**
 * A state object for all the data specified by a configuration file. This is to be used in order to conveniently
 * move around and access the configuration file data of a particular jSymbolic instance,
 * from one central place.
 */
public class ConfigurationFileData {

    /**
     * Name of the features to be saved.
     * These names are based off the unique {@link ace.datatypes.FeatureDefinition} names.
     */
    private List<String> featuresToSave;

    /**
     * A boolean array associated to the features that need to be saved in the particular order
     * specified in {@link FeatureExtractorAccess}.
     */
    private boolean[] featuresToSaveBoolean;

    /**
     * The current state of the option from the configuration file.
     */
    private ConfigurationOptionState optionState;

    /**
     * The output file paths from the configuration file.
     */
    private ConfigurationOutputFiles outputFileList;

    /**
     * A String representation of the path of the configuration file.
     */
    private String configurationFilePath;

    /**
     * All valid and invalid input files from the configuration file.
     */
    private ConfigurationInputFiles inputFileList;

    /**
     * Constructor
     * @param featuresToSave Name of features to save for this configuration file.
     * @param optionState State of the options for this configuration file.
     * @param outputFileList All the output file paths for this configuration file.
     * @param configurationFilePath File path for the configuration file.
     * @param inputFileList Valid and invalid files for this configuration file.
     * @throws Exception Thrown if a particular feature is non-existing in jSymbolic.
     */
    public ConfigurationFileData(List<String> featuresToSave,
                                 ConfigurationOptionState optionState,
                                 ConfigurationOutputFiles outputFileList,
                                 String configurationFilePath,
                                 ConfigurationInputFiles inputFileList)
            throws Exception
    {
        this.featuresToSave = featuresToSave;
        this.optionState = optionState;
        this.outputFileList = outputFileList;
        this.configurationFilePath = configurationFilePath;
        this.inputFileList = inputFileList;
        this.featuresToSaveBoolean = FeatureExtractorAccess.findSpecifiedFeatures(featuresToSave);
    }

    /**
     * @return List of Name of the features to be saved.
     * These names are based off the unique {@link ace.datatypes.FeatureDefinition} names.
     */
    public List<String> getFeaturesToSave() {
        return featuresToSave;
    }

    /**
     * @return A boolean array associated to the features that need to be saved in the particular order
     * specified in {@link FeatureExtractorAccess}.
     */
    public boolean[] getFeaturesToSaveBoolean() {
        return featuresToSaveBoolean;
    }

    /**
     *
     * @return The window size from the option state of the configuration file.
     */
    public double getWindowSize() {
        return optionState.getWindow_size();
    }

    /**
     *
     * @return The window overlap of the option state of the configuration file.
     */
    public double getWindowOverlap() {
        return optionState.getWindow_overlap();
    }

    /**
     *
     * @return True if configuration file specifies that piece must be split up into windows,
     * otherwise false.
     */
    public boolean saveWindow() {
        return optionState.isSave_features_for_each_window();
    }

    /**
     *
     * @return True if configuration specifies that features must only be saved for overall piece,
     * otherwise false.
     */
    public boolean saveOverall() {
        return optionState.isSave_overall_recording_features();
    }

    /**
     *
     * @return True if the ARFF format must be converted to as specified by configuration file, otherwise false.
     */
    public boolean convertToArff() {
        return optionState.isConvert_to_arff();
    }

    /**
     *
     * @return True if the CSV format must be converted to as specified by the configuration file, otherwise false.
     */
    public boolean convertToCsv() {
        return optionState.isConvert_to_csv();
    }

    /**
     *
     * @return The path that the feature value ACE XML file will be saved to.
     */
    public String getFeatureValueSavePath() {
        return outputFileList.getFeature_values_save_path();
    }

    /**
     *
     * @return The path that the feature value ACE XML file will be saved to.
     */
    public String getFeatureDefinitionSavePath() {
        return outputFileList.getFeature_definition_save_path();
    }

    /**
     *
     * @return The path that the configuration file is saved at.
     */
    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    /**
     *
     * @return The list of input files specified by the configuration file.
     */
    public ConfigurationInputFiles getInputFileList() {
        return inputFileList;
    }

    /**
     *
     * @return The state of the options specified by the configuration file.
     */
    public ConfigurationOptionState getOptionState() {
        return optionState;
    }

    /**
     *
     * @return The list of output files specified by the configuration file.
     */
    public ConfigurationOutputFiles getOutputFileList() {
        return outputFileList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationFileData that = (ConfigurationFileData) o;

        if (featuresToSave != null ? !featuresToSave.equals(that.featuresToSave) : that.featuresToSave != null)
            return false;
        if (!Arrays.equals(featuresToSaveBoolean, that.featuresToSaveBoolean)) return false;
        if (optionState != null ? !optionState.equals(that.optionState) : that.optionState != null) return false;
        if (outputFileList != null ? !outputFileList.equals(that.outputFileList) : that.outputFileList != null)
            return false;
        if (configurationFilePath != null ? !configurationFilePath.equals(that.configurationFilePath) : that.configurationFilePath != null)
            return false;
        return inputFileList != null ? inputFileList.equals(that.inputFileList) : that.inputFileList == null;

    }

    @Override
    public int hashCode() {
        int result = featuresToSave != null ? featuresToSave.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(featuresToSaveBoolean);
        result = 31 * result + (optionState != null ? optionState.hashCode() : 0);
        result = 31 * result + (outputFileList != null ? outputFileList.hashCode() : 0);
        result = 31 * result + (configurationFilePath != null ? configurationFilePath.hashCode() : 0);
        result = 31 * result + (inputFileList != null ? inputFileList.hashCode() : 0);
        return result;
    }
}
