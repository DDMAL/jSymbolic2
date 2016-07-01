/*
 * RecordingInfo.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.datatypes;

import  javax.sound.midi.Sequence;


/**
 * A class for holding references to MIDI recordings.
 *
 * @author Cory McKay
 */
public class RecordingInfo
{
     /* FIELDS ****************************************************************/
     
     
     /**
      * A name used internally to refer to the referenced recording.
      */
     public	String			identifier;
     
     
     /**
      * The path of the MIDI file referred to by objects of this class.
      * Should be unique.
      */
     public	String			file_path;
     
     
     /**
      * A MIDI sequence. May sometimes be set to null in order to store space,
      * in which case they can be extracted from the file referred to in the 
      * file_path field.
      */
     public	Sequence		MIDI_sequence;
     
     
     /**
      * Whether or not a feature extractor receiving	an object of this class
      * should extract features from the referenced file.
      */
     public	boolean			should_extract_features;
     
     
     /* CONSTRUCTORS **********************************************************/
     
     
     /**
      * Basic constructor for filling fields.
      *
      * @param	identifier              A name used internally to refer to the
      *					referenced recording.
      * @param	file_path               The path of the MIDI file referred to by
      *					objects of this class. Should be unique.
      * @param	MIDI_sequence		A MIDI sequence. May sometimes be set to
      *                                 null in order to store space.
      * @param	should_extract_features Whether or not a feature extractor 
      *                                 receiving an object of this class should
      *                                 extract	features from the referenced 
      *                                 file.
      */
     public RecordingInfo( String identifier,
          String file_path,
          Sequence MIDI_sequence,
          boolean should_extract_features )
     {
          this.identifier = identifier;
          this.file_path = file_path;
          this.MIDI_sequence = MIDI_sequence;
          this.should_extract_features = should_extract_features;
     }
     
     
     /**
      * Basic constructor for filling fields. Sets the identifier to the
      * file name extracted from the given file_path, and sets the
      * should_extract_features field to false and the MIDI_sequence field to
      * null.
      *
      * @param	file_path     The path of the MIDI file referred to by objects
      *                       of this class. Should be unique.
      */
     public RecordingInfo(String file_path)
     {
          identifier = mckay.utilities.staticlibraries.StringMethods.convertFilePathToFileName(file_path);
          this.file_path = file_path;
          MIDI_sequence = null;
          should_extract_features = false;
     }
}