package jsymbolic2.ngrams;

import java.util.LinkedList;
import jsymbolic2.featureutils.NoteOnsetSliceContainer;

/**
 * Objects of this class calculate n-grams from contiguous sequences of either n or n+1 note onset slices,
 * such that an n-gram encodes n musical events or transitions in such a sequence. An n-gram will require n
 * slices if it encodes events, and n+1 slices if it encodes transitions. N-grams can encode a variety of
 * meaningful information. The types of n-grams that jSymbolic currently supports are melodic interval
 * n-grams, vertical interval (either complete or lowest and highest lines) n-grams, and rhythmic value 
 * n-grams.
 *
 * Objects of this class should be given data structures appropriate to all supported type of n-gram (melodic
 * intervals, rhythmic values and vertical intervals) when they are instantiated. Method calls to objects of
 * this class can then be used to generate NGramAggregates based on the information provided upon
 * instantiation, with details as to types of n-grams to be generated provided to the method. The n-grams
 * aggregates by objects of this class will then be generated using overlapping sliding window of size n
 * (where the value of n is specified at the method call).
 *
 * @author radamian and Cory McKay
 */
public class NGramGenerator
{
	/* PRIVATE FIELDS ***************************************************************************************/
	
	
	/**
	 * The music divided into note onset slices. NOTE: This field is currently unused, but will be needed
	 * if the currently commented out getVerticalAndMelodicIntervalNGrams method is reactivated (to calculate
	 * contrapuntal note onset slices).
	 */
	private final NoteOnsetSliceContainer note_onset_slice_container;	
	
	/**
	 * The sequence of melodic intervals in a piece of music, separated by voice. Each entry of the outer list
	 * corresponds to a MIDI track, and each entry of the array it contains corresponds to a MIDI channel.
	 * Each entry in this array corresponds to a list of the melodic intervals that occurred on that channel
	 * on that track, in the order that they occurred. Each such interval is represented by a number
	 * indicating the associated number of semitones, with positive values indicating upwards motion and
	 * negative values indicating downwards motion. In cases where multiple notes were sounding simultaneously
	 * on a given track and channel, then only the highest of these was used in finding these melodic
	 * intervals. There is always an entry for every channel on every track present, but the entry for a given
	 * channel will be an empty list if there are no melodic intervals on that track and channel. Notes on
	 * MIDI channel 10 (unpitched percussion) are excluded. See the documentation on note onset windows for
	 * more implementation details (e.g. rhythmic quantization).
	 */
	private final LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel;
	
	/**
	 * The sequence of rhythmic values in a piece of music, separated by voice. The first array index
	 * indicates MIDI track and the second indicates MIDI channel. Each entry in the array consists of a list
	 * of the rhythmic values that occurred on that track and channel, in the order that they occurred. Each
	 * such rhythmic value is specified in terms of number of quarter notes, quantized to the nearest duration
	 * value (e.g. a value of 0.5 corresponds to a duration of an eighth note). Supported values are 0.125,
	 * 0.25, 0.33333333, 0.5, 0.66666667, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, and 12.0. In cases where
	 * multiple notes were sounding simultaneously on a given track and channel’s note onset window, then only
	 * the rhythmic value of the highest-pitched of these was stored. Notes on MIDI channel 10 (unpitched
	 * percussion) are excluded. Rests are ignored. See the documentation on note onset windows for more
	 * implementation details (e.g. rhythmic quantization).
	 */
	private final LinkedList<Double>[][] rhythmic_values_by_track_and_channel;
	
	/**
	 * The sequence of sets of vertical intervals measured above the lowest note in each note onset slice. The
	 * outer list contains one entry for each note onset slice that contains at least one vertical interval
	 * (slices without vertical intervals are omitted). Each inner list contains a list of all unique vertical
	 * intervals (measured in number of semitones) above the lowest note in the given slice. Intervals
	 * relative to pitches other than the lowest are not included. The list for each slice only counts each
	 * vertical interval once (e.g. a doubled major third above the lowest note will be stored as a single
	 * major third), but one (or more) notes doubling the lowest note will be counted as a (single) unison (0
	 * semitones). The list of vertical intervals for each slice is ordered from smallest to largest. Notes in
	 * all voices (MIDI tracks and channels) are combined together, and treated identically to multiple notes
	 * in a single voice (e.g. a guitar chord), so voice separation is effectively ignored. Channel 10
	 * unpitched instrument notes are excluded. See the documentation on note onset windows for more
	 * implementation details (e.g. rhythmic quantization).
	 */
	private final LinkedList<LinkedList<Integer>> complete_vertical_intervals;
	
