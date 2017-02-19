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
import jsymbolic2.processing.FileValidator;
import mckay.utilities.general.FileFilterImplementation;
import mckay.utilities.sound.midi.MIDIMethods;
import mckay.utilities.staticlibraries.ArrayMethods;
import mckay.utilities.staticlibraries.StringMethods;
import org.ddmal.jmei2midi.MeiSequence;

/**
 * A JPanel containing a table listing all files from which features are to be extracted. The first column
 * indicates the name of each file, and the second indicates its file path. Double clicking on a given row
 * provides additional metadata about its associated file. Buttons are included for adding or removing files
 * from the table, as well as for sonifying them. The table may be sorted by clicking on either of the column
 * headings.
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
	 * be parsed. Brings up a file chooser dialog box for doing so. An error message will be displayed if
	 * invalid or nonexistent files are chosen. Files chosen are already on the table will be ignored.
	 */
	private JButton add_files_button;
	
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
	 * For synthesizing MIDI streams.
	 */
	private Sequencer midi_sequencer;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Set up this MusicFileSelector's fields and GUI elements.
	 *
	 * @param outer_frame				The JFrame element that contains this JPanel.
	 * @param configuration_file_data	The configuration file data that will be used to initialize the 
	 *									files to extract features from. If null is provided here, then no
	 *									files will be set to have features extracted from them at 
	 *									instantiation.
	 */
	public MusicFileSelectorPanel(OuterFrame outer_frame, ConfigurationFileData configuration_file_data)
	{
		// Store a reference to the containing JFrame
		this.outer_frame = outer_frame;

		// Initialize JFileChooser to null
		load_symbolic_music_file_chooser = null;

		// Initialize the symbolic_music_files_table and symbolic_music_files_table_model fields.
		setUpSymbolicMusicFilesTable();
			
		// Set up the GUI elements on this JPanel
		formatAndAddGuiElements();
				
		// Cause the table to respond to double clicks
		addTableMouseListener();	
		
		// Add symbolic music files to the table if any are specified in the configuration_file_data
		setupGuiWithConfigInputFiles(configuration_file_data);
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
		
		// React to the remove_files_button
		else if (event.getSource().equals(remove_files_button))
			removeSymbolicMusicFilesFromTable();
		
		// React to the sonify_file_directly_button
		else if (event.getSource().equals(sonify_file_directly_button))
			startMidiPlayback();
		
		// React to the stop_sonification_button
		else if (event.getSource().equals(stop_sonification_button))
			stopMidiPlayback();
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
		remove_files_button = new JButton("Remove Files");
		sonify_file_directly_button = new JButton("Play Sonification");
		stop_sonification_button = new JButton("Stop Sonification");
		JPanel button_panel = new JPanel(new GridLayout(1, 4, horizontal_gap, vertical_gap));
		button_panel.add(add_files_button);
		button_panel.add(remove_files_button);
		button_panel.add(sonify_file_directly_button);
		button_panel.add(stop_sonification_button);

		// Add action listeners to buttons
		add_files_button.addActionListener(this);
		remove_files_button.addActionListener(this);
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
	 * Load references to files from the provided information parsed from a configuration settings file and
	 * add them to the symbolic_music_files_table.
	 *
	 * @param configuration_file_data	Data parsed from a configuration settings file, potentially holding
	 *									references to symbolic music files that should be referenced by the
	 *									symbolic_music_files_table. Do nothing if this is null.
	 */
	private void setupGuiWithConfigInputFiles(ConfigurationFileData configuration_file_data)
	{
		if (configuration_file_data != null)
		{
			ConfigurationInputFiles input_files = configuration_file_data.getInputFileList();
			if (input_files != null)
				addSymbolicMusicFilesToTable(input_files.getValidFiles().toArray(new File[0]));
		}
	}

	
	/**
	 * Instantiates a JFileChooser for the load_symbolic_music_file_chooser field if one does not already
	 * exist. This dialog box allows the user to choose one or more files to add to the
	 * symbolic_music_files_table list of references of symbolic music files. Only symbolic music files of
	 * known types are displayed in this JFileChooser by default. If an entered file path corresponds to a
	 * file that does not exist, then an error message dialog box is displayed.
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
	 * Adds the given files to the symbolic_music_files_table, after first verifying that they exist and that
	 * they are files that can be parsed. Displays an error dialog box for each file that cannot be added.
	 * Ignores files that have already been added to the table.
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
					FileValidator.getValidSequence(files_to_add[i], error_log);
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
	}

	
	/**
	 * Removes all selected rows from the symbolic_music_files_table. Brings up an error dialog box if no
	 * files are selected for removal.
	 */
	private void removeSymbolicMusicFilesFromTable()
	{
		try
		{
			int[] selected_rows = symbolic_music_files_table.getSelectedRows();
			for (int i = 0; i < selected_rows.length; i++)
				selected_rows[i] = symbolic_music_files_table.convertRowIndexToModel(selected_rows[i]);
			if (selected_rows.length == 0)
				throw new Exception("Could not delete rows from the table.\nDetails: No files selcected on the table.");
			SymbolicMusicFile[] symbolic_music_files = getSymbolicMusicFilesToExtractFeaturesFrom();
			for (int i = 0; i < selected_rows.length; i++)
				symbolic_music_files[selected_rows[i]] = null;
			Object[] results = ArrayMethods.removeNullEntriesFromArray(symbolic_music_files);
			if (results != null)
			{
				symbolic_music_files = new SymbolicMusicFile[results.length];
				for (int i = 0; i < results.length; i++)
					symbolic_music_files[i] = (SymbolicMusicFile) results[i];
				symbolic_music_files_table_model.resetAndFillTable(symbolic_music_files);
			} 
			else
				symbolic_music_files_table_model.clearTable();
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
			Sequence midi_sequence = FileValidator.getValidSequence(play_file, new ArrayList<>());

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
}