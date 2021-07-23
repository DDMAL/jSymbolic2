package jsymbolic2;

import jsymbolic2.commandline.CommandLineSwitchEnum;

/**
 * The jSymbolic runnable class. See the README or manual for more details on jSymbolic.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class Main
{
	/* PUBLIC STATIC FINAL FIELDS ***************************************************************************/


	/**
	 * The name and version of this jSymbolic version.
	 */
	public static final String SOFTWARE_NAME_AND_VERSION = "jSymbolic 2.3";

	/**
	 * The string indicating licensing credit.
	 */
	public static final String YEAR_AND_LICENSING = "2021 (GNU GPL)";
	
	/**
	 * The name of this software's author.
	 */
	public static final String PRINCIPALAUTHOR_CREDIT = "Cory McKay";
	
	/**
	 * The credited institution.
	 */
	public static final String INSTITUTION_CREDIT = "CIRMMT / Marianopolis College / McGill University";
	
	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Runs the jSymbolic Feature Extractor. Operation will take place either in GUI or entirely via command
	 * line processing, depending on the provided command line arguments.
	 *
	 * @param args Command line input parameter arguments.
	 */
	public static void main(String[] args)
	{
		try { CommandLineSwitchEnum.runCommandLine(args); }
		
		// Note that this should never need to be executed, since internal processing should catch all errors.
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			// CommandLineUtils.printMessageAndTerminate(CommandLineUtils.getCommandLineCorrectUsage(), -1);
		}
	}
}