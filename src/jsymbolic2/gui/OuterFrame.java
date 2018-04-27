package jsymbolic2.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;
import jsymbolic2.commandline.CommandLineSwitchEnum;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.UserFeedbackGenerator;
import mckay.utilities.gui.templates.AboutDialog;
import mckay.utilities.gui.templates.HelpDialog;

/**
 * A JFrame holding the components of the jSymbolic Feature Extractor GUI, including the
 * MusicFileSelectorPanel (for selecting symbolic music files to extract features from), the
 * FeatureSelectorPanel (for choosing the features to extract), the ExtractionConfigurationsPanel (for loading
 * and saving configuration files, for setting feature extraction settings and paths, and for initiating
 * feature extractions. This JFrame also holds two text areas that can be written to (via the
 * status_print_stream and error_print_stream fields). Finally, this JFrame includes a menu that allows the
 * user to display the on-line manual and an about dialog box.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class OuterFrame
		extends JFrame
		implements ActionListener
{
	/* PUBLIC STATIC FINAL FIELDS ***************************************************************************/

	
	/**
	 * The horizontal gap between GUI elements.
	 */
	public static final int HORIZONTAL_GAP = 5;

	/**
	 * The vertical gap between GUI elements.
	 */
	public static final int VERTICAL_GAP = 5;
	
	/**
	 * The maximum number of characters a single line of text displayed in a JOptionPane should have.
	 */
	public static final int DIALOG_BOX_MAX_CHARS_PER_LINE = 115;
	
	/**
	 * The number of characters to indent all lines but the first in each paragraph in a JOptionPane after
	 * line wrapping.
	 */
	public static final int DIALOG_BOX_HANGING_INDENT_CHARS = 5;
	

	/* PRIVATE STATIC FINAL FIELDS ***************************************************************************/

	
	/**
	 * The title to include in the GUI title bar.
	 */
	private static final String WINDOW_TITLE = "jSymbolic 2.2";
	
	/**
	 * The name of this software's author.
	 */
	private static final String AUTHOR_STRING = "Cory McKay";
	
	/**
	 * The string indicating licensing credit.
	 */
	private static final String LICENSING_STRING = "2018 (GNU GPL)";
	
	/**
	 * The credited institution.
	 */
	private static final String INSTITUTION_CREDIT_STRING = "CIRMMT / Marianopolis College / McGill University";
	
	/**
	 * The horizontal dimension of the GUI window, in pixels.
	 */
	private static final int WINDOW_WIDTH_HORIZONTAL = 1250;

	/**
	 * The vertical dimension of the GUI window, in pixels.
	 */
	private static final int WINDOW_WIDTH_VERTICAL = 940;
	

	/* PUBLIC FIELDS ****************************************************************************************/

	
	/**
	 * A panel listing the symbolic files from which features are to be extracted. The user may add files to
	 * this list, delete files from it, view information about the files or play the files.
	 */
	public MusicFileSelectorPanel music_file_selector_panel;

	/**
	 * A panel allowing the user to select the features to be extracted and to see information about each
	 * feature.
	 */
	public FeatureSelectorPanel feature_selector_panel;
	
	/**
	 * A panel allowing the user to specify all feature extraction and configuration file settings other than
	 * the particular symbolic files from which features are to be extracted (this is set in the
	 * MusicFileSelectorPanel) and the particular features that are to be extracted and saved from them (this
	 * is set in the FeatureSelectorPanel). The user may use buttons on this panel to initiate feature
	 * extraction and saving, to save the settings currently entered on the GUI in a configuration file, or to
	 * load settings from an existing configuration file.
	 */
	public ExtractionConfigurationsPanel extraction_configurations_panel;
	
	/**
	 * The stream to which processing status updates are written to.
	 */
	public PrintStream status_print_stream;
			
	/**
	 * The stream to which errors are written to.
	 */
	public PrintStream error_print_stream;
			
	
	/* PRIVATE FIELDS ***************************************************************************************/
	
	
	/**
	 * Where processing status messages are displayed.
	 */
	private JTextArea status_text_area;
		
	/**
	 * Where error messages are displayed during processing.
	 */
	private JTextArea error_text_area;

	/**
	 * Displays ownership and version information.
	 */
	private JMenuItem about_menu_item;

	/**
	 * Makes the help_dialog visible.
	 */
	private JMenuItem help_menu_item;
	
	/**
	 * Makes the tutorial_dialog visible.
	 */
	private JMenuItem tutorial_menu_item;
	
	/**
	 * Displays the on-line manual.
	 */
	private HelpDialog help_dialog;

	/**
	 * Displays the on-line tutorial.
	 */
	private HelpDialog tutorial_dialog;


	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Basic constructor that sets up the GUI.
	 *
	 * @param config_file_data	Data parsed from a configuration file. Null if no configuration file data is 
	 *							available or specified.
	 */
	public OuterFrame(ConfigurationFileData config_file_data)
	{
		// Determine the proper window size based on the current display settings. Use default size if there
		// is enough room, otherwise choose a size that will fit the screen.
		int frame_width = WINDOW_WIDTH_HORIZONTAL;
		int frame_height = WINDOW_WIDTH_VERTICAL;
		Dimension current_screen_size = Toolkit.getDefaultToolkit().getScreenSize();
		if (frame_width > current_screen_size.getWidth())
			frame_width = (int) current_screen_size.getWidth();
		if (frame_height > current_screen_size.getHeight())
			frame_height = (int) current_screen_size.getHeight() - 80;
		
		// Position the GUI window at the left corner of the an appropriate size
		setBounds (0, 0, frame_width, frame_height);
		
		// Set the GUI window title
		setTitle(WINDOW_TITLE);

		// Make jSymbolic quit when exit box is pressed
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Create an outer border
		JPanel outer_padding_panel = new JPanel();				
		Border outer_padding = BorderFactory.createEmptyBorder(VERTICAL_GAP, HORIZONTAL_GAP, VERTICAL_GAP, HORIZONTAL_GAP);
		outer_padding_panel.setBorder(outer_padding);
		setContentPane(outer_padding_panel);
		
		// Set up the menu bar
		JMenuBar menu_bar = new JMenuBar();
		JMenu information_menu = new JMenu("Information");
		information_menu.setMnemonic('i');
		about_menu_item = new JMenuItem("About");
		about_menu_item.setMnemonic('a');
		about_menu_item.addActionListener(OuterFrame.this);
		information_menu.add(about_menu_item);
		help_menu_item = new JMenuItem("Help");
		help_menu_item.setMnemonic('h');
		help_menu_item.addActionListener(OuterFrame.this);
		information_menu.add(help_menu_item);
		tutorial_menu_item = new JMenuItem("Tutorial");
		tutorial_menu_item.setMnemonic('t');
		tutorial_menu_item.addActionListener(OuterFrame.this);
		information_menu.add(tutorial_menu_item);
		menu_bar.add(information_menu);
		setJMenuBar(menu_bar);

		// Set up help dialog box
		help_dialog = new HelpDialog( "manual" + File.separator + "home_files" + File.separator + "contents.html",
									  "manual" + File.separator + "home_files" + File.separator + "splash.html" );
		
		// Set up tutorial dialog box
		tutorial_dialog = new HelpDialog( "tutorial" + File.separator + "contents.html",
									      "tutorial" + File.separator + "introduction.html" );
		
		// Set up the music_file_selector_panel and feature_selector_panel
		JPanel music_and_feature_panel = new JPanel(new GridLayout(1, 2, HORIZONTAL_GAP, VERTICAL_GAP));
		music_file_selector_panel = new MusicFileSelectorPanel(this);
		addFormattedBorder(music_file_selector_panel);
		feature_selector_panel = new FeatureSelectorPanel(this, config_file_data);
		addFormattedBorder(feature_selector_panel);
		music_and_feature_panel.add(music_file_selector_panel);
		music_and_feature_panel.add(feature_selector_panel);
		
		// Set up the extraction_configurations_panel
		extraction_configurations_panel = new ExtractionConfigurationsPanel(this, config_file_data);
		
		// Set up the message_print_stream and its associated message_text_area
		JPanel status_text_panel = new JPanel(new BorderLayout(HORIZONTAL_GAP, VERTICAL_GAP));
		OuterFrame.addFormattedBorder(status_text_panel);
		status_text_area = new JTextArea();
		DefaultCaret status_text_area_caret = (DefaultCaret)status_text_area.getCaret();
		status_text_area_caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		status_text_area.setLineWrap(true);
		status_text_area.setWrapStyleWord(true);
		status_text_area.setEditable(false);
		status_text_area.setTabSize(2);
		status_text_area.setText("");
		JLabel status_text_panel_label = new JLabel("PROCESSING INFORMATION");
		formatLabel(status_text_panel_label);
		status_text_panel.add(status_text_panel_label, BorderLayout.NORTH);
		JScrollPane status_text_area_scrollpane = new JScrollPane(status_text_area);
		JScrollBar status_text_area_scrollbar = status_text_area_scrollpane.getVerticalScrollBar();
		status_text_panel.add(status_text_area_scrollpane, BorderLayout.CENTER);
		status_print_stream = getPrintStream(status_text_area, status_text_area_scrollbar);
		
		// Set up the error_print_stream and its associated error_text_area
		JPanel error_text_panel = new JPanel(new BorderLayout(HORIZONTAL_GAP, VERTICAL_GAP));
		OuterFrame.addFormattedBorder(error_text_panel);
		error_text_area = new JTextArea();
		DefaultCaret error_text_area_caret = (DefaultCaret)error_text_area.getCaret();
		error_text_area_caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		error_text_area.setLineWrap(true);
		error_text_area.setWrapStyleWord(true);
		error_text_area.setEditable(false);
		error_text_area.setTabSize(2);
		error_text_area.setForeground(Color.RED);
		error_text_area.setText("");
		JLabel error_text_panel_label = new JLabel("ERROR REPORTS");
		formatLabel(error_text_panel_label);
		error_text_panel.add(error_text_panel_label, BorderLayout.NORTH);
		JScrollPane error_text_area_scrollpane = new JScrollPane(error_text_area);
		JScrollBar error_text_area_scrollbar = error_text_area_scrollpane.getVerticalScrollBar();
		error_text_panel.add(error_text_area_scrollpane, BorderLayout.CENTER);
		error_print_stream = getPrintStream(error_text_area, error_text_area_scrollbar);
		
		// Print out a warning if no default configuration file was found at startup
		if (config_file_data == null)
			error_print_stream.println("NON-CRITICAL WARNING: Could not find a valid configurations file called " + CommandLineSwitchEnum.default_config_file_path +  " in the jSymbolic home directory. As a result, the jSymbolic GUI was launched with standard settings. Although a default configurations file is by no means necessary to use jSymbolic, it is often convenient. You can save one at anytime using the jSymbolic GUI, if you wish (see the manual for more details).\n");
		
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
		
		// Print out basic welcome information for the user
		status_print_stream.println("Welcome to jSymbolic!\n");
        try
        {
            status_print_stream.println("SUMMARY INFORMATION ON FEATURES SELECTED FOR EXTRACTION AT STARTUP:");
            status_print_stream.println(FeatureExtractorAccess.getFeatureCatalogueOverviewReport(feature_selector_panel.getFeaturesToSave()));
        }
        catch (Exception e)
        {
            status_print_stream.println("Unable to summarize this information.\n");
        }
		status_print_stream.println("SUMMARY INFORMATION ON ALL IMPLEMENTED FEATURES:");
        status_print_stream.println(FeatureExtractorAccess.getFeatureCatalogueOverviewReport(null));
		
		// Load references to any potential symbolic music files from a config file
		music_file_selector_panel.addMusicFilesParsedFromConfigFile(config_file_data);
		
		// Set up the combined_text_panels
		JPanel combined_text_panels = new JPanel(new GridLayout(1, 2, HORIZONTAL_GAP, VERTICAL_GAP));
		combined_text_panels.add(status_text_panel);
		combined_text_panels.add(error_text_panel);

		// Set up the all_but_extraction_configuration_panels
		GridBagLayout all_but_extraction_configuration_panels_layout = new GridBagLayout();
		JPanel all_but_extraction_configuration_panels = new JPanel(all_but_extraction_configuration_panels_layout);
		GridBagConstraints all_but_extraction_configuration_panels_constraints = new GridBagConstraints();
		all_but_extraction_configuration_panels_constraints.weightx = 1.0;
		all_but_extraction_configuration_panels_constraints.weighty = 0.7;
		all_but_extraction_configuration_panels_constraints.fill = GridBagConstraints.BOTH;
		all_but_extraction_configuration_panels_constraints.gridwidth = GridBagConstraints.REMAINDER;
		all_but_extraction_configuration_panels_layout.setConstraints(music_and_feature_panel, all_but_extraction_configuration_panels_constraints);
		all_but_extraction_configuration_panels.add(music_and_feature_panel);
		all_but_extraction_configuration_panels_constraints.insets = new Insets(VERTICAL_GAP, 0, 0, 0);
		all_but_extraction_configuration_panels_constraints.weighty = 0.3;
		all_but_extraction_configuration_panels_layout.setConstraints(combined_text_panels, all_but_extraction_configuration_panels_constraints);
		all_but_extraction_configuration_panels.add(combined_text_panels);
		
		// Add items to the GUI
		setLayout(new BorderLayout(HORIZONTAL_GAP, VERTICAL_GAP));
		add(all_but_extraction_configuration_panels, BorderLayout.CENTER);
		add(extraction_configurations_panel, BorderLayout.SOUTH);

		// Display the GUI
		this.setVisible(true);
	}
		

	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Respond to menu commands.
	 *
	 * @param	event	The event that is to be reacted to.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		// React to the about_menu_item
		if (event.getSource().equals(about_menu_item))
		{
			new AboutDialog( this,
			                 WINDOW_TITLE,
			                 AUTHOR_STRING,
			                 LICENSING_STRING,
			                 INSTITUTION_CREDIT_STRING );
		}

		// React to the help_menu_item
		if (event.getSource().equals(help_menu_item))
			help_dialog.setVisible(true);

		// React to the tutorial_menu_item
		if (event.getSource().equals(tutorial_menu_item))
			tutorial_dialog.setVisible(true);
	}
	
	
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
	public static void formatLabel(JLabel to_format)
	{
		// Change the font, style and/or size
		Font old_font = to_format.getFont();
		Font new_font = new Font(old_font.getFontName(), Font.BOLD, (old_font.getSize() + 1));
		to_format.setFont(new_font);
		
		// Change the alignment
		to_format.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Change the colour
		to_format.setForeground(Color.BLACK);
	}

	
	/**
	 * Add a compound border to the given JPanel, including one which is just empty space for the sake of 
	 * spacing.
	 * 
	 * @param to_format	The JPanel to add the border to.
	 */
	public static void addFormattedBorder(JPanel to_format)
	{
		Border basic_border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border spacing_border = BorderFactory.createEmptyBorder(VERTICAL_GAP, HORIZONTAL_GAP, VERTICAL_GAP, HORIZONTAL_GAP);
		to_format.setBorder(BorderFactory.createCompoundBorder(basic_border, spacing_border));
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