	/**
	 * The sequence of vertical intervals measured between the lowest and highest overall voices in a piece,
	 * measured in number of semitones. The list contains one entry for each note onset slice that contains a
	 * vertical interval between these two voices (slices where neither or only one of these voices has a note
	 * sounding are omitted). If a given slice contains multiple sounding notes for the upper voice, then only
	 * the highest of these is considered. If a given slice contains multiple sounding notes for the lower
	 * voice, then only the lowest of these is considered. The determination of lowest and highest voices
	 * overall is based on the mean pitch of all notes in every MIDI track and channel pairing over the music
	 * as a whole (MIDI Channel 10 unpitched instrument notes are excluded). See the documentation on note
	 * onset windows for more implementation details (e.g. rhythmic quantization).
	 */
	private final LinkedList<Integer> lowest_and_highest_lines_vertical_intervals;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Store the provided data in this object.
	 * 
	 * @param note_onset_slice_container					Note onset slices from which n-grams can be 
	 *														calculated. Note that these are not currently
	 *														used by objects of this class, but this 
	 *														information is taken in and stored by this 
	 *														constructor so that they can be used in future 
	 *														additions to this class.
	 * @param melodic_intervals_by_track_and_channel		The melodic intervals in a piece of music, 
	 *														separated by voice. Each entry of the outer list
	 *														corresponds to a MIDI track, and each entry of the
	 *														array it contains corresponds to a MIDI channel.
	 *														Each entry in this array corresponds to a list of 
	 *														the melodic intervals that occurred on that 
	 *														channel on that track, in the order that they 
	 *														occurred. Each such interval is represented by a
	 *														number indicating the associated number of 
	 *														semitones, with positive values indicating upwards
	 *														motion and negative values indicating downwards 
	 *														motion. In cases where multiple notes were 
	 *														sounding simultaneously on a given track and
	 *														channel, then only the highest of these was used 
	 *														in finding these melodic intervals. Notes on MIDI 
	 *														channel 10 (unpitched percussion) are excluded.
	 *														See the documentation on note onset windows for
	 *														more implementation details (e.g. rhythmic 
	 *														quantization).
	 * @param rhythmic_values_by_track_and_channel			The sequence of rhythmic values in a piece of 
	 *														music, separated by voice. The first array index 
	 *														indicates MIDI track and the second indicates MIDI
	 *														channel. Each entry in the array consists of a
	 *														list of the rhythmic values that occurred on that
	 *														track and channel, in the order that they
	 *														occurred. Each such rhythmic value is specified in
	 *														terms of number of quarter notes, quantized to the
	 *														nearest duration value (e.g. a value of 0.5
	 *														corresponds to a duration of an eighth note).
	 *														Supported values are 0.125, 0.25, 0.33333333, 0.5,
	 *														0.66666667, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0,
	 *														10.0, and 12.0. In cases where multiple notes were
	 *														sounding simultaneously on a given track and 
	 *														channel’s note onset window, then only the 
	 *														rhythmic value of the highest-pitched of these was
	 *														stored. Notes on MIDI channel 10 (unpitched
	 *														percussion) are excluded. See the documentation on
	 *														note onset windows for more implementation details
	 *														(e.g. rhythmic quantization).
	 * @param complete_vertical_intervals					The sequence of sets of vertical intervals 
	 *														measured above the lowest note in each note onset
	 *														slice. The outer list contains one entry for each
	 *														note onset slice that contains at least one
	 *														vertical interval (slices without vertical
	 *														intervals are omitted). Each inner list contains a
	 *														list of all unique vertical intervals (measured in
	 *														number of semitones) above the lowest note in the
	 *														given slice. Notes relative to pitches other than
	 *														the lowest are not included. The list for each 
	 *														slice only counts each vertical interval once 
	 *														(e.g. a doubled major third above the lowest note
	 *														will be stored as a single major third), but one
	 *														(or more) notes doubling the lowest note will be
	 *														counted as a (single) unison (0 semitones). The
	 *														list of vertical intervals for each slice is
	 *														ordered from smallest to largest. Notes in all
	 *														voices (MIDI tracks and channels) are combined
	 *														together, and treated identically to multiple 
	 *														notes in a single voice (e.g. a guitar chord), so
	 *														voice separation is effectively ignored. Channel
	 *														10 unpitched instrument notes are excluded. See 
	 *														the documentation on note onset windows for more 
	 *														implementation details (e.g. rhythmic 
	 *														quantization).
	 * @param lowest_and_highest_lines_vertical_intervals	The sequence of vertical intervals measured 
	 *														between the lowest and highest overall voices in a
	 *														piece, measured in number of semitones. The list
	 *														contains one entry for each note onset slice that
	 *														contains a vertical interval between these two
	 *														voices (slices where neither or only one of these
	 *														voices has a note sounding are omitted). If a
	 *														given slice contains multiple sounding notes for
	 *														the upper voice, then only the highest of these is
	 *														considered. If a given slice contains multiple
	 *														sounding notes for the lower voice, then only the
	 *														lowest of these is considered. The determination
	 *														of lowest and highest voices overall is based on 
	 *														the mean pitch of all notes in every MIDI track
	 *														and channel pairing over the music as a whole
	 *														(MIDI Channel 10 unpitched instrument notes are
	 *														excluded). See the documentation on note onset
	 *														windows for more implementation details (e.g. 
	 *														rhythmic quantization).
	 */
	public NGramGenerator(	NoteOnsetSliceContainer note_onset_slice_container,
							LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel,
							LinkedList[][] rhythmic_values_by_track_and_channel,
							LinkedList<LinkedList<Integer>> complete_vertical_intervals,
							LinkedList<Integer> lowest_and_highest_lines_vertical_intervals )
	{
		this.note_onset_slice_container = note_onset_slice_container;
		this.melodic_intervals_by_track_and_channel = melodic_intervals_by_track_and_channel;
		this.rhythmic_values_by_track_and_channel = rhythmic_values_by_track_and_channel;
		this.complete_vertical_intervals = complete_vertical_intervals;
		this.lowest_and_highest_lines_vertical_intervals = lowest_and_highest_lines_vertical_intervals;
	}


	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Construct and access the aggregated set of melodic interval n-grams present in the music provided to
	 * this object at its instantiation, based on the settings passed to this method's parameters. These
	 * melodic n-grams are calculated using sequential sliding windows, with one n-gram for each note onset
	 * slice that has n note onset slices following it.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 melodic intervals.
	 * @param	track_and_channel_pairs	A list of integer arrays, where each array has a size of two, and each
	 *									list entry represents a different voice. Melodic n-grams will be 
	 *									calculated for each voice that has an array present. The entry at the 
	 *									first index of each array is the voice's MIDI track number and the  
	 *									entry at the second index is the same voice's MIDI channel number.
	 * @param	direction				Whether the direction of intervals is encoded. If this is set to 
	 *									true, then positive values indicate upwards movement and negative 
	 *									values indicate downwards movement. If this parameter is set to false, 
	 *									then all n-gram values will be positive, regardless of direction.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The aggregated list of windowed melodic interval n-grams across the
	 *									specified voices.
	 */
	public NGramAggregate getMelodicIntervalNGramAggregate(	int n_value,
															LinkedList<int[]> track_and_channel_pairs, 
															boolean direction,
															boolean wrapping, 
															boolean diatonic_intervals )
	{
		// All melodic n-grams found accross all specified voices
		LinkedList<NGram> ngrams = new LinkedList<>();
		for (int voice = 0; voice < track_and_channel_pairs.size(); voice++)
		{
			LinkedList<NGram> ngrams_for_voice = getMelodicIntervalNGrams( n_value, 
																		   track_and_channel_pairs.get(voice), 
																		   direction, 
																		   wrapping, 
																		   diatonic_intervals );
			ngrams.addAll(ngrams_for_voice);
		}

		// Return all melodic interval n-grams aggregated accross all specified voices
		return new NGramAggregate(ngrams, 0.0);
	}


