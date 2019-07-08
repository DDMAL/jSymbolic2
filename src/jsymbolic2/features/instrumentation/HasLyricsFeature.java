package jsymbolic2.features.instrumentation;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that sets the feature value to 1 if the MIDI file includes encdoded lyrics, and to 0 
 * if it does not.
 *
 * @author radamian
 */
public class HasLyricsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public HasLyricsFeature()
	{
		String name = "Has Lyrics";
		String code = "I-19";
		String description = "Set to 1 if the MIDI file includes encdoded lyrics, and to 0 if it does not.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
		offsets = null;
		is_default = true;
		is_secure = false;
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
		double value = 0.0;
		if (sequence_info != null)
		{
			// Iterate through each message in the MIDI sequence
			for (int n_track = 0; n_track < sequence.getTracks().length; n_track++)
				for (int event = 0; event < sequence.getTracks()[n_track].size(); event++)
				{
					MidiMessage message = sequence.getTracks()[n_track].get(event).getMessage();
					if (message instanceof MetaMessage)
						// If a lyrics meta-message is found, set the feature value to 1
						if (((MetaMessage) message).getType() == 0x05 )
						{
							value = 1.0;
							break;
						}
				}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}