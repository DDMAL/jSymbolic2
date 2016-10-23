/*
 * FeatureSelectorPanel.java
 * Version 2.0
 *
 * Last modified on June 24, 2010.
 * McGill University and the University of Waikato
 */

package jsymbolic2.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import ace.datatypes.FeatureDefinition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import jsymbolic2.configuration.*;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileWriterTxtImpl;
import jsymbolic2.datatypes.RecordingInfo;
import jsymbolic2.featureutils.FeatureConversion;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.AceXmlConverter;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.FileValidator;
import jsymbolic2.processing.MIDIFeatureProcessor;


/**
 * A window that allows users to select which features to save as well as some
 * basic parameters relating to these features. These parameters include the
 * window length to use for analyses and the amount of overlap between analysis
 * windows. The user may also see the features that can be extracted and some
 * details about them.
 * <p>The resulting feature values and the features used are saved to the
 * specified feature_vector_file and a feature_key_file respectively.
 * </p>
 * <p>Note that some features need other features in order to be extracted. Even
 * if a feature is not checked for saving, it will be extracted (but not saved)
 * if another feature that needs it is checked for saving.
 * </p>
 * <p>The table allows the user to view all features which are possible to
 * extract. The click box indicates whether this feature is to be saved during
 * feature extraction. The Dimensions indicate how many values are produced for
 * a given feature each time that it is extracted. Double clicking on a feature
 * brings up a window describing it.
 * </p>
 * <p>The Do Not Use Windows checkbox allows the user to decide whether features
 * are to be extracted for the recording overall or in individual windows. The
 * Save Features For Each Window and Save For Overall Recordings check boxes
 * apply only to windowed feature extraction. The former allows features to be
 * saved for each window and the latter causes the averages and standard
 * deviations of each feature across all windows in each recording to be saved.
 * </p>
 * <p>The Window Size indicates the duration in seconds of each window that
 * features are to be extracted for.
 * </p>
 * <p>The Window Size indicates the duration in seconds of each window that
 * features are to be extracted for.
 * </p>
 * <p>The Window Overlap indicates the fraction, from 0 to 1, of overlap between
 * adjacent analysis windows.
 * </p>
 * <p>The Feature Values Save Path and Feature Definitions Save Path allow the
 * user to choose what paths to save extracted feature values and feature
 * definitions respectively.
 * </p>
 * <p>The Extract Features button extracts all appropriate features and from the
 * loaded recordings, and saves the results to disk.
 * <p>The Save Configuration File buttons saves a configuration based on the
 * current configuration of the jSymbolic GUI.
 * </p>
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class FeatureSelectorPanel
        extends JPanel
        implements ActionListener {
     /* FIELDS ****************************************************************/


    /**
     * Holds a reference to the JPanel that holds objects of this class.
     */
    public OuterFrame outer_frame;

    /**
     * Holds references to all of the features that it's possible to extract.
     */
    private MIDIFeatureExtractor[] feature_extractors;

    /**
     * The default as to whether each feature is to be saved after feature
     * extraction. Indices correspond to those of feature_extractors.
     */
    private boolean[] feature_save_defaults;

    /**
     * GUI panels
     */
    private JPanel features_panel;
    private JScrollPane features_scroll_pane;

    /**
     * GUI table-related fields
     */
    private JTable features_table;
    private FeatureSelectorTableModel features_table_model;

    /**
     * GUI text areas
     */
    private JTextArea window_length_text_field;
    private JTextArea window_overlap_fraction_text_field;
    private JTextArea values_save_path_text_field;
    private JTextArea definitions_save_path_text_field;
    private JTextArea configuration_output_path;

    /**
     * GUI radio buttons
     */
    private JRadioButton save_window_features_radio_button;
    private JRadioButton save_overall_file_features_radio_button;

    /**
     * GUI check boxes
     */
    private JCheckBox weka_arff_check_box;
    private JCheckBox csv_check_box;

    /**
     * GUI buttons
     */
    private JButton values_save_path_button;
    private JButton definitions_save_path_button;
    private JButton extract_features_button;
    private JButton save_configuration_file_button;

    /**
     * GUI dialog boxes
     */
    private JFileChooser save_file_chooser;

     /* CONSTRUCTORS **********************************************************/


    /**
     * Set up frame.
     *
     * @param outer_frame The GUI element that contains this object.
     * @param configuration_file_data The configuration file data that could be used to set up this
     *                                panel with. If it is null then the default settings are used.
     */
    public FeatureSelectorPanel(OuterFrame outer_frame, ConfigurationFileData configuration_file_data) {
        // Store containing panel
        this.outer_frame = outer_frame;

        // Set the file chooser to null initially
        this.save_file_chooser = null;

        // General container preparations containers
        int horizontal_gap = Integer.parseInt(FeaturePanelEnum.horizontal_gap.toString()); // horizontal space between GUI elements
        int vertical_gap = Integer.parseInt(FeaturePanelEnum.vertical_gap.toString()); // horizontal space between GUI elements
        setLayout(new BorderLayout(horizontal_gap, vertical_gap));

        // Find the list of available feature extractors
        populateFeatureExtractors(configuration_file_data);

        // Set up the list of feature extractors
        setUpFeatureTable();

        // Add an overall title for this panel
        add(new JLabel("FEATURES:"), BorderLayout.NORTH);

        //Check if we need to setup with config file or not and do so
        if (configuration_file_data != null) {
            configurationSetup(configuration_file_data);
        } else {
            noConfigurationSetup();
        }

        // Cause the table to respond to double clicks
        addTableMouseListener();
    }

    /**
     * Setup the GUI based on the configuration_file_data.
     * @param configuration_file_data The configuration file data that will be setup on the GUI.
     */
    private void configurationSetup(ConfigurationFileData configuration_file_data) {
        //Setup data from configuration file
        boolean convertToArff = configuration_file_data.convertToArff();
        boolean convertToCsv = configuration_file_data.convertToCsv();
        boolean saveWindow = configuration_file_data.saveWindow();
        boolean saveOverall = configuration_file_data.saveOverall();
        String windowSize = Double.toString(configuration_file_data.getWindowSize());
        String windowOverlap = Double.toString(configuration_file_data.getWindowOverlap());

        String featureValue = FeaturePanelEnum.value_save_path_default.toString();
        String featureDefinition = FeaturePanelEnum.definition_save_path_default.toString();
        if (configuration_file_data.getOutputFileList() != null) {
            featureValue = configuration_file_data.getFeatureValueSavePath();
            featureDefinition = configuration_file_data.getFeatureDefinitionSavePath();
        }

        // General container preparations containers
        int horizontal_gap = Integer.parseInt(FeaturePanelEnum.horizontal_gap.toString()); // horizontal space between GUI elements
        int vertical_gap = Integer.parseInt(FeaturePanelEnum.vertical_gap.toString()); // horizontal space between GUI elements

        //Set up radio buttons in group
        save_window_features_radio_button = new JRadioButton(FeaturePanelEnum.save_window_label.toString(), saveWindow);
        save_overall_file_features_radio_button = new JRadioButton(FeaturePanelEnum.save_overall_label.toString(), saveOverall);
        ButtonGroup save_feature_group = new ButtonGroup();
        save_feature_group.add(save_overall_file_features_radio_button);
        save_feature_group.add(save_window_features_radio_button);

        // Set up buttons and text area
        JPanel control_panel = new JPanel(new GridLayout(8, 2, horizontal_gap, vertical_gap));
        control_panel.add(save_window_features_radio_button);
        weka_arff_check_box = new JCheckBox(FeaturePanelEnum.convert_arff_label.toString(), convertToArff);
        control_panel.add(weka_arff_check_box);
        control_panel.add(save_overall_file_features_radio_button);
        csv_check_box = new JCheckBox(FeaturePanelEnum.convert_csv_label.toString(), convertToCsv);
        control_panel.add(csv_check_box);
        control_panel.add(new JLabel(FeaturePanelEnum.window_length_label.toString()));
        window_length_text_field = new JTextArea(windowSize, 1, 20);
        control_panel.add(window_length_text_field);
        control_panel.add(new JLabel(FeaturePanelEnum.window_overlap_label.toString()));
        window_overlap_fraction_text_field = new JTextArea(windowOverlap, 1, 20);
        control_panel.add(window_overlap_fraction_text_field);
        values_save_path_button = new JButton(FeaturePanelEnum.value_save_path_label.toString());
        values_save_path_button.addActionListener(this);
        control_panel.add(values_save_path_button);
        values_save_path_text_field = new JTextArea(featureValue, 1, 20);
        control_panel.add(values_save_path_text_field);
        definitions_save_path_button = new JButton(FeaturePanelEnum.definition_save_path_label.toString());
        definitions_save_path_button.addActionListener(this);
        control_panel.add(definitions_save_path_button);
        definitions_save_path_text_field = new JTextArea(featureDefinition, 1, 20);
        control_panel.add(definitions_save_path_text_field);
        control_panel.add(new JLabel(""));
        extract_features_button = new JButton(FeaturePanelEnum.extract_feature_label.toString());
        extract_features_button.addActionListener(this);
        control_panel.add(extract_features_button);
        configuration_output_path = new JTextArea(FeaturePanelEnum.configuration_file_default.toString(), 1, 20);
        control_panel.add(configuration_output_path);
        save_configuration_file_button = new JButton(FeaturePanelEnum.configuration_file_label.toString());
        save_configuration_file_button.addActionListener(this);
        control_panel.add(save_configuration_file_button);
        add(control_panel, BorderLayout.SOUTH);
    }

    /**
     * Setup the default GUI.
     */
    private void noConfigurationSetup() {
        // General container preparations containers
        int horizontal_gap = Integer.parseInt(FeaturePanelEnum.horizontal_gap.toString()); // horizontal space between GUI elements
        int vertical_gap = Integer.parseInt(FeaturePanelEnum.vertical_gap.toString()); // horizontal space between GUI elements

        //Set up radio buttons in group
        save_window_features_radio_button =
                new JRadioButton(FeaturePanelEnum.save_window_label.toString(), Boolean.parseBoolean(FeaturePanelEnum.save_window_default.toString()));
        save_overall_file_features_radio_button =
                new JRadioButton(FeaturePanelEnum.save_overall_label.toString(), Boolean.parseBoolean(FeaturePanelEnum.save_overall_default.toString()));
        ButtonGroup save_feature_group = new ButtonGroup();
        save_feature_group.add(save_overall_file_features_radio_button);
        save_feature_group.add(save_window_features_radio_button);

        // Set up buttons and text area
        JPanel control_panel = new JPanel(new GridLayout(8, 2, horizontal_gap, vertical_gap));
        control_panel.add(save_window_features_radio_button);
        weka_arff_check_box = new JCheckBox(FeaturePanelEnum.convert_arff_label.toString(), Boolean.parseBoolean(FeaturePanelEnum.convert_arff_default.toString()));
        control_panel.add(weka_arff_check_box);
        control_panel.add(save_overall_file_features_radio_button);
        csv_check_box = new JCheckBox(FeaturePanelEnum.convert_csv_label.toString(), Boolean.parseBoolean(FeaturePanelEnum.convert_csv_default.toString()));
        control_panel.add(csv_check_box);
        control_panel.add(new JLabel(FeaturePanelEnum.window_length_label.toString()));
        window_length_text_field = new JTextArea(FeaturePanelEnum.window_length_default.toString(), 1, 20);
        control_panel.add(window_length_text_field);
        control_panel.add(new JLabel(FeaturePanelEnum.window_overlap_label.toString()));
        window_overlap_fraction_text_field = new JTextArea(FeaturePanelEnum.window_overlap_default.toString(), 1, 20);
        control_panel.add(window_overlap_fraction_text_field);
        values_save_path_button = new JButton(FeaturePanelEnum.value_save_path_label.toString());
        values_save_path_button.addActionListener(this);
        control_panel.add(values_save_path_button);
        values_save_path_text_field = new JTextArea(FeaturePanelEnum.value_save_path_default.toString(), 1, 20);
        control_panel.add(values_save_path_text_field);
        definitions_save_path_button = new JButton(FeaturePanelEnum.definition_save_path_label.toString());
        definitions_save_path_button.addActionListener(this);
        control_panel.add(definitions_save_path_button);
        definitions_save_path_text_field = new JTextArea(FeaturePanelEnum.definition_save_path_default.toString(), 1, 20);
        control_panel.add(definitions_save_path_text_field);
        control_panel.add(new JLabel(""));
        extract_features_button = new JButton(FeaturePanelEnum.extract_feature_label.toString());
        extract_features_button.addActionListener(this);
        control_panel.add(extract_features_button);
        configuration_output_path = new JTextArea(FeaturePanelEnum.configuration_file_default.toString(), 1, 20);
        control_panel.add(configuration_output_path);
        save_configuration_file_button = new JButton(FeaturePanelEnum.configuration_file_label.toString());
        save_configuration_file_button.addActionListener(this);
        control_panel.add(save_configuration_file_button);
        add(control_panel, BorderLayout.SOUTH);
    }
     

     /* PUBLIC METHODS ********************************************************/

    /**
     * Calls the appropriate methods when the buttons are pressed.
     *
     * @param event The event that is to be reacted to.
     */
    public void actionPerformed(ActionEvent event) {
        // React to the values_save_path_button
        if (event.getSource().equals(values_save_path_button))
            browseFeatureValuesSavePath();

            // React to the definitions_save_path_button
        else if (event.getSource().equals(definitions_save_path_button))
            browseFeatureDefinitionsSavePath();

            // React to the extract_features_button
        else if (event.getSource().equals(extract_features_button))
            extractFeatures();

        // React to the save_configuration_file_button button
        else if (event.getSource().equals(save_configuration_file_button)) {
            saveConfigurationFile();
        }
    }

     /* PRIVATE METHODS *******************************************************/

    /**
     * Allow the user to choose a save path for the feature_vector_file XML
     * file where feature values are to be saved. The selected path is entered
     * in the values_save_path_text_field.
     */
    private void browseFeatureValuesSavePath() {
        String path = chooseSavePath();
        if (path != null)
            values_save_path_text_field.setText(path);
    }


    /**
     * Allow the user to choose a save path for the feature_key_file XML
     * file where feature values are to be saved. The selected path is entered
     * in the definitions_save_path_text_field.
     */
    private void browseFeatureDefinitionsSavePath() {
        String path = chooseSavePath();
        if (path != null)
            definitions_save_path_text_field.setText(path);
    }

    /**
     * Helper method to get current features that need to be saved.
     *
     * @return features that need to be saved in the boolean array where the
     * array numbers correspond to the appropriate feature name
     * @throws Exception if no features have been selected
     */
    private boolean[] featuresToSave() throws Exception {
        boolean[] features_to_save = new boolean[feature_extractors.length];

        for (int i = 0; i < features_to_save.length; i++)
            features_to_save[i] = ((Boolean) features_table_model.getValueAt(i, 0)).booleanValue();

        return features_to_save;
    }

    /**
     * This is a helper method to save feature names to a configuration file from the GUI.
     * Given an array of booleans, an appropriate feature name list will be returned,
     * in the order used globally by FeatureExtractorAccess.
     *
     * @param featuresToSave The boolean array to check to save features from.
     * @return A List of the appropriate feature names to save.
     */
    private List<String> featureNamesToSave(boolean[] featuresToSave) {
        List<String> allFeatureList = FeatureExtractorAccess.getNamesOfAllImplementedFeatures();
        List<String> featureNamesToSave = new ArrayList<>();
        for (int i = 0; i < featuresToSave.length; i++) {
            if (featuresToSave[i] == true) {
                String featureName = allFeatureList.get(i);
                featureNamesToSave.add(featureName);
            }
        }
        return featureNamesToSave;
    }

    /**
     * Save configuration file according to the data input into the GUI.
     * This event occurs when save_configuration_file_button button is pressed
     * and the file name is saved as configuration_output_path.
     */
    private void saveConfigurationFile() {
        // Get the control parameters
        boolean save_features_for_each_window = save_window_features_radio_button.isSelected();
        boolean save_overall_recording_features = save_overall_file_features_radio_button.isSelected();
        String feature_values_save_path = values_save_path_text_field.getText();
        String feature_definitions_save_path = definitions_save_path_text_field.getText();
        double window_size = Double.parseDouble(window_length_text_field.getText());
        double window_overlap = Double.parseDouble(window_overlap_fraction_text_field.getText());
        String configuration_save_path = configuration_output_path.getText();
        boolean convert_to_arff = weka_arff_check_box.isSelected();
        boolean convert_to_csv = csv_check_box.isSelected();

        //Get the configuration format required to write configuration file
        ConfigurationOptionState optionState = new ConfigurationOptionState(
                window_size,
                window_overlap,
                save_features_for_each_window,
                save_overall_recording_features,
                convert_to_arff,
                convert_to_csv
        );
        ConfigurationOutputFiles outputFiles = new ConfigurationOutputFiles(feature_values_save_path, feature_definitions_save_path);
        try {
            // Get features that need to be saved
            RecordingInfo[] recordings = outer_frame.recording_selector_panel.recording_list;
            boolean[] features_to_save = featuresToSave();
            ConfigurationFileData configFileData;
            if(recordings == null) {
                configFileData = writeConfigFileNoIO(optionState, features_to_save, configuration_save_path);
            } else {
                //If there are input recordings to extract features from
                configFileData = writeConfigFileAll(optionState, features_to_save, outputFiles, recordings, configuration_save_path);
            }
            //On success
            JOptionPane.showConfirmDialog(null, "Configuration file successfully saved as " + configFileData.getConfigurationFilePath()
                    , "SUCCESS", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            //On failure
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.OK_OPTION);
        }
    }

    /**
     * Write out the configuration file with no input or output files specified.
     * @param optionState The option state of the configuration file.
     * @param configuration_save_path The configuration save path specified from the GUI.
     * @param features_to_save Correct ordering for features to save.
     * @return The configuration file data for further processing with input and output file objects
     * set to null.
     * @throws Exception Thrown if problems with the configuration file data reading/writing occurs.
     */
    private ConfigurationFileData writeConfigFileNoIO(ConfigurationOptionState optionState,
                                                      boolean[] features_to_save,
                                                      String configuration_save_path)
            throws Exception
    {
        List<String> featureNames = featureNamesToSave(features_to_save);
        ConfigurationFileData configFileData = new ConfigurationFileData(
                featureNames,
                optionState,
                null, //no output files
                configuration_save_path,
                null //no input files
        );
        ConfigurationFileWriter writer = new ConfigurationFileWriterTxtImpl();
        //Only want to write out feature names and options
        List<HeaderEnum> featureoptions = Arrays.asList(HeaderEnum.FEATURE_HEADER, HeaderEnum.OPTION_HEADER);
        writer.write(configFileData, featureoptions);
        return configFileData;
    }

    /**
     * Write out the configuration file with all headers specified.
     * @param optionState The option state of the configuration file.
     * @param outputFiles Specified output files paths from the GUI.
     * @param recordings The specified recording info form the GUI.
     * @param configuration_save_path THe configuration save path specified from the GUI.
     * @param features_to_save Correct ordering for features to save.
     * @return The configuration file data for further processing.
     * @throws Exception Thrown if problems with the configuration file data reading/writing occurs.
     */
    private ConfigurationFileData writeConfigFileAll(ConfigurationOptionState optionState,
                                                     boolean[] features_to_save,
                                                     ConfigurationOutputFiles outputFiles,
                                                     RecordingInfo[] recordings,
                                                     String configuration_save_path)
            throws Exception
    {
        ConfigurationInputFiles inputFiles = new ConfigurationInputFiles();
        for (RecordingInfo recording : recordings) {
            inputFiles.addValidFile(new File(recording.file_path));
        }

        //Setup data that needs to be in configuration file and write it out
        List<String> featureNames = featureNamesToSave(features_to_save);
        ConfigurationFileData configFileData = new ConfigurationFileData(
                featureNames,
                optionState,
                outputFiles,
                configuration_save_path,
                inputFiles
        );
        ConfigurationFileWriter writer = new ConfigurationFileWriterTxtImpl();
        writer.write(configFileData, HeaderEnum.asList());
        return configFileData;
    }

    /**
     * Extract the features from all of the files added in the GUI. Use the
     * features and feature settings entered in the GUI. Save the results in a
     * feature_vector_file and the features used in a feature_key_file.
     */
    private void extractFeatures() {
        // Get the control parameters
        boolean save_features_for_each_window = save_window_features_radio_button.isSelected();
        boolean save_overall_recording_features = save_overall_file_features_radio_button.isSelected();
        String feature_values_save_path =
                FileValidator.correctFileExtension(values_save_path_text_field.getText(), "xml");
        String feature_definitions_save_path =
                FileValidator.correctFileExtension(definitions_save_path_text_field.getText(), "xml");
        double window_size = Double.parseDouble(window_length_text_field.getText());
        double window_overlap = Double.parseDouble(window_overlap_fraction_text_field.getText());
        boolean csv_check = csv_check_box.isSelected();
        boolean arff_check = weka_arff_check_box.isSelected();

        try {
            // Get features that need to be saved
            RecordingInfo[] recordings = outer_frame.recording_selector_panel.recording_list;

            //Throw exception if there are no recordings to extract from
            if (recordings == null)
                throw new Exception("No recordings available to extract features from.");

            boolean[] features_to_save = featuresToSave();
            validateMEIFeatureFiles(features_to_save, recordings);

            // Prepare to extract features
            MIDIFeatureProcessor processor = new MIDIFeatureProcessor(window_size,
                    window_overlap,
                    feature_extractors,
                    features_to_save,
                    save_features_for_each_window,
                    save_overall_recording_features,
                    feature_values_save_path,
                    feature_definitions_save_path);

            // Extract features from recordings one by one and save them in XML files
            List<String> errorLog = new ArrayList<>();
            for (int i = 0; i < recordings.length; i++) {
                File load_file = new File(recordings[i].file_path);
                try {
                    processor.extractFeatures(load_file, errorLog);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    errorLog.add("Error found in : " + recordings[i].identifier + ". Error Message : " + ex.getMessage() + ".");
                }
            }
            // Finalize saved XML files
            processor.finalize();

            //Check for other csv or arff output here
            try {
                AceXmlConverter.saveAsArffOrCsvFiles(feature_values_save_path,
						feature_definitions_save_path,
                        arff_check,
                        csv_check);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Features were successfully extracted and saved in ACE XML, "
                        + "but CSV/ARFF conversion failed.", "CONVERSION FAILURE", JOptionPane.ERROR_MESSAGE);
            }

            if (errorLog.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Features successfully extracted and saved.", "DONE", JOptionPane.INFORMATION_MESSAGE);
            } else {
                FileValidator.windowErrorLog(errorLog);
                JOptionPane.showMessageDialog(null, "Features were successfully extracted and saved from any remaining specified files.\n" +
                        "No features were saved from files in which errors were reported.", "DONE", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (OutOfMemoryError o) {
            // React to the Java Runtime running out of memory
                JOptionPane.showMessageDialog(null, "The Java Runtime ran out of memory. Please rerun this program\n" +
                        "with a higher maximum amount of memory assignable to the Java\n" +
                        "Runtime heap.", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Verify that MEI-specific features are only coupled with MEI files.
     * @param features_to_save Features to be saved.
     * @param recordings Recording info of files of features to be extracted from.
     * @throws Exception Thrown if a non-MEI file is found and MEI-specific features
     * are requested to be extracted.
     */
    private void validateMEIFeatureFiles(boolean[] features_to_save, RecordingInfo[] recordings)
            throws Exception
    {
        List<String> featureNames = FeatureConversion.featureBooleanToNames(features_to_save);
        List<String> meiSpecificFeatures = FeatureExtractorAccess.getNamesOfMeiSpecificFeatures();
        boolean meiFeatureCheck = false;
        String invalidFeatureName = null;
        for(String featureName : featureNames) {
            if(meiSpecificFeatures.contains(featureName)) {
                meiFeatureCheck = true;
                invalidFeatureName = featureName;
                break;
            }
        }

        boolean nonMeiFileCheck = false;
        RecordingInfo invalidFile = null;
        for(RecordingInfo recording : recordings) {
            File recordingFile = new File(recording.file_path);
            if(!FileValidator.validMeiFile(recordingFile)) {
                nonMeiFileCheck = true;
                invalidFile = recording;
                break;
            }
        }

        if(meiFeatureCheck && nonMeiFileCheck) {
            //TODO is this an informative exception???
            throw new Exception("Cannot extract MEI-specific features with non-MEI files present.\n\n" +
                    "For example " + invalidFeatureName + " is an MEI-specific feature and\n" +
                    invalidFile.file_path + " is a non-MEI file.\n\n" +
                    "Please only include MEI files if MEI-specific features are to be extracted.");
        }
    }

    /**
     * Initialize the table displaying the features which can be extracted.
     */
    private void setUpFeatureTable() {
        // Find the descriptions of features that can be extracted
        FeatureDefinition[] feature_definitions = new FeatureDefinition[feature_extractors.length];
        for (int i = 0; i < feature_definitions.length; i++)
            feature_definitions[i] = feature_extractors[i].getFeatureDefinition();

        // Initialize features_table and features_table_model
        Object[] column_names = {new String("Save"),
                new String("Feature"),
                new String("Dimensions")};
        features_table_model = new FeatureSelectorTableModel(column_names, feature_definitions.length);
        features_table_model.fillTable(feature_definitions, feature_save_defaults);
        features_table = new JTable(features_table_model);

        // Set up and display the table
        features_scroll_pane = new JScrollPane(features_table);
        features_panel = new JPanel(new GridLayout(1, 1));
        features_panel.add(features_scroll_pane);
        add(features_panel, BorderLayout.CENTER);
        features_table_model.fireTableDataChanged();
        repaint();
        outer_frame.repaint();
    }


    /**
     * Makes it so that if a row is double clicked on, a description of the
     * corresponding feature is displayed along with its dependencies.
     */
    public void addTableMouseListener() {
        features_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int[] row_clicked = new int[1];
                    row_clicked[0] = features_table.rowAtPoint(event.getPoint());
                    FeatureDefinition definition = feature_extractors[row_clicked[0]].getFeatureDefinition();
                    String text =
                            "NAME: " + definition.name + "\n" +
                                    "DESCRIPTION: " + definition.description + "\n" +
                                    "IS SEQUENTIAL: " + definition.is_sequential + "\n" +
                                    "DIMENSIONS: " + definition.dimensions;
                    JOptionPane.showMessageDialog(null, text, "FEATURE DETAILS", JOptionPane.INFORMATION_MESSAGE);

                }
            }
        });
    }

    /**
     * Returns an array of all the available feature extractors. An empty LinkedList may also
     * be provided specifying whether or not each feature is to be extracted by default.
     *
     * @param defaults An <b>empty</b> Linked list that will be filled with whether or not
     *                 each feature is to be extracted by default. Entries will be in the
     *                 same order as the features in the returned array. If this is not
     *                 needed, then null can be specified for this argument.
     * @return An array of all the available features.
     */
    public static MIDIFeatureExtractor[] getAllAvailableFeatureExtractors(LinkedList<Boolean> defaults) {
        if (defaults == null) defaults = new LinkedList<Boolean>();
        boolean[] defaultArray = FeatureExtractorAccess.getDefaultFeaturesToSave();
        for (boolean b : defaultArray) {
            defaults.add(b);
        }
        return FeatureExtractorAccess.getAllImplementedFeatureExtractors();
    }

    /**
     * Populates with feature extractors that are currently available.
     * @param configuration_file_data Uses the configuration file specified features to save
     *                                if the configuration file is not null. If it is null then the default
     *                                features from {@link FeatureExtractorAccess} are used.
     */
    private void populateFeatureExtractors(ConfigurationFileData configuration_file_data) {
        feature_extractors = FeatureExtractorAccess.getAllImplementedFeatureExtractors();

        if (configuration_file_data != null) {
            feature_save_defaults = configuration_file_data.getFeaturesToSaveBoolean();
        } else {
            feature_save_defaults = FeatureExtractorAccess.getDefaultFeaturesToSave();
        }
    }


    /**
     * Allows the user to select or enter a file path using a JFileChooser.
     * If the selected path does not have an extension of .XML, it is given
     * this extension. If the chosen path refers to a file that already exists,
     * then the user is asked if s/he wishes to overwrite the selected file.
     * <p>No file is actually saved or overwritten by this method. The selected
     * path is simply returned.
     *
     * @return The path of the selected or entered file. A value of null is
     * returned if the user presses the cancel button or chooses
     * not to overwrite a file.
     */
    private String chooseSavePath() {
        // Create the JFileChooser if it does not already exist
        if (save_file_chooser == null) {
            save_file_chooser = new JFileChooser();
            save_file_chooser.setCurrentDirectory(new File("."));
            String[] accepted_extensions = {"xml"};
            save_file_chooser.setFileFilter(new mckay.utilities.general.FileFilterImplementation(accepted_extensions));
        }

        // Process the user's entry
        String path = null;
        int dialog_result = save_file_chooser.showSaveDialog(FeatureSelectorPanel.this);
        if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
        {
            // Get the file the user chose
            File to_save_to = save_file_chooser.getSelectedFile();

            // Make sure has .xml extension
            path = to_save_to.getPath();
            String ext = mckay.utilities.staticlibraries.StringMethods.getExtension(path);
            if (ext == null) {
                path += ".xml";
                to_save_to = new File(path);
            } else if (!ext.equals(".xml")) {
                path = mckay.utilities.staticlibraries.StringMethods.removeExtension(path) + ".xml";
                to_save_to = new File(path);
            }

            // See if user wishes to overwrite if a file with the same name exists
            if (to_save_to.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(null,
                        "This file already exists.\nDo you wish to overwrite it?",
                        "WARNING",
                        JOptionPane.YES_NO_OPTION);
                if (overwrite != JOptionPane.YES_OPTION)
                    path = null;
            }
        }

        // Return the selected file path
        return path;
    }
}