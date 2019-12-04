package jsymbolic2.ngrams;

import java.util.LinkedList;
import jsymbolic2.featureutils.NoteOnsetSliceContainer;

/**
 * Objects of this class generate n-grams for the purpose of analyzing the sequential aspects of the piece of 
 * music. An n-gram contains information extracted from a contiguous sequence of either n or n+1 note onset 
 * slices, such that the n-gram encodes n musical events or transitions in this sequence. An n-gram will 
 * require n slices if it encodes events, and n+1 slices if it encodes transitions. N-grams can encode a 
 * variety of meaningful information. The types of n-grams that jSymbolic currently supports are melodic 
 * interval, vertical interval (complete, lowest and highest lines), and rhythmic value n-grams.
 * 
 * This object is instantiated with data structures for each supported musical moment (vertical intervals, 
 * melodic intervals, rhythmic values) from which it generates n-grams using a sliding window of size n.
 *
 * @author radamian
 */
public class NGramGenerator
{
	/* PRIVATE FIELDS ***************************************************************************************/
	
	/**
	 * The music divided into note onset slices. A note onset slice, in its most basic form, is defined here 
	 * as a set of pitched notes that start simultaneously, or nearly simultaneously. A new onset slice is 
	 * created whenever a new pitched note (in any voice) occurs with sufficient rhythmic separation from 
	 * previous pitched notes. 
	 */
	private final NoteOnsetSliceContainer note_onset_slice_container;	
	
	/**
	 * A list of data structures, where the outer list contains a separate entry for each MIDI track. The
	 * inner structure is an array of lists where the array index corresponds to MIDI channel, and each entry
	 * in the array consists of a list of all melodic intervals that occurred in that channel on that track,
	 * in the order that they occurred. Each entry in these lists of melodic intervals is an Integer that
	 * indicates the number of semitones comprising the melodic interval, with positive values indicating
	 * upwards motion and negative values indicating downwards motion. Notes on MIDI channel 10 (unpitched 
	 * percussion) are excluded from this field.
	 */
	private final LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel;
	
	/**
	 * The rhythmic values of each melodic note in the piece, quantized to the nearest duration in quarter 
	 * notes (e.g. a value of 0.5 corresponds to a duration of an eighth note, possible values being 0.125, 
	 * 0.25, 0.33333333, 0.5, 0.66666667, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, and 12.0) and separated 
	 * out by MIDI track and channel. This follows jSymbolic's convention that the highest note sounding at a 
	 * given time is that belonging to the melody. The row (first array index) corresponds to the MIDI track 
	 * number and the column (second array index) corresponds to the MIDI channel on which notes occur. Each 
	 * entry is a list of rhythmic values occurring on that track and channel, in the order that they occur. 
	 * Notes on MIDI channel 10 (unpitched percussion) are excluded from this field.
	 */
	private final LinkedList<Double>[][] rhythmic_values_by_track_and_channel;
	
	/** 
	 * The complete vertical intervals between all notes in all voices in the piece. There is an inner list 
	 * for every note onset slice with at least one vertical interval, and each inner list contains the unique 
	 * vertical intervals at that moment, in number of semitones. Each inner list is ordered from smallest to 
	 * greatest vertical interval.
	 */
	private final LinkedList<LinkedList<Integer>> complete_vertical_intervals;
	
	/**
	 * A list containing the vertical intervals between the highest and lowest lines in the piece. Each
	 * vertical interval is in number of semitones and appears in the order it occurs. Here, the highest line 
	 * is the list of pitches from the note onset slices that contain only the melodic line (i.e. only the 
	 * highest notes sounding) on the MIDI track and channel with the highest average pitch. The lowest line 
	 * is the list of pitches from the note onset slices that contain only the lowest note sounding on the 
	 * MIDI track and channel with the lowest average pitch. Each interval is calculated by taking the 
	 * difference between the highest line and the lowest line at each note onset slice synchronization. No 
	 * vertical interval is added if either of these slices are empty, so there is no vertical interval when 
	 * one voice has a rest. 
	 */
	private final LinkedList<Integer> lowest_and_highest_lines_vertical_intervals;
	
