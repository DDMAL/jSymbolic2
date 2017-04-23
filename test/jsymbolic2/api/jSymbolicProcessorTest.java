package jsymbolic2.api;

import jsymbolic2.api.deprecated.JsymbolicData;
import jsymbolic2.api.deprecated.JsymbolicProcessorDeprecated;
import ace.datatypes.DataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for the jSymbolic API.
 *
 * @author Tristano Tenaglia
 */
public class jSymbolicProcessorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    //Try testing with both raw data processor and configuration file processor
    private JsymbolicProcessorDeprecated processor;
    private JsymbolicProcessorDeprecated processorConfig;
    private JsymbolicProcessorDeprecated processorConvert;
    private String valuesPath = "./test/jsymbolic2/api/resources/feature_values.xml";
    private String definitionsPath = "./test/jsymbolic2/api/resources/feature_definitions.xml";

    @Before
    public void setUp() throws Exception {
        List<String> featureNames = Arrays.asList("Duration", "Acoustic Guitar Prevalence", "Beat Histogram");
        processor = new JsymbolicProcessorDeprecated(valuesPath, definitionsPath, false, false, featureNames, false, true, 10, 0.1, System.out, System.err);
        processorConfig = new JsymbolicProcessorDeprecated("./test/jsymbolic2/api/resources/sampleConfiguration.txt", System.out, System.err);
        processorConvert = new JsymbolicProcessorDeprecated("./test/jsymbolic2/api/resources/sampleConfigConvert.txt", System.out, System.err);
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(Paths.get(valuesPath));
        Files.delete(Paths.get(definitionsPath));
    }

    @Test
    public void getJsymbolicData() throws Exception {
        File saintSaensTest = new File("./test/jsymbolic2/api/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei");
        List<String> errorLog = new ArrayList<>();
        JsymbolicData data = processor.extractReturnAndSaveFeaturesFromFile(saintSaensTest, errorLog);
        DataSet[] dataSets = data.getFeatureValuesAndDefinitions().getFeatureVectors();
        DataSet dataSet = dataSets[0].sub_sets[0];
        assertEquals("Beat Histogram", dataSet.feature_names[0]);
        assertEquals("Duration", dataSet.feature_names[1]);
        assertEquals("Acoustic Guitar Prevalence", dataSet.feature_names[2]);

        //Do it a 2nd time to test 1 processor can work on more than one file
        JsymbolicData data2 = processor.extractReturnAndSaveFeaturesFromFile(saintSaensTest, errorLog);
        DataSet[] dataSets2 = data2.getFeatureValuesAndDefinitions().getFeatureVectors();
        DataSet dataSet2 = dataSets2[0].sub_sets[0];
        assertEquals("Beat Histogram", dataSet.feature_names[0]);
        assertEquals("Duration", dataSet.feature_names[1]);
        assertEquals("Acoustic Guitar Prevalence", dataSet.feature_names[2]);

        //Test that CSV and ARFF conversion is successful
        JsymbolicData convertData = processorConvert.extractReturnAndSaveFeaturesFromFile(saintSaensTest, errorLog);
        String csvFileName = valuesPath.replaceAll(".xml",".csv");
        String arffFileName = valuesPath.replaceAll(".xml",".arff");
        File csvFile = new File(csvFileName);
        File arffFile = new File(arffFileName);
        assertEquals(csvFile, convertData.getSavedCsvFile());
        assertEquals(arffFile, convertData.getSavedWekaArffFile());

        //Remove the new resources
        if(csvFile.exists()) Files.delete(Paths.get(csvFileName));
        if(arffFile.exists()) Files.delete(Paths.get(arffFileName));

        //Check for non existing files
        exception.expect(Exception.class);
        processor.extractReturnAndSaveFeaturesFromFile(new File("dne"), errorLog);
        exception.expect(Exception.class);
        processor.extractReturnAndSaveFeaturesFromFile(null, errorLog);
    }

    @Test
    public void getJsymbolicDataDirectory() throws Exception {
        File dir = new File("./test/jsymbolic2/api/resources/");
        List<String> errorLog = new ArrayList<>();
        Map<File, JsymbolicData> dirMap = processorConfig.extractAndReturnSavedFeaturesFromDirectory(dir, errorLog);
        File[] allFiles = dirMap.keySet().toArray(new File[1]);
        File saintSaens = allFiles[0];
        File chopin = allFiles[1];
        JsymbolicData saintSaensData = dirMap.get(saintSaens);
        JsymbolicData chopinData = dirMap.get(chopin);

        DataSet[] dataSetsSaint = chopinData.getFeatureValuesAndDefinitions().getFeatureVectors();
        DataSet dataSetSaint = dataSetsSaint[0].sub_sets[0];
        assertEquals("Beat Histogram", dataSetSaint.feature_names[0]);
        assertEquals("Duration", dataSetSaint.feature_names[1]);
        assertEquals("Acoustic Guitar Prevalence", dataSetSaint.feature_names[2]);

        DataSet[] dataSetsChopin = saintSaensData.getFeatureValuesAndDefinitions().getFeatureVectors();
        DataSet dataSetChopin = dataSetsChopin[0].sub_sets[0];
        assertEquals("Beat Histogram", dataSetChopin.feature_names[0]);
        assertEquals("Duration", dataSetChopin.feature_names[1]);
        assertEquals("Acoustic Guitar Prevalence", dataSetChopin.feature_names[2]);

        //Check for non existing files
        exception.expect(Exception.class);
        processorConfig.extractReturnAndSaveFeaturesFromFile(new File("dne"), errorLog);
        exception.expect(Exception.class);
        processorConfig.extractReturnAndSaveFeaturesFromFile(null, errorLog);
    }

}