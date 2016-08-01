/*
 * PercussionPrevalenceFeature.java
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
 * A feature exractor that finds the total number of Note Ons corresponding to
 * unpitched percussion instruments divided by total number of Note Ons in the
 * recording.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class PercussionPrevalenceFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public PercussionPrevalenceFeature()
     {
          String name = "Percussion Prevalence";
          String description = "Total number of Note Ons corresponding to unpitched percussion\n " +
               "instruments divided by total number of Note Ons in the recording.";
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
               int total_number_note_ons = 0;
               for (int channel = 0; channel < sequence_info.channel_statistics.length; channel++)
                    total_number_note_ons += sequence_info.channel_statistics[channel][0];
               
               int number_unpitched_note_ons =  sequence_info.channel_statistics[10 - 1][0];
               
               value = (double) number_unpitched_note_ons / (double) total_number_note_ons;
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}