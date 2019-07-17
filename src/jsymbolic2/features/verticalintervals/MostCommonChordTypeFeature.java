package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the Chord Type Histogram feature bin index corresponding to the Chord 
 * Type Histogram bin with the highest magnitude. The bins are indexed as follows: partial chords consisting 
 * of just two pitch classes [0], minor triads [1], major triads [2], diminished triads [3], augmented triads 
 * [4], other triads [5], minor seventh chords [6], dominant seventh chords [7], major seventh chords [8], 
 * other chords consisting of four pitch classes [9], and complex chords with more than four pitch classes 
 * [10].
 *
 * @author radamian
 */
public class MostCommonChordTypeFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MostCommonChordTypeFeature()
	{
		String name = "Most Common Chord Type";
		String code = "C-64";
		String description = "The Chord Type Histogram feature bin index corresponding to the Chord Type Histogram bin with the highest magnitude. The bins are indexed as follows: partial chords consisting of just two pitch classes [0], minor triads [1], major triads [2], diminished triads [3], augmented triads [4], other triads [5], minor seventh chords [6], dominant seventh chords [7], major seventh chords [8], other chords consisting of four pitch classes [9], and complex chords with more than four pitch classes [10].";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] { "Chord Type Histogram" };
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
			// Get the Chord Type Histogram
			double[] chord_type_histogram = other_feature_values[0];
			
			// Calculate the feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(chord_type_histogram);		
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}