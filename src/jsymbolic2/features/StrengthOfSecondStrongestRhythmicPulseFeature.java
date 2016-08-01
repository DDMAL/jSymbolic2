/*
 * StrengthOfSecondStrongestRhythmicPulseFeature.java
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
 * A feature exractor that finds the frequency of the beat bin of the peak with
 * the second highest frequency.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class StrengthOfSecondStrongestRhythmicPulseFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public StrengthOfSecondStrongestRhythmicPulseFeature()
     {
          String name = "Strength of Second Strongest Rhythmic Pulse";
          String description = "Frequency of the beat bin of the peak with the second highest frequency.";
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
               // Find the highest bin
               double max = 0.0;
               int max_index = 0;
               for (int bin = 0; bin < sequence_info.rhythmic_histogram.length; bin++)
                    if (sequence_info.rhythmic_histogram[bin] > max)
                    {
                    max = sequence_info.rhythmic_histogram[bin];
                    max_index = bin;
                    }
               
               // Find the second highest bin
               double second_highest = 0.0;
               int second_hidhest_index = 0;
               for (int bin = 0; bin < sequence_info.rhythmic_histogram_table.length; bin++)
                    if (sequence_info.rhythmic_histogram_table[bin][1] > second_highest && bin != max_index)
                    {
                    second_highest = sequence_info.rhythmic_histogram_table[bin][1];
                    second_hidhest_index = bin;
                    }
               
               // Calculate the value
               value = second_highest;
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}