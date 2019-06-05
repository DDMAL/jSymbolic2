package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of melodic intervals that are repeated notes, minor thirds,
 * major thirds, perfect fifths, minor sevenths, major sevenths, octaves, minor tenths or major tenths. This
 * is only a very approximate measure of the amount of arpeggiation in the music, of course.
 *
 * @author Cory McKay
 */
public class AmountOfArpeggiationFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AmountOfArpeggiationFeature()
	{
		String name = "Amount of Arpeggiation";
		String code = "M-31";
		String description = "Fraction of melodic intervals that are repeated notes, minor thirds, major thirds, perfect fifths, minor sevenths, major sevenths, octaves, minor tenths or major tenths. This is only a very approximate measure of the amount of arpeggiation in the music, of course.";
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
			value =   sequence_info.melodic_interval_histogram[0]
					+ sequence_info.melodic_interval_histogram[3]
					+ sequence_info.melodic_interval_histogram[4]
					+ sequence_info.melodic_interval_histogram[7]
					+ sequence_info.melodic_interval_histogram[10]
					+ sequence_info.melodic_interval_histogram[11]
					+ sequence_info.melodic_interval_histogram[12]
					+ sequence_info.melodic_interval_histogram[15]
					+ sequence_info.melodic_interval_histogram[16];
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}