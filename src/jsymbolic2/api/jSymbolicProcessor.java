package jsymbolic2.api;

import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.featureutils.FeatureConversion;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.*;
import mckay.utilities.staticlibraries.FileMethods;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class acts as an API for jSymbolic. The constructor allows the programmer to input
 * the particular options they would like to extract from jSymbolic. The appropriate functions
 * can then be called to obtain the jSymbolic Data from a particular music file or directory.
 *
 * @author Tristano Tenaglia
 */
public class jSymbolicProcessor {
    private final double window_size;
    private final double window_overlap;
    private final boolean[] boolean_feature_names;
    private final boolean save_features_for_each_window;
    private final boolean save_overall_recording_features;
    private final boolean convert_to_arff;
    private final boolean convert_to_csv;
    private final String feature_values_save_path;
    private final String feature_definitions_save_path;

    /**
     * Constructor to build jSymbolicProcessor object from raw input data.
     * @param window_size Raw window size for MIDI window.
     * @param window_overlap Raw window overlap for MIDI window overlap offset.
     * @param feature_names Raw list of feature names which will we checked for validity.
     * @param save_features_for_each_window  Whether or not to save features for each window. If this is true
     *                                       then the overall recording must be false.
     * @param save_overall_recording_features Whether or not to save features for overall recording If this is true
     *                                        then the windowed recording must be false.
     * @param feature_values_save_path Save path for the feature values to be saved to ACE XML. Although a valid save
     *                                 path must be specified, this is not important to the API.
     * @param feature_definitions_save_path Save path for the feature definitions to be saved to ACE XML.
     *                                      Although a valid save path must be specified, this is not important
     *                                      to the API.
     * @param convert_to_arff Whether or not to convert feature values file to arff format.
     * @param convert_to_csv Whether or not to convert feature values file to csv arff format.
     * @throws Exception Thrown when a particular feature name in the feature_names list does not exist in jSymbolic.
     */
    public jSymbolicProcessor(double window_size,
                              double window_overlap,
                              List<String> feature_names,
                              boolean save_features_for_each_window,
                              boolean save_overall_recording_features,
                              boolean convert_to_arff,
                              boolean convert_to_csv,
                              String feature_values_save_path,
                              String feature_definitions_save_path)
            throws Exception
    {
        this.window_size = window_size;
        this.window_overlap = window_overlap;
        this.boolean_feature_names = FeatureConversion.processFeaturesToSave(feature_names);
        this.save_features_for_each_window = save_features_for_each_window;
        this.save_overall_recording_features = save_overall_recording_features;
        this.convert_to_arff = convert_to_arff;
        this.convert_to_csv = convert_to_csv;
        this.feature_values_save_path = feature_values_save_path;
        this.feature_definitions_save_path = feature_definitions_save_path;
    }

    /**
     * Constructor to build jSymbolicProcessor object from raw input data using the default feature names.
     * Therefore no feature names need to be explicitly given to the constructor.
     * @param window_size Raw window size for MIDI window.
     * @param window_overlap Raw window overlap for MIDI window overlap offset.
     * @param save_features_for_each_window  Whether or not to save features for each window. If this is true
     *                                       then the overall recording must be false.
     * @param save_overall_recording_features Whether or not to save features for overall recording If this is true
     *                                        then the windowed recording must be false.
     * @param feature_values_save_path Save path for the feature values to be saved to ACE XML. Although a valid save
     *                                 path must be specified, this is not important to the API.
     * @param feature_definitions_save_path Save path for the feature definitions to be saved to ACE XML.
     *                                      Although a valid save path must be specified, this is not important
     *                                      to the API.
     * @param convert_to_arff Whether or not to convert feature values file to arff format.
     * @param convert_to_csv Whether or not to convert feature values file to csv arff format.
     * @throws Exception Thrown when a particular feature name in the feature_names list does not exist in jSymbolic.
     */
    public jSymbolicProcessor(double window_size,
                              double window_overlap,
                              boolean save_features_for_each_window,
                              boolean save_overall_recording_features,
                              boolean convert_to_arff,
                              boolean convert_to_csv,
                              String feature_values_save_path,
                              String feature_definitions_save_path)
            throws Exception
    {
        this(window_size,
                window_overlap,
                FeatureExtractorAccess.getDefaultFeatureNamesToSave(),
                save_features_for_each_window,
                save_overall_recording_features,
                convert_to_arff,
                convert_to_csv,
                feature_values_save_path,
                feature_definitions_save_path);
    }

