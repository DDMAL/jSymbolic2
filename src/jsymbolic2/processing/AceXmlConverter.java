package jsymbolic2.processing;

import java.io.File;
import java.io.PrintStream;
import ace.datatypes.DataBoard;
import ace.datatypes.DataSet;
import ace.datatypes.SegmentedClassification;

/**
 * Class relating to methods and objects for converting ACE XML Feature Value files to other formats, and to
 * generating an ACE XML Feature Values file.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public final class AceXmlConverter
{
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Convert the given ACE XML Feature Values file to a Weka ARFF and/or to a CSV file and, possibly, to
	 * also save an ACE XML class labels file (assigning each instance a model class consisting of the name of
	 * the directory holding it).
	 *
	 * <p>The CSV and ARFF files will have the same file names as the ACE XML Feature Values file, but with an
	 * appropriately modified extension. The ACE XML class labels file will also have the same name, but
	 * with_ClassLabels appended before the extension.
	 *
	 * <p>This method does nothing if ace_xml_feature_values_file_path is null or empty, or if both save_ARFF 
	 * and save_CSV are false (null is returned in any of these cases).
	 *
	 * <p> The ARFF file will have a relation name of Converted_from_ACE_XML.</p>
	 *
	 * <p> The first row of the CSV file will list the feature code and names (multi-dimensional features will
	 * have a feature index number appended to the end of their feature name). Each other row will consist of,
	 * first, the instance identifier, followed by the value of each feature. The first column will consist
	 * of instance identifiers, and the final (if this option is selected) will consist of the instance's
	 * class label.</p>
	 *
	 * @param ace_xml_feature_values_file_path			The path of the ACE XML Feature Values file to
	 *													convert.
	 * @param ace_xml_feature_definitions_file_path		The path of the ACE XML Feature Definitions file 
	 *													corresponding to the ACE XML Feature Values file at
	 *													ace_xml_feature_values_file_path.
	 * @param save_ARFF									True if a Weka ARFF file is to be generated and saved.
	 * @param save_CSV									True if a Weka ARFF file is to be generated and saved.
	 * @param save_model_class_labels					True if model class labels are to be saved. If this is
	 *													true, then every instance is auto-assigned a class
	 *													label consisting of the name of its parent directory,
	 *													and this information is saved in an ACE XML class 
	 *													labels file, as well as in columns of the CSV and ARFF
	 *													files (if those formats are set to be saved). If this
	 *													parameter is set to false, then no ACE XML class 
	 *													labels file will be generated, and the class label
	 *													column will be omitted from any CSV or ARFF files
	 *													saved.
	 * @param url_encode_text							Whether or not to URL-encode (using UTF-8) feature
	 *													names, instance identifiers and class labels in any
	 *													CSV or ARFF files to be generated.
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
														   boolean save_model_class_labels,
														   boolean url_encode_text,
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

			// Parse the ACE XML feature values file and (if appropriate) generate class labels and save an
			// ACE XML class labels file holding model classifications
			DataBoard instance_data = null;
			try
			{
				// Parse the ACE XML feature values file
				String[] input_files = { ace_xml_feature_values_file_path };
				instance_data = new ace.datatypes.DataBoard(null, ace_xml_feature_definitions_file_path, input_files, null);

				// Save the ACE XML class labels file, and update instance_data to hold class labels
				if (save_model_class_labels)
				{
					try	{ instance_data = generateSaveAndNoteClassLabels(instance_data, ace_xml_feature_values_file_path, ace_xml_feature_definitions_file_path); }
					catch (Exception e) { throw new Exception("Could not succesfully generate class labels for instances specified in the file: " + ace_xml_feature_values_file_path + "."); }
				}
			}
			catch (Exception e)
			{
				throw new Exception("Could not succesfully parse the file specified at that path: " + ace_xml_feature_values_file_path + ". Perhaps this file does not exist, or is not a valid ACE XML Feature Values file?"); 
			}
			
			// Convert data to CSV format and save the CSV file
			if (save_CSV)
			{
				try
				{
					UserFeedbackGenerator.printGeneratingCsvFile(status_print_stream, output_csv_file_path);
					instance_data.saveToCSV(new File(output_csv_file_path), true, true, save_model_class_labels, true, true, url_encode_text, url_encode_text, url_encode_text);
				}
				catch (Exception e)
				{
					throw new Exception("Could not succesfully save the CSV file to the path:" + output_csv_file_path + ". Perhaps you do not have write permission?");
				}
			}

			// Convert data to Weka ARFF format and save the ARFF file
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

			// Return the paths of the saved files (excluding the ACE XML class labels file)
			return new AceConversionPaths( ace_xml_feature_values_file_path,
			                               ace_xml_feature_definitions_file_path,
			                               output_arff_file_path,
			                               output_csv_file_path );
		}
		else return null;
	}

	
	/* PRIVATE STATIC METHODS *******************************************************************************/
	
	
	/**
	 * Take the given instance_data and save an ACE XML class labels file holding the instance identifier
	 * (which should be a file path) of each instance in instance_data, and assigning each instance a class
	 * label consisting of the name of the parent directory of the instance (based on the instance
	 * identifier). Then return a new DataBoard holding a copy of the combined original instance_data with
	 * class labels added.
	 * 
	 * @param instance_data								Data already parsed from the two specified ACE XML 
	 *													files (consisting of instance identifiers and 
	 *													feature values, but not class labels.
	 * @param ace_xml_feature_values_file_path			The path of the ACE XML Feature Values file to
	 *													convert. instance_data is based on this.
	 * @param ace_xml_feature_definitions_file_path		The path of the ACE XML Feature Definitions file 
	 *													corresponding to the ACE XML Feature Values file at
	 *													ace_xml_feature_values_file_path. instance_data is
	 *													based on this.
	 * @return											A new DataBoard holding a copy of the combined 
	 *													original instance_data with class labels added.
	 * @throws Exception								Throws an Exception if a problem occurs.
	 */
	private static DataBoard generateSaveAndNoteClassLabels( DataBoard instance_data,
	                                                         String ace_xml_feature_values_file_path,
	                                                         String ace_xml_feature_definitions_file_path )
		throws Exception
	{
		// Parse instances, note identifiers for each one (which should consist of file paths), assign
		// class labels corresponding to the parent directory of each instance
		DataSet[] each_instance = instance_data.getFeatureVectors();
		SegmentedClassification[] each_classification = new SegmentedClassification[each_instance.length];
		for (int inst = 0; inst < each_instance.length; inst++)
		{
			String this_instance_identifier = each_instance[inst].identifier;
			String parent_directory_name = (new File(this_instance_identifier)).getParentFile().getName();
			String[] this_class_label = {parent_directory_name};
			each_classification[inst] = new SegmentedClassification(this_instance_identifier, Double.NaN, Double.NaN, this_class_label, null, null, null);
		}

		// Set the file path of the ACE XML class labels file
		String classifications_file_path = FeatureExtractionJobProcessor.getMatchingClassLabelsXmlSavePath(ace_xml_feature_values_file_path);
		File classifications_file = new File(classifications_file_path);

		// Save the ACE XML class labels classifications file
		SegmentedClassification.saveClassifications(each_classification, classifications_file, "");

		// Reparse all three ACE XML files, store the results (now containing class labels) in the returned
		// DataBoard
		String[] input_files = { ace_xml_feature_values_file_path };
		return new ace.datatypes.DataBoard(null, ace_xml_feature_definitions_file_path, input_files, classifications_file_path);
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