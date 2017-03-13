package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature extractor that finds the average number of different pitch classes sounding simultaneously. Rests
 * are excluded from this calculation.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class AverageNumberOfSimultaneousPitchClassesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AverageNumberOfSimultaneousPitchClassesFeature()
	{
		code = "C-4";
		String name = "Average Number of Simultaneous Pitch Classes";
		String description = "Average number of different pitch classes sounding simultaneously. Rests are excluded from this calculation.";
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
			short[][] pitch_classes_present_by_tick_excluding_rests = sequence_info.pitch_classes_present_by_tick_excluding_rests;
			
			// Will hold the number of pitches sounding each tick
			short[] number_pitch_classes_by_tick = new short[pitch_classes_present_by_tick_excluding_rests.length];

			// Fill in number_pitches_by_tick tick by tick 
			for (int tick = 0; tick < pitch_classes_present_by_tick_excluding_rests.length; tick++)
				number_pitch_classes_by_tick[tick] = (short) pitch_classes_present_by_tick_excluding_rests[tick].length;
			
			// Find the average of the number of pitches sounding simultaneously
			if (number_pitch_classes_by_tick == null || number_pitch_classes_by_tick.length == 0)
				value = 0.0;
			else
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(number_pitch_classes_by_tick);		
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}