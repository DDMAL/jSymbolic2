package jsymbolic2.features.rhythm;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature extractor that calculates a feature vector representing a normalized histogram where the value of
 * each bin specifies the fraction of all notes in the music with a rhythmic value corresponding to that of
 * the given bin. The bins are numbered as follows: thirty second notes (or less) [0], sixteenth notes [1],
 * eighth note triplets [2], eighth notes [3], quarter note triplets [4], dotted eighth notes [5], quarter 
 * notes [6], dotted quarter notes [7], half notes [8], dotted half notes [9], whole notes [10], dotted whole 
 * notes [11], double whole notes [12], and dotted double whole notes (or more) [13]. Both pitched and 
 * unpitched notes are included in this histogram. Tempo is, of course, not relevant to this histogram. Notes 
 * with durations not precisely matching one of these rhythmic note values are mapped to the closest note 
 * value (to filter out the effects of rubato or uneven human rhythmic performances, for example). This 
 * histogram is calculated without regard to the dynamics, voice or instrument of any given note.
 *
 * @author Cory McKay and radamian
 */
public class RhythmicValueHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicValueHistogramFeature()
	{
		String name = "Rhythmic Value Histogram";
		String code = "R-14";
		String description = "A feature vector, representing a normalized histogram where the value of each bin specifies the fraction of all notes in the music with a rhythmic value corresponding to that of the given bin. The bins are numbered as follows: thirty second notes (or less) [0], sixteenth notes [1], eighth note triplets [2], eighth notes [3], quarter note triplets [4], dotted eighth notes [5], quarter notes [6], dotted quarter notes [7], half notes [8], dotted half notes [9], whole notes [10], dotted whole notes [11], double whole notes [12], and dotted double whole notes (or more) [13]. Both pitched and unpitched notes are included in this histogram. Tempo is, of course, not relevant to this histogram. Notes with durations not precisely matching one of these rhythmic note values are mapped to the closest note value (to filter out the effects of rubato or uneven human rhythmic performances, for example). This histogram is calculated without regard to the dynamics, voice or instrument of any given note.";
		boolean is_sequential = true;
		int dimensions = 12;
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
		double[] result = null;
		if (sequence_info != null)
		{
			result = new double[sequence_info.rhythmic_value_histogram.length];
			for (int value = 0; value < result.length; value++)
				result[value] = sequence_info.rhythmic_value_histogram[value];
		}
		return result;
	}
}