	/**
	 * Construct and access the aggregated melodic interval n-grams in the music given to this object at
	 * instantiation (in the specific MIDI track and channel indicated in the track_and_channel parameter of
	 * this method). These melodic n-grams are calculated using sequential sliding windows, with one n-gram
	 * for each note onset slice that has n note onset slices following it.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 melodic intervals.
	 * @param	track_and_channel		An array representing the voice whose melodic intervals are to be 
	 *									used in encoding the n-grams. The entry at the first index is the MIDI
	 *									track number and the entry at the second index is the MIDI channel
	 *									number.
	 * @param	direction				Whether the direction of intervals is encoded. If this is set to 
	 *									true, then positive values indicate upwards movement and negative 
	 *									values indicate downwards movement. If this parameter is set to false, 
	 *									then all n-gram values will be positive, regardless of direction.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The aggregated list of windowed melodic interval n-grams in the
	 *									specified voice.
	 */
	public NGramAggregate getMelodicIntervalNGramAggregateForVoice(	int n_value,
																	int[] track_and_channel, 
																	boolean direction,
																	boolean wrapping, 
																	boolean diatonic_intervals )
	{
		LinkedList<NGram> ngrams = getMelodicIntervalNGrams( n_value,
															 track_and_channel,
															 direction, wrapping,
															 diatonic_intervals );
		return new NGramAggregate(ngrams, 0.0);
	}
	

