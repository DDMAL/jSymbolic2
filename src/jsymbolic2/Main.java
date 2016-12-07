/*
 * Main.java
 * Version 2.0
 *
 * Last modified on June 6, 2016.
 * McGill University and the University of Waikato
 */

package jsymbolic2;


import jsymbolic2.commandline.CommandLineSwitchEnum;
import jsymbolic2.commandline.CommandLineUtils;

/**
 * Runs the jSymbolic Feature Extractor in GUI or Command Line mode
 * depending on the command line arguments given.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class Main
{
	/**
	 * Runs jSymbolic based on command line arguments.
	 * @param args Command line input parameter arguments.
	 */
	public static void main(String[] args)
	{
		try {
			CommandLineSwitchEnum.runCommand(args);
		} catch (Exception e) {
			//Top level error checking and termination
			e.printStackTrace();
			CommandLineUtils.printMessageAndTerminate(CommandLineUtils.getUsageMessage(),-1);
		}
	}
}