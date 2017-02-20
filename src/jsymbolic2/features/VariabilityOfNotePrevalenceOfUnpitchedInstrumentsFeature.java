package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the fraction of total notes in the piece played
 * by each (unpitched) MIDI Percussion Key Map instrument that is used to play at least one note. It should be
 * noted that only MIDI Channel 10 instruments 35 to 81 are included here, as they are the ones that meet the
 * official standard.
 *
 * @author Cory McKay
 */
public class VariabilityOfNotePrevalenceOfUnpitchedInstrumentsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfNotePrevalenceOfUnpitchedInstrumentsFeature()
	{
		code = "I-7";
		String name = "Variability of Note Prevalence of Unpitched Instruments";
		String description = "Standard deviation of the fraction of total notes in the piece played by each (unpitched) MIDI Percussion Key Map instrument that is used to play at least one note. It should be noted that only MIDI Channel 10 instruments 35 to 81 are included here, as they are the ones that meet the official standard.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
		offsets = null;
	}
	

	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Extract this feature from the given sequence of MIDI data and its associated information.
	 *
	 * @param sequence				The MIDI data to extract the feature from.
	 * @param sequence_info			Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that may be needed to calculate this feature. 
	 *								The order and offsets of these features must be the same as those returned
	 *								by this class' getDependencies and getDependencyOffsets methods, 
	 *								respectively. The first indice indicates the feature/window, and the 
	 *								second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if the feature cannot be calculated.
	 */
	@Override
	public double[] extractFeature( Sequence sequence,
									MIDIIntermediateRepresentations sequence_info,
									double[][] other_feature_values )
	throws Exception
	{
		double value;
		if (sequence_info != null)
		{
			// Find the number of unpitched instruments used to play at least one note
			int instruments_present = 0;
			for (int instrument = 35; instrument < 82; instrument++)
			{
				if (sequence_info.non_pitched_instrument_prevalence[instrument] != 0)
				{
					instruments_present++;
				}
			}

			// Calculate the feature value
			double[] instrument_frequencies = new double[instruments_present];
			int count = 0;
			for (int instrument = 35; instrument < 82; instrument++)
			{
				if (sequence_info.non_pitched_instrument_prevalence[instrument] != 0)
				{
					instrument_frequencies[count] = (double) sequence_info.non_pitched_instrument_prevalence[instrument];
					count++;
				}
			}
			if (instrument_frequencies == null || instrument_frequencies.length == 0)
				value = 0.0;
			else 
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(instrument_frequencies);
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}