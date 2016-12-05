package jsymbolic2.processing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.sound.midi.*;
import jsymbolic2.featureutils.CollectedNoteInfo;

/**
 * An object of this class is instantiated with a MIDI sequence. The constructor parses this sequence and
 * extracts a range of information from it, which is stored in the public fields of this class. The
 * information stored in the fields of an object of this class can be useful in calculating a range of
 * features.
 *
 * <p>After instantiation, an object of this class should simply have its public fields accessed (no changes 
 * to these fields should be made). All public methods are simply static convenience classes for interpreting
 * information stored in public fields (they should never change values in the public fields). Private methods
 * and fields are only used to fill the public fields with values during instantiation, and should not be 
 * used in any other context.</p>
 * 
 * <p>It should be noted that this design is messy. Ultimately, each of the public fields of this class should
 * be re-implemented as individual MEIFeatureExtractor objects.</p>
 *
 * <p><b>Important Notes:</b></p>
 *
 * <ul>
 * <li>Instrument patches are numbered one unit lower here than in their General MIDI patch names, so 
 * remember to raise by one when processing.</li>
 *
 * <li>Channels are numbered 1 lower here than in proper MIDI, so, for example, check for Channel 9 when
 * actually looking for Channel 10.</li>
 *
 * <li>The MIDI data starts at tick 0.</li>
 *
 * <li>MIDI sequences that use SMPTE time encoding are not compatible with this software.</li>
 * </ul>
 * 
 * @author Cory McKay and Tristano Tenaglia
 */
public class MIDIIntermediateRepresentations
{
	/* PUBLIC FIELDS ****************************************************************************************/

	
	/**
	 * Holds miscellaneous metadata read from the MIDI sequence. Note that this information is based purely on
	 * metadata directly specified in the MIDI sequence, not on any sophisticated analysis. Indices of this
	 * field correspond to the following pieces of information:
	 *
	 * <ul>
	 * <li><i>Indice 0:</i> Major/minor quality of the piece, based on the first MIDI key signature 
	 * metamessage encountered in the piece (any key signature metamessages after the first are ignored). 
	 * The value is stored as an Integer, with 0 indicating major and 1 indicating minor. A default value of 0
	 * is stored if no key signature metamessage is present.</li>
	 *
	 * <li><i>Indice 1:</i> LinkedList of all time signature numerators in the order that they appear in the
	 * MIDI sequence. Stored individually as Integers, and based on time signature metamessages. Empty if no
	 * time signature metamessages are present.</li>
	 *
	 * <li><i>Indice 2:</i> LinkedList of all time signature denominators in the order that they appear in the
	 * MIDI sequence. Stored individually as Integers, and based on time signature metamessages. Empty if no
	 * time signature metamessages are present.</li>
	 *
	 * <li><i>Indice 3:</i> Integer storing the initial tempo in beats per minute. Based on the first 
	 * encountered tempo metamessage. Any further tempo metamessages are ignored.</li>
	 * </ul>
	 */
	public Object[] overall_metadata;

	/**
	 * The total duration in seconds of the sequence (as an int).
	 */
	public int sequence_duration;

	/**
	 * The total duration in seconds of the sequence (as a double).
	 */
	public double sequence_duration_precise;

	/**
	 * An array with an entry for each MIDI tick. The value at each entry specifies the duration of that 
	 * particular MIDI tick in seconds.
	 */
	public double[] duration_of_ticks_in_seconds;
	
	/**
	 * A table with rows (first index) corresponding to MIDI ticks and columns (second index) corresponding to
	 * MIDI channels. Each entry's value is set to the channel volume at that tick, as set by channel volume
	 * controller messages divided by 127. The default is set to 1.0, which corresponds to a controller value 
	 * of 127.
	 */
	public double[][] volume_of_channels_tick_map;

	/**
	 * A table with rows (first index) corresponding to General MIDI patch numbers. The first column (second
	 * index) specifies the total number of Note Ons played using each patch. The second column gives the 
	 * total time in seconds (rounded) that at least one note was being held by each patch.
	 */
	public int[][] pitched_instrument_prevalence;

	/**
	 * A table with rows (first index) corresponding to MIDI ticks in the MIDI sequence and columns (second
	 * index) corresponding to each of the pitched General MIDI patches. An entry is set to true if a
	 * particular patch is playing at least one note during a particular MIDI tick.
	 */
	public boolean[][] pitched_instrumentation_tick_map;

	/**
	 * An array whose index is matched to patches from the MIDI Percussion Key Map. Each entry indicates the
	 * total number of Note Ons played by the specified percussion patch. Note that values for all 128
	 * possible values are collected here, although perhaps only notes 35 to 81 should be used. This is
	 * because some MIDI sequences actually use these other values, even though they should not (when they are
	 * used, these other values typically simply duplicate the allowed values).
	 */
	public int[] non_pitched_instrument_prevalence;

	/**
	 * The total number of Note Ons in the sequence.
	 */
	public int total_number_note_ons;

	/**
	 * The total number of Note Ons in the sequence that were played using a General MIDI pitched instrument
	 * (i.e. not Channel 10).
	 */
	public int total_number_pitched_note_ons;

	/**
	 * The total number of Note Ons in the sequence that were played using a MIDI Percussion Key Map
	 * instrument (i.e. on Channel 10). <b>NOTE:</b> although values for all 128 Channel 10 notes values are
	 * collected here, perhaps only notes 35 to 81 should be used. This is because some MIDI sequences
	 * actually use these other values, even though they should not (when they are used, these other values
	 * typically simply duplicate the allowed values).
	 */
	public int total_number_non_pitched_note_ons;

	/**
	 * A normalized histogram, with bins corresponding to rhythmic periodicities measured in beats per minute.
	 * The magnitude of each bin is proportional to the aggregated loudnesses of the notes that occur at the
	 * bin's rhythmic periodicity, and calculation is done using autocorrelation. All bins below 40 BPM are
	 * set to 0 because autocorrelation was not performed at these lags (because the results are too noisy in
	 * this range). Calculations did NOT take tempo change messages after the first into consideration, in
	 * order to emphasize the metrical notation of the recording.
	 */
	public double[] beat_histogram;

	/**
	 * Table with rows (first index) corresponding to the bins of the beat_histogram (i.e. rhythmic
	 * periodicities measured in beats per minute). Entries are set to the magnitude of the corresponding bin
	 * of beat_histogram if the magnitude in that bin of beat_histogram is high enough to meet this
	 * beat_histogram_thresholded_table column's threshold requirements, and to 0 otherwise. Column 0 has a
	 * threshold that only allows beat_histogram bins with a normalized frequency over 0.1 to be counted,
	 * column 1 has a threshold of higher than 0.01, and column 2 only counts beat_histogram bins that have a
	 * normalized frequency at least 30% as high as the normalized frequency of the highest beat_histogram
	 * bin. This table is then processed so that only peaks are included, which is to say that entries of
	 * beat_histogram_thresholded_table that are adjacent to beat_histogram_thresholded_table bins with higher
	 * magnitudes are set to 0.
	 */
	public double[][] beat_histogram_thresholded_table;
	
	/**
	 * A list of the duration of each note in the sequence, in seconds. This includes all notes, including
	 * non-pitched notes on Channel 10. Ordering of notes is iterated first by track, and only then by tick;
	 * this means that this list does NOT necessarily match the temporal order of the notes.
	 */
	public LinkedList<Double> note_durations;

	/**
	 * A table with rows (first index) corresponding to MIDI ticks, and columns (second index) corresponding
	 * to MIDI channels. Entries are set to true whenever a Note On event occurs on a given tick and channel,
	 * and to false otherwise. A different, final summary aggregate column (column 16) is also present; its
	 * entry is set to true if at least one Note On occurs on any channel at the corresponding tick, and to
	 * false otherwise.
	 */
	public boolean[][] note_attack_tick_map;

	/**
	 * Information on the pitch, start tick, end tick, track and channel of every note (including Channel 10
	 * unpitched notes). The notes can be accessed simply as a List of notes, or a query can be made (using
	 * the getNotesStartingOnTick method) to find all notes starting on a particular MIDI tick. Additional
	 * information is also available, such as a list of all notes on a particular channel (via the
	 * getNotesOnChannel method).
	 */
	public CollectedNoteInfo all_notes;
	
	/**
	 * A normalized histogram with bins corresponding to MIDI pitches (0 to 127). The magnitude of each bin
	 * is proportional to the number of Note Ons in the MIDI sequence at the pitch of the bin. Any Note Ons on
	 * Channel 10 (non-pitched percussion) are ignored for the purpose of this histogram.
	 */
	public double[] basic_pitch_histogram;

	/**
	 * A normalized histogram with bins corresponding to MIDI pitch classes (0 to 11). The magnitude of each
	 * bin is proportional to the number of Note Ons in the MIDI sequence at the pitch class of the bin. Any
	 * Note Ons on Channel 10 (non-pitched percussion) are ignored for the purpose of this histogram.
	 * Enharmonic equivalents are assigned the same pitch class number. Index 0 refers to C, and index pitches 
	 * increase by semitone from there.
	 */
	public double[] pitch_class_histogram;

