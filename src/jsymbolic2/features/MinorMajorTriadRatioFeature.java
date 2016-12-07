package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.ChordTypeEnum;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the prevalence of minor triads divided by the prevalence of major triads.
 * This is weighted by how long the chords are held (e.g. a chord lasting a whole note will be weighted four
 * times as strongly as a chord lasting a quarter note). Set to 0 if there are no minor triads or if there
 * are no major triads.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class MinorMajorTriadRatioFeature 
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MinorMajorTriadRatioFeature()
	{
		code = "C-35";
		String name = "Minor Major Triad Ratio";
		String description = "The prevalence of minor triads divided by the prevalence of major triads. This is weighted by how long the chords are held (e.g. a chord lasting a whole note will be weighted four times as strongly as a chord lasting a quarter note). Set to 0 if there are no minor triads or if there are no major triads.";
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
			
			if ( chord_type_histogram[ChordTypeEnum.MINOR_TRIAD.getChordTypeCode()] == 0.0 || 
			     chord_type_histogram[ChordTypeEnum.MAJOR_TRIAD.getChordTypeCode()] == 0.0 )
				value = 0.0;
			else
				value = chord_type_histogram[ChordTypeEnum.MINOR_TRIAD.getChordTypeCode()] /
						chord_type_histogram[ChordTypeEnum.MAJOR_TRIAD.getChordTypeCode()];			
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}