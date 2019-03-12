package jsymbolic2.configurationfile;

import jsymbolic2.processing.UserFeedbackGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * Contains methods for parsing and validating the contents of jSymbolic configuration files (txt 
 * implementation).
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public abstract class ValidatorConfigFile
{
	/* ABSTRACT METHODS *************************************************************************************/

	
	/**
	 * Return the names of all features contained in the specified jSymbolic configuration file. Throws an
	 * informative exception if one or more of the features specified in the file do not match the name of
	 * a feature actually implemented in jSymbolic.
	 *
	 * @param config_file_lines	The lines of text pre-parsed from the specified config_file.
	 * @param config_file		A valid jSymbolic configuration file. Must correspond to config_file_lines.
	 * @return					The names of all jSymbolic features parsed from the specified
	 *							config_file_lines.
	 * @throws Exception		Throws an informative exception if one or more of the features specified in
	 *							config_file_lines do not match the name of a feature actually implemented in
	 *							jSymbolic.
	 */
	public abstract List<String> getAndValidateFeatureNames(List<String> config_file_lines, File config_file)
		throws Exception;

	/**
	 * Validate the options syntax specified in the configuration file.
	 *
	 * @param config_file_lines A list of strings containing the line by line raw configuration file.
	 * @param config_file The configuration file for file information.
	 * @return ConfigurationOptionState based on the actual state of the configuration options.
	 * @throws Exception If syntax is not valid for options in the configuration file.
	 */
	public abstract ConfigFileWindowingAndOutputFormatSettings validateOptionSyntax(List<String> config_file_lines, File config_file) throws Exception;

	/**
	 * Validate the options logic specified in the configuration file. This occurs after the option syntax
	 * validation in the parseConfigFile(String, List, PrintSteram) method.
	 *
	 * @param configOption The syntax validated configuration option data.
	 * @param config_file The configuration file for file information.
	 * @return ConfigurationOptionState The fully validated state of the configuration options from the
	 * configuration file.
	 * @throws Exception If logic is not valid for options in the configuration file.
	 */
	public abstract ConfigFileWindowingAndOutputFormatSettings validateOptionLogic(ConfigFileWindowingAndOutputFormatSettings configOption, File config_file) throws Exception;

	/**
	 * Validate the input files in the specified configuration file.
	 *
	 * @param rawConfigFile A list of strings containing the line by line raw configuration file.
	 * @param config_file The configuration file for file information.
	 * @return ConfigurationInputFiles Both the valid and invalid input files that need to be processed.
	 * @throws Exception Thrown when invalid input file syntax/logic is found in the configuration file.
	 */
	public abstract ConfigFileInputFilePaths checkForInvalidInputFiles(List<String> rawConfigFile, File config_file) throws Exception;

	/**
	 * Validate the output files in the specified configuration file.
	 *
	 * @param rawConfigFile A list of strings containing the line by line raw configuration file.
	 * @param config_file The configuration file for file information.
	 * @return ConfigurationOutputFiles Returns the feature value save path and the feature description save
	 * path.
	 * @throws Exception Thrown when invalid output file syntax/logic is found in the configuration file.
	 */
	public abstract ConfigFileOutputFilePaths checkForInvalidOutputFiles(List<String> rawConfigFile, File config_file) throws Exception;

	/**
	 * Validate the header syntax in the specified configuration file.
	 *
	 * @param rawConfigFile A list of strings containing the line by line raw configuration file.
	 * @param config_file The configuration file for file information.
	 * @param headersToCheck The headers that need to be validated. Headers not contained in this list will
	 * not be validated.
	 * @throws Exception Thrown when configuration file is missing a header or has wrong header syntax on the
	 * headers that it needs to check.
	 */
	public abstract void validateHeaders(List<String> rawConfigFile, File config_file, List<EnumSectionDividers> headersToCheck) throws Exception;

	/**
	 * Extract from the configuration file a list of strings containing the line by line raw configuration
	 * file.
	 *
	 * @param config_file The configuration file that needs to be extracted from.
	 * @return A list of strings containing the line by line raw configuration file.
	 * @throws IOException Thrown if the configurationFile is not valid.
	 */
	public abstract List<String> extractRawConfigurationFile(File config_file) throws IOException;

	/**
	 * Validates the specified configuration file name. In particular, it validates if it is indeed a file and
	 * if the name is right.
	 *
	 * @param config_file The configuration file name that will be verified.
	 * @return A valid file created from the configuration file name input.
	 * @throws Exception Thrown if the name or the file itself is not valid.
	 */
	public abstract File checkConfigFile(String config_file) throws Exception;

	/**
	 * IMPORTANT: If a specific header does not need to be checked, then the value of the corresponding data
	 * in the returned ConfigurationFileData object will be set to null. For example, if input files and
	 * output save paths are specified in the command line instead of the configuration file, then this
	 * function will return a ConfigurationFileData object with all the appropriate data but the
	 * ConfigurationInputFiles Object and the ConfigurationOutputFiles object will be set to null.
	 *
	 * Convenience template method pattern to allow one function call to validate configuration file. This
	 * design pattern specifies order of function call but not implementation, which is left up to the
	 * programmer and depends on the format of the configuration file. Notice that configFile null is not
	 * checked as this should be done in the checkConfigFile function implementation.
	 *
	 * @param configurationFileName The configuration file name that needs to be validated.
	 * @param headersToCheck Headers that need to be validated in configuration file. Any headers not
	 * specified here will result in an exception and thus this method will print usage on error and then
	 * terminate the jSymbolic program.
	 * @param error_stream	A print stream to write errors to.
	 * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
	 * from the specified configuration file. Only headers that need to be checked will return appropriate
	 * data. All headers that need not be checked will be returned as null in the ConfigurationFileData return
	 * object.
	 * @throws Exception Thrown if configuration file is not valid.
	 */
	public ConfigFileCompleteData parseConfigFile(String configurationFileName,
			List<EnumSectionDividers> headersToCheck,
			PrintStream error_stream)
			throws Exception
	{
		//TODO could also have a function that takes in different config data and places it appropriately
		//Validate the actual config file
		File configFile = checkConfigFile(configurationFileName);

		//Read in the raw config file data
		List<String> rawConfigData = extractRawConfigurationFile(configFile);

		//Validate the headers in the config file
		validateHeaders(rawConfigData, configFile, headersToCheck);

		//Verify config file syntax and logic for options
		ConfigFileWindowingAndOutputFormatSettings configOptions = null;
		if (headersToCheck.contains(EnumSectionDividers.OPTIONS_HEADER))
		{
			configOptions = validateOptionLogic(validateOptionSyntax(rawConfigData, configFile), configFile);
		}

		//Verify config file syntax and logic for features
		List<String> featuresToSave = null;
		if (headersToCheck.contains(EnumSectionDividers.FEATURE_HEADER))
		{
			featuresToSave = getAndValidateFeatureNames(rawConfigData, configFile);
		}

		//Verify config file for invalid input files
		ConfigFileInputFilePaths invalidInputFiles = null;
		if (headersToCheck.contains(EnumSectionDividers.INPUT_FILES_HEADER))
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
		ConfigFileOutputFilePaths outputFiles = null;
		if (headersToCheck.contains(EnumSectionDividers.OUTPUT_FILES_HEADER))
		{
			outputFiles = checkForInvalidOutputFiles(rawConfigData, configFile);
		}

		return new ConfigFileCompleteData(featuresToSave,
				configOptions,
				outputFiles,
				configurationFileName,
				invalidInputFiles);
	}

	/**
	 * Parse and validate the configuration file, assuming that all headers need to validated.
	 *
	 * @param configurationFileName The configuration file name that needs to be validated.
	 * @param error_stream	A print stream to write errors to.
	 * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
	 * from the specified configuration file. In this case, all jSymbolic data is specified in the
	 * configuration file itself.
	 * @throws Exception Thrown if configuration file is not valid.
	 */
	public ConfigFileCompleteData parseConfigFileAllHeaders(String configurationFileName, PrintStream error_stream) throws Exception
	{
		return parseConfigFile(configurationFileName, Arrays.asList(EnumSectionDividers.values()), error_stream);
	}

	/**
	 * Parse and validate the configuration file with both all headers and, if this doesn't work, feature and
	 * option headers.
	 *
	 * @param configurationFileName The configuration file name that needs to be validated.
	 * @param error_stream	A print stream to write errors to.
	 * @return ConfigurationFileData object which contains all the appropriate data extracted and validated
	 * from the specified configuration file. In this case, all jSymbolic data is specified in the
	 * @throws Exception Thrown if configuration file is not valid.
	 */
	public ConfigFileCompleteData parseConfigFileAllOrFeatOpt(String configurationFileName, PrintStream error_stream)
			throws Exception
	{
		ConfigFileCompleteData configurationFileData = null;

		try
		{
			//Try with all headers
			configurationFileData = parseConfigFileAllHeaders(configurationFileName, error_stream);
		} catch (Exception ex)
		{
			//continue
		}

		//If it works then return it
		if (configurationFileData != null)
		{
			return configurationFileData;
		}

		//Otherwise try with only feature and option header and return it if valid
		List<EnumSectionDividers> headersToCheck = Arrays.asList(EnumSectionDividers.FEATURE_HEADER, EnumSectionDividers.OPTIONS_HEADER);
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
	public ConfigFileCompleteData parseConfigFileTwoThreeOrFour(String config_file_name, PrintStream error_stream)
			throws Exception
	{
		ConfigFileCompleteData configuration_file_data = null;

		// Try with all four headers
		try
		{
			configuration_file_data = parseConfigFileAllHeaders(config_file_name, error_stream);
			return configuration_file_data;
		} catch (Exception e)
		{
		}

		// Try with FEATURE_HEADER, OPTION_HEADER and INPUT_FILE_HEADER
		try
		{
			List<EnumSectionDividers> headersToCheck = Arrays.asList(EnumSectionDividers.FEATURE_HEADER, EnumSectionDividers.OPTIONS_HEADER, EnumSectionDividers.INPUT_FILES_HEADER);
			configuration_file_data = parseConfigFile(config_file_name, headersToCheck, error_stream);
			return configuration_file_data;
		} catch (Exception e)
		{
		}

		// Try with FEATURE_HEADER, OPTION_HEADER and OUTPUT_FILE_HEADER
		try
		{
			List<EnumSectionDividers> headersToCheck = Arrays.asList(EnumSectionDividers.FEATURE_HEADER, EnumSectionDividers.OPTIONS_HEADER, EnumSectionDividers.OUTPUT_FILES_HEADER);
			configuration_file_data = parseConfigFile(config_file_name, headersToCheck, error_stream);
			return configuration_file_data;
		} catch (Exception e)
		{
		}

		//Otherwise try with only FEATURE_HEADER and OPTION_HEADER, and return it if valid
		List<EnumSectionDividers> headersToCheck = Arrays.asList(EnumSectionDividers.FEATURE_HEADER, EnumSectionDividers.OPTIONS_HEADER);
		configuration_file_data = parseConfigFile(config_file_name, headersToCheck, error_stream);
		return configuration_file_data;
	}
}
