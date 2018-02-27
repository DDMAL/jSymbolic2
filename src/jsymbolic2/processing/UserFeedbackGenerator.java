package jsymbolic2.processing;

import java.io.PrintStream;
import java.util.List;
import ace.datatypes.DataBoard;
import jsymbolic2.commandline.CommandLineUtilities;

/**
 * A holder class for static methods for printing various kinds of formatted messages during processing to
 * specified PrintStreams, primarily related to jSymbolic status updates and to errors.
 *
 * @author Cory McKay
 */
public class UserFeedbackGenerator
{	
	/**
	 * Generic method for printing the given message directly to stream, with no added new line at the end.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param message	The message to print.
	 */
	public static void simplePrint(PrintStream stream, String message)
	{
		stream.print(message);
	}

	
	/**
	 * Generic method for printing the given message directly to stream, with a new line added return at the
	 * end.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param message	The message to print.
	 */
	public static void simplePrintln(PrintStream stream, String message)
	{
		stream.println(message);
	}

	
	/**
	 * Print a formatted message to stream providing a warning of some kind.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param message	The message to print.
	 */	
	public static void printWarningMessage(PrintStream stream, String message)
	{
		stream.println("\nWARNING: " + message + "\n");
	}

	
	/**
	 * Print a formatted message to stream indicating an error of some kind.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param message	The message to print.
	 */		
	public static void printErrorMessage(PrintStream stream, String message)
	{
		stream.println("\nERROR: " + message + "\n");
	}
	
	
	/**
	 * Print a formatted message to stream indicating the message contained in the given Exception.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param exception	An exception whose message is to be printed to stream.
	 */	
	public static void printExceptionErrorMessage(PrintStream stream, Exception exception)
	{
		stream.println("\nERROR: " + exception.getMessage() + "\n");
		// exception.printStackTrace();
	}
	

