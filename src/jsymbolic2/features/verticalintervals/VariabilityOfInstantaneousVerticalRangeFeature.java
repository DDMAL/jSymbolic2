package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import java.util.ArrayList;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the interval (in semitones) between the lowest 
 * pitch and the highest pitch sounding simultaneously. Calculated MIDI tick-by-tick, so is influenced by how 
 * long intervals are held. Rests are ignored in the calculation of this feature, but if only one note is 
 * sound at a given moment then this counts as a value of 0 for the purposes of calculating this feature.
 *
 * @author radamian
 */
public class VariabilityOfInstantaneousVerticalRangeFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfInstantaneousVerticalRangeFeature()
	{
		String name = "Variability of Instantaneous Vertical Range";
		String code = "C-59";
		String description = "Standard deviation of the interval (in semitones) between the lowest pitch and the highest pitch sounding simultaneously. Calculated MIDI tick-by-tick, so is influenced by how long intervals are held. Rests are ignored in the calculation of this feature, but if only one note is sound at a given moment then this counts as a value of 0 for the purposes of calculating this feature.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
		offsets = null;
		is_default = true;
		is_secure = true;
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
			// Get the pitches_present_by_tick_excluding_rests table
			short[][] pitches_present_by_tick_excluding_rests = sequence_info.pitches_present_by_tick_excluding_rests;
			
			// Create array of all vertical ranges in the piece
			int[] vertical_ranges = new int[pitches_present_by_tick_excluding_rests.length];
			for (int tick = 0; tick < pitches_present_by_tick_excluding_rests.length; tick++)
			{
				if (pitches_present_by_tick_excluding_rests[tick].length > 1)
				{
					int last_index = pitches_present_by_tick_excluding_rests[tick].length - 1;
					int interval = pitches_present_by_tick_excluding_rests[tick][last_index] - pitches_present_by_tick_excluding_rests[tick][0];
					vertical_ranges[tick] = interval;
				}
				else
				{
					// If there is only one pitch present on a tick, then the vertical range is 0
					vertical_ranges[tick] = 0;
				}
			}

			// Calculate the feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(vertical_ranges);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}