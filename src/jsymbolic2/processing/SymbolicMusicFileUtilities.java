package jsymbolic2.processing;

import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import ca.mcgill.music.ddmal.mei.MeiXmlReader;
import ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException;
import org.ddmal.jmei2midi.MeiSequence;

/**
 * A class holding static methods for validating and parsing MIDI and MEI files. Many of these methods in
 * effect check to see if files are valid MIDI or MEI as part of their processing. If they are valid, they are
 * parsed into a MIDI sequence (MEI files are converted to MIDI as part of this processing). If they are not
 * valid, errors are logged in provided error logs and Exceptions are thrown.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class SymbolicMusicFileUtilities
{
	/* PUBLIC STATIC METHODS ********************************************************************************/

	
	/**
	 * Test all of the files specified in files_to_test. Return a list of all of these files that are in fact
	 * valid MIDI or MEI files. If one or more files are invalid, then output an error message identifying 
	 * them. If no valid files are present, then print a message indicating this and end execution.
	 * 
	 * @param files_to_test			The files to test to see if they are valid MIDI or MEI files.
	 * @param status_print_stream	A stream to print general messages to.
	 * @param error_print_stream	A stream to print error messages to.
	 * @param error_log				Errors that occur during processing are added to this list.
	 * @return						A list of the valid MIDI and MEI files found in files_to_test.
	 */
	public static ArrayList<File> validateAndGetMidiAndMeiFiles( ArrayList<File> files_to_test,
	                                                             PrintStream status_print_stream,
	                                                             PrintStream error_print_stream,
	                                                             List<String> error_log )
	{
		// The files listed in files_to_test that are valid MIDI or MEI files
		ArrayList<File> valid_files = new ArrayList<>();

		// The files listed in files_to_test that are not valid MIDI or MEI files
		ArrayList<File> invalid_files = new ArrayList<>();
		
		// Note which files are valid and which are not
		for (File file : files_to_test)
		{
			
			if ( isValidMidiFile(file) || isValidMeiFile(file) )
				valid_files.add(file);
			else invalid_files.add(file);
		}
		
		// Output error messages if any of the files listed 
		if (!invalid_files.isEmpty())
		{
			String invalid_file_paths = new String();
			for (File file : invalid_files)	
				invalid_file_paths += file.getAbsolutePath() + " ";
			String error_string = "The following " + invalid_files.size() + " files cannot be processed because they are not valid MIDI or MEI files, and were thus removed from the list of files to process: " + invalid_file_paths;
			error_log.add(error_string);
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_string);
		}

		// End execution and output an explanatory message if there are no valid files to process
		if (valid_files.isEmpty())
		{
			UserFeedbackGenerator.simplePrintln(status_print_stream, "Ending execution, as no valid files have been specified for processing.\n");
			System.exit(0);
		}
		
		// Return the set of valid MIDI and MEI files
		return valid_files;
	}
		
	
	/**
	 * Checks to see if the specified file is a valid MIDI file. This is done by attempting to parse it.
	 *
	 * @param file	The file to be checked.
	 * @return		True if it is a valid MIDI file, false if it is not.
	 */
	public static boolean isValidMidiFile(File file)
	{
		try { MidiSystem.getMidiFileFormat(file); }
		catch ( InvalidMidiDataException | IOException e) { return false; }
		return true;
	}


	/**
	 * Checks to see if the specified file is a valid MEI file. This is done by attempting to parse it.
	 *
	 * @param file	The file to be checked.
	 * @return		True if it is a valid MEI file, false if it is not.
	 */
	public static boolean isValidMeiFile(File file)
	{
		try { MeiXmlReader.loadFile(file); }
		catch ( MeiXmlReadException e ) { return false; }
		return true;
	}

	
	/**
	 * Extracts a MIDI Sequence object from the given MIDI or MEI file. If the file is not a valid MIDI or MEI
	 * file, then an exception is thrown and a report is added to error_log.
	 *
	 * @param file			A MIDI or MEI file to parse.
	 * @param error_log		Errors that occur during processing are added to this list.
	 * @return				A MIDI Sequence parsed from the given file.
	 * @throws Exception	An informative Exception is thrown if file is not a valid MIDI or MEI file.
	 */
	public static Sequence getMidiSequenceFromMidiOrMeiFile( File file, List<String> error_log )
		throws Exception
	{
		Sequence sequence = null;
		if (isValidMidiFile (file))
			sequence = getMidiSequenceFromMidiFile(file, error_log);
		else if (isValidMeiFile(file))
			sequence = getMidiSequenceFromMeiFile(file, error_log);
		else
		{
			error_log.add("The specified file, " + file + ", is not a valid MIDI or MEI file.");
			throw new Exception("The specified file, " + file + ", is not a valid MIDI or MEI file.");
		}
		return sequence;
	}


	/**
	 * Extracts a MIDI Sequence object from the given MIDI file. If the file is not a valid MIDI file, then an
	 * exception is thrown and a report is added to error_log.
	 *
	 * @param file						A MIDI file to parse.
	 * @param error_log					Errors that occur during processing are added to this list.
	 * @return							An MeiSequence parsed from the given MEI file.
	 * @throws IOException				Thrown if there is a problem accessing the given file.
	 * @throws InvalidMidiDataException	Thrown if the given file is not a valid MIDI file.
	 */
	public static Sequence getMidiSequenceFromMidiFile(File file, List<String> error_log)
		throws IOException, InvalidMidiDataException
	{
		Sequence sequence = null;
		try { sequence = MidiSystem.getSequence(file); }
		catch (IOException e)
		{
			error_log.add("The specified path, " + file + ", does not refer to a valid file.");
			throw e;
		}
		catch (InvalidMidiDataException e)
		{
			error_log.add("The specified file, " + file + ", is not a valid MIDI or MEI file.");
			throw e;
		}
		return sequence;
	}

	
	/**
	 * Extracts an MeiSequence object from the given MEI file. If the file is not a valid MEI file, then an
	 * exception is thrown and a report is added to error_log.
	 *
	 * @param file						An MEI file to parse.
	 * @param error_log					Errors that occur during processing are added to this list.
	 * @return							An MeiSequence parsed from the given MEI file.
	 * @throws MeiXmlReadException		Thrown if there is a problem parsing the MEI in the given file.
	 * @throws InvalidMidiDataException	Thrown if MIDI data extracted from the given MEI file is invalid.
	 */
	public static MeiSequence getMeiSequenceFromMeiFile( File file, List<String> error_log )
		throws InvalidMidiDataException, MeiXmlReadException
	{
		try { return new MeiSequence(file); }
		catch (InvalidMidiDataException | MeiXmlReadException e)
		{
			error_log.add("The specified file, " + file + ", is not a valid MEI file.");
			throw e;
		}
	}

	
	/**
	 * Extracts a MIDI Sequence object from the given MEI file. If the file is not a valid MEI file, then an
	 * exception is thrown and a report is added to error_log.
	 *
	 * @param file						An MEI file to parse.
	 * @param error_log					Errors that occur during processing are added to this list.
	 * @return							A MIDI Sequence parsed from the given MEI file.
	 * @throws MeiXmlReadException		Thrown if there is a problem parsing the MEI in the given file.
	 * @throws InvalidMidiDataException	Thrown if MIDI data extracted from the given MEI file is invalid.
	 */
	public static Sequence getMidiSequenceFromMeiFile( File file, List<String> error_log )
		throws MeiXmlReadException, InvalidMidiDataException
	{
		Sequence sequence = null;
		try
		{
			MeiSequence meiSequence = new MeiSequence(file);
			sequence = meiSequence.getSequence();
		} 
		catch (MeiXmlReadException | InvalidMidiDataException e)
		{
			error_log.add("The specified file, " + file + ", is not a valid MIDI or MEI file.");
			throw e;
		}
		return sequence;
	}


	/**
	 * If the given file_or_directory_path is a file, then simply return it in a single-entry array (it is not
	 * subjected to file_filter). If it is a directory, then recursively traverse it and return an array
	 * containing all files found that pass the specified file_filter. Print a warning to the given 
	 * print_stream if no qualifying files are found in the directory
	 * 
	 * @param file_or_directory_path	A path to a file or to a directory.
	 * @param file_filter				A filter through which files parsed from a directory must pass in
	 *									order to be included in the returned array.
	 * @param error_print_stream		A stream to print processing error reports to. May not be null.
	 * @param error_log					A list of errors encountered so far. Errors are added to it if any are
	 *									encountered. May not be null.
	 * @return							The specified File, if file_or_directory_path refers to a file, or
	 *									the files parsed from file_or_directory_path if it is a directory.
	 *									Returns null if the specified file does not refer to an existing file
	 *									or directory.
	 */
	public static File[] getRecursiveListOfFiles( String file_or_directory_path,
	                                              FileFilter file_filter,
	                                              PrintStream error_print_stream, 
	                                              List<String> error_log )
	{
		// The set of qualifying files in file_or_directory_path
		File[] files_found = {new File(file_or_directory_path)};
		
		// Return null if file_or_directory_path does not exist
		if (!files_found[0].exists())
		{
			UserFeedbackGenerator.printErrorMessage(error_print_stream, file_or_directory_path + " does not refer to an existing file or directory.");
			return null;
		}
			
		// If it is a directory, then parse and filter it recursively
		if (files_found[0].isDirectory())
		{
			List directory_to_parse_dummy_list = new ArrayList<>();
			directory_to_parse_dummy_list.add(files_found[0]);
			ArrayList<File> qualifying_files = getFilteredFilesRecursiveTraversal( directory_to_parse_dummy_list,
			                                                                       false,
			                                                                       file_filter,
			                                                                       error_print_stream,
			                                                                       error_log );
			files_found = qualifying_files.toArray(new File[qualifying_files.size()]);
			if (qualifying_files.isEmpty())
				UserFeedbackGenerator.printWarningMessage(error_print_stream, "No files with appropriate file extensions were found in the " + directory_to_parse_dummy_list.get(0) + " directory or its sub-directories.");
		}

		// Return the set of files
		return files_found;
	}


	/**
	 * Traverses the provided files_and_directories list and generates a new list consisting of all files in
	 * files_and_directories. Any directories referred to in files_and_directories are also traversed
	 * recursively, and files found while doing this are also added to the returned list (but only if they
	 * pass the requirements of the file_filter, regardless of the value of
	 * filter_explicitly_specified_files). Files explicitly mentioned in files_and_directories are only
	 * subjected to the file_filter, however, if filter_explicitly_specified_files is set to true. Regardless,
	 * the returned list will NOT include any directories directly, only qualifying files found within them.
	 * 
	 * @param files_and_directories				The set of files and/or directories to traverse.
	 * @param filter_explicitly_specified_files	Whether or not files mentioned directly in 
	 *											files_and_directories (i.e. are not just in subfolders of
	 *											directories mentioned there) should be subjected to 
	 *											file_filter.
	 * @param file_filter						A filter through which files must pass in order to be included 
	 *											in the returned list.
	 * @param error_print_stream				A stream to print processing error reports to. May be null, in
	 *											which case errors will still be added to error_log.
	 * @param error_log							A list of errors encountered so far. Errors are added to it if
	 *											any are encountered. May not be null.
	 * @return									A list of all files in files_and_directories and its
	 *											recursively traversed sub-directories that passed file_filter.
	 *											Files explicitly mentioned in files_and_directories that do
	 *											not pass file_filter will still be included in the returned
	 *											list if filter_explicitly_specified_files is false.
	 */
	public static ArrayList<File> getFilteredFilesRecursiveTraversal( List<File> files_and_directories,
	                                                                  boolean filter_explicitly_specified_files,
	                                                                  FileFilter file_filter,
	                                                                  PrintStream error_print_stream,
	                                                                  List<String> error_log )
	{
		ArrayList<File> complete_file_list = new ArrayList<>();
		for (File file : files_and_directories)
		{
			if (file.isDirectory())
			{
				List<Path> files_in_subfolder;
				try
				{
					files_in_subfolder = Files.walk(Paths.get(file.getAbsolutePath())).filter(name -> new MusicFilter().accept(name.toFile())).collect(Collectors.toList());
					for (Path path_in_subfolder : files_in_subfolder)
						complete_file_list.add(path_in_subfolder.toFile());
				} 
				catch (IOException ioe)
				{
					String error_message = "Could not traverse files from this folder: " + file.getAbsolutePath();
					if (error_print_stream != null)
						UserFeedbackGenerator.printWarningMessage(error_print_stream, error_message);
					error_log.add(error_message);
				}
			}
			else
			{
				boolean pass_this_file = true;
				if (filter_explicitly_specified_files && !MusicFilter.passesFilter(file))
					pass_this_file = false;
				if (pass_this_file)
					complete_file_list.add(file);
			}
		}
		return complete_file_list;
	}
	
	
	/**
	 * Checks if the specified file_name ends with the specified file_extension (preceded by a period). If it
	 * does, then return a reference to it unchanged. If it does not, then return a reference to a new string
	 * with file_extension (preceded by a period) added to the end of file_name.
	 *
	 * @param file_name			The file name to check.
	 * @param file_extension	The file extension to check file_name for. This file_extension should not 
	 *							include a period.
	 * @return					file_name with the proper extension added to it if it is not there already.
	 */
	public static String correctFileExtension(String file_name, String file_extension)
	{
		String file_name_with_extension_added = file_name + "." + file_extension;
		
		if (!file_name.contains("."))
			return file_name_with_extension_added;

		String[] split_file_name = file_name.split("\\.");
		if (split_file_name[split_file_name.length - 1].equalsIgnoreCase(file_extension))
			return file_name;
		else return file_name_with_extension_added;
	}

	
	/**
	 * Given a file path, return true if a file (or other resource) at the corresponding path exists, and
	 * false if it does not.
	 *
	 * @param path_to_check	The file path to check.
	 * @return				True if a resource exists at path_to_check, false if it does not.
	 */
	public static boolean filePathExists(String path_to_check)
	{
		String absolute_path = (new File(path_to_check)).getAbsolutePath();
		String path_string = absolute_path.substring(0, absolute_path.lastIndexOf(File.separator));
		Path path = Paths.get(path_string);
		return Files.exists(path);
	}
}