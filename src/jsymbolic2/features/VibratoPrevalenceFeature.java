/*
 * VibratoPrevalenceFeature.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;


/**
 * A feature exractor that finds the number of notes for which Pitch Bend
 * messages change direction at least twice divided by total number of notes
 * that have Pitch Bend messages associated with them.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class VibratoPrevalenceFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public VibratoPrevalenceFeature()
     {
          String name = "Vibrato Prevalence";
          String description = "Number of notes for which Pitch Bend messages change direction\n" +
               "at least twice divided by total number of notes that have Pitch\n" +
               "Bend messages associated with them.";
          boolean is_sequential = true;
          int dimensions = 1;
          definition = new FeatureDefinition( name,
               description,
               is_sequential,
               dimensions );
          
          dependencies = null;
          
          offsets = null;
     }
     
     
     /* PUBLIC METHODS ********************************************************/
     
     
     /**
      * Extracts this feature from the given MIDI sequence given the other
      * feature values.
      *
      * <p>In the case of this feature, the other_feature_values parameters
      * are ignored.
      *
      * @param sequence			The MIDI sequence to extract the feature
      *                                 from.
      * @param sequence_info		Additional data about the MIDI sequence.
      * @param other_feature_values	The values of other features that are
      *					needed to calculate this value. The
      *					order and offsets of these features
      *					must be the same as those returned by
      *					this class's getDependencies and
      *					getDependencyOffsets methods
      *                                 respectively. The first indice indicates
      *                                 the feature/window and the second
      *                                 indicates the value.
      * @return				The extracted feature value(s).
      * @throws Exception		Throws an informative exception if the
      *					feature cannot be calculated.
      */
     public double[] extractFeature( Sequence sequence,
          MIDIIntermediateRepresentations sequence_info,
          double[][] other_feature_values )
          throws Exception
     {
          double value;
          if (sequence_info != null)
          {
               // If there are no pitch bends
               if (sequence_info.pitch_bends_list.size() == 0)
                    value = 0.0;
               
               else
               {
                    // Generate array of pitch bends
                    Object[] notes_objects = sequence_info.pitch_bends_list.toArray();
                    LinkedList[] notes = new LinkedList[notes_objects.length];
                    for (int i = 0; i < notes.length; i++)
                         notes[i] = (LinkedList) notes_objects[i];
                    int[][] pitch_bends = new int[notes.length][];
                    for (int i = 0; i < notes.length; i++)
                    {
                         Object[] this_note_pitch_bends_objects = notes[i].toArray();
                         pitch_bends[i] = new int[this_note_pitch_bends_objects.length];
                         for (int j = 0; j < pitch_bends[i].length; j++)
                              pitch_bends[i][j] = ((Integer) this_note_pitch_bends_objects[j]).intValue();
                    }
                    
                    // Find the number of changes of direction of bend bend for each note
                    int notes_with_vibrato = 0;;
                    for (int note = 0; note < pitch_bends.length; note++)
                    {
                         int changes = 0;
                         int last_value = pitch_bends[note][0];
                         int direction = 0;
                         for (int bend = 0; bend < pitch_bends[note].length; bend++)
                         {
                              if (pitch_bends[note][bend] > last_value)
                              {
                                   if (direction == -1)
                                        changes++;
                                   direction = 1;
                              }
                              else if (pitch_bends[note][bend] < last_value)
                              {
                                   if (direction == 1)
                                        changes++;
                                   direction = -1;
                              }
                         }
                         
                         if (changes > 2)
                              notes_with_vibrato++;
                    }
                    
                    // Calculate the value
                    value = (double) notes_with_vibrato / (double) pitch_bends.length;
               }
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}