package jsymbolic2.ngrams;

import java.util.LinkedList;
import javax.sound.midi.Track;
import jsymbolic2.featureutils.NoteOnsetSliceContainer;

/**
 * Objects of this class generate vertical interval, melodic interval, and rhythmic value n-grams. Here, an 
 * n-gram is a sequence of n values representing these moments. Each note onset in a voice initiates a new 
 * n-gram.
 * 
 * At instantiation, this object uses note onset slices from the given NoteOnsetSliceContainer object to 
 * calculate the vertical intervals between each voice in the piece. The note onset slices used are separated 
 * out by track and channel, and containing up to one note at a given time, so that there is a constant number 
 * of voices to analyze (No stream segregation beyond this is performed). Here, a voice is represented by an 
 * array of integers of length two, the entry at the first index being the MIDI track number, and the entry at 
 * the second index being the MIDI channel number (only pairs provided in the list passed to this object at 
 * instantiation are analyzed). This object is also passed all the melodic intervals and the rhythmic values 
 * in the piece separated out by MIDI track and channel, at instantiation. 
 * 
 * This object returns aggregates of the n-grams that it creates to facilitate easy usage and analysis by
 * feature calculators.
 *
 * @author radamian
 */
public class NGramGenerator
{
	/* PRIVATE FIELDS ***************************************************************************************/
	
	/**
	 * A list of arrays of integers, where each entry represents a different voice in the piece. Each array is
	 * of length two, with the value at the first index being the MIDI track number, and the value at the 
	 * second index being the MIDI channel number. Vertical intervals are calculated between all voices listed
	 * in this field.
	 */
	private final LinkedList<int[]> track_and_channel_pairs;
	
	/** 
	 * A 2-D array containing the vertical intervals between the notes of the melodic lines in the piece, 
	 * separated out by MIDI track (first array index) and channel (second array index). Each entry in this 
	 * array is a list of list of vertical intervals, measured in number of semitones. The outer list 
	 * contains a list of vertical intervals at each onset slice , and the inner list contains the vertical 
	 * intervals separating the voice corresponding to the entry of the 2-D array and the rest of the voices, 
	 * in the same order of the list of voices given at instantiation. If a voice has a rest, then the inner 
	 * list will be empty. If another voice has a rest, then the vertical interval at the entry corresponding
	 * to that voice in the inner list will be 128. 
	 */
	private final LinkedList<LinkedList<Integer>>[][] vertical_intervals_by_track_and_channel;	
	
	/**
	 * A list of data structures, where the outer list contains a separate entry for each MIDI track. The
	 * inner structure is an array of lists where the array index corresponds to MIDI channel, and each entry
	 * in the array consists of a list of all melodic intervals that occurred in that channel on that track,
	 * in the order that they occurred. Each entry in these lists of melodic intervals is an Integer that
	 * indicates the number of semitones comprising the melodic interval, with positive values indicating
	 * upwards motion and negative values indicating downwards motion. Any notes on Channel 10 (non-pitched
	 * percussion) are ignored (i.e. the Channel 10 entry on the array is left empty).
	 */
	private final LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel;
	
	/**
	 * The rhythmic values of each note in the piece, quantized to the nearest duration in quarter notes (e.g. 
	 * a value of 0.5 corresponds to a duration of an eighth note) and separated out by MIDI track and 
	 * channel. The row (first array index) corresponds to the MIDI track number and the column (second array 
	 * index) corresponds to the MIDI channel on which notes occur. Each entry is a list of rhythmic values
	 * occurring on that track and channel, in the order that they occur.
	 */
	private final LinkedList<Double>[][] rhythmic_values_by_track_and_channel;
	
