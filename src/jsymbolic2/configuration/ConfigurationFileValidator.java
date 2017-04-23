package jsymbolic2.configuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import jsymbolic2.processing.UserFeedbackGenerator;

/**
 * <p>
 * A general class for implementing a configuration file validator for the jSymbolic program.
 * Methods have been separated into validation for the configuration file itself,
 * features, options, input files and output files.
 * </p>
 * <p>
 * Each abstract method should be implemented to validate and parse the configuration file depending
 * on the chosen format of the configuration file. Each corresponding method should then return
 * the appropriate file object, with the data that jSymbolic requires from the configuration file.
 * </p>
 * <p>
 * In particular, the {@link #parseConfigFile(String, List, PrintStream)} method is not abstract and uses the template method pattern
 * to return an object the contains all the appropriate configuration file data. This method runs
 * the abstract functions in the correct order and also deals with exception handling.
 * Exception handling is dealt with by printing the particular error to console and then exiting
 * with a status code of -1.
 * </p>
 *
 * @author Tristano Tenaglia
 */
public abstract class ConfigurationFileValidator {

    /**
     * Validate the features syntax specified in the configuration file.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return A list of the entire configuration file with validated feature syntax.
     * @throws Exception If syntax is not valid for features in configuration file.
     */
    public abstract List<String> validateFeatureSyntax(List<String> rawConfigFile, File configurationFile) throws Exception;

    /**
     * Validate the features logic specified in the configuration file.
     * This occurs after the feature syntax validation in the {@link #parseConfigFile(String, List, PrintStream)} function.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return A list of string containing the line by line raw configuration file with feature logic validated.
     * @throws Exception If logic is not valid for features in configuration file.
     */
    public abstract List<String> validateFeatureLogic(List<String> rawConfigFile, File configurationFile) throws Exception;

    /**
     * Validate the options syntax specified in the configuration file.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return ConfigurationOptionState based on the actual state of the configuration options.
     * @throws Exception If syntax is not valid for options in the configuration file.
     */
    public abstract ConfigurationOptionState validateOptionSyntax(List<String> rawConfigFile, File configurationFile) throws Exception;

    /**
     * Validate the options logic specified in the configuration file.
     * This occurs after the option syntax validation in the parseConfigFile(String, List, PrintSteram) method.
     * @param configOption The syntax validated configuration option data.
     * @param configurationFile The configuration file for file information.
     * @return ConfigurationOptionState The fully validated state of the configuration options from the configuration file.
     * @throws Exception If logic is not valid for options in the configuration file.
     */
    public abstract ConfigurationOptionState validateOptionLogic(ConfigurationOptionState configOption, File configurationFile) throws Exception;

    /**
     * Validate the input files in the specified configuration file.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return ConfigurationInputFiles Both the valid and invalid input files that need to be processed.
     * @throws Exception Thrown when invalid input file syntax/logic is found in the configuration file.
     */
    public abstract ConfigurationInputFiles checkForInvalidInputFiles(List<String> rawConfigFile, File configurationFile) throws Exception;

    /**
     * Validate the output files in the specified configuration file.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @return ConfigurationOutputFiles Returns the feature value save path and the feature description save path.
     * @throws Exception Thrown when invalid output file syntax/logic is found in the configuration file.
     */
    public abstract ConfigurationOutputFiles checkForInvalidOutputFiles(List<String> rawConfigFile, File configurationFile) throws Exception;

    /**
     * Validate the header syntax in the specified configuration file.
     * @param rawConfigFile A list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file for file information.
     * @param headersToCheck The headers that need to be validated. Headers not contained in this list will
     *                       not be validated.
     * @throws Exception Thrown when configuration file is missing a header or has wrong header syntax on the headers
     * that it needs to check.
     */
    public abstract void validateHeaders(List<String> rawConfigFile, File configurationFile, List<ConfigFileHeaderEnum> headersToCheck) throws Exception;

