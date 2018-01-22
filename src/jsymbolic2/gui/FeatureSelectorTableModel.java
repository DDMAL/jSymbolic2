package jsymbolic2.gui;

import javax.swing.table.DefaultTableModel;
import jsymbolic2.featureutils.MEIFeatureExtractor;
import jsymbolic2.featureutils.MIDIFeatureExtractor;

/**
 * The table model used by the features_table JTable in FeatureSelectorPanel to list all features that
 * jSymbolic can extract, metadata about each such features, and whether each feature should be extracted and
 * saved during the next feature extraction. Provides methods to fill the table row by row. Makes all columns
 * except the first non-editable. The first column is filled with check boxes indicating whether the feature
 * in the corresponding row should be saved, and this class also includes a method for changing which of these
 * check boxes are selected.
 *
 * @author Cory McKay
 */
public class FeatureSelectorTableModel
	extends DefaultTableModel
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Simply calls the DefaultTableModel constructor.
	 *
	 * @param column_names	The names of each column in the table model.
	 * @param rows			The number of rows in the table.
	 */
	public FeatureSelectorTableModel(Object[] column_names, int rows)
	{
		super(column_names, rows);
	}

	
	/* OVERRIDDEN PUBLIC METHODS ****************************************************************************/
	
	
	/**
	 * Returns the type of class held by the entries of each column. Necessary in order for text boxes to be
	 * properly displayed.
	 *
	 * @param	column	The table column to check.
	 * @return			The type of class used by the specified column.
	 */
	@Override
	public Class getColumnClass(int column)
	{
		return getValueAt(0, column).getClass();
	}

	
	/**
	 * Returns false for all cells except those in the first column, so that only cells in the first column
	 * are editable.
	 *
	 * @param	row		The table row to check.
	 * @param	column	The table column to check.
	 * @return			Whether or not the cell at the specified row and column is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return column == 0;
	}	

	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Delete everything in the table and then fill it up one row at a time to hold the contents of the 
	 * given FeatureDefinition array.
	 *
	 * @param features				The features to list in the table, in the order that they will be listed.
	 * @param feature_save_defaults	Whether each feature in features should be marked to be extracted and
	 *								saved by default (i.e. in the first column text box). Order corresponds
	 *								to the order of the MIDIFeatureExtractor objects passed to the features
	 *								parameter.
	 */
	public void fillTable(MIDIFeatureExtractor[] features, boolean[] feature_save_defaults)
	{
		// Remove all rows in the table
		while (getRowCount() != 0)
			removeRow(0);

		// Add each feature one by one
		for (int i = 0; i < features.length; i++)
		{
			Object[] row_contents = new Object[5];
			
			row_contents[0] = feature_save_defaults[i];

			row_contents[1] = features[i].getFeatureDefinition().name;

			row_contents[2] = features[i].getFeatureCode();

			if (features[i].getFeatureDefinition().dimensions > 0)
				row_contents[3] = features[i].getFeatureDefinition().dimensions;
			else row_contents[3] = "variable";

			if (features[i] instanceof MEIFeatureExtractor)
				row_contents[4] = "Yes";
			else row_contents[4] = "No";
			
			//if (features[i].getFeatureDefinition().is_sequential)
			//	row_contents[5] = "Yes";
			//else row_contents[5] = "No";
			
			addRow(row_contents);
		}
	}
	
	
	/**
	 * Sets the checkboxes in the first column of each row to the corresponding value in the specified
	 * should_save.
	 * 
	 * @param should_save	Whether or not each feature should be saved when feature extraction occurs. Must
	 *						be the same size as the number of rows in the table, and must have an ordering
	 *						corresponding to the rows in the table.
	 */
	public void setFeaturesMarkedForSaving(boolean[] should_save)
	{
		for (int i = 0; i < getRowCount(); i++)
			setValueAt(should_save[i], i, 0);
	}
}