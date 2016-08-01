/*
 * DominantSpreadFeature.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;


/**
 * A feature exractor that finds the largest number of consecutive pitch classes
 * separated by perfect 5ths that accounted for at least 9% each of the notes.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class DominantSpreadFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public DominantSpreadFeature()
     {
          String name = "Dominant Spread";
          String description = "Largest number of consecutive pitch classes separated by\n" +
               "perfect 5ths that accounted for at least 9% each of the notes.";
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
               // Check all streaks
               int max_count = 0;
               for (int bin = 0; bin < sequence_info.fifths_pitch_histogram.length; bin++)
               {
                    if (sequence_info.fifths_pitch_histogram[bin] >= 0.09)
                    {
                         boolean done = false;
                         int count = 1;
                         int i = bin + 1;
                         while (!done)
                         {
                              // Wrap around
                              if (i == sequence_info.fifths_pitch_histogram.length)
                                   i = 0;
                              
                              // If rereach starting point
                              if (i == bin)
                                   done = true;
                              
                              // Increment if sufficient number
                              else if (sequence_info.fifths_pitch_histogram[i] >= 0.09)
                              {
                                   count++;
                                   i++;
                              }
                              
                              // End if not sufficient number
                              else
                                   done = true;
                         }
                         
                         // Record the largest streak
                         if (count > max_count)
                              max_count = count;
                    }
               }
               
               // Calculate the value
               value = (double) max_count;
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}