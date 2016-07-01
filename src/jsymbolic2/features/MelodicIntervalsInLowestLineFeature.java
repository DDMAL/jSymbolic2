/*
 * MelodicIntervalsInLowestLineFeature.java
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
 * A feature exractor that finds the average melodic interval in semitones of
 * the channel with the lowest average pitch divided by the average melodic 
 * interval of all channels that contain at least two notes.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class MelodicIntervalsInLowestLineFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public MelodicIntervalsInLowestLineFeature()
     {
          String name = "Melodic Intervals in Lowest Line";
          String description = "Average melodic interval in semitones of the channel with\n" +
               "the lowest average pitch divided by the average melodic\n" +
               "interval of all channels that contain at least two notes.";
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
               // Find the channel with the lowest average pitch
               int min_so_far = 0;
               int lowest_chan = 0;
               for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
                    if (sequence_info.channel_statistics[chan][6] != 0 && chan != (10 - 1))
                         if (sequence_info.channel_statistics[chan][6] < min_so_far)
                         {
                    min_so_far = sequence_info.channel_statistics[chan][6];
                    lowest_chan = chan;
                         }
               
               // Find the number of channels with no note ons (or that is channel 10 or that is the highest channel)
               int silent_count = 0;
               for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
                    if (sequence_info.channel_statistics[chan][0] == 0 || chan == (10 - 1))
                         silent_count++;
               
               // Find the average melodic interval of notes in the other channels
               double[] intervals = new double[sequence_info.channel_statistics.length - silent_count];
               int count = 0;
               for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
                    if (sequence_info.channel_statistics[chan][0] != 0 && chan != (10 - 1))
                    {
                    intervals[count] = (double) sequence_info.channel_statistics[chan][3];
                    count++;
                    }
               double average = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(intervals);
               
               // Set value
               value = ((double) sequence_info.channel_statistics[lowest_chan][3]) / ((double) average);
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}