    /**
     * Extract from the configuration file a list of strings containing the line by line raw configuration file.
     * @param configurationFile The configuration file that needs to be extracted from.
     * @return A list of strings containing the line by line raw configuration file.
     * @throws IOException Thrown if the configurationFile is not valid.
     */
    public abstract List<String> extractRawConfigurationFile(File configurationFile) throws IOException;

    /**
     * Validates the specified configuration file name. In particular, it validates if it is indeed a file and
     * if the name is right.
     * @param configurationFile The configuration file name that will be verified.
     * @return A valid file created from the configuration file name input.
     * @throws Exception Thrown if the name or the file itself is not valid.
     */
    public abstract File checkConfigFile(String configurationFile) throws Exception;

    /**
     * IMPORTANT: If a specific header does not need to be checked, then the value of the corresponding
     * data in the returned ConfigurationFileData object will be set to null. For example,
     * if input files and output save paths are specified in the command line instead of the configuration file,
     * then this function will return a ConfigurationFileData object with all the appropriate data
     * but the ConfigurationInputFiles Object and the ConfigurationOutputFiles object will be set to null.
     *
     * Convenience template method pattern to allow one function call to validate configuration file.
     * This design pattern specifies order of function call but not implementation, which is left
     * up to the programmer and depends on the format of the configuration file.
     * Notice that configFile null is not checked as this should be done
     * in the checkConfigFile function implementation.
     * @param configurationFileName The configuration file name that needs to be validated.
     * @param headersToCheck Headers that need to be validated in configuration file.
     *                       Any headers not specified here will result in an exception and thus
     *                       this method will print usage on error and then terminate the jSymbolic program.
	 * @param error_stream	A print stream to write errors to.	
     * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
     * from the specified configuration file. Only headers that need to be checked will return appropriate data.
     * All headers that need not be checked will be returned as null in the ConfigurationFileData return object.
     * @throws Exception Thrown if configuration file is not valid.
     */
    public ConfigurationFileData parseConfigFile(String configurationFileName,
			List<ConfigFileHeaderEnum> headersToCheck,
			PrintStream error_stream )
            throws Exception
    {
        //TODO could also have a function that takes in different config data and places it appropriately
        //Validate the actual config file
        File configFile = checkConfigFile(configurationFileName);

        //Read in the raw config file data
        List<String> rawConfigData = extractRawConfigurationFile(configFile);

        //Validate the headers in the config file
        validateHeaders(rawConfigData,configFile,headersToCheck);

        //Verify config file syntax and logic for options
        ConfigurationOptionState configOptions = null;
        if(headersToCheck.contains(ConfigFileHeaderEnum.OPTION_HEADER)) {
            configOptions = validateOptionLogic(validateOptionSyntax(rawConfigData, configFile), configFile);
        }

        //Verify config file syntax and logic for features
        List<String> featuresToSave = null;
        if(headersToCheck.contains(ConfigFileHeaderEnum.FEATURE_HEADER)) {
            featuresToSave = validateFeatureLogic(validateFeatureSyntax(rawConfigData, configFile), configFile);
        }

        //Verify config file for invalid input files
        ConfigurationInputFiles invalidInputFiles = null;
        if(headersToCheck.contains(ConfigFileHeaderEnum.INPUT_FILE_HEADER))
        {
            invalidInputFiles = checkForInvalidInputFiles(rawConfigData, configFile);
            //Print out all invalid files to console
            for (File invalid : invalidInputFiles.getInvalidFiles()) 
			{
				String error_message = "The " + configurationFileName + " configuration file refers to an invalid file path: " + invalid.getAbsolutePath() + ".";
                UserFeedbackGenerator.printWarningMessage(error_stream, error_message);
            }
        }

        //Verify specified output files
        ConfigurationOutputFiles outputFiles = null;
        if(headersToCheck.contains(ConfigFileHeaderEnum.OUTPUT_FILE_HEADER))
        {
            outputFiles = checkForInvalidOutputFiles(rawConfigData, configFile);
        }

        return new ConfigurationFileData(featuresToSave,
                configOptions,
                outputFiles,
                configurationFileName,
                invalidInputFiles);
    }

