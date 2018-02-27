package jsymbolic2.processing;

import java.io.File;
import java.io.FileFilter;

/**
 * A FileFilter that only accepts files with MIDI or MEI extensions (.mei, .midi or .mid). Case is ignored.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class MusicFilter implements FileFilter
{
	/* PRIVATE STATIC FIELDSS *******************************************************************************/

	
	/**
	 * File extensions that are accepted by this FileFilter.
	 */
	private static final String[] accepted_file_extensions = new String[] { ".mei", ".midi", ".mid" };

	
	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Check to see if the specified file path ends with an accepted extension. These are .mei, .midi or .mid,
	 * in any case.
	 * 
	 * @param file	The file to check.
	 * @return		Whether or not the file path ends with an accepted extension.
	 */
	@Override
	public boolean accept(File file)
	{
		for (String extension : accepted_file_extensions)
			if (file.getName().toLowerCase().endsWith(extension))
				return true;
		return false;
	}

	
	/* PUBLIC STATIC METHODS **************(*****************************************************************/
	
	
	/**
	 * Check to see if the specified file path ends with an accepted extension. These are .mei, .midi or .mid,
	 * in any case.
	 * 
	 * @param file	The file to check.
	 * @return		Whether or not the file path ends with an accepted extension.
	 */
	public static boolean passesFilter(File file)
	{
		for (String extension : accepted_file_extensions)
			if (file.getName().toLowerCase().endsWith(extension))
				return true;
		return false;
	}	
}