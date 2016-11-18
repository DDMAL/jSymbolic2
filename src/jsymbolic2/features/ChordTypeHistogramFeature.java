package jsymbolic2.features;

import jsymbolic2.featureutils.ChordTypeEnum;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import ace.datatypes.FeatureDefinition;
import mckay.utilities.staticlibraries.MathAndStatsMethods;
import javax.sound.midi.Sequence;

/**
 * A feature vector consisting of bin magnitudes of the chord type histogram described in the jSymbolic
 * manual. This is a normalized histogram that has bins labeled with types of chords (in the following order
 * and with the indicated identifying codes): partial chords consisting of just two pitch classes [0], minor
 * triads [1], major triads [2], diminished triads [3], augmented triads [4], other triads [5], minor seventh
 * chords [6], dominant seventh chords [7], major seventh chords [8], other chords consisting of four pitch
 * classes [9], and complex chords with more than four pitch classes [10]. The bin magnitudes are calculated
 * by going through MIDI ticks one by one and incrementing the counter for the bin that corresponds to the
 * chord, if any, that is present during each given tick; the result is that the chords in this histogram are
 * weighted by the duration with which each chord is played. All inversions are treated as equivalent and
 * octave doubling is ignored in the calculation of this histogram. Melodic behaviour is not considered, so
 * arpeggios are not counted in this histogram.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ChordTypeHistogramFeature
		extends MIDIFeatureExtractor
{
	/* FIELDS ***********************************************************************************************/
	
	
	/**
	 * The number of different types of chords that are included in this histogram. In other words, the 
	 * histogram's dimensions.
	 */
	private final int number_of_chord_types = ChordTypeEnum.values().length;


	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ChordTypeHistogramFeature()
	{
		code = "C-3";
		String name = "Chord Type Histogram";
		String description = "A feature vector consisting of bin magnitudes of the chord type histogram described in the jSymbolic manual. This is a normalized histogram that has bins labeled with types of chords (in the following order and with the indicated identifying codes): partial chords consisting of just two pitch classes [0], minor triads [1], major triads [2], diminished triads [3], augmented triads [4], other triads [5], minor seventh chords [6], dominant seventh chords [7], major seventh chords [8], other chords consisting of four pitch classes [9], and complex chords with more than four pitch classes [10]. The bin magnitudes are calculated by going through MIDI ticks one by one and incrementing the counter for the bin that corresponds to the chord, if any, that is present during eah given tick; the result is that the chords in this histogram are weighted by the duration with which each chord is played. All inversions are treated as equivalent and octave doubling is ignored in the calculation of this histogram. Melodic behaviour is not considered, so arpeggios are not counted in this histogram.";
		boolean is_sequential = true;
		int dimensions = number_of_chord_types; //for each possible MIDI pitch
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
		double[] result = null;

		if (sequence_info != null)
		{
			// The histogram to return
			result = new double[number_of_chord_types];

			// The total number of pitch classes
			int number_pitch_classes = 12;
			
			// The combined MIDI velocity of all (non-Channel 10) notes sounding on a MIDI tick by tick basis
			short[][] pitch_strength_by_tick_chart = sequence_info.pitch_strength_by_tick_chart;

			// Count the chord types tick by tick
			for (int tick = 0; tick < pitch_strength_by_tick_chart.length; tick++)
			{
				// Find the combined MIDI velocity of all (non-Channel 10) pitch classes sounding at this tick
				int[] pitch_class_strengths_this_tick = new int[number_pitch_classes];
				for (int pitch = 0; pitch < pitch_strength_by_tick_chart[tick].length - 1; pitch++)
				{
					int pitch_class = pitch % number_pitch_classes;
					pitch_class_strengths_this_tick[pitch_class] += pitch_strength_by_tick_chart[tick][pitch];
				}

				// Find the type of chord
				ChordTypeEnum chord_type = ChordTypeEnum.getChordType(pitch_class_strengths_this_tick);
				
				// Update the histogram to reflect the chord on this tick
				if (chord_type != null)
					result[chord_type.getChordTypeCode()] += 1;
			}

			// Normalize the histogram
			result = MathAndStatsMethods.normalize(result);
		}
		
		return result;
	}

	
	/* PRIVATE METHODS ***************************************************************************************/
	
	
	private static double standardizeMidiVelocities(int[] tick_chord)
	{
		int note_count = 0;
		double total_velocity = 0;
		for (int note = 0; note < tick_chord.length; note++)
		{
			int current_velocity = tick_chord[note];
			if (current_velocity > 0)
			{
				note_count++;
				total_velocity += current_velocity;
			}
		}
		if (note_count == 0)
			return 0;
		else return total_velocity / note_count;
	}
}