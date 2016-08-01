/*
 * VariabilityOfTimeBetweenAttacksFeature.java
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
 * A feature exractor that finds the standard deviation of the times, in
 * seconds, between Note On events (irregardless of channel).
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class VariabilityOfTimeBetweenAttacksFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public VariabilityOfTimeBetweenAttacksFeature()
     {
          String name = "Variability of Time Between Attacks";
          String description = "Standard deviation of the times, in seconds, between Note On\n" +
               "events (irregardless of channel).";
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
               // Find number of intervals
               int number_of_intervals = -1;
               for (int tick = 0; tick < sequence_info.note_beginnings_map.length; tick++)
               {
                    // Check if an attack occured on this tick
                    boolean attack = false;
                    for (int chan = 0; chan < 16; chan++)
                    {
                         if (sequence_info.note_beginnings_map[tick][chan])
                         {
                              attack = true;
                              chan = 17; // exit the loop
                         }
                    }
                    
                    if (attack)
                         number_of_intervals++;
               }
               
               // Added check for NegativeArraySizeException
               double[] intervals;
               if(number_of_intervals < 0)
               {
                   intervals = new double[0]; //this array is empty
               }
               else
               {
                   // Fill in the array of intervals
                   intervals = new double[number_of_intervals];
               }
               number_of_intervals = 0;
               double time_so_far = 0.0;
               int tick_of_last_attack  = -1;
               for (int tick = 0; tick < sequence_info.note_beginnings_map.length; tick++)
               {
                    // Check if an attack occured on this tick
                    boolean attack = false;
                    for (int chan = 0; chan < 16; chan++)
                    {
                         if (sequence_info.note_beginnings_map[tick][chan])
                         {
                              attack = true;
                              chan = 17; // exit the loop
                         }
                    }
                    
                    if (!attack)
                         time_so_far += sequence_info.seconds_per_tick[tick];
                    else
                    {
                         if (tick_of_last_attack != -1)
                         {
                              intervals[number_of_intervals] = time_so_far;
                              number_of_intervals++;
                         }
                         
                         time_so_far = 0.0;
                         tick_of_last_attack = tick;
                    }
               }
               
               value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(intervals);
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}