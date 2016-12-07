package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MEIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;

/**
 * A feature calculator that The total number of notes marked with slurs in the piece (i.e. the number of
 * notes indicated as notes with slurs in the MEI encoding) divided by the total number of pitched notes in 
 * the music.
 *
 * <p>Since this is an MEI-specific feature, the feature information is obtained from the
 * {@link MeiSpecificStorage} class. The associated code can be found
 * <a href="https://github.com/DDMAL/jMei2Midi">jMei2Midi</a>, which is included as a dependency of
 * jSymbolic.</p>
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class NumberOfSlursMeiFeature extends MEIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NumberOfSlursMeiFeature()
	{
		code = "S-2";
		String name = "Number of Slurs";
		String description = "The total number of notes marked with slurs in the piece (i.e. the number of notes indicated as notes with slurs in the MEI encoding) divided by the total number of pitched notes in the music.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
		offsets = null;
	}
	

	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * The prototype method that classes extending this class will override in order to extract their
	 * feature from a section of MEI data (and the associated MIDI data generated from it).
	 *
	 * @param meiSpecificStorage	Holds all accessible MEI-specific data from which to extract this feature.
	 * @param sequence				The MIDI data generated from the MEI data, and which can abe used to help
	 *								extract this feature. Notice that this can be taken directly from the 
	 *								{@link org.ddmal.jmei2midi.MeiSequence}.
	 * @param sequence_info			Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that are needed to calculate this value. The
	 *								order and offsets of these features must be the same as those returned by 
	 *								this class's getDependencies and getDependencyOffsets methods
	 *								respectively.The first indice indicates the feature/window and the second
	 *								indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if the feature cannot be calculated.
	 */
	@Override
	public double[] extractMEIFeature( MeiSpecificStorage meiSpecificStorage,
									   Sequence sequence,
									   MIDIIntermediateRepresentations sequence_info,
									   double[][] other_feature_values )
	throws Exception
	{
		double value;
		if (sequence_info != null)
		{
			double total_number_pitched_note_ons = (double) sequence_info.total_number_pitched_note_ons;
			double number_of_slur_notes = (double) meiSpecificStorage.getSlurNoteList().size();

			if (total_number_pitched_note_ons == 0.0 || number_of_slur_notes == 0.0)
				value = 0.0;
			else
				value = number_of_slur_notes / total_number_pitched_note_ons;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}