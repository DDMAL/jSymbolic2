package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configuration.*;
import jsymbolic2.processing.SymbolicMusicFileUtilities;

import java.io.File;
import java.util.List;

/**
 * An implementation of the {@link ConfigurationFileWriter} class. This deals with writing out the configuration file
 * of the .txt implementation of the configuration file writer.
 */
public class ConfigurationFileWriterTxtImpl extends ConfigurationFileWriter {

    /**
     * Adds the formatted options to the configuration file in the form optionName=optionValue.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param optionState The state of the options sections that needs to be written to the configuration file.
     * @return List of string for the raw line by line configuration file with the added option state.
     */
    @Override
    public List<String> addFormattedOptions(List<String> rawConfigFile, ConfigurationOptionState optionState) {
        rawConfigFile.add(ConfigFileHeaderEnum.OPTION_HEADER.toString());

        double windowSize = optionState.getWindow_size();
        String formattedWindowSize =
                OptionsEnum.window_size.name() + ConfigurationFileDelimiterEnum.EQUAL + Double.toString(windowSize);
        rawConfigFile.add(formattedWindowSize);

        double windowOverlap = optionState.getWindow_overlap();
        String formattedWindowOverlap =
                OptionsEnum.window_overlap.name() + ConfigurationFileDelimiterEnum.EQUAL + Double.toString(windowOverlap);
        rawConfigFile.add(formattedWindowOverlap);

        boolean saveWindow = optionState.isSave_features_for_each_window();
        String formattedSaveWindow =
                OptionsEnum.save_features_for_each_window.name() + ConfigurationFileDelimiterEnum.EQUAL + Boolean.toString(saveWindow);
        rawConfigFile.add(formattedSaveWindow);

        boolean saveOverall = optionState.isSave_overall_recording_features();
        String formattedSaveOverall =
                OptionsEnum.save_overall_recording_features.name() + ConfigurationFileDelimiterEnum.EQUAL + Boolean.toString(saveOverall);
        rawConfigFile.add(formattedSaveOverall);

        boolean convertArff = optionState.isConvert_to_arff();
        String formattedArff =
                OptionsEnum.convert_to_arff.name() + ConfigurationFileDelimiterEnum.EQUAL + Boolean.toString(convertArff);
        rawConfigFile.add(formattedArff);

        boolean convertCsv = optionState.isConvert_to_csv();
        String formattedCsv =
                OptionsEnum.convert_to_csv.name() + ConfigurationFileDelimiterEnum.EQUAL + Boolean.toString(convertCsv);
        rawConfigFile.add(formattedCsv);

        return rawConfigFile;
    }

    /**
     * Adds the formatted features to the configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param featuresToSave The unique names of all the features that need to be saved specified by each
     *                       features in {@link ace.datatypes.FeatureDefinition}.
     * @return List of string for the raw line by line configuration file with the added feature state.
     */
    @Override
    public List<String> addFormattedFeatures(List<String> rawConfigFile, List<String> featuresToSave) {
        rawConfigFile.add(ConfigFileHeaderEnum.FEATURE_HEADER.toString());
        rawConfigFile.addAll(featuresToSave);
        return rawConfigFile;
    }

    /**
     * Adds the formatted input files to the configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param inputFiles The input files that need to be added to the configuration file.
     * @return List of string for the raw line by line configuration file with the added input line state.
     */
    @Override
    public List<String> addFormattedInputFiles(List<String> rawConfigFile, ConfigurationInputFiles inputFiles) {
        rawConfigFile.add(ConfigFileHeaderEnum.INPUT_FILE_HEADER.toString());

        for(File file : inputFiles.getValidFiles()) {
            rawConfigFile.add(file.getPath());
        }

        return rawConfigFile;
    }

    /**
     * Adds the formatted output files to the configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param outputFiles The output files that need to be added to the configuration file.
     * @return List of string for the raw line by line configuration file with the added output line state.
     */
    @Override
    public List<String> addFormattedOutputFiles(List<String> rawConfigFile, ConfigurationOutputFiles outputFiles) {
        rawConfigFile.add(ConfigFileHeaderEnum.OUTPUT_FILE_HEADER.toString());

        String featureValue = SymbolicMusicFileUtilities.correctFileExtension(outputFiles.getFeature_values_save_path(), "xml");
        String formattedValue =
                OutputEnum.feature_values_save_path.name() + ConfigurationFileDelimiterEnum.EQUAL + featureValue;
        rawConfigFile.add(formattedValue);

        String definitionValue = SymbolicMusicFileUtilities.correctFileExtension(outputFiles.getFeature_definition_save_path(),"xml");
        String formattedDefinition =
                OutputEnum.feature_definitions_save_path.name() + ConfigurationFileDelimiterEnum.EQUAL + definitionValue;
        rawConfigFile.add(formattedDefinition);

        return rawConfigFile;
    }
}
