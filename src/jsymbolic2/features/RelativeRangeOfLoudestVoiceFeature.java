/*
 * RelativeRangeOfLoudestVoiceFeature.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;


/**
 * A feature exractor that finds the difference between the highest note and the
 * lowest note played in the channel with the highest average loudness divided
 * by the difference between the highest note and the lowest note overall in the
 * piece.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class RelativeRangeOfLoudestVoiceFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public RelativeRangeOfLoudestVoiceFeature()
     {
          String name = "Relative Range of Loudest Voice";
          String description = "Difference between the highest note and the lowest note played in\n" +
               "the channel with the highest average loudness divided by the difference\n" +
               "between the highest note and the lowest note overall in the piece.";
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
               // Find the loudest channel
               int max_so_far = 0;
               int loudest_chan = 0;
               for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
                    if (sequence_info.channel_statistics[chan][0] != 0 && chan != (10 - 1))
                         if (sequence_info.channel_statistics[chan][2] > max_so_far)
                         {
                    max_so_far = sequence_info.channel_statistics[chan][2];
                    loudest_chan = chan;
                         }
               
               // Find the range of the loudest channel
               double loudest_range = (double) (sequence_info.channel_statistics[loudest_chan][5] -
                    sequence_info.channel_statistics[loudest_chan][4]);
               
               // Finde the overall range
               int lowest = 127;
               int highest = 0;
               for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
                    if (sequence_info.channel_statistics[chan][0] != 0 && chan != (10 - 1))
                    {
                    if (sequence_info.channel_statistics[chan][4] < lowest)
                         lowest = sequence_info.channel_statistics[chan][4];
                    if (sequence_info.channel_statistics[chan][5] > highest)
                         highest = sequence_info.channel_statistics[chan][5];
                    }
               
               // Set value
               value = loudest_range / ((double) (highest - lowest));
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}