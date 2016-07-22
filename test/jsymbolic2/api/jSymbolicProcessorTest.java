package jsymbolic2.api;

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
    private jSymbolicProcessor processor;
    private jSymbolicProcessor processorConfig;
    private jSymbolicProcessor processorConvert;
    private String valuesPath = "./test/jsymbolic2/api/resources/feature_values.xml";
    private String definitionsPath = "./test/jsymbolic2/api/resources/feature_definitions.xml";

    @Before
    public void setUp() throws Exception {
        List<String> featureNames = Arrays.asList("Beat Histogram", "Acoustic Guitar Fraction", "Duration");
        processor = new jSymbolicProcessor(10, 0.1, featureNames, true, false, false, false, valuesPath, definitionsPath);
        processorConfig = new jSymbolicProcessor("./test/jsymbolic2/api/resources/sampleConfiguration.txt");
        processorConvert = new jSymbolicProcessor("./test/jsymbolic2/api/resources/sampleConfigConvert.txt");
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
        jSymbolicData data = processor.computeJsymbolicData(saintSaensTest, errorLog);
        DataSet[] dataSets = data.getFeatureValuesDataBoard().getFeatureVectors();
        DataSet dataSet = dataSets[0].sub_sets[0];
        assertEquals("Duration", dataSet.feature_names[0]);
        assertEquals("Acoustic Guitar Fraction", dataSet.feature_names[1]);
        assertEquals("Beat Histogram", dataSet.feature_names[2]);

        //Do it a 2nd time to test 1 processor can work on more than one file
        jSymbolicData data2 = processor.computeJsymbolicData(saintSaensTest, errorLog);
        DataSet[] dataSets2 = data2.getFeatureValuesDataBoard().getFeatureVectors();
        DataSet dataSet2 = dataSets2[0].sub_sets[0];
        assertEquals("Duration", dataSet2.feature_names[0]);
        assertEquals("Acoustic Guitar Fraction", dataSet2.feature_names[1]);
        assertEquals("Beat Histogram", dataSet2.feature_names[2]);

        //Test that CSV and ARFF conversion is successful
        jSymbolicData convertData = processorConvert.computeJsymbolicData(saintSaensTest, errorLog);
        String csvFileName = valuesPath.replaceAll(".xml",".csv");
        String arffFileName = valuesPath.replaceAll(".xml",".arff");
        File csvFile = new File(csvFileName);
        File arffFile = new File(arffFileName);
        assertEquals(csvFile, convertData.getCsvArffFile());
        assertEquals(arffFile, convertData.getArffFile());

        //Remove the new resources
        if(csvFile.exists()) Files.delete(Paths.get(csvFileName));
        if(arffFile.exists()) Files.delete(Paths.get(arffFileName));

        //Check for non existing files
        exception.expect(Exception.class);
        processor.computeJsymbolicData(new File("dne"), errorLog);
        exception.expect(Exception.class);
        processor.computeJsymbolicData(null, errorLog);
    }

    @Test
    public void getJsymbolicDataDirectory() throws Exception {
        File dir = new File("./test/jsymbolic2/api/resources/");
        List<String> errorLog = new ArrayList<>();
        Map<File, jSymbolicData> dirMap = processorConfig.computeJsymbolicDataDirectory(dir, errorLog);
        File[] allFiles = dirMap.keySet().toArray(new File[1]);
        File saintSaens = allFiles[0];
        File chopin = allFiles[1];
        jSymbolicData saintSaensData = dirMap.get(saintSaens);
        jSymbolicData chopinData = dirMap.get(chopin);

        DataSet[] dataSetsSaint = chopinData.getFeatureValuesDataBoard().getFeatureVectors();
        DataSet dataSetSaint = dataSetsSaint[0].sub_sets[0];
        assertEquals("Duration", dataSetSaint.feature_names[0]);
        assertEquals("Acoustic Guitar Fraction", dataSetSaint.feature_names[1]);
        assertEquals("Beat Histogram", dataSetSaint.feature_names[2]);

        DataSet[] dataSetsChopin = saintSaensData.getFeatureValuesDataBoard().getFeatureVectors();
        DataSet dataSetChopin = dataSetsChopin[0].sub_sets[0];
        assertEquals("Duration", dataSetChopin.feature_names[0]);
        assertEquals("Acoustic Guitar Fraction", dataSetChopin.feature_names[1]);
        assertEquals("Beat Histogram", dataSetChopin.feature_names[2]);

        //Check for non existing files
        exception.expect(Exception.class);
        processorConfig.computeJsymbolicData(new File("dne"), errorLog);
        exception.expect(Exception.class);
        processorConfig.computeJsymbolicData(null, errorLog);
    }

}