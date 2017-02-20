package jsymbolic2.processing;

import java.io.File;
import java.io.PrintStream;

/**
 * Class relating to methods and objects for converting ACE XML Feature Value files to other formats.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public final class AceXmlConverter
{
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Convert the given ACE XML Feature Values file to a Weka ARFF and/or to a CSV file. Each file will have
	 * the same file name as the ACE XML Feature Values file, but with an appropriately modified extension.
	 * Does nothing if ace_xml_feature_values_file_path is null or empty, or if both save_ARFF and save_CSV
	 * are false (null is returned in any of these cases).
	 *
	 * <p>The ARFF file will have a relation name of Converted_from_ACE_XML.</p>
	 *
	 * <p> The first row of the CSV file will list the feature names (multi-dimensional features will have a
	 * feature index number appended to the end of their feature name). Each other row will consist of, first,
	 * the instance identifier, followed by the value of each feature.</p>
	 *
	 * @param ace_xml_feature_values_file_path			The path of the ACE XML Feature Values file to
	 *													convert.
	 * @param ace_xml_feature_definitions_file_path		The path of the ACE XML Feature Definitions file 
	 *													corresponding to the ACE XML Feature Values file at
	 *													ace_xml_feature_values_file_path.
	 * @param save_ARFF									True if a Weka ARFF file is to be generated and saved.
	 * @param save_CSV									True if a Weka ARFF file is to be generated and saved.
	 * @param status_print_stream						Stream to print progress messages to.
	 * @return											An object containing the paths of the saved ARFF
	 *													and/or CSV files. Returns null if 
	 *													ace_xml_feature_values_file_path is null or empty, or 
	 *													if both save_ARFF and save_CSV are false.
	 * @throws Exception								An informative exception is thrown if the feature
	 *													values file could not be successfully parsed and
	 *													converted.
	 */
	public static AceConversionPaths saveAsArffOrCsvFiles( String ace_xml_feature_values_file_path,
	                                                       String ace_xml_feature_definitions_file_path,
	                                                       boolean save_ARFF,
	                                                       boolean save_CSV,
														   PrintStream status_print_stream )
		throws Exception
	{
		if ( (save_ARFF || save_CSV) &&
		     ace_xml_feature_values_file_path != null &&
		     !ace_xml_feature_values_file_path.isEmpty() )
		{
			// Set output file paths. Make sure extensions are appropriate.
			String output_arff_file_path;
			String output_csv_file_path;
			if (ace_xml_feature_values_file_path.endsWith(".xml"))
			{
				output_arff_file_path = ace_xml_feature_values_file_path.replaceAll(".xml", ".arff");
				output_csv_file_path = ace_xml_feature_values_file_path.replaceAll(".xml", ".csv");
			}
			else
			{
				output_arff_file_path = ace_xml_feature_values_file_path + ".arff";
				output_csv_file_path = ace_xml_feature_values_file_path + ".csv";
			}

			// Parse the ACE XML file
			ace.datatypes.DataBoard instance_data = null;
			try
			{
				String[] input_files = { ace_xml_feature_values_file_path };
				instance_data = new ace.datatypes.DataBoard(null, ace_xml_feature_definitions_file_path, input_files, null);
			}
			catch (Exception e)
			{
				throw new Exception("Could not succesfully parse the file specified at that path: " + ace_xml_feature_values_file_path + ". Perhaps this file does not exist, or is not a valid ACE XML Feature Values file?"); 
			}

			// Convert feature values to Weka ARFF format and save the ARFF file
			if (save_ARFF)
			{
				try
				{
					UserFeedbackGenerator.printGeneratingArffFile(status_print_stream, output_arff_file_path);
					String relation_name = "Converted_from_ACE_XML";
					instance_data.saveToARFF(relation_name, new File(output_arff_file_path), true, true);
				} 
				catch (Exception e)
				{
					throw new Exception("Could not succesfully save the Weka ARFF file to the path:" + output_arff_file_path + ". Perhaps you do not have write permission?");
				}
			}

			// Convert feature values to CSV format and save the CSV file
			if (save_CSV)
			{
				try
				{
					UserFeedbackGenerator.printGeneratingCsvFile(status_print_stream, output_csv_file_path);
					instance_data.saveToCSV(new File(output_csv_file_path), true, true, true, true, true);
				}
				catch (Exception e)
				{
					throw new Exception("Could not succesfully save the CSV file to the path:" + output_csv_file_path + ". Perhaps you do not have write permission?");
				}
			}

			// Return the paths of the saved files
			return new AceConversionPaths( ace_xml_feature_values_file_path,
			                               ace_xml_feature_definitions_file_path,
			                               output_arff_file_path,
			                               output_csv_file_path );
		}
		else return null;
	}

	
	/* INTERNAL CLASS ***************************************************************************************/
	
	
	/**
	 * Objects of this class hold the paths of an associated ACE XML Feature Values file, ACE XML Feature
	 * Definitions file, Weka ARFF file and CSV file. Typically the ARFF and CSV files were converted from
	 * the ACE XML file.
	 */
	public static class AceConversionPaths
	{
		/**
		 * Fields holding file paths.
		 */
		private final String ace_xml_feature_values_file_path;
		private final String ace_xml_feature_definitions_file_path;
		private final String arff_file_path;
		private final String csv_arff_file_path;

		
		/**
		 * Instantiate an object of this class. Field values cannot be changed after instantiation.
		 * 
		 * @param ace_xml_feature_values_file_path		The path of the associated ACE XML Feature Values 
		 *												file.
		 * @param ace_xml_feature_definitions_file_path	The path of the associated ACE XML Feature Definitions 
		 *												file.
		 * @param arff_file_path						The path of the associated Weka ARFF file.
		 * @param csv_arff_file_path 					The path of the associated CSV file.
		 */
		public AceConversionPaths( String ace_xml_feature_values_file_path,
		                           String ace_xml_feature_definitions_file_path,
		                           String arff_file_path,
		                           String csv_arff_file_path )
		{
			this.ace_xml_feature_values_file_path = ace_xml_feature_values_file_path;
			this.ace_xml_feature_definitions_file_path = ace_xml_feature_definitions_file_path;
			this.arff_file_path = arff_file_path;
			this.csv_arff_file_path = csv_arff_file_path;
		}

		
		/**
		 * @return	The path of the associated ACE XML Feature Values file.
		 */
		public String getAceXmlFeatureValuesFilePath()
		{
			return ace_xml_feature_values_file_path;
		}

		
		/**
		 * @return	The path of the associated ACE XML Feature Definitions file.
		 */
		public String getAceXmlFeatureDefinitionsFilePath()
		{
			return ace_xml_feature_definitions_file_path;
		}
		
		
		/**
		 * @return	The path of the associated Weka ARFF file.
		 */
		public String getArffFilePath()
		{
			return arff_file_path;
		}

		
		/**
		 * @return	The path of the associated CSV file.
		 */
		public String getCsvFilePath()
		{
			return csv_arff_file_path;
		}
	}
}