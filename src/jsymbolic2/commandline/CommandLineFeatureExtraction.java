package jsymbolic2.commandline;

import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.FileValidator;
import jsymbolic2.processing.MIDIFeatureProcessor;
import jsymbolic2.processing.MusicFileFilter;
import mckay.utilities.staticlibraries.FileMethods;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A general class that deals with the feature extraction process to be used from the command line.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public final class CommandLineFeatureExtraction {

    /**
     * Extracts all available features from a single MIDI file. Any errors
     * encountered are printed to standard error.
     *
     * @param input_MIDI_path The path of the MIDI file to extract features
     *                        from.
     * @param print_log       Whether or not to print a log of actions to standard
     *                        out.
     * @param processor       The Feature Processor to extract features with.
     * @param errorLog        Error log that holds error message for each incorrect
     *                        file.
     */
    private static void extractFeatures(String input_MIDI_path,
                                        boolean print_log,
                                        MIDIFeatureProcessor processor,
                                        List<String> errorLog) {
        try {
            // Note progress
            if (print_log) {
                System.out.println("jSymbolic is parsing " + input_MIDI_path + "...");
            }

            // Prepare and validate the input file
            File input_MIDI_file = new File(input_MIDI_path);
            FileMethods.validateFile(input_MIDI_file, true, false);

            // Note progress
            if (print_log) {
                System.out.println("jSymbolic is extracting features from " + input_MIDI_path + "...");
            }

            // Extract features from the MIDI file and save them in an XML file
            // Files validated here
            processor.extractFeatures(input_MIDI_file, errorLog);

            // Note progress
            if (print_log) {
                System.out.println("jSymbolic succesfully extracted features from " + input_MIDI_path + "...");
            }
        } catch (OutOfMemoryError e) {
            String errorMessage = "JSYMBOLIC ERROR WHILE PROCESSING " + input_MIDI_path + "\n" +
                    "- The Java Runtime ran out of memory.\n " +
                    "- Please rerun this program with more memory assigned to the runtime heap.\n";
            // End execution
            //TODO note that this will note terminate now and try to rerun jsymbolic with more memory from jSymbolicRunner
            CommandLineUtils.printMessageAndTerminate(errorMessage, -1);
        } catch (Exception e) {
            errorLog.add("Error found in file : " + input_MIDI_path + ". Error Message : " + e.getMessage() + ".");
        }
    }

    /**
     * Extract features for a single file.
     *
     * @param inputFile input file given by user
     * @param processor feature processor used for feature extraction
     * @param errorLog  error log of all errors that occur during file validation
     *                  and general feature extraction
     */
    private static void extractFeaturesFromFile(File inputFile,
                                                MIDIFeatureProcessor processor,
                                                List<String> errorLog) {
        String path = inputFile.getPath();
        //Files split up by name
        extractFeatures(path,
                true,
                processor,
                errorLog);

        try {
            // Finalize saved XML files
            processor.finalize();
        } catch (Exception ex) {
            errorLog.add(ex + "- " + ex.getMessage());
        }
    }

    /**
     * Extracts all files from the input directory and processes the features
     * from each of those files.
     *
     * @param folder    Directory in which all MIDI files are processed.
     * @param processor The Feature Processor to extract features with.
     * @param errorLog  Listof Strings of all errors to be printed.
     */
    private static void extractFeaturesFromFolder(File folder,
                                                  MIDIFeatureProcessor processor,
                                                  List<String> errorLog) {
        List<Path> meiList;
        String filename = folder.getAbsolutePath();
        try {
            meiList = Files.walk(Paths.get(filename))
                    .filter(name -> new MusicFileFilter().accept(name.toFile()))
                    .collect(Collectors.toList());
        }
        catch(IOException ioe) {
            //Exception will only be thrown at the start of Paths.get()
            errorLog.add("Error with starting file in : " + folder);
            meiList = new ArrayList<>();
        }

        for (Path path : meiList) {
            extractFeatures(path.toAbsolutePath().toString(),
                    true,
                    processor,
                    errorLog);
        }

        try {
            // Finalize saved XML files
            processor.finalize();
        } catch (Exception ex) {
            errorLog.add(ex + "- " + ex.getMessage());
        }
    }

    /**
     * Extracts all files from the input directory and processes the features
     * from each of those files. Eventually saves to an ACE XML file for analysis.
     *
     * @param fileList  List of all files that need to be feature extracted.
     * @param processor The Feature Processor to extract features with.
     * @param errorLog  List of strings of all errors to be printed.
     */
    public static void extractFeaturesFromAllFiles(List<File> fileList,
                                                   MIDIFeatureProcessor processor,
                                                   List<String> errorLog) {
        for (File file : fileList) {
            if (file.isDirectory()) {
                CommandLineFeatureExtraction.extractFeaturesFromFolder(file, processor, errorLog);
            } else {
                CommandLineFeatureExtraction.extractFeaturesFromFile(file, processor, errorLog);
            }
        }
        FileValidator.printErrorLog(errorLog);
    }

    /**
     * Extracts the files from all the input files given the specified configuration file data.
     *
     * @param configFileData            Configuration file data to be used.
     * @param inputFileList             List of all files that need to be feature extracted.
     * @param featureValueSavePath      The path of the feature value output file.
     * @param featureDefinitionSavePath The path of the feature definition output file.
     */
    public static void setupFeatureProcessorFromConfig(ConfigurationFileData configFileData,
                                                       List<File> inputFileList,
                                                       String featureValueSavePath,
                                                       String featureDefinitionSavePath) {
        // Get all available features
        MIDIFeatureExtractor[] feature_extractors = FeatureExtractorAccess.getAllFeatureExtractors();

        // Extract appropriate features specified by config file
        boolean[] features_to_save = configFileData.getFeaturesToSaveBoolean();

        MIDIFeatureProcessor processor = null;
        try {
            // Prepare to extract features
            processor = new MIDIFeatureProcessor(configFileData.getWindowSize(),
                    configFileData.getWindowOverlap(),
                    feature_extractors,
                    features_to_save,
                    configFileData.saveWindow(),
                    configFileData.saveOverall(),
                    featureValueSavePath,
                    featureDefinitionSavePath);
        } catch (Exception ex) {
            String errorMessage = "Fatal Error : " + ex + "\n- " + ex.getMessage() + "\n";
            CommandLineUtils.printMessageAndTerminate(errorMessage, -1);
        }

        List<String> errorLog = new ArrayList<>();
        CommandLineFeatureExtraction.extractFeaturesFromAllFiles(inputFileList, processor, errorLog);
    }

    /**
     * Set up the feature processor with the appropriate variables
     * and then extract features accordingly.
     *
     * @param input_file_path                 file path given by user
     * @param feature_values_save_path        feature value save path given by user
     * @param feature_descriptions_save_path  feature description save path given by user
     * @param save_features_for_each_window   given by user for window
     * @param save_overall_recording_features opposite of SAVE_FEATURES_FOR_EACH_WINDOW
     * @param window_size                     size of window given by user or else assumed to be 1.0
     * @param window_overlap                  overlap offset given by user or else assumed to be 0
     */
    public static void setupFeatureProcessorAndExtractFeatures(String input_file_path,
                                                               String feature_values_save_path,
                                                               String feature_descriptions_save_path,
                                                               boolean save_features_for_each_window,
                                                               boolean save_overall_recording_features,
                                                               double window_size,
                                                               double window_overlap) {
        // Get all available features
        MIDIFeatureExtractor[] feature_extractors = FeatureExtractorAccess.getAllFeatureExtractors();

        // Choose to extract all features
        boolean[] features_to_save = FeatureExtractorAccess.getDefaultFeaturesToSave();

        MIDIFeatureProcessor processor = null;
        try {
            // Prepare to extract features
            processor = new MIDIFeatureProcessor(window_size,
                    window_overlap,
                    feature_extractors,
                    features_to_save,
                    save_features_for_each_window,
                    save_overall_recording_features,
                    feature_values_save_path,
                    feature_descriptions_save_path);
        } catch (Exception ex) {
            String errorMessage = "\nFatal Error : " + ex + "\n- " + ex.getMessage() + "\n";
            CommandLineUtils.printMessageAndTerminate(errorMessage, -1);
        }

        //Extract features appropriately
        CommandLineFeatureExtraction.extractFeaturesFromAllFiles(
                Arrays.asList(new File(input_file_path)), processor, new ArrayList<>()
        );
    }
}
