package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the median offset in duration of notes from the idealized durations of each
 * of their nearest quantized rhythmic values, expressed as a fraction of the duration of an idealized
 * quantized quarter note. Offsets are treated as absolute values, so offsets that are longer or shorter than
 * each idealized duration are both treated as identical positive numbers in this calculation. This feature
 * provides an indication of how consistently quantized note durations are or, expressed slightly differently,
 * how well they conform to idealized note durations. Higher values indicate greater average deviation from
 * idealized note durations. Both pitched and unpitched notes are included, and this is calculated without
 * regard to the dynamics, voice or instrument of any given note.
 * 
 * @author Cory McKay
 */
public class MedianRhythmicValueOffsetFeature

		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MedianRhythmicValueOffsetFeature()
	{
		code = "R-39";
		String name = "Median Rhythmic Value Offset";
		String description = "Median offset in duration of notes from the idealized durations of each of their nearest quantized rhythmic values, expressed as a fraction of the duration of an idealized quantized quarter note. Offsets are treated as absolute values, so offsets that are longer or shorter than each idealized duration are both treated as identical positive numbers in this calculation. This feature provides an indication of how consistently quantized note durations are or, expressed slightly differently, how well they conform to idealized note durations. Higher values indicate greater average deviation from idealized note durations. Both pitched and unpitched notes are included, and this is calculated without regard to the dynamics, voice or instrument of any given note.";
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
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getMedianValue(sequence_info.rhythmic_value_offsets);
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}