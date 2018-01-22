package jsymbolic2.gui;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import jsymbolic2.configuration.*;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.featureutils.MEIFeatureExtractor;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import mckay.utilities.gui.tables.TableMethods;
import mckay.utilities.staticlibraries.StringMethods;
import ace.datatypes.FeatureDefinition;

/**
 * A JPanel containing a table containing one row for each implemented feature, and whose first column
 * contains check boxes allowing users to select which features to save during feature extraction. This
 * feature table also allows users to view additional metadata about each feature, and still further metadata
 * can be seen for a feature (including its description and feature dependencies) by double clicking on its
 * row. Buttons are included for auto-selecting all features, no features or just the default features.
 * 
 * <p>The Feature Name column indicates the unique name of each feature, the Code column indicates its unique
 * code (feature group letter followed by the numerical identifier within the group), the Values column
 * indicates the number of dimensions (i.e. independent values) associated with the feature (any feature with
 * a Values entry greater than 1 is a feature vector rather than a single-value feature), the MEI-Only column
 * indicates whether a given feature can only be extracted from MEI files (such features should only be used
 * if features are exclusively being extracted from MEI files), and the Sequential column indicates whether a
 * given feature can be extracted for windows of a symbolic piece (a value of Yes means that the feature can
 * be extracted from either complete pieces or from individual windows, and a Value of No indicates that it
 * can only be extracted from complete pieces).</p>
 *
 * <p>Note that some features need other features to first be calculated in order to be extracted themselves. 
 * So, even if a given feature is not explicitly selected for saving on this table, it will still be 
 * automatically extracted (but not saved) if another feature that is calculated based on it is in fact
 * selected for saving.</p>
 * 
 * @author Cory McKay and Tristano Tenaglia
 */
