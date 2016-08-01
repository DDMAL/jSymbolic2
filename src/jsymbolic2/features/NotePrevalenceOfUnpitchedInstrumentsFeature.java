/*
 * NotePrevalenceOfUnpitchedInstrumentsFeature.java
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
 * A feature exractor that finds the fraction of (unpitched) notes played by 
 * each General MIDI Percussion Key Map Instrument. There is one entry for each
 * instrument, which is set to the number of Note Ons played using the 
 * corresponding MIDI note value divided by the total number of Note Ons in the
 * recording.
 *
 * It should be noted that only instruments 35 to 81 are included here, as they 
 * are the ones that meet the official standard. They are numbered in this array 
 * from 0 to 46.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class NotePrevalenceOfUnpitchedInstrumentsFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public NotePrevalenceOfUnpitchedInstrumentsFeature()
     {
          String name = "Note Prevalence of Unpitched Instruments";
          String description = "The fraction of (unpitched) notes played by each General MIDI Percussion\n" +
               "Key Map Instrument. There is one entry for each instrument, which is set\n" +
               "to the number of Note Ons played using the corresponding MIDI note\n" +
               "value divided by the total number of Note Ons in the recording.\n" +
               "\nIt should be noted that only instruments 35 to 81 are\n" +
               "included here, as they are the ones that meet the official\n" +
               "standard. They are numbered in this array from 0 to 46.";
          boolean is_sequential = true;
          int dimensions = 47;
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
          double[] result = null;
          if (sequence_info != null)
          {
               result = new double[47];
               for (int instrument = 35; instrument < 82; instrument++)
                    result[instrument - 35] = sequence_info.non_pitched_instrumentation_frequencies[instrument] /
                         (double) sequence_info.total_number_notes;
          }
          return result;
     }
}