	/**
	 * Return a list of melodic interval n-grams in the music given to this object at instantiation (in the
	 * specific MIDI track and channel indicated in the track_and_channel parameter of this method). These
	 * melodic n-grams are calculated using sequential sliding windows, with one n-gram for each note onset
	 * slice that has n note onset slices following it.
	 * 
	 * A melodic interval n-gram consists of a set of n values, where each value specifies the interval of a
	 * melodic transition in a musical line, and can be expressed as a count of semitones (with 0 indicating a
	 * melodic unison) or as a diatonic interval. Each melodic interval n-gram corresponds to n+1 sequential
	 * note onset slices. If there are multiple notes in a given note onset slice for the given MIDI track and
	 * channel, then only the highest pitch in the line's slice is included in the calculation of the melodic
	 * interval n-gram. Pitches still held in a given note onset slice from a previous slice are included in
	 * the set of candidate pitches in the new note onset slice, and new note onset slices are only considered
	 * to occur in the calculation of this particular type of n-gram if new notes in a line's slice are higher
	 * in pitch than any still-sounding notes from one of the line's previous slices.
	 *
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 melodic intervals.
	 * @param	track_and_channel		An array representing the voice whose melodic intervals are to be 
	 *									used in encoding the n-grams. The entry at the first index is the MIDI
	 *									track number and the entry at the second index is the MIDI channel
	 *									number.
	 * @param	direction				Whether the direction of intervals is encoded. If this is set to 
	 *									true, then positive values indicate upwards movement and negative 
	 *									values indicate downwards movement. If this parameter is set to false, 
	 *									then all n-gram values will be positive, regardless of direction.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The sequentially ordered list of windowed melodic interval n-grams
	 *									found in the specified voice.
	 */
	public LinkedList<NGram> getMelodicIntervalNGrams(	int n_value,
														int[] track_and_channel, 
														boolean direction,
														boolean wrapping, 
														boolean diatonic_intervals )
	{
		// The complete list of n-grams found
		LinkedList<NGram> ngrams_found = new LinkedList<>();
		
		// The MIDI track and channel for which melodic intervals will be collected
		int track = track_and_channel[0];
		int channel = track_and_channel[1];
		
		// A list of up to n arrays containing melodic intervals within a sliding window
		LinkedList<double[]> melodic_intervals_in_window = new LinkedList<>();
		
		// Iterate by melodic interval
		for (int interval_count = 0; interval_count < melodic_intervals_by_track_and_channel.get(track)[channel].size(); interval_count++)
		{
			// Copy the current melodic interval
			int interval = melodic_intervals_by_track_and_channel.get(track)[channel].get(interval_count);

			// Apply direction, wrapping or diatonic interval formatting, if requested
			if (!direction) interval = Math.abs(interval);
			if (wrapping) interval = interval % 12;
			if (diatonic_intervals) interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToDiatonicInterval(interval);

			// Add an array containing the melodic interval to the list of melodic intervals in the current 
			// window
			melodic_intervals_in_window.add(new double[] { interval });

			// If melodic_intervals_in_window contains n arrays, create a new n-gram and remove the first
			// element to slide the window forward
			if (melodic_intervals_in_window.size() == n_value)
			{
				ngrams_found.add(new NGram(melodic_intervals_in_window));
				melodic_intervals_in_window.remove(0);
			}
		}
		
		/* TESTING CODE: Output all n-grams found for this voice (i.e. what will be returned by this method)
		System.out.println("\n\n\nMELODIC INTERVAL N-GRAMS TRACK " + track + " CHANNEL " + channel + ": ");
		for (int i = 0; i < ngrams_found.size(); i++)
			System.out.println((i+1) + ": " + ngrams_found.get(i).getStringIdentifier());
		*/

		// Return the n-grams found
		return ngrams_found;
	}
	
	
	/**
	 * Construct and access the aggregated set of rhythmic value n-grams present in the music provided to
	 * this object at its instantiation, based on the settings passed to this method's parameters. These
	 * rhythmic n-grams are calculated using sequential sliding windows, with one n-gram for each note onset
	 * slice that has n-1 note onset slices following it.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 rhythmic values.
	 * @param	track_and_channel_pairs	A list of integer arrays, where each array has a size of two, and each
	 *									list entry represents a different voice. Rhythmic n-grams will be 
	 *									calculated for each voice that has an array present. The entry at the 
	 *									first index of each array is the voice's MIDI track number and the  
	 *									entry at the second index is the same voice's MIDI channel number.
	 * @return							The aggregated list of windowed rhythmic value n-grams across the
	 *									specified voices.
	 */
	public NGramAggregate getRhythmicValueNGramAggregate( int n_value,
														  LinkedList<int[]> track_and_channel_pairs )
	{
		// All rhythmic value n-grams found accross all specified voices
		LinkedList<NGram> ngrams = new LinkedList<>();
		for (int voice = 0; voice < track_and_channel_pairs.size(); voice++)
		{
			LinkedList<NGram> ngrams_for_voice = getRhythmicValueNGrams( n_value,
																		 track_and_channel_pairs.get(voice) );
			ngrams.addAll(ngrams_for_voice);
		}
		
		// Return all rhythmic value n-grams aggregated accross all specified voices
		return new NGramAggregate(ngrams, 0.0);
	}

	
	/**
	 * Construct and access the aggregated rhythmic values n-grams in the music given to this object at
	 * instantiation (in the specific MIDI track and channel indicated in the track_and_channel parameter of
	 * this method). These rhythmic value n-grams are calculated using sequential sliding windows, with one 
	 * n-gram for each note onset slice that has n-1 note onset slices following it.
	 * 
	 * @param	n_value				The n value of the n-grams to be generated. A value of 3, for example,
	 *								will indicate a 3-gram consisting of 3 rhythmic values.
	 * @param	track_and_channel	An array representing the voice whose rhythmic values are to be used in
	 *								encoding the n-grams. The entry at the first index is the MIDI track
	 *								number and the entry at the second index is the MIDI channel number.
	 * @return						The aggregated list of windowed rhythmic value n-grams in the specified
	 *								voice.
	 */
	public NGramAggregate getRhythmicValueNGramAggregateForVoice( int n_value,
																  int[] track_and_channel )
	{
		LinkedList<NGram> ngrams = getRhythmicValueNGrams(n_value, track_and_channel);
		return new NGramAggregate(ngrams, 0.0);
	}
	

