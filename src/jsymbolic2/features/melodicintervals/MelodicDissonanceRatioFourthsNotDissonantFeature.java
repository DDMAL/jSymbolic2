package jsymbolic2.features.melodicintervals;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the ratio of wrapped melodic intervals that are dissonant (2nds, tritones 
 * and 7ths) to wrapped horizontal intervals that are consonant (unisons, 3rds, 4ths, 5ths, 6ths and octaves).
 * Set to 0 if there are no dissonant melodic intervals or no consonant melodic intervals.
 *
 * @author radamian
 */
public class MelodicDissonanceRatioFourthsNotDissonantFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicDissonanceRatioFourthsNotDissonantFeature()
	{
		String name = "Melodic Dissonance Ratio - Fourths Not Dissonant";
		String code = "M-66";
		String description = "Ratio of wrapped melodic intervals that are dissonant (2nds, tritones and 7ths) to wrapped horizontal intervals that are consonant (unisons, 3rds, 4ths, 5ths, 6ths and octaves). Set to 0 if there are no dissonant melodic intervals or no consonant melodic intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Wrapped Melodic Interval Histogram";
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
			double fraction_dissonant_intervals = 0.0;
			double fraction_consonant_intervals = 0.0;
			
			double[] wrapped_melodic_histogram = other_feature_values[0];
			
			// Sum the fractions of dissonant wrapped melodic intervals
			fraction_dissonant_intervals += wrapped_melodic_histogram[1];
			fraction_dissonant_intervals += wrapped_melodic_histogram[2];
			fraction_dissonant_intervals += wrapped_melodic_histogram[6];
			fraction_dissonant_intervals += wrapped_melodic_histogram[10];
			fraction_dissonant_intervals += wrapped_melodic_histogram[11];
			
			// Sum the fractions of consonant wrapped melodic intervals
			fraction_consonant_intervals += wrapped_melodic_histogram[0];
			fraction_consonant_intervals += wrapped_melodic_histogram[3];
			fraction_consonant_intervals += wrapped_melodic_histogram[4];
			fraction_consonant_intervals += wrapped_melodic_histogram[5];
			fraction_consonant_intervals += wrapped_melodic_histogram[7];
			fraction_consonant_intervals += wrapped_melodic_histogram[8];
			fraction_consonant_intervals += wrapped_melodic_histogram[9];
			
			if (fraction_consonant_intervals == 0.0)
				value = 0.0;
			else
				value = fraction_dissonant_intervals / fraction_consonant_intervals;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}