	/**
	 * The number of channels supported by the MIDI protocol.
	 */
	private final int NUMBER_OF_MIDI_CHANNELS = 16;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Parse the note onset slices within the given NoteOnsetSliceContainer object to create structures
	 * containing vertical and melodic intervals from which n-grams can be created.
	 * 
	 * @param tracks_from_sequence						An array of tracks contained in the MIDI sequence of 
	 *													the piece.
	 * @param note_onset_slice_container				The object containing the note onset slices from which 
	 *													vertical and melodic intervals will be calculated.
	 * @param melodic_intervals_by_track_and_channel	The melodic intervals in the piece, separated out by
	 *													MIDI track and channel.
	 * @param rhythmic_values_by_track_and_channel		The rhythmic values in the piece, separated out by
	 *													track and channel
	 * @param track_and_channel_pairs					A list of arrays of integers, each representing a 
	 *													combination of track and MIDI channel, a different 
	 *													voice in the piece. The entry at index 0 is the number 
	 *													of the track and the entry at index 1 is the number of 
	 *													the MIDI channel.
	 */
	public NGramGenerator(	Track[] tracks_from_sequence,
							NoteOnsetSliceContainer note_onset_slice_container,
							LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel,
							LinkedList[][] rhythmic_values_by_track_and_channel,
							LinkedList<int[]> track_and_channel_pairs)
	{
		this.track_and_channel_pairs = track_and_channel_pairs;
		this.melodic_intervals_by_track_and_channel = melodic_intervals_by_track_and_channel;
		this.rhythmic_values_by_track_and_channel = rhythmic_values_by_track_and_channel;
		
		// Get the note onset slices separated out by track and by channel. The outer list index specifies the 
		// slice (the slices are listed in temporal order), and the inner list index specifies the MIDI 
		// pitches in that slice on that track and channel (this list of pitches will always be empty or hold 
		// only one pitch). 
		LinkedList<LinkedList<Integer>>[][] note_onset_slices_only_melodic_lines = note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannelMelodicLinesOnlyHeldNotesIncluded();
		
		// Initialize the vertical_intervals_by_track_and_channel field
		vertical_intervals_by_track_and_channel = new LinkedList[note_onset_slices_only_melodic_lines.length][NUMBER_OF_MIDI_CHANNELS];
		for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
			for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
				vertical_intervals_by_track_and_channel[n_track][chan] = new LinkedList<>();
		
		// Iterate by note onset slice, calculating the vertical intervals between each voice
		for (int slice = 0; slice < note_onset_slice_container.NUMBER_OF_ONSET_SLICES; slice++)
		{
			for (int pair = 0; pair < track_and_channel_pairs.size(); pair++)
			{
				int track = track_and_channel_pairs.get(pair)[0];
				int channel = track_and_channel_pairs.get(pair)[1];
				
				// If the onset slice for the current track and channel pair is not empty (i.e. that voice 
				// does not have a rest), get the vertical intervals between the current voice and all other
				// voices
				if (!note_onset_slices_only_melodic_lines[track][channel].get(slice).isEmpty())
				{
					LinkedList<Integer> vertical_intervals = new LinkedList<>();
					
					for (int other_pair = 0; other_pair < track_and_channel_pairs.size(); other_pair++)
					{	
						if (pair != other_pair)
						{
							int other_track = track_and_channel_pairs.get(other_pair)[0];
							int other_channel = track_and_channel_pairs.get(other_pair)[1];

							int vertical_interval;
							// The vertical interval is recorded as 128 if the current onset slice for a 
							// track and channel is empty (i.e. that voice has a rest)
							if (note_onset_slices_only_melodic_lines[other_track][other_channel].get(slice).isEmpty())
								vertical_interval = 128;
							else
								vertical_interval = note_onset_slices_only_melodic_lines[other_track][other_channel].get(slice).get(0) - 
													note_onset_slices_only_melodic_lines[track][channel].get(slice).get(0);

							vertical_intervals.add(vertical_interval);
						}
					}
					
					vertical_intervals_by_track_and_channel[track][channel].add(vertical_intervals);
				}
			}
		}
	}

	
	/* PRIVATE METHODS **************************************************************************************/
	
	
	/**
	 * Return a list of vertical interval n-grams, where the encoded intervals are between the given base 
	 * voice and the voices in the given list.
	 * 
	 * @param		n_value					The value of n of the n-grams.
	 * @param		base_voice				An array of integers of length two representing the voice for 
	 *										which the vertical intervals are encoded in the n-grams. The entry 
	 *										at the first index is the MIDI track number and the entry at the 
	 *										second index is the MIDI channel number.
	 * @param		track_and_channel_pairs	A list of arrays of integers of length two, each entry
	 *										representing a voice in the generated n-grams. For each entry of
	 *										this list, the entry at the first array index is the MIDI track 
	 *										number, and the entry at the second array index is the MIDI 
	 *										channel number. The track_and_channel_pairs field of the this 
	 *										object must contain each entry.
	 * @param		direction				Whether the direction of the interval is encoded.
	 * @param		wrapping				Whether intervals are wrapped.
	 * @param		generic_intervals		Whether intervals are represented by generic interval, as opposed 
	 *										to number of semitones.
	 * @return								A list of vertical interval n-grams.
	 */
	private LinkedList<NGram> getVerticalIntervalNGrams(int n_value,
														int[] base_voice,
														LinkedList<int[]> track_and_channel_pairs, 
														boolean direction,
														boolean wrapping, 
														boolean generic_intervals)
	{
		// The list of n-grams to return
		LinkedList<NGram> n_grams_ll = new LinkedList<>();
		
		// Get the MIDI track and channel corresponding to the entry of the 
		// vertical_intervals_by_track_and_channel field from which vertical intervals will be collected
		int base_track = base_voice[0];
		int base_channel = base_voice[1];
		
		// A list of up to n arrays containing vertical intervals within a sliding window
		LinkedList<double[]> vertical_intervals_in_window = new LinkedList<>();

		for (int slice = 0; slice < vertical_intervals_by_track_and_channel[base_track][base_channel].size(); slice++)
		{
			LinkedList<Integer> vertical_intervals_in_slice_ll = vertical_intervals_by_track_and_channel[base_track][base_channel].get(slice);

			// Create an array of vertical intervals in the current onset slice and add it to the list of 
			// those in the current window
			double[] vertical_intervals_in_slice = new double[vertical_intervals_in_slice_ll.size()];
			for (int i = 0; i < vertical_intervals_in_slice_ll.size(); i++)
			{
				int copy = vertical_intervals_in_slice_ll.get(i);

				// If direction, wrapping, or generic intervals has been specified, apply the necessary 
				// changes to the copied vertical interval
				if (!direction) copy = Math.abs(copy);
				if (wrapping) copy = copy % 12;
				if (generic_intervals) copy = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(copy);

				vertical_intervals_in_slice[i] = copy;
			}

			vertical_intervals_in_window.add(vertical_intervals_in_slice);

			// If vertical_intervals_in_window contains n lists, create a new n-gram and remove the first
			// element to slide the window forward
			if (vertical_intervals_in_window.size() == n_value)
			{
				n_grams_ll.add(new NGram(vertical_intervals_in_window, track_and_channel_pairs.size() - 1));
				vertical_intervals_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n VERTICAL INTERVALS: ");
//		for (int i = 0; i < n_grams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + n_grams_ll.get(i).nGramToString());

		return n_grams_ll;
	}
	
	
	/**
	 * Return a list of melodic interval n-grams for the given MIDI track and channel.
	 * 
	 * @param		n_value				The value of n of the n-grams.
	 * @param		track_and_channel	An array representing the voice whose melodic intervals are encoded in 
	 *									the n-grams. The entry at the first index is the MIDI track number and
	 *									the entry at the second index is the MIDI channel number.
	 * @param		direction			Whether the direction of the interval is encoded.
	 * @param		wrapping			Whether intervals are wrapped.
	 * @param		generic_intervals	Whether intervals are represented by generic interval, as opposed to 
	 *									number of semitones.
	 * @return							A list of melodic interval n-grams.
	 */
	private LinkedList<NGram> getMelodicIntervalNGrams(	int n_value,
														int[] track_and_channel, 
														boolean direction,
														boolean wrapping, 
														boolean generic_intervals)
	{
		// The list of n-grams to return
		LinkedList<NGram> n_grams_ll = new LinkedList<>();
		
		// Get the MIDI track and channel for which melodic intervals will be collected
		int track = track_and_channel[0];
		int channel = track_and_channel[1];
		
		// A list of up to n arrays containing melodic intervals within a sliding window
		LinkedList<double[]> melodic_intervals_in_window = new LinkedList<>();
		
		// Iterate by melodic interval
		for (int melodic_interval = 0; melodic_interval < melodic_intervals_by_track_and_channel.get(track)[channel].size(); melodic_interval++)
		{
			int copy = melodic_intervals_by_track_and_channel.get(track)[channel].get(melodic_interval);

			// If direction, wrapping, or generic intervals has been specified, apply the necessary changes to 
			// the copied melodic interval
			if (!direction) copy = Math.abs(copy);
			if (wrapping) copy = copy % 12;
			if (generic_intervals) copy = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(copy);
			
			// Add an array containing the copied melodic interval to the list of melodic intervals in the
			// current window
			melodic_intervals_in_window.add(new double[] { copy });
			
			// If melodic_intervals_in_window contains n lists, create a new n-gram and remove the first
			// element to slide the window forward
			if (melodic_intervals_in_window.size() == n_value)
			{
				n_grams_ll.add(new NGram(melodic_intervals_in_window, 1));
				melodic_intervals_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n MELODIC INTERVALS: ");
//		for (int i = 0; i < n_grams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + n_grams_ll.get(i).nGramToString());

		return n_grams_ll;
	}
	
	
	/**
	 * Return a list of n-grams containing the sequences of rhythmic values for the given MIDI track and
	 * channel.
	 * 
	 * @param		n_value				The value of n of the n-grams.
	 * @param		track_and_channel	An array representing the voice whose rhythmic values are encoded in 
	 *									the n-grams. The entry at the first index is the MIDI track number and
	 *									the entry at the second index is the MIDI channel number.
	 * @return							A list of rhythmic value n-grams.
	 */
	private LinkedList<NGram> getRhythmicValueNGrams(	int n_value,
														int[] track_and_channel)
	{
		// The list of n-grams to return
		LinkedList<NGram> n_grams_ll = new LinkedList<>();
		
		// Get the MIDI track and channel for which rhythmic values are collected
		int track = track_and_channel[0];
		int channel = track_and_channel[1];
		
		// A list of up to n arrays containing rhythmic values within a sliding window
		LinkedList<double[]> rhythmic_values_in_window = new LinkedList<>();
		
		// Iterate by rhythmic value
		for (int rhythmic_value = 0; rhythmic_value < rhythmic_values_by_track_and_channel[track][channel].size(); rhythmic_value++)
		{
			double copy = rhythmic_values_by_track_and_channel[track][channel].get(rhythmic_value);
			
			// Add an array containing the current rhythmic value to the list of rhythmic values in the
			// current window
			rhythmic_values_in_window.add(new double[] { copy });
			
			// If rhythmic_values_in_window contains n lists, create a new n-gram and remove the first
			// element to slide the window forward
			if (rhythmic_values_in_window.size() == n_value)
			{
				n_grams_ll.add(new NGram(rhythmic_values_in_window, 1));
				rhythmic_values_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n RHYTHMIC VALUES: ");
//		for (int i = 0; i < n_grams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + n_grams_ll.get(i).nGramToString());

		return n_grams_ll;
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Return an aggregate of vertical interval n-grams according to the specified values.
	 * 
	 * @param		n_value					The value of n of the n-grams.
	 * @param		base_voice				An array of integers of length two representing the voice for 
	 *										which the vertical intervals are encoded in the n-grams. The entry 
	 *										at the first index is the MIDI track number and the entry at the 
	 *										second index is the MIDI channel number.
	 * @param		track_and_channel_pairs	A list of arrays of integers of length two, each entry
	 *										representing a voice in the generated n-grams. For each entry of
	 *										this list, the entry at the first array index is the MIDI track 
	 *										number, and the entry at the second array index is the MIDI 
	 *										channel number. The track_and_channel_pairs field of the this 
	 *										object must contain each entry.
	 * @param		direction				Whether the direction of the interval is encoded.
	 * @param		wrapping				Whether intervals are wrapped.
	 * @param		generic_intervals		Whether intervals are represented by generic interval, as opposed 
	 *										to number of semitones.
	 * @return								A list of vertical interval n-grams.
	 */
	public NGramAggregate getVerticalIntervalNGramAggregate(int n_value,
															int[] base_voice,
															LinkedList<int[]> track_and_channel_pairs, 
															boolean direction,
															boolean wrapping, 
															boolean generic_intervals)
	{
		LinkedList<NGram> n_grams = getVerticalIntervalNGrams(n_value, base_voice, track_and_channel_pairs, direction, wrapping, generic_intervals);
		return new NGramAggregate(n_grams);
	}
	
	
	/**
	 * Return an aggregate of melodic interval n-grams according to the specified values.
	 * 
	 * @param		n_value				The value of n of the n-grams.
	 * @param		track_and_channel	An array representing the voice whose melodic intervals are encoded in 
	 *									the n-grams. The entry at the first index is the MIDI track number and
	 *									the entry at the second index is the MIDI channel number.
	 * @param		direction			Whether the direction of the interval is encoded.
	 * @param		wrapping			Whether intervals are wrapped.
	 * @param		generic_intervals	Whether intervals are represented by generic interval, as opposed to 
	 *									number of semitones.
	 * @return							A list of melodic interval n-grams.
	 */
	public NGramAggregate getMelodicIntervalNGramAggregate(	int n_value,
															int[] track_and_channel, 
															boolean direction,
															boolean wrapping, 
															boolean generic_intervals)
	{
		LinkedList<NGram> n_grams = getMelodicIntervalNGrams(n_value, track_and_channel, direction, wrapping, generic_intervals);
		return new NGramAggregate(n_grams);
	}
	
	
	/**
	 * Return an aggregate of rhythmic value n-grams according to the specified values.
	 * 
	 * @param		n_value				The value of n of the n-grams.
	 * @param		track_and_channel	An array representing the voice whose rhythmic values are encoded in 
	 *									the n-grams. The entry at the first index is the MIDI track number and
	 *									the entry at the second index is the MIDI channel number.
	 * @return							A list of rhythmic value n-grams.
	 */
	public NGramAggregate getRhythmicValueNGramAggregate(	int n_value,
															int[] track_and_channel)
	{
		LinkedList<NGram> n_grams = getRhythmicValueNGrams(n_value, track_and_channel);
		return new NGramAggregate(n_grams);
	}
}