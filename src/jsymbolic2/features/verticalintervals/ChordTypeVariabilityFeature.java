package jsymbolic2.features.verticalintervals;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import ace.datatypes.FeatureDefinition;
import java.util.ArrayList;
import javax.sound.midi.Sequence;
import jsymbolic2.featureutils.ChordTypeEnum;

/**
 * A feature calculator that finds the standard deviation of the chord type distribution of the piece (as 
 * defined by the Chord Type Histogram feature). Provides a measure of how close the chords types as a whole 
 * are to the mean chord type.
 *
 * @author radamian, Tristano Tenaglia and Cory McKay
 */
public class ChordTypeVariabilityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ChordTypeVariabilityFeature()
	{
		String name = "Chord Type Variability";
		String code = "C-69";
		String description = "Standard deviation of the chord type distribution of the piece (as defined by the Chord Type Histogram feature). Provides a measure of how close the chords types as a whole are to the mean chord type.";
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
			// The total number of pitch classes
			int number_pitch_classes = 12;
			
			// The combined MIDI velocity of all (non-Channel 10) notes sounding on a MIDI tick by tick basis
			short[][] pitch_strength_by_tick_chart = sequence_info.pitch_strength_by_tick_chart;

			// Iterate tick by tick, creating an array list of all chord type codes in the piece
			ArrayList<Integer> chord_type_list = new ArrayList<Integer>();
			for (int tick = 0; tick < pitch_strength_by_tick_chart.length; tick++)
			{
				// Find the combined MIDI velocity of all (non-Channel 10) pitch classes sounding at this tick
				int[] pitch_class_strengths_this_tick = new int[number_pitch_classes];
				for (int pitch = 0; pitch < pitch_strength_by_tick_chart[tick].length - 1; pitch++)
				{
					int pitch_class = pitch % number_pitch_classes;
					pitch_class_strengths_this_tick[pitch_class] += pitch_strength_by_tick_chart[tick][pitch];
				}

				// Find the type of chord
				ChordTypeEnum chord_type = ChordTypeEnum.getChordType(pitch_class_strengths_this_tick);
				
				if (chord_type != null)
					chord_type_list.add(chord_type.getChordTypeCode());
			}
			
			// Create array of chord type codes for feature calculation
			double[] chord_type_codes = new double[chord_type_list.size()];
			for (int i = 0; i < chord_type_codes.length; i++)
				chord_type_codes[i] = chord_type_list.get(i);

			// Calculate the feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(chord_type_codes);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}