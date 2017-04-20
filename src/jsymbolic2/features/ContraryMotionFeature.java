package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction fraction of movements between voices that consist of contrary
 * motion (the fraction is calculated relative to the total amount of qualifying transitions, including all
 * parallel, similar, contrary and oblique transitions). If more than two voices are involved in a given pitch
 * transition, then each possible pair of voices comprising the transition is included in the calculation.
 * Note that only transitions from one set of pitches to another set of pitches comprising the same number of
 * pitches as the first are included in this calculation, although a brief lookahead is performed in order to
 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
 * performance). Only unique pitches are included in this calculation (unisons are treated as a single pitch).
 * All pitches present are considered, regardless of their MIDI channel or track; this has the advantage of
 * accommodating polyphonic instruments such as piano or guitar, but the consequence is that this feature does
 * not incorporate an awareness of voice crossing.
 * 
 * @author Cory McKay
 */
public class ContraryMotionFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ContraryMotionFeature()
	{
		code = "T-21";
		String name = "Contrary Motion";
		String description = "Fraction of movements between voices that consist of contrary motion (the fraction is calculated relative to the total amount of qualifying transitions, including all parallel, similar, contrary and oblique transitions). If more than two voices are involved in a given pitch transition, then each possible pair of voices comprising the transition is included in the calculation. Note that only transitions from one set of pitches to another set of pitches comprising the same number of pitches as the first are included in this calculation, although a brief lookahead is performed in order to accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human performance). Only unique pitches are included in this calculation (unisons are treated as a single pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that this feature does not incorporate an awareness of voice crossing.";
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
		double[] result = new double[1];
		result[0] = sequence_info.contrary_motion_fraction;
		return result;
	}
}