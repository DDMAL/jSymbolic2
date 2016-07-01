/*
 * SizeOfMelodicArcsFeature.java
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
 * A feature exractor that finds the average melodic interval separating the top
 * note of melodic peaks and the bottom note of melodic troughs.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class SizeOfMelodicArcsFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public SizeOfMelodicArcsFeature()
     {
          String name = "Size of Melodic Arcs";
          String description = "Average melodic interval separating the top note of melodic\n" +
               "peaks and the bottom note of melodic troughs.";
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
               int total_intervals = 0;
               int number_intervals = 0;
               for (int chan = 0; chan < sequence_info.melody_list.length; chan++)
               {
                    if (chan != (10 - 1))
                    {
                         // Convert to array
                         Object[] list_contents = sequence_info.melody_list[chan].toArray();
                         int[] intervals = new int[list_contents.length];
                         for (int i = 0; i < intervals.length; i++)
                              intervals[i] = ((Integer) list_contents[i]).intValue();
                         
                         // Find the number of arcs
                         int direction = 0;
                         int interval_so_far = 0;
                         for (int i = 0; i < intervals.length; i++)
                         {
                              // If arc is currently decending
                              if (direction == -1)
                              {
                                   if (intervals[i] < 0)
                                        interval_so_far += Math.abs(intervals[i]);
                                   else if (intervals[i] > 0)
                                   {
                                        total_intervals += interval_so_far;
                                        number_intervals++;
                                        interval_so_far = Math.abs(intervals[i]);
                                        direction = 1;
                                   }
                              }
                              
                              // If arc is currently ascending
                              else if (direction == 1)
                              {
                                   if (intervals[i] > 0)
                                        interval_so_far += Math.abs(intervals[i]);
                                   else if (intervals[i] < 0)
                                   {
                                        total_intervals += interval_so_far;
                                        number_intervals++;
                                        interval_so_far = Math.abs(intervals[i]);
                                        direction = -1;
                                   }
                              }
                              
                              // If arc is currently stationary
                              else if (direction == 0)
                              {
                                   if (intervals[i] > 0)
                                   {
                                        direction = 1;
                                        interval_so_far += Math.abs(intervals[i]);
                                   }
                                   if (intervals[i] < 0)
                                   {
                                        direction = -1;
                                        interval_so_far += Math.abs(intervals[i]);
                                   }
                              }
                         }
                    }
               }
               
               // Calculate the value
               value = (double) total_intervals / (double) number_intervals;
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}