package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configuration.*;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.SymbolicMusicFileUtilities;
import jsymbolic2.processing.MusicFilter;
import mckay.utilities.staticlibraries.FileMethods;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link ConfigurationFileValidator} class. This deals with the validation and parsing
 * of the .txt implementation of the configuration file validator.
 *
 * @author Tristano Tenaglia
 */
public class ConfigurationFileValidatorTxtImpl extends ConfigurationFileValidator {

    /**
     * Validate the syntax of the features specified in the rawConfigFile. This includes verifying that
     * each feature is in fact a valid jSymbolic feature name corresponding to the appropriate
     * {@link ace.datatypes.FeatureDefinition} of that feature.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return A list of strings containing the line by line raw configuration file, with the feature syntax
     * validated.
     * @throws Exception Thrown if a particular feature is not valid in jSymbolic.
     */
    @Override
    public List<String> validateFeatureSyntax(List<String> rawConfigFile, File configurationFile) throws Exception {
        String featureHeader = ConfigFileHeaderEnum.FEATURE_HEADER.toString();
        int featureHeaderIndex = rawConfigFile.indexOf(featureHeader);
        int nextHeaderIndex = nextHeaderIndex(rawConfigFile,featureHeader);

        //Look for each feature one by one and verify
        List<String> extractorName = FeatureExtractorAccess.getNamesOfAllImplementedFeatures();
        List<String> featuresToSave = rawConfigFile.subList(featureHeaderIndex + 1, nextHeaderIndex);
        int lineNumber = featureHeaderIndex + 1;
        for(String featureLine : featuresToSave){
            //Check to make sure feature actually exists in jSymbolic
            if(!extractorName.contains(featureLine)) {
                throw new Exception("The " + configurationFile.getName() + " configuration file has an error at line " + lineNumber + ": " + featureLine + " is a non-existant feature in jSymbolic.");
            }
            lineNumber++;
        }
        return featuresToSave;
    }

    /**
     * Validates the logic of the features, specifically by removing any duplicate feature names that are
     * passed in the rawConfigFile.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return A list of strings containing the line by line raw configuration file, with the feature logic
     * validated.
     * @throws Exception None currently thrown.
     */
    @Override
    public List<String> validateFeatureLogic(List<String> rawConfigFile, File configurationFile) throws Exception {
        //Remove duplicate features
        return new ArrayList<>(new HashSet<>(rawConfigFile));
    }

    /**
     * Validate the syntax of the options in the rawConfigFile input. This deals with the verifications made in the
     * {@link #checkOptionLine(String, List, int, File)} function, which validates syntax and type for the options.
     * It also validates that all the options do in fact exist in the configuration file.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return The option state of the configuration file through a {@link ConfigurationOptionState} object.
     * @throws Exception Thrown if option syntax or format is invalid.
     */
    @Override
    public ConfigurationOptionState validateOptionSyntax(List<String> rawConfigFile, File configurationFile) throws Exception {
        String optionHeader = ConfigFileHeaderEnum.OPTION_HEADER.toString();
        int currentHeaderIndex = rawConfigFile.indexOf(optionHeader);
        int nextHeaderIndex = nextHeaderIndex(rawConfigFile,optionHeader);

        //Option value storage for later instantiation of option state object
        boolean saveWindow = false;
        boolean saveOverall = false;
        boolean convertToArff = false;
        boolean convertToCsv = false;
        double windowSize = 0;
        double windowOverlap = 0;

        if(nextHeaderIndex - currentHeaderIndex - 1 != OptionsEnum.values().length) {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() + " does not " +
                    "contain all possible options. It is required that all of " + Arrays.toString(ConfigFileHeaderEnum.values())
                    + " must be options under " + optionHeader + " in configuration file.");
        }

        //List and check all options mentioned in config
        List<String> optionLineList = rawConfigFile.subList(currentHeaderIndex + 1, nextHeaderIndex);
        List<String> optionsInConfig = new ArrayList<>();
        int lineNumber = nextHeaderIndex + 1;
        for(String optionLine : optionLineList)
        {
            //Check this particular option
            ConfigurationOption option = checkOptionLine(optionLine, rawConfigFile,lineNumber,configurationFile);
            optionsInConfig.add(option.getOptionName().toString());

            //Assign appropriate values for later processing
            //This can be assigned directly since the option has already been checked
            switch(option.getOptionName()) {
                case window_size:
                    windowSize = Double.parseDouble(option.getOptionValue());
                    break;
                case window_overlap:
                    windowOverlap = Double.parseDouble(option.getOptionValue());
                    break;
                case save_features_for_each_window:
                    saveWindow = Boolean.parseBoolean(option.getOptionValue());
                    break;
                case save_overall_recording_features:
                    saveOverall = Boolean.parseBoolean(option.getOptionValue());
                    break;
                case convert_to_arff:
                    convertToArff = Boolean.parseBoolean(option.getOptionValue());
                    break;
                case convert_to_csv:
                    convertToCsv = Boolean.parseBoolean(option.getOptionValue());
                    break;
            }
            lineNumber++;
        }

