package jsymbolic2.commandline;

import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.processing.AceConversion;

import java.io.File;
import java.util.List;

/**
 * Running jSymbolic from the command line using a configuration file.
 *
 * @author Tristano Tenaglia
 */
public class CommandLineConfig {

    /**
     * Convenience method for when all data can be taken from the configuration file itself.
     *
     * @param configFileData The configuration file data that needs to be taken from.
     */
    public CommandLineConfig(ConfigurationFileData configFileData) {
        this(configFileData,
                configFileData.getInputFileList().getValidFiles(),
                configFileData.getFeatureValueSavePath(),
                configFileData.getFeatureDefinitionSavePath()
        );
    }

    /**
     * Constructor which runs the feature extraction and all other appropriate options.
     *
     * @param configFileData            The configuration file data to be used for feature extraction.
     * @param inputFileList             The input file list where features need to be extracted from.
     * @param featureValueSavePath      The save path of the feature values file.
     * @param featureDefinitionSavePath The save path of the feature definition file.
     */
    public CommandLineConfig(ConfigurationFileData configFileData,
                             List<File> inputFileList,
                             String featureValueSavePath,
                             String featureDefinitionSavePath) {
        //Extract features
        CommandLineFeatureExtraction.setupFeatureProcessorFromConfig(configFileData,
                inputFileList,
                featureValueSavePath,
                featureDefinitionSavePath);

        //Convert to arff and csv if necessary
        try {
            AceConversion.outputArffandCsvFormats(featureValueSavePath,
                    configFileData.convertToArff(), configFileData.convertToCsv());
        } catch (Exception ex) {
            //ex.printStackTrace();
            CommandLineUtils.printMessageAndTerminate(ex.getMessage(), -1);
        }
    }
}
