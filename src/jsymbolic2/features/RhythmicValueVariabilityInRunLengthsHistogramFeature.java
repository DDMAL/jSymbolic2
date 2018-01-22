package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature extractor that calculates a normalized feature vector that indicates, for each rhythmic value,
 * the normalized standard deviation of the number of times that notes with that rhythmic value occur
 * consecutively (either vertically or horizontally) in the same voice (MIDI channel and track). Each bin
 * corresponds to a different rhythmic value, and they are numbered as follows: thirty second notes (or less)
 * [0], sixteenth notes [1], eighth notes [2], dotted eighth notes [3], quarter notes [4], dotted quarter
 * notes [5], half notes [6], dotted half notes [7], whole notes [8], dotted whole notes [9], double whole
 * notes [10] and dotted double whole notes (or more ) [11]. Both pitched and unpitched notes are included in
 * this histogram. Tempo is, of course, not relevant to this histogram. Notes with durations not precisely
 * matching one of these rhythmic note values are mapped to the closest note value (to filter out the effects
 * of rubato or uneven human rhythmic performances, for example). This histogram is calculated without regard
 * to dynamics.
 *
 * @author Cory McKay
 */
public class RhythmicValueVariabilityInRunLengthsHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicValueVariabilityInRunLengthsHistogramFeature()
	{
		code = "R-37";
		String name = "Rhythmic Value Variability in Run Lengths Histogram";
		String description = "A normalized feature vector that indicates, for each rhythmic value, the normalized standard deviation of the number of times that notes with that rhythmic value occur consecutively (either vertically or horizontally) in the same voice (MIDI channel and track). Each bin corresponds to a different rhythmic value, and they are numbered as follows: thirty second notes (or less) [0], sixteenth notes [1], eighth notes [2], dotted eighth notes [3], quarter notes [4], dotted quarter notes [5], half notes [6], dotted half notes [7], whole notes [8], dotted whole notes [9], double whole notes [10] and dotted double whole notes (or more ) [11]. Both pitched and unpitched notes are included in this histogram. Tempo is, of course, not relevant to this histogram. Notes with durations not precisely matching one of these rhythmic note values are mapped to the closest note value (to filter out the effects of rubato or uneven human rhythmic performances, for example). This histogram is calculated without regard to dynamics.";
		boolean is_sequential = true;
		int dimensions = 12;
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
		double[] result = null;
		if (sequence_info != null)
		{
			// Initialize result
			result = new double[sequence_info.runs_of_same_rhythmic_value.length];
			
			// Access the runs
			LinkedList<Integer>[] runs_of_same_rhythmic_value = sequence_info.runs_of_same_rhythmic_value;
			
			// Calculate the values of the histogram bin by bin
			for (int i = 0; i < runs_of_same_rhythmic_value.length; i++)
			{
				double[] run_lengths_for_this_rhythmic_value = new double[runs_of_same_rhythmic_value[i].size()];
				for (int j = 0; j < run_lengths_for_this_rhythmic_value.length; j++)
					run_lengths_for_this_rhythmic_value[j] = (double) runs_of_same_rhythmic_value[i].get(j);
				
				result[i] = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(run_lengths_for_this_rhythmic_value);
			}
			
			// Normalize the histogram
			result = mckay.utilities.staticlibraries.MathAndStatsMethods.normalize(result);
		}
		return result;
	}
}