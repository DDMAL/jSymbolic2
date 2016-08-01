/*
 * VariationOfDynamicsInEachVoiceFeature.java
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
 * A feature exractor that finds the average of the standard deviations of
 * loudness levels within each channel that contains at least one note.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class VariationOfDynamicsInEachVoiceFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public VariationOfDynamicsInEachVoiceFeature()
     {
          String name = "Variation of Dynamics In Each Voice";
          String description = "The average of the standard deviations of loudness levels within\n" +
               "each channel that contains at least one note.";
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
               // Find number of channels with notes
               int number_with_notes = 0;
               for (int i = 0; i < sequence_info.channel_statistics.length; i++)
                    if (sequence_info.channel_statistics[i][0] != 0)
                         number_with_notes++;
               
               // Calculate the standard deviations
               double[] standard_deviations = new double[number_with_notes];
               int count = 0;
               for (int i = 0; i < sequence_info.note_loudnesses.length; i++)
               {
                    if (sequence_info.note_loudnesses[i].length > 0)
                    {
                         double[] loudnesses = new double[sequence_info.note_loudnesses[i].length];
                         for (int j = 0; j < sequence_info.note_loudnesses[i].length; j++)
                              loudnesses[j] = (double) sequence_info.note_loudnesses[i][j];
                         standard_deviations[count] = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(loudnesses);
                         count++;
                    }
               }
               
               // Calculate the average standard deviation
               value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(standard_deviations);
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}