package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configuration.*;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 5/27/16.
 */
public class ConfigurationFileWriterTxtImplTest {
    @Test
    public void addFormattedOptions() throws Exception {
        List<String> expectedList = Arrays.asList(ConfigFileHeaderEnum.OPTION_HEADER.toString(),
                OptionsEnum.window_size.name() + ConfigurationFileDelimiterEnum.EQUAL + "1.5",
                OptionsEnum.window_overlap.name() + ConfigurationFileDelimiterEnum.EQUAL + "0.1",
                OptionsEnum.save_features_for_each_window.name() + ConfigurationFileDelimiterEnum.EQUAL + "true",
                OptionsEnum.save_overall_recording_features.name() + ConfigurationFileDelimiterEnum.EQUAL + "false",
                OptionsEnum.convert_to_arff.name() + ConfigurationFileDelimiterEnum.EQUAL + "false",
                OptionsEnum.convert_to_csv.name() + ConfigurationFileDelimiterEnum.EQUAL + "false"
        );
        List<String> actualList = new ArrayList<>();
        ConfigurationFileWriterTxtImpl writer = new ConfigurationFileWriterTxtImpl();
        actualList = writer.addFormattedOptions(actualList,
                            new ConfigurationOptionState(1.5,0.1,true,false,false,false));

        assertEquals(expectedList,actualList);
    }

    @Test
    public void addFormattedFeatures() throws Exception {
        List<String> expectedList = Arrays.asList(
                ConfigFileHeaderEnum.FEATURE_HEADER.toString(),
                "Acoustic Guitar Prevalence",
                "Duration",
                "Beat Histogram"
        );

        ConfigurationFileWriterTxtImpl writer = new ConfigurationFileWriterTxtImpl();
        List<String> actualList = new ArrayList<>();
        actualList = writer.addFormattedFeatures(
                actualList,
                Arrays.asList("Acoustic Guitar Prevalence",
                                "Duration",
                                "Beat Histogram")
        );

        assertEquals(expectedList,actualList);
    }

    @Test
    public void addFormattedInputFiles() throws Exception {
        String fileName = "test.mei";

        List<String> expectedList = Arrays.asList(
                ConfigFileHeaderEnum.INPUT_FILE_HEADER.toString(),
                fileName
        );

        List<String> actualList = new ArrayList<>();
        ConfigurationFileWriterTxtImpl writer = new ConfigurationFileWriterTxtImpl();
        ConfigurationInputFiles inputFiles = new ConfigurationInputFiles();
        inputFiles.addValidFile(new File(fileName));
        writer.addFormattedInputFiles(actualList,inputFiles);
        assertEquals(expectedList,actualList);
    }

    @Test
    public void addFormattedOutputFiles() throws Exception {
        String value = "test_value.xml";
        String definition = "test_definition.xml";

        List<String> expectedList = Arrays.asList(
                ConfigFileHeaderEnum.OUTPUT_FILE_HEADER.toString(),
                OutputEnum.feature_values_save_path.name() + ConfigurationFileDelimiterEnum.EQUAL + value,
                OutputEnum.feature_definitions_save_path.name() + ConfigurationFileDelimiterEnum.EQUAL + definition
        );

        List<String> actualList = new ArrayList<>();
        ConfigurationFileWriterTxtImpl writer = new ConfigurationFileWriterTxtImpl();
        actualList = writer.addFormattedOutputFiles(actualList,new ConfigurationOutputFiles(value,definition));

        assertEquals(expectedList,actualList);
    }

}