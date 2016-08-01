/*
 * PrimaryRegisterFeature.java
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
 * A feature exractor that finds the average MIDI pitch.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class PrimaryRegisterFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public PrimaryRegisterFeature()
     {
          String name = "Primary Register";
          String description = "Average MIDI pitch.";
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
               // Find the total pitch
               double pitch_total = 0;
               for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
                    if (chan != (10 - 1))
                         pitch_total += sequence_info.channel_statistics[chan][6] *
                              sequence_info.channel_statistics[chan][0];
               
               // Calculate the value
               value = pitch_total / (double) sequence_info.total_number_pitched_notes;
               
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}