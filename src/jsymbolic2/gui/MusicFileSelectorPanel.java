package jsymbolic2.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sound.midi.*;
import javax.swing.*;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.ConfigurationInputFiles;
import jsymbolic2.processing.MIDIReporter;
import jsymbolic2.processing.MusicFilter;
import jsymbolic2.processing.SymbolicMusicFileUtilities;
import mckay.utilities.general.FileFilterImplementation;
import mckay.utilities.gui.templates.InformationDialogFlexible;
import mckay.utilities.sound.midi.MIDIMethods;
import mckay.utilities.staticlibraries.ArrayMethods;
import mckay.utilities.staticlibraries.StringMethods;
import org.ddmal.jmei2midi.MeiSequence;

/**
 * A JPanel containing a table listing all files from which features are to be extracted. The first column
 * indicates the name of each file, and the second indicates its file path. Double clicking on a given row
 * provides additional metadata about its associated file. Buttons are included for adding or removing files
 * from the table, for sonifying them and for generating reports on them. The table may be sorted by clicking
 * on either of the column headings.
* 
 * @author Cory McKay and Tristano Tenaglia
 */
public class MusicFileSelectorPanel
	extends JPanel
	implements ActionListener
{
	/* STATIC FINAL FIELDS **********************************************************************************/
	
	
	/**
	 * The default directory to begin in when looking for symbolic music files to load.
	 */
	private static final String DEFAULT_LOAD_DIRECTORY = ".";
	
	/**
	 * File extensions of symbolic music files recognized by jSymbolic.
	 */
	private static final String[] ALLOWED_EXTENSIONS = { "mid", "midi", "mei" };
	
	
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * Holds a reference to the JFrame that holds this MusicFileSelectorPanel object.
	 */
	private final OuterFrame outer_frame;

	/**
	 * The table holding symbolic music files from which features will eventually be extracted.
	 */
	private JTable symbolic_music_files_table;
	
	/**
	 * The table model for symbolic_music_files_table.
	 */
	private SymbolicMusicFilesTableModel symbolic_music_files_table_model;

	/**
	 * A button for adding symbolic music files to the the table of files from which features will eventually
	 * be extracted. Brings up a file chooser dialog box for doing so. An error message will be displayed if
	 * invalid or nonexistent files are chosen. Files chosen that are already on the table will be ignored. If
	 * any files are selected, then a report will be written to the Processing Information text area.
	 */
	private JButton add_files_button;
	
	/**
	 * A button for adding symbolic music files to the the table of files from which features will eventually
	 * be extracted. Brings up a file chooser dialog box that allows the user to choose one (and only one)
	 * directory, whose contents are added to the symbolic_music_files_table list of references of symbolic
	 * music files. Any files that do not have a file extension associated with a compatible symbolic music
	 * file are filtered out from consideration. Sub-directories are also explored, recursively. If an entered
	 * path corresponds to a directory that does not exist, or that contains an invalid symbolic music file,
	 * then an error message dialog box is displayed. Files chosen that are already on the table will be
	 * ignored. Prints a report to the Processing Information text area.
	 */	
	private JButton add_directory_button;

	/**
	 * A button for displaying consistency reports. Brings up a new text window holding a formatted
	 * consistency report on those symbolic music files that the user has selected on the
	 * symbolic_music_files_table table (i.e. the particular ones that are selected, not necessarily all of
	 * the ones that appear on the table). This report begins by providing, for each file separately, an
	 * intraconsistenccy report that indicates whether the given file has more than one value for a range of
	 * quantities (e.g. more than one tempo, more than one meter, etc.). Then, if more than one file has been
	 * selected by the user, an interconsistency is provided that indicates, for all files considered as a
	 * whole, whether all the files share the same value or set of values for each of the quantities being
	 * tested for (e.g. all the files have the same tempo, or the same set of tempos).
	 */
	private JButton get_consistency_reports_button;

	/**
	 * A button for displaying MIDI dump reports. Brings up a new text window holding a report on those
	 * symbolic music files that the user has selected on the symbolic_music_files_table table (i.e. the
	 * particular ones that are selected, not necessarily all of the ones that appear on the table). This
	 * report indicates, separately for each file selected by the user, a structured transcription of all the
	 * relevant MIDI messages that the given file contains. If a given file is an MEI file rather than a MIDI
	 * file, then it is converted to MIDI before the report on it is generated.
	 */
	private JButton see_midi_messages_button;
	
	/**
	 * Removes any files selected on the table from the table.
	 */
	private JButton remove_files_button;
	
	/**
	 * Plays the selected symbolic music file. Converts it to MIDI if it is not already MIDI.
	 */
	private JButton sonify_file_directly_button;
	
	/**
	 * Stops any currently ongoing playback of symbolic music files.
	 */
	private JButton stop_sonification_button;

	/**
	 * Allows the user to choose one or more symbolic music files to load.
	 */
	private JFileChooser load_symbolic_music_file_chooser;

	/**
	 * Allows the user to a directory from which all symbolic music files with qualifying file extensions will
	 * be loaded. Sub-directories are explored recursively.
	 */
	private JFileChooser load_symbolic_music_in_a_directory_chooser;
	
	/**
	 * For synthesizing MIDI streams.
	 */
	private Sequencer midi_sequencer;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Set up this MusicFileSelector's fields and GUI elements.
	 *
	 * @param outer_frame				The JFrame element that contains this JPanel.
	 */
	public MusicFileSelectorPanel(OuterFrame outer_frame)
	{
		// Store a reference to the containing JFrame
		this.outer_frame = outer_frame;

		// Initialize JFileChoosers
		load_symbolic_music_file_chooser = null;
		load_symbolic_music_in_a_directory_chooser = null;

		// Initialize the symbolic_music_files_table and symbolic_music_files_table_model fields.
		setUpSymbolicMusicFilesTable();
			
		// Set up the GUI elements on this JPanel
		formatAndAddGuiElements();
				
		// Cause the table to respond to double clicks
		addTableMouseListener();
	}

	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Calls the appropriate method when one of the JButtons on this JPanel is pressed. 
	 *
	 * @param	event	The button-triggered event that is to be reacted to.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		// React to the add_files_button
		if (event.getSource().equals(add_files_button))
			addSymbolicMusicFilesToTableWithJFileChooser();
		
		// React to the add_directory_button
		else if (event.getSource().equals(add_directory_button))
			addDirectoryRecursivelyToTableWithJFileChooser();
		
		// React to the remove_files_button
		else if (event.getSource().equals(remove_files_button))
			removeSymbolicMusicFilesFromTable();

		// React to the get_consistency_report_button
		else if (event.getSource().equals(get_consistency_reports_button))
			displayConsistencyReport();

		// React to the see_midi_messages_button
		else if (event.getSource().equals(see_midi_messages_button))
			displayMidiDump();
		
		// React to the sonify_file_directly_button
		else if (event.getSource().equals(sonify_file_directly_button))
			startMidiPlayback();
		
		// React to the stop_sonification_button
		else if (event.getSource().equals(stop_sonification_button))
			stopMidiPlayback();
	}
	
	
	/**
	 * Load references to files from the provided information parsed from a configuration settings file and
	 * add them to the symbolic_music_files_table.
	 *
	 * @param configuration_file_data	Data parsed from a configuration settings file, potentially holding
	 *									references to symbolic music files that should be referenced by the
	 *									symbolic_music_files_table. Do nothing if this is null.
	 */
	public void addMusicFilesParsedFromConfigFile(ConfigurationFileData configuration_file_data)
	{
		if (configuration_file_data != null)
		{
			ConfigurationInputFiles input_files = configuration_file_data.getInputFileList();
			if (input_files != null)
				addSymbolicMusicFilesToTable(input_files.getValidFiles().toArray(new File[0]));
		}
	}

	
	/**
	 * Get an array indicating all symbolic music files which are currently displayed on this object's
	 * symbolic_music_files_table, in the order that they are displayed on this table.
	 * 
	 * @return	The symbolic music files to extract features from, in the order they are displayed on the
	 *			table. Null if there are none.
	 */
	public SymbolicMusicFile[] getSymbolicMusicFilesToExtractFeaturesFrom()
	{
		if (symbolic_music_files_table_model.getRowCount() == 0)
			return null;
		
		SymbolicMusicFile[] files_to_extract_features_from = new SymbolicMusicFile[symbolic_music_files_table_model.getRowCount()];
		for (int i = 0; i < files_to_extract_features_from.length; i++)
		{
			int table_model_index =  symbolic_music_files_table.convertRowIndexToModel(i);
			File this_file = new File((String) symbolic_music_files_table_model.getValueAt(table_model_index, 1));
			files_to_extract_features_from[i] = new SymbolicMusicFile(this_file, null);
		}
		
		return files_to_extract_features_from;
	}
	
	
	/* STATIC METHODS ***************************************************************************************/

	
	/**
	 * Find the total number of MIDI files and MEI files in the given music_files.
	 * 
	 * @param midi_mei_counts	An array of size 2, where the 0th entry corresponds to the number of MIDI
	 *							files and the 1st entry corresponds to the number of MEI files. Both are
	 *							typically initially 0, but do not have to be. This method adds the respective
	 *							totals from music_files to these two entries. Sets both values to 0 if 
	 *							music_files is null.
	 * @param music_files		The symbolic music files to count.
	 */
	private static void findNumberMeiAndMidiFile(int[] midi_mei_counts, SymbolicMusicFile[] music_files)
	{
		if (music_files == null)
		{
			midi_mei_counts[0] = 0;
			midi_mei_counts[1] = 0;
		}
		
		else for (SymbolicMusicFile this_music_file : music_files)
		{
			if (SymbolicMusicFileUtilities.isValidMidiFile(new File(this_music_file.file_path)))
				midi_mei_counts[0]++;
			else if (SymbolicMusicFileUtilities.isValidMeiFile(new File(this_music_file.file_path)))
				midi_mei_counts[1]++;
		}
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Initialize the symbolic_music_files_table_model and symbolic_music_files_table fields.
	 */
	private void setUpSymbolicMusicFilesTable()
	{
		// Set the column headings and ordering
		Object[] column_names = { "File Name", "File Path" };

		// Prepare the symbolic_music_files_table_model and symbolic_music_files_table fields.
		symbolic_music_files_table_model = new SymbolicMusicFilesTableModel(column_names, 0);
		symbolic_music_files_table = new JTable(symbolic_music_files_table_model);
		
		// Make table sortable
		symbolic_music_files_table.setAutoCreateRowSorter(true);
	}	
	
	
	/**
	 * Instantiate (where not done already), set up and lay out the various GUI components on this JPanel. Add 
	 * action listeners to buttons.
	 */
	private void formatAndAddGuiElements()
	{
		// Prepare this JPanel's basic layout settings
		int horizontal_gap = OuterFrame.HORIZONTAL_GAP; // horizontal space between GUI elements
		int vertical_gap = OuterFrame.VERTICAL_GAP; // horizontal space between GUI elements
		setLayout(new BorderLayout(horizontal_gap, vertical_gap));

		// Add an overall title for this panel
		JLabel panel_label = new JLabel("SYMBOLIC FILES TO EXTRACT FEATURES FROM");
		OuterFrame.formatLabel(panel_label);
		
		// Set up buttons
		add_files_button = new JButton("Add Files");
		add_directory_button = new JButton("Add Directory");
		remove_files_button = new JButton("Remove Files");
		get_consistency_reports_button = new JButton("Consistency Report");
		see_midi_messages_button = new JButton("Contents Report");
		sonify_file_directly_button = new JButton("Play Sonification");
		stop_sonification_button = new JButton("Stop Sonification");
		JPanel button_panel = new JPanel(new GridLayout(2, 4, horizontal_gap, vertical_gap));
		button_panel.add(add_files_button);
		button_panel.add(add_directory_button);
		button_panel.add(remove_files_button);
		button_panel.add(new JLabel());
		button_panel.add(get_consistency_reports_button);
		button_panel.add(see_midi_messages_button);
		button_panel.add(sonify_file_directly_button);
		button_panel.add(stop_sonification_button);

		// Add action listeners to buttons
		add_files_button.addActionListener(this);
		add_directory_button.addActionListener(this);
		remove_files_button.addActionListener(this);
		get_consistency_reports_button.addActionListener(this);
		see_midi_messages_button.addActionListener(this);
		sonify_file_directly_button.addActionListener(this);
		stop_sonification_button.addActionListener(this);

		// Make the symbolic_music_files_table scrollable and place it on its own JPanel
		JScrollPane symbolic_music_files_scroll_pane = new JScrollPane(symbolic_music_files_table);
		JPanel symbolic_music_files_panel = new JPanel(new GridLayout(1, 1));
		symbolic_music_files_panel.add(symbolic_music_files_scroll_pane);

		// Add all GUI elements to this JPanel	
		add(panel_label, BorderLayout.NORTH);
		add(symbolic_music_files_panel, BorderLayout.CENTER);
		add(button_panel, BorderLayout.SOUTH);
		symbolic_music_files_table_model.fireTableDataChanged();
		repaint();
		outer_frame.repaint();
	}


	/**
	 * Makes it so that if a row is double clicked on, then a description of the row's corresponding symbolic
	 * music file is displayed in a pop-up window. Display an error dialog if this is not possible.
	 */
	private void addTableMouseListener()
	{
		symbolic_music_files_table.addMouseListener
		(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent event)
				{
					if (event.getClickCount() == 2)
					{
						int row_clicked = symbolic_music_files_table.rowAtPoint(event.getPoint());
						row_clicked = symbolic_music_files_table.convertRowIndexToModel(row_clicked);
						try
						{
							File file_to_examine = new File((String) symbolic_music_files_table_model.getValueAt(row_clicked, 1));
							String file_metadata = "FILE PATH: " + file_to_examine.getAbsolutePath() + "\n";
							MeiSequence mei_sequence = null;
							try {mei_sequence = new MeiSequence(file_to_examine);} catch (Exception e) {}
							if (mei_sequence != null)
							{
								file_metadata += "FILE TYPE: MEI\n" + 
								                 "SIZE: " + (file_to_examine.length() / 1024) + " KB\n" +
								                 "LAST MODIFIED: " + (new Date(file_to_examine.lastModified())) + "\n" +
								                 "FILE NAME: " + file_to_examine.getName();
							}
							else
							{
								file_metadata += "FILE TYPE: MIDI\n" +
								                 "SIZE: " + (file_to_examine.length() / 1024) + " KB\n" +
								                 "LAST MODIFIED: " + (new Date(file_to_examine.lastModified())) + "\n";
								file_metadata += MIDIMethods.getMIDIFileFormatData(file_to_examine);
							}
							JOptionPane.showMessageDialog( outer_frame,
							                               StringMethods.wrapString(file_metadata, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, OuterFrame.DIALOG_BOX_HANGING_INDENT_CHARS),
							                               "Symbolic Music File Metadata",
							                               JOptionPane.INFORMATION_MESSAGE );
						}
						catch (Exception e)
						{
							String message = "Could not display metadata for the file:\n" +
							                 "     " + symbolic_music_files_table_model.getValueAt(row_clicked, 1) + "\n" +
							                 "Details: " + e.getMessage();
							JOptionPane.showMessageDialog( outer_frame,
							                               StringMethods.wrapString(message, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, OuterFrame.DIALOG_BOX_HANGING_INDENT_CHARS),
							                               "Error",
							                               JOptionPane.ERROR_MESSAGE );
						}
					}
				}
			}
		);
	}
	
	
	/**
	 * Instantiates a JFileChooser for the load_symbolic_music_file_chooser field if one does not already
	 * exist. This dialog box allows the user to choose one or more files to add to the
	 * symbolic_music_files_table list of references of symbolic music files. Only symbolic music files of
	 * known types are displayed in this JFileChooser by default. If an entered file path corresponds to a
	 * file that does not exist, then an error message dialog box is displayed. Prints a report to the
	 * status_print_stream.
	 */
	private void addSymbolicMusicFilesToTableWithJFileChooser()
	{
		// Initialize the load_symbolic_music_file_chooser if it has not been opened yet
		if (load_symbolic_music_file_chooser == null)
		{
			load_symbolic_music_file_chooser = new JFileChooser();
			load_symbolic_music_file_chooser.setCurrentDirectory(new File(DEFAULT_LOAD_DIRECTORY));
			load_symbolic_music_file_chooser.setFileFilter(new FileFilterImplementation(ALLOWED_EXTENSIONS));
			load_symbolic_music_file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			load_symbolic_music_file_chooser.setMultiSelectionEnabled(true);
		}

		// Read the user's choice of load or cancel
		int dialog_result = load_symbolic_music_file_chooser.showOpenDialog(MusicFileSelectorPanel.this);

		// Add the files to the table
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
		{
			File[] load_files = load_symbolic_music_file_chooser.getSelectedFiles();
			addSymbolicMusicFilesToTable(load_files);
		}
	}
	
	
	/**
	 * Instantiates a JFileChooser for the load_symbolic_music_in_a_directory_chooser field if one does not
	 * already exist. This dialog box allows the user to choose one (and only one) directory, whose contents
	 * are added to the symbolic_music_files_table list of references of symbolic music files. Any files that
	 * do not have a file extension associated with a compatible symbolic music file are filtered out from
	 * consideration. Sub-directories are also explored, recursively. If an entered path corresponds to a
	 * directory that does not exist, or that contains an invalid symbolic music file, then an error message
	 * dialog box is displayed. Files chosen that are already on the table will be ignored. Prints a report to
	 * the status_print_stream.
	 */
	private void addDirectoryRecursivelyToTableWithJFileChooser()
	{
		// Initialize the load_symbolic_music_in_a_directory_chooser if it has not been opened yet
		if (load_symbolic_music_in_a_directory_chooser == null)
		{
			load_symbolic_music_in_a_directory_chooser = new JFileChooser();
			load_symbolic_music_in_a_directory_chooser.setCurrentDirectory(new File(DEFAULT_LOAD_DIRECTORY));
			load_symbolic_music_in_a_directory_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			load_symbolic_music_in_a_directory_chooser.setMultiSelectionEnabled(false);
		}

		// Read the user's choice of load or cancel
		int dialog_result = load_symbolic_music_in_a_directory_chooser.showOpenDialog(MusicFileSelectorPanel.this);

		// Add the qualifying files in the directory specified to the table
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
		{
			// Note the directory chosen
			ArrayList<File> directory_chosen = new ArrayList<>();
			directory_chosen.add(load_symbolic_music_in_a_directory_chooser.getSelectedFile());
			
			// Traverse the files and subdirectories (recursively) in directory_chosen to find files with qualifying extensions
			ArrayList<File> files_in_directory = SymbolicMusicFileUtilities.getFilteredFilesRecursiveTraversal( directory_chosen,
			                                                                                                    false,
			                                                                                                    new MusicFilter(),
			                                                                                                    null,
			                                                                                                    new ArrayList<>() );
			
			// Add the files to the table
			File[] load_files = files_in_directory.toArray(new File[files_in_directory.size()]);
			addSymbolicMusicFilesToTable(load_files);
		}
	}
	
	
	/**
	 * Adds the given files to the symbolic_music_files_table, after first verifying that they exist and that
	 * they are files that can be parsed. Displays an error dialog box for each file that cannot be added.
	 * Ignores files that have already been added to the table. Prints a report to the status_print_stream.
	 *
	 * @param	files_to_add	The files to add to the table. These files should all exist, and should all
	 *							be symbolic music files of types accepted by jSymbolic.
	 */
	private void addSymbolicMusicFilesToTable(File[] files_to_add)
	{
		// The symbolic music files to add to the table
		SymbolicMusicFile[] music_files_to_add = new SymbolicMusicFile[files_to_add.length];

		// Go through the new symbolic music files one by one. Add them to music_files_to_add. Display an
		// error dialog box if a problem is encountered.
		for (int i = 0; i < music_files_to_add.length; i++)
		{
			// Assume file is invalid as first guess
			music_files_to_add[i] = null;

			// Verify that the new file exists and is of a type that can be parsed and display an error dialog
			// box if a problem is detected.
			if (files_to_add[i].exists())
			{
				List<String> error_log = new ArrayList<>();
				try
				{
					SymbolicMusicFileUtilities.getMidiSequenceFromMidiOrMeiFile(files_to_add[i], error_log);
					music_files_to_add[i] = new SymbolicMusicFile(files_to_add[i], null);
				} 
				catch (Exception e)
				{
					if (!error_log.isEmpty())
					{
						String text = "Could not parse a specified input file.\nDETAILS: " + error_log.get(0);
						JOptionPane.showMessageDialog( outer_frame,
						                               StringMethods.wrapString(text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
						                               "Error",
						                               JOptionPane.ERROR_MESSAGE );
					}
				}
			}
			else
			{
				String error_message = "Could not parse a specified input file.\n" +
				                       "DETAILS: The file " + files_to_add[i].getPath() + " does not exist.";
				JOptionPane.showMessageDialog( outer_frame,
				                               StringMethods.wrapString(error_message, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
				                               "Error",
				                               JOptionPane.ERROR_MESSAGE );
			}
		}

		// Combine the new symbolic music files with those already on the table
		SymbolicMusicFile[] music_files_already_on_table = getSymbolicMusicFilesToExtractFeaturesFrom();
		int number_music_files_already_on_table = 0;
		if (music_files_already_on_table != null)
			number_music_files_already_on_table = music_files_already_on_table.length;
		int number_music_files_to_add = music_files_to_add.length;
		SymbolicMusicFile[] new_and_old_music_files = new SymbolicMusicFile[number_music_files_already_on_table + number_music_files_to_add];
		for (int i = 0; i < number_music_files_already_on_table; i++)
			new_and_old_music_files[i] = music_files_already_on_table[i];
		for (int i = 0; i < number_music_files_to_add; i++)
			new_and_old_music_files[i + number_music_files_already_on_table] = music_files_to_add[i];

		// Remove duplicate symbolic music files with the same file path
		for (int i = 0; i < new_and_old_music_files.length - 1; i++)
		{
			if (new_and_old_music_files[i] != null)
			{
				String current_path = new_and_old_music_files[i].file_path;
				for (int j = i + 1; j < new_and_old_music_files.length; j++)
					if (new_and_old_music_files[j] != null)
						if (current_path.equals(new_and_old_music_files[j].file_path))
							new_and_old_music_files[j] = null;
			}
		}

		// Remove null entries
		Object[] final_combined_and_cleaned_music_files = ArrayMethods.removeNullEntriesFromArray(new_and_old_music_files);
		if (final_combined_and_cleaned_music_files != null)
		{
			music_files_already_on_table = new SymbolicMusicFile[final_combined_and_cleaned_music_files.length];
			for (int i = 0; i < final_combined_and_cleaned_music_files.length; i++)
				music_files_already_on_table[i] = (SymbolicMusicFile) final_combined_and_cleaned_music_files[i];
		}

		// Update the table to display the new symbolic music files
		symbolic_music_files_table_model.resetAndFillTable(music_files_already_on_table);
		
		// Add a report to the status_print_stream
		int[] midi_mei_counts = new int[2];
		int count_of_final_combined_and_cleaned_music_files = 0;
		if (final_combined_and_cleaned_music_files != null)
			count_of_final_combined_and_cleaned_music_files = final_combined_and_cleaned_music_files.length;
		findNumberMeiAndMidiFile(midi_mei_counts, getSymbolicMusicFilesToExtractFeaturesFrom());
		String report = ">>> Adding to the list of symbolic music files from which features are to be extracted . . . \n";
		for (int i = 0; i < number_music_files_to_add; i++)
			if (music_files_to_add[i] != null)
				report += "\t\t>>> " + music_files_to_add[i].file_path + "\n";
		report += "\t>>> " + number_music_files_to_add + " files were selected to be added to the existing list of " + number_music_files_already_on_table + " files.\n";
		report += "\t>>> Of these, " + (count_of_final_combined_and_cleaned_music_files - number_music_files_already_on_table) + " files were valid non-duplicate files, and were added to the list.\n";
		report += "\t>>> There are now a total of " + count_of_final_combined_and_cleaned_music_files + " files ready to have features extracted from them.\n";
		report += "\t>>> This total includes " + midi_mei_counts[0] + " MIDI files and " + midi_mei_counts[1] + " MEI files.\n";
		outer_frame.status_print_stream.println(report);
	}

	
	/**
	 * Removes all selected rows from the symbolic_music_files_table. Brings up an error dialog box if no
	 * files are selected for removal.
	 */
	private void removeSymbolicMusicFilesFromTable()
	{
		try
		{
			// Remove the rows from the table, and keep track of what is being done
			int[] selected_rows = symbolic_music_files_table.getSelectedRows();
			for (int i = 0; i < selected_rows.length; i++)
				selected_rows[i] = symbolic_music_files_table.convertRowIndexToModel(selected_rows[i]);
			if (selected_rows.length == 0)
				throw new Exception("Could not delete rows from the table.\nDetails: No files selcected on the table.");
			SymbolicMusicFile[] symbolic_music_files = getSymbolicMusicFilesToExtractFeaturesFrom();
			int count_of_files_before_removal = symbolic_music_files.length;
			String[] paths_of_removed_files = new String[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++)
			{
				paths_of_removed_files[i] = symbolic_music_files[selected_rows[i]].file_path;
				symbolic_music_files[selected_rows[i]] = null;
			}
			Object[] results = ArrayMethods.removeNullEntriesFromArray(symbolic_music_files);
			if (results != null)
			{
				symbolic_music_files = new SymbolicMusicFile[results.length];
				for (int i = 0; i < results.length; i++)
					symbolic_music_files[i] = (SymbolicMusicFile) results[i];
				symbolic_music_files_table_model.resetAndFillTable(symbolic_music_files);
			} 
			else symbolic_music_files_table_model.clearTable();

			// Add a report to the status_print_stream
			int[] midi_mei_counts = new int[2];
			int count_of_files_after_removal = 0;
			if (results != null)
				count_of_files_after_removal = results.length;
			findNumberMeiAndMidiFile(midi_mei_counts, getSymbolicMusicFilesToExtractFeaturesFrom());
			String report = ">>> Removing entries from the list of symbolic music files from which features are to be extracted . . . \n";
			for (int i = 0; i < paths_of_removed_files.length; i++)
				report += "\t\t>>> " + paths_of_removed_files[i] + "\n";
			report += "\t>>> " + paths_of_removed_files.length + " files were selected to be removed from the previous list of " + count_of_files_before_removal + " files.\n";
			report += "\t>>> There are now a total of " + count_of_files_after_removal + " files ready to have features extracted from them.\n";
			report += "\t>>> This total includes " + midi_mei_counts[0] + " MIDI files and " + midi_mei_counts[1] + " MEI files.\n";
			outer_frame.status_print_stream.println(report);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog( outer_frame,
			                               StringMethods.wrapString(e.getMessage(), OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
			                               "Error",
			                               JOptionPane.ERROR_MESSAGE );
		}
	}
	
	
	/**
	 * Spawn a new text window holding a formatted consistency report on those symbolic music files that the
	 * user has selected on the symbolic_music_files_table table (i.e. the particular ones that are selected,
	 * not necessarily all of the ones that appear on the table). This report begins by providing, for each
	 * file separately, an intraconsistenccy report that indicates whether the given file has more than one
	 * value for a range of quantities (e.g. more than one tempo, more than one meter, etc.). Then, if more
	 * than one file has been selected by the user, an interconsistency is provided that indicates, for all
	 * files considered as a whole, whether all the files share the same value or set of values for each of
	 * the quantities being tested for (e.g. all the files have the same tempo, or the same set of tempos).
	 */
	private void displayConsistencyReport()
	{
		try
		{
			// Note the files selected on the table by the user
			File[] selected_files = getSelectedFiles();
			
			// Prepare the report
			String report = MIDIReporter.prepareConsistencyReports(selected_files, true, true, true);

			// Display the report in a new window
			new InformationDialogFlexible(report, "Consistency Report", false, false, false, true, 80, 50);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog( outer_frame,
			                               StringMethods.wrapString(e.getMessage(), OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
			                               "Error",
			                               JOptionPane.ERROR_MESSAGE );
		}
	}
	
	
	/**
	 * Spawn a new text window holding a MIDI dump report on those symbolic music files that the user has
	 * selected on the symbolic_music_files_table table (i.e. the particular ones that are selected, not
	 * necessarily all of the ones that appear on the table). This report indicates, separately for each file
	 * selected by the user, a structured transcription of all the relevant MIDI messages that the given file
	 * contains. If a given file is an MEI rather than a MIDI file, then it is converted to MIDI before
	 * reporting. 
	 */
	private void displayMidiDump()
	{
		try
		{
			// Note the files selected on the table by the user
			File[] selected_files = getSelectedFiles();
			
			// The report to display
			StringBuilder report = new StringBuilder();
			
			// Report on each file
			for (int i = 0; i < selected_files.length; i++)
			{
				// Parse and check the MIDI file
				MIDIReporter midi_debugger = new MIDIReporter(selected_files[i]);

				// Generate the report
				report.append("\n============ MIDI MESSAGES REPORT FOR FILE " + (i+1) + " / " + selected_files.length + " ============\n");
				report.append(midi_debugger.prepareHeaderReport());
				report.append(midi_debugger.prepareMetaMessageReport(true, true, true, true, true, true));
				report.append(midi_debugger.prepareProgramChangeAndUnpitchedInstrumentsReport());
				report.append(midi_debugger.prepareControllerMessageReport());
				report.append(midi_debugger.prepareNoteReport(false, true));
			}

			// Display the report in a new window
			new InformationDialogFlexible(report.toString(), "MIDI Messages Report", false, false, false, true, 60, 50);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog( outer_frame,
			                               StringMethods.wrapString(e.getMessage(), OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
			                               "Error",
			                               JOptionPane.ERROR_MESSAGE );
		}
	}
	
	
	/**
	 * Plays the selected symbolic music file directly from the file referred to by the currently selected row
	 * on the symbolic_music_files_table. If multiple files are selected, plays only the first one. Any 
	 * previous playback is stopped. Can play MIDI files or MEI files (by converting them to MIDI). Displays
	 * an error dialog if the file cannot be played.
	 */
	private void startMidiPlayback()
	{
		try
		{
			// Stop any existing playback
			stopMidiPlayback();

			// Get the file selected for playback
			int selected_row = symbolic_music_files_table.getSelectedRow();
			selected_row = symbolic_music_files_table.convertRowIndexToModel(selected_row);
			if (selected_row < 0)
				throw new Exception("No file selcected for playback.");
			File play_file = new File((String) symbolic_music_files_table_model.getValueAt(selected_row, 1));

			// Load the file into a MIDI Sequence object
			Sequence midi_sequence = SymbolicMusicFileUtilities.getMidiSequenceFromMidiOrMeiFile(play_file, new ArrayList<>());

			// Begin playback
			midi_sequencer = MIDIMethods.playMIDISequence(midi_sequence);
		}
		catch (Exception e)
		{
			String text = "Could not play specified symbolic file.\nDetails: " + e.getMessage();
			JOptionPane.showMessageDialog( outer_frame,
			                               StringMethods.wrapString(text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
			                               "Error",
			                               JOptionPane.ERROR_MESSAGE );
		}
	}

	
	/**
	 * Stop any playback of a symbolic MIDI file in progress.
	 */
	private void stopMidiPlayback()
	{
		if (midi_sequencer != null)
		{
			midi_sequencer.stop();
			midi_sequencer = null;
		}
	}
	
	
	/**
	 * Return the files that are currently selected in the symbolic_music_files_table.
	 * 
	 * @return				The selected files. Note that no verification is performed by this particular
	 *						method to ensure that the files exist and are valid.
	 * @throws Exception	Throws an informative Exception if the user has not selected any files.
	 */
	private File[] getSelectedFiles()
		throws Exception
	{
		// Note which rows are selected on the table
		int[] selected_rows = symbolic_music_files_table.getSelectedRows();
		for (int i = 0; i < selected_rows.length; i++)
			selected_rows[i] = symbolic_music_files_table.convertRowIndexToModel(selected_rows[i]);
		if (selected_rows.length == 0)
			throw new Exception("Could not generate the report.\nDetails: No files selcected on the table to generate the report on.");

		// All the files listed on the table (selected or not)
		SymbolicMusicFile[] all_symbolic_music_files_on_table = getSymbolicMusicFilesToExtractFeaturesFrom();

		// Find the paths of the particular files selected on the table
		File[] selected_files = new File[selected_rows.length];
		for (int i = 0; i < selected_rows.length; i++)
			selected_files[i] = new File(all_symbolic_music_files_on_table[selected_rows[i]].file_path);

		// Return the results
		return selected_files;
	}
}