	/**
	 * Return a list of rhythmic value n-grams in the music given to this object at instantiation (in the
	 * specific MIDI track and channel indicated in the track_and_channel parameter of this method). These
	 * melodic n-grams are calculated using sequential sliding windows, with one n-gram for each note onset
	 * slice that has n-1 note onset slices following it.
	 *
	 * A rhythmic value n-gram consists of a set of n values, with one value for each of n sequential note
	 * onset slices for a musical line. Each such value specifies a tempo-independent note duration: each
	 * (quantized) duration is expressed as a fraction of a quarter note (e.g. a value of 0.5 corresponds to
	 * the duration of an eighth note). The possible (rhythmically quantized) values are 0.125, 0.25,
	 * 0.33333333, 0.5, 0.66666667, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0 and 12.0. If there are multiple
	 * notes in a given note onset slice for the given voice, then only the highest pitched note in the slice
	 * is included in the calculation of the rhythmic value n-gram. A new note onset slice is only considered
	 * to occur in this particular type of n-gram if at least one new note in a line is higher in pitch than
	 * any still-sounding notes from a previous slice in that line. Rests are ignored. MIDI Channel 10
	 * unpitched notes are not included here, as they often represent instruments that cannot hold notes.
	 * 
	 * @param	n_value				The n value of the n-grams to be generated. A value of 3, for example,
	 *								will indicate a 3-gram consisting of 3 rhythmic values.
	 * @param	track_and_channel	An array representing the voice whose rhythmic values are to be used in
	 *								encoding the n-grams. The entry at the first index is the MIDI track
	 *								number and the entry at the second index is the MIDI channel number.
	 * @return						The sequentially ordered list of windowed rhythmic value n-grams found in 
	 *								the specified voice.
	 */
	public LinkedList<NGram> getRhythmicValueNGrams( int n_value,
													 int[] track_and_channel )
	{
		// The complete list of n-grams found
		LinkedList<NGram> ngrams_found = new LinkedList<>();
		
		// The MIDI track and channel for which rhythmic values will be collected
		int track = track_and_channel[0];
		int channel = track_and_channel[1];
		
		// A list of up to n arrays containing rhythmic values within a sliding window
		LinkedList<double[]> rhythmic_values_in_window = new LinkedList<>();
		
		// Iterate by rhythmic value
		for (int rhythmic_value_count = 0; rhythmic_value_count < rhythmic_values_by_track_and_channel[track][channel].size(); rhythmic_value_count++)
		{
			// Copy the current rhythmic value
			double rhythmic_value = rhythmic_values_by_track_and_channel[track][channel].get(rhythmic_value_count);
			
			// Add an array containing the rhythmic value to the list of rhythmic values in the current window
			rhythmic_values_in_window.add(new double[] { rhythmic_value });
			
			// If rhythmic_values_in_window contains n arrays, create a new n-gram and remove the first
			// element to slide the window forward
			if (rhythmic_values_in_window.size() == n_value)
			{
				ngrams_found.add(new NGram(rhythmic_values_in_window));
				rhythmic_values_in_window.remove(0);
			}
		}
		
		/* TESTING CODE: Output all n-grams found for this voice (i.e. what will be returned by this method)
		System.out.println("\n\n\nRHYTHMIC VALUE N-GRAMS TRACK " + track + " CHANNEL " + channel + ": ");
		for (int i = 0; i < ngrams_found.size(); i++)
			System.out.println((i+1) + ": " + ngrams_found.get(i).getStringIdentifier());
		*/
		
		// Return the n-grams found
		return ngrams_found;
	}
	
	
	/**
	 * Construct and access the aggregated set of complete vertical interval n-grams present in the music
	 * provided to this object at its instantiation, based on the settings passed to this method's parameters.
	 * These n-grams are calculated using sequential sliding windows, with one n-gram for each note onset
	 * slice that has n-1 note onset slices following it.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 sets of vertical intervals.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The aggregated list of windowed complete vertical interval n-grams 
	 *									across all voices.
	 */
	public NGramAggregate getCompleteVerticalIntervalNGramAggregate( int n_value,
																	 boolean wrapping, 
																	 boolean diatonic_intervals )
	{
		LinkedList<NGram> ngrams = getCompleteVerticalIntervalNGrams(n_value, wrapping, diatonic_intervals);
		return new NGramAggregate(ngrams, 0.0);
	}

	
	/**
	 * Return a list of complete vertical interval n-grams in the music given to this object at instantiation.
	 * These n-grams are calculated using sequential sliding windows, with one n-gram for each note onset
	 * slice that has n-1 note onset slices following it.
	 *
	 * A complete vertical interval n-gram consists of a set of n arrays, with one array for each of n
	 * qualifying sequential note onset slices, and where each array specifies the vertical intervals between
	 * the lowest pitch in the slice and every other unique pitch in the slice. The vertical intervals
	 * indicated by the entries for each array can be expressed in number of semitones, or by diatonic
	 * interval value, as preferred. The size of each of these arrays can vary, depending on the number of
	 * unique vertical intervals found in each array's corresponding note onset slice. The ordering of each
	 * array is such that the first number indicates the interval between the lowest pitch and the second
	 * lowest pitch, the second number indicates the interval between the lowest pitch and the third lowest
	 * pitch, etc. If the lowest note in the slice is doubled, then the first number will indicate a unison,
	 * and the array continues as described. Note onset slices with no vertical intervals (i.e. where only one
	 * note is sounding in the slice) are omitted from the collection of vertical interval n-grams generated.
	 * So, for example, the four consecutive note onset slices [C4,E4,G4,C5], [C4], [C4,C4,E4,G4,G4] and
	 * [C4,C4] would result in the following complete vertical interval 3-gram: [(4,7,12),(0,4,7),(0)]. Note
	 * how the second note onset slice was ignored because it did not contain any vertical intervals. In the
	 * third note onset slice, the lowest note C4 is doubled, so the second array begins with the unison
	 * vertical interval (0). The note G4 is also doubled in this slice, but the corresponding second array
	 * has only one entry (7) for the vertical interval between the lowest note and the note G4, because each
	 * value can only appear at most once in a given array. Vertical intervals are calculated for all pitched
	 * (non-MIDI Channel 10) notes in all MIDI track/channel pairings, and neither inclusion nor ordering is
	 * influenced by the MIDI track/channel a given note is in. Pitches still held in a given note onset slice
	 * from a previous slice are included in the array of pitches in the new note onset slice.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 sets of vertical intervals.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The sequentially ordered list of windowed complete vertical interval
	 *									n-grams	found across all voices.
	 */
	public LinkedList<NGram> getCompleteVerticalIntervalNGrams( int n_value,
																boolean wrapping, 
																boolean diatonic_intervals)
	{
		// The complete list of n-grams found
		LinkedList<NGram> ngrams_found = new LinkedList<>();
		
		// A list of up to n arrays containing vertical intervals within a sliding window
		LinkedList<double[]> vertical_interval_in_window_by_slice = new LinkedList<>();
		
		// Go through the slices one by one
		for (int slice = 0; slice < complete_vertical_intervals.size(); slice++)
		{
			// The vertical intervals in the current slice being processed
			LinkedList<Integer> vertical_intervals_raw_in_slice = complete_vertical_intervals.get(slice);
			
			// Format and add each vertical interval in the slice, making sure no vertical is counted twice
			LinkedList<Integer> vertical_intervals_formatted_in_slice = new LinkedList<>();
			for (int vi = 0; vi < vertical_intervals_raw_in_slice.size(); vi++)
			{
				// Apply wrapping or diatonic formatting, if requested
				int vertical_interval = vertical_intervals_raw_in_slice.get(vi);
				if (wrapping) vertical_interval = vertical_interval % 12;
				if (diatonic_intervals) vertical_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToDiatonicInterval(vertical_interval);
				
				// Make sure each vertical interval only appears once
				if (!vertical_intervals_formatted_in_slice.contains(vertical_interval))
					vertical_intervals_formatted_in_slice.add(vertical_interval);
			}
		
			// Sort the list of vertical intervals in this slice
			vertical_intervals_formatted_in_slice.sort((s1, s2) -> s1.compareTo(s2));
		
			// Copy the vertical intervals to an array
			double[] vertical_intervals_in_slice = new double[vertical_intervals_formatted_in_slice.size()];
			for (int vi = 0; vi < vertical_intervals_in_slice.length; vi++)
				vertical_intervals_in_slice[vi] = vertical_intervals_formatted_in_slice.get(vi);
			
			// Store the vertical intervals for this slice
			vertical_interval_in_window_by_slice.add(vertical_intervals_in_slice);

			// If vertical_interval_in_window_by_slice contains n arrays, create a new n-gram and remove the
			// first element to slide the window forward
			if (vertical_interval_in_window_by_slice.size() == n_value)
			{
				ngrams_found.add(new NGram(vertical_interval_in_window_by_slice));
				vertical_interval_in_window_by_slice.remove(0);
			}
		}
		
		/* TESTING CODE: Output all n-grams found
		System.out.println("\n\n\nCOMPLETE VERTICAL INTERVAL N-GRAMS: ");
		for (int i = 0; i < ngrams_found.size(); i++)
			System.out.println((i+1) + ": " + ngrams_found.get(i).getStringIdentifier());
		*/

		// Return the n-grams found
		return ngrams_found;
	}

	
	/**
	 * Construct and access the aggregated set of lowest and highest line vertical interval n-grams present in
	 * the music provided to this object at its instantiation, based on the settings passed to this method's
	 * parameters. These n-grams are calculated using sequential sliding windows, with one n-gram for each
	 * note onset slice that has n-1 note onset slices following it.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 vertical intervals.
	 * @param	direction				Whether the direction of intervals is encoded. If this is set to 
	 *									true, then positive values indicate the note in the higher line is
	 *									above the note in the lowest line in a the given interval, and
	 *									negative values indicate the opposite (i.e. voice crossing is 
	 *									occurring). If this parameter is set to false, then all n-gram values
	 *									will be positive, regardless of direction.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The aggregated list of windowed lowest and highest line vertical 
	 *									interval n-grams.
	 */
	public NGramAggregate getLowestAndHighestLinesVerticalIntervalNGramAggregate( int n_value,
																				  boolean direction,
																				  boolean wrapping, 
																				  boolean diatonic_intervals )
	{
		LinkedList<NGram> ngrams = getLowestAndHighestLinesVerticalIntervalNGrams( n_value,
																				   direction,
																				   wrapping,
																				   diatonic_intervals );
		return new NGramAggregate(ngrams, 0.0);
	}
	
	
	/**
	 * Return a list of lowest and highest line vertical interval n-grams in the music given to this object at
	 * instantiation. These n-grams are calculated using sequential sliding windows, with one n-gram for each
	 * note onset slice that has n-1 note onset slices following it.
	 * 
	 * These n-grams are calculated so as to only consider notes in the lowest and highest lines in the piece,
	 * which is to say the two MIDI track/channel pairings with the lowest and highest average pitches,
	 * respectively, as calculated across the entire piece of music. Notes in all other MIDI track/channel
	 * pairings are ignored in lowest and highest lines vertical interval n-grams and the note onset slices
	 * they are derived from. This means that only note onset slices that involve a new note in one of these
	 * two outer parts are considered; note onset slices corresponding to only new notes in inner lines are
	 * ignored. If there are multiple notes sounding in a given note onset slice for either of the lowest or
	 * highest lines, then only the lowest or highest note is considered, respectively, in the calculation of
	 * lowest and highest lines vertical interval n-grams.
	 *
	 * So, lowest and highest lines vertical n-grams each consists of a set of n numbers, with one number for
	 * each of n qualifying sequential note onset slices, and where each number specifies the vertical
	 * interval between the lowest sounding pitch in the (overall) lowest line and the highest sounding pitch
	 * in the (overall) highest line. These vertical intervals can be expressed in number of semitones, with 0
	 * representing a vertical unison, or as diatonic interval values. A negative value indicates voice
	 * crossing. Wrapping and/or direction formatting can also be applied, depending on parameter settings.
	 * Note onset slices with no vertical intervals (i.e. where zero notes or only one note are sounding in
	 * the two relevant voices) are ignored during calculation of lowest and highest lines vertical interval
	 * n-grams. Pitches still held in a given note onset slice from a previous slice are included in the list
	 * of pitches in the new note onset slice. Only pitched (non-MIDI Channel 10) notes are considered.
	 * 
	 * @param	n_value					The n value of the n-grams to be generated. A value of 3, for example,
	 *									will indicate a 3-gram consisting of 3 vertical intervals.
	 * @param	direction				Whether the direction of intervals is encoded. If this is set to 
	 *									true, then positive values indicate the note in the higher line is
	 *									above the note in the lowest line in a the given interval, and
	 *									negative values indicate the opposite (i.e. voice crossing is 
	 *									occurring). If this parameter is set to false, then all n-gram values
	 *									will be positive, regardless of direction.
	 * @param	wrapping				Whether intervals in the n-grams are wrapped by octave. If this is set 
	 *									to true, then intervals will be wrapped by octave before being stored
	 *									(e.g. a P12 will be encoded as a P5) in the n-grams. If this is set to
	 *									false, then they are left as is.
	 * @param	diatonic_intervals		Whether intervals are represented in n-grams by diatonic intervals or
	 *									by number of semitones. If this is set to true, then they will be
	 *									stored as diatonic intervals (e.g. m3 and M3 will both be represented 
	 *									by 3). If this is set to false, then they will be stored as semitone
	 *									counts (e.g. m3 and M3 will be represented by 3 and 4, respectively). 
	 * @return							The sequentially ordered list of windowed lowest and highest line
	 *									vertical interval n-grams found.
	 */
	public LinkedList<NGram> getLowestAndHighestLinesVerticalIntervalNGrams( int n_value,
																			 boolean direction,
																			 boolean wrapping, 
																			 boolean diatonic_intervals )
	{
		// The complete list of n-grams found
		LinkedList<NGram> ngrams_found = new LinkedList<>();
		
		// A list of up to n arrays containing vertical intervals within a sliding window
		LinkedList<double[]> vertical_intervals_in_window = new LinkedList<>();

		// Go through the slices one by one
		for (int slice = 0; slice < lowest_and_highest_lines_vertical_intervals.size(); slice++)
		{	
			// Copy the value of the vertical interval for this slice
			int vertical_interval = lowest_and_highest_lines_vertical_intervals.get(slice);

			// If direction, wrapping, or diatonic intervals has been specified, apply the necessary changes to 
			// the copied vertical interval
			if (!direction) vertical_interval = Math.abs(vertical_interval);
			if (wrapping) vertical_interval = vertical_interval % 12;
			if (diatonic_intervals) vertical_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToDiatonicInterval(vertical_interval);
			
			// Store the vertical interval for this slice
			vertical_intervals_in_window.add(new double[] { vertical_interval });

			// If vertical_intervals_in_window contains n arrays, create a new n-gram and remove the first
			// element to slide the window forward
			if (vertical_intervals_in_window.size() == n_value)
			{
				ngrams_found.add(new NGram(vertical_intervals_in_window));
				vertical_intervals_in_window.remove(0);
			}
		}
		
		/* TESTING CODE: Output all n-grams found
		System.out.println("\n\n\nLOWEST AND HIGHEST LINES VERTICAL INTERVAL N-GRAMS: ");
		for (int i = 0; i < ngrams_found.size(); i++)
			System.out.println((i+1) + ": " + ngrams_found.get(i).getStringIdentifier());
		*/

		// Return the n-grams found
		return ngrams_found;
	}

	
	/* TEMPORARILY DISABLED METHODS *************************************************************************/


