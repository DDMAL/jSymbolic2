/*
 * RhythmicLoosenessFeature.java
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
 * A feature exractor that finds the average width of beat histogram peaks (in 
 * beats per minute). Width is measured for all peaks with frequencies at least 
 * 30% as high as the highest peak, and is defined by the distance between the
 * points on the peak in question that are 30% of the height of the peak.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class RhythmicLoosenessFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public RhythmicLoosenessFeature()
     {
          String name = "Rhythmic Looseness";
          String description = "Average width of beat histogram peaks (in beats per minute).\n" +
               "Width is measured for all peaks with frequencies at least 30%\n" +
               "as high as the highest peak, and is defined by the distance between the\n" +
               "points on the peak in question that are 30% of the height of the peak.";
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
               // Find the number of sufficiently large peaks
               int count = 0;
               for (int bin = 0; bin < sequence_info.rhythmic_histogram_table.length; bin++)
                    if (sequence_info.rhythmic_histogram_table[bin][2] > 0.001)
                         count++;
               
               // Store the peak bins
               int[] peak_bins = new int[count];
               int so_far = 0;
               for (int bin = 0; bin < sequence_info.rhythmic_histogram_table.length; bin++)
                    if (sequence_info.rhythmic_histogram_table[bin][2] > 0.001)
                    {
                    peak_bins[so_far] = bin;
                    so_far++;
                    }
               
               // Find the width of each peak
               double[] widths = new double[peak_bins.length];
               for (int peak = 0; peak < peak_bins.length; peak++)
               {
                    // 30% of this peak
                    double limit_value = 0.3 * sequence_info.rhythmic_histogram[ peak_bins[peak] ];
                    
                    // Find left limit
                    int i = peak_bins[peak];
                    int left_index = 0;
                    while (i >= 0 )
                    {
                         if (sequence_info.rhythmic_histogram[i] < limit_value)
                              i = -1;
                         else
                         {
                              left_index = i;
                              i--;
                         }
                    }
                    
                    // Find right limit
                    i = peak_bins[peak];
                    int right_index = 0;
                    while (i < sequence_info.rhythmic_histogram.length)
                    {
                         if (sequence_info.rhythmic_histogram[i] < limit_value)
                              i = sequence_info.rhythmic_histogram.length + 1;
                         else
                         {
                              right_index = i;
                              i++;
                         }
                    }
                    
                    // Calculate width
                    widths[peak] = (double) right_index - (double) left_index;
               }
               
               // Calculate the value
               value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(widths);
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}