package jsymbolic2;

import jsymbolic2.commandline.CommandLineSwitchEnum;

/**
 * The jSymbolic runnable class. See the README or manual for more details on jSymbolic.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class Main
{
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