	/*
	 * The methods below implement the ability to construct and aggregate two-dimensional n-grams by combining
	 * any two of melodic, rhythmic and vertical n-grams. For example, vertical interval and melodic interval
	 * n-grams can be combined to permit contrapuntal n-grams, similar to those proposed in the jSymbolic
	 * manual. Similarly, melodic interval and rhythmic value n-grams could be combined to produce n-grams
	 * facilitating isorhythmic analysis.
	 *
	 * These methods are currently commented out, as no current jSymbolic feature uses two-dimensional
	 * n-grams. These commented out methods have not undergone code or documentation review yet, nor have they
	 * been rigorously tested, so care should be taken when using them in the future. Of particular note, the
	 * getVerticalAndMelodicIntervalNGrams() and getVerticalAndMelodicIntervalNGramAggregate() methods use a
	 * former implementation of vertical intervals calculated using pitches from a specified voice, and
	 * encodes them in a voice-specific order, rather than in order of least to greatest interval.
	 */
	
	
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
	 * at that moment. Intervals can be in number of semitones or their diatonic interval value. Vertical
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
	 * @param	diatonic_intervals			Whether intervals are represented by diatonic interval, as opposed 
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
	public LinkedList<NGram> getVerticalAndMelodicIntervalNGrams(	int n_value,
																	int[] base_voice,
																	LinkedList<int[]> track_and_channel_pairs, 
																	boolean direction,
																	boolean wrapping, 
																	boolean diatonic_intervals,
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
					// and direction, wrapping, or diatonic intervals has been specified, apply the necessary 
					// changes to the copied vertical interval
					if (copy != 128)
					{
						if (!direction) copy = Math.abs(copy);
						if (wrapping) copy = copy % 12;
						if (diatonic_intervals) copy = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToDiatonicInterval(copy);
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
							
							// If direction, wrapping, or diatonic intervals has been specified, apply the 
							// necessary changes to the melodic interval
							if (!direction) melodic_interval = Math.abs(melodic_interval);
							if (wrapping) melodic_interval = melodic_interval % 12;
							if (diatonic_intervals) melodic_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToDiatonicInterval(melodic_interval);
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
	 * @param	diatonic_intervals		Whether n-grams are generated when there is a rest in the base voice.
	 * @return							A list of rhythmic value and melodic interval n-grams.
	 * @throws	Exception				Throws an informative exception if n-grams cannot be created for the 
	 *									specified values.
	 */
	/*
	public LinkedList<NGram> getRhythmicValueAndMelodicIntervalNGrams(	int n_value,
																		int[] track_and_channel_pair,
																		boolean direction,
																		boolean wrapping, 
																		boolean diatonic_intervals)
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
				
				// If direction, wrapping, or diatonic intervals has been specified, apply the 
				// necessary changes to the melodic interval
				if (!direction) melodic_interval = Math.abs(melodic_interval);
				if (wrapping) melodic_interval = melodic_interval % 12;
				if (diatonic_intervals) melodic_interval = mckay.utilities.staticlibraries.MiscellaneousMethods.semitonesToDiatonicInterval(melodic_interval);
			
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
	 * @param	diatonic_intervals			Whether intervals are represented by diatonic interval, as opposed 
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
																					boolean diatonic_intervals,
																					boolean ignore_rests_in_base_voice)
			throws Exception
	{
		LinkedList<NGram> n_grams = getVerticalAndMelodicIntervalNGrams(n_value, base_voice, track_and_channel_pairs, direction, wrapping, diatonic_intervals, ignore_rests_in_base_voice);
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
	 * @param	diatonic_intervals			Whether intervals are represented by diatonic interval, as opposed 
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
																									boolean diatonic_intervals)
			throws Exception
	{
		LinkedList<NGram> n_grams = getRhythmicValueAndMelodicIntervalNGrams(n_value, track_and_channel, direction, wrapping, diatonic_intervals);
		return new TwoDimensionalNGramAggregate(n_grams);
	}
	*/
}