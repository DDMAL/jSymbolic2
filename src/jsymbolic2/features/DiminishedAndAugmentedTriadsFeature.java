package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.ChordTypeEnum;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all simultaneously sounding pitch groups that are either
 * diminished or augmented triads. This is weighted by how long pitch groups are held (e.g. a pitch group
 * lasting a whole note will be weighted four times as strongly as a pitch group lasting a quarter note).
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class DiminishedAndAugmentedTriadsFeature 
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DiminishedAndAugmentedTriadsFeature()
	{
		code = "C-30";
		String name = "Diminished and Augmented Triads";
		String description = "Fraction of all simultaneously sounding pitch groups that are either diminished or augmented triads. This is weighted by how long pitch groups are held (e.g. a pitch group lasting a whole note will be weighted four times as strongly as a pitch group lasting a quarter note).";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[] { "Chord Type Histogram" };
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
			double[] chord_type_histogram = other_feature_values[0];
			value = chord_type_histogram[ChordTypeEnum.DIMINISHED_TRIAD.getChordTypeCode()] +
					chord_type_histogram[ChordTypeEnum.AUGMENTED_TRIAD.getChordTypeCode()];				
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}