package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the number of notes sounding simultaneously. This 
 * includes doubled pitches. Rests are excluded from this calculation, as are unpitched notes.
 *
 * @author radamian
 */
public class VariabilityOfVerticalNoteDensityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfVerticalNoteDensityFeature()
	{
		String name = "Variability of Vertical Note Density";
		String code = "C-7";
		String description = "Standard deviation of the number of notes sounding simultaneously. This includes doubled pitches. Rests are excluded from this calculation, as are unpitched notes.";
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
			Map<Integer, List<NoteInfo>> all_notes_by_tick_map = sequence_info.all_notes_by_tick_map;
			
			ArrayList<Integer> number_of_notes_on_each_tick = new ArrayList<>();
			
			// Iterate over each tick for which there is at least one note sounding, noting the number of 
			// notes sounding simultaneously on each tick
			for (Integer tick: all_notes_by_tick_map.keySet())
			{
				int number_of_notes_on_tick = 0;
				
				// Whether there is at least one pitched note sounding on the current tick
				boolean are_pitched_notes_on_tick = false;
				
				// Count the number of pitched notes on the current tick
				for (NoteInfo note: all_notes_by_tick_map.get(tick))
					if (note.getChannel() != 10 - 1) // Exclude Channel 10 (Percussion)
					{
						are_pitched_notes_on_tick = true;
						number_of_notes_on_tick++;
					}
				
				if (are_pitched_notes_on_tick)
					number_of_notes_on_each_tick.add(number_of_notes_on_tick);
			} 
			
			// Create array for feature calculation
			int[] vertical_note_densities = new int[number_of_notes_on_each_tick.size()];
			for (int i = 0; i < vertical_note_densities.length; i++)
				vertical_note_densities[i] = number_of_notes_on_each_tick.get(i);
			
			// Calculate the feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(vertical_note_densities);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}