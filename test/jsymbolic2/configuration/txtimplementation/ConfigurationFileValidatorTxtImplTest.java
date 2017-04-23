package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configuration.*;
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

    private ConfigurationFileValidator validate = new ConfigurationFileValidatorTxtImpl();

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
        validate.validateHeaders(rawInvalidConfig,invalidConfiguration,Arrays.asList(ConfigFileHeaderEnum.values()));

        //Exception thrown when IO should not be but is in fact in the configuration file
        exception.expect(Exception.class);
        validate.validateHeaders(rawSampleConfig,sampleConfiguration,Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER,ConfigFileHeaderEnum.OPTION_HEADER));
    }

    @Test
    public void parseConfigFile() throws Exception {
        //Validate normal configuration files
        List<String> featuresToSave = Arrays.asList("Duration", "Beat Histogram", "Acoustic Guitar Prevalence");
        ConfigurationOptionState opt = new ConfigurationOptionState(1.5,0.1,true,false,false,false);
        ConfigurationInputFiles input = new ConfigurationInputFiles();
        input.addValidFile(new File("./test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei"));
        ConfigurationOutputFiles output = new ConfigurationOutputFiles("test_value.xml","test_definition.xml");
        ConfigurationFileData expecteddata = new ConfigurationFileData(featuresToSave,opt,output,sampleConfigFileName,input);
        ConfigurationFileData actualdata =
                validate.parseConfigFileAllHeaders(sampleConfigFileName, System.err);
        assertEquals(expecteddata,actualdata);

        //Validate configuration files with no IO
        List<String> ioToSave = Arrays.asList("Duration", "Beat Histogram", "Acoustic Guitar Prevalence");
        ConfigurationOptionState ioOpt = new ConfigurationOptionState(1.5,0.1,true,false,false,false);
        ConfigurationFileData expectedIOData = new ConfigurationFileData(ioToSave,ioOpt,null,noIOConfig,null);
        ConfigurationFileData actualIOData =
                validate.parseConfigFile(noIOConfig, Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER), System.err);
        assertEquals(expectedIOData,actualIOData);
    }

    @Test
    public void checkForInvalidOutputFiles() throws Exception {
        ConfigurationOutputFiles expectedSavePaths = new ConfigurationOutputFiles("test_value.xml","test_definition.xml");
        assertEquals(expectedSavePaths,validate.checkForInvalidOutputFiles(rawSampleConfig,sampleConfiguration));

        exception.expect(Exception.class);
        validate.checkForInvalidOutputFiles(rawInvalidConfig,invalidConfiguration);
    }

    @Test
    public void validateFeatureSyntax() throws Exception {
        List<String> expectedFeatures = Arrays.asList("Acoustic Guitar Prevalence",
                "Duration", "Beat Histogram");
        assertEquals(expectedFeatures,validate.validateFeatureSyntax(rawSampleConfig,sampleConfiguration));

        exception.expect(Exception.class);
        validate.validateFeatureSyntax(rawInvalidConfig, invalidConfiguration);
    }

    @Test
    public void validateOptionSyntax() throws Exception {
        ConfigurationOptionState expectedState = new ConfigurationOptionState(1.5,0.1,true,false,false,false);
        assertEquals(expectedState,validate.validateOptionSyntax(rawSampleConfig, sampleConfiguration));
        assertEquals(expectedState,validate.validateOptionSyntax(rawSampleConfig2, sampleConfiguration2));

        exception.expect(Exception.class);
        validate.validateOptionSyntax(rawInvalidConfig, invalidConfiguration);
    }

    @Test
    public void checkForInvalidInputFiles() throws Exception {
        ConfigurationInputFiles input = new ConfigurationInputFiles();
        input.addValidFile(new File("./test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei"));
        assertEquals(input,validate.checkForInvalidInputFiles(rawSampleConfig,sampleConfiguration));

        ConfigurationInputFiles expectedInvalid = new ConfigurationInputFiles();
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