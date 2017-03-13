/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsymbolic2.processing;

import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit Tests for the AceXmlConverter class.
 * 
 * @author Tristano Tenaglia
 */
public class AceToArffAndCsvTest {
    
    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

    @After
    public void cleanupTempFolder() {
        tempFolder.delete();
    }
    
    /**
     * Test of convertACEXMLtoARFF method, of class AceXmlConverter.
     * @throws java.io.IOException test
     */
    @Test
    public void testConvertACEXMLtoARFF() 
            throws Exception {
        String ACEname = "./test/jsymbolic2/processing/resources/feature_values.xml";
        File testACE = new File(ACEname);
        String ARFFname = "./test/jsymbolic2/processing/resources/feature_values.arff";
        File testARFF = new File(ARFFname);
        if(Files.exists(testARFF.toPath())) {
            Files.delete(testARFF.toPath());
        }
        AceXmlConverter.saveAsArffOrCsvFiles(ACEname, null, true, false, System.out);
        testARFF = new File(ARFFname);
        File actualARFF = new File(ARFFname);
        try(Scanner inACE = new Scanner(testARFF);
            Scanner inARFF = new Scanner(actualARFF))
        {
            String nextACELine;
            String nextARFFLine;
            while(inACE.hasNextLine()) {
                nextACELine = inACE.nextLine();
                nextARFFLine = inARFF.nextLine();
                assertEquals(nextACELine,nextARFFLine);
            }
        }
    }

    /**
     * Test of convertARFFtoCSV method, of class AceXmlConverter.
     * @throws java.lang.Exception test
     */
    @Test
    public void testConvertARFFtoCSV() 
            throws Exception {
        String CSVname = "./test/jsymbolic2/processing/resources/feature_values_noxml.csv";
        File testCSV = new File(CSVname);
        String ACEname = "./test/jsymbolic2/processing/resources/feature_values.xml";
        String newCSVname = "./test/jsymbolic2/processing/resources/feature_values.csv";
        File tempCSV = new File(newCSVname);
        if(Files.exists(tempCSV.toPath())) {
            Files.delete(tempCSV.toPath());
        }
        AceXmlConverter.saveAsArffOrCsvFiles(ACEname, null, false, true, System.out);
        tempCSV = new File(newCSVname);
        try(Scanner inACE = new Scanner(testCSV);
            Scanner inCSV = new Scanner(tempCSV))
        {
            String nextACELine;
            String nextCSVLine;
            while(inACE.hasNextLine()) {
                nextACELine = inACE.nextLine();
                nextCSVLine = inCSV.nextLine();
                assertEquals(nextACELine,nextCSVLine);
            }
        }
    }

    /**
     * Test of convertACEtoCSVwithARFF method, of class AceXmlConverter.
     * @throws Exception test
     */
    @Test
    public void testConvertACEtoCSVwithARFF() throws Exception {
        String CSVname = "./test/jsymbolic2/processing/resources/feature_values_1.csv";
        File testCSV = new File(CSVname);
        String ACEname = "./test/jsymbolic2/processing/resources/feature_values.xml";
        String newCSVname = "./test/jsymbolic2/processing/resources/feature_values.csv";
        File tempCSV = new File(newCSVname);
        if(Files.exists(tempCSV.toPath())) {
            Files.delete(tempCSV.toPath());
        }
        AceXmlConverter.saveAsArffOrCsvFiles(ACEname, null, true, true, System.out);
        tempCSV = new File(newCSVname);
        
        try(Scanner inACE = new Scanner(testCSV);
            Scanner inCSV = new Scanner(tempCSV))
        {
            String nextACELine;
            String nextCSVLine;
            while(inACE.hasNextLine()) {
                nextACELine = inACE.nextLine();
                nextCSVLine = inCSV.nextLine();
                assertEquals(nextACELine,nextCSVLine);
            }
        }
    }

    /**
     * Test of saveAsArffOrCsvFiles method, of class AceXmlConverter.
     * @throws java.lang.Exception Test
     */
    @Test
    public void testOutputArffandCsvFormats() 
            throws Exception {
        //Check for xml extension
        String ACEname = "./test/jsymbolic2/processing/resources/feature_values.xml";

        String ARFFname = ACEname.replace(".xml", ".arff");
        File arffFile = new File(ARFFname);
        if(arffFile.exists()) {
            Files.delete(arffFile.toPath());
        }

        String CSVname = ACEname.replace(".xml", ".csv");
        File csvFile = new File(CSVname);
        if(csvFile.exists()) {
            Files.delete(csvFile.toPath());
        }

        AceXmlConverter.saveAsArffOrCsvFiles(ACEname, null, true, true, System.out);
        assertTrue(arffFile.exists());
        assertTrue(csvFile.exists());
        
        //Check for no xml extension
        String noXML = "./test/jsymbolic2/processing/resources/feature_values_noxml";

        String ARFFnoXMLName = noXML + ".arff";
        File arffnoXMLFile = new File(ARFFnoXMLName);
        if(arffnoXMLFile.exists()) {
            Files.delete(arffnoXMLFile.toPath());
        }

        String CSVnoXMLname = noXML + ".csv";
        File csvnoXMLFile = new File(CSVnoXMLname);
        if(csvnoXMLFile.exists()) {
            Files.delete(csvnoXMLFile.toPath());
        }

        AceXmlConverter.saveAsArffOrCsvFiles(noXML, null, true, true, System.out);
        assertTrue(arffnoXMLFile.exists());
        assertTrue(csvnoXMLFile.exists());
    }
    
}
