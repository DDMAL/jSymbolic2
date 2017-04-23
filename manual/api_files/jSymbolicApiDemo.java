import jsymbolic2.api.JsymbolicProcessor;
import ace.datatypes.DataSet;

/**
 * A simple demo of how to access jSymbolic functionality programmatically using its API.
 *
 * @author Cory McKay
 */
public class jSymbolicApiDemo
{
	/**
	 * Extracts features from the files in the ./MySymbolicMusicFiles/ directory, saves the
	 * extracted features in various file formats in the ./ directory, and prints the extracted
	 * feature values to standard out.
	 *
	 * @param args Command line input parameter arguments.
	 */
	public static void main(String[] args)
	{
		// Define feature extraction settings
		String feature_values_save_path = "./ExtractedFeatureValues.xml";
		String feature_definitions_save_path = "./FeatureDefinitions.xml";
		boolean save_arff_file = true;
		boolean save_csv_file = true;
		boolean save_features_for_overall_pieces = true;
		boolean save_features_for_each_window = false;
		double analysis_window_size = 0.0;
		double analysis_window_overlap = 0.0;
		java.io.PrintStream status_print_stream = System.out;
		java.io.PrintStream error_print_stream = System.err;		

		// Define feature extraction targets
		String path_of_file_or_folder_to_parse = "./MySymbolicMusicFiles/";
		
		try
		{
			// Set extraction settings by instantiating JsymbolicProcessor object
			// Note that this particular constructor selects only the default jSymbolic features for extraction
			JsymbolicProcessor proc = new JsymbolicProcessor( feature_values_save_path,
				feature_definitions_save_path,
				save_arff_file,
				save_csv_file,
				save_features_for_overall_pieces,
				save_features_for_each_window,
				analysis_window_size,
				analysis_window_overlap,
				status_print_stream,
				error_print_stream );
		
			// Extract and save features in files
			proc.extractAndSaveFeaturesFromFileOrDirectory(path_of_file_or_folder_to_parse);
			
			// Access the saved features programmatically
			DataSet[] feature_data = proc.getExtractedFeatureValues();
			
			// Print out the feature data to standard out for each instance (i.e. musical piece) one-by-one
			// Note that this formulation assumes that windowed feature extraction was not used
			for (int instance = 0; instance < feature_data.length; instance++)
			{
				// Identify the instance
				System.out.println("INSTANCE ID: " + feature_data[instance].identifier);
				
				// The extracted feature values for this instance
				double[][] feature_values = feature_data[instance].feature_values;

				// Go through the features extracted from this instance one by one
				for (int feature = 0; feature < feature_values.length; feature++)
				{
					// Identify this feature
					System.out.print(feature_data[instance].feature_names[feature] + ":");

					// Print out each dimension of this feature, one dimension at a time
					for (int dimension = 0; dimension < feature_values[feature].length; dimension++)
						System.out.print(" " + feature_values[feature][dimension]);
					
					System.out.print("\n");
				}
				System.out.print("\n");
			}
		}
		catch (Exception e)
		{
			// Print a report of any errors to the defined error stream
			e.printStackTrace(error_print_stream);
		}
	}
}