	/**
	 * The number of channels supported by the MIDI protocol.
	 */
	private final int NUMBER_OF_MIDI_CHANNELS = 16;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * @param note_onset_slice_container							The object containing the note onset 
	 *																slices from which vertical intervals will 
	 *																be calculated.
	 * @param melodic_intervals_by_track_and_channel				The melodic intervals in the piece, 
	 *																separated out by track and channel.
	 * @param rhythmic_values_by_track_and_channel					The rhythmic values of the melodic notes 
	 *																in the piece, separated out by track and 
	 *																channel.
	 * @param complete_vertical_intervals							The complete vertical intervals in the
	 *																piece.
	 * @param lowest_and_highest_lines_vertical_intervals			The vertical intervals between the lowest
	 *																and highest lines in the piece.
	 */
	public NGramGenerator(	NoteOnsetSliceContainer note_onset_slice_container,
							LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel,
							LinkedList[][] rhythmic_values_by_track_and_channel,
							LinkedList<LinkedList<Integer>> complete_vertical_intervals,
							LinkedList<Integer> lowest_and_highest_lines_vertical_intervals)
	{
		this.note_onset_slice_container = note_onset_slice_container;
		this.melodic_intervals_by_track_and_channel = melodic_intervals_by_track_and_channel;
		this.rhythmic_values_by_track_and_channel = rhythmic_values_by_track_and_channel;
		this.complete_vertical_intervals = complete_vertical_intervals;
		this.lowest_and_highest_lines_vertical_intervals = lowest_and_highest_lines_vertical_intervals;
	}

	
	/* PRIVATE METHODS **************************************************************************************/
	
	
	/**
	 * Return a list of melodic interval n-grams for the given MIDI track and channel. A melodic interval 
	 * n-gram consists of a set of n values,  where each value specifies the interval of a melodic transition 
	 * in a musical line, and can be expressed as a count of semitones (with 0 indicating a melodic unison), 
	 * or as a generic interval value. Each melodic interval n-gram corresponds to n+1 sequential note onset 
	 * slices.
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
		LinkedList<NGram> ngrams_ll = new LinkedList<>();
		
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

			// If melodic_intervals_in_window contains n arrays, create a new n-gram and remove the first
			// element to slide the window forward
			if (melodic_intervals_in_window.size() == n_value)
			{
				ngrams_ll.add(new NGram(melodic_intervals_in_window));
				melodic_intervals_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n MELODIC INTERVALS: ");
//		for (int i = 0; i < ngrams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + ngrams_ll.get(i).getStringIdentifier());

		return ngrams_ll;
	}
	
	
	/**
	 * Return a list of complete vertical interval n-grams. A complete vertical interval n-gram consists of a 
	 * set of n lists, with one list for each of n qualifying sequential note onset slices, and where each 
	 * list specifies the vertical intervals between the lowest pitch in the slice and every other unique 
	 * pitch in the slice (a unison is specified if the lowest pitch is doubled). The vertical intervals 
	 * indicated by the entries for each list are expressed in number of semitones, or by the generic interval
	 * value. The size of each of these lists can vary, depending on the number of unique vertical intervals 
	 * found in each list's corresponding note onset slice: there will be at least x-1 list entries for x 
	 * unique pitches in a slice (there will be an additional list entry, a unison, if the lowest note in the 
	 * slice is doubled). The ordering of each list is such that the first number indicates the interval 
	 * between the lowest pitch and the second lowest pitch, the second number indicates the interval between 
	 * the lowest pitch and the third lowest pitch, etc. If the lowest note in the slice is doubled, then the 
	 * first number will indicate a unison, and the list continues as described.
	 * 
	 * @param		n_value						The value of n of the n-grams.
	 * @param		wrapping					Whether intervals are wrapped.
	 * @param		generic_intervals			Whether intervals are represented by generic interval, as 
	 *											opposed to number of semitones.
	 * @return									A list of complete vertical interval n-grams.
	 */
	private LinkedList<NGram> getCompleteVerticalIntervalNGrams(int n_value,
																boolean wrapping, 
																boolean generic_intervals)
	{
		// The list of n-grams to return
		LinkedList<NGram> ngrams_ll = new LinkedList<>();
		
		LinkedList<double[]> slices_in_window = new LinkedList<>();
		
		for (int i = 0; i < complete_vertical_intervals.size(); i++)
		{
			LinkedList<Integer> vertical_intervals_in_slice_ll = complete_vertical_intervals.get(i);
			
			double[] vertical_intervals_in_slice = new double[vertical_intervals_in_slice_ll.size()];
			for (int vi = 0; vi < vertical_intervals_in_slice_ll.size(); vi++)
			{
				int vertical_interval = vertical_intervals_in_slice_ll.get(vi);
				
				if (wrapping) vertical_interval = vertical_interval % 12;
				if (generic_intervals) vertical_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(vertical_interval);
						
				vertical_intervals_in_slice[vi] = vertical_interval;
			}
			
			slices_in_window.add(vertical_intervals_in_slice);

			// If slices_in_window contains n arrays, create a new n-gram and remove the first element to 
			// slide the window forward
			if (slices_in_window.size() == n_value)
			{
				ngrams_ll.add(new NGram(slices_in_window));
				slices_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n VERTICAL INTERVALS: ");
//		for (int i = 0; i < ngrams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + ngrams_ll.get(i).getStringIdentifier());

		return ngrams_ll;
	}
	
	
	/**
	 * Return a list of vertical interval n-grams, where the vertical intervals are between the lowest and
	 * highest lines in the piece. A lowest and highest lines vertical n-gram each consists of a set of n 
	 * numbers, with one number for each of n qualifying sequential note onset slices, and where each number 
	 * specifies the vertical interval between the lowest sounding pitch in the (overall) lowest line and the 
	 * highest sounding pitch in the (overall) highest line. These vertical intervals are expressed in number 
	 * of semitones, with 0 representing a vertical unison, or by a generic interval value, and a negative 
	 * value indicating voice crossing. Note onset slices with no vertical intervals (i.e. where only one note 
	 * is sounding) are ignored during calculation of lowest and highest lines vertical interval n-grams. 
	 * Pitches still held in a given note onset slice from a previous slice are included in the list of 
	 * pitches in the new note onset slice.
	 * 
	 * @param		n_value				The value of n of the n-grams.
	 * @param		direction			Whether the direction of the interval is encoded.
	 * @param		wrapping			Whether intervals are wrapped.
	 * @param		generic_intervals	Whether intervals are represented by generic interval, as opposed to 
	 *									number of semitones.
	 * @return							A list of lowest and highest lines vertical interval n-grams.
	 */
	private LinkedList<NGram> getLowestAndHighestLinesVerticalIntervalNGrams(	int n_value,
																				boolean direction,
																				boolean wrapping, 
																				boolean generic_intervals)
	{
		// The list of n-grams to return
		LinkedList<NGram> ngrams_ll = new LinkedList<>();
		
		// A list of up to n arrays containing vertical intervals within a sliding window
		LinkedList<double[]> vertical_intervals_in_window = new LinkedList<>();

		for (int i = 0; i < lowest_and_highest_lines_vertical_intervals.size(); i++)
		{	
			int copy = lowest_and_highest_lines_vertical_intervals.get(i);

			// If direction, wrapping, or generic intervals has been specified, apply the necessary changes to 
			// the copied vertical interval
			if (!direction) copy = Math.abs(copy);
			if (wrapping) copy = copy % 12;
			if (generic_intervals) copy = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(copy);
				
			vertical_intervals_in_window.add(new double[] { copy });

			// If vertical_intervals_in_window contains n arrays, create a new n-gram and remove the first
			// element to slide the window forward
			if (vertical_intervals_in_window.size() == n_value)
			{
				ngrams_ll.add(new NGram(vertical_intervals_in_window));
				vertical_intervals_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n LOWEST AND HIGHEST LINES VERTICAL INTERVAL N-GRAMS: ");
//		for (int i = 0; i < ngrams_ll.size(); i++)
//			System.out.println("\n" + (i + 1) + ": " + ngrams_ll.get(i).getStringIdentifier());

		return ngrams_ll;
	}
	
	
	/**
	 * Return a list of n-grams containing the sequences of rhythmic values for the given MIDI track and
	 * channel. A rhythmic value n-grams consists of a set of n values, with one value for each of n 
	 * sequential note onset slices for a musical line. Each such value specifies a tempo-independent note 
	 * duration: each (quantized) duration is expressed as a fraction of a quarter note (e.g. a value of 0.5 
	 * corresponds to the duration of an eighth note). The possible (rhythmically quantized) values are 0.125, 
	 * 0.25, 0.33333333, 0.5, 0.66666667, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0 and 12.0.
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
		LinkedList<NGram> ngrams_ll = new LinkedList<>();
		
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
			
			// If rhythmic_values_in_window contains n arrays, create a new n-gram and remove the first
			// element to slide the window forward
			if (rhythmic_values_in_window.size() == n_value)
			{
				ngrams_ll.add(new NGram(rhythmic_values_in_window));
				rhythmic_values_in_window.remove(0);
			}
		}
		
//		System.out.println("\n\n\n RHYTHMIC VALUES: ");
//		for (int i = 0; i < ngrams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + ngrams_ll.get(i).getStringIdentifier());

		return ngrams_ll;
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Return an aggregate of complete vertical interval n-grams according to the specified values.
	 * 
	 * @param	n_value						The value of n of the n-grams.
	 * @param	wrapping					Whether intervals are wrapped.
	 * @param	generic_intervals			Whether intervals are represented by generic interval, as opposed 
	 *										to number of semitones.
	 * 
	 * @return								An aggregate of vertical interval n-grams.
	 */
	public NGramAggregate getCompleteVerticalIntervalNGramAggregate(int n_value,
																	boolean wrapping, 
																	boolean generic_intervals)
	{
		LinkedList<NGram> ngrams = getCompleteVerticalIntervalNGrams(n_value, wrapping, generic_intervals);
		return new NGramAggregate(ngrams);
	}
	
	
	/**
	 * Return an aggregate of lowest and highest lines vertical interval n-grams according to the specified
	 * values.
	 * 
	 * @param		n_value				The value of n of the n-grams.
	 * @param		direction			Whether the direction of the interval is encoded.
	 * @param		wrapping			Whether intervals are wrapped.
	 * @param		generic_intervals	Whether intervals are represented by generic interval, as opposed to 
	 *									number of semitones.
	 * @return							A list of lowest and highest lines vertical interval n-grams.
	 */
	public NGramAggregate getLowestAndHighestLinesVerticalIntervalNGramAggregate(	int n_value,
																					boolean direction,
																					boolean wrapping, 
																					boolean generic_intervals)
	{
		LinkedList<NGram> ngrams = getLowestAndHighestLinesVerticalIntervalNGrams(n_value, direction, wrapping, generic_intervals);
		return new NGramAggregate(ngrams);
	}
	
	
	/**
	 * Return an aggregate of melodic interval n-grams for the provided MIDI track and channel pairs, 
	 * according to the specified values.
	 * 
	 * @param	n_value					The value of n of the n-grams.
	 * @param	track_and_channel_pairs	A list of arrays of integers of length two, each entry representing a 
	 *									voice in the generated n-grams. For each entry of this list, the entry 
	 *									at the first array index is the MIDI track number, and the entry at 
	 *									the second array index is the MIDI channel number. The 
	 *									track_and_channel_pairs field of this object must contain each entry.
	 * @param	direction				Whether the direction of the interval is encoded.
	 * @param	wrapping				Whether the intervals are wrapped.
	 * @param	generic_intervals		Whether intervals are represented by generic interval, as opposed to 
	 *									number of semitones.
	 * @return							An aggregate of melodic interval n-grams.
	 */
	public NGramAggregate getMelodicIntervalNGramAggregate(	int n_value,
															LinkedList<int[]> track_and_channel_pairs, 
															boolean direction,
															boolean wrapping, 
															boolean generic_intervals)
	{
		LinkedList<NGram> ngrams = new LinkedList<>();
		
		for (int pair = 0; pair < track_and_channel_pairs.size(); pair++)
		{
			LinkedList<NGram> ngrams_for_pair = getMelodicIntervalNGrams(n_value, track_and_channel_pairs.get(pair), direction, wrapping, generic_intervals);
			ngrams.addAll(ngrams_for_pair);
		}
		
		return new NGramAggregate(ngrams);
	}
	
	
	/**
	 * Return an aggregate of melodic interval n-grams for a single MIDI track and channel pair, according to 
	 * the specified values.
	 * 
	 * @param	n_value				The value of n of the n-grams.
	 * @param	track_and_channel	An array representing the voice whose melodic intervals are encoded in the 
	 *								n-grams. The entry at the first index is the MIDI track number and the 
	 *								entry at the second index is the MIDI channel number.
	 * @param	direction			Whether the direction of the interval is encoded.
	 * @param	wrapping			Whether intervals are wrapped.
	 * @param	generic_intervals	Whether intervals are represented by generic interval, as opposed to 
	 *								number of semitones.
	 * @return						An aggregate of melodic interval n-grams.
	 */
	public NGramAggregate getMelodicIntervalNGramAggregateForVoice(	int n_value,
																	int[] track_and_channel, 
																	boolean direction,
																	boolean wrapping, 
																	boolean generic_intervals)
	{
		LinkedList<NGram> ngrams = getMelodicIntervalNGrams(n_value, track_and_channel, direction, wrapping, generic_intervals);
		return new NGramAggregate(ngrams);
	}
	
	
	/**
	 * Return an aggregate of melodic interval n-grams for the provided MIDI track and channel pairs, 
	 * according to the specified n-value.
	 * 
	 * @param	n_value						The value of n of the n-grams.
	 * @param	track_and_channel_pairs		A list of arrays of integers of length two, each entry
	 *										representing a voice in the generated n-grams. For each entry of
	 *										this list, the entry at the first array index is the MIDI track 
	 *										number, and the entry at the second array index is the MIDI 
	 *										channel number. The track_and_channel_pairs field of the this 
	 *										object must contain each entry.
	 * @return								An aggregate of rhythmic value n-grams.
	 */
	public NGramAggregate getRhythmicValueNGramAggregate(	int n_value,
															LinkedList<int[]> track_and_channel_pairs)
	{
		LinkedList<NGram> ngrams = new LinkedList<>();
		
		for (int pair = 0; pair < track_and_channel_pairs.size(); pair++)
		{
			LinkedList<NGram> ngrams_for_pair = getRhythmicValueNGrams(n_value, track_and_channel_pairs.get(pair));
			ngrams.addAll(ngrams_for_pair);
		}
		
		return new NGramAggregate(ngrams);
	}
	
	
	/**
	 * Return an aggregate of rhythmic value n-grams according to the specified values.
	 * 
	 * @param	n_value				The value of n of the n-grams.
	 * @param	track_and_channel	An array representing the voice whose rhythmic values are encoded in the 
	 *								n-grams. The entry at the first index is the MIDI track number and the 
	 *								entry at the second index is the MIDI channel number.
	 * @return						An aggregate of rhythmic value n-grams.
	 */
	public NGramAggregate getRhythmicValueNGramAggregateForVoice(	int n_value,
																	int[] track_and_channel)
	{
		LinkedList<NGram> ngrams = getRhythmicValueNGrams(n_value, track_and_channel);
		return new NGramAggregate(ngrams);
	}
	
	
	// The following code generates two-dimensional n-grams and aggregates of those n-grams. These include
	// vertical interval and melodic interval n-grams which can potentially be used for contrapuntal analysis,
	// and melodic interval and rhythmic value n-grams which can potentially be used for isorhythmic analysis.
	// The following code is commented out, as no current jSymbolic feature uses two-dimensional n-grams.
	// The getVerticalAndMelodicIntervalNGrams() and getVerticalAndMelodicIntervalNGramAggregate() methods use 
	// a former implementation of calculates vertical intervals using pitches from a specified voice, and 
	// encodes them in a voice-specific order, rather than in order of least to greatest interval. So, these
	// methods will be adapted should dependent features using two-dimensional n-grams be implemented.
	
	
	/**
	 * Return a list of vertical and melodic interval n-grams, where the encoded vertical intervals are 
	 * between the given base voice and the voices in the given list. This method does not use this object's
	 * melodic_intervals_by_track_and_channel field to encode melodic intervals because the intervals listed
	 * therein are not recorded at the same moment for each track and channel, and so do not coincide with
	 * each other. This method instead uses note onset slices, taking the difference between the highest 
	 * pitches in sequential slices for each voice, so that melodic intervals are encoded in the n-gram's 
	 * secondary identifier between the note onsets for which vertical intervals are encoded in its primary
	 * identifier. This means that melodic intervals are recorded for all voices upon a note onset in any
	 * voice, and trivial unison values are recorded for melodic intervals in voices not having a new onset
	 * at that moment. Intervals can be in number of semitones or their generic interval value. Vertical
	 * intervals are recorded as 128 (an impossible interval in MIDI) when one voice has a rest. Melodic
	 * intervals for a voice are recorded as -128 when that voice has a rest following a note, and as 128 when
	 * that voice has a note on following a rest.
	 * 
	 * @param	n_value						The value of n of the n-grams.
	 * @param	base_voice					An array of integers of length two representing the voice for 
	 *										which the vertical intervals are encoded in the n-grams. The entry 
	 *										at the first index is the MIDI track number and the entry at the 
	 *										second index is the MIDI channel number.
	 * @param	track_and_channel_pairs		A list of arrays of integers of length two, each entry 
	 *										representing a voice in the generated n-grams. For each entry of 
	 *										this list, the entry at the first array index is the MIDI track 
	 *										number, and the entry at the second array index is the MIDI 
	 *										channel number. The track_and_channel_pairs field of the this 
	 *										object must	contain each entry.
	 * @param	direction					Whether the direction of the interval is encoded.
	 * @param	wrapping					Whether intervals are wrapped.
	 * @param	generic_intervals			Whether intervals are represented by generic interval, as opposed 
	 *										to number of semitones.
	 * @param	ignore_rests_in_base_voice	Whether to generate n-grams for intervallic moments when there is 
	 *										a rest in the given base voice. When this parameter is set to 
	 *										false, and the base voice has a rest, then vertical intervals are 
	 *										encoded instead from the first voice encountered in the given list 
	 *										of track and channel pairs that does not have a rest at that 
	 *										moment.
	 * @return								A list of vertical and melodic interval n-grams.
	 */
	/*
	private LinkedList<NGram> getVerticalAndMelodicIntervalNGrams(	int n_value,
																	int[] base_voice,
																	LinkedList<int[]> track_and_channel_pairs, 
																	boolean direction,
																	boolean wrapping, 
																	boolean generic_intervals,
																	boolean ignore_rests_in_base_voice)
	throws Exception
	{
		// The list of n-grams to return
		LinkedList<NGram> n_grams_ll = new LinkedList<>();
		
		// Get the MIDI track and channel corresponding to the entry of the 
		// vertical_intervals_by_track_and_channel field from which vertical intervals will be collected
		int base_track = base_voice[0];
		int base_channel = base_voice[1];
		
		// Get the note onset slices separated out by track and by channel. The outer list index specifies the 
		// slice (the slices are listed in temporal order), and the inner list index specifies the MIDI 
		// pitches in that slice on that track and channel (this list of pitches will always be empty or hold 
		// only one pitch). 
		LinkedList<LinkedList<Integer>>[][] onset_slices_by_track_and_channel = note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannelMelodicLinesOnlyHeldNotesIncluded();
		
		// A list of up to n arrays containing vertical intervals within a sliding window
		LinkedList<double[]> vertical_intervals_in_window = new LinkedList<>();
		// A list of up to n - 1 arrays containing melodic intervals within a sliding window
		LinkedList<double[]> melodic_intervals_in_window = new LinkedList<>();
		
		// Get the list of indices of onset slices at which n-grams should be created
		LinkedList<Integer> n_gram_indices = getVerticalIntervalNGramIndices(track_and_channel_pairs);
		
		for (int i = 0; i < n_gram_indices.size(); i++)
		{
			int n_gram_index = n_gram_indices.get(i);
			
			LinkedList<Integer> vertical_intervals_in_slice_ll = vertical_intervals_by_track_and_channel[base_track][base_channel].get(n_gram_index);

			if (!vertical_intervals_in_slice_ll.isEmpty() || !ignore_rests_in_base_voice)
			{
				// If there are no vertical intervals in the onset slice for the given base voice (i.e. that
				// voice as a rest), then encode the intervals for the first track and channel pair found
				// whose onset slice at the current n-gram index is not empty
				if (vertical_intervals_in_slice_ll.isEmpty())
					for (int pair = 0; pair < track_and_channel_pairs.size(); pair++)
					{
						int track = track_and_channel_pairs.get(pair)[0];
						int channel = track_and_channel_pairs.get(pair)[1];

						vertical_intervals_in_slice_ll = vertical_intervals_by_track_and_channel[track][channel].get(n_gram_index);
						if (!vertical_intervals_in_slice_ll.isEmpty()) break;
					}
				
				// Create an array of vertical intervals in the current onset slice and add it to the list of 
				// those in the current window
				double[] vertical_intervals_in_slice = new double[vertical_intervals_in_slice_ll.size()];
				for (int vertical_interval = 0; vertical_interval < vertical_intervals_in_slice_ll.size(); vertical_interval++)
				{
					int copy = vertical_intervals_in_slice_ll.get(vertical_interval);

					// If the vertical interval is valid (i.e. not encoded as 128 for when a voice has a rest)
					// and direction, wrapping, or generic intervals has been specified, apply the necessary 
					// changes to the copied vertical interval
					if (copy != 128)
					{
						if (!direction) copy = Math.abs(copy);
						if (wrapping) copy = copy % 12;
						if (generic_intervals) copy = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(copy);
					}

					vertical_intervals_in_slice[vertical_interval] = copy;
				}

				vertical_intervals_in_window.add(vertical_intervals_in_slice);

				if (i > 0)
				{
					// Create an array of melodic intervals in the current onset slice and add it to the list of 
					// those in the current window
					double[] melodic_intervals_in_slice = new double[track_and_channel_pairs.size()];
					for (int pair = 0; pair < track_and_channel_pairs.size(); pair++)
					{
						int track = track_and_channel_pairs.get(pair)[0];
						int channel = track_and_channel_pairs.get(pair)[1];

						int melodic_interval;
						int last_n_gram_index = n_gram_indices.get(i - 1);
						
						// If there is a rest at this n-gram index, the melodic interval is encoded as -128
						if (onset_slices_by_track_and_channel[track][channel].get(n_gram_index).isEmpty())
						{
							melodic_interval = -128;
						}
						// If there is a note on following a rest at this n-gram index, the melodic interval 
						// is encoded as 128
						else if (onset_slices_by_track_and_channel[track][channel].get(last_n_gram_index).isEmpty())
						{
							melodic_interval = 128;
						}
						else
						{
							melodic_interval =	onset_slices_by_track_and_channel[track][channel].get(n_gram_index).get(0) -
												onset_slices_by_track_and_channel[track][channel].get(last_n_gram_index).get(0);
							
							// If direction, wrapping, or generic intervals has been specified, apply the 
							// necessary changes to the melodic interval
							if (!direction) melodic_interval = Math.abs(melodic_interval);
							if (wrapping) melodic_interval = melodic_interval % 12;
							if (generic_intervals) melodic_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(melodic_interval);
						}
						
						melodic_intervals_in_slice[pair] = melodic_interval;
					}

					melodic_intervals_in_window.add(melodic_intervals_in_slice);
				}
				
				// If vertical_intervals_in_window contains n lists, create a new n-gram and remove the first
				// element from vertical_intervals_in_window and melodic_intervals_in_window to slide the 
				// window forward
				if (vertical_intervals_in_window.size() == n_value)
				{
					n_grams_ll.add(new TwoDimensionalNGram(vertical_intervals_in_window, melodic_intervals_in_window));
					vertical_intervals_in_window.remove(0);
					melodic_intervals_in_window.remove(0);
				}	
			}
		}
		
//		System.out.println("\n\n\n VERTICAL AND MELODIC INTERVALS NGRAMS: ");
//		for (int i = 0; i < n_grams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + ((TwoDimensionalNGram) n_grams_ll.get(i)).getJointStringIdentifier());
		
		return n_grams_ll;
	}
	*/
	
	
	/**
	 * Return a list of n-grams that encode rhythmic values and the melodic intervals between the notes having
	 * those rhythmic values. Rhythmic values are quantized to the nearest duration in quarter notes (e.g. a 
	 * value of 0.5 corresponds to the duration of an eighth note, possible values being 0.125, 0.25, 0.5, 
	 * 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, 12.0). Only rhythmic values of the melodic line on the given 
	 * MIDI track and channel are encoded in the return n-grams. 
	 *	
	 * @param	n_value					The value of n of the n-grams.
	 * @param	track_and_channel_pair	An array representing the voice whose rhythmic values and melodic 
	 *									intervals are encoded in the n-grams. The entry at the first index is 
	 *									the MIDI track number and the entry at the second index is the MIDI 
	 *									channel number.
	 * @param	direction				Whether the direction of the melodic interval is encoded.
	 * @param	wrapping				Whether intervals are wrapped.
	 * @param	generic_intervals		Whether n-grams are generated when there is a rest in the base voice.
	 * @return							A list of rhythmic value and melodic interval n-grams.
	 * @throws	Exception				Throws an informative exception if n-grams cannot be created for the 
	 *									specified values.
	 */
	/*
	private LinkedList<NGram> getRhythmicValueAndMelodicIntervalNGrams(	int n_value,
																		int[] track_and_channel_pair,
																		boolean direction,
																		boolean wrapping, 
																		boolean generic_intervals)
			throws Exception
	{	
		// The list of n-grams to return
		LinkedList<NGram> n_grams_ll = new LinkedList<>();
		
		// Get the track and channel for which rhythmic values and melodic intervals will be collected
		int track = track_and_channel_pair[0];
		int channel = track_and_channel_pair[1];
		
		// A list of up to n arrays containing rhythmic values within a sliding window
		LinkedList<double[]> rhythmic_values_in_window = new LinkedList<>();
		// A list of up to n - 1 arrays containing melodic intervals within a sliding window
		LinkedList<double[]> melodic_intervals_in_window = new LinkedList<>();
		
		for (int i = 0; i < rhythmic_values_by_track_and_channel[track][channel].size(); i++)
		{
			double copy = rhythmic_values_by_track_and_channel[track][channel].get(i);
			
			// Add an array containing the current rhythmic value to the list of rhythmic values in the
			// current window
			rhythmic_values_in_window.add(new double[] { copy });
			
			if (i > 0)
			{
				int melodic_interval = melodic_intervals_by_track_and_channel.get(track)[channel].get(i - 1);
				
				// If direction, wrapping, or generic intervals has been specified, apply the 
				// necessary changes to the melodic interval
				if (!direction) melodic_interval = Math.abs(melodic_interval);
				if (wrapping) melodic_interval = melodic_interval % 12;
				if (generic_intervals) melodic_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToGenericInterval(melodic_interval);
			
				melodic_intervals_in_window.add(new double[] { melodic_interval });
			}
			
			// If rhythmic_values_in_window contains n lists, create a new n-gram and remove the first element 
			// from rhythmic_values_in_window and melodic_intervals_in_window to slide the window forward
			if (rhythmic_values_in_window.size() == n_value)
			{
				n_grams_ll.add(new TwoDimensionalNGram(rhythmic_values_in_window, melodic_intervals_in_window));
				rhythmic_values_in_window.remove(0);
				melodic_intervals_in_window.remove(0);
			}	
		}
		
//		System.out.println("\n\n\n RHYTHMIC VALUES AND MELODIC INTERVALS: ");
//		for (int i = 0; i < n_grams_ll.size(); i++)
//			System.out.println("\n" + i + ": " + ((TwoDimensionalNGram) n_grams_ll.get(i)).nGramToString());
		
		return n_grams_ll;
	}
	*/
	
	/**
	 * Return an aggregate of vertical and melodic interval n-grams according to the specified values.
	 * 
	 * @param	n_value						The value of n of the n-grams.
	 * @param	base_voice					An array of integers of length two representing the voice for 
	 *										which the vertical intervals are encoded in the n-grams. The entry 
	 *										at the first index is the MIDI track number and the entry at the 
	 *										second index is the MIDI channel number.
	 * @param	track_and_channel_pairs		A list of arrays of integers of length two, each entry 
	 *										representing a voice in the generated n-grams. For each entry of 
	 *										this list, the entry at the first array index is the MIDI track 
	 *										number, and the entry at the second array index is the MIDI 
	 *										channel number. The track_and_channel_pairs field of the this 
	 *										object must contain each entry.
	 * @param	direction					Whether the direction of the interval is encoded.
	 * @param	wrapping					Whether intervals are wrapped.
	 * @param	generic_intervals			Whether intervals are represented by generic interval, as opposed 
	 *										to number of semitones.
	 * @param	ignore_rests_in_base_voice	Whether to generate n-grams for intervallic moments when there is 
	 *										a rest in the given base voice. When this parameter is set to 
	 *										false, and the base voice has a rest, then vertical intervals are 
	 *										encoded instead from the first voice encountered in the given list 
	 *										of track and channel pairs that does not have a rest at that 
	 *										moment.
	 * @return								An aggregate of vertical and melodic interval n-grams.
	 * @throws	Exception					Throws an informative exception if n-grams cannot be created for
	 *										the specified values.
	 */
	/*
	public TwoDimensionalNGramAggregate getVerticalAndMelodicIntervalNGramAggregate(int n_value,
																					int[] base_voice,
																					LinkedList<int[]> track_and_channel_pairs, 
																					boolean direction,
																					boolean wrapping, 
																					boolean generic_intervals,
																					boolean ignore_rests_in_base_voice)
			throws Exception
	{
		LinkedList<NGram> n_grams = getVerticalAndMelodicIntervalNGrams(n_value, base_voice, track_and_channel_pairs, direction, wrapping, generic_intervals, ignore_rests_in_base_voice);
		return new TwoDimensionalNGramAggregate(n_grams);
	}
	*/
	
	
	/**
	 * Return an aggregate of rhythmic value and melodic interval n-grams according to the specified values.
	 * 
	 * @param	n_value						The value of n of the n-grams.
	 * @param	track_and_channel			An array representing the voice whose rhythmic values and melodic
	 *										intervals are encoded in the n-grams. The entry at the first index 
	 *										is the MIDI track number and the entry at the second index is the 
	 *										MIDI channel number.
	 * @param	direction					Whether the direction of the interval is encoded.
	 * @param	wrapping					Whether intervals are wrapped.
	 * @param	generic_intervals			Whether intervals are represented by generic interval, as opposed 
	 *										to number of semitones.
	 * @return								An aggregate of rhythmic value and melodic interval n-grams.
	 * @throws	Exception					Throws an informative exception if n-grams cannot be created for
	 *										the specified values.
	 */
	/*
	public TwoDimensionalNGramAggregate getRhythmicValueAndMelodicIntervalNGramAggregateForVoice(	int n_value,
																									int[] track_and_channel,
																									boolean direction,
																									boolean wrapping, 
																									boolean generic_intervals)
			throws Exception
	{
		LinkedList<NGram> n_grams = getRhythmicValueAndMelodicIntervalNGrams(n_value, track_and_channel, direction, wrapping, generic_intervals);
		return new TwoDimensionalNGramAggregate(n_grams);
	}
	*/
}