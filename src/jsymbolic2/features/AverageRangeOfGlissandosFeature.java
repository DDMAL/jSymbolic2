/*
 * AverageRangeOfGlissandosFeature.java
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
 * A feature exractor that extracts the average range of Pitch Bends, where 
 * range is defined as the greatest value of the absolute difference between 64
 * and the second data byte of all MIDI Pitch Bend messages falling between the
 * Note On and Note Off messages of any note.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class AverageRangeOfGlissandosFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public AverageRangeOfGlissandosFeature()
     {
          String name = "Average Range of Glissandos";
          String description = "Average range of Pitch Bends, where range is defined as the\n" +
               "greatest value of the absolute difference between 64 and the\n" +
               "second data byte of all MIDI Pitch Bend messages falling between" +
               "the Note On and Note Off messages of any note.";
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
                    
                    // Find the range of the bend for each note
                    double[] greatest_differences = new double[pitch_bends.length];
                    for (int note = 0; note < greatest_differences.length; note++)
                    {
                         int greatest_so_far = 0;
                         for (int bend = 0; bend < pitch_bends[note].length; bend++)
                              if (Math.abs( 64 - pitch_bends[note][bend] ) > greatest_so_far)
                                   greatest_so_far = Math.abs( 64 - pitch_bends[note][bend] );
                         greatest_differences[note] = (double) greatest_so_far;
                    }
                    
                    // Calculate the value
                    value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(greatest_differences);
               }
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}