	/**
	 * A normalized histogram with bins corresponding to MIDI pitch classes (0 to 11). The bins are ordered
	 * such that Index 0 refers to C, and adjacent bins are separated by a perfect fifth rather than a
	 * semitone (as is the case with the pitch_class_histogram). The magnitude of each bin is proportional to
	 * the number of Note Ons in the MIDI sequence at the pitch class of the bin. Any Note Ons on Channel 10
	 * (non-pitched percussion) are ignored for the purpose of this histogram. Enharmonic equivalents are
	 * assigned the same pitch class number.
	 */
	public double[] fifths_pitch_histogram;
	
	/**
	 * A list of lists of pitch bends associated with pitched (i.e. not Channel 10) notes. Each entry of the
	 * root list corresponds to a Note On that has at least one pitch bend message associated with it. Each
	 * such entry contains a list of all MIDI pitchbend values (the second MIDI data byte stored as an
	 * Integer) associated with the Note On, in the order that they occurred. Note that the order of the root
	 * list is based on an iteration through tracks, and then through MIDI events on that track; this means
	 * that the order of the Note Ons may not necessarily be in the temporal order that they occur.
	 */
	public LinkedList<LinkedList<Integer>> pitch_bends_list;

	/**
	 * Each bin corresponds to a melodic interval, and the bin index indicates the number of semitones
	 * comprising the interval associated with the bin (there are 128 bins in all). For example, bin 0
	 * corresponds to repeated pitches, bin 1 to a melodic interval of one semitone, bin 2 to a melodic
	 * interval of 2 semitones, etc. The magnitude of each bin is proportional to the fraction of melodic
	 * intervals in the piece that are of the kind associated with the bin (this histogram is normalized).
	 * Rising and falling intervals are treated as identical. Melodies are assumed to be contained within
	 * individual MIDI tracks and channels, so melodic intervals are found separately for each track and
	 * channel before being combined in this histogram. It is also assumed that there is only one melody at a
	 * time per MIDI channel (if multiple notes occur simultaneously on the same MIDI tick on the same MIDI
	 * track and channel, then all notes but the first note on that tick are ignored). Other than this, all
	 * notes on the same track and the same channel are treated as if they are part of a single melody. It is
	 * also assumed that melodies do not cross MIDI tracks or channels (i.e. that they are each separately
	 * contained in their own track and channel). Only pitched notes are considered, so all notes on the
	 * unpitched MIDI Channel 10 are ignored.
	 */
	public double[] melodic_interval_histogram;

	/**
	 * A list of data structures, where the outer list contains a separate entry for each MIDI track. The
	 * inner structure is an array of lists where the array index corresponds to MIDI channel, and each entry
	 * in the array consists of a list of all melodic intervals that occurred in that channel on that track,
	 * in the order that they occurred. Each entry in these lists of melodic intervals is an Integer that
	 * indicates the number of semitones comprising the melodic interval, with positive values indicating
	 * upwards motion and negative values indicating downwards motion. Any notes on Channel 10 (non-pitched
	 * percussion) are ignored (i.e. the Channel 10 entry on the array is left empty). It is assumed that
	 * there is only one melody per channel (if multiple notes occur simultaneously on the same MIDI tick on
	 * the same MIDI track and channel, then all notes but the first note on that tick are ignored). Other
	 * than this, all notes on the same track and the same channel are treated as if they are part of a single
	 * melody. It is also assumed that melodies do not cross MIDI tracks or channels (i.e. that they are each
	 * separately contained in their own track and channel).
	 */
	public LinkedList<LinkedList<Integer>[]> melodic_intervals_by_track_and_channel;

	/**
	 * A table with rows (first index) corresponding to MIDI channels and column (second index) designations
	 * as follows:
	 *
	 * <ul>
	 * <li><i>Column 0:</i> Total number of Note Ons on the given channel.</li>
	 *
	 * <li><i>Column 1:</i> Total amount of time in seconds that one or more notes were sounding on the given
	 * channel.</li>
	 *
	 * <li><i>Column 2:</i> Average loudness (velocity scaled by channel volume, still falls between 0 and 
	 * 127) of notes on the given channel.</li>
	 *
	 * <li><i>Column 3:</i> Average melodic leap on the given channel (in semitones). This includes leaps 
	 * across rests, and ignores direction. Note that this will give potentially problematic values if there
	 * is more than one melody per channel, or if a channel is polyphonic.</li>
	 *
	 * <li><i>Column 4:</i> Lowest MIDI pitch on the given channel (a value of 1000 means no pitches on the
	 * given channel).</li>
	 *
	 * <li><i>Column 5:</i> Highest MIDI pitch on the given channel (a value of -1000 means no pitches on
	 * the given channel).</li>
	 *
	 * <li><i>Column 6:</i> Mean MIDI pitch on the given channel (a value of 0 means there are no pitches on 
	 * the given channel).</li>
	 * </ul>
	 *
	 * <p>NOTE: This data includes MIDI Channel 10, even though it is understood that notes on Channel 10 are
	 * in fact unpitched percussion patches.
	 *
	 * <p>NOTE: This data combines data in all MIDI tracks.
	 */
	public int[][] channel_statistics;
	
	/**
	 * The first list contains a list for each channel. Each channel's list in turn contains the MIDI pitch
	 * of each Note On in that channel, with one entry for each Note On. Note that the order of the pitches
	 * may not in fact reflect the temporal order in which they occurred.
	 *
	 * <p>NOTE: This data includes MIDI Channel 10, even though it is understood that notes on Channel 10 are
	 * in fact unpitched percussion patches.
	 */
	public List<List<Integer>> list_of_note_on_pitches_by_channel;	

	/**
	 * A table with rows (first index) corresponding to MIDI ticks. The columns (second index) correspond to
	 * the MIDI channels. An entry is set to true if one or more notes was sounding on the given channel
	 * during the given MIDI tick.
	 *
	 * <p>NOTE: This data includes MIDI Channel 10, even though it is understood that notes on Channel 10 are
	 * in fact unpitched percussion patches.
	 */
	public boolean[][] note_sounding_on_a_channel_tick_map;
	
	/**
	 * A table indicating what pitches are sounding during each MIDI tick. The first index indicates tick and
	 * the second indicates MIDI pitch (and is always set to size 128). Each entry indicates the cumulative
	 * velocity of all notes (NOT including Channel 10 unpitched notes) sounding at that tick with that
	 * pitch.
	 */
	public short[][] pitch_strength_by_tick_chart;

	/**
	 * Total combined velocity of all notes involved in a vertical unison, summed over the the entire piece.
	 */
	public int total_vertical_unison_velocity;

	/**
	 * A data structure indicating all MIDI pitches (NOT including Channel 10 unpitched notes) sounding at
	 * each MIDI tick. However, all ticks during which no notes are playing are excluded from this data
	 * structure, so the tick index will most likely not correspond to the actual MIDI ticks in the MIDI
	 * stream. The first dimension indicates the MIDI tick (after removal of rest ticks) and the second
	 * dimension indicates the note index (there will be one entry for each MIDI pitch sounding during the
	 * given MIDI tick). Each entry indicates the MIDI pitch number (0 to 127) of one of the sounding notes.
	 */	
	public short[][] pitches_present_by_tick_excluding_rests;
	
	/**
	 * A data structure indicating all pitch classes (NOT including Channel 10 unpitched notes) sounding at
	 * each MIDI tick. However, all ticks during which no notes are playing are excluded from this data
	 * structure, so the tick index will most likely not correspond to the actual MIDI ticks in the MIDI
	 * stream. The first dimension indicates the MIDI tick (after removal of rest ticks) and the second
	 * dimension indicates the note index (there will be one entry for each pitch class sounding during the
	 * given MIDI tick). Each entry indicates the pitch class (0 to 11, where 0 is C) of one of the sounding
	 * notes.
	 */
	public short[][] pitch_classes_present_by_tick_excluding_rests;
	
	/**
	 * A table with rows (first index) corresponding to MIDI channel number, and columns (second index)
	 * corresponding to separate notes (in the temporal order that the Note On for each note occurred). Each
	 * entry specifies the velocity of the Note On scaled by the channel volume at the time of the Note On,
	 * such that each value ranges from 0 to 127. Note that the order of the notes may not always  precisely
	 * reflect the temporal order in which they occurred (because the outer iteration is by MIDI track).
	 */
	public int[][] note_loudnesses;
	

	/* PRIVATE FIELDS ***************************************************************************************/


	/**
	 * The MIDI sequence from which data is extracted by this object.
	 */
	private final Sequence sequence;

	/**
	 * All the MIDI tracks loaded from the MIDI sequence.
	 */
	private final Track[] tracks;

