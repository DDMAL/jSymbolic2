package jsymbolic2.configuration;

import jsymbolic2.configuration.txtimplementation.ConfigurationFileExtensionEnum;
import jsymbolic2.processing.SymbolicMusicFileUtilities;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A general class for implementing a configuration file writer for the jSymbolic program.
 * Methods have been separated into adding different sections for the configuration file itself.
 * Accordingly, features, options, input files and output files are each added to the raw line by line
 * raw configuration file.
 * </p>
 * <p>
 * Each abstract method should be implemented to write the particular data section to the specified
 * raw List of line by line configuration file.
 * </p>
 * <p>
 * In particular, the {@link #write(ConfigurationFileData, List)} method is not abstract and uses the template method pattern
 * to write the configuration file to a file on disk. This method runs
 * the abstract functions in the correct order and also deals with which headers to write and proper
 * configuration file format.
 * </p>
 * <p>
 * It is worth noting that each method should also write out the appropriate headers for each section
 * if need be.
 * </p>
 * No file validation should be performed in the writer since the configuration file can be used separately.
 *
 * @author Tristano Tenaglia
 */
public abstract class ConfigurationFileWriter {

    /**
     * Add appropriately formatted options to the raw line by line raw configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param optionState The state of the options sections that needs to be written to the configuration file.
     * @return A raw line by line configuration file that now has the properly formatted configuration
     * option state.
     */
    public abstract List<String> addFormattedOptions(List<String> rawConfigFile, ConfigurationOptionState optionState);

    /**
     * Add appropriately formatted features to the raw line by line raw configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param featuresToSave The names of all the features that need to be saved in the configuration file.
     * @return A raw line by line configuration file that now has the properly formatted configuration
     * feature state.
     */
    public abstract List<String> addFormattedFeatures(List<String> rawConfigFile, List<String> featuresToSave);

    /**
     * Add appropriately formatted input files to the raw line by line raw configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param inputFiles The input files that need to be added to the configuration file.
     * @return A raw line by line configuration file that now has the properly formatted configuration
     * input file state.
     */
    public abstract List<String> addFormattedInputFiles(List<String> rawConfigFile, ConfigurationInputFiles inputFiles);

    /**
     * Add appropriately formatted output files to the raw line by line raw configuration file.
     * @param rawConfigFile List of string for the raw line by line configuration file.
     * @param outputFiles The output files that need to be added to the configuration file.
     * @return A raw line by line configuration file that now has the properly formatted configuration
     * output file state.
     */
    public abstract List<String> addFormattedOutputFiles(List<String> rawConfigFile, ConfigurationOutputFiles outputFiles);

    /**
     * The template method pattern which correctly uses the abstract methods to build up the raw line by line
     * configuration file. It then writes the file to disk with the appropriate extension.
     * @param configurationFileData The data that needs to be written to the configuration file.
     * @param headersToWrite The headers and corresponding sections that need to be written to the configuration file.
     * @throws IOException Thrown if any writing problems occur on the local system.
     */
    public void write(ConfigurationFileData configurationFileData, List<ConfigFileHeaderEnum> headersToWrite)
            throws IOException
    {
        List<String> rawConfigFile = new ArrayList<>();

        if(headersToWrite.contains(ConfigFileHeaderEnum.OPTION_HEADER)) {
            addFormattedOptions(rawConfigFile,configurationFileData.getOptionState());
        }

        if(headersToWrite.contains(ConfigFileHeaderEnum.FEATURE_HEADER)) {
            addFormattedFeatures(rawConfigFile,configurationFileData.getFeaturesToSave());
        }

        if(headersToWrite.contains(ConfigFileHeaderEnum.INPUT_FILE_HEADER)) {
            addFormattedInputFiles(rawConfigFile,configurationFileData.getInputFileList());
        }

        if(headersToWrite.contains(ConfigFileHeaderEnum.OUTPUT_FILE_HEADER)) {
            addFormattedOutputFiles(rawConfigFile,configurationFileData.getOutputFileList());
        }

        String correctConfigFileName = SymbolicMusicFileUtilities.correctFileExtension(configurationFileData.getConfigurationFilePath(), ConfigurationFileExtensionEnum.txt.name());
        Files.write(Paths.get(correctConfigFileName), rawConfigFile, Charset.forName("ISO-8859-1"));
    }
}
