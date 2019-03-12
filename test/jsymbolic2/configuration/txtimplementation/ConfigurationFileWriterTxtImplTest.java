package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configurationfile.txtimplementation.EnumFieldValueDelimiter;
import jsymbolic2.configurationfile.txtimplementation.WriterConfigFileTxtImpl;
import jsymbolic2.configurationfile.EnumWindowingAndOutputFormatSettings;
import jsymbolic2.configurationfile.ConfigFileOutputFilePaths;
import jsymbolic2.configurationfile.EnumSectionDividers;
import jsymbolic2.configurationfile.EnumOutputFileTypes;
import jsymbolic2.configurationfile.ConfigFileInputFilePaths;
import jsymbolic2.configurationfile.ConfigFileWindowingAndOutputFormatSettings;
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
        List<String> expectedList = Arrays.asList(EnumSectionDividers.OPTIONS_HEADER.toString(),
                EnumWindowingAndOutputFormatSettings.window_size.name() + EnumFieldValueDelimiter.EQUAL + "1.5",
                EnumWindowingAndOutputFormatSettings.window_overlap.name() + EnumFieldValueDelimiter.EQUAL + "0.1",
                EnumWindowingAndOutputFormatSettings.save_features_for_each_window.name() + EnumFieldValueDelimiter.EQUAL + "true",
                EnumWindowingAndOutputFormatSettings.save_overall_recording_features.name() + EnumFieldValueDelimiter.EQUAL + "false",
                EnumWindowingAndOutputFormatSettings.convert_to_arff.name() + EnumFieldValueDelimiter.EQUAL + "false",
                EnumWindowingAndOutputFormatSettings.convert_to_csv.name() + EnumFieldValueDelimiter.EQUAL + "false"
        );
        List<String> actualList = new ArrayList<>();
        WriterConfigFileTxtImpl writer = new WriterConfigFileTxtImpl();
        actualList = writer.addFormattedOptions(actualList,
                            new ConfigFileWindowingAndOutputFormatSettings(1.5,0.1,true,false,false,false));

        assertEquals(expectedList,actualList);
    }

    @Test
    public void addFormattedFeatures() throws Exception {
        List<String> expectedList = Arrays.asList(EnumSectionDividers.FEATURE_HEADER.toString(),
                "Acoustic Guitar Prevalence",
                "Duration",
                "Beat Histogram"
        );

        WriterConfigFileTxtImpl writer = new WriterConfigFileTxtImpl();
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

        List<String> expectedList = Arrays.asList(EnumSectionDividers.INPUT_FILES_HEADER.toString(),
                fileName
        );

        List<String> actualList = new ArrayList<>();
        WriterConfigFileTxtImpl writer = new WriterConfigFileTxtImpl();
        ConfigFileInputFilePaths inputFiles = new ConfigFileInputFilePaths();
        inputFiles.addValidFile(new File(fileName));
        writer.addFormattedInputFiles(actualList,inputFiles);
        assertEquals(expectedList,actualList);
    }

    @Test
    public void addFormattedOutputFiles() throws Exception {
        String value = "test_value.xml";
        String definition = "test_definition.xml";

        List<String> expectedList = Arrays.asList(EnumSectionDividers.OUTPUT_FILES_HEADER.toString(),
                EnumOutputFileTypes.feature_values_save_path.name() + EnumFieldValueDelimiter.EQUAL + value);

        List<String> actualList = new ArrayList<>();
        WriterConfigFileTxtImpl writer = new WriterConfigFileTxtImpl();
        actualList = writer.addFormattedOutputFiles(actualList,new ConfigFileOutputFilePaths(value));

        assertEquals(expectedList,actualList);
    }

}