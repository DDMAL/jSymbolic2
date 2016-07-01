/*
 * OuterFrame.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.gui;

import jsymbolic2.configuration.ConfigurationFileData;

import java.awt.*;
import javax.swing.*;


/**
 * A panel holding various components of the jSymbolic Feature Extractor GUI.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class OuterFrame
     extends JFrame
{
     /* FIELDS ****************************************************************/
     
     
     /**
      * A panel allowing the user to select files to extract features from.
      *
      * <p>Symbolic music files may also be played here.
      */
     public	RecordingSelectorPanel  recording_selector_panel;
     
     
     /**
      * A panel allowing the user to select features to extract from symbolic
      * music files and extract the features. Basic feature parameters may be
      * set and feature values and definitions can be saved to disk.
      */
     public	FeatureSelectorPanel    feature_selector_panel;
     
     
     /* CONSTRUCTORS **********************************************************/

     /**
      * Basic constructor that sets up the GUI.
      * @param configFileData Data acquired from the configuration file.
      *                       Null is possible and means that no config file has been specified.
      */
     public OuterFrame(ConfigurationFileData configFileData)
     {
          // Set window title
          setTitle("jSymbolic Symbolic Music Feature Extractor");

          // Make quit when exit box pressed
          setDefaultCloseOperation(EXIT_ON_CLOSE);

          // Instantiate panels
          recording_selector_panel = new RecordingSelectorPanel(this,configFileData);
          feature_selector_panel = new FeatureSelectorPanel(this,configFileData);

          // Add items to GUI
          setLayout(new BorderLayout(8, 8));
          add(recording_selector_panel, BorderLayout.WEST);
          add(feature_selector_panel, BorderLayout.EAST);

          // Display GUI
          pack();
          setVisible(true);
     }
}