	/**
	 * Average number of MIDI ticks corresponding to 1 second of score time. Tempo change messages can cause
	 * variations in the number of ticks per second, which is why this is a mean value.
	 */
	private final double mean_ticks_per_second;


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Parse the specified MIDI sequence and fill this object's fields with the appropriate information
	 * extracted from the sequence.
	 *
	 * @param	midi_sequence	The MIDI sequence to extract information from.
	 * @throws	Exception		Informative exceptions are thrown if problems are encountered during parsing.
	 */
	public MIDIIntermediateRepresentations(Sequence midi_sequence)
			throws Exception
	{
		// Check the MIDI sequence. Throw exceptions if it uses SMPTE timing or if it is too long.
		// Fill sequence and all_tracks fields otherwise.
		sequence = midi_sequence;
		tracks = sequence.getTracks();
		if (sequence.getDivisionType() != Sequence.PPQ)
			throw new Exception("The MIDI sequence uses SMPTE time encoding.\n"
							  + "Only PPQ time encoding is accepted by this software.");
		if (((double) sequence.getTickLength()) > ((double) Integer.MAX_VALUE) - 1.0)
			throw new Exception("The MIDI sequence could not be processed because it is too long.");

		// Caclulate mean_ticks_per_second
		mean_ticks_per_second = ((double) sequence.getTickLength()) / ((double) sequence.getMicrosecondLength() / 1000000.0);

		// Fill in the public fields of this class
		// Commented out code is for the purpose of testing calculated values in case changes are made
		
		generateOverallMetadata();
		/*int quality = ((Integer) overall_metadata[0]).intValue();
		Object[] numerators_objects = ((LinkedList) overall_metadata[1]).toArray();
		int[] numerators = new int[numerators_objects.length];
		for (int i = 0; i < numerators.length; i++)
			numerators[i] = ((Integer) numerators_objects[i]).intValue();
		Object[] denominators_objects = ((LinkedList) overall_metadata[2]).toArray();
		int[] denominators = new int[denominators_objects.length];
		for (int i = 0; i < denominators.length; i++)
			denominators[i] = ((Integer) denominators_objects[i]).intValue();
		int tempo = ((Integer) overall_metadata[3]).intValue();
		System.out.println(quality);
		for (int i = 0; i < numerators.length; i++)
			System.out.print(numerators[i] + " ");
		for (int i = 0; i < denominators.length; i++)
			System.out.print(denominators[i] + " ");
		System.out.println("\n" + tempo);
		System.out.println();*/
	
		generateSequenceDurationIntermediateRepresentations();
		//System.out.println(sequence_duration);

		generateTempoAndChannelVolumeMaps();
		/*for (int i = 0; i < duration_of_ticks_in_seconds.length; i++)
			System.out.println(i + " " + duration_of_ticks_in_seconds[i]);
		for (int i = 0; i < volume_of_channels_tick_map.length; i++)
		{
			System.out.print("\nTick: " + i + "     ");
			for (int j = 0; j < volume_of_channels_tick_map[i].length; j++)
				System.out.print("j: " + j + " vol: " + volume_of_channels_tick_map[i][j] + "    ");
		}*/
		 
		generatePitchedInstrumentIntermediateRepresentations();
		/*for (int i = 0; i < pitched_instrument_prevalence.length; i++)
			System.out.println("INST: " + i + "   N Ons: " + pitched_instrument_prevalence[i][0] + "    Time: " + pitched_instrument_prevalence[i][1]);
		for (int i = 0; i < pitched_instrumentation_tick_map.length; i++)
		{
			System.out.print("Tick: " + i + " - ");
			for (int j = 0; j < pitched_instrumentation_tick_map[i].length; j++)
				System.out.print("Inst " + j + ": " + pitched_instrumentation_tick_map[i][j] + "  |  ");
			System.out.print("\n");
		}*/
		
		generateNonPitchedInstrumentPrevalence();
		/*for (int i = 0; i < non_pitched_instrument_prevalence.length; i++)
			System.out.println(i + " " + non_pitched_instrument_prevalence[i]);*/

		generateNoteCountIntermediateRepresentations();
		//System.out.println(total_number_note_ons + " " + total_number_pitched_note_ons + " " + total_number_non_pitched_note_ons);

		generateBeatHistogram();
		/*for (int i = 0; i < beat_histogram.length; i++)
			System.out.println("BPM: " + i + ": " + beat_histogram[i]);*/
	
		generateBeatHistogramThresholdTable();
		/*for (int i = 0; i < beat_histogram_thresholded_table.length; i++)
		{
			System.out.print("\nBPM: " + i + "     ");
			for (int j = 0; j < beat_histogram_thresholded_table[i].length; j++)
				System.out.print("   " + beat_histogram_thresholded_table[i][j]);
		}*/
		
		generateNoteDurations();
		/*for (int i = 0; i < note_durations.size(); i++)
		{
			double duration = ((Double) (note_durations.get(i))).doubleValue();
			System.out.println(duration);
		}*/
	
		generateNoteAttackTickMap();
		/*for (int i = 0; i < note_attack_tick_map.length; i++)
		{
			System.out.print("\ntick: " + i + "     ");
			for (int j = 0; j < note_attack_tick_map[i].length; j++)
				System.out.print("   " + note_attack_tick_map[i][j]);
		}*/
		
		generateAllNotes();
		
		generatePitchHistogramsIntermediateRepresentations();
		/*System.out.println("basic_pitch_histogram");
		for (int i = 0; i < basic_pitch_histogram.length; i++)
			System.out.println(i + ": " + basic_pitch_histogram[i]);
		System.out.println("pitch_class_histogram");
		for (int i = 0; i < pitch_class_histogram.length; i++)
			System.out.println(i + ": " + pitch_class_histogram[i]);
		System.out.println("fifths_pitch_histogram");
		for (int i = 0; i < fifths_pitch_histogram.length; i++)
			System.out.println(i + ": " + fifths_pitch_histogram[i]);*/
	
		generatePitchBendsList();
		/*Object[] notes_objects = pitch_bends_list.toArray();
		LinkedList[] notes = new LinkedList[notes_objects.length];
		for (int i = 0; i < notes.length; i++)
			notes[i] = (LinkedList) notes_objects[i];
		int[][] pitch_bends = new int[notes.length][];
		for (int i = 0; i < notes.length; i++)
		{
			Object[] this_note_pitch_bends_objects = notes[i].toArray();
			pitch_bends[i] = new int[this_note_pitch_bends_objects.length];
			for (int j = 0; j < pitch_bends[i].length; j++)
				pitch_bends[i][j] = ((Integer) this_note_pitch_bends_objects[j]).intValue();
		}
		System.out.println(pitch_bends.length);
		for (int i = 0; i < pitch_bends.length; i++)
		{
			for (int j = 0; j < pitch_bends[i].length; j++)
				System.out.print(pitch_bends[i][j] + " ");
			System.out.print("\n\n");
		}
		System.out.print("\n\n--\n\n");*/
		 	
		generateMelodicIntermediateRepresentations();
		/*
		for (int i = 0; i < melodic_interval_histogram.length; i++)
			System.out.println(i + ": " + melodic_interval_histogram[i]);
		for (int t = 0; t < melodic_intervals_by_track_and_channel.size(); t++)
		{
			System.out.print("\nTrack: " + t + "     ");
			LinkedList<Integer>[] melodic_intervals_by_channel = melodic_intervals_by_track_and_channel.get(t);
			for (int i = 0; i < melodic_intervals_by_channel.length; i++)
			{
				System.out.print("\nChannel: " + i + "     ");
				for (int j = 0; j < melodic_intervals_by_channel[i].size(); j++)
					System.out.print(" " + ((Integer) melodic_intervals_by_channel[i].get(j)).intValue());
			}
		}*/
		
		generateChannelNoteOnIntermediateRepresentations();
		/*for (int i = 0; i < channel_statistics.length; i++)
		{
			System.out.print("Channel: " + i + "  ");
			for (int j = 0; j < channel_statistics[i].length; j++)
				System.out.print(channel_statistics[i][j] + "    ");
			System.out.print("\n");
		}
		for (int i = 0; i < note_sounding_on_a_channel_tick_map.length; i++)
		{
			System.out.print("Tick: " + i + " - ");
			for (int j = 0; j < note_sounding_on_a_channel_tick_map[i].length; j++)
				System.out.print("Channel " + j + ": " + note_sounding_on_a_channel_tick_map[i][j] + "  |  ");
			System.out.print("\n");
		}*/

		generatePitchStrengthByTickChartAndCalculateTotalVerticalUnsionVelocity();

		generatePitchesAndPitchClassesPresentByTickExcludingRests();
		/*for (int i = 0; i < pitches_present_by_tick_excluding_rests.length; i++)
		{
			System.out.print("\nTICK " + i + ": ");
			for (int j = 0; j < pitches_present_by_tick_excluding_rests[i].length; j++)
				System.out.print(pitches_present_by_tick_excluding_rests[i][j] + " ");
		}
		for (int i = 0; i < pitch_classes_present_by_tick_excluding_rests.length; i++)
		{
			System.out.print("\nTICK: " + i + ": ");
			for (int j = 0; j < pitch_classes_present_by_tick_excluding_rests[i].length; j++)
				System.out.print(pitch_classes_present_by_tick_excluding_rests[i][j] + " ");
		}*/	

		generateNoteLoudnesses();
		/*for (int i = 0; i < note_loudnesses.length; i++)
			System.out.print("\nCHAN: " + i + "  ");
			for (int j = 0; j < note_loudnesses[i].length; j++)
				System.out.print("   " + note_loudnesses[i][j]);
		System.out.println("\n");*/
	}

	
	/* STATIC PUBLIC METHOD *********************************************************************************/