    /**
     * Constructor to build the jSymbolicProcessor using the specified configuration file settings.
     * An invalid configuration file will result in an error output to the console and termination of the program.
     * Notice that this will not process the input files as these should be obtained when making the API
     * function calls.
     * @param configurationFile The specified configuration file that the jSymbolic settings will be taken from.
     * @throws Exception Thrown when the configuration file is not valid.
     */
    public jSymbolicProcessor(String configurationFile) throws Exception {
        ConfigurationFileData data = new ConfigurationFileValidatorTxtImpl().parseConfigFile(configurationFile);
        this.window_size = data.getWindowSize();
        this.window_overlap = data.getWindowOverlap();
        this.boolean_feature_names = data.getFeaturesToSaveBoolean();
        this.save_features_for_each_window = data.saveWindow();
        this.save_overall_recording_features = data.saveOverall();
        this.convert_to_arff = data.convertToArff();
        this.convert_to_csv = data.convertToCsv();
        this.feature_values_save_path = data.getFeatureValueSavePath();
        this.feature_definitions_save_path = data.getFeatureDefinitionSavePath();
    }

    /**
     * This is necessary as each time new data is obtained
     * a new processor must be made to avoid IOExceptions.
     * @return A new Midi Feature Processor with the original data passed into the constructor.
     * @throws Exception Thrown when the MIDIFeatureProcessor is not well formed.
     */
    private MIDIFeatureProcessor buildNewProcessor() throws Exception {
        return new MIDIFeatureProcessor(window_size,
                window_overlap,
                FeatureExtractorAccess.getAllFeatureExtractors(),
                boolean_feature_names,
                save_features_for_each_window,
                save_overall_recording_features,
                feature_values_save_path,
                feature_definitions_save_path);
    }

    /**
     * Obtain a jSymbolic data object given a single input music file.
     * @param input_file The music file to extract data from.
     * @param errorLog The error log to see if any files are invalid. This will be printed out to console after
     *                 processing and any invalid files will be outputted.
     * @return A jSymbolicData object that contains all the necessary data from jSymbolic.
     * @throws Exception Thrown if the input music file is not valid.
     */
    public jSymbolicData computeJsymbolicData(File input_file, List<String> errorLog) throws Exception {
        if(input_file == null) {
            throw new Exception("The passed in input file to jSymbolicProcessor is null");
        }
        if(input_file.exists()) {
            MIDIFeatureProcessor processor = buildNewProcessor();
            jSymbolicData featureState = processor.extractAndReturnFeatures(input_file, errorLog);
            createFileConversions(featureState);
            //Print out all errors to console, they will also be returned inherently by the error log
            System.out.println(Arrays.toString(errorLog.toArray()));
            return featureState;
        } else {
            throw new Exception(input_file + " file does not exist");
        }
    }

    /**
     * Obtain a jSymbolic data object given a directory of music files. This will also check subdirectories all with
     * valid file extension. Currently, the valid file extensions are .mei and .midi.
     * @param directory The directory to obtain the files from.
     * @param errorLog The error log to see if any files are invalid. This will be printed out to console after
     *                 processing and any invalid files will be outputted.
     * @return A Map with a File as a key and jSymbolicData as a value. They specific File keys can be obtained
     * using the keySet() function of the Map object. This is recommended as then the appropriate jSymbolicData value
     * can be obtained to get the corresponding desired data.
     * @throws Exception Thrown if the directory is not valid.
     */
    public Map<File,jSymbolicData> computeJsymbolicDataDirectory(File directory, List<String> errorLog) throws Exception {
        if(directory == null) {
            throw new Exception("The passed in directory to jSymbolicProcessor is null.");
        }
        if(!directory.isDirectory()) {
            throw new Exception(directory.getName() + " is not an existing directory.");
        }

        Map<File, jSymbolicData> featureMap = new HashMap<>();
        File[] allFile = FileMethods.getAllFilesInDirectory(directory, true, new MusicFileFilter(), null);
        for(File file: allFile) {
            MIDIFeatureProcessor processor = buildNewProcessor();
            try {
                jSymbolicData featureState = processor.extractAndReturnFeatures(file,errorLog);
                featureMap.put(file, featureState);
                //Create file conversion only if no exception is thrown
                createFileConversions(featureState);
            }
            catch (Exception e) {
                errorLog.add("Error found in file : " + file.getName() + ". Error Message : " + e.getMessage() + ".");
            }
        }
        //Print out all errors to console, they will also be returned inherently by the error log
        System.out.println(Arrays.toString(errorLog.toArray()));
        return featureMap;
    }

    /**
     * Convert files if it is required by the corresponding constructor input parameters.
     * @param featureState The state of the feature data that may need to be changed.
     * @throws Exception Thrown if there are errors that occur with conversion.
     */
    private void createFileConversions(jSymbolicData featureState) throws Exception {
        AceConversionPaths conversionPaths =
                AceXmlConverter.saveAsArffOrCsvFiles(feature_values_save_path, feature_definitions_save_path, convert_to_arff, convert_to_csv);
        File arffFile = new File(conversionPaths.getArff_file_path());
        File csvFile = new File(conversionPaths.getCsv_arff_file_path());
        featureState.setArffFile(arffFile);
        featureState.setCsvArffFile(csvFile);
    }
}
