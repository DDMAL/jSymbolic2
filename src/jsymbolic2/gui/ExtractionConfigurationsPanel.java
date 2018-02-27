package jsymbolic2.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jsymbolic2.configuration.*;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileWriterTxtImpl;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.FeatureExtractionJobProcessor;
import jsymbolic2.processing.SymbolicMusicFileUtilities;
import mckay.utilities.staticlibraries.FileMethods;
import mckay.utilities.staticlibraries.StringMethods;

/**
 * A GUI panel that allows the user to specify all feature extraction and configuration file settings other
 * than the particular symbolic files from which features are to be extracted (this is set in the
 * MusicFileSelectorPanel) and the particular features that are to be extracted and saved from them (this is
 * set in the FeatureSelectorPanel). The user may use buttons on this panel to initiate feature extraction and
 * saving, to save the settings currently entered on the GUI in a configuration file, or to load a new GUI
 * based on the contents of a configuration file.
 * 
 * <p>Brings up a file chooser dialog box that allows the user to load the contents of a specified jSymbolic
 * configuration settings file into the GUI. Note that all input symbolic music file paths specified in the
 * input config file should ideally exist and be valid. Note also that any settings currently entered on the
 * GUI will be lost.</p>
 * 
 * <p>The Save These Settings to a Configuration File button brings up a file chooser dialog that allows the 
 * user to select a path to save a configuration settings file holding all the settings currently entered on
 * the GUI. Note that this file must be saved with a .txt extension (it will be given one automatically if one
 * is not specified). Note that ACE XML save paths are only saved in the resultant config file if input
 * symbolic music files have been loaded onto the the music file selector table (config files must contain
 * both of these elements or neither). The default path specified in the file chooser dialog box will be a 
 * file in the jSymbolic working directory called jSymbolicDefaultConfigs.txt (jSymbolic looks for such a file
 * at startup, and automatically loads settings from it). Alternative save paths may be used, of course, and 
 * internal default settings are used if such a file is not available at startup.</p>
 * 
 * <p>The Extract Features from Files in Their Entirety radio button causes features to only be extracted from
 * symbolic files in their entirety (i.e. without windowing). Selecting this option will grey out the Window
 * Duration and Window Overlap text areas.</p>
 * 
 * <p>The Extract Features from Windows radio button causes features to be extracted from windows only (i.e. 
 * not from files in their entirety). Each window will have a duration equal to the number of seconds
 * specified in the Window Duration text area, and the windows will have a factional overlap specified in the 
 * Window Overlap Fraction text area (a value of 0 indicates no overlap). For example, for a window duration 
 * value of 10 and a window overlap fraction value of 0.1, the windows will be from 0 sec to 10 sec, 9 sec to
 * 19 sec, etc.</p>
 * 
 * <p>The Set ACE XML Feature Values Save Path button brings up a dialog box allowing the user to select a
 * path to save extracted feature values to as an ACE XML Feature Values file after feature extraction is 
 * complete. This path must have a .xml extension (it will automatically be given one if one is not 
 * specified). The selected path will automatically be entered in the text area to the right of this button.
 * Users may enter or change the save path directly in this text box if they prefer, rather than using the 
 * button.</p>
 * 
 * <p>The Set ACE XML Feature Definitions Save Path button brings up a dialog box allowing the user to select
 * a path to save extracted feature definitions to as an ACE XML Feature Definition file after feature
 * extraction is complete. This path must have a .xml extension (it will automatically be given one if one is 
 * not specified). The selected path will be automatically entered in the text area to the right of this
 * button. Users may enter or change the save path directly in this text box if they prefer, rather than using 
 * the button.</p>
 * 
 * <p>The Also Save Features in a Weka ARFF File check box will, if selected, cause features to be saved as a
 * Weka ARFF file after feature extraction is complete (features will still also be saved as ACE XML Feature
 * Values file as well). The path of this ARFF file will be the same as that entered in the Feature Values
 * File text area, but the extension will be changed from .xml to .arff.</p>
 * 
 * <p>The Also Save Features in a CSV File check box will, if selected, cause features to be saved as a
 * CSV file after feature extraction is complete (features will still also be saved as ACE XML Feature
 * Values File as well). The path of this CSV file will be the same as that entered in the Feature Values
 * file text area, but the extension will be changed from .xml to .csv.</p>
 * 
 * <p>The EXTRACT AND SAVE FEATURES button will cause feature extraction to be carried out (and features 
 * saved) based on the settings specified on all components of the GUI. The PROCESSING INFORMATION and ERROR
 * REPORTS text areas will be updated to indicate progress. A beep will sound and a confirmation dialog box
 * will appear when feature extraction is complete (the GUI settings may not be changed while feature 
 * extraction is occurring).</p>
 * 
 * <p>This class also includes an internal enum (DefaultSettingsEnum) that specifies the default settings for
 * all the above GUI components. These are used if the GUI is instantiated without a configuration file.</p>
 * 
 * @author Cory McKay and Tristano Tenaglia
 */