	/**
	 * Return the fraction of Note Ons in the MIDI sequence from which the given 
	 * MIDIIntermediateRepresentations has been generated that are played by one of the General MIDI patches
	 * included in the the given set of MIDI instrument patch numbers.
	 *
	 * @param	instruments		An array holding the General MIDI patches of interest.
	 * @param	sequence_info	Data extracted from a MIDI sequence.
	 * @return					The fraction of Note Ons played by the specified MIDI instrument patches.
	 */
	public static double calculateInstrumentGroupFrequency(int[] instruments,
			MIDIIntermediateRepresentations sequence_info)
	{
		int notes_played = 0;
		for (int i = 0; i < instruments.length; i++)
			notes_played += sequence_info.pitched_instrument_prevalence[instruments[i]][0];
		return ((double) notes_played) / ((double) sequence_info.total_number_note_ons);
	}

	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Calculate values for the overall_metadata field.
	 */
	private void generateOverallMetadata()
	{
		// Instantiat overall_metadata
		overall_metadata = new Object[4];
		overall_metadata[0] = new Integer(0); // major or minor
		overall_metadata[1] = new LinkedList(); // time signature numerators
		overall_metadata[2] = new LinkedList(); // time signature denominators
		overall_metadata[3] = new Integer(0); // tempo

		// Note that a key signature and initial tempo have not yet been found
		boolean key_sig_found = false;
		boolean tempo_found = false;

		// Search for MetaMessages
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Go through all the events in the current track, searching for meta messages
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If MIDI message is a MetaMessage
				if (message instanceof MetaMessage)
				{
					byte[] data = ((MetaMessage) message).getData();

					// Check if major or minor, based on first key signature
					if (((MetaMessage) message).getType() == 0x59)
					{
						if (!key_sig_found)
						{
							if (data[1] == 0) // major
								overall_metadata[0] = new Integer(0);
							else if (data[1] == 1) // minor
								overall_metadata[0] = new Integer(1);
								key_sig_found = true;
						}
					}

					// Check time signature, based on first time signature
					if (((MetaMessage) message).getType() == 0x58)
					{
						((LinkedList) overall_metadata[1]).add(new Integer((int) (data[0] & 0xFF)));
						((LinkedList) overall_metadata[2]).add(new Integer((int) (1 << (data[1] & 0xFF))));
					}

					// Check initial tempo
					if (((MetaMessage) message).getType() == 0x51)
					{
						if (!tempo_found)
						{
							// Find tempo in microseconds per beat
							int ms_tempo = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);

							// Convert to beats per minute
							float ms_tempo_float = (float) ms_tempo;
							if (ms_tempo_float <= 0)
								ms_tempo_float = 0.1f;
							float bpm = 60000000.0f / ms_tempo_float;
							overall_metadata[3] = new Integer((int) bpm);

							tempo_found = true;
						}
					}
				}
			}
		}
	}


	/**
	 * Calculate the values of the sequence_duration and sequence_duration_precise fields.
	 */
	private void generateSequenceDurationIntermediateRepresentations()
	{
		sequence_duration = (int) (sequence.getMicrosecondLength() / 1000000);
		sequence_duration_precise = sequence.getMicrosecondLength() / 1000000.0;
	}


	/**
	 * Calculate the value of the duration_of_ticks_in_seconds and volume_of_channels_tick_map fields based on
	 * tempo change and channel volume controller messages messages.
	 */
	private void generateTempoAndChannelVolumeMaps()
	{
		// Instantiate duration_of_ticks_in_seconds and initialize entries to the average number of ticks per
		// second
		duration_of_ticks_in_seconds = new double[(int) sequence.getTickLength() + 1];
		for (int i = 0; i < duration_of_ticks_in_seconds.length; i++)
			duration_of_ticks_in_seconds[i] = 1.0 / mean_ticks_per_second;

		// Instantiate volume_of_channels_tick_map and initialize entries to 1.0
		volume_of_channels_tick_map = new double[(int) sequence.getTickLength() + 1][16];
		for (int i = 0; i < volume_of_channels_tick_map.length; i++)
			for (int j = 0; j < volume_of_channels_tick_map[i].length; j++)
				volume_of_channels_tick_map[i][j] = 1.0;

		// Fill in duration_of_ticks_in_seconds based on tempo change messagess
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Go through all the events in the current track, searching for tempo change messages
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If message is a MetaMessage (which tempo change messages are)
				if (message instanceof MetaMessage)
				{
					MetaMessage meta_message = (MetaMessage) message;

					if (meta_message.getType() == 0x51) // tempo change message
					{
						// Find the number of PPQ ticks per beat
						int ticks_per_beat = sequence.getResolution();

						// Find the number of microseconds per beat
						byte[] meta_data = meta_message.getData();
						int microseconds_per_beat = ((meta_data[0] & 0xFF) << 16)
								| ((meta_data[1] & 0xFF) << 8)
								| (meta_data[2] & 0xFF);

						// Find the number of seconds per tick
						double current_seconds_per_tick = ((double) microseconds_per_beat) / ((double) ticks_per_beat);
						current_seconds_per_tick = current_seconds_per_tick / 1000000.0;

						//System.out.println("Tick: " + event.getTick() + "  Current: " + current_seconds_per_tick  + "   Average: " + (1.0 / mean_ticks_per_second));
						
						// Make all subsequent tempos be at the current_seconds_per_tick rate
						for (int i = (int) event.getTick(); i < duration_of_ticks_in_seconds.length; i++)
							duration_of_ticks_in_seconds[i] = current_seconds_per_tick;
					}
				}

				// If message is a ShortMessage (which volume controller messages are)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;

					if (short_message.getCommand() == 0xb0) // Controller message
					{
						if (short_message.getData1() == 7) // Volume controller
						{
							// Make all subsequent channel volumes be at the given channel
							for (int i = (int) event.getTick(); i < duration_of_ticks_in_seconds.length; i++)
								volume_of_channels_tick_map[i][short_message.getChannel()] = ((double) short_message.getData2()) / 127.0;

							//System.out.println("-> " + event.getTick() + " " + short_message.getChannel() + " " + short_message.getData2());
						}
					}
				}
			}
		}
	}


	/**
	 * Calculate the values of the pitched_instrument_prevalence and pitched_instrumentation_tick_map fields.
	 */
	private void generatePitchedInstrumentIntermediateRepresentations()
	{
		// Instantiate pitched_instrument_prevalence and initialize entries to 0
		pitched_instrument_prevalence = new int[128][2];
		for (int i = 0; i < pitched_instrument_prevalence.length; i++)
		{
			pitched_instrument_prevalence[i][0] = 0;
			pitched_instrument_prevalence[i][1] = 0;
		}

		// Instantiate pitched_instrumentation_tick_map and initialize entries to false
		pitched_instrumentation_tick_map = new boolean[(int) sequence.getTickLength() + 1][128];
		for (int i = 0; i < pitched_instrumentation_tick_map.length; i++)
			for (int j = 0; j < pitched_instrumentation_tick_map[i].length; j++)
				pitched_instrumentation_tick_map[i][j] = false;

		// Fill in fields
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Keep track of what patch is being used for each channel. Default is 0.
			int[] current_patch_numbers = new int[16];
			for (int i = 0; i < current_patch_numbers.length; i++)
				current_patch_numbers[i] = 0;

			// Go through all the events in the current track, searching for note ons, note offs and program 
			// change messages
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If message is a ShortMessage (which Note Ons, Note Offs and Program Change messages are)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
					{
						// If a Program Change message is encountered, then update current_patch_numbers
						if (short_message.getCommand() == 0xc0)
							current_patch_numbers[short_message.getChannel()] = short_message.getData1();

						// If a Note On message is encountered, then increment first column of
						// pitched_instrument_prevalence and note that have started playing/ the appropriate
						// instrument
						if (short_message.getCommand() == 0x90)
						{
							if (short_message.getData2() != 0) // not velocity 0
							{
								// Increment the Note On count in pitched_instrument_prevalence
								pitched_instrument_prevalence[current_patch_numbers[short_message.getChannel()]][0]++;

								// Look ahead to find the corresponding note off for this note on
								int event_start_tick = (int) event.getTick();
								int event_end_tick = track.size(); // when the note off occurs (default to last tick)
								for (int i = n_event + 1; i < track.size(); i++)
								{
									MidiEvent end_event = track.get(i);
									MidiMessage end_message = end_event.getMessage();
									if (end_message instanceof ShortMessage)
									{
										ShortMessage end_short_message = (ShortMessage) end_message;
										if (end_short_message.getChannel() == short_message.getChannel()) // must be on same channel
										{
											if (end_short_message.getCommand() == 0x80) // note off
											{
												if (end_short_message.getData1() == short_message.getData1()) // same pitch
												{
													event_end_tick = (int) end_event.getTick();
													i = track.size() + 1; // exit loop
												}
											}
											if (end_short_message.getCommand() == 0x90) // note on (with vel 0 is equiv to note off)
											{
												if (end_short_message.getData2() == 0) // velocity 0
												{
													if (end_short_message.getData1() == short_message.getData1()) // same pitch
													{
														event_end_tick = (int) end_event.getTick();
														i = track.size() + 1; // exit loop
													}
												}
											}
										}
									}
								}

								// Fill in pitched_instrumentation_tick_map for all the ticks corresponding to this note
								for (int i = event_start_tick; i < event_end_tick; i++)
									pitched_instrumentation_tick_map[i][current_patch_numbers[short_message.getChannel()]] = true;
							}
						}
					}
				}
			}
		}

		// Note the total time that each instrument was sounding in pitched_instrument_prevalence
		double[] total = new double[pitched_instrument_prevalence.length];
		for (int i = 0; i < total.length; i++)
			total[i] = 0.0;
		for (int instrument = 0; instrument < pitched_instrument_prevalence.length; instrument++)
			for (int tick = 0; tick < pitched_instrumentation_tick_map.length; tick++)
				if (pitched_instrumentation_tick_map[tick][instrument])
					total[instrument] = total[instrument] + duration_of_ticks_in_seconds[tick];
		for (int i = 0; i < total.length; i++)
			pitched_instrument_prevalence[i][1] = (int) total[i];
	}

	
	/**
	 * Calculate the values of the non_pitched_instrument_prevalence field. Note that all 128 note values are
	 * collected, although in general only note values 35 to 81 should be used.
	 */
	private void generateNonPitchedInstrumentPrevalence()
	{
		// Instantiate non_pitched_instrument_prevalence and initialize entries to 0
		non_pitched_instrument_prevalence = new int[128];
		for (int i = 0; i < non_pitched_instrument_prevalence.length; i++)
			non_pitched_instrument_prevalence[i] = 0;

		// Fill in non_pitched_instrument_prevalence
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Go through all the events in the current track, searching for note ons
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If message is a ShortMessage (which Note Ons are)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getChannel() == 10 - 1) // is channel 10 (percussion)
					{
						// If a Note On message is encountered, then increment appropriate row of
						// non_pitched_instrument_prevalence
						if (short_message.getCommand() == 0x90)
						{
							if (short_message.getData2() != 0) // not velocity 0
							{
								// Increment the Note On count in non_pitched_instrument_prevalence
								non_pitched_instrument_prevalence[short_message.getData1()]++;
							}
						}
					}
				}
			}
		}
	}


	/**
	 * Calculate the values of the total_number_note_ons, total_number_pitched_note_ons and 
	 * total_number_non_pitched_note_ons fields.
	 */
	private void generateNoteCountIntermediateRepresentations()
	{
		// Calculate total_number_note_ons
		total_number_note_ons = 0;
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Go through all the events in the current track, searching for note ons
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If message is a ShortMessage (which Note Ons are)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getCommand() == 0x90)
						if (short_message.getData2() != 0) // not velocity 0
							total_number_note_ons++;
				}
			}
		}

		// Calculate total_number_pitched_note_ons
		total_number_pitched_note_ons = 0;
		for (int i = 0; i < pitched_instrument_prevalence.length; i++)
			total_number_pitched_note_ons += pitched_instrument_prevalence[i][0];

		// Calculate total_number_non_pitched_note_ons
		total_number_non_pitched_note_ons = 0;
		for (int i = 0; i < non_pitched_instrument_prevalence.length; i++)
			total_number_non_pitched_note_ons += non_pitched_instrument_prevalence[i];
	}


	/**
	 * Calculate the values of the beat_histogram field.
	 */
	private void generateBeatHistogram()
	{
		// Set the minimum and maximum periodicities that will be used in the autocorrelation
		int min_BPM = 40;
		int max_BPM = 200;

		// Instantiate beat_histogram and initialize entries to 0
		beat_histogram = new double[max_BPM + 1];
		for (int i = 0; i < beat_histogram.length; i++)
			beat_histogram[i] = 0.0;

		// Generate an array whose indices correspond to ticks and whose contents
		// correspond to total velocity of all notes occuring at each given tick
		int[] rhythm_score = new int[((int) sequence.getTickLength()) + 1];
		for (int i = 0; i < rhythm_score.length; i++)
			rhythm_score[i] = 0;
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event and find the MIDI tick
				// that it corresponds to
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();
				int current_tick = (int) event.getTick();

				// Mark rhythm_score with combined loudness on a tick with a note on
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getCommand() == 0x90) // note on
						rhythm_score[current_tick] += (int) (((double) short_message.getData2()) * volume_of_channels_tick_map[current_tick][short_message.getChannel()]);
				}
			}
		}

		// Histogram based on tick interval bins
		double[] tick_histogram = new double[convertBPMtoTicks(min_BPM - 1, mean_ticks_per_second)];
		for (int lag = convertBPMtoTicks(max_BPM, mean_ticks_per_second); lag < tick_histogram.length; lag++)
			tick_histogram[lag] = autoCorrelate(rhythm_score, lag);

		// Histogram with tick intervals collected into beats per minute bins
		for (int bin = min_BPM; bin <= max_BPM; bin++)
		{
			beat_histogram[bin] = 0.0;
			for (int tick = convertBPMtoTicks(bin, mean_ticks_per_second); tick < convertBPMtoTicks(bin - 1, mean_ticks_per_second); tick++)
				beat_histogram[bin] += tick_histogram[tick];
		}

		// Normalize beat_histogram
		double sum = 0;
		for (int i = 0; i < beat_histogram.length; i++)
			sum += beat_histogram[i];
		for (int i = 0; i < beat_histogram.length; i++)
			beat_histogram[i] = beat_histogram[i] / sum;
	}


	/**
	 * Calculate the values of the beat_histogram_thresholded_table field.
	 */
	private void generateBeatHistogramThresholdTable()
	{
		// Instantiate beat_histogram_thresholded_table and set entries to 0
		beat_histogram_thresholded_table = new double[beat_histogram.length][3];
		for (int i = 0; i < beat_histogram_thresholded_table.length; i++)
			for (int j = 0; j < beat_histogram_thresholded_table[i].length; j++)
				beat_histogram_thresholded_table[i][j] = 0.0;

		// Find the highest frequency in rhythmic histogram
		double highest_frequency = beat_histogram[getIndexOfHighest(beat_histogram)];

		// Fill out beat_histogram_thresholded_table
		for (int i = 0; i < beat_histogram.length; i++)
		{
			if (beat_histogram[i] > 0.1)
				beat_histogram_thresholded_table[i][0] = beat_histogram[i];
			if (beat_histogram[i] > 0.01)
				beat_histogram_thresholded_table[i][1] = beat_histogram[i];
			if (beat_histogram[i] > (0.3 * highest_frequency))
				beat_histogram_thresholded_table[i][2] = beat_histogram[i];
		}

		// Make sure all values refer to peaks (are not adjacent to higher values in
		// beat_histogram_thresholded_table)
		for (int i = 1; i < beat_histogram_thresholded_table.length; i++)
			for (int j = 0; j < beat_histogram_thresholded_table[i].length; j++)
				if (beat_histogram_thresholded_table[i][j] > 0.0 && beat_histogram_thresholded_table[i - 1][j] > 0.0)
				{
					if (beat_histogram_thresholded_table[i][j] > beat_histogram_thresholded_table[i - 1][j])
						beat_histogram_thresholded_table[i - 1][j] = 0.0;
					else
						beat_histogram_thresholded_table[i][j] = 0.0;
				}
	}


	/**
	 * Calculate the contents of the note_durations field.
	 */
	private void generateNoteDurations()
	{
		// Instantiate note_durations as an empty list
		note_durations = new LinkedList<>();

		// Fill in the list
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If message is a ShortMessage (which Note Ons are)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;

					// If a Note On message is encountered
					if (short_message.getCommand() == 0x90)
					{
						if (short_message.getData2() != 0) // not velocity 0
						{
							// Look ahead to find the corresponding note off for this note on
							int event_start_tick = (int) event.getTick();
							int event_end_tick = track.size(); // when the note off occurs (default to last tick)
							for (int i = n_event + 1; i < track.size(); i++)
							{
								MidiEvent end_event = track.get(i);
								MidiMessage end_message = end_event.getMessage();
								if (end_message instanceof ShortMessage)
								{
									ShortMessage end_short_message = (ShortMessage) end_message;
									if (end_short_message.getChannel() == short_message.getChannel()) // must be on same channel
									{
										if (end_short_message.getCommand() == 0x80) // note off
										{
											if (end_short_message.getData1() == short_message.getData1()) // same pitch
											{
												event_end_tick = (int) end_event.getTick();
												i = track.size() + 1; // exit loop
											}
										}
										if (end_short_message.getCommand() == 0x90) // note on with velocity 0 is equivalent to note off
										{
											if (end_short_message.getData2() == 0) // velocity 0
											{
												if (end_short_message.getData1() == short_message.getData1()) // same pitch
												{
													event_end_tick = (int) end_event.getTick();
													i = track.size() + 1; // exit loop
												}
											}
										}
									}
								}
							}

							// Calculate duration of note
							double duration = 0;
							for (int i = event_start_tick; i < event_end_tick; i++)
								duration += duration_of_ticks_in_seconds[i];

							// Add note to list
							note_durations.add(new Double(duration));
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Find the values of the note_attack_tick_map field
	 */
	private void generateNoteAttackTickMap()
	{
		// Instantiate note_attack_tick_map and set entries to false
		note_attack_tick_map = new boolean[((int) sequence.getTickLength()) + 1][17];
		for (int i = 0; i < note_attack_tick_map.length; i++)
			for (int j = 0; j < note_attack_tick_map[i].length; j++)
				note_attack_tick_map[i][j] = false;

		// Fill in note_attack_tick_map for all channels
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Go through all the events in the current track, searching for note ons
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				// Get the MIDI message corresponding to the next MIDI event
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// If message is a ShortMessage (which Note Ons are)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getCommand() == 0x90) // note on
						if (short_message.getData2() != 0) // not velocity 0
							note_attack_tick_map[(int) event.getTick()][short_message.getChannel()] = true;
				}
			}
		}

		// Fill in column 16 of note_attack_tick_map to show if a Note On occured on at least one channel
		// during the corresponding tick
		for (int i = 0; i < note_attack_tick_map.length; i++)
		{
			for (int j = 0; j < note_attack_tick_map[i].length - 1; j++)
			{
				if (note_attack_tick_map[i][j])
				{
					note_attack_tick_map[i][16] = true;
					j = note_attack_tick_map[i].length; // exit loop
				}
			}
		}
	}

	
	/**
	 * Fill the all_notes field.  
	 */
	private void generateAllNotes()
	{
		all_notes = new CollectedNoteInfo(tracks);
	}
	
	
	/**
	 * Calculate the values of the basic_pitch_histogram, pitch_class_histogram and fifths_pitch_histogram
	 * fields.
	 */
	private void generatePitchHistogramsIntermediateRepresentations()
	{
		// Initialize basic_pitch_histogram
		basic_pitch_histogram = new double[128];
		for (int i = 0; i < basic_pitch_histogram.length; i++)
			basic_pitch_histogram[i] = 0.0;

		// Fill basic_pitch_histogram
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();

				// Increment pitch of a note on
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
						if (short_message.getCommand() == 0x90) // note on
							if (short_message.getData2() != 0) // not velocity 0
								basic_pitch_histogram[short_message.getData1()]++;
				}
			}
		}

		// Normalize basic_pitch_histogram
		double sum = 0.0;
		for (int i = 0; i < basic_pitch_histogram.length; i++)
			sum += basic_pitch_histogram[i];
		for (int i = 0; i < basic_pitch_histogram.length; i++)
			basic_pitch_histogram[i] = basic_pitch_histogram[i] / sum;

		// Generate pitch_class_histogram
		pitch_class_histogram = new double[12];
		for (int i = 0; i < pitch_class_histogram.length; i++)
			pitch_class_histogram[i] = 0;
		for (int i = 0; i < basic_pitch_histogram.length; i++)
			pitch_class_histogram[i % 12] += basic_pitch_histogram[i];

		// Generate fifths_pitch_histogram
		fifths_pitch_histogram = new double[12];
		for (int i = 0; i < fifths_pitch_histogram.length; i++)
			fifths_pitch_histogram[i] = 0;
		for (int i = 0; i < fifths_pitch_histogram.length; i++)
			fifths_pitch_histogram[(7 * i) % 12] += pitch_class_histogram[i];
	}

	
	/**
	 * Calculate the values of the pitch_bends_list field.
	 */
	private void generatePitchBendsList()
	{
		// The list of lists of pitch bends
		pitch_bends_list = new LinkedList<>();

		// The lists of pitch bends for the most recently found note on each channel. An entry will be null
		// unless a pitch bend message has been received on the given channel since the last Note Off on that
		// channel
		LinkedList going[] = new LinkedList[16];
		for (int i = 0; i < going.length; i++)
			going[i] = null;

		// Fill pitch_bends_list
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
					{
						// If message is a pitch bend
						if (short_message.getCommand() == 0xe0)
						{
							int pitch_bend_value = short_message.getData2();
							
							// If a pitch bend has already been given for this note
							if (going[short_message.getChannel()] != null)
								going[short_message.getChannel()].add(pitch_bend_value);

							// If a pitch bend has not already been given for this note
							else
							{
								LinkedList this_note = new LinkedList();
								this_note.add(pitch_bend_value);
								pitch_bends_list.add(this_note);

								going[short_message.getChannel()] = this_note;
							}
						}

						// If message is a Note Off
						else if (short_message.getCommand() == 0x80) // note off
							going[short_message.getChannel()] = null;
						else if (short_message.getCommand() == 0x90) // note on with velocity 0
						{
							if (short_message.getData2() == 0) // velocity 0
								going[short_message.getChannel()] = null;
						}
					}
				}
			}
		}
	}

	
	/**
	 * Calculate the values of the melodic_interval_histogram and melodic_intervals_by_track_and_channel
	 * fields.
	 */
	private void generateMelodicIntermediateRepresentations()
	{
		// Initialize the melodic_intervals_by_track_and_channel field
		melodic_intervals_by_track_and_channel = new LinkedList<>();
				
		// Initialize melodic_interval_histogram
		melodic_interval_histogram = new double[128];
		for (int i = 0; i < melodic_interval_histogram.length; i++)
			melodic_interval_histogram[i] = 0.0;

		// Fill melodic_interval_histogram and melodic_intervals_by_track_and_channel
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// Prepare melodic_intervals_by_channel for this channel, and add it to
			// melodic_intervals_by_track_and_channel
			LinkedList<Integer>[] melodic_intervals_by_channel = new LinkedList[16];
			for (int i = 0; i < melodic_intervals_by_channel.length; i++)
				melodic_intervals_by_channel[i] = new LinkedList<>();
			melodic_intervals_by_track_and_channel.add(melodic_intervals_by_channel);

			// The last MIDI pitch encountered on each channel
			// -1 means none was encountered yet
			int[] previous_pitches = new int[16];
			for (int i = 0; i < previous_pitches.length; i++)
				previous_pitches[i] = -1;

			// The last tick on which a note on was encountered for each channel
			// -1 means none was encountered yet
			int[] last_tick = new int[16];
			for (int i = 0; i < last_tick.length; i++)
				last_tick[i] = -1;

			// Go through all MIDI events on this track
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getCommand() == 0x90) // note on
					{
						if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
						{
							if (short_message.getData2() != 0) // not velocity 0
							{
								int current_tick = (int) event.getTick();
								if (previous_pitches[short_message.getChannel()] != -1)
								{
									// Check if the note is occuring on the same tick as the previous note
									// on this channel (which would indicate a vertical interval, not a
									// melodic interval)
									if (current_tick != last_tick[short_message.getChannel()])
									{
										int interval = short_message.getData1() - previous_pitches[short_message.getChannel()];
										melodic_interval_histogram[Math.abs(interval)]++;
										melodic_intervals_by_channel[short_message.getChannel()].add(interval);
									}
								}
								last_tick[short_message.getChannel()] = current_tick;
								previous_pitches[short_message.getChannel()] = short_message.getData1();
							}
						}
					}
				}
			}
		}

		// Normalize melodic_interval_histogram
		double sum = 0.0;
		for (int i = 0; i < melodic_interval_histogram.length; i++)
			sum += melodic_interval_histogram[i];
		for (int i = 0; i < melodic_interval_histogram.length; i++)
			melodic_interval_histogram[i] = melodic_interval_histogram[i] / sum;
	}


	/**
	 * Calculate the values of the channel_statistics, list_of_note_on_pitches_by_channel and
	 * note_sounding_on_a_channel_tick_map fields.
	 */
	private void generateChannelNoteOnIntermediateRepresentations()
	{
		// Instantiate channel_statistics and initialize entries to 0
		channel_statistics = new int[16][7];
		for (int i = 0; i < channel_statistics.length; i++)
			for (int j = 0; j < channel_statistics[i].length; j++)
				channel_statistics[i][j] = 0;

		// Instantiate list_of_note_on_pitches_by_channel with one empty list per channel
		list_of_note_on_pitches_by_channel = new ArrayList<>(16);
		for (int channel = 0; channel < 16; channel++)
			list_of_note_on_pitches_by_channel.add(new ArrayList<>());

		// Instantiate note_sounding_on_a_channel_tick_map and initialize entries to false
		note_sounding_on_a_channel_tick_map = new boolean[(int) sequence.getTickLength() + 1][16];
		for (int i = 0; i < note_sounding_on_a_channel_tick_map.length; i++)
			for (int j = 0; j < note_sounding_on_a_channel_tick_map[i].length; j++)
				note_sounding_on_a_channel_tick_map[i][j] = false;

		// Last MIDI tick on which a Note On was encountered, by channel, initialized to -1 for none
		int[] tick_of_last_note_on = new int[16];
		for (int i = 0; i < tick_of_last_note_on.length; i++)
			tick_of_last_note_on[i] = -1;

		// Total sum of the MIDI pitches of al Note Ons encountered, by channel
		int[] sum_of_pitches = new int[16];
		for (int i = 0; i < sum_of_pitches.length; i++)
			sum_of_pitches[i] = 0;

		// Lowest MIDI pitch encountered on each channel, initialized to 1000 for none
		int[] lowest_pitch_so_far = new int[16];
		for (int i = 0; i < lowest_pitch_so_far.length; i++)
			lowest_pitch_so_far[i] = 1000;

		// Highest MIDI pitch encountered on each channel, initialized to -1000 for none
		int[] highest_pitch_so_far = new int[16];
		for (int i = 0; i < highest_pitch_so_far.length; i++)
			highest_pitch_so_far[i] = -1000;

		// Total number of melodic intervals, by channel
		int[] total_number_melodic_intervals = new int[16];
		for (int i = 0; i < total_number_melodic_intervals.length; i++)
			total_number_melodic_intervals[i] = 0;

		// Total combined distance (in semitones) of all melodic leaps, by channel
		int[] sum_of_melodic_intervals = new int[16];
		for (int i = 0; i < sum_of_melodic_intervals.length; i++)
			sum_of_melodic_intervals[i] = 0;

		// Go through all MIDI tracks, and all MIDI events on each track, searching for Notes Ons and Off
		for (int track_i = 0; track_i < tracks.length; track_i++)
		{
			// The MIDI pitch of the last Note On encountered on each channel, initialized to -1 for none
			int[] previous_pitch = new int[16];
			for (int i = 0; i < previous_pitch.length; i++)
				previous_pitch[i] = -1;

			// Go through all the MIDI events on this track
			Track this_track = tracks[track_i];
			for (int event_i = 0; event_i < this_track.size(); event_i++)
			{
				if (this_track.get(event_i).getMessage() instanceof ShortMessage) // If message is a ShortMessage (which Note Ons and Note Offs are)
				{
					ShortMessage short_message = (ShortMessage) this_track.get(event_i).getMessage();
					if (short_message.getCommand() == 0x90) // Note On
					{
						// Note information about this Note On
						int on_tick = (int) this_track.get(event_i).getTick();
						int on_channel = short_message.getChannel();
						int on_pitch = short_message.getData1();
						int on_velocity = short_message.getData2();
						
						// Update values based on this Note On
						if (on_velocity != 0) // Not velocity 0
						{
							// Add the pitch of this note to list_of_note_on_pitches_by_channel
							list_of_note_on_pitches_by_channel.get(on_channel).add(on_pitch);

							// Update the total number of Note Ons
							channel_statistics[on_channel][0]++;

							// Total the loudnesses of Note Ons for each channel
							channel_statistics[on_channel][2] += (int) (((double) on_velocity) * volume_of_channels_tick_map[on_tick][on_channel]);

							// Update sum_of_pitches
							sum_of_pitches[on_channel] += on_pitch;

							// Update lowest_pitch_so_far, if appropriate
							if (on_pitch < lowest_pitch_so_far[on_channel])
								lowest_pitch_so_far[on_channel] = on_pitch;

							// Update highest_pitch_so_far, if appropriate
							if (on_pitch > highest_pitch_so_far[on_channel])
								highest_pitch_so_far[on_channel] = on_pitch;

							// Update variables relating to melodic intervals
							if (previous_pitch[on_channel] != -1)
							{
								// Check if the note is occuring on the same tick as the previous note on this
								// channel (which would indicate a vertical interval, not a melodic leap)
								if (on_tick != tick_of_last_note_on[on_channel])
								{
									sum_of_melodic_intervals[on_channel] += Math.abs(previous_pitch[on_channel] - on_pitch);
									total_number_melodic_intervals[on_channel]++;
								}
							}
							tick_of_last_note_on[on_channel] = on_tick;
							previous_pitch[on_channel] = on_pitch;
							
							// Look ahead to find the Note Off corresponding to this Note On
							int end_tick = this_track.size(); // When the Note Off occurs, defaulted to the last tick
							for (int i = event_i + 1; i < this_track.size(); i++)
							{
								MidiEvent end_event = this_track.get(i);
								if (end_event.getMessage() instanceof ShortMessage)
								{
									ShortMessage end_message = (ShortMessage) end_event.getMessage();
									if (end_message.getChannel() == on_channel) // Must be on same channel as Note On
									{
										if (end_message.getCommand() == 0x80) // Note off
										{
											if (end_message.getData1() == on_pitch) // Pitch must match
											{
												end_tick = (int) end_event.getTick();
												i = this_track.size() + 1; // Exit loop
											}
										}
										if (end_message.getCommand() == 0x90) // Note On (with velocity 0 is equivalent to Note Off)
										{
											if (end_message.getData2() == 0) // Velocity 0
											{
												if (end_message.getData1() == on_pitch) //Pitch must match
												{
													end_tick = (int) end_event.getTick();
													i = this_track.size() + 1; // Exit loop
												}
											}
										}
									}
								}
							}

							// Fill in note_sounding_on_a_channel_tick_map for all the ticks corresponding to this note
							for (int i = on_tick; i < end_tick; i++)
								note_sounding_on_a_channel_tick_map[i][on_channel] = true;
						}
					}
				}
			}
		}

		// Fill column 1 of channel_statistics by finding the total amount of time that one or more notes were
		// playing on each channel
		double[] total_seconds = new double[channel_statistics.length];
		for (int ch = 0; ch < total_seconds.length; ch++)
			total_seconds[ch] = 0.0;
		for (int ti = 0; ti < note_sounding_on_a_channel_tick_map.length; ti++)
			for (int ch = 0; ch < note_sounding_on_a_channel_tick_map[ti].length; ch++)
				if (note_sounding_on_a_channel_tick_map[ti][ch])
					total_seconds[ch] += duration_of_ticks_in_seconds[ti];
		for (int ch = 0; ch < channel_statistics.length; ch++)
			channel_statistics[ch][1] = (int) total_seconds[ch];

		// Fill column 2 of channel_statistics by dividing by the total number of notes per channel notes on
		// each channel
		for (int i = 0; i < channel_statistics.length; i++)
			channel_statistics[i][2] = (int) (((double) channel_statistics[i][2]) / ((double) channel_statistics[i][0]));

		// Fill column 3 of channel_statistics by dividing the total melodic leaps in semi-tones by the number
		// of melodic intervals for each channel
		for (int i = 0; i < channel_statistics.length; i++)
			channel_statistics[i][3] = (int) (((double) sum_of_melodic_intervals[i]) / ((double) total_number_melodic_intervals[i]));

		// Fill columns 4 and 5 (lowest and highest pitches) of channel_statistics
		for (int i = 0; i < channel_statistics.length; i++)
		{
			channel_statistics[i][4] = lowest_pitch_so_far[i];
			channel_statistics[i][5] = highest_pitch_so_far[i];
		}

		// Fill column 6 of channel_statistics
		for (int i = 0; i < channel_statistics.length; i++)
		{
			if (channel_statistics[i][0] == 0)
				channel_statistics[i][6] = 0;
			else
				channel_statistics[i][6] = sum_of_pitches[i] / channel_statistics[i][0];
		}
	}

	
	/**
	 * Calculate the values of the pitch_strength_by_tick_chart and the total_vertical_unison_velocity fields.
	 */
	private void generatePitchStrengthByTickChartAndCalculateTotalVerticalUnsionVelocity()
	{
		// Duration of the piece in MIDI ticks
		int duration_in_ticks = (int) sequence.getTickLength() + 1;

		// Number of piches in the MIDI specification
		int candidate_midi_pitches = 128;

		// Instantiate pitch_strength_by_tick_chart with 0 values
		pitch_strength_by_tick_chart = new short[duration_in_ticks][candidate_midi_pitches];

		// Initialize total_vertical_unison_velocity
		total_vertical_unison_velocity = 0;

		// Intantiate a chart indicating the number of notes sounding at each pitch during each tick.
		// The first index indicates the MIDI tick and the second index indicates the MIDI pitch (this is 
		// always set to size 128). Each entry indicates the number of notes sounding. For example, a value of
		// 1 means on note is sounding at the given tick and pitch, a value of 2 means 2 notes are sounding 
		// (and thus there is 1 unison), a value of 3 means 3 notes are sounding (and thus there are 2 
		// unisons), etc. Instantiated with 0 values.
		short[][] number_notes_sounding_by_tick_and_pitch_chart = new short[duration_in_ticks][candidate_midi_pitches];

		// Go through all MIDI events in all MIDI tracks, searching for Note On events. When a Note On is 
		// found, fill in the pitch_strength_by_tick_chart based on it. Channel 10 (non-pitched) Note Ons are
		// excluded, as are velocity 0 Note Ons (i.e. effective Note Offs).
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if ( short_message.getChannel() != 10 - 1 && // is not on Channel 10
						 short_message.getCommand() == 0x90 && // is a Note On message
						 short_message.getData2() != 0 ) // does not have velocity 0
					{
						total_vertical_unison_velocity = lookAheadForNoteOffAndUpdate( 
								track,
								n_event,
								(int) event.getTick(),
								short_message.getChannel(),
								short_message.getData1(),
								short_message.getData2(),
								number_notes_sounding_by_tick_and_pitch_chart,
								pitch_strength_by_tick_chart,
								total_vertical_unison_velocity);
					}
				}
			}
		}
	}
	
	
	/**
	 * Calculate the values of the pitches_present_by_tick_excluding_rests and 
	 * pitches_classes_present_by_tick_excluding_rests fields.
	 */
	private void generatePitchesAndPitchClassesPresentByTickExcludingRests()
	{
		// An ArrayList version of pitches_present_by_tick_excluding_rests
		ArrayList<short[]> pitches_present_by_tick_excluding_rests_arli = new ArrayList<>();
		
		// An ArrayList version of pitch_classes_present_by_tick_excluding_rests
		ArrayList<short[]> pitch_classes_present_by_tick_excluding_rests_arli = new ArrayList<>();
		
		// Fill in number_pitch_classes_by_tick tick by tick 
		for (int tick = 0; tick < pitch_strength_by_tick_chart.length; tick++)
		{
			// Find the MIDI pitch numbers of all pitches found this tick
			ArrayList<Short> pitches_this_tick = new ArrayList<>();
			for (int pitch = 0; pitch < pitch_strength_by_tick_chart[tick].length - 1; pitch++)
				if (pitch_strength_by_tick_chart[tick][pitch] != 0)
					pitches_this_tick.add((short) pitch);

			// If not a rest
			if (!pitches_this_tick.isEmpty())
			{
				// Store pitches present this tick
				short[] these_pitches = new short[pitches_this_tick.size()];
				for (int i = 0; i < these_pitches.length; i++)
					these_pitches[i] = pitches_this_tick.get(i);
				pitches_present_by_tick_excluding_rests_arli.add(these_pitches);
				
				// Store pitch classes present this tick
				ArrayList<Short> pitch_classes_this_tick = new ArrayList<>();
				for (int i = 0; i < these_pitches.length; i++)
				{
					short pitch_class = (short) (these_pitches[i] % 12);
					if (i == 0)
						pitch_classes_this_tick.add(pitch_class);
					else
					{
						boolean repeated_pitch_class = false;
						for (int j = 0; j < pitch_classes_this_tick.size(); j++)
						{
							if (pitch_classes_this_tick.get(j) == pitch_class)
							{
								repeated_pitch_class = true;
								break;
							}
						}
						if (!repeated_pitch_class)
							pitch_classes_this_tick.add(pitch_class);
					}
				}
				short[] these_pitch_classes = new short[pitch_classes_this_tick.size()];
				for (int i = 0; i < these_pitch_classes.length; i++)
					these_pitch_classes[i] = pitch_classes_this_tick.get(i);
				pitch_classes_present_by_tick_excluding_rests_arli.add(these_pitch_classes);
			}
		}

		// Convert ArrayLists to arrays
		pitches_present_by_tick_excluding_rests = pitches_present_by_tick_excluding_rests_arli.toArray(new short[pitches_present_by_tick_excluding_rests_arli.size()][]);
		pitch_classes_present_by_tick_excluding_rests = pitch_classes_present_by_tick_excluding_rests_arli.toArray(new short[pitch_classes_present_by_tick_excluding_rests_arli.size()][]);
	}


	/**
	 * Calculate the values of the note_loudnesses field.
	 */
	private void generateNoteLoudnesses()
	{
		// Instantiate note_loudnesses
		note_loudnesses = new int[16][];
		for (int i = 0; i < note_loudnesses.length; i++)
			note_loudnesses[i] = new int[channel_statistics[i][0]];

		// Keep track of how many notes have occured on each channel
		int[] notes_so_far = new int[16];
		for (int i = 0; i < notes_so_far.length; i++)
			notes_so_far[i] = 0;
		
		// Fill in note_loudnesses
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			for (int n_event = 0; n_event < tracks[n_track].size(); n_event++)
			{
				MidiEvent event = tracks[n_track].get(n_event);
				if (event.getMessage() instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) event.getMessage();
					if (short_message.getCommand() == 0x90) // Note on
					{
						if (short_message.getData2() != 0) // Not velocity 0
						{
							int channel = short_message.getChannel();
							int tick = (int) event.getTick();
							note_loudnesses[channel][notes_so_far[channel]] = (int) (((double) short_message.getData2()) * volume_of_channels_tick_map[tick][channel]);
							notes_so_far[channel]++;
						}
					}
				}
			}
		}
	}

	
	/* PRIVATE STATIC METHODS *******************************************************************************/


	/**
	 * Given information for a MIDI Note On event, find the corresponding Note Off event, and update the
	 * number_notes_sounding_by_tick_and_pitch_chart and pitch_strength_by_tick_chart_to_fill variables
	 * correspondingly. Also return an updated version of total_vertical_unison_velocity_so_far.
	 *
	 * @param midi_track									The track the Note On occurred on.
	 * @param note_on_event_index							The event index of the Note On event.
	 * @param note_on_tick									The MIDI tick the Note On occurred at.
	 * @param note_on_channel								The MIDI channel the Note On occurred on.
	 * @param note_on_pitch									The MIDI pitch of the Note On.
	 * @param note_on_velocity								The MIDI velocity of the Not On.
	 * @param number_notes_sounding_by_tick_and_pitch_chart	A chart indicating the number of notes sounding at
	 *														each pitch during each tick. The first index
	 *														indicates the MIDI tick and the second index
	 *														indicates the MIDI pitch (this is always set to
	 *														size 128). Each entry indicates the number of 
	 *														notes sounding.
	 * @param pitch_strength_by_tick_chart_to_fill			A chart indicating what pitches are sounding
	 *														during each tick. The first index indicates MIDI
	 *														tick and the second indicates MIDI pitch (this is
	 *														always set to size 128). Each entry indicates the
	 *														cumulative velocity of all notes (not including
	 *														Channel 10 non-pitched percussion instruments)
	 *														sounding at that tick on that pitch.
	 * @param total_vertical_unison_velocity_so_far			Total velocity (so far) of all notes involved in a
	 *														vertical unison.
	 * @return												The value of total_vertical_unison_velocity_so_far
	 *														after updating to account for this Note On.
	 */
	private static int lookAheadForNoteOffAndUpdate( Track midi_track,
													 int note_on_event_index,
													 int note_on_tick,
													 int note_on_channel,
													 int note_on_pitch,
													 int note_on_velocity,
													 short[][] number_notes_sounding_by_tick_and_pitch_chart,
													 short[][] pitch_strength_by_tick_chart_to_fill,
													 int total_vertical_unison_velocity_so_far)
	{
		for (int n_event = note_on_event_index; n_event < midi_track.size(); n_event++)
		{
			MidiEvent event = midi_track.get(n_event);
			MidiMessage message = event.getMessage();
			if (message instanceof ShortMessage)
			{
				// Is this a Note Off message (or equivalent Note On with velocity 0)?
				ShortMessage short_message = (ShortMessage) message;
				if (     short_message.getCommand() == 0x80
				     || (short_message.getCommand() == 0x90 && short_message.getData2() == 0))
				{
					// Is this Note Off message on the same channel, and does it have the same pitch as the
					// Note On message under consideration?
					if (short_message.getChannel() == note_on_channel && short_message.getData1() == note_on_pitch)
					{
						// Fill in pitch_strength_by_tick_chart_to_fill now that the start and end ticks are
						// known for this note
						int note_off_tick = (int) event.getTick();
						for (int tick = note_on_tick; tick < note_off_tick; tick++)
						{
							// Update total_vertical_unison_velocity_so_far
							if (number_notes_sounding_by_tick_and_pitch_chart[tick][note_on_pitch] > 0)
							{
								int number_previously_detected_notes_at_this_pitch = number_notes_sounding_by_tick_and_pitch_chart[tick][note_on_pitch];
								if (number_previously_detected_notes_at_this_pitch == 1)
									total_vertical_unison_velocity_so_far += pitch_strength_by_tick_chart_to_fill[tick][note_on_pitch] + note_on_velocity;
								else if (number_previously_detected_notes_at_this_pitch > 1)
									total_vertical_unison_velocity_so_far += note_on_velocity;
							}

							// Update number_notes_sounding_by_tick_and_pitch_chart
							number_notes_sounding_by_tick_and_pitch_chart[tick][note_on_pitch]++;

							// Update pitch_strength_by_tick_chart_to_fill
							pitch_strength_by_tick_chart_to_fill[tick][note_on_pitch] += note_on_velocity;
						}

						// Stop looping through MIDI events, since the appropriate Note Off has been
						// found, and processing has proceded accordingly.
						break;
					}
				}
			}
		}

		// Return the given total_vertical_unison_velocity_so_far, updated to include the information
		// associated with the Note On that this method was called for
		return total_vertical_unison_velocity_so_far;
	}
	
	
	/**
	 * Return the index of the highest value in the given data.
	 *
	 * @param	data	The data that is being searched.
	 * @return			The index of data that corresponds to the entry of data with the highest value.
	 */
	private static int getIndexOfHighest(double[] data)
	{
		int highest_index_so_far = 0;
		double highest_so_far = data[0];
		for (int i = 0; i < data.length; i++)
		{
			if (data[i] > highest_so_far)
			{
				highest_so_far = data[i];
				highest_index_so_far = i;
			}
		}
		return highest_index_so_far;
	}

	
	/**
	 * Finds the number of MIDI ticks corresponding to the duration of a single beat at the given tempo in
	 * beats per minute (assuming the specified average tempo in ticks per second).
	 *
	 * @param	bpm					The tempo to convert, in beats per minute.
	 * @param	ticks_per_second	The number of MIDI ticks in one second.
	 * @return						The number of MIDI ticks in one beat at the given bpm tempo.
	 */
	private static int convertBPMtoTicks(int bpm, double ticks_per_second)
	{
		return (int) ((ticks_per_second * 60) / bpm);
	}
	
	
	/**
	 * Perform an autocorrelation calculation as follows on the specified data with the specified lag:
	 *
	 * y[lag] = (1/N) SUM(n to N){ x[n] * x[n-lag] }
	 *
	 * @param	data	The data to be correlated.
	 * @param	lag		The lag for each data point.
	 * @return			The resultant auto correlation.
	 */
	private static double autoCorrelate(int[] data, int lag)
	{
		double result = 0.0;
		for (int i = lag; i < data.length; i++)
			result += (double) (data[i] * data[i - lag]);
		return result / (double) data.length; // divide by N
	}
}