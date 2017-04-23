package jsymbolic2.gui;

import javax.swing.table.DefaultTableModel;

/**
 * The table model used by the symbolic_music_files_table JTable in MusicFileSelectorPanel to list all
 * symbolic music files from which features are to be extracted. The file path for each such file is listed in
 * its own row, with one column for the file name and one for the file path. Provides methods to fill the
 * table row by row and to delete everything in it. Makes all cells non-editable.
 *
 * @author Cory McKay
 */
public class SymbolicMusicFilesTableModel
	extends DefaultTableModel
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Simply calls the DefaultTableModel constructor.
	 */
	SymbolicMusicFilesTableModel(Object[] column_names, int rows)
	{
		super(column_names, rows);
	}

	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Deletes everything in the table and then fills it up one row at a time based on the given
	 * symbolic_music_files.
	 *
	 * @param	symbolic_music_files	Files to include on the table, in the order that they are to be added
	 *									to it. If this is null, then the table is simply cleared of any 
	 *									existing rows.
	 */
	public void resetAndFillTable(SymbolicMusicFile[] symbolic_music_files)
	{
		// Clear the contents of the table
		clearTable();

		// Populate each row one by one
		if (symbolic_music_files != null)
		{
			for (SymbolicMusicFile this_music_file : symbolic_music_files)
			{
				Object[] row_contents = { this_music_file.file_name, this_music_file.file_path };
				addRow(row_contents);
			}
		}
	}

	
	/**
	 * Removes all rows in the table.
	 */
	public void clearTable()
	{
		while (getRowCount() != 0)
			removeRow(0);
	}

	
	/**
	 * Returns false for all cells on the table, with the effect that no cells are editable.
	 *
	 * @param	row		The table row to check.
	 * @param	column	The table column to check.
	 * @return			Whether or not the cell at the specified row and column is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}