    /**
     * Parse and validate the configuration file, assuming that all headers need to validated.
     * @param configurationFileName The configuration file name that needs to be validated.
	 * @param error_stream	A print stream to write errors to.	
	 * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
     * from the specified configuration file. In this case, all jSymbolic data is specified in the
     * configuration file itself.
     * @throws Exception Thrown if configuration file is not valid.
     */
    public ConfigurationFileData parseConfigFileAllHeaders(String configurationFileName, PrintStream error_stream) throws Exception {
        return parseConfigFile(configurationFileName, Arrays.asList(ConfigFileHeaderEnum.values()), error_stream);
    }

    /**
     * Parse and validate the configuration file with both all headers and, if this doesn't work, feature and
	 * option headers.
     * @param configurationFileName The configuration file name that needs to be validated.
	 * @param error_stream	A print stream to write errors to.
     * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
     * from the specified configuration file. In this case, all jSymbolic data is specified in the
     * @throws Exception Thrown if configuration file is not valid.
     */
    public ConfigurationFileData parseConfigFileAllOrFeatOpt(String configurationFileName, PrintStream error_stream)
			throws Exception {
        ConfigurationFileData configurationFileData = null;

        try {
            //Try with all headers
            configurationFileData = parseConfigFileAllHeaders(configurationFileName, error_stream);
        } catch (Exception ex) {
            //continue
        }

        //If it works then return it
        if(configurationFileData != null) {
            return configurationFileData;
        }

        //Otherwise try with only feature and option header and return it if valid
        List<ConfigFileHeaderEnum> headersToCheck = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER);
        configurationFileData = parseConfigFile(configurationFileName, headersToCheck, error_stream);
        return configurationFileData;
    }

	/**
	 * Parse and validate the configuration file with both all headers or, if this doesn't work, all headers
	 * except input files, all headers except output files and just feature and option headers.
	 *
	 * @param config_file_name The configuration file name that needs to be validated.
	 * @param error_stream	A print stream to write errors to.
	 * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
	 * from the specified configuration file. In this case, all jSymbolic data is specified in the
	 * @throws Exception Thrown if configuration file is not valid.
	 */
	public ConfigurationFileData parseConfigFileTwoThreeOrFour(String config_file_name, PrintStream error_stream)
			throws Exception
	{
		ConfigurationFileData configuration_file_data = null;

		// Try with all four headers
		try
		{
			configuration_file_data = parseConfigFileAllHeaders(config_file_name, error_stream);
			return configuration_file_data;
		}
		catch (Exception e) {}

		// Try with FEATURE_HEADER, OPTION_HEADER and INPUT_FILE_HEADER
		try
		{
			List<ConfigFileHeaderEnum> headersToCheck = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER, ConfigFileHeaderEnum.INPUT_FILE_HEADER);
			configuration_file_data = parseConfigFile(config_file_name, headersToCheck, error_stream);
			return configuration_file_data;
		}
		catch (Exception e) {}		
		
		// Try with FEATURE_HEADER, OPTION_HEADER and OUTPUT_FILE_HEADER
		try
		{
			List<ConfigFileHeaderEnum> headersToCheck = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER, ConfigFileHeaderEnum.OUTPUT_FILE_HEADER);
			configuration_file_data = parseConfigFile(config_file_name, headersToCheck, error_stream);
			return configuration_file_data;
		}
		catch (Exception e) {}
		
		//Otherwise try with only FEATURE_HEADER and OPTION_HEADER, and return it if valid
		List<ConfigFileHeaderEnum> headersToCheck = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER);
		configuration_file_data = parseConfigFile(config_file_name, headersToCheck, error_stream);
		return configuration_file_data;
	}
}
