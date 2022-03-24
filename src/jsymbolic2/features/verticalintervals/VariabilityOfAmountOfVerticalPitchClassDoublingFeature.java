package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the number of notes sounding at a time that match
 * the pitch class of another note sounding at the same time. Rests are excluded from this calculation.
 *
 * @author radamian
 */
public class VariabilityOfAmountOfVerticalPitchClassDoublingFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfAmountOfVerticalPitchClassDoublingFeature()
	{
		String name = "Variability of Amount of Vertical Pitch Class Doubling";
		String code = "C-10";
		String description = "Standard deviation of the number of notes sounding at a time that match the pitch class of another note sounding at the same time. Rests are excluded from this calculation.";
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

			// Iterate over each tick for which there is at least one note sounding, storing the number of 
			// notes on each tick that have the same pitch class of another note sounding at the same time 
			ArrayList<Integer> amount_of_doubling_on_each_tick_with_notes_sounding = new ArrayList<>();
			for (Integer tick: all_notes_by_tick_map.keySet())
			{
				// Get the list of all notes sounding on the current tick
				List<NoteInfo> notes_on_tick = all_notes_by_tick_map.get(tick);

				// A working list of all pitch classes analyzed on the current tick so far
				LinkedList<Integer> pitch_classes_encountered_on_tick = new LinkedList<>();
				
				// Whether there is at least one pitched note on the current tick
				boolean are_pitched_notes_on_tick = false;
				
				// How many doubled pitch classes there are on the current tick
				int doubled_pitch_classes_on_this_tick = 0;
				
				for (int note = 0; note < notes_on_tick.size(); note++)
				{
					if (notes_on_tick.get(note).getChannel() != 10 - 1) // Exclude Channel 10 (Percussion)
					{
						are_pitched_notes_on_tick = true;
						int pitch_class = notes_on_tick.get(note).getPitch() % 12;
						
						if (!pitch_classes_encountered_on_tick.contains(pitch_class))
						{
							// Add the pitch class to the list of those encountered on this tick
							pitch_classes_encountered_on_tick.add(pitch_class);
							
							// A boolean indicating whether there is pitch class doubling for the current 
							// pitch
							boolean doubling_encountered = false;

							// Compare the current pitch to all other pitches sounding on the same tick
							for (int another_note = note + 1; another_note < notes_on_tick.size(); another_note++)
								if (notes_on_tick.get(another_note).getChannel() != 10 - 1) // Exclude Channel 10 (Percussion)
									if (notes_on_tick.get(another_note).getPitch() % 12 == pitch_class)
									{
										// If another_pitch is the first pitch encountered that doubles the 
										// current pitch, the count is incremented by 2 to account for the 
										// current pitch itself
										if (!doubling_encountered)
										{
											doubled_pitch_classes_on_this_tick += 2;
											doubling_encountered = true;
										}
										else doubled_pitch_classes_on_this_tick++;
									}
						}
					}
				}
				
				if (are_pitched_notes_on_tick) 
					amount_of_doubling_on_each_tick_with_notes_sounding.add(doubled_pitch_classes_on_this_tick);
			}
			
			// Create array for feature caculation
			int[] vertical_pitch_class_doubling = new int[amount_of_doubling_on_each_tick_with_notes_sounding.size()];
			for (int i = 0; i < vertical_pitch_class_doubling.length; i++)
				vertical_pitch_class_doubling[i] = amount_of_doubling_on_each_tick_with_notes_sounding.get(i);
			
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(vertical_pitch_class_doubling);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}