public class ExtractionConfigurationsPanel
	extends JPanel
	implements ActionListener
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * Holds a reference to the JFrame that holds this ExtractionConfigurationsPanel object.
	 */
	private final OuterFrame outer_frame;
	
	/**
	 * A text are where the user can indicate the duration of each window to use during windowed feature
	 * extraction.
	 */
	private JTextArea window_length_text_area;

	/**
	 * A text are where the user can indicate the window overlap fraction to use during windowed feature
	 * extraction.
	 */
	private JTextArea window_overlap_fraction_text_area;
	
	/**
	 * A text area where the user can indicate the path to which feature values are to be saved in the form of
	 * an ACE XML feature values file after feature extraction is complete.
	 */
	private JTextArea feature_values_save_path_text_area;

	/**
	 * A text area where the user can indicate the path to which feature metadata is to be saved in the form of
	 * an ACE XML feature definitions file after feature extraction is complete.
	 */
	private JTextArea feature_definitions_save_path_text_area;

	/**
	 * A radio button indicating that features are to be saved for each file as a whole (i.e. not for
	 * individual windows).
	 */
	private JRadioButton save_overall_features_only_radio_button;

	/**
	 * A radio button indicating that input files are to be subdivided into windows, and that features are to
	 * be extracted from each window individually (and not for files as a whole).
	 */
	private JRadioButton save_windowed_features_only_radio_button;

	/**
	 * A check box indicating whether extracted feature values are to be saved as Weka ARFF files (as well as
	 * ACE XML files).
	 */
	private JCheckBox save_as_weka_arff_check_box;
	
	/**
	 * A check box indicating whether extracted feature values are to be saved as CSV files (as well as ACE
	 * XML files).
	 */
	private JCheckBox save_as_csv_check_box;

	/**
	 * A button that brings up a dialog box allowing the user to select the path to load a new configuration
	 * file from.
	 */
	private JButton load_configuration_file_button;

	/**
	 * A button that brings up a dialog box allowing the user to select the path to save new configuration
	 * files to.
	 */
	private JButton save_configuration_file_button;

	/**
	 * A button that brings up a dialog box allowing the user to select the path to which feature values are
	 * to be saved (in the form of an ACE XML feature values file) after feature extraction is complete.
	 */
	private JButton feature_values_save_path_button;
	
	/**
	 * A button that brings up a dialog box allowing the user to select the path to which feature metadata is
	 * to be saved (in the form of an ACE XML feature definitions file) after feature extraction is complete.
	 */
	private JButton feature_definitions_save_path_button;
	
	/**
	 * A button initiating feature extraction and saving.
	 */
	private JButton extract_features_button;

	/**
	 * A dialog box allowing the user to choose paths for loading various types of files.
	 */
	private JFileChooser load_file_chooser;

	/**
	 * A dialog box allowing the user to choose paths for saving various types of files.
	 */
	private JFileChooser save_file_chooser;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Set up this panel.
	 *
	 * @param outer_frame				The JFrame element that contains this object.
	 * @param configuration_file_data	The configuration file data that could be used to set up this panel's
	 *									initial settings. If it is null then the default settings are used.
	 */
	public ExtractionConfigurationsPanel( OuterFrame outer_frame,
	                                      ConfigurationFileData configuration_file_data )
	{
		// Store a reference to the containing JFrame
		this.outer_frame = outer_frame;

		// Set the JFileChoosers to null initially
		load_file_chooser = null;
		save_file_chooser = null;

		// Initialize GUI component fields based on configuration file data if available, and based on
		// default settings if not
		initializeGuiComponentFields(configuration_file_data);

		// Set up and lay out the sub-JPanels and various JLabels that will be displayed on this
		// ExtractionConfigurations panel. Add all GUI components held in this class' fields to these
		// component JPanels.
		initializePanelsAndLabels();
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Calls the appropriate method when one of the JButtons or JRadioButtons on this JPanel is pressed.
	 *
	 * @param event	The button-triggered event that is to be reacted to.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource().equals(save_overall_features_only_radio_button))
		{
			window_length_text_area.setEnabled(false);
			window_overlap_fraction_text_area.setEnabled(false);
		}
		else if (event.getSource().equals(save_windowed_features_only_radio_button))
		{
			window_length_text_area.setEnabled(true);
			window_overlap_fraction_text_area.setEnabled(true);
		}
		else if (event.getSource().equals(load_configuration_file_button))
			browseAndLoadConfigurationFile();
		else if (event.getSource().equals(save_configuration_file_button))
			browseAndSaveConfigurationFile();
		else if (event.getSource().equals(feature_values_save_path_button))
			browseFeatureValuesSavePath();
		else if (event.getSource().equals(feature_definitions_save_path_button))
			browseFeatureDefinitionsSavePath();
		else if (event.getSource().equals(extract_features_button))
			extractAndSaveFeatures();
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Initialize this class' GUI component fields based on given information parsed from a configuration
	 * file or, if such information is unavailable, based on default settings.
	 *
	 * @param configuration_file_data	The configuration file data that will be used in setting up the GUI
	 *									components. Null if no configuration settings have been parsed.
	 */
	private void initializeGuiComponentFields(ConfigurationFileData configuration_file_data)
	{
		// Set up buttons associated with configuration files
		load_configuration_file_button = new JButton("Load New Settings from a Config File");
		load_configuration_file_button.addActionListener(this);
		save_configuration_file_button = new JButton("Save These Settings to a Config File");
		save_configuration_file_button.addActionListener(this);

		// Set up the overall file vs. windowed extraction radio buttons and join them into a group
		boolean save_overall_features_only = Boolean.parseBoolean(DefaultSettingsEnum.default_save_overall_features_only.toString());
		boolean save_windowed_features_only = Boolean.parseBoolean(DefaultSettingsEnum.default_save_windowed_features.toString());
		if (configuration_file_data != null)
		{
			save_overall_features_only = configuration_file_data.saveOverall();
			save_windowed_features_only = configuration_file_data.saveWindow();
		}
		save_overall_features_only_radio_button = new JRadioButton( "Extract Features from Entire Files",
																	save_overall_features_only );
		save_windowed_features_only_radio_button = new JRadioButton( "Extract Features from Windows",
																	 save_windowed_features_only );
		ButtonGroup windowing_button_group = new ButtonGroup();
		windowing_button_group.add(save_overall_features_only_radio_button);
		windowing_button_group.add(save_windowed_features_only_radio_button);
		
		// Set up text areas associated with windowed extraction
		String window_length = DefaultSettingsEnum.default_window_length.toString();
		String window_overlap_fraction = DefaultSettingsEnum.default_window_overlap.toString();
		if (configuration_file_data != null)
		{
			window_length = Double.toString(configuration_file_data.getWindowSize());
			window_overlap_fraction = Double.toString(configuration_file_data.getWindowOverlap());
		}
		window_length_text_area = new JTextArea();
		window_length_text_area.setText(window_length);
		window_overlap_fraction_text_area = new JTextArea();
		window_overlap_fraction_text_area.setText(window_overlap_fraction);
		
		// Add ActionListener to windowing radio buttons to grey out window settings if overall option is
		// selected. Also set the window-related text areas to be greyed out if the current settings are set 
		// to only extract overall features.
		save_overall_features_only_radio_button.addActionListener(this);
		save_windowed_features_only_radio_button.addActionListener(this);
		if(save_overall_features_only)
		{
			window_length_text_area.setEnabled(false);
			window_overlap_fraction_text_area.setEnabled(false);
		}
		
		// Set the default ACE XML save paths and associated buttons and text areas
		String feature_values_save_path = DefaultSettingsEnum.default_feature_values_save_path.toString();
		String feature_definition_save_path = DefaultSettingsEnum.default_feature_definition_save_path.toString();
		if (configuration_file_data != null && configuration_file_data.getOutputFileList() != null)
		{
			feature_values_save_path = configuration_file_data.getFeatureValueSavePath();
			feature_definition_save_path = configuration_file_data.getFeatureDefinitionSavePath();
		}
		feature_values_save_path_button = new JButton("Set ACE XML Feature Values Save Path:");
		feature_values_save_path_button.addActionListener(this);
		feature_values_save_path_text_area = new JTextArea();
		feature_values_save_path_text_area.setText(feature_values_save_path);
		feature_definitions_save_path_button = new JButton("Set ACE XML Feature Definitions Save Path:");
		feature_definitions_save_path_button.addActionListener(this);
		feature_definitions_save_path_text_area = new JTextArea();
		feature_definitions_save_path_text_area.setText(feature_definition_save_path);
		
		// Set up buttons and text areas associated with saving CSV and ARFF files
		boolean save_as_weka_arff = Boolean.parseBoolean(DefaultSettingsEnum.default_convert_arff.toString());
		boolean save_as_csv = Boolean.parseBoolean(DefaultSettingsEnum.default_convert_csv.toString());
		if (configuration_file_data != null)
		{
			save_as_weka_arff = configuration_file_data.convertToArff();
			save_as_csv = configuration_file_data.convertToCsv();
		}
		save_as_weka_arff_check_box = new JCheckBox( "Also Save Features in a Weka ARFF File",
		                                             save_as_weka_arff );
		save_as_csv_check_box = new JCheckBox( "Also Save Features in a CSV File",
		                                       save_as_csv );

		// Set up the extract_features button
		extract_features_button = new JButton("EXTRACT AND SAVE FEATURES");
		extract_features_button.setForeground(Color.RED);
		extract_features_button.addActionListener(this);
	}
	
	
	/**
	 * Set up and lay out the sub-JPanels and various JLabels that will be displayed on this
	 * ExtractionConfigurations panel. Add all GUI components held in this class' fields to these component
	 * JPanels. This method must be called only after the initializeGuiComponentFields method has already
	 * been called.
	 */
	private void initializePanelsAndLabels()
	{
		// Prepare this JPanel's basic layout settings
		int horizontal_gap = OuterFrame.HORIZONTAL_GAP; // horizontal space between GUI elements
		int vertical_gap = OuterFrame.VERTICAL_GAP; // horizontal space between GUI elements
		setLayout(new GridLayout(1, 2, horizontal_gap, vertical_gap));
		
		// Set up the config_file_and_windowing_super_panel JPanel
		JPanel config_file_and_windowing_super_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		OuterFrame.addFormattedBorder(config_file_and_windowing_super_panel);
		JLabel config_file_and_windowing_panel_label = new JLabel("CONFIGURATION FILE AND WINDOWING SETTINGS");
		OuterFrame.formatLabel(config_file_and_windowing_panel_label);
		config_file_and_windowing_super_panel.add(config_file_and_windowing_panel_label, BorderLayout.NORTH);
		JPanel config_file_and_windowing_panel = new JPanel(new GridLayout(4, 2, horizontal_gap, vertical_gap));
		config_file_and_windowing_panel.add(load_configuration_file_button);
		config_file_and_windowing_panel.add(save_configuration_file_button);
		config_file_and_windowing_panel.add(save_overall_features_only_radio_button);
		config_file_and_windowing_panel.add(save_windowed_features_only_radio_button);
		config_file_and_windowing_panel.add(new JLabel("Window Duration (seconds):"));
		config_file_and_windowing_panel.add(window_length_text_area);
		config_file_and_windowing_panel.add(new JLabel("Window Overlap Fraction (0.0 to 1.0):"));
		config_file_and_windowing_panel.add(window_overlap_fraction_text_area);
		config_file_and_windowing_super_panel.add(config_file_and_windowing_panel, BorderLayout.CENTER);
		add(config_file_and_windowing_super_panel);
		
		// Set up the save_settings_and_extraction_super_panel JPanel
		JPanel save_settings_and_extraction_super_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		OuterFrame.addFormattedBorder(save_settings_and_extraction_super_panel);
		JLabel save_settings_and_extraction_panel_label = new JLabel("FEATURE EXTRACTION AND SAVING SETTINGS");
		OuterFrame.formatLabel(save_settings_and_extraction_panel_label);
		save_settings_and_extraction_super_panel.add(save_settings_and_extraction_panel_label, BorderLayout.NORTH);
		JPanel save_settings_and_extraction_panel = new JPanel(new GridLayout(4, 2, horizontal_gap, vertical_gap));
		save_settings_and_extraction_panel.add(feature_values_save_path_button);
		save_settings_and_extraction_panel.add(feature_values_save_path_text_area);
		save_settings_and_extraction_panel.add(feature_definitions_save_path_button);
		save_settings_and_extraction_panel.add(feature_definitions_save_path_text_area);
		save_settings_and_extraction_panel.add(save_as_weka_arff_check_box);
		save_settings_and_extraction_panel.add(save_as_csv_check_box);
		save_settings_and_extraction_panel.add(new JLabel(""));
		save_settings_and_extraction_panel.add(extract_features_button);
		save_settings_and_extraction_super_panel.add(save_settings_and_extraction_panel, BorderLayout.CENTER);
		add(save_settings_and_extraction_super_panel);
	}
	
	
	/**
	 * Allow the user to use the load_file_chooser JFileChooser to select a jSymbolic2 configuration file to
	 * load settings from. If the user proceeds, then the current settings are disposed of and a new 
	 * jSymbolic instance is created based on the settings contained in the configuration file. Cancel the
	 * operation if the selected file is invalid.
	 */
	private void browseAndLoadConfigurationFile()
	{
		// Instantiate the load_file_chooser JFileChooser if it does not already exist
		if (load_file_chooser == null)
		{
			load_file_chooser = new JFileChooser();
			load_file_chooser.setCurrentDirectory(new File(DefaultSettingsEnum.default_jfilechooser_path.toString()));
			load_file_chooser.setFileFilter(new FileNameExtensionFilter("jSymbolic configuration settings txt file", "txt"));
		}
		
		// Process the user's entry
		int dialog_result = load_file_chooser.showOpenDialog(outer_frame);
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
		{
			// Note the file that the user chose
			String load_path = load_file_chooser.getSelectedFile().getPath();

			try
			{
				// Verify that the configuration file is valid (an exception is thrown if it isn't)
				(new ConfigurationFileValidatorTxtImpl()).parseConfigFileAllOrFeatOpt(load_path, outer_frame.error_print_stream);

				// Warn the user that s/he will lose all settings currently entered on the GUI if proceed
				int proceed = JOptionPane.showConfirmDialog( outer_frame,
															 "This will cause you to lose all settings currently entered in the GUI.\nDo you wish to proceed?",
															 "Warning",
															 JOptionPane.YES_NO_OPTION );
			
				// Run a new jSymbolic instance and dispose of this one if the user decides to proceed
				if (proceed == JOptionPane.YES_OPTION)
				{
						String[] args = {"-configgui", load_path};
						jsymbolic2.Main.main(args);
						outer_frame.dispose();
				}
			}
			catch (Exception e)
			{
				String text = "An error occurred while attempting to load a configuration file.\n" + 
				              "Details: " + e.getMessage() + "\n" + 
				              "Operation cancelled.";
				JOptionPane.showMessageDialog( outer_frame,
											   StringMethods.wrapString(text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, OuterFrame.DIALOG_BOX_HANGING_INDENT_CHARS),
											   "Error",
											   JOptionPane.ERROR_MESSAGE );
			}
		}
	}
	
	
	/**
	 * Save a jSymbolic configuration file based on the settings currently displayed on the GUI to a location
	 * selected by the user with a JFileChooser. This save file path is automatically given a txt extension.
	 * Shows an error dialog box if no features are set to be extracted, or if some kind of error is detected
	 * during the configuration file saving process. An informative dialog box is displayed if the
	 * configuration file is saved successfully.
	 */
	private void browseAndSaveConfigurationFile()
	{
		try
		{
			// Verify correct settings are currently entered on the GUI
			if (outer_frame.music_file_selector_panel.getSymbolicMusicFilesToExtractFeaturesFrom() == null)
				validateSettingsEnteredOnGui(true, false, false, true);
			else
				validateSettingsEnteredOnGui(true, true, true, true);
			
			// Get the user to enter the save path for the configuration file
			String configuration_save_path = chooseSavePath("txt", "jSymbolic configuration settings txt file");
			
			// If the user did not cancel the operation
			if (configuration_save_path != null)
			{
				// Read the music_files_to_extract_features_from from which to extract features from the GUI
				SymbolicMusicFile[] music_files_to_extract_features_from = outer_frame.music_file_selector_panel.getSymbolicMusicFilesToExtractFeaturesFrom();

				// Read the names of features to extract from the GUI
				List<String> names_of_features_to_save = outer_frame.feature_selector_panel.getNamesOfFeaturesToSave();

				// Read basic configuration settings from the GUI
				boolean save_overall_music_file_features = save_overall_features_only_radio_button.isSelected();
				boolean save_features_for_each_window = save_windowed_features_only_radio_button.isSelected();
				double window_size = Double.parseDouble(window_length_text_area.getText());
				double window_overlap = Double.parseDouble(window_overlap_fraction_text_area.getText());
				boolean convert_to_arff = save_as_weka_arff_check_box.isSelected();
				boolean convert_to_csv = save_as_csv_check_box.isSelected();

				// Read the feature save paths from the GUI
				String feature_values_save_path = feature_values_save_path_text_area.getText();
				String feature_definitions_save_path = feature_definitions_save_path_text_area.getText();

				// Prepare configuration settings as data to be saved in a configuration file
				ConfigurationOptionState basic_config_settings_from_gui = new ConfigurationOptionState( window_size,
																										window_overlap,
																										save_features_for_each_window,
																										save_overall_music_file_features,
																										convert_to_arff,
																										convert_to_csv );
				ConfigurationOutputFiles feature_save_path_config_settings_from_gui = new ConfigurationOutputFiles( feature_values_save_path,
																													feature_definitions_save_path);

				// Save the configuration file
				ConfigurationFileData combined_config_settings_from_gui;
				if (music_files_to_extract_features_from != null) // If input music_files_to_extract_features_from are specified on the GUI
					combined_config_settings_from_gui = writeCompleteConfigurationFile( configuration_save_path,
					                                                                    music_files_to_extract_features_from,
					                                                                    names_of_features_to_save,
					                                                                    basic_config_settings_from_gui,
					                                                                    feature_save_path_config_settings_from_gui );

				// If no input music_files_to_extract_features_from are specified on the GUI
				else
					combined_config_settings_from_gui = writeConfigurationFileWithoutReadOrSavePaths( configuration_save_path,
																									  names_of_features_to_save,
																									  basic_config_settings_from_gui );

				// Try test parsing the new configuration file to make sure that it is valid
				try {(new ConfigurationFileValidatorTxtImpl()).parseConfigFileAllOrFeatOpt(configuration_save_path, outer_frame.error_print_stream); }
				catch (Exception e) {throw new Exception("An invalid configuration settings file was saved. Although this file was in fact saved at the specified location (" + combined_config_settings_from_gui.getConfigurationFilePath() + "), it contains invalid settings, and jSymbolic will not be able tor read settings from it.");}
				
				// Show a dialob box indicating succes
				String confirmation_text = "Configuration file successfully saved to: " + combined_config_settings_from_gui.getConfigurationFilePath();
				JOptionPane.showMessageDialog( outer_frame,
				                               StringMethods.wrapString(confirmation_text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, OuterFrame.DIALOG_BOX_HANGING_INDENT_CHARS),
				                               "File Saved",
				                               JOptionPane.INFORMATION_MESSAGE );
			}
		}
		
		// Show a dialog box indicating an error
		catch (Exception e)
		{
			String text = "A problem was detected while attempting to save a configuration settings file.\n" + 
						  "Details: " + e.getMessage() + "\n" +
				          "Operation cancelled.";
			JOptionPane.showMessageDialog( outer_frame,
			                               StringMethods.wrapString(text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, OuterFrame.DIALOG_BOX_HANGING_INDENT_CHARS),
			                               "Error",
			                               JOptionPane.ERROR_MESSAGE );
		}
	}
	
	
	/**
	 * Allow the user to choose a save path for the Feature Values XML file to which features will be saved
	 * after feature extraction is performed. The selected path is entered in the
	 * feature_values_save_path_text_area.
	 */
	private void browseFeatureValuesSavePath()
	{
		String path = chooseSavePath("xml", "ACE XML feature values file");
		if (path != null)
			feature_values_save_path_text_area.setText(path);
	}

	
	/**
	 * Allow the user to choose a save path for the Feature Definitions XML file to which feature metadata
	 * will be saved after feature extraction is performed. The selected path is entered in the 
	 * feature_definitions_save_path_text_area.
	 */
	private void browseFeatureDefinitionsSavePath()
	{
		String path = chooseSavePath("xml", "ACE XML feature definitions file");
		if (path != null)
			feature_definitions_save_path_text_area.setText(path);
	}


	/**
	 * Allows the user to select or enter a file path using the JFileChooser referred to in this class'
	 * save_file_chooser field. If the selected path does not have the specified extension, then it is given
	 * this extension. If the chosen path refers to a file that already exists, then the user is asked if s/he
	 * wishes to overwrite the selected file. No file is actually saved or overwritten by this method. The
	 * selected path is simply returned.
	 *
	 * @param	extension				The extension that the selected file save path should have (e.g. 
	 *									"xml"). Note that this should NOT include a period before the
	 *									extension.
	 * @param	file_type_description	A description of the file type to be saved, which is shown in the
	 *									JFileChooser's file type filter box.
	 * @return							The path of the selected or entered file. A value of null is returned
	 *									if the user presses the cancel button or chooses not to overwrite a
	 *									file.
	 */
	private String chooseSavePath(String extension, String file_type_description)
	{
		// Instantiate the save_file_chooser JFileChooser if it does not already exist
		if (save_file_chooser == null)
		{
			save_file_chooser = new JFileChooser();
			save_file_chooser.setCurrentDirectory(new File(DefaultSettingsEnum.default_jfilechooser_path.toString()));
		}

		// Set the JFileChooser filter to only dislpay files of the appropriate type
		save_file_chooser.setFileFilter(new FileNameExtensionFilter(file_type_description, extension));
		
		// Set file name to the default config file name if nothing is entered
		if (extension.equals("txt") && save_file_chooser.getSelectedFile() == null)
			save_file_chooser.setSelectedFile(new File(DefaultSettingsEnum.default_configuration_file_save_path.toString()));
		
		// Process the user's entry
		String path = null;
		int dialog_result = save_file_chooser.showSaveDialog(outer_frame);
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
		{
			// Note the file that the user chose
			File to_save_to = save_file_chooser.getSelectedFile();

			// Make sure the file that the user chose has proper extension
			path = to_save_to.getPath();
			String ext = StringMethods.getExtension(path);
			if (ext == null)
			{
				path += "." + extension;
				to_save_to = new File(path);
			}
			else if (!ext.equals("." + extension))
			{
				path = StringMethods.removeExtension(path) + "." + extension;
				to_save_to = new File(path);
			}

			// See if user wishes to overwrite if a file with the same path exists
			if (to_save_to.exists())
			{
				int overwrite = JOptionPane.showConfirmDialog( outer_frame,
				                                               "A file at the specified path already exists.\nDo you wish to overwrite it?",
				                                               "Warning",
				                                               JOptionPane.YES_NO_OPTION );
				if (overwrite != JOptionPane.YES_OPTION)
					path = null;
			}
		}

		// Return the file path chosen by the user
		return path;
	}
	

	/**
	 * Extract and save the features selected on the FeatureSelectorPanel from all the symbolic music files
	 * selected on the MusicFileSelectorPanel. Use the extraction settings entered on this
	 * ExtractionConfigurationsPanel, and save the extracted features to the paths specified on this
	 * ExtractionConfigurationsPanel. The GUI text areas are updated as progress continues. Beeps when
	 * processing is finished and shows a dialog box indicating whether or not any problems occurred during
	 * processing.
	 */
	private void extractAndSaveFeatures()
	{
		// Perform processing
		try
		{
			// Verify the settings currently entered on the GUI
			validateSettingsEnteredOnGui(true, true, true, true);
			
			// Note the music fileds to extract features from from
			List<File> files_to_extract_features_from = new ArrayList<>();
			SymbolicMusicFile[] music_files_to_extract_features_from = outer_frame.music_file_selector_panel.getSymbolicMusicFilesToExtractFeaturesFrom();
			for (SymbolicMusicFile rec : music_files_to_extract_features_from)
				files_to_extract_features_from.add(new File(rec.file_path));

			// Note the features to extract and save
			boolean[] features_to_save = outer_frame.feature_selector_panel.getFeaturesToSave();

			// Clear the GUI display text areas
			outer_frame.clearTextAreas();

			// Extract and save features. Update the text areas as progress continues.
			List<String> error_log = FeatureExtractionJobProcessor.extractAndSaveSpecificFeatures(files_to_extract_features_from,
			                                                                                       SymbolicMusicFileUtilities.correctFileExtension(feature_values_save_path_text_area.getText(), "xml"),
			                                                                                       SymbolicMusicFileUtilities.correctFileExtension(feature_definitions_save_path_text_area.getText(), "xml"),
			                                                                                       features_to_save,
			                                                                                       save_windowed_features_only_radio_button.isSelected(),
			                                                                                       save_overall_features_only_radio_button.isSelected(),
			                                                                                       Double.parseDouble(window_length_text_area.getText()),
			                                                                                       Double.parseDouble(window_overlap_fraction_text_area.getText()),
			                                                                                       save_as_weka_arff_check_box.isSelected(),
			                                                                                       save_as_csv_check_box.isSelected(),
			                                                                                       outer_frame.status_print_stream,
			                                                                                       outer_frame.error_print_stream,
			                                                                                       true );

			// Beep when processing is finished and show a dialog box indicating whether any problems
			// occurred during processing.
			java.awt.Toolkit.getDefaultToolkit().beep();
			if (error_log.isEmpty())
				JOptionPane.showMessageDialog( outer_frame,
				                               "Feature extraction complete.\nNo errors encountered.",
				                               "Finished Extracting Features",
				                               JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog( outer_frame,
				                               "Feature processing complete with " + error_log.size() + " errors.\n" +
				                               "Features were still saved for any files that could be succesfully processed.\n" +
				                               "Features were not saved for files for which errors were reported.\n",
				                               "Finished Extracting Features",
				                               JOptionPane.WARNING_MESSAGE);
		}

		// Display an error dialog box if a problem is found with the GUI settings before processing occurs.
		catch (Exception e)
		{
			java.awt.Toolkit.getDefaultToolkit().beep();
			String text = "A problem with the settings selected on the GUI was detected.\n" + 
						  "Details: " + e.getMessage() + "\n" +
				          "Operation cancelled.";
			JOptionPane.showMessageDialog( outer_frame,
			                               StringMethods.wrapString(text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, 0),
			                               "Error",
			                               JOptionPane.ERROR_MESSAGE );
		}
	}
	
	
	/**
	 * Validates the settings entered on the GUI to make sure that they are legitimate. Throws an informative
	 * exception if they are not.
	 * 
	 * @param test_windowing	Test whether the window duration and overlap fraction settings are legitimate.
	 * @param test_save_paths	Tests whether the ACE XML feature values and feature definitions save paths
	 *							are legitimate.
	 * @param test_input_files	Tests whether the files selected for feature extraction are legitimate.
	 * @param test_features		Tests whether the features selected to be extracted and saved are legitimate.
	 * @throws Exception		Throws an informative exception if an incorrect setting on the GUI is 
	 *							detected.
	 */
	private void validateSettingsEnteredOnGui( boolean test_windowing,
	                                           boolean test_save_paths,
											   boolean test_input_files,
											   boolean test_features )
		throws Exception
	{
		// Test windowing settings
		if (test_windowing)
		{
			boolean window_length_is_numeric = window_length_text_area.getText().matches("-?\\d+(\\.\\d+)?");
			boolean window_overlap_is_numeric = window_overlap_fraction_text_area.getText().matches("-?\\d+(\\.\\d+)?");
			if (!window_length_is_numeric)
				throw new Exception( "Window Duration is set to " + window_length_text_area.getText() + ".\n" +
									 "This value must be be purely numeric.");
			if (!window_overlap_is_numeric)
				throw new Exception( "Window Overlap Fraction is set to " + window_overlap_fraction_text_area.getText() + ".\n" +
									 "This value must be be purely numeric.");			
			boolean save_features_for_each_window = save_windowed_features_only_radio_button.isSelected();
			double window_size = Double.parseDouble(window_length_text_area.getText());
			double window_overlap = Double.parseDouble(window_overlap_fraction_text_area.getText());
			if (save_features_for_each_window && window_size == 0.0)
				throw new Exception( "Window Duration is set to " + window_size + " seconds.\n" +
									 "This value must be be greater than 0.0 if windowed extraction is to be used.");
			if (window_size < 0.0)
				throw new Exception( "Window Duration is set to " + window_size + " seconds.\n" +
									 "This value must be 0.0 or above.");
			if (window_overlap < 0.0 || window_overlap > 1.0)
				throw new Exception( "Window Overlap Fraction is set to " + window_overlap + ".\n" +
									 "This value must be between 0.0 and 1.0.");
		}
		
		// Test save path settings
		if (test_save_paths)
		{
			FileMethods.verifyValidPath(feature_values_save_path_text_area.getText(), true, false, false, true, false, false);
			FileMethods.verifyValidPath(feature_definitions_save_path_text_area.getText(), true, false, false, true, false, false);
		}
		
		// Test input files from which features are to be extracted
		if (test_input_files)
		{
			SymbolicMusicFile[] music_files_to_extract_features_from = outer_frame.music_file_selector_panel.getSymbolicMusicFilesToExtractFeaturesFrom();
			if (music_files_to_extract_features_from == null)
				throw new Exception("No symbolic music files have been selected to extract features from.");
			for (SymbolicMusicFile ri : music_files_to_extract_features_from)
			{
				boolean valid_file = false;
				try {valid_file = FileMethods.validateFile(new File(ri.file_path), true, false);}
				catch (Exception e) {}
				if (!valid_file)
					throw new Exception( "The following specified input file path does not correspond to a valid existing readable file:" +
					                     "      " + ri.file_path);
			}
		}
		
		// Test features set to be extracted
		if (test_features)
		{
			boolean[] features_to_save = outer_frame.feature_selector_panel.getFeaturesToSave();
			boolean at_least_one_feature = false;
			for (int i = 0; i < features_to_save.length; i++)
				if (features_to_save[i])
					at_least_one_feature = true;
			if (!at_least_one_feature)
				throw new Exception("No features are selected to be extracted and saved.");
			SymbolicMusicFile[] music_files_to_extract_features_from = outer_frame.music_file_selector_panel.getSymbolicMusicFilesToExtractFeaturesFrom();
			if (music_files_to_extract_features_from != null)
				verifyNoMeiSpecificFeaturesAndNonMeiFiles(features_to_save, music_files_to_extract_features_from);
		}
	}
	
	
	/* PRIVATE STATIC METHODS *******************************************************************************/


	/**
	 * Verify that, based on the passed arguments, no MEI-specific features are set to be extracted from one 
	 * or more non-MEI files. Throw an exception if this is in fact the case.
	 *
	 * @param features_to_save		Which features are set to be saved (true if they are, false if they are 
	 *								not). Ordering is the same as that used by the FeatureExtractorAccess 
	 *								class and, equivalently, the order that the features are listed on the
	 *								FeatureSelectorPanel feature table.
	 * @param music_to_process		Each file from which features will be extracted if feature extraction is
	 *								begun.
	 * @throws Exception			An informative exception is thrown if an MEI-specific feature is set to be
	 *								extracted from a non-MEI file.
	 */
	private static void verifyNoMeiSpecificFeaturesAndNonMeiFiles( boolean[] features_to_save,
	                                                               SymbolicMusicFile[] music_to_process )
		throws Exception
	{
		List<String> names_of_features_to_save = FeatureExtractorAccess.getNamesOfFeaturesToExtract(features_to_save);
		List<String> names_of_mei_specific_features = FeatureExtractorAccess.getNamesOfMeiSpecificFeatures();
		boolean at_least_one_mei_specific_feature = false;
		String name_of_an_mei_specific_feature_marked_to_be_saved = null;
		for (String this_feature_name : names_of_features_to_save)
		{
			if (names_of_mei_specific_features.contains(this_feature_name))
			{
				at_least_one_mei_specific_feature = true;
				name_of_an_mei_specific_feature_marked_to_be_saved = this_feature_name;
				break;
			}
		}

		boolean at_least_one_non_mei_file = false;
		SymbolicMusicFile a_non_mei_file = null;
		for (SymbolicMusicFile this_music_file : music_to_process)
		{
			if ( SymbolicMusicFileUtilities.isValidMidiFile(new File(this_music_file.file_path)) )
			{
				at_least_one_non_mei_file = true;
				a_non_mei_file = this_music_file;
				break;
			}
		}

		if (at_least_one_mei_specific_feature && at_least_one_non_mei_file)
		{
			String file_path = "UNKNOWN";
			if (a_non_mei_file != null)
				file_path = a_non_mei_file.file_path;
			throw new Exception("Cannot extract MEI-specific features from non-MEI files.\n" +
					"Current settings have, for example, the following MEI-specific feature set to be extracted: " + 
					name_of_an_mei_specific_feature_marked_to_be_saved + ", and the following non-MEI file " +
					"included in the list of files to extract features from: " + file_path + ". " +
					"This is only a sampling, and there may be additional incompatible features and files as well. " +
					"Please only include MEI-specific features if features will only be extracted from MEI files exclusively.");
		}
	}
	
	
	/**
	 * Save a configuration file containing all the data specified.
	 *
	 * @param configuration_save_path				The path to which this configuration file is to be saved.
	 * @param music_to_extract_features_from		The symbolic music files designated to have features
	 *												extracted from them.
	 * @param names_of_features_to_save				The names of the features designated to be saved. Must
	 *												match the names of existing implemented jSymbolic
	 *												features.
	 * @param basic_config_settings					Basic general settings associated with feature extraction.
	 * @param feature_save_paths					Specified output files paths from the GUI.
	 * @return										The compiled information associated with the saved
	 *												configuration file and the data associated with it.
	 * @throws Exception							Throws an informative exception if the saving fails or if
	 *												there is a problem with the data itself.
	 */
	private static ConfigurationFileData writeCompleteConfigurationFile( String configuration_save_path,
	                                                                     SymbolicMusicFile[] music_to_extract_features_from,
	                                                                     List<String> names_of_features_to_save,
	                                                                     ConfigurationOptionState basic_config_settings,
	                                                                     ConfigurationOutputFiles feature_save_paths )
		throws Exception
	{
		// Collect the files marked to have features extracted from them
		ConfigurationInputFiles files_to_extract_features_from = new ConfigurationInputFiles();
		for (SymbolicMusicFile this_symbolic_music_file : music_to_extract_features_from)
			files_to_extract_features_from.addValidFile(new File(this_symbolic_music_file.file_path));

		// Format the data into configuration file format
		ConfigurationFileData configuration_file_data = new ConfigurationFileData( names_of_features_to_save,
		                                                                           basic_config_settings,
		                                                                           feature_save_paths,
		                                                                           configuration_save_path,
		                                                                           files_to_extract_features_from );
		
		// Save the configuration file to disk
		ConfigurationFileWriter writer = new ConfigurationFileWriterTxtImpl();
		writer.write(configuration_file_data, ConfigFileHeaderEnum.asList());
		
		// Return the configuration file data
		return configuration_file_data;
	}

	
	/**
	 * Save a configuration file containing all the data specified. Note that specific files to extract
	 * features from and paths to save extracted features to are neither taken by this method nor stored
	 * in the saved configuration file.
	 *
	 * @param configuration_save_path	The path to which this configuration file is to be saved.
	 * @param names_of_features_to_save	The names of the features designated to be saved. Must match the names
	 *									of existing implemented jSymbolic features.
	 * @param basic_config_settings		Basic general settings associated with feature extraction.
	 * @return							The compiled information associated with the saved configuration file 
	 *									and the data associated with it.
	 * @throws Exception				Throws an informative exception if the saving fails or if there is a 
	 *									problem with the data itself.
	 */
	private static ConfigurationFileData writeConfigurationFileWithoutReadOrSavePaths( String configuration_save_path,
	                                                                                   List<String> names_of_features_to_save,
	                                                                                   ConfigurationOptionState basic_config_settings )
		throws Exception
	{

		// Format the data into configuration file format
		ConfigurationFileData configuration_file_data = new ConfigurationFileData( names_of_features_to_save,
		                                                                           basic_config_settings,
		                                                                           null,
		                                                                           configuration_save_path,
		                                                                           null );

		// Save the configuration file to disk
		ConfigurationFileWriter writer = new ConfigurationFileWriterTxtImpl();
		List<ConfigFileHeaderEnum> feature_options = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER);
		writer.write(configuration_file_data, feature_options);
		
		// Return the configuration file data
		return configuration_file_data;
	}

	
	/* PRIVATE ENUMS ****************************************************************************************/


	/**
	 * An enum that stores the default settings to store in an ExtractionConfigurationsPanel if no 
	 * configuration is available.
	 */
	private enum DefaultSettingsEnum
	{
		default_configuration_file_save_path("./jSymbolicDefaultConfigs.txt"),
		default_save_overall_features_only("true"),
		default_save_windowed_features("false"),
		default_window_length("0.0"),
		default_window_overlap("0.0"),
		default_feature_values_save_path("./extracted_feature_values.xml"),
		default_feature_definition_save_path("./feature_definitions.xml"),
		default_convert_arff("true"),
		default_convert_csv("true"),
		default_jfilechooser_path(".");

		private final String data;

		DefaultSettingsEnum(String data)
		{
			this.data = data;
		}

		@Override
		public String toString()
		{
			return data;
		}
	}
}