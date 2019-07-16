package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import java.util.ArrayList;
import ace.datatypes.FeatureDefinition;
import java.util.List;
import java.util.Map;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the kurtosis of the vertical interval distribution of the piece. The higher 
 * the kurtosis, the more the vertical intervals are clustered near the mean and the fewer outliers there are.
 * Unlike the calculation of Vertical Interval Histogram and its dependent features, each vertical interval is 
 * not weighted by the MIDI velocity at which it is played in the calculation of this feature.
 *
 * @author radamian
 */
public class VerticalIntervalKurtosisFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VerticalIntervalKurtosisFeature()
	{
		String name = "Vertical Interval Kurtosis";
		String code = "C-38";
		String description = "Kurtosis of the vertical interval distribution of the piece. The higher the kurtosis, the more the vertical intervals are clustered near the mean and the fewer outliers there are. Unlike the calculation of Vertical Interval Histogram and its dependent features, each vertical interval is not weighted by the MIDI velocity at which it is played in the calculation of this feature.";
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
			
			// Iterate over each tick for which there is at least one note sounding, creating a list of all 
			// vertical intervals in the piece
			ArrayList<Integer> vertical_intervals_arli = new ArrayList<>();
			for (Integer tick: all_notes_by_tick_map.keySet())
			{
				// Create a list of all pitches sounding on the current tick, including duplicate pitches so 
				// that unisons are counted
				ArrayList<Integer> pitches_on_tick = new ArrayList<>();
				for (NoteInfo note: all_notes_by_tick_map.get(tick))
					if (note.getChannel() != 10 - 1) // Exclude Channel 10 (unpitched percussion)
						pitches_on_tick.add(note.getPitch());
				
				// If there are pitched notes sounding on the current tick, sort the list of pitches sounding
				// and calculate each interval on the current tick
				if (!pitches_on_tick.isEmpty())
				{
					pitches_on_tick.sort((s1, s2) -> s1.compareTo(s2));
					for (int pitch = 0; pitch < pitches_on_tick.size() - 1; pitch++)
						for (int another_pitch = pitch + 1; another_pitch < pitches_on_tick.size(); another_pitch++)
						{
							int interval = pitches_on_tick.get(another_pitch) - pitches_on_tick.get(pitch);
							vertical_intervals_arli.add(interval);
						}
				}
			}
			
			// Create array of vertical intervals for feature calculation
			double[] vertical_intervals = new double[vertical_intervals_arli.size()];
			for (int i = 0; i < vertical_intervals.length; i++)
				vertical_intervals[i] = vertical_intervals_arli.get(i);

			// Calculate the feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getSampleExcessKurtosis(vertical_intervals);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}