	/**
	 * Print a formatted message to stream indicating that erroneous command line parameters were entered, and
	 * providing correct usage instructions. Terminate all processing once this is done.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param args		Arguments with which jSymbolic was run at the command line.
	 */
	public static void indicateIncorrectCommandLineArgumentsAndEndExecution( PrintStream stream,
	                                                                         String[] args )
	{
		// Output reason for termination
		stream.print("\nEXECUTION TERMINATED DUE TO INCORRECT COMMAND LINE ARGUMENTS: ");
		for (String arg : args)
			stream.print(arg + " ");
		stream.print("\n");

		// Output correct usage
		stream.println(CommandLineUtilities.getCommandLineCorrectUsage());

		// End execution
		System.exit(-1);
	}
	
	
	/**
	 * Print a formatted message to stream indicating that a configurations settings file is being loaded, and
	 * specify what the file's path is.
	 * 
	 * @param stream			The stream to print the message to.
	 * @param config_file_path	The path of the file being loaded.
	 */
	public static void printParsingConfigFileMessage(PrintStream stream, String config_file_path)
	{
		stream.println(">>> Loading configuration settings from: " + config_file_path + ".\n");
	}

	
	/**
	 * Print a formatted message to stream indicating that feature extraction is starting, and noting how
	 * many files will need to be processed.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param total		The number of input files that will need to be processed.
	 */
	public static void printFeatureExtractionStartingMessage(PrintStream stream, int total)
	{
		stream.println(">>> Extracting features from " + total + " input files, one file at at time:\n");
	}

	
	/**
	 * Print a formatted message to stream indicating that an input file is currently being pre-tested for
	 * validity.
	 * 
	 * @param stream		The stream to print the message to.
	 * @param identifier	The identifier of the input file being processed.
	 * @param current		The count of this file amongst the total number of files being processed.
	 * @param total			The total number of input files that are being processed.
	 */
	public static void printFeatureExtractionFileTestProgressMessage( PrintStream stream,
	                                                                  String identifier,
	                                                                  int current,
	                                                                  int total )
	{
		stream.println("\t>>> " + current + "/" + total + " Testing: " + identifier + "  . . .");
	}
	
	
	/**
	 * Print a formatted message to stream indicating that an input file is currently having features
	 * extracted from it.
	 * 
	 * @param stream		The stream to print the message to.
	 * @param identifier	The identifier of the input file being processed.
	 * @param current		The count of this file amongst the total number of files being processed.
	 * @param total			The total number of input files that are being processed.
	 */
	public static void printFeatureExtractionProgressMessage( PrintStream stream,
	                                                          String identifier,
	                                                          int current,
	                                                          int total )
	{
		stream.println("\t>>> " + current + "/" + total + " Extracting features: " + identifier + "  . . .");
	}
	
	
	/**
	 * Print a formatted message to stream indicating that an input file successfully had features extracted
	 * from it.
	 * 
	 * @param stream		The stream to print the message to.
	 * @param identifier	The identifier of the input file being processed.
	 * @param current		The count of this file amongst the total number of files being processed.
	 * @param total			The total number of input files that are being processed.
	 */
	public static void printFeatureExtractionDoneAFileProgressMessage( PrintStream stream,
	                                                                   String identifier,
	                                                                   int current,
	                                                                   int total )
	{
		stream.println("\t>>> " + current + "/" + total + " Successfully extracted: " + identifier + ".");
	}
	
	
	/**
	 * Print a Print a formatted message to stream indicating that feature extraction is complete. If
	 * saved_features_file_path is not null or empty, then also print how many files were successfully
	 * extracted out of the number attempted.
	 * 
	 * @param stream					The stream to write output to.	
	 * @param saved_features_file_path	The path of the ACE XML Feature Values file holding the extracted
	 *									features.
	 * @param total_attempted_files		The total number of files that were processed, successfully or not,
	 *									during feature extraction.
	 */
	public static void printFeatureExtractionCompleteMessage( PrintStream stream,
	                                                          String saved_features_file_path,
											                  int total_attempted_files )
	{
		stream.println(">>> Feature extraction complete.\n");
		if (saved_features_file_path != null && !saved_features_file_path.isEmpty())
		{
			try
			{
				String[] input_files = { saved_features_file_path };
				DataBoard instance_data = new DataBoard(null, null, input_files, null);
				int number_succesfully_extracted_files = instance_data.getNumOverall();
				stream.println("\t>>> Features succesfully extracted and saved from " + number_succesfully_extracted_files + " of " + total_attempted_files + " attempted files.\n");
			}
			catch (Exception e)
			{
				stream.println("\t>>> Features succesfully extracted and saved from 0 of " + total_attempted_files + " attempted files.");
				stream.println("\t>>> The saved ACE XML Feature Values file is empty as a result.\n");
			}
		}
	}
	
	
	/**
	 * Print a formatted message to stream indicating that an ACE XML feature definitions file is being saved.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param save_path The path of the file being saved.
	 */
	public static void printGeneratingAceXmlFeatureDefinitionsFile(PrintStream stream, String save_path)
	{
		stream.println(">>> Saving feature metadata as an ACE XML Feature Definitions file: " + save_path + ".\n");
	}
		
	
	/**
	 * Print a formatted message to stream indicating that an ACE XML feature values file is being saved.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param save_path The path of the file being saved.
	 */
	public static void printGeneratingAceXmlFeatureValuesFile(PrintStream stream, String save_path)
	{
		stream.println("\n>>> Saving extracted features as an ACE XML Feature Values file: " + save_path + ".\n");
	}

	
	/**
	 * Print a formatted message to stream indicating that Weka ARFF file is being saved.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param save_path The path of the file being saved.
	 */
	public static void printGeneratingArffFile(PrintStream stream, String save_path)
	{
		stream.println(">>> Saving extracted features as a Weka ARFF file: " + save_path + ".\n");
	}
	
	
	/**
	 * Print a formatted message to stream indicating that a CSV file is being saved.
	 * 
	 * @param stream	The stream to print the message to.
	 * @param save_path The path of the file being saved.
	 */

	public static void printGeneratingCsvFile(PrintStream stream, String save_path)
	{
		stream.println(">>> Saving extracted features as a CSV file: " + save_path + ".\n");
	}
	
	
	/**
	 * Print a formatted message to stream indicating that all processing is complete.
	 * 
	 * @param stream	The stream to print the message to.
	 */
	public static void printExecutionFinished(PrintStream stream)
	{
		stream.println(">>> ALL PROCESSING COMPLETE. EXECUTION ENDED.\n");
	}

	
	/**
	 * Print a formatted message to stream providing a summary of all errors that occurred during processing.
	 * This includes both a summary header indicating the total number of messages and numbered error-by-error
	 * listing.
	 * 
	 * @param stream		The stream to print the message to.
	 * @param error_log		The list of error messages to summarize.
	 * @param header_only	Only print the summary header if this is true. Also print each message 
	 *						individually if this is false.
	 */
	public static void printErrorSummary(PrintStream stream, List<String> error_log, boolean header_only)
	{
		if (error_log != null && !error_log.isEmpty())
		{
			if (header_only)
				stream.println(error_log.size() + " ERRORS ENCOUNTERED IN TOTAL.\n");
			else
			{
				stream.println("SUMMARY OF THE " + error_log.size() + " ERRORS ENCOUNTERED:\n");
				for (int i = 0; i < error_log.size(); i++)
					stream.println(">> " + (i+1) + "/" + error_log.size() + ": " + error_log.get(i));
			}
			stream.println();
		}
	}
}