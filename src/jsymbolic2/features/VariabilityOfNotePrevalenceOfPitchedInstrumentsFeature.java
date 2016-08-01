/*
 * VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature.java
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
 * A feature exractor that finds the standard deviation of the fraction of Note
 * Ons played by each (pitched) General MIDI instrument that is used to play at
 * least one note.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature()
     {
          String name = "Variability of Note Prevalence of Pitched Instruments";
          String description = "Standard deviation of the fraction of Note Ons played by\n" +
               "each (pitched) General MIDI instrument that is used to play\n" +
               "at least one note.";
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
               // Find the number of pitched instruments used to play at least one note
               int instruments_present = 0;
               for (int instrument = 0; instrument < sequence_info.pitched_instrumentation_frequencies.length; instrument++)
                    if (sequence_info.pitched_instrumentation_frequencies[instrument][0] != 0)
                         instruments_present++;
               
               // Calculate the feature value
               double[] instrument_frequencies = new double[instruments_present];
               int count = 0;
               for (int instrument = 0; instrument < sequence_info.pitched_instrumentation_frequencies.length; instrument++)
                    if (sequence_info.pitched_instrumentation_frequencies[instrument][0] != 0)
                    {
                    instrument_frequencies[count] = (double) sequence_info.pitched_instrumentation_frequencies[instrument][0];
                    count++;
                    }
               value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(instrument_frequencies);
          }
          else value = -1.0;
          
          double[] result = new double[1];
          result[0] = value;
          return result;
     }
}