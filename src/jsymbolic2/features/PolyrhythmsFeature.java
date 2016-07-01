/*
 * PolyrhythmsFeature.java
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
 * A feature exractor that finds the number of beat peaks with frequencies at
 * least 30% of the highest frequency whose bin labels are not integer multiples
 * or factors (using only multipliers of 1, 2, 3, 4, 6 and 8) (with an accepted
 * error of +/- 3 bins) of the bin label of the peak with the highest frequency. 
 * This number is then divided by the total number of beat bins with frequencies 
 * over 30% of the highest frequency.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class PolyrhythmsFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public PolyrhythmsFeature()
     {
          String name = "Polyrhythms";
          String description = "Number of beat peaks with frequencies at least 30% of the highest\n" +
               "frequency whose bin labels are not integer multiples or factors\n" +
               "(using only multipliers of 1, 2, 3, 4, 6 and 8) (with an accepted error\n" +
               "of +/- 3 bins) of the bin label of the peak with the highest frequency.\n" +
               "This number is then divided by the total number of beat bins with\n" +
               "frequencies over 30% of the highest frequency.";
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
               
               // Find the highest peak
               int highest_index = 0;
               double max_so_far = 0.0;
               for (int bin = 0; bin < peak_bins.length; bin++)
                    if (sequence_info.rhythmic_histogram_table[ peak_bins[bin] ][2] > max_so_far)
                    {
                    max_so_far = sequence_info.rhythmic_histogram_table[ peak_bins[bin] ][2];
                    highest_index = peak_bins[bin];
                    }
               
               // Find the number of peak bins which are multiples or factors of the highest bin
               int hits = 0;
               for (int i = 0; i < peak_bins.length; i++)
               {
                    int left_limit = peak_bins[i] - 3;
                    if (left_limit < 0)
                         left_limit = 0;
                    
                    int right_limit = peak_bins[i] + 4;
                    if (right_limit > sequence_info.rhythmic_histogram_table.length)
                         right_limit = sequence_info.rhythmic_histogram_table.length;
                    
                    int[] multipliers = {1, 2, 3, 4, 6, 8};
                    for (int j = left_limit; j < right_limit; j++)
                    {
                         if (mckay.utilities.staticlibraries.MathAndStatsMethods.isFactorOrMultiple(j, highest_index, multipliers))
                         {
                              hits++;
                              left_limit = right_limit + 1; // exit loop
                         }
                    }
               }
               
               // Calculate the value
               value = (double) hits / (double) peak_bins.length;
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}