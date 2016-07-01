/*
 * AverageNumberOfIndependentVoicesFeature.java
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
 * A feature exractor that extracts the average number of different channels in 
 * which notes have sounded simultaneously. Rests are not included in this 
 * calculation.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class AverageNumberOfIndependentVoicesFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public AverageNumberOfIndependentVoicesFeature()
     {
          String name = "Average Number of Independent Voices";
          String description = "Average number of different channels in which notes have\n" +
               "sounded simultaneously. Rests are not included in this\n" +
               "calculation.";
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
               // Instantiate of the variable holding the number of voices sounding at each tick
               int[] number_sounding = new int[sequence_info.channel_tick_map.length];
               for (int i = 0; i < number_sounding.length; i++)
                    number_sounding[i] = 0;
               
               // Find the number of voices sounding at each tick
               int rest_count = 0;
               for (int tick = 0; tick < sequence_info.channel_tick_map.length; tick++)
               {
                    for (int chan = 0; chan < sequence_info.channel_tick_map[tick].length; chan++)
                         if (sequence_info.channel_tick_map[tick][chan])
                              number_sounding[tick]++;
                    
                    // Keep track of number of ticks with no notes sounding
                    if (number_sounding[tick] == 0 )
                         rest_count++;
               }
               
               // Only count the ticks where at least one note was sounding
               double[] final_number_sounding = new double[number_sounding.length - rest_count];
               int count = 0;
               for (int i = 0; i < number_sounding.length; i++)
                    if (number_sounding[i] > 0.5)
                    {
                    final_number_sounding[count] = (double) number_sounding[i];
                    count++;
                    }
               
               // Calculate the average
               value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(final_number_sounding);
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}