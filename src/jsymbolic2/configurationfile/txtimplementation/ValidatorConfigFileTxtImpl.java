package jsymbolic2.configurationfile.txtimplementation;

import jsymbolic2.configurationfile.*;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.MusicFilter;

import mckay.utilities.staticlibraries.FileMethods;
import mckay.utilities.staticlibraries.StringMethods;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains methods for parsing and validating the contents of jSymbolic configuration files (txt 
 * implementation).
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ValidatorConfigFileTxtImpl
	extends ValidatorConfigFile
{
	/* METHODS **********************************************************************************************/


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
	@Override
	public List<String> getAndValidateFeatureNames(List<String> config_file_lines, File config_file)
		throws Exception
	{
		// Variables for parsing the config file contents
		String feature_header = EnumSectionDividers.FEATURE_HEADER.toString();
		int feature_header_index = config_file_lines.indexOf(feature_header);
		int next_header_index = nextHeaderIndex(config_file_lines, feature_header);

		// Note each feature contained in the config file
		List<String> features_noted_in_config_file = config_file_lines.subList(feature_header_index + 1, next_header_index);

		// Verify all features contained in the config file are implemented in jSymbolic
		List<String> all_implemented_feature_names = FeatureExtractorAccess.getNamesOfAllImplementedFeatures();
		int current_line_number = feature_header_index + 1;
		for (String feature_line_in_config_file : features_noted_in_config_file)
		{
			if (!all_implemented_feature_names.contains(feature_line_in_config_file))
				throw new Exception("The " + config_file.getName() + " configuration file has an error at line " + current_line_number + ": " + feature_line_in_config_file + " does not match the name of a feature implemented in jSymbolic.");
			current_line_number++;
		}
		
		// Remove duplicate feature entries, if any
		List<String> features_duplicates_removed = new ArrayList<>(new HashSet<>(features_noted_in_config_file));
		
		// Return the results
		return features_duplicates_removed;
	}
	
	/**
	 * Validate the syntax of the options in the rawConfigFile input. This deals with the verifications made
	 * in the {@link #checkOptionLine(String, List, int, File)} function, which validates syntax and type for
	 * the options. It also validates that all the options do in fact exist in the configuration file.
	 *
	 * @param config_file_lines A list of strings containing the line by line raw configuration file.
	 * @param config_file The configuration file for file information.
	 * @return The option state of the configuration file through a
	 * {@link ConfigFileWindowingAndOutputFormatSettings} object.
	 * @throws Exception Thrown if option syntax or format is invalid.
	 */
	@Override
	public ConfigFileWindowingAndOutputFormatSettings validateOptionSyntax(List<String> config_file_lines, File config_file) throws Exception
	{
		String optionHeader = EnumSectionDividers.OPTIONS_HEADER.toString();
		int currentHeaderIndex = config_file_lines.indexOf(optionHeader);
		int nextHeaderIndex = nextHeaderIndex(config_file_lines, optionHeader);

		//Option value storage for later instantiation of option state object
		boolean saveWindow = false;
		boolean saveOverall = false;
		boolean convertToArff = false;
		boolean convertToCsv = false;
		double windowSize = 0;
		double windowOverlap = 0;

		if (nextHeaderIndex - currentHeaderIndex - 1 != EnumWindowingAndOutputFormatSettings.values().length)
		{
			throw new Exception("Configuration file for jSymbolic " + config_file.getName() + " does not "
					+ "contain all possible options. It is required that all of " + Arrays.toString(EnumSectionDividers.values())
					+ " must be options under " + optionHeader + " in configuration file.");
		}

		//List and check all options mentioned in config
		List<String> optionLineList = config_file_lines.subList(currentHeaderIndex + 1, nextHeaderIndex);
		List<String> optionsInConfig = new ArrayList<>();
		int lineNumber = nextHeaderIndex + 1;
		for (String optionLine : optionLineList)
		{
			//Check this particular option
			ConfigFileFieldNameValuePair option = checkOptionLine(optionLine, config_file_lines, lineNumber, config_file);
			optionsInConfig.add(option.getFieldName().toString());

			//Assign appropriate values for later processing
			//This can be assigned directly since the option has already been checked
			switch (option.getFieldName())
			{
				case window_size:
					windowSize = Double.parseDouble(option.getFieldValue());
					break;
				case window_overlap:
					windowOverlap = Double.parseDouble(option.getFieldValue());
					break;
				case save_features_for_each_window:
					saveWindow = Boolean.parseBoolean(option.getFieldValue());
					break;
				case save_overall_recording_features:
					saveOverall = Boolean.parseBoolean(option.getFieldValue());
					break;
				case convert_to_arff:
					convertToArff = Boolean.parseBoolean(option.getFieldValue());
					break;
				case convert_to_csv:
					convertToCsv = Boolean.parseBoolean(option.getFieldValue());
					break;
			}
			lineNumber++;
		}

		//Return option state if all options are in fact mentioned in config file
		if (EnumWindowingAndOutputFormatSettings.allOptionsExist(optionsInConfig))
		{
			return new ConfigFileWindowingAndOutputFormatSettings(windowSize, windowOverlap, saveWindow, saveOverall, convertToArff, convertToCsv);
		} else
		{
			throw new Exception("Configuration file for jSymbolic " + config_file.getName() + " does not "
					+ "contain all possible options. It is required that all of " + Arrays.toString(EnumSectionDividers.values())
					+ " must be options under " + optionHeader + " in configuration file.");
		}
	}

	/**
	 * Validate the logic of the options in the passed in configOption state object. This currently ensures
	 * that the save overall recording features and save windowed recording features are not the same boolean
	 * value.
	 *
	 * @param configOption The syntax validated configuration option data.
	 * @param config_file The configuration file for file information.
	 * @return The configuration option state with the validated logic.
	 * @throws Exception Thrown if overall and windowed recording feature option are the same boolean value.
	 */
	@Override
	public ConfigFileWindowingAndOutputFormatSettings validateOptionLogic(ConfigFileWindowingAndOutputFormatSettings configOption, File config_file)
			throws Exception
	{
		//Check that save overall and save window are not the same
		if (configOption.getSaveOverallRecordingFeatures() && configOption.getSaveFeaturesForEachWindow())
		{
			throw new Exception("In configuration file " + config_file.getName()
					+ EnumWindowingAndOutputFormatSettings.save_overall_recording_features.name() + " and "
					+ EnumWindowingAndOutputFormatSettings.save_features_for_each_window.name() + " are both true. "
					+ "This is not allowed in jSymbolic, either one or the other must be true, but not both.");
		}
		if (!configOption.getSaveOverallRecordingFeatures() && !configOption.getSaveFeaturesForEachWindow())
		{
			throw new Exception("In configuration file " + config_file.getName()
					+ EnumWindowingAndOutputFormatSettings.save_overall_recording_features.name() + " and "
					+ EnumWindowingAndOutputFormatSettings.save_features_for_each_window.name() + " are both false. "
					+ "This is not allowed in jSymbolic, either one or the other must be true, but not both.");
		}
		return configOption;
	}

	/**
	 * Performs checks for each option in the configuration file. Specifically, checks 1) format is
	 * optionName=optionValue 2) optionName is a valid option 3) optionValue is appropriate type for
	 * corresponding optionName
	 *
	 * @param optionLine The current option line that needs to be checked.
	 * @param rawConfigData The configuration file that the option was taken from.
	 * @param lineNumber The line that the option is on in the configuration file.
	 * @param config_file The configuration file that was parsed.
	 * @return ConfigurationOption object that contains both the appropriate value and key.
	 * @throws Exception Thrown if any syntax errors are found in the options portion of the configuration
	 * file.
	 */
	public ConfigFileFieldNameValuePair checkOptionLine(String optionLine, List<String> rawConfigData, int lineNumber, File config_file)
			throws Exception
	{
		//Check that we have optionName=optionValue format
		String[] optionArray = optionLine.split("=");
		if (optionArray.length != 2)
		{
			throw new Exception("Configuration file for jSymbolic " + config_file.getName() + " at line "
					+ lineNumber + " - " + optionLine + " is incorrect option for config file format.");
		}

		//Check that optionName is in fact a valid known option
		String optionName = optionArray[0];
		String optionValue = optionArray[1];
		ConfigFileFieldNameValuePair option
				= new ConfigFileFieldNameValuePair(EnumWindowingAndOutputFormatSettings.valueOf(optionName), optionValue);
		if (!EnumWindowingAndOutputFormatSettings.contains(optionName))
		{
			throw new Exception("Configuration file for jSymbolic " + config_file.getName() + " at line "
					+ lineNumber + " - " + optionLine + " is non-existent option for config file.");
		}

		//Check that the optionValue type corresponds to the appropriate optionName
		EnumWindowingAndOutputFormatSettings optEnum = EnumWindowingAndOutputFormatSettings.valueOf(optionName);
		if (!optEnum.checkValue(optionValue))
		{
			throw new Exception("Configuration file for jSymbolic " + config_file.getName() + " at line "
					+ lineNumber + " - " + optionLine + " is incorrect value format for " + optEnum.toString());
		}

		return option;
	}

	/**
	 * Implementation for the checking of invalid input files. Only single mei and midi files are allowed as
	 * per the use of the Music File Filter. Any invalid files will be added to the invalid log which this
	 * function will return. Directories are also considered invalid and will not be processed and simply
	 * returned in the invalid return log.
	 *
	 * @param rawConfigFile The configuration file that needs to be checked.
	 * @param config_file The configuration file for file information.
	 * @return A list of invalid files and directories specified in the configuration file.
	 * @throws Exception Thrown if input file is not valid.
	 */
	@Override
	public ConfigFileInputFilePaths checkForInvalidInputFiles(List<String> rawConfigFile, File config_file) throws Exception
	{
		ConfigFileInputFilePaths inputFileList = new ConfigFileInputFilePaths();
		String inputHeader = EnumSectionDividers.INPUT_FILES_HEADER.toString();
		int currentHeaderIndex = rawConfigFile.indexOf(inputHeader);
		int nextHeaderIndex = nextHeaderIndex(rawConfigFile, inputHeader);
		List<String> inputFiles = rawConfigFile.subList(currentHeaderIndex + 1, nextHeaderIndex);
		for (String inputLine : inputFiles)
		{
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
	 * feature_values_save_path=test_value.xml and the specified files need to exist and be accessible in the
	 * local system. Anything else will result in a thrown exception.
	 *
	 * @param rawConfigFile The raw line by line configuration file that needs to be validated.
	 * @return A List of the valid files. Index 1 would contain feature value save path and index 2 would
	 * contain feature definition save path.
	 * @throws Exception thrown if format is wrong or if path does not exist in local system
	 */
	@Override
	public ConfigFileOutputFilePaths checkForInvalidOutputFiles(List<String> rawConfigFile, File config_file) throws Exception
	{
		String outputHeader = EnumSectionDividers.OUTPUT_FILES_HEADER.toString();
		String valueSavePath = EnumOutputFileTypes.feature_values_save_path.name();

		int currentHeaderIndex = rawConfigFile.indexOf(outputHeader);
		int nextHeaderIndex = nextHeaderIndex(rawConfigFile, outputHeader);

		List<String> outputFileList = rawConfigFile.subList(currentHeaderIndex + 1, nextHeaderIndex);
		if (outputFileList.size() != EnumOutputFileTypes.values().length)
		{
			throw new Exception("Missing output files. Need to include line of the form feature_value_save_path=example.xml under the header" + outputHeader);
		}

		String featureValuesSavePath = "";
		for (String output : outputFileList)
			if (output.contains(valueSavePath))
				featureValuesSavePath = output;

		String[] valuesArray = featureValuesSavePath.split("=");
		if (valuesArray.length != 2)
		{
			throw new Exception("Configuration file for jSymbolic " + config_file.getName()
					+ " is invalid feature value format for config file format. Need to have "
					+ "feature_value_save_path=example.xml");
		}

		//Ensured to not throw null pointer due to previous check for array length = 2
		String valuesName = valuesArray[0];
		String valuesValue = valuesArray[1];
		if (!valuesName.equals(valueSavePath))
		{
			throw new Exception("Path of features values save file must be: " + valueSavePath + ". It is now "
					+ valuesName);
		}

		//Check that path exists for feature value save path
		if (!FileMethods.pathExists(valuesValue))
		{
			throw new Exception("Feature value save path " + valuesValue + " is not a valid path in configuration file "
					+ config_file.getName());
		}

		//Check and correct that the extension is in fact xml
		valuesValue = StringMethods.correctExtension(valuesValue, "xml");
		ConfigFileOutputFilePaths outputFile = new ConfigFileOutputFilePaths(valuesValue);
		return outputFile;
	}

	/**
	 * Validates if all headers to check in HeaderEnum are in the specified configuration file.
	 *
	 * @param rawConfigFile Raw string lines of the configuration file.
	 * @param config_file The original configuration file for extra data (e.g. file name).
	 * @param headersToCheck Headers that need to be checked in the configuration file.
	 * @throws Exception Thrown if any header in the HeaderEnum is missing and if headers that do not need to
	 * be checked are found in the configuration file.
	 */
	@Override
	public void validateHeaders(List<String> rawConfigFile, File config_file, List<EnumSectionDividers> headersToCheck)
			throws Exception
	{
		for (EnumSectionDividers header : headersToCheck)
		{
			if (Collections.frequency(rawConfigFile, header.toString()) != 1)
			{
				throw new Exception(config_file.getName() + " is not a valid configuration file "
						+ "as it does not contain 1 of the following header : " + header.toString());
			}
		}

		List<EnumSectionDividers> headersNotToCheck = headersNotToCheck(headersToCheck);
		for (EnumSectionDividers header : headersNotToCheck)
		{
			if (rawConfigFile.contains(header.toString()))
			{
				throw new Exception("jSymbolic configuration file " + config_file.getName() + " contains "
						+ header.toString() + " which has already been specified in the command line.\n Either "
						+ "include this header/data in the configuration file or in the command line, but not both.");
			}
		}
	}

	/**
	 * Helper function to get the headers that do not need to be checked.
	 *
	 * @param headersToCheck List of headers that need to be checked.
	 * @return List of headers that do not need to be checked.
	 */
	private List<EnumSectionDividers> headersNotToCheck(List<EnumSectionDividers> headersToCheck)
	{
		List<EnumSectionDividers> allHeaders = Arrays.asList(EnumSectionDividers.values());
		List<EnumSectionDividers> headersNotToCheck = new ArrayList<>();
		for (EnumSectionDividers header : allHeaders)
		{
			if (!headersToCheck.contains(header))
			{
				headersNotToCheck.add(header);
			}
		}
		return headersNotToCheck;
	}

	/**
	 * Helper function to return next header index after in raw data after the given header.
	 *
	 * @param rawData Raw data of config file to be checked.
	 * @param header Header to start from.
	 * @return Next header index if it is found, otherwise returns rawData.size().
	 */
	private int nextHeaderIndex(List<String> rawData, String header)
	{
		int currentHeaderIndex = rawData.indexOf(header);
		for (int nextIndex = currentHeaderIndex + 1; nextIndex < rawData.size(); nextIndex++)
		{
			String nextLine = rawData.get(nextIndex);
			if (EnumSectionDividers.contains(nextLine))
			{
				return nextIndex;
			}
		}
		return rawData.size();
	}

	/**
	 * Read in the file line by line.
	 *
	 * @param config_file File to be read in.
	 * @return The raw data from the file line by line.
	 * @throws IOException Thrown if there were problems parsing or retrieving the configuration file.
	 */
	@Override
	public List<String> extractRawConfigurationFile(File config_file) throws IOException
	{
		return Files.lines(Paths.get(config_file.getAbsolutePath()), Charset.forName("ISO-8859-1"))
				.collect(Collectors.toList());
	}

	private void validateSingleFile(File file, ConfigFileInputFilePaths inputFiles)
	{
		try
		{
			FileMethods.validateFile(file, true, false);
			inputFiles.addValidFile(file);
		} catch (Exception e)
		{
			inputFiles.addInvalidFile(file);
		}
	}

	/**
	 * Currently unused as input directories are considered invalid in the configuration file.
	 *
	 * @param directory The directory that needs to be validated.
	 * @param inputFiles The input files extracted from the configuration file.
	 */
	private void validateDirectory(File directory, ConfigFileInputFilePaths inputFiles)
	{
		File[] allFiles = FileMethods.getAllFilesInDirectory(directory, true, new MusicFilter(), null);
		for (File file : allFiles)
		{
			validateSingleFile(file, inputFiles);
		}
	}

	/**
	 * Validates the configuration file to see if the give file exists and if it is a .txt.
	 *
	 * @param configurationFile File name to be validated.
	 * @return The associated file if it is valid.
	 * @throws Exception Thrown if file dne or if it is not in .txt format.
	 */
	@Override
	public File checkConfigFile(String configurationFile) throws Exception
	{
		File configFile = new File(configurationFile);
		String configFileName = ".*\\." + EnumFileExtension.txt.name();
		if (configFile == null || !configFile.exists())
		{
			throw new Exception("The configuration file specified at " + configurationFile + " does not exist.");
		}
		if (!configurationFile.matches(configFileName))
		{
			throw new Exception("The file " + configurationFile + " does not have a txt extension.");
		}
		return configFile;
	}
}
