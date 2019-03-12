package jsymbolic2.configurationfile.txtimplementation;

import jsymbolic2.configurationfile.ConfigFileInputFilePaths;
import jsymbolic2.configurationfile.EnumSectionDividers;
import jsymbolic2.configurationfile.WriterConfigFile;
import jsymbolic2.configurationfile.ConfigFileWindowingAndOutputFormatSettings;
import jsymbolic2.configurationfile.EnumWindowingAndOutputFormatSettings;
import jsymbolic2.configurationfile.EnumOutputFileTypes;
import jsymbolic2.configurationfile.ConfigFileOutputFilePaths;

import mckay.utilities.staticlibraries.StringMethods;

import java.io.File;
import java.util.List;

/**
 * An implementation of the {@link WriterConfigFile} class. This deals with writing out the configuration file
 * of the .txt implementation of the configuration file writer.
 */
public class WriterConfigFileTxtImpl extends WriterConfigFile {

    /**
     * Adds the formatted options to the configuration file in the form optionName=optionValue.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param optionState The state of the options sections that needs to be written to the configuration file.
     * @return List of string for the raw line by line configuration file with the added option state.
     */
    @Override
    public List<String> addFormattedOptions(List<String> rawConfigFile, ConfigFileWindowingAndOutputFormatSettings optionState) {
        rawConfigFile.add(EnumSectionDividers.OPTIONS_HEADER.toString());

        double windowSize = optionState.getWindowSize();
        String formattedWindowSize =
                EnumWindowingAndOutputFormatSettings.window_size.name() + EnumFieldValueDelimiter.EQUAL + Double.toString(windowSize);
        rawConfigFile.add(formattedWindowSize);

        double windowOverlap = optionState.getWindowOverlap();
        String formattedWindowOverlap =
                EnumWindowingAndOutputFormatSettings.window_overlap.name() + EnumFieldValueDelimiter.EQUAL + Double.toString(windowOverlap);
        rawConfigFile.add(formattedWindowOverlap);

        boolean saveWindow = optionState.getSaveFeaturesForEachWindow();
        String formattedSaveWindow =
                EnumWindowingAndOutputFormatSettings.save_features_for_each_window.name() + EnumFieldValueDelimiter.EQUAL + Boolean.toString(saveWindow);
        rawConfigFile.add(formattedSaveWindow);

        boolean saveOverall = optionState.getSaveOverallRecordingFeatures();
        String formattedSaveOverall =
                EnumWindowingAndOutputFormatSettings.save_overall_recording_features.name() + EnumFieldValueDelimiter.EQUAL + Boolean.toString(saveOverall);
        rawConfigFile.add(formattedSaveOverall);

        boolean convertArff = optionState.getConvertToArff();
        String formattedArff =
                EnumWindowingAndOutputFormatSettings.convert_to_arff.name() + EnumFieldValueDelimiter.EQUAL + Boolean.toString(convertArff);
        rawConfigFile.add(formattedArff);

        boolean convertCsv = optionState.getConvertToCsv();
        String formattedCsv =
                EnumWindowingAndOutputFormatSettings.convert_to_csv.name() + EnumFieldValueDelimiter.EQUAL + Boolean.toString(convertCsv);
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
        rawConfigFile.add(EnumSectionDividers.FEATURE_HEADER.toString());
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
    public List<String> addFormattedInputFiles(List<String> rawConfigFile, ConfigFileInputFilePaths inputFiles) {
        rawConfigFile.add(EnumSectionDividers.INPUT_FILES_HEADER.toString());

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
    public List<String> addFormattedOutputFiles(List<String> rawConfigFile, ConfigFileOutputFilePaths outputFiles) {
        rawConfigFile.add(EnumSectionDividers.OUTPUT_FILES_HEADER.toString());

        String featureValue = StringMethods.correctExtension(outputFiles.getFeatureValuesSavePath(), "xml");
        String formattedValue =
                EnumOutputFileTypes.feature_values_save_path.name() + EnumFieldValueDelimiter.EQUAL + featureValue;
        rawConfigFile.add(formattedValue);

        return rawConfigFile;
    }
}
