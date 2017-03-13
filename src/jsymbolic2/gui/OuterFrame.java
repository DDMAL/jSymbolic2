package jsymbolic2.gui;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import jsymbolic2.commandline.CommandLineSwitchEnum;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.processing.UserFeedbackGenerator;

/**
 * A panel holding the components of the jSymbolic Feature Extractor GUI, including the
 * RecordingSelectorPanel, the FeatureSelectorPanel and two text areas that can be written to (via the
 * status_print_stream and error_print_stream).
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class OuterFrame
		extends JFrame
{
	/* PUBLIC FIELDS ****************************************************************************************/

	
	/**
	 * A panel listing the symbolic files from which features are to be extracted. The user may add files to
	 * this list, delete files from it, view information about the files or play the files.
	 */
	public RecordingSelectorPanel recording_selector_panel;

	/**
	 * A panel allowing the user to select the features to be extracted from the selected symbolic music
	 * files, to choose whether windowed extraction is to be performed (and, if so, using what kind of
	 * windows), to choose settings related to file output, to perform operations related to configuration
	 * files and to begin feature extraction.
	 */
	public FeatureSelectorPanel feature_selector_panel;
	
	/**
	 * The stream to which processing status updates are written to.
	 */
	public PrintStream status_print_stream;
			
	/**
	 * The stream to which errors are written to.
	 */
	public PrintStream error_print_stream;
			
	/**
	 * The horizontal gap between GUI elements.
	 */
	public final int horizontal_gap = 4;

	/**
	 * The vertical gap between GUI elements.
	 */
	public final int vertical_gap = 4;
	
	
	/* PRIVATE FIELDS ***************************************************************************************/
	
	
	/**
	 * Where processing status messages are posted.
	 */
	private JTextArea status_text_area;
		
	/**
	 * Where error messages are posted during processing.
	 */
	private JTextArea error_text_area;


	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Basic constructor that sets up the GUI.
	 *
	 * @param config_file_data	Data parsed from a configuration file. Null if no configuration file is 
	 *							specified.
	 */
	public OuterFrame(ConfigurationFileData config_file_data)
	{
		// Position the GUI window at the left corner of the screen with a size of 1280 x 1024
		setBounds (0, 0, 1280, 1024);
		
		// Set the GUI window title
		setTitle("jSymbolic 2");

		// Make jSymbolic quit when exit box is pressed
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Set up the recording_selector_panel and feature_selector_panel
		JPanel recording_and_feature_panel = new JPanel(new GridLayout(1, 2, horizontal_gap, vertical_gap));
		recording_selector_panel = new RecordingSelectorPanel(this, config_file_data);
		feature_selector_panel = new FeatureSelectorPanel(this, config_file_data);
		recording_and_feature_panel.add(recording_selector_panel);
		recording_and_feature_panel.add(feature_selector_panel);
		
		// Set up the message_print_stream and its associated message_text_area
		JPanel status_text_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		status_text_area = new JTextArea();
		DefaultCaret status_text_area_caret = (DefaultCaret)status_text_area.getCaret();
		status_text_area_caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		status_text_area.setLineWrap(true);
		status_text_area.setEditable(false);
		status_text_area.setTabSize(2);
		status_text_area.setText("");
		JLabel status_text_panel_label = new JLabel("PROCESSING STATUS UPDATES:");
		formatLabel(status_text_panel_label);
		status_text_panel.add(status_text_panel_label, BorderLayout.NORTH);
		JScrollPane status_text_area_scrollpane = new JScrollPane(status_text_area);
		JScrollBar status_text_area_scrollbar = status_text_area_scrollpane.getVerticalScrollBar();
		status_text_panel.add(status_text_area_scrollpane, BorderLayout.CENTER);
		status_print_stream = getPrintStream(status_text_area, status_text_area_scrollbar);
		
		// Set up the error_print_stream and its associated error_text_area
		JPanel error_text_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		error_text_area = new JTextArea();
		DefaultCaret error_text_area_caret = (DefaultCaret)error_text_area.getCaret();
		error_text_area_caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		error_text_area.setLineWrap(true);
		error_text_area.setEditable(false);
		error_text_area.setTabSize(2);
		error_text_area.setForeground(Color.RED);
		error_text_area.setText("");
		JLabel error_text_panel_label = new JLabel("ERROR REPORTS:");
		formatLabel(error_text_panel_label);
		error_text_panel.add(error_text_panel_label, BorderLayout.NORTH);
		JScrollPane error_text_area_scrollpane = new JScrollPane(error_text_area);
		JScrollBar error_text_area_scrollbar = error_text_area_scrollpane.getVerticalScrollBar();
		error_text_panel.add(error_text_area_scrollpane, BorderLayout.CENTER);
		error_print_stream = getPrintStream(error_text_area, error_text_area_scrollbar);
		
		// Print out a warning if no default configuration file was found at startup
		if (config_file_data == null)
			error_print_stream.println("NON-CRITICAL WARNING: Could not find a valid a configurations file called " + CommandLineSwitchEnum.default_config_file_path +  "  in the jSymbolic home directory. As a result, the jSymbolic GUI was launched with standard settings. Although a default configurations file is by no means necessary to use jSymbolic, it is often convenient. You can save one at anytime using the jSymbolic GUI, if you wish (see the manual for more details).\n");
		
		// Verify that the input files specified in the config file are valid. Print out warnings if any
		// invalid files are present.
		else
		{
			try
			{
				UserFeedbackGenerator.printParsingConfigFileMessage(status_print_stream, config_file_data.getConfigurationFilePath());
				new ConfigurationFileValidatorTxtImpl().parseConfigFileTwoThreeOrFour(config_file_data.getConfigurationFilePath(), error_print_stream);
			}
			catch (Exception e) {}
		}
		
		// Set up the combined_text_panels
		JPanel combined_text_panels = new JPanel(new GridLayout(1, 2, horizontal_gap, vertical_gap));
		combined_text_panels.add(status_text_panel);
		combined_text_panels.add(error_text_panel);
		
		// Add items to the GUI
		setLayout(new BorderLayout(horizontal_gap, vertical_gap));
		add(recording_and_feature_panel, BorderLayout.NORTH);
		add(combined_text_panels, BorderLayout.CENTER);

		// Display the GUI
		this.setVisible(true);
	}
		

	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Delete all contents in the status_text_area and error_text_area.
	 */
	public void clearTextAreas()
	{
		status_text_area.setText("");
		status_text_area.setCaretPosition(0);
		status_text_area.update(status_text_area.getGraphics());
		
		error_text_area.setText("");
		error_text_area.setCaretPosition(0);
		error_text_area.update(error_text_area.getGraphics());
	}

	
	/* PUBLIC STATIC METHODS*********************************************************************************/
	
	
	/**
	 * Adjust the formatting of the given JLabel.
	 * 
	 * @param to_format	The JLabel to format.
	 */
	public static void formatLabel (JLabel to_format)
	{
		// Change the font, style and/or size
		Font old_font = to_format.getFont();
		Font new_font = new Font(old_font.getFontName(), Font.BOLD, (old_font.getSize() + 1));
		to_format.setFont(new_font);
		
		// Change the alignment
		to_format.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Change the colour
		to_format.setForeground(Color.DARK_GRAY);
	}


	/* PRIVATE STATIC METHODS *******************************************************************************/

	
	/**
	 * Return a PrintStream that, when written to, will append the supplied text to the specified JTextArea
	 * and cause scrolling to follow text as it is written.
	 * 
	 * @param text_area				The JTextArea to append text to when the returned PrintStream is written
	 *								to.
	 * @param vertical_scroll_bar	The vertical scroll bar associated with the JTextArea that will be written
	 *								to. Needed to ensure scrolling updates properly.
	 * @return						The PrintStream that can be used to write to the text_area.
	 */
	private static PrintStream getPrintStream(final JTextArea text_area, final JScrollBar vertical_scroll_bar)
	{
		OutputStream new_stream = new OutputStream() 
		{
			@Override
			public void write(int b) throws IOException 
			{
				text_area.append(String.valueOf((char) b));
				// text_area.update(text_area.getGraphics());
				vertical_scroll_bar.setValue(vertical_scroll_bar.getMaximum());
				// vertical_scroll_bar.paint(vertical_scroll_bar.getGraphics());
				text_area.scrollRectToVisible(text_area.getVisibleRect());
				text_area.paint(text_area.getGraphics());
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException 
			{
				text_area.append(new String(b, off, len));
				// text_area.update(text_area.getGraphics());
				vertical_scroll_bar.setValue(vertical_scroll_bar.getMaximum());
				// vertical_scroll_bar.paint(vertical_scroll_bar.getGraphics());
				text_area.scrollRectToVisible(text_area.getVisibleRect());
				text_area.paint(text_area.getGraphics());
			}

			@Override
			public void write(byte[] b)
				throws IOException
			{
				write(b, 0, b.length);
			}
		};
		
		return new PrintStream(new_stream);
	}
}