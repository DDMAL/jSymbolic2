package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configurationfile.txtimplementation.ValidatorConfigFileTxtImpl;
import jsymbolic2.configurationfile.ConfigFileOutputFilePaths;
import jsymbolic2.configurationfile.EnumSectionDividers;
import jsymbolic2.configurationfile.ValidatorConfigFile;
import jsymbolic2.configurationfile.ConfigFileCompleteData;
import jsymbolic2.configurationfile.ConfigFileInputFilePaths;
import jsymbolic2.configurationfile.ConfigFileWindowingAndOutputFormatSettings;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 5/17/16.
 */
public class ConfigurationFileValidatorTxtImplTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ValidatorConfigFile validate = new ValidatorConfigFileTxtImpl();

    private static File sampleConfiguration;
    private static List<String> rawSampleConfig;
    private static String sampleConfigFileName = "./test/jsymbolic2/configuration/resources/sampleConfiguration.txt";
    private static File sampleConfiguration2;
    private static List<String> rawSampleConfig2;
    private static String sampleConfigFileName2 = "./test/jsymbolic2/configuration/resources/sampleConfiguration2.txt";
    private static File invalidConfiguration;
    private static List<String> rawInvalidConfig;
    private static String invalidConfig = "./test/jsymbolic2/configuration/resources/invalidConfiguration.txt";
    private static File noIOConfiguration;
    private static List<String> rawNoIOConfig;
    private static String noIOConfig = "./test/jsymbolic2/configuration/resources/noIOConfiguration.txt";

    @Before
    public void setUp() throws Exception {
        sampleConfiguration = new File(sampleConfigFileName);
        sampleConfiguration2 = new File(sampleConfigFileName2);
        invalidConfiguration = new File(invalidConfig);
        noIOConfiguration = new File(noIOConfig);
        rawSampleConfig = Files.lines(Paths.get(sampleConfigFileName))
                               .collect(Collectors.toList());
        rawSampleConfig2 = Files.lines(Paths.get(sampleConfigFileName2))
                                .collect(Collectors.toList());
        rawInvalidConfig = Files.lines(Paths.get(invalidConfig))
                                .collect(Collectors.toList());
        rawNoIOConfig = Files.lines(Paths.get(noIOConfig))
                             .collect(Collectors.toList());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void validateHeaders() throws Exception {
        exception.expect(Exception.class);
        validate.validateHeaders(rawInvalidConfig,invalidConfiguration,Arrays.asList(EnumSectionDividers.values()));

        //Exception thrown when IO should not be but is in fact in the configuration file
        exception.expect(Exception.class);
        validate.validateHeaders(rawSampleConfig,sampleConfiguration,Arrays.asList(EnumSectionDividers.FEATURE_HEADER,EnumSectionDividers.OPTIONS_HEADER));
    }

    @Test
    public void parseConfigFile() throws Exception {
        //Validate normal configuration files
        List<String> featuresToSave = Arrays.asList("Duration", "Beat Histogram", "Acoustic Guitar Prevalence");
        ConfigFileWindowingAndOutputFormatSettings opt = new ConfigFileWindowingAndOutputFormatSettings(1.5,0.1,true,false,false,false);
        ConfigFileInputFilePaths input = new ConfigFileInputFilePaths();
        input.addValidFile(new File("./test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei"));
        ConfigFileOutputFilePaths output = new ConfigFileOutputFilePaths("test_value.xml");
        ConfigFileCompleteData expecteddata = new ConfigFileCompleteData(featuresToSave,opt,output,sampleConfigFileName,input);
        ConfigFileCompleteData actualdata =
                validate.parseConfigFileAllHeaders(sampleConfigFileName, System.err);
        assertEquals(expecteddata,actualdata);

        //Validate configuration files with no IO
        List<String> ioToSave = Arrays.asList("Duration", "Beat Histogram", "Acoustic Guitar Prevalence");
        ConfigFileWindowingAndOutputFormatSettings ioOpt = new ConfigFileWindowingAndOutputFormatSettings(1.5,0.1,true,false,false,false);
        ConfigFileCompleteData expectedIOData = new ConfigFileCompleteData(ioToSave,ioOpt,null,noIOConfig,null);
        ConfigFileCompleteData actualIOData =
                validate.parseConfigFile(noIOConfig, Arrays.asList(EnumSectionDividers.FEATURE_HEADER, EnumSectionDividers.OPTIONS_HEADER), System.err);
        assertEquals(expectedIOData,actualIOData);
    }

    @Test
    public void checkForInvalidOutputFiles() throws Exception {
        ConfigFileOutputFilePaths expectedSavePaths = new ConfigFileOutputFilePaths("test_value.xml");
        assertEquals(expectedSavePaths,validate.checkForInvalidOutputFiles(rawSampleConfig,sampleConfiguration));

        exception.expect(Exception.class);
        validate.checkForInvalidOutputFiles(rawInvalidConfig,invalidConfiguration);
    }

    @Test
    public void validateFeatureSyntax() throws Exception {
        List<String> expectedFeatures = Arrays.asList("Acoustic Guitar Prevalence",
                "Duration", "Beat Histogram");
        assertEquals(expectedFeatures,validate.getAndValidateFeatureNames(rawSampleConfig,sampleConfiguration));

        exception.expect(Exception.class);
        validate.getAndValidateFeatureNames(rawInvalidConfig, invalidConfiguration);
    }

    @Test
    public void validateOptionSyntax() throws Exception {
        ConfigFileWindowingAndOutputFormatSettings expectedState = new ConfigFileWindowingAndOutputFormatSettings(1.5,0.1,true,false,false,false);
        assertEquals(expectedState,validate.validateOptionSyntax(rawSampleConfig, sampleConfiguration));
        assertEquals(expectedState,validate.validateOptionSyntax(rawSampleConfig2, sampleConfiguration2));

        exception.expect(Exception.class);
        validate.validateOptionSyntax(rawInvalidConfig, invalidConfiguration);
    }

    @Test
    public void checkForInvalidInputFiles() throws Exception {
        ConfigFileInputFilePaths input = new ConfigFileInputFilePaths();
        input.addValidFile(new File("./test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei"));
        assertEquals(input,validate.checkForInvalidInputFiles(rawSampleConfig,sampleConfiguration));

        ConfigFileInputFilePaths expectedInvalid = new ConfigFileInputFilePaths();
        expectedInvalid.addValidFile(new File("./test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei"));
        expectedInvalid.addInvalidFile(new File("./invalid.midi"));

        assertEquals(expectedInvalid,validate.checkForInvalidInputFiles(rawInvalidConfig,invalidConfiguration));
    }

    @Test
    public void checkConfigFile() throws Exception {
        String meiFileName = "./test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei";

        exception.expect(Exception.class);
        validate.checkConfigFile(meiFileName);

        exception.expect(Exception.class);
        validate.checkConfigFile("dne.txt");
    }

}