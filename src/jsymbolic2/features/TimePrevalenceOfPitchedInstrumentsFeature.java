/*
 * TimePrevalenceOfPitchedInstrumentsFeature.java
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
 * A feature exractor that finds the fraction of the total time of the recording
 * in which a note was sounding for each (pitched) General MIDI Instrument.
 * There is one entry for each instrument, which is set to the total time in
 * seconds during which a given instrument was sounding one or more notes
 * divided by the total length in seconds of the piece.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class TimePrevalenceOfPitchedInstrumentsFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public TimePrevalenceOfPitchedInstrumentsFeature()
     {
          String name = "Time Prevalence of Pitched Instruments";
          String description = "The fraction of the total time of the recording in which a note was\n" +
               "sounding for each (pitched) General MIDI Instrument.\n" +
               "There is one entry for each instrument, which is set to the total\n" +
               "time in seconds during which a given instrument was sounding one\n" +
               "or more notes divided by the total length in seconds of the piece.";
          boolean is_sequential = true;
          int dimensions = 128;
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
               result = new double[128];
               for (int instrument = 0; instrument < result.length; instrument++)
                    result[instrument] = sequence_info.pitched_instrumentation_frequencies[instrument][1] /
                         (double) sequence_info.recording_length;
          }
          return result;
     }
}