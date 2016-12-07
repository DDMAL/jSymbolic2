package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of the music by time where at least one wrapped vertical major
 * third is sounding (regardless of whatever other vertical intervals may or may not be sounding at the same
 * time). Only that part of the music where one or more pitched notes is sounding is included in this
 * calculation (rests and sections containing only unpitched notes are ignored).
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class VerticalMajorThirdPrevalenceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VerticalMajorThirdPrevalenceFeature()
	{
		code = "C-26";
		String name = "Vertical Major Third Prevalence";
		String description = "Fraction of the music by time where at least one wrapped vertical major third is sounding (regardless of whatever other vertical intervals may or may not be sounding at the same time). Only that part of the music where one or more pitched notes is sounding is included in this calculation (rests and sections containing only unpitched notes are ignored).";
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
			// All MIDI pitches (NOT including Channel 10 unpitched notes sounding at each MIDI tick, with
			// ticks with no sounding notes excluded.
			short[][] pitches_present_by_tick_excluding_rests = sequence_info.pitches_present_by_tick_excluding_rests;

			double ticks_with_major_third = 0.0;
			
			for (int tick = 0; tick < pitches_present_by_tick_excluding_rests.length; tick++)
			{
				// The MIDI pitch numbers of all pitches found this tick
				short[] pitches_this_tick = pitches_present_by_tick_excluding_rests[tick];
				
				// Update ticks_with_major_third to find the number of ticks that contain a wrapped minor
				// third
				if (pitches_this_tick.length > 1)
				{
					for (int i = 0; i < pitches_this_tick.length; i++)
					{
						for (int j = 0; j < i; j++)
						{
							if ( (pitches_this_tick[i] - pitches_this_tick[j]) % 12 == 4 )
							{
								ticks_with_major_third++;
								i = pitches_this_tick.length;
								j = i;
							}
						}
					}
				}
			}

			// Calculate the fraction
			if (pitches_present_by_tick_excluding_rests.length == 0)
				value = 0.0;
			else 
				value = ticks_with_major_third / (double) pitches_present_by_tick_excluding_rests.length; 
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}