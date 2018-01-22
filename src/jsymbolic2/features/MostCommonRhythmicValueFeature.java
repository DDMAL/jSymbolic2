package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the most common rhythmic value of the music, in quarter note units. So, for
 * example, a Most Common Rhythmic Value of 0.5 would mean that eighth notes occur more frequently than any
 * other rhythmic value. This calculation includes both pitched and unpitched notes, is calculated after
 * rhythmic quantization, is not influenced by tempo, and is calculated without regard to the dynamics, voice
 * or instrument of any given note.
 *
 * @author Cory McKay
 */
public class MostCommonRhythmicValueFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MostCommonRhythmicValueFeature()
	{
		code = "R-26";
		String name = "Most Common Rhythmic Value";
		String description = "The most common rhythmic value of the music, in quarter note units. So, for example, a Most Common Rhythmic Value of 0.5 would mean that eighth notes occur more frequently than any other rhythmic value. This calculation includes both pitched and unpitched notes, is calculated after rhythmic quantization, is not influenced by tempo, and is calculated without regard to the dynamics, voice or instrument of any given note.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[] { "Rhythmic Value Histogram" };
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
			// Initialize
			value = 0.0;
			double[] rhythmic_value_histogram = other_feature_values[0];
			
			// Find the strongest bin
			int most_common_index = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(rhythmic_value_histogram);
			
			// Find the rhythmic value in quarter note units associated with this bin
			switch (most_common_index)
			{
				case 0: value = 1.0 / 8.0; break;
				case 1: value = 1.0 / 4.0; break;
				case 2: value = 1.0 / 2.0; break;
				case 3: value = 3.0 / 4.0; break;
				case 4: value = 1.0; break;
				case 5: value = 1.5; break;
				case 6: value = 2.0; break;
				case 7: value = 3.0; break;
				case 8: value = 4.0; break;
				case 9: value = 6.0; break;
				case 10: value = 8.0; break;
				case 11: value = 12.0; break;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}