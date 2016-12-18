/*
 * RecordingSelectorPanel.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.util.List;
import java.util.ArrayList;

import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.ConfigurationInputFiles;
import jsymbolic2.datatypes.RecordingInfo;
import jsymbolic2.processing.FileValidator;


/**
 * A window that allows users to select MIDI files to extract features from
 * play. Alos allows the user to view data about individual files.
 *
 * <p>The Add Recordings button allows the user to add one or more MIDI
 * files to the table.
 *
 * <p>The Delete Recordings button deletes one or more recordings from the
 * table.
 *
 * <p>The Store Sequence checkbox sets whether sequences are to be stored in
 * memory upon adding with the Add Recordings button. This can use up memory,
 * but can also speed up processing.
 *
 * <p>The Validate Recordings checkbox sets whether files added to the table
 * are verified to see if they can be read.
 *
 * <p>The View File Info. button displays information on the selected files
 * in the table.
 *
 * <p>The Play Sequence button plays the file selected on the table
 *
 * <p>The Stop Playback button stops playback in progress.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class RecordingSelectorPanel
     extends JPanel
     implements ActionListener
{
     /* FIELDS ****************************************************************/
     
     
     /**
      * Holds a reference to the JPanel that holds objects of this class.
      */
     public    OuterFrame                    outer_frame;
     
     /**
      * Holds references to files, associated meta-data and
      * extracted features.
      */
     public    RecordingInfo[]               recording_list;
     
     /**
      * GUI panels
      */
     private   JPanel                        recordings_panel;
     private   JScrollPane                   recordings_scroll_pane;
     
     /**
      * GUI table-related fields
      */
     private   JTable                        recordings_table;
     private   RecordingsTableModel          recordings_table_model;
     
     /**
      * GUI buttons
      */
     private   JButton                       add_recordings_button;
     private   JButton                       delete_recordings_button;
     private   JButton                       view_recording_information_button;
     private   JButton                       play_recording_directly_button;
     private   JButton                       stop_playback_button;
     
     /**
      * GUI check boxes
      */
     private   JCheckBox                     store_symbolic_stream_check_box;
     private   JCheckBox                     validate_recordings_when_load_them_check_box;
     
     /**
      * GUI dialog boxes
      */
     private   JFileChooser                  load_recording_chooser;
     
     /**
      * For playing MIDI files
      */
     private   Sequencer                     midi_sequencer;
     
     /* CONSTRUCTORS **********************************************************/
     
     
     /**
      * Set up frame.
      *
      * @param outer_frame	The GUI element that contains this object.
      * @param configuration_file_data The configuration file data that could be used to set up this
      *                                panel with. If it is null then the default settings are used.
      */
     public RecordingSelectorPanel(OuterFrame outer_frame, ConfigurationFileData configuration_file_data)
     {
          // Store containing panel
          this.outer_frame = outer_frame;

          // Initialize some fields to null
          recording_list = null;
          load_recording_chooser = null;
          
          // General container preparations containers
          int horizontal_gap = 6; // horizontal space between GUI elements
          int vertical_gap = 11; // horizontal space between GUI elements
          setLayout(new BorderLayout(horizontal_gap, vertical_gap));
          
          // Add an overall title for this panel
		JLabel panel_label = new JLabel("SYMBOLIC FILES TO PROCESS:");
		OuterFrame.formatLabel(panel_label);
		add(panel_label, BorderLayout.NORTH);
          
          // Set up the list of recordings (initially blank)
          recordings_panel = null;
          setUpRecordingListTable();
          
          // Set up buttons and check boxes
          JPanel button_panel = new JPanel(new GridLayout(7, 2, horizontal_gap, vertical_gap));
          add_recordings_button = new JButton("Add Recordings");
          add_recordings_button.addActionListener(this);
          button_panel.add(add_recordings_button);
          validate_recordings_when_load_them_check_box = new JCheckBox("Validate Recordings", true);
          button_panel.add(validate_recordings_when_load_them_check_box);
          button_panel.add(new JLabel(""));
          store_symbolic_stream_check_box = new JCheckBox("Store Sequence", false);
          button_panel.add(store_symbolic_stream_check_box);
          delete_recordings_button = new JButton("Delete Recordings");
          delete_recordings_button.addActionListener(this);
          button_panel.add(delete_recordings_button);
          button_panel.add(new JLabel(""));
          button_panel.add(new JLabel(""));
          button_panel.add(new JLabel(""));
          play_recording_directly_button = new JButton("Play Sequence");
          play_recording_directly_button.addActionListener(this);
          button_panel.add(play_recording_directly_button);
          stop_playback_button = new JButton("Stop Playback");
          stop_playback_button.addActionListener(this);
          button_panel.add(stop_playback_button);
          button_panel.add(new JLabel(""));
          button_panel.add(new JLabel(""));
          view_recording_information_button = new JButton("View File Info");
          view_recording_information_button.addActionListener(this);
          button_panel.add(view_recording_information_button);
          add(button_panel, BorderLayout.SOUTH);

          if(configuration_file_data != null) {
               setupGuiWithConfigInputFiles(configuration_file_data);
          }
     }
     
     
     /* PUBLIC METHODS ********************************************************/

    /**
     * Sets up the Recording Selector panel with the input files from the config file.
     * @param configuration_file_data Parsed data from the config file.
     */
     public void setupGuiWithConfigInputFiles(ConfigurationFileData configuration_file_data) {
          ConfigurationInputFiles inputFiles = configuration_file_data.getInputFileList();
          if (inputFiles != null) {
               addRecordings(inputFiles.getValidFiles().toArray(new File[0]));
          }
     }

     /**
      * Adds the given files to the table display and stores a reference to them.
      * Ignores files that have already been added to the table.
      *
      * <p>Verifies that the files are valid MIDI files that can be read if the
      * validate_recordings_when_load_them_check_box checkbox is selected. Only stores the
      * actual sequences if the store_symbolic_stream_check_box check box is selected (otherwise
      * just stores file references).
      *
      * <p>If a given file path corresponds to a file that does not exist,
      * then an error message is displayed.
      *
      * @param	files_to_add	The files to add to the table.
      */
     public void addRecordings(File[] files_to_add)
     {
          // Prepare to store the information about each file
          RecordingInfo[] recording_info = new RecordingInfo[files_to_add.length];
          
          // Go through the files one by one
          for (int i = 0; i < files_to_add.length; i++)
          {
               // Assume file is invalid as first guess
               recording_info[i] = null;
               
               // Verify that the file exists
               if ( files_to_add[i].exists() )
               {
                   List<String> errorLog = new ArrayList<>();
                    try
                    {
                         // The symbolic music extracted from each file
                         Sequence symbolic_sequence = null;

                         // Load the symbolic data if the validate_recordings_when_load_them_check_box
                         // is selected. Throw an exception if the file is not a valid
                         // symbolic file of a type that can be read and processed.
                         if (validate_recordings_when_load_them_check_box.isSelected())
                              symbolic_sequence = FileValidator.getValidSequence(files_to_add[i], errorLog);
                         
                         // Store the symbolic streams themselves in memory if the
                         // store_symbolic_stream_check_box check box is selected. Throw an
                         // exception if the file is not a valid symbolic file of a type that
                         // can be read and processed.
                         if (!store_symbolic_stream_check_box.isSelected())
                              symbolic_sequence = null;
                         
                         // Generate a RecordingInfo object for the loaded file
                         recording_info[i] = new RecordingInfo( files_to_add[i].getName(),
                              files_to_add[i].getPath(),
                              symbolic_sequence,
                              false );
                    }
                    catch (Exception e) {
                        FileValidator.windowErrorLog(errorLog);
                    }
               }
               else
               {
                    JOptionPane.showMessageDialog(null, "The file " + files_to_add[i].getName() + " does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
               }
          }
          
          // Update the recording_list field to include these new entries, while removing
          // null entries due to problems with invalid files
          int number_old_recordings = 0;
          if (recording_list != null)
               number_old_recordings = recording_list.length;
          int number_new_recordings = 0;
          if (recording_info != null)
               number_new_recordings = recording_info.length;
          RecordingInfo[] temp_recording_list = new RecordingInfo[number_old_recordings + number_new_recordings];
          for (int i = 0; i < number_old_recordings; i++)
               temp_recording_list[i] = recording_list[i];
          for (int i = 0; i < number_new_recordings; i++)
               temp_recording_list[i + number_old_recordings] = recording_info[i];
          
          // Remove duplicate entries in the recording_list with the same file path
          for (int i = 0; i < temp_recording_list.length - 1; i++)
               if (temp_recording_list[i] != null)
               {
               String current_path = temp_recording_list[i].file_path;
               for (int j = i + 1; j < temp_recording_list.length; j++)
                    if (temp_recording_list[j] != null)
                         if (current_path.equals( temp_recording_list[j].file_path ))
                              temp_recording_list[j] = null;
               }
          
          // Remove null entries in recording_list due to invalid files or
          // duplicate file names
          Object[] results = mckay.utilities.staticlibraries.ArrayMethods.removeNullEntriesFromArray(temp_recording_list);
          if (results != null)
          {
               recording_list = new RecordingInfo[results.length];
               for (int i = 0; i < results.length; i++)
                    recording_list[i] = (RecordingInfo) results[i];
          }
          
          // Update the table to display the new recording_list
          recordings_table_model.fillTable(recording_list);
     }
     
     
     /**
      * Calls the appropriate methods when the buttons are pressed.
      *
      * @param	event		The event that is to be reacted to.
      */
     public void actionPerformed(ActionEvent event)
     {
          // React to the add_recordings_button
          if (event.getSource().equals(add_recordings_button))
               addRecordings();
          
          // React to the delete_recordings_button
          else if (event.getSource().equals(delete_recordings_button))
               deleteRecordings();
          
          // React to the view_recording_information_button
          else if (event.getSource().equals(view_recording_information_button))
               viewRecordingInformation();
          
          // React to the play_recording_directly_button
          else if (event.getSource().equals(play_recording_directly_button))
               playRecordingDirectly();
          
          // React to the stop_playback_button
          else if (event.getSource().equals(stop_playback_button))
               stopMIDIPlayback();
     }
     
     
     /* PRIVATE METHODS *******************************************************/
     
     
     /**
      * Instantiates a JFileChooser for the load_recording_chooser field if
      * one does not already exist. This dialog box allows the user to choose
      * one or more files to add to the recording_list list of references to
      * MIDI file and display the added files on the recordings_table.
      *
      * <p>Only MIDI files of known types are displayed in the file chooser.
      *
      * <p>Verifies that the files are valid MIDI files that can be read if the
      * validate_recordings_when_load_them_check_box checkbox is selected. Only
      * stores sequences if the store_symbolic_stream_check_box check box is
      * selected (otherwise just stores file references).
      *
      * <p>If a selected file path corresponds to a file that does not exist,
      * then an error message is displayed.
      */
     private void addRecordings()
     {
          // Initialize the load_recording_chooser if it has not been opened yet
          if (load_recording_chooser == null)
          {
               load_recording_chooser = new JFileChooser();
               load_recording_chooser.setCurrentDirectory(new File("."));
               String[] allowed_extensions = {"mid", "midi","mei"};
               load_recording_chooser.setFileFilter(new mckay.utilities.general.FileFilterImplementation(allowed_extensions));
               load_recording_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
               load_recording_chooser.setMultiSelectionEnabled(true);
          }
          
          // Read the user's choice of load or cancel
          int dialog_result = load_recording_chooser.showOpenDialog(RecordingSelectorPanel.this);
          
          // Add the files to the table and to recording_list
          if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
          {
               File[] load_files = load_recording_chooser.getSelectedFiles();
               addRecordings(load_files);
          }
     }
     
     
     /**
      * Removes all rows selected from the table display as well as from
      * the recording_list field.
      */
     private void deleteRecordings()
     {
          int[] selected_rows = recordings_table.getSelectedRows();
          for (int i = 0; i < selected_rows.length; i++)
               recording_list[selected_rows[i]] = null;
          Object[] results = mckay.utilities.staticlibraries.ArrayMethods.removeNullEntriesFromArray(recording_list);
          if (results != null)
          {
               recording_list = new RecordingInfo[results.length];
               for (int i = 0; i < results.length; i++)
                    recording_list[i] = (RecordingInfo) results[i];
               recordings_table_model.fillTable(recording_list);
          }
          else
          {
               recording_list = null;
               recordings_table_model.clearTable();
          }
     }
     
     
     /**
      * Displays MIDI encoding information about each selected files.
      */
     private void viewRecordingInformation()
     {
          int[] selected_rows = recordings_table.getSelectedRows();
          for (int i = 0; i < selected_rows.length; i++)
          {
               try
               {
                    File file = new File(recording_list[ selected_rows[i] ].file_path);
                    String data = mckay.utilities.sound.midi.MIDIMethods.getMIDIFileFormatData(file);
                    JOptionPane.showMessageDialog(null, data, "FILE INFORMATION", JOptionPane.INFORMATION_MESSAGE);
               }
               catch (Exception e)
               {
                    String message = "Could not display file information for file " +
                         recording_list[ selected_rows[i] ].file_path + "\n" + e.getMessage();
                    JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
               }
          }
     }
     
     
     /**
      * Plays the selected MIDI file directly from the file referred to by the
      * entry on the recordings_table. If multiple files are selected, plays
      * only the first one. Any previous playback is stopped.
      */
     private void playRecordingDirectly()
     {
          try
          {
               // Get the file selected for playback
               int selected_row = recordings_table.getSelectedRow();
               if (selected_row < 0)
                    throw new Exception("No file selcected for playback.");
               File play_file = new File(recording_list[selected_row].file_path);
               
               // Load the file into a MIDI Sequence object
               List<String> errorLog = new ArrayList<>();
               Sequence midi_sequence = FileValidator.getValidSequence(play_file, errorLog);
               
               // Perform playback
               if (midi_sequencer != null)
                    stopMIDIPlayback();
               midi_sequencer = mckay.utilities.sound.midi.MIDIMethods.playMIDISequence(midi_sequence);
          }
          catch (Exception e)
          {
               JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
          }
     }
     
     
     /**
      * Stop any playback of a MIDI file in progress.
      */
     private void stopMIDIPlayback()
     {
          if (midi_sequencer != null)
          {
               midi_sequencer.stop();
               midi_sequencer = null;
          }
     }
     
     
     /**
      * Initialize the table displaying files whose references have been loaded.
      */
     private void setUpRecordingListTable()
     {
          // Remove anything on the left side of the panel
          if (recordings_table != null)
               remove(recordings_table);
          
          // Initialize recordings_table_model and recordings_table
          Object[] column_names = { new String("Name"),
          new String("Path") };
          int number_recordings = 0;
          if (recording_list != null)
               number_recordings = recording_list.length;
          recordings_table_model = new RecordingsTableModel(column_names, number_recordings);
          recordings_table_model.fillTable(recording_list);
          recordings_table = new JTable(recordings_table_model);
          
          // Set up and display the table
          recordings_scroll_pane = new JScrollPane(recordings_table);
          recordings_panel = new JPanel(new GridLayout(1, 1));
          recordings_panel.add(recordings_scroll_pane);
          add(recordings_panel, BorderLayout.CENTER);
          recordings_table_model.fireTableDataChanged();
          repaint();
          outer_frame.repaint();
     }
}