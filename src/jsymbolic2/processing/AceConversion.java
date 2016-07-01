package jsymbolic2.processing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * General conversion class from ACE XML to ARFF or CSV that should only be called
 * statically. This class will convert ACE XML to ARFF and then use the ARFF
 * file to obtain the CSV file. Therefore the ACE XML file is always built and
 * if the CSV file is required, the ARFF file is also built as an intermediate.
 * @author Tristano Tenaglia
 */
public final class AceConversion {

    /**
     * Parse the specified <i>ace_xml_input_file_path</i> ACE XML Feature Value
     * file and convert it into a new Weka ARFF file at the path specified by
     * <i>weka_arff_output_file_path</i>. Note that any existing file at the
     * output path is overwritten.
     *
     * <p>
     * The instance identifiers stored in the ACE XML file are printed to
     * standard out, as these cannot be stored in a meaningful way in Weka ARFF
     * files. Also, the Weka relation name is defaulted to
     * "Converted_from_ACE_XML".
     *
     * @param ace_xml_input_file_path	The path of the input ACE XML file
     * containing feature values that is to be parsed and converted.
     * @param weka_arff_output_file_path	The path of the new Weka ARFF file to
     * save to.
     * @throws java.lang.Exception Thrown if the input file could not be successfully parsed and converted.
     */
    public static void convertACEXMLtoARFF(String ace_xml_input_file_path,
            String weka_arff_output_file_path) throws Exception {
        // Stores parsed contents of the ACE XML file
        ace.datatypes.DataBoard feature_values = null;

        // Parse the ACE XML file
        try {
            String[] input_files = new String[1];
            input_files[0] = ace_xml_input_file_path;
            feature_values = new ace.datatypes.DataBoard(null, null, input_files, null);
        } catch (Exception e) {
            System.err.println("ERROR: Could not succesfully parse the file specified at that path: " + ace_xml_input_file_path + ". Perhaps this file does not exist, or is not a valid ACE XML Feature Values file?");
            throw e;
        }

        // Convert and save to Weka ARFF and output the instance identifiers to standard out
        try {
            File output_file = new File(weka_arff_output_file_path);
            String relation_name = "Converted_from_ACE_XML";
            String[] instance_identififiers = feature_values.saveToARFF(relation_name, output_file, true, true);

            if (instance_identififiers != null) {
                for (int i = 0; i < instance_identififiers.length; i++) {
                    System.out.println(instance_identififiers[i]);
                }
            }
        } catch (Exception e) {
            //Can use this printStackTrace to debug feature_values.saveToARFF()
            //e.printStackTrace();
            throw new Exception("ERROR: Could not succesfully save the Weka ARFF file to the path:" + weka_arff_output_file_path + ". Perhaps you do not have write permission to this path?");
        }
    }

    /**
     * Convert ARFF file directly to CSV file.
     *
     * @param weka_arff_input_file_path input arff file path
     * @param csv_output_file_path output csv file path
     * @throws java.lang.Exception Thrown if the input file could not be successfully parsed and converted.
     */
    public static void convertARFFtoCSV(String weka_arff_input_file_path,
            String csv_output_file_path)
            throws Exception {
        File weka = new File(weka_arff_input_file_path);
        File csv = new File(csv_output_file_path);

        try (Scanner ARFFin = new Scanner(weka);
                PrintWriter CSVout = new PrintWriter(csv)) 
        {
            //Print feature names
            String featureName = ARFFin.nextLine();
            while (!featureName.contains("@data")) {
                featureName = processFeatureName(featureName);
                CSVout.print(featureName);
                featureName = ARFFin.nextLine();
            }
            CSVout.println();

            //Print features
            String nextARFFLine;
            while (ARFFin.hasNextLine()) {
                nextARFFLine = ARFFin.nextLine();
                CSVout.println(nextARFFLine);
            }
        } catch (IOException ioe) {
            throw new Exception("Error : Could not succesfully convert ARFF file " + weka_arff_input_file_path + " to csv " + csv_output_file_path);
        }
    }
    
    /**
     * Process the feature name appropriately so that it is only a name.
     * @param featureName the feature name to be processed
     * @return process feature name if contains @attribute or else return
     *         an empty string
     */
    public static String processFeatureName(String featureName) {
        if (featureName.contains("@attribute")) {
            featureName = featureName.replaceAll("@attribute ", "");
            featureName = featureName.replaceAll(" numeric", "");
            return featureName + ",";
        }
        return ""; //nothing will get printed
    }

    /**
     * Builds an CSV file using an ARFF file intermediate but then removes the
     * ARFF file for convenience.
     *
     * @param ace_xml_input_file_path input ace file path
     * @param csv_output_file_path output csv file path
     * @throws java.lang.Exception Thrown if the input file could not be successfully parsed and converted.
     */
    public static void convertACEtoCSVwithARFF(String ace_xml_input_file_path,
            String csv_output_file_path)
            throws Exception {
        String tempArffFile = System.currentTimeMillis() + "-987456123temp.arff";
        convertACEXMLtoARFF(ace_xml_input_file_path, tempArffFile);
        convertARFFtoCSV(tempArffFile, csv_output_file_path);

        try {
            Files.delete(Paths.get(tempArffFile));
        } catch (IOException ioe) {
            throw new Exception("Error : Could not succesfully convert ace " + ace_xml_input_file_path + " to csv " + csv_output_file_path);
        }
    }

    /**
     * Output file formats depending on -csv and -arff switches given.
     *
     * @param ace_xml_input_file input ace xml file path
     * @param ARFFcheck true if we need to output arff
     * @param CSVcheck true if we need to output csv
     * @return A conversion paths object that contains all paths to appropriate jSymbolic conversions.
     * @throws java.lang.Exception Thrown if the input file could not be successfully parsed and converted.
     */
    public static AceConversionPaths outputArffandCsvFormats(String ace_xml_input_file,
            boolean ARFFcheck,
            boolean CSVcheck)
            throws Exception {
        String fileInput = ace_xml_input_file;
        String arff;
        String csv;
        if(fileInput.endsWith(".xml")) {
            arff = ace_xml_input_file.replaceAll(".xml", ".arff");
            csv = ace_xml_input_file.replaceAll(".xml", ".csv");
        } else {
            arff = ace_xml_input_file + ".arff";
            csv = ace_xml_input_file + ".csv";
        }

        if (ARFFcheck && !CSVcheck) {
            AceConversion.convertACEXMLtoARFF(fileInput, arff);
        } else if (!ARFFcheck && CSVcheck) {
            AceConversion.convertACEtoCSVwithARFF(fileInput, csv);
        } else if (ARFFcheck && CSVcheck) {
            AceConversion.convertACEXMLtoARFF(fileInput, arff);
            AceConversion.convertARFFtoCSV(arff, csv);
        }
        return new AceConversionPaths(arff, csv);
    }
}