        //Return option state if all options are in fact mentioned in config file
        if (OptionsEnum.allOptionsExist(optionsInConfig)) {
            return new ConfigurationOptionState(windowSize,windowOverlap,saveWindow,saveOverall,convertToArff,convertToCsv);
        } else {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() + " does not " +
                    "contain all possible options. It is required that all of " + Arrays.toString(ConfigFileHeaderEnum.values())
                    + " must be options under " + optionHeader + " in configuration file.");
        }
    }

    /**
     * Validate the logic of the options in the passed in configOption state object. This currently ensures that the
     * save overall recording features and save windowed recording features are not the same boolean value.
     * @param configOption The syntax validated configuration option data.
     * @param configurationFile The configuration file for file information.
     * @return The configuration option state with the validated logic.
     * @throws Exception Thrown if overall and windowed recording feature option are the same boolean value.
     */
    @Override
    public ConfigurationOptionState validateOptionLogic(ConfigurationOptionState configOption, File configurationFile)
            throws Exception
    {
        //Check that save overall and save window are not the same
        if(configOption.isSave_overall_recording_features() && configOption.isSave_features_for_each_window()) {
            throw new Exception("In configuration file " + configurationFile.getName() +
                    OptionsEnum.save_overall_recording_features.name() + " and " +
                    OptionsEnum.save_features_for_each_window.name() + " are both true. " +
                    "This is not allowed in jSymbolic, either one or the other must be true, but not both.");
        }
        if(!configOption.isSave_overall_recording_features() && !configOption.isSave_features_for_each_window()) {
            throw new Exception("In configuration file " + configurationFile.getName() +
                    OptionsEnum.save_overall_recording_features.name() + " and " +
                    OptionsEnum.save_features_for_each_window.name() + " are both false. " +
                    "This is not allowed in jSymbolic, either one or the other must be true, but not both.");
        }
        return configOption;
    }

    /**
     * Performs checks for each option in the configuration file. Specifically, checks
     * 1) format is optionName=optionValue
     * 2) optionName is a valid option
     * 3) optionValue is appropriate type for corresponding optionName
     * @param optionLine The current option line that needs to be checked.
     * @param rawConfigData The configuration file that the option was taken from.
     * @param lineNumber The line that the option is on in the configuration file.
     * @param configurationFile The configuration file that was parsed.
     * @return ConfigurationOption object that contains both the appropriate value and key.
     * @throws Exception Thrown if any syntax errors are found in the options portion of the configuration file.
     */
    private ConfigurationOption checkOptionLine(String optionLine, List<String> rawConfigData, int lineNumber, File configurationFile)
            throws Exception
    {
        //Check that we have optionName=optionValue format
        String[] optionArray = optionLine.split("=");
        if(optionArray.length != 2) {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() + " at line " +
                    lineNumber + " - " + optionLine + " is incorrect option for config file format.");
        }

        //Check that optionName is in fact a valid known option
        String optionName = optionArray[0];
        String optionValue = optionArray[1];
        ConfigurationOption option =
                new ConfigurationOption(OptionsEnum.valueOf(optionName),optionValue);
        if(!OptionsEnum.contains(optionName)) {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() + " at line " +
                    lineNumber + " - " + optionLine + " is non-existent option for config file.");
        }

        //Check that the optionValue type corresponds to the appropriate optionName
        OptionsEnum optEnum = OptionsEnum.valueOf(optionName);
        if(!optEnum.checkValue(optionValue)) {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() + " at line " +
                    lineNumber + " - " + optionLine + " is incorrect value format for " + optEnum.toString());
        }

        return option;
    }

    /**
     * Implementation for the checking of invalid input files. Only single mei and midi files are allowed
     * as per the use of the Music File Filter. Any invalid files will be added to the invalid log which
     * this function will return. Directories are also considered invalid and will not be processed
     * and simply returned in the invalid return log.
     * @param rawConfigFile The configuration file that needs to be checked.
     * @param configurationFile The configuration file for file information.
     * @return A list of invalid files and directories specified in the configuration file.
     * @throws Exception Thrown if input file is not valid.
     */
    @Override
    public ConfigurationInputFiles checkForInvalidInputFiles(List<String> rawConfigFile, File configurationFile) throws Exception {
        ConfigurationInputFiles inputFileList = new ConfigurationInputFiles();
        String inputHeader = ConfigFileHeaderEnum.INPUT_FILE_HEADER.toString();
        int currentHeaderIndex = rawConfigFile.indexOf(inputHeader);
        int nextHeaderIndex = nextHeaderIndex(rawConfigFile,inputHeader);
        List<String> inputFiles = rawConfigFile.subList(currentHeaderIndex + 1, nextHeaderIndex);
        for(String inputLine : inputFiles) {
            //Check each file in configuration
            File inputFile = new File(inputLine);
            //Directories not currently allowed, so only validate file
            validateSingleFile(inputFile, inputFileList);
        }
        return inputFileList;
    }

    /**
     * Checks for invalid output files in the given configuration file. This needs to be of the precise format
     * &lt;output_files&gt; header with 2 fields below it. These 2 fields need to be in the format
     * 1) feature_values_save_path=test_value.xml
     * 2) feature_definitions_save_path=test_definition.xml
     * and the specified files need to exist and be accessible in the local system.
     * Anything else will result in a thrown exception.
     * @param rawConfigFile The raw line by line configuration file that needs to be validated.
     * @return A List of the valid files. Index 1 would contain feature value save path
     * and index 2 would contain feature definition save path.
     * @throws Exception thrown if format is wrong or if path does not exist in local system
     */
    @Override
    public ConfigurationOutputFiles checkForInvalidOutputFiles(List<String> rawConfigFile, File configurationFile) throws Exception {
        String outputHeader = ConfigFileHeaderEnum.OUTPUT_FILE_HEADER.toString();
        String valueSavePath = OutputEnum.feature_values_save_path.name();
        String definitionSavePath = OutputEnum.feature_definitions_save_path.name();

        int currentHeaderIndex = rawConfigFile.indexOf(outputHeader);
        int nextHeaderIndex = nextHeaderIndex(rawConfigFile,outputHeader);

        List<String> outputFileList = rawConfigFile.subList(currentHeaderIndex + 1, nextHeaderIndex);
        if(outputFileList.size() != OutputEnum.values().length) {
            throw new Exception("Missing output files. Need to include line of the form " +
                    "feature_value_save_path=example.xml and feature_definition_save_path under the header " +
                    outputHeader);
        }

        String featureValuesSavePath = "";
        String featureDefinitionsSavePath = "";
        for(String output : outputFileList) {
            if(output.contains(valueSavePath)) {
                featureValuesSavePath = output;
            }
            if(output.contains(definitionSavePath)) {
                featureDefinitionsSavePath = output;
            }
        }

        String[] valuesArray = featureValuesSavePath.split("=");
        String[] definitionArray = featureDefinitionsSavePath.split("=");
        if(valuesArray.length != 2) {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() +
                    " is invalid feature value format for config file format. Need to have " +
                    "feature_value_save_path=example.xml");
        }
        if(definitionArray.length != 2) {
            throw new Exception("Configuration file for jSymbolic " + configurationFile.getName() +
                    " is invalid feature definition format for config file format. Need to have " +
                    "feature_definition_save_path=example.xml");
        }

        //Ensured to not throw null pointer due to previous check for array length = 2
        String valuesName = valuesArray[0];
        String valuesValue = valuesArray[1];
        String definitionName = definitionArray[0];
        String definitionValue = definitionArray[1];
        if(!valuesName.equals(valueSavePath)) {
            throw new Exception("Name of features values must be: " + valueSavePath + ". It is now " +
                    valuesName);
        }
        if(!definitionName.equals(definitionSavePath)) {
            throw new Exception("Name of feature definitions must be: " + definitionSavePath + ". It is now " +
                    definitionName);
        }

        //Check that path exists for feature value and definition save paths
        if(!SymbolicMusicFileUtilities.filePathExists(valuesValue)) {
            throw new Exception("Feature value save path " + definitionValue + " is not a valid path in configuration file "
                    + configurationFile.getName());
        }
        if(!SymbolicMusicFileUtilities.filePathExists(definitionValue)) {
            throw new Exception("Feature definition save path " + valuesValue + " is not a valid path in configuration file "
                    + configurationFile.getName());
        }

        //Check and correct that the extension is in fact xml
        valuesValue = SymbolicMusicFileUtilities.correctFileExtension(valuesValue, "xml");
        definitionValue = SymbolicMusicFileUtilities.correctFileExtension(definitionValue, "xml");
        ConfigurationOutputFiles outputFile = new ConfigurationOutputFiles(valuesValue,definitionValue);
        return outputFile;
    }

    /**
     * Validates if all headers to check in HeaderEnum are in the specified configuration file.
     * @param rawConfigFile Raw string lines of the configuration file.
     * @param configurationFile The original configuration file for extra data (e.g. file name).
     * @param headersToCheck Headers that need to be checked in the configuration file.
     * @throws Exception Thrown if any header in the HeaderEnum is missing and if headers that
     * do not need to be checked are found in the configuration file.
     */
    @Override
    public void validateHeaders(List<String> rawConfigFile, File configurationFile, List<ConfigFileHeaderEnum> headersToCheck)
            throws Exception
    {
        for(ConfigFileHeaderEnum header : headersToCheck) {
            if(Collections.frequency(rawConfigFile,header.toString()) != 1) {
                throw new Exception(configurationFile.getName() + " is not a valid configuration file " +
                        "as it does not contain 1 of the following header : " + header.toString());
            }
        }

        List<ConfigFileHeaderEnum> headersNotToCheck = headersNotToCheck(headersToCheck);
        for(ConfigFileHeaderEnum header : headersNotToCheck) {
            if(rawConfigFile.contains(header.toString())) {
                throw new Exception("jSymbolic configuration file " + configurationFile.getName() + " contains " +
                        header.toString() + " which has already been specified in the command line.\n Either " +
                        "include this header/data in the configuration file or in the command line, but not both.");
            }
        }
    }

    /**
     * Helper function to get the headers that do not need to be checked.
     * @param headersToCheck List of headers that need to be checked.
     * @return List of headers that do not need to be checked.
     */
    private List<ConfigFileHeaderEnum> headersNotToCheck(List<ConfigFileHeaderEnum> headersToCheck) {
        List<ConfigFileHeaderEnum> allHeaders = Arrays.asList(ConfigFileHeaderEnum.values());
        List<ConfigFileHeaderEnum> headersNotToCheck = new ArrayList<>();
        for(ConfigFileHeaderEnum header : allHeaders) {
            if(!headersToCheck.contains(header)) {
                headersNotToCheck.add(header);
            }
        }
        return headersNotToCheck;
    }

    /**
     * Helper function to return next header index after in raw data after the given header.
     * @param rawData Raw data of config file to be checked.
     * @param header Header to start from.
     * @return Next header index if it is found, otherwise returns rawData.size().
     */
    private int nextHeaderIndex(List<String> rawData, String header) {
        int currentHeaderIndex = rawData.indexOf(header);
        for(int nextIndex = currentHeaderIndex + 1; nextIndex < rawData.size(); nextIndex++) {
            String nextLine = rawData.get(nextIndex);
            if(ConfigFileHeaderEnum.contains(nextLine)) {
                return nextIndex;
            }
        }
        return rawData.size();
    }

    /**
     * Read in the file line by line.
     * @param configurationFile File to be read in.
     * @return The raw data from the file line by line.
     * @throws IOException Thrown if there were problems parsing or retrieving the configuration file.
     */
    @Override
    public List<String> extractRawConfigurationFile(File configurationFile) throws IOException {
        return Files.lines(Paths.get(configurationFile.getAbsolutePath()), Charset.forName("ISO-8859-1"))
                    .collect(Collectors.toList());
    }

    private void validateSingleFile(File file, ConfigurationInputFiles inputFiles) {
        try {
            FileMethods.validateFile(file, true, false);
            inputFiles.addValidFile(file);
        } catch (Exception e) {
            inputFiles.addInvalidFile(file);
        }
    }

    /**
     * Currently unused as input directories are considered invalid in the configuration file.
     * @param directory The directory that needs to be validated.
     * @param inputFiles The input files extracted from the configuration file.
     */
    private void validateDirectory(File directory, ConfigurationInputFiles inputFiles) {
        File[] allFiles = FileMethods.getAllFilesInDirectory(directory, true, new MusicFilter(), null);
        for(File file : allFiles) {
            validateSingleFile(file,inputFiles);
        }
    }

    /**
     * Validates the configuration file to see if the give file exists and if it is a .txt.
     * @param configurationFile File name to be validated.
     * @return The associated file if it is valid.
     * @throws Exception Thrown if file dne or if it is not in .txt format.
     */
    @Override
    public File checkConfigFile(String configurationFile) throws Exception {
        File configFile = new File(configurationFile);
        String configFileName = ".*\\." + ConfigurationFileExtensionEnum.txt.name();
        if(configFile == null || !configFile.exists()) {
            throw new Exception("The configuration file specified at " + configurationFile + " does not exist.");
        }
        if(!configurationFile.matches(configFileName)) {
            throw new Exception("The file " + configurationFile + " does not have a txt extension.");
        }
        return configFile;
    }
}