public class FeatureSelectorPanel
	extends JPanel
	implements ActionListener
{
	/* FIELDS ***********************************************************************************************/
	
	
	/**
	 * Holds a reference to the JFrame that holds this FeatureSelectionPanel object.
	 */
	private final OuterFrame outer_frame;

	/**
	 * Hold one instance of each implemented feature.
	 */
	private final MIDIFeatureExtractor[] all_feature_extractors;

	/**
	 * The default features to save during feature extraction. Ordering matches those of the features in the
	 * all_feature_extractors field.
	 */
	private final boolean[] feature_save_defaults;

	/**
	 * A table where each row corresponds to a different feature, and each implemented feature is listed
	 * exactly once. The first column holds a check box indicating whether that row's feature should be saved
	 * during feature extraction. The other columns hold metadata about the given row's feature.
	 */
	private JTable features_table;
	
	/**
	 * The table model for the features_table field.
	 */
	private FeatureSelectorTableModel features_table_model;

	/**
	 * A button causing the default features to be marked for extraction on the features_table.
	 */
	private JButton select_default_features_button;

	/**
	 * A button causing the all features to be marked for extraction on the features_table.
	 */
	private JButton select_all_features_button;

	/**
	 * A button causing no features to be marked for extraction on the features_table.
	 */
	private JButton deselect_all_features_button;
	
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Set up this JPanel's fields and GUI elements.
	 *
	 * @param outer_frame				The JFrame element that contains this JPanel.
	 * @param configuration_file_data	The configuration file data that will be used to initialize the 
	 *									features set to be saved. If null is provided here, then default 
	 *									features are set to be saved.
	 */
	public FeatureSelectorPanel(OuterFrame outer_frame, ConfigurationFileData configuration_file_data)
	{
		// Store a reference to the containing JFrame
		this.outer_frame = outer_frame;

		// Store informaation relating to feature extractors in this object's fields (including the particular
		// features initially set to be saved, as parsed from a config file if one is specified).
		all_feature_extractors = FeatureExtractorAccess.getAllImplementedFeatureExtractors();
		if (configuration_file_data != null)
			feature_save_defaults = configuration_file_data.getFeaturesToSaveBoolean();
		else feature_save_defaults = FeatureExtractorAccess.getDefaultFeaturesToSave();
				
		// Set up the features_table and its features_table_model
		setUpFeatureTable();

		// Set up the GUI elements on this JPanel
		formatAndAddGuiElements();
		
		// Cause the table to respond to double clicks
		addTableMouseListener();	
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Calls the appropriate method when one of the JButtons on this JPanel is pressed. Adjusts the features
	 * marked to be saved on the table based on the particular button pressed.
	 *
	 * @param event	The button-triggered event that is to be reacted to.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource().equals(select_default_features_button))
			features_table_model.setFeaturesMarkedForSaving(feature_save_defaults);
		else if (event.getSource().equals(select_all_features_button))
		{
			boolean[] to_save = new boolean[features_table_model.getRowCount()];
			for (int i = 0; i < to_save.length; i++)
				to_save[i] = true;
			features_table_model.setFeaturesMarkedForSaving(to_save);
		}
		else if (event.getSource().equals(deselect_all_features_button))
		{
			boolean[] to_save = new boolean[features_table_model.getRowCount()];
			for (int i = 0; i < to_save.length; i++)
				to_save[i] = false;
			features_table_model.setFeaturesMarkedForSaving(to_save);
		}
	}


	/**
	 * Returns whether or not each of the features is marked to be saved on the features table, based on
	 * whether its check box is checked on the table.
	 *
	 * @return				Which features should be saved during feature extraction. The ordering of the 
	 *						returned booleans corresponds to the ordering of the features on the features
	 *						table.
	 * @throws Exception	An informative exception is thrown if no features have been selected for saving.
	 */
	public boolean[] getFeaturesToSave()
		throws Exception
	{
		boolean[] features_to_save = new boolean[all_feature_extractors.length];
		for (int i = 0; i < features_to_save.length; i++)
			features_to_save[i] = ((Boolean) features_table_model.getValueAt(i, 0));
		return features_to_save;
	}
	
	
	/**
	 * Returns a list of the names of all features currently selected on the features table to be saved. 
	 *
	 * @return				A list of the features marked for saving during feature extraction, in the order
	 *						that they appear on the features table.
	 * @throws Exception	An informative exception is thrown if no features have been selected for saving.
	 */
	public List<String> getNamesOfFeaturesToSave()
		throws Exception
	{
		boolean[] features_to_save = getFeaturesToSave();
		List<String> names_of_features_to_save = new ArrayList<>();
		for (int i = 0; i < features_to_save.length; i++)
			if (features_to_save[i] == true)
				names_of_features_to_save.add(FeatureExtractorAccess.getNamesOfAllImplementedFeatures().get(i));
		return names_of_features_to_save;
	}

	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Initialize the features_table and features_table_model fields and format the features_table.
	 */
	private void setUpFeatureTable()
	{
		// Set the column headings and ordering
		Object[] column_names =
		{
			"Save",
			"Feature Name",
			"Code",
			"Values",
			"MEI-Only"
			//"Sequential"
		};

		// Initialize features_table_model and features_table
		features_table_model = new FeatureSelectorTableModel(column_names, all_feature_extractors.length);
		features_table_model.fillTable(all_feature_extractors, feature_save_defaults);
		features_table = new JTable(features_table_model);

		// Right-justify features_table columns
		DefaultTableCellRenderer right_column_renderer = new DefaultTableCellRenderer();
		right_column_renderer.setHorizontalAlignment(JLabel.RIGHT);
		features_table.getColumnModel().getColumn(2).setCellRenderer(right_column_renderer);
		features_table.getColumnModel().getColumn(3).setCellRenderer(right_column_renderer);
		features_table.getColumnModel().getColumn(4).setCellRenderer(right_column_renderer);
		//features_table.getColumnModel().getColumn(5).setCellRenderer(right_column_renderer);
		
		// Resize features_table columns to fit the data
		TableMethods.sizeTableColumnsToFit(features_table, false);
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
		JLabel panel_label = new JLabel("FEATURES TO SAVE");
		OuterFrame.formatLabel(panel_label);
		
		// Set up the features table button panel
		select_default_features_button = new JButton("Select Default Features");
		select_all_features_button = new JButton("Select All Features");
		deselect_all_features_button = new JButton("Deselect All Features");
		JPanel button_panel = new JPanel(new GridLayout(1, 3, horizontal_gap, vertical_gap));
		button_panel.add(select_default_features_button);
		button_panel.add(select_all_features_button);
		button_panel.add(deselect_all_features_button);
		
		// Add action listeners to buttons
		select_default_features_button.addActionListener(this);
		select_all_features_button.addActionListener(this);
		deselect_all_features_button.addActionListener(this);

		// Make the features_table scrollable and place it on its own JPanel
		JScrollPane features_scroll_pane = new JScrollPane(features_table);
		JPanel features_panel = new JPanel(new GridLayout(1, 1));
		features_panel.add(features_scroll_pane);

		// Add all GUI elements to this JPanel		
		add(panel_label, BorderLayout.NORTH);
		add(features_panel, BorderLayout.CENTER);
		add(button_panel, BorderLayout.SOUTH);
		features_table_model.fireTableDataChanged();
		repaint();
		outer_frame.repaint();	
	}
	
	
	/**
	 * Makes it so that if a row is double clicked on, then a description of the row's corresponding feature
	 * is displayed in a pop-up window.
	 */
	private void addTableMouseListener()
	{
		features_table.addMouseListener
		(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent event)
				{
					if (event.getClickCount() == 2)
					{
						int row_clicked = features_table.rowAtPoint(event.getPoint());
						FeatureDefinition definition = all_feature_extractors[row_clicked].getFeatureDefinition();
						boolean mei_specific = all_feature_extractors[row_clicked] instanceof MEIFeatureExtractor;

						String text
								= "NAME: " + definition.name + "\n"
								+ "CODE: " + all_feature_extractors[row_clicked].getFeatureCode() + "\n"
								+ "DESCRIPTION: " + definition.description + "\n"
								+ "DIMENSIONS: " + definition.dimensions + "\n"
								+ "MEI-SPECIFIC: " + mei_specific + "\n"
								+ "IS SEQUENTIAL: " + definition.is_sequential + "\n";

						String[] dependencies = all_feature_extractors[row_clicked].getDepenedencies();
						if (dependencies == null)
							text += "DEPENDENCIES: none";
						else
						{
							for (int dep = 0; dep < dependencies.length; dep++)
							{
								text += "DEPENDENCY: " + dependencies[dep];
								if (dep != dependencies.length - 1)
									text += "\n";
							}
						}

						JOptionPane.showMessageDialog( outer_frame,
													   StringMethods.wrapString(text, OuterFrame.DIALOG_BOX_MAX_CHARS_PER_LINE, OuterFrame.DIALOG_BOX_HANGING_INDENT_CHARS),
													   "Feature Metadata",
													   JOptionPane.INFORMATION_MESSAGE );
					}
				}
			}
		);
	}
}