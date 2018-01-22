package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the median number of notes of the same rhythmic value that occur
 * consecutively (either vertically or horizontally) in the same voice (MIDI channel and track). This
 * calculation includes both pitched and unpitched notes, is calculated after rhythmic quantization and not
 * influenced by neither tempo nor dynamics.
 * * 
 * @author Cory McKay
 */
public class MedianRhythmicValueRunLengthFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MedianRhythmicValueRunLengthFeature()
	{
		code = "R-35";
		String name = "Median Rhythmic Value Run Length";
		String description = "Median number of notes of the same rhythmic value that occur consecutively (either vertically or horizontally) in the same voice (MIDI channel and track). This calculation includes both pitched and unpitched notes, is calculated after rhythmic quantization and not influenced by neither tempo nor dynamics.";
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
			// Access the runs
			LinkedList<Integer>[] runs_of_same_rhythmic_value = sequence_info.runs_of_same_rhythmic_value;
			LinkedList<Integer> collapsed_runs_of_same_rhythmic_value = new LinkedList<>();
			for (int i = 0; i < runs_of_same_rhythmic_value.length; i++)
				for (int j = 0; j < runs_of_same_rhythmic_value[i].size(); j++)
					collapsed_runs_of_same_rhythmic_value.add(runs_of_same_rhythmic_value[i].get(j));
			double[] array_of_all_runs_of_same_rhythmic_value = new double[collapsed_runs_of_same_rhythmic_value.size()];
			for (int i = 0; i < array_of_all_runs_of_same_rhythmic_value.length; i++)
				array_of_all_runs_of_same_rhythmic_value[i] = (double) collapsed_runs_of_same_rhythmic_value.get(i);
			
			// Calculate the final value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getMedianValue(array_of_all_runs_of_same_rhythmic_value);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}