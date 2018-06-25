package jsymbolic2.processing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.sound.midi.*;
import jsymbolic2.featureutils.CollectedNoteInfo;
import jsymbolic2.featureutils.NoteInfo;
import mckay.utilities.staticlibraries.ArrayMethods;
import mckay.utilities.staticlibraries.MathAndStatsMethods;

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
	 * encountered tempo metamessage. Any further tempo metamessages are ignored. Set to the MIDI default of
	 * 120 BPM if no tempo metamessage is present.</li>
	 * </ul>
	 */
	public Object[] overall_metadata;

	/**
	 * The total duration in seconds of the sequence (as an integer).
	 */
	public int sequence_duration;

	/**
	 * The total duration in seconds of the sequence (as a double).
	 */
	public double sequence_duration_precise;
	
	/**
	 * The average duration in seconds of a MIDI tick. This is averaged across the sequence, since the 
	 * moment-to-moment duration of a tick can vary due to tempo change messages.
	 */
	public double average_tick_duration;

	/**
	 * An array with an entry for each MIDI tick. The value at each entry specifies the duration of that 
	 * particular MIDI tick in seconds.
	 */
	public double[] duration_of_ticks_in_seconds;
	
	/**
	 * A table with rows (first index) corresponding to MIDI ticks and columns (second index) corresponding to
	 * MIDI channels. Each entry's value is set to the channel volume at that tick, as set by channel volume
	 * controller messages divided by 127. The default is set to 1.0, which corresponds to a controller value 
	 * of 127. IMPORTANT: only coarse channel volume (CC 7) messages are considered, not expression (CC 11) or
	 * fine channel volume (CC 39) messages are considered.
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
	 * The duration in seconds of a quarter note. The average value for the piece as a whole (since tempo
	 * changes can cause it to vary.
	 */
	public double average_quarter_note_duration_in_seconds;
	
	/**
	 * A normalized histogram where the value of each bin specifies the fraction of all notes in the piece
	 * with a rhythmic value corresponding to that of the given bin. The bins are numbered as follows: 
	 * 
	 *		0: thirty second notes (or less)
	 *		1: sixteenth notes
	 *		2: eighth notes
	 *		3: dotted eighth notes
	 *		4: quarter notes
	 *		5: dotted quarter notes
	 *		6: half notes
	 *		7: dotted half notes
	 *		8: whole notes
	 *		9: dotted whole notes
	 *		10: double whole notes
	 *		11: dotted double whole notes (or more)
	 * 
	 * Both pitched and unpitched notes are included in this histogram. Tempo is, of course, not relevant to
	 * this feature. Notes with durations not precisely matching one of these rhythmic note values are mapped
	 * to the closest note value (to filter out the effects of rubato or uneven human rhythmic performances, 
	 * for example). This histogram is calculated without regard to the dynamics, voice or instrument of any 
	 * given note.
	 */
	public double[] rhythmic_value_histogram;

	/**
	 * Each dimension of this array is associated with a different rhythmic value (using the same numbering
	 * scheme as the rhythmic_value_histogram). Each value of this array contains a list. There is an entry in
	 * each list for each time a note with the list's associated rhythmic value is encountered in a context
	 * where its rhythmic value differs from the preceding note's rhythmic value on the same channel and
	 * track. Each such value indicates the number of notes of this same rhythmic value that occurred on the
	 * same channel and track before a new rhythmic value was encountered on that channel and track.
	 */
	public LinkedList<Integer>[] runs_of_same_rhythmic_value;
	
	/**
	 * An array with one entry for each note, where the value of each entry indicates the quantized duration
	 * of the note in quarter notes (e.g. a value of 0.5 corresponds to a duration of an eighth note). The
	 * order of the durations in this array is completely unrelated to the order in which the corresponding
	 * notes each occur. Reported rhythmic values are after quantization as done for the
	 * rhythmic_value_histogram. Both pitched and unpitched notes are included. This is calculated without
	 * regard to the dynamics, voice or instrument of any given note.
	 */
	public double[] rhythmic_value_of_each_note_in_quarter_notes;
	
	/**
	 * The offset in duration of each note from the exact idealized duration of its nearest rhythmic value,
	 * expressed as a fraction of the duration of an idealized quarter note. This is an absolute value, so
	 * offsets that are longer or shorter than each idealized duration are identical (they are both expressed
	 * here as positive numbers). For example, a value of 0.1 for a note that happens to be a whole note would
	 * indicate that the associated note has a duration that is one tenth the duration of a quarter note
	 * shorter or longer than the idealized duration of a whole note. The order of the durations in this array
	 * is completely unrelated to the order in which the corresponding notes each occur. Both pitched and
	 * unpitched notes are included. This is calculated without regard to the dynamics, voice or instrument of
	 * any given note.
	 */
	public double[] rhythmic_value_offsets;
	
	/**
	 * A normalized histogram, with bins corresponding to rhythmic periodicities measured in beats per minute.
	 * The magnitude of each bin is proportional to the aggregated loudnesses of the notes that occur at the
	 * bin's rhythmic periodicity, and calculation is done using autocorrelation. All bins below 40 BPM are
	 * set to 0 because autocorrelation was not performed at these lags (because the results are too noisy in
	 * this range). Bins only go up to 200 BPM. Calculations use the overall average tempo of the piece, in
	 * order to emphasize the metrical notation of the recording, and do thus do not take into account tempo
	 * variations in the piece.
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
	 * A normalized histogram, with bins corresponding to rhythmic periodicities measured in beats per minute.
	 * This is similar to the beat_histogram field, except that music is effectively transformed before this
	 * feature is calculated to have a tempo of 120 beats per minute, rather than the tempo its actual tempo
	 * setting indicates that it should have. Any variations in tempo are ignored; for the purposes of this
	 * feature the tempo is consistently 120 BPM throughout the music. The magnitude of each bin is
	 * proportional to the aggregated loudnesses of the notes that occur at the bin's (virtual) rhythmic periodicity,
	 * and calculation is done using autocorrelation. All bins below the (virtual) 40 BPM periodicity (relative
	 * to the virtual 120 BPM tempo) are set to 0 because autocorrelation was not performed at these lags
	 * (because the results are too noisy in this range). Bins only go up to a (virtual) 200 BPM.
	 */
	public double[] beat_histogram_120_bpm_standardized;
	
	/**
	 * Table with rows (first index) corresponding to the bins of the beat_histogram_120_bpm_standardized
	 * (i.e. rhythmic periodicities measured in beats per minute, after a tempo transformation to 120 BPM
	 * consistently throughout the music). Entries are set to the magnitude of the corresponding bin of
	 * beat_histogram_120_bpm_standardized if the magnitude in that bin of beat_histogram_120_bpm_standardized
	 * is high enough to meet this beat_histogram_thresholded_table_120_bpm_standardized column's threshold
	 * requirements, and to 0 otherwise. Column 0 has a threshold that only allows
	 * beat_histogram_120_bpm_standardized bins with a normalized frequency over 0.1 to be counted, column 1
	 * has a threshold of higher than 0.01, and column 2 only counts beat_histogram_120_bpm_standardized bins
	 * that have a normalized frequency at least 30% as high as the normalized frequency of the highest
	 * beat_histogram bin. This table is then processed so that only peaks are included, which is to say that
	 * entries of beat_histogram_thresholded_table_120_bpm_standardized that are adjacent to
	 * beat_histogram_thresholded_table_120_bpm_standardized bins with higher magnitudes are set to 0.
	 */
	public double[][] beat_histogram_thresholded_table_120_bpm_standardized;	
	
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
	 * increase by semitone from there. IMPORTANT: The PitchClassHistogramFeature reorders these pitch classes
	 * to start with the most common pitch class at Index 0, but this reordering is NOT performed in this 
	 * field.
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
	 * that the order of the Note Ons may not necessarily be in the temporal order that they occur. Note that
	 * this only takes the second (more significant) pitch bend byte into account.
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
	 * channel. Warning: This could suffer from rounding error. Use total_time_notes_sounding_per_channel if
	 * more precision is needed.</li>
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
	 * The number of MIDI channels that contain at least one note. This DOES include MIDI Channel 10.
	 */
	public double number_of_active_voices;
	
	/**
	 * Total amount of time in seconds that one or more notes were sounding on the channel corresponding to
	 * the index. This data includes MIDI Channel 10, even though it is understood that notes on Channel 10
	 * are in fact unpitched percussion patches. This data combines data in all MIDI tracks.
	 */
	public double[] total_time_notes_sounding_per_channel;
	
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
	 * The MIDI pitches of all pitched note ons in the piece (unpitched notes on Channel 10 are excluded).
	 * There is one entry for every note on, but they will not necessarily be in the same temporal order as
	 * they occur in the piece. Each entry indicates the MIDI pitch of one of the sounding notes.
	 */
	public short[] pitches_of_all_note_ons;	
	
	/**
	 * The pitch classes of all pitched note ons in the piece (unpitched notes on Channel 10 are excluded).
	 * There is one entry for every note on, but they will not necessarily be in the same temporal order as
	 * they occur in the piece. Each entry indicates the pitch class (0 to 11, where 0 is C) of one of the 
	 * sounding notes.
	 */
	public short[] pitch_classes_of_all_note_ons;
	
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
	 * The pitch values are sorted from lowest to highest. Will be empty if there are no pitched notes.
	 */	
	public short[][] pitches_present_by_tick_excluding_rests;
	
	/**
	 * A data structure indicating all pitch classes (NOT including Channel 10 unpitched notes) sounding at
	 * each MIDI tick. However, all ticks during which no notes are playing are excluded from this data
	 * structure, so the tick index will most likely not correspond to the actual MIDI ticks in the MIDI
	 * stream. The first dimension indicates the MIDI tick (after removal of rest ticks) and the second
	 * dimension indicates the note index (there will be one entry for each pitch class sounding during the
	 * given MIDI tick). Each entry indicates the pitch class (0 to 11, where 0 is C) of one of the sounding
	 * notes. The pitch class values are sorted from lowest to highest. Will be empty if there are no pitched 
	 * notes.
	 */
	public short[][] pitch_classes_present_by_tick_excluding_rests;
	
	/**
	 * Fraction of movements between voices that consist of parallel motion (the fraction is calculated
	 * relative to the total amount of qualifying transitions, including all parallel, similar, contrary and
	 * oblique transitions). If more than two voices are involved in a given pitch transition, then each
	 * possible pair of voices comprising the transition is included in the calculation. Note that only
	 * transitions from one set of pitches to another set of pitches comprising the same number of pitches as
	 * the first are included in this calculation, although a brief lookahead is performed in order to
	 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
	 * performance). Only unique pitches are included in this calculation (unisons are treated as a single
	 * pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the
	 * advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that
	 * this feature does not incorporate an awareness of voice crossing.
	 */
	public double parallel_motion_fraction;
	
	/**
	 * Fraction of movements between voices that consist of similar motion (the fraction is calculated
	 * relative to the total amount of qualifying transitions, including all parallel, similar, contrary and
	 * oblique transitions). If more than two voices are involved in a given pitch transition, then each
	 * possible pair of voices comprising the transition is included in the calculation. Note that only
	 * transitions from one set of pitches to another set of pitches comprising the same number of pitches as
	 * the first are included in this calculation, although a brief lookahead is performed in order to
	 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
	 * performance). Only unique pitches are included in this calculation (unisons are treated as a single
	 * pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the
	 * advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that
	 * this feature does not incorporate an awareness of voice crossing.
	 */
	public double similar_motion_fraction;
	
	/**
	 * Fraction of movements between voices that consist of contrary motion (the fraction is calculated
	 * relative to the total amount of qualifying transitions, including all parallel, similar, contrary and
	 * oblique transitions). If more than two voices are involved in a given pitch transition, then each
	 * possible pair of voices comprising the transition is included in the calculation. Note that only
	 * transitions from one set of pitches to another set of pitches comprising the same number of pitches as
	 * the first are included in this calculation, although a brief lookahead is performed in order to
	 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
	 * performance). Only unique pitches are included in this calculation (unisons are treated as a single
	 * pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the
	 * advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that
	 * this feature does not incorporate an awareness of voice crossing.
	 */
	public double contrary_motion_fraction;
	
	/**
	 * Fraction of movements between voices that consist of oblique motion (the fraction is calculated
	 * relative to the total amount of qualifying transitions, including all parallel, similar, contrary and
	 * oblique transitions). If more than two voices are involved in a given pitch transition, then each
	 * possible pair of voices comprising the transition is included in the calculation. Note that only
	 * transitions from one set of pitches to another set of pitches comprising the same number of pitches as
	 * the first are included in this calculation, although a brief lookahead is performed in order to
	 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
	 * performance). Only unique pitches are included in this calculation (unisons are treated as a single
	 * pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the
	 * advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that
	 * this feature does not incorporate an awareness of voice crossing.
	 */
	public double oblique_motion_fraction;
	
	/**
	 * Fraction of movements between voices that consist of parallel fifths (the fraction is calculated
	 * relative to the total amount of qualifying transitions, including all parallel, similar, contrary and
	 * oblique transitions). If more than two voices are involved in a given pitch transition, then each
	 * possible pair of voices comprising the transition is included in the calculation. Note that only
	 * transitions from one set of pitches to another set of pitches comprising the same number of pitches as
	 * the first are included in this calculation, although a brief lookahead is performed in order to
	 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
	 * performance). Only unique pitches are included in this calculation (unisons are treated as a single
	 * pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the
	 * advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that
	 * this feature does not incorporate an awareness of voice crossing.
	 */
	public double parallel_fifths_fraction;
		
	/**
	 * Fraction of movements between voices that consist of parallel octaves (the fraction is calculated
	 * relative to the total amount of qualifying transitions, including all parallel, similar, contrary and
	 * oblique transitions). If more than two voices are involved in a given pitch transition, then each
	 * possible pair of voices comprising the transition is included in the calculation. Note that only
	 * transitions from one set of pitches to another set of pitches comprising the same number of pitches as
	 * the first are included in this calculation, although a brief lookahead is performed in order to
	 * accommodate small rhythmic desynchronizations (e.g. if a MIDI file is a transcription of a human
	 * performance). Only unique pitches are included in this calculation (unisons are treated as a single
	 * pitch). All pitches present are considered, regardless of their MIDI channel or track; this has the
	 * advantage of accommodating polyphonic instruments such as piano or guitar, but the consequence is that
	 * this feature does not incorporate an awareness of voice crossing.
	 */
	public double parallel_octaves_fraction;	
	
	/**
	 * A table with rows (first index) corresponding to MIDI channel number, and columns (second index)
	 * corresponding to separate notes (in the temporal order that the Note On for each note occurred). Each
	 * entry specifies the velocity of the Note On scaled by the channel volume at the time of the Note On,
	 * such that each value ranges from 0 to 127. Note that the order of the notes may not always  precisely
	 * reflect the temporal order in which they occurred (because the outer iteration is by MIDI track).
	 */
	public int[][] note_loudnesses;
	
	/**
	 * An array indicating channel-by-channel durations of rests, expressed in terms of a fraction of a
	 * quarter note's duration (e.g. a half rest would have a value of 2). The first index corresponds to
	 * channel, but not necessarily the actual channel number, since only channels that contain at least one
	 * note and at least one rest are included here. The second index refers to the index of the rest (there
	 * is one entry per rest), where rests are listed in the order in which they occur. A rest in this case is
	 * defined as a period of time during which there are no notes being held on the particular channel in
	 * question. Rests shorter than 0.1 of a quarter note are ignored (i.e. omitted from this listing). A
	 * value of null for this field as a whole indicates that there are no rests at all. Individual channel
	 * rows cannot be null.
	 *
	 * <p>NOTE: This data includes MIDI Channel 10, even though it is understood that notes on Channel 10 are
	 * in fact unpitched percussion patches.
	 */
	public double[][] rest_durations_separated_by_channel;
	
	/**
	 * An array indicating the durations of all complete rests, expressed in terms of a fraction of a quarter
	 * note's duration (e.g. a half rest would have a value of 2). A complete rest is defined as a period in
	 * which no pitched notes are sounding on any MIDI channel. Non-pitched (MIDI channel 10) notes are NOT
	 * considered in this calculation. The index indicates the index of the rest, and the rests are ordered
	 * based on the order they occur in the piece. Rests shorter than 0.1 of a quarter note are ignored (i.e.
	 * omitted from this listing). A value of null for this field indicates that there are no complete rests
	 * of sufficient duration in the piece.
	 */
	public double[] complete_rest_durations;
	

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

		generateAverageTickDuration();
		// System.out.println("Average tick duration (in seconds): " + average_tick_duration);
		// System.out.println("Ticks per beat: " + sequence.getResolution());
		
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
		
		generateRhythmicValueFeatures();
		// System.out.println("Fraction thirty second notes: " + rhythmic_value_histogram[0]);
		// System.out.println("Fraction sixteenth notes: " + rhythmic_value_histogram[1]);
		// System.out.println("Fraction eighth notes: " + rhythmic_value_histogram[2]);
		// System.out.println("Fraction dotted eighth notes: " + rhythmic_value_histogram[3]);
		// System.out.println("Fraction quarter notes: " + rhythmic_value_histogram[4]);
		// System.out.println("Fraction dotted quarter notes: " + rhythmic_value_histogram[5]);
		// System.out.println("Fraction half notes: " + rhythmic_value_histogram[6]);
		// System.out.println("Fraction dotted half notes: " + rhythmic_value_histogram[7]);
		// System.out.println("Fraction whole notes: " + rhythmic_value_histogram[8]);
		// System.out.println("Fraction dotted whole notes: " + rhythmic_value_histogram[9]);
		// System.out.println("Fraction double whole notes: " + rhythmic_value_histogram[10]);
		// System.out.println("Fraction dotted double whole notes: " + rhythmic_value_histogram[11]);
		// System.out.println("---");
		// System.out.println("Average quarter note duration (seconds): " + average_quarter_note_duration_in_seconds);
		// System.out.println("---");
		// for (int i = 0; i < runs_of_same_rhythmic_value.length; i ++)		
		//	for (int j = 0; j < runs_of_same_rhythmic_value[i].size(); j++)
		// 		System.out.println("Rhythmic Value: " + i + " Run Length: " + runs_of_same_rhythmic_value[i].get(j));
		// System.out.println("---");			
		// for (int i = 0; i < rhythmic_value_of_each_note_in_quarter_notes.length; i++)
		//	System.out.println(rhythmic_value_of_each_note_in_quarter_notes[i]);
		// System.out.println("===");
		// for (int i = 0; i < rhythmic_value_offsets.length; i++)
		//	System.out.println(rhythmic_value_offsets[i]);
		// System.out.println("===");
		
		generateBeatHistograms();
		/*for (int i = 0; i < beat_histogram.length; i++)
			System.out.println("BPM: " + i + ": " + beat_histogram[i]);*/
	
		generateBeatHistogramThresholdedTables();
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
		}
		System.out.println("Number of active voices: " + number_of_active_voices);*/
		
		generatePitchAndPitchClaessesOfAllNoteOns();
		/*
		for (int i = 0; i < pitch_classes_of_all_note_ons.length; i++)
			System.out.println("NOTE " + (i + 1) + ": Pitch " + pitches_of_all_note_ons[i] + "  Pitch Class:" + pitch_classes_of_all_note_ons[i]);
		*/
		
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
		
		generateContrapuntalCounts();
		/*System.out.println("SEQUENCE DURATION: " + sequence_duration);
		System.out.println("parallel_motion_fraction " + parallel_motion_fraction);
		System.out.println("similar_motion_fraction " + similar_motion_fraction);
		System.out.println("contrary_motion_fraction " + contrary_motion_fraction);
		System.out.println("oblique_motion_fraction " + oblique_motion_fraction);
		System.out.println("parallel_fifths_fraction " + parallel_fifths_fraction);
		System.out.println("parallel_octaves_fraction " + parallel_octaves_fraction);*/
	
		generateNoteLoudnesses();
		/*for (int i = 0; i < note_loudnesses.length; i++)
			System.out.print("\nCHAN: " + i + "  ");
			for (int j = 0; j < note_loudnesses[i].length; j++)
				System.out.print("   " + note_loudnesses[i][j]);
		System.out.println("\n");*/
		
		generateRestDurationsSeparatedByChannel();
		/*for (int i = 0; i < rest_durations_separated_by_channel.length; i++)
		{
			for (int j = 0; j < rest_durations_separated_by_channel[i].length; j++)
				System.out.print(rest_durations_separated_by_channel[i][j] + " / ");
			System.out.print("\n");
		}*/
		
		generateCompleteRestDurations();
		/*if (complete_rest_durations == null)
			System.out.print("complete_rest_durataions: null");
		else
		{
			System.out.print("complete_rest_durataions: ");
			for (int i = 0; i < complete_rest_durations.length; i++)
				System.out.print(complete_rest_durations[i] + " / ");
		}
		System.out.print("\n");*/
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
		if (sequence_info.total_number_note_ons == 0)
			return 0.0;
		else
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
		overall_metadata[0] = 0; // major or minor
		overall_metadata[1] = new LinkedList(); // time signature numerators
		overall_metadata[2] = new LinkedList(); // time signature denominators
		overall_metadata[3] = 120; // default MIDI tempo

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
							if (ms_tempo_float <= 0) ms_tempo_float = 0.1f;
							int bpm = Math.round(60000000.0f / ms_tempo_float);
							overall_metadata[3] = bpm;

							// Note that the tempo has been found
							tempo_found = true;
						}
					}
				}
			}
		}
		
		// Default tihe time signature to 4 / 4 if no time signature is
		if (((LinkedList) overall_metadata[1]).isEmpty())
		{
			((LinkedList) overall_metadata[1]).add(4);
			((LinkedList) overall_metadata[2]).add(4);
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
	 * Calculate the value of the average_tick_duration field.
	 */
	private void generateAverageTickDuration()
	{
		average_tick_duration = sequence_duration_precise / ((double) sequence.getTickLength());
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

						// System.out.println("Tick: " + event.getTick() + "  Current: " + current_seconds_per_tick  + "   Average: " + (1.0 / mean_ticks_per_second));
						
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

								// Look ahead to find the corresponding note off for this note on. Defaults
								// to the last tick if no corresponding note off is found.
								int event_start_tick = (int) event.getTick();
								int event_end_tick = findCorrespondingNoteOffEndTick(short_message, n_event, track);

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
	 * Calculate the values of the average_quarter_note_duration_in_seconds, rhythmic_value_histogram,
	 * runs_of_same_rhythmic_value, rhythmic_value_of_each_note_in_quarter_notes and rhythmic_value_offsets
	 * fields.
	 */
	private void generateRhythmicValueFeatures()
	{
		// The number of ticks per quarter note for the entire sequence
		int ppqn_ticks_per_beat = sequence.getResolution();
		
		// The number of ticks corresponding to each note value
		int ticks_per_thirty_second_note = ppqn_ticks_per_beat / 8;
		int ticks_per_sixteenth_note = ppqn_ticks_per_beat / 4;
		int ticks_per_eighth_note = ppqn_ticks_per_beat / 2;
		int ticks_per_dotted_eighth_note = ppqn_ticks_per_beat * 3 / 4;
		int ticks_per_quarter_note = ppqn_ticks_per_beat;
		int ticks_per_dotted_quarter_note = ppqn_ticks_per_beat * 3 / 2;
		int ticks_per_half_note = ppqn_ticks_per_beat * 2;
		int ticks_per_dotted_half_note = ppqn_ticks_per_beat * 3;
		int ticks_per_whole_note = ppqn_ticks_per_beat * 4;
		int ticks_per_dotted_whole_note = ppqn_ticks_per_beat * 6;
		int ticks_per_double_whole_note = ppqn_ticks_per_beat * 8;
		int ticks_per_dotted_double_whole_note = ppqn_ticks_per_beat * 12;
	
		// The average duration in seconds of a quarter note 
		average_quarter_note_duration_in_seconds = (double) ticks_per_quarter_note * average_tick_duration;
		
		// The number of ticks corresponding to each note value in the form of an array
		int central_ticks_per_note_value[] = new int[] { ticks_per_thirty_second_note, // i=0
														 ticks_per_sixteenth_note, // i=1
														 ticks_per_eighth_note, // i=2
														 ticks_per_dotted_eighth_note, // i=3
														 ticks_per_quarter_note, // i=4
														 ticks_per_dotted_quarter_note, // i=5
														 ticks_per_half_note, // i=6
														 ticks_per_dotted_half_note, // i=7
														 ticks_per_whole_note, // i=8
														 ticks_per_dotted_whole_note, // i=9
														 ticks_per_double_whole_note, // i=10
														 ticks_per_dotted_double_whole_note }; // i=11
		
		// The lowest number of ticks that a note of the given value can have
		int lower_bound_ticks_per_note_value[] = new int[central_ticks_per_note_value.length];
		lower_bound_ticks_per_note_value[0] = 0;
		for (int i = 1; i < lower_bound_ticks_per_note_value.length; i++)
			lower_bound_ticks_per_note_value[i] = central_ticks_per_note_value[i-1] + ((central_ticks_per_note_value[i] - central_ticks_per_note_value[i-1]) / 2);
		// for (int i = 0; i < lower_bound_ticks_per_note_value.length; i++)
		//	System.out.println("-- " + i + " " + ppqn_ticks_per_beat + " " + central_ticks_per_note_value[i] + " " + lower_bound_ticks_per_note_value[i]);
		
		// The number of notes corresponding to each note value
		int[] rhythmic_duration_note_counts = new int[lower_bound_ticks_per_note_value.length]; 
		
		// Initialize runs_of_same_rhythmic_value
		runs_of_same_rhythmic_value = new LinkedList[central_ticks_per_note_value.length]; 
		for (int i = 0; i < runs_of_same_rhythmic_value.length; i++)
			runs_of_same_rhythmic_value[i] = new LinkedList<>();

		// The offset in duration of each note from the exact idealized duration of its nearest rhythmic
		// value, expressed as a fraction of the duration of an idealized quarter note
		LinkedList<Double> quantization_offsets_in_quarter_note_fractions = new LinkedList<>();
		
		// Fill rhythmic_duration_note_counts for the entire sequence
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			// The rhythmic value of each note on this track, divided by channel, and in the order they occur
			// on that channel
			LinkedList<Integer>[] ordered_rhythmic_values_by_channel = new LinkedList[16]; 
			for (int i = 0; i < ordered_rhythmic_values_by_channel.length; i++)
				ordered_rhythmic_values_by_channel[i] = new LinkedList<>();

			// Process this track
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
							// Look ahead to find the corresponding note off for this note on. Defaults
							// to the last tick if no corresponding note off is found.
							int event_start_tick = (int) event.getTick();
							int event_end_tick = findCorrespondingNoteOffEndTick(short_message, n_event, track);
							
							// Calculate duration in ticks of the note
							int duration_in_ticks = event_end_tick - event_start_tick;
							
							// System.out.println("NOTE FOUND: lasts " + duration_in_ticks + " ticks at " + ppqn_ticks_per_beat + " ticks per beat");
	
							// Map each note to its appropriate rhythmic value
							for (int i = 0; i < rhythmic_duration_note_counts.length; i++)
							{
								if (i == rhythmic_duration_note_counts.length - 1)
								{
									// Fill in rhythmic_duration_note_counts
									rhythmic_duration_note_counts[i]++;
									// System.out.println("\tSet to rhythmic_duration_note_counts entry " + i);
									
									// Fill in ordered_rhythmic_values_by_channel
									ordered_rhythmic_values_by_channel[short_message.getChannel()].add(i);
									
									// Fill in quantization_offsets_in_quarter_note_fractions
									double quantization_offset_in_ticks = (double) Math.abs(duration_in_ticks - central_ticks_per_note_value[i]);
									double quantization_offset_in_quarter_note_fractions = quantization_offset_in_ticks / (double) central_ticks_per_note_value[4];
									// System.out.println("\t\tOffset: " + quantization_offset_in_ticks + " ticks " + quantization_offset_in_quarter_note_fractions + " quarter note fractions");
									quantization_offsets_in_quarter_note_fractions.add(quantization_offset_in_quarter_note_fractions);
								}
								else if (duration_in_ticks < lower_bound_ticks_per_note_value[i+1])
								{
									// Fill in rhythmic_duration_note_counts
									rhythmic_duration_note_counts[i]++;
									// System.out.println("\tSet to rhythmic_duration_note_counts entry " + i);
									
									// Fill in ordered_rhythmic_values_by_channel
									ordered_rhythmic_values_by_channel[short_message.getChannel()].add(i);
									
									// Fill in quantization_offsets_in_quarter_note_fractions
									double quantization_offset_in_ticks = (double) Math.abs(duration_in_ticks - central_ticks_per_note_value[i]);
									double quantization_offset_in_quarter_note_fractions = quantization_offset_in_ticks / (double) central_ticks_per_note_value[4];
									// System.out.println("\t\tOffset: " + quantization_offset_in_ticks + " ticks " + quantization_offset_in_quarter_note_fractions + " quarter note fractions");
									quantization_offsets_in_quarter_note_fractions.add(quantization_offset_in_quarter_note_fractions);
									
									// Exit the loop
									break;
								}
							}
						}
					}
				}
			}
			
			// Update runs_of_same_rhythmic_value for this track
			for (int chan = 0; chan < ordered_rhythmic_values_by_channel.length; chan++)
			{
				int current_run_length = 0;
				int last_rhythmic_value = -1;
				for (int note = 0; note < ordered_rhythmic_values_by_channel[chan].size(); note++)
				{
					int this_rhythmic_value = ordered_rhythmic_values_by_channel[chan].get(note);
					// System.out.println("Track: " + n_track + " Channel : " + chan + " Rhythmic Value: " + this_rhythmic_value);
					if (last_rhythmic_value == -1)
					{
						last_rhythmic_value = this_rhythmic_value;
						current_run_length = 1;
					}
					else if (this_rhythmic_value == last_rhythmic_value)
						current_run_length++;
					else
					{
						runs_of_same_rhythmic_value[last_rhythmic_value].add(current_run_length);
						// System.out.println("\t1) ADD Rhythmic Value: " + last_rhythmic_value + " Run Length: " + current_run_length);
						last_rhythmic_value = this_rhythmic_value;
						current_run_length = 1;
					}
				}
				if (last_rhythmic_value != -1)
				{
					runs_of_same_rhythmic_value[last_rhythmic_value].add(current_run_length);
					// System.out.println("\t2) ADD Rhythmic Value: " + last_rhythmic_value + " Run Length: " + current_run_length);
				}
			}
		}

		// Debugging code
		// System.out.println("Number thirty second notes: " + rhythmic_duration_note_counts[0]);
		// System.out.println("Number sixteenth notes: " + rhythmic_duration_note_counts[1]);
		// System.out.println("Number eighth notes: " + rhythmic_duration_note_counts[2]);
		// System.out.println("Number dotted eighth notes: " + rhythmic_duration_note_counts[3]);
		// System.out.println("Number quarter notes: " + rhythmic_duration_note_counts[4]);
		// System.out.println("Number dotted quarter notes: " + rhythmic_duration_note_counts[5]);
		// System.out.println("Number half notes: " + rhythmic_duration_note_counts[6]);
		// System.out.println("Number dotted half notes: " + rhythmic_duration_note_counts[7]);
		// System.out.println("Number whole notes: " + rhythmic_duration_note_counts[8]);
		// System.out.println("Number dotted whole notes: " + rhythmic_duration_note_counts[9]);
		// System.out.println("Number double whole notes: " + rhythmic_duration_note_counts[10]);
		// System.out.println("Number dotted double whole notes: " + rhythmic_duration_note_counts[11]);

		// Calculate the total number of notes
		int total_notes = 0;
		for (int i = 0; i < rhythmic_duration_note_counts.length; i++)
			total_notes += rhythmic_duration_note_counts[i];
		
		// Calculate the normalized fraction of notes corresponding to each rhythmic value
		rhythmic_value_histogram = new double[rhythmic_duration_note_counts.length];
		for (int i = 0; i < rhythmic_value_histogram.length; i++)
		{
			if (total_notes == 0)
				rhythmic_value_histogram[i] = 0.0;
			else rhythmic_value_histogram[i] = ((double) rhythmic_duration_note_counts[i]) / (double) total_notes;
		}		
		
		// Construct an array with one entry for each note, where the value indicates the quantized duration
		// of the note in quarter notes (e.g. 0.5 dorresponds to a duration of an eighth note).
		rhythmic_value_of_each_note_in_quarter_notes = new double[total_notes];
		int rvofeniqn_index = 0;
		for (int i = 0; i < rhythmic_duration_note_counts.length; i++)
		{
			double quarter_note_fraction = 0.0;
			switch (i)
			{
				case 0: quarter_note_fraction = 1.0 / 8.0; break;
				case 1: quarter_note_fraction = 1.0 / 4.0; break;
				case 2: quarter_note_fraction = 1.0 / 2.0; break;
				case 3: quarter_note_fraction = 1.0 * 3.0 / 4.0; break;
				case 4: quarter_note_fraction = 1.0; break;
				case 5: quarter_note_fraction = 1.0 * 1.5; break;
				case 6: quarter_note_fraction = 1.0 * 2.0; break;
				case 7: quarter_note_fraction = 1.0 * 3.0; break;
				case 8: quarter_note_fraction = 1.0 * 4.0; break;
				case 9: quarter_note_fraction = 1.0 * 6.0; break;
				case 10:quarter_note_fraction = 1.0 * 8.0; break;
				case 11: quarter_note_fraction = 1.0 * 12.0; break;
			}
			
			for (int j = 0; j < rhythmic_duration_note_counts[i]; j++)
			{
				rhythmic_value_of_each_note_in_quarter_notes[rvofeniqn_index] = quarter_note_fraction;
				rvofeniqn_index++;
			}
		}
		
		// Prepare rhythmic_value_offsets
		rhythmic_value_offsets = new double[quantization_offsets_in_quarter_note_fractions.size()];
		for (int i = 0; i < rhythmic_value_offsets.length; i++)
			rhythmic_value_offsets[i] = quantization_offsets_in_quarter_note_fractions.get(i);
	}
		

	/**
	 * Calculate the values of the beat_histogram and beat_histogram_120_bpm_standardized fields.
	 */
	private void generateBeatHistograms()
	{
		// Set the minimum and maximum periodicities that will be used in the autocorrelation
		int min_BPM = 40;
		int max_BPM = 200;

		// Instantiate beat_histogram and initialize entries to 0
		beat_histogram = new double[max_BPM + 1];
		beat_histogram_120_bpm_standardized = new double[max_BPM + 1];
		for (int i = 0; i < beat_histogram.length; i++)
		{
			beat_histogram[i] = 0.0;
			beat_histogram_120_bpm_standardized[i] = 0.0;
		}
		
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
		
		// Histogram based on tick interval bins (standardized to 120 BPM)
		int ticks_per_beat = sequence.getResolution();
		int ticks_per_second_at_120_bpm = ticks_per_beat * 2; 
		double[] tick_histogram_120_bpm_standardized = new double[convertBPMtoTicks(min_BPM - 1, ticks_per_second_at_120_bpm)];
		for (int lag = convertBPMtoTicks(max_BPM, ticks_per_second_at_120_bpm); lag < tick_histogram_120_bpm_standardized.length; lag++)
			tick_histogram_120_bpm_standardized[lag] = autoCorrelate(rhythm_score, lag);				
		
		// Histograms with tick intervals collected into beats per minute bins
		for (int bin = min_BPM; bin <= max_BPM; bin++)
		{
			beat_histogram[bin] = 0.0;
			for (int tick = convertBPMtoTicks(bin, mean_ticks_per_second); tick < convertBPMtoTicks(bin - 1, mean_ticks_per_second); tick++)
				beat_histogram[bin] += tick_histogram[tick];
			
			beat_histogram_120_bpm_standardized[bin] = 0.0;
			for (int tick = convertBPMtoTicks(bin, ticks_per_second_at_120_bpm); tick < convertBPMtoTicks(bin - 1, ticks_per_second_at_120_bpm); tick++)
				beat_histogram_120_bpm_standardized[bin] += tick_histogram_120_bpm_standardized[tick];
		}

		// Normalize beat_histogram and beat_histogram_120_bpm_standardized
		beat_histogram = MathAndStatsMethods.normalize(beat_histogram);
		beat_histogram_120_bpm_standardized = MathAndStatsMethods.normalize(beat_histogram_120_bpm_standardized);
	}


	/**
	 * Calculate the contents of the beat_histogram_thresholded_table and
	 * beat_histogram_thresholded_table_120_bpm_standardized field fields.
	 */
	private void generateBeatHistogramThresholdedTables()
	{
		beat_histogram_thresholded_table = MathAndStatsMethods.calculateTablesOfThresholdedPeaks(beat_histogram);
		beat_histogram_thresholded_table_120_bpm_standardized = MathAndStatsMethods.calculateTablesOfThresholdedPeaks(beat_histogram_120_bpm_standardized);
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
							// Look ahead to find the corresponding note off for this note on. Defaults
							// to the last tick if no corresponding note off is found.
							int event_start_tick = (int) event.getTick();
							int event_end_tick = findCorrespondingNoteOffEndTick(short_message, n_event, track);
							
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
		basic_pitch_histogram = MathAndStatsMethods.normalize(basic_pitch_histogram);

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
			// Prepare melodic_intervals_by_channel for this track, and add it to
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
		melodic_interval_histogram = MathAndStatsMethods.normalize(melodic_interval_histogram);
	}


	/**
	 * Calculate the values of the channel_statistics, number_of_active_voices,
	 * total_time_notes_sounding_per_channel, list_of_note_on_pitches_by_channel and 
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
							int end_tick = (int) this_track.ticks(); // When the Note Off occurs, defaulted to the last tick
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
												break;
											}
										}
										if (end_message.getCommand() == 0x90) // Note On (with velocity 0 is equivalent to Note Off)
										{
											if (end_message.getData2() == 0) // Velocity 0
											{
												if (end_message.getData1() == on_pitch) //Pitch must match
												{
													end_tick = (int) end_event.getTick();
													break;
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
		total_time_notes_sounding_per_channel = new double[channel_statistics.length];
		for (int ch = 0; ch < total_time_notes_sounding_per_channel.length; ch++)
			total_time_notes_sounding_per_channel[ch] = 0.0;
		for (int ti = 0; ti < note_sounding_on_a_channel_tick_map.length; ti++)
			for (int ch = 0; ch < note_sounding_on_a_channel_tick_map[ti].length; ch++)
				if (note_sounding_on_a_channel_tick_map[ti][ch])
					total_time_notes_sounding_per_channel[ch] += duration_of_ticks_in_seconds[ti];
		for (int ch = 0; ch < channel_statistics.length; ch++)
			channel_statistics[ch][1] = (int) total_time_notes_sounding_per_channel[ch];

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
		
		// Calculate number_of_active_voices
		int num_of_active_voices = 0;
		for (int chan = 0; chan < channel_statistics.length; chan++)
			if (channel_statistics[chan][0] != 0)
				num_of_active_voices++;
		number_of_active_voices = (double) num_of_active_voices;
	}

	
	/**
	 * Calculate the value of the pitch_classes_of_all_note_ons field.
	 */
	private void generatePitchAndPitchClaessesOfAllNoteOns()
	{
		List<NoteInfo> all_notes_in_piece = all_notes.getNoteList();
		List<Short> list_of_midi_pitches_of_all_notes_in_piece = new ArrayList<>();
		for (NoteInfo this_note : all_notes_in_piece)
			if (this_note.getChannel() != 10 - 1) // Excluding Channel 10
				list_of_midi_pitches_of_all_notes_in_piece.add((short) (this_note.getPitch()));

		pitches_of_all_note_ons = new short[list_of_midi_pitches_of_all_notes_in_piece.size()];
		pitch_classes_of_all_note_ons = new short[list_of_midi_pitches_of_all_notes_in_piece.size()];
		for (int i = 0; i < pitch_classes_of_all_note_ons.length; i++)
		{
			short pitch = (short) list_of_midi_pitches_of_all_notes_in_piece.get(i);
			pitches_of_all_note_ons[i] = pitch;
			
			short pitch_class = (short) (pitch % 12);
			pitch_classes_of_all_note_ons[i] = pitch_class;
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
			for (int pitch = 0; pitch < pitch_strength_by_tick_chart[tick].length; pitch++)
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
	 * Calculate the values of the parallel_motion_fraction, similar_motion_fraction,
	 * contrary_motion_fraction, oblique_motion_fraction, parallel_fifths_fraction and
	 * parallel_octaves_fraction fields.
	 */
	private void generateContrapuntalCounts()
	{
		// The number of ticks that this method will look ahead if the number of sounding voices changes from
		// one tick to the next (this is because of rhythmic desynching that can occur between note onsets and
		// offsets. Note that this is counted in addition to ticks on which no notes are sounding (rests).
		final int TICK_DELAY_TOLERANCE = 150;
		
		// The number of times each type of movement occurs
		int parallel_count = 0;
		int similar_count = 0;
		int contrary_count = 0;
		int oblique_count = 0;
		int parallel_fifths_count = 0;
		int parallel_octaves_count = 0;
	
		// The unique sorted pitches on the last tick on which one or more pitches were sounding
		short[] previous_pitches = null;
		
		// The unique sorted pitches on the current tick (on which pitches are sounding)
		short[] current_pitches = null;
		
		// Iterate through all ticks on which one or more pitches were sounding
		for (int tick = 0; tick < pitches_present_by_tick_excluding_rests.length; tick++)
		{
			// Fill previous_pitches and move to the next tick if it has not been filled yet
			if (previous_pitches == null || previous_pitches.length == 0)
			{
				previous_pitches = pitches_present_by_tick_excluding_rests[tick];
				continue;
			}

			// Update current_pitches and previous_pitches
			if (current_pitches == null || current_pitches.length == 0)
				current_pitches = pitches_present_by_tick_excluding_rests[tick];
			else
			{
				previous_pitches = current_pitches;
				current_pitches = pitches_present_by_tick_excluding_rests[tick];
			}

			// Output current_pitches for debugging
			//System.out.print(tick + ": ");
			//for (int i = 0; i < current_pitches.length; i++)
			//	System.out.print(current_pitches[i] + " ");
			//System.out.print("\n");
			
			// Move on to the next tick if current_pitches and previous_pitches are of different sizes,
			// allowing for a look ahead of TICK_DELAY_TOLERANCE ticks (if a set of pitches of the same size
			// as previous_pitches is found in the lookahead, then we jump ahead to that tick).
			if (previous_pitches.length != current_pitches.length)
			{
				boolean move_on = true;
				if ( tick < (pitches_present_by_tick_excluding_rests.length - TICK_DELAY_TOLERANCE - 1) )
				{
					for (int look_ahead_tick = tick + 1; look_ahead_tick < (tick + TICK_DELAY_TOLERANCE); look_ahead_tick++)
					{
						if (pitches_present_by_tick_excluding_rests[look_ahead_tick].length == previous_pitches.length)
						{
							tick = look_ahead_tick;
							current_pitches = pitches_present_by_tick_excluding_rests[look_ahead_tick];
							move_on = false;
							break;
						}
					}
				}
				if (move_on) continue;
			}
			
			// Move on to the next tick if only one or no notes are sounding
			if (current_pitches.length <= 1)
				continue;
				
			// Move on to the next tick if current_pitches and previous_pitches are the same
			boolean prev_and_cur_are_identical = true;
			for (int i = 0; i < current_pitches.length; i++)
				if (previous_pitches[i] != current_pitches[i])
					prev_and_cur_are_identical = false;
			if (prev_and_cur_are_identical)
				continue;
			
			// Count the different types of motion by comparing each pair of voices
			for (int bottom_ind = 0; bottom_ind < current_pitches.length - 1; bottom_ind++)
			{
				for (int top_ind = bottom_ind + 1; top_ind < current_pitches.length; top_ind++)
				{
					// Note each of the four pitches involved
					short prev_bottom_pitch = previous_pitches[bottom_ind];
					short prev_top_pitch = previous_pitches[top_ind];
					short cur_bottom_pitch = current_pitches[bottom_ind];
					short cur_top_pitch = current_pitches[top_ind];

					// Output the pair's pitch transition for debugging
					//System.out.println(tick + ": " + prev_bottom_pitch + " to " + cur_bottom_pitch + " and " + prev_top_pitch + " to " + cur_top_pitch);
					
					// Move to the next pair if there is no change between this pair's pitches
					if (prev_bottom_pitch == cur_bottom_pitch && prev_top_pitch == cur_top_pitch)
						continue;
					
					// Note oblique motion
					if (prev_bottom_pitch == cur_bottom_pitch || prev_top_pitch == cur_top_pitch)
						oblique_count++;
					
					// Note contrary motion
					else if ( ((cur_bottom_pitch - prev_bottom_pitch) < 0 && (cur_top_pitch - prev_top_pitch) > 0) ||
					          ((cur_bottom_pitch - prev_bottom_pitch) > 0 && (cur_top_pitch - prev_top_pitch) < 0) )
						contrary_count++;
					
					// Note parallel motion
					else if ( (cur_top_pitch - cur_bottom_pitch) == (prev_top_pitch - prev_bottom_pitch) )
					{
						parallel_count++;
						
						// Also look for parallel fifths and octaves
						if ( (cur_top_pitch - cur_bottom_pitch) == 7)
							parallel_fifths_count++;
						if ( (cur_top_pitch - cur_bottom_pitch) == 12)
							parallel_octaves_count++;
					}
					
					// Note similar motion
					else similar_count++;
				}
			}
		}
		
		// Caculate the total amount of qualifying motion
		double total_motion_count = (double) parallel_count + (double) similar_count + 
									(double) contrary_count + (double) oblique_count;

		// Calculate the fractions of each type of motion
		if (total_motion_count > 0.0)
		{
			parallel_motion_fraction = ((double) parallel_count) / total_motion_count;
			similar_motion_fraction = ((double) similar_count) / total_motion_count;
			contrary_motion_fraction = ((double) contrary_count) / total_motion_count;
			oblique_motion_fraction = ((double) oblique_count) / total_motion_count;
			parallel_fifths_fraction = ((double) parallel_fifths_count) / total_motion_count;
			parallel_octaves_fraction = ((double) parallel_octaves_count) / total_motion_count;
		}
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


	/**
	 * Calculate the values of the rest_durations_separated_by_channel field.
	 */
	private void generateRestDurationsSeparatedByChannel()
	{
		// Set non_empty_channels to true for channels that have at least one note
		boolean[] non_empty_channels = new boolean[channel_statistics.length];
		for (int i = 0; i < non_empty_channels.length; i++)
		{
			if (channel_statistics[i][0] != 0)
				non_empty_channels[i] = true;
			else non_empty_channels[i] = false;
		}
		
		// A list of the duration of all rests (in seconds) in each channel, in the order that they appear
		// in that channel. Only channels including at least one note are included.
		ArrayList<ArrayList<Double>> rest_dration_list = new ArrayList<>();

		// The number of ticks to examine (the minus 1 is because Java doesn't count the last tick
		int ticks_to_test = note_sounding_on_a_channel_tick_map.length - 1;
		
		// Fill rest_dration_list channel by channel
		for (int chan = 0; chan < non_empty_channels.length; chan++)
		{
			if (non_empty_channels[chan])
			{
				// Note the amount of time during which there is a rest on each tick of this channel
				double[] seconds_of_rest_per_tick = new double[ticks_to_test];
				for (int tick = 0; tick < ticks_to_test; tick++)
				{
					if (!note_sounding_on_a_channel_tick_map[tick][chan])
						seconds_of_rest_per_tick[tick] = duration_of_ticks_in_seconds[tick];
					else seconds_of_rest_per_tick[tick] = 0.0;
				}
				
				// Find the duration of each rest in this channel (combined across ticks)
				ArrayList<Double> rest_durations_on_this_channel = new ArrayList<>();
				double current_rest_duration = 0.0;
				for (int tick = 0; tick < seconds_of_rest_per_tick.length; tick++)
				{
					if (seconds_of_rest_per_tick[tick] == 0.0 && current_rest_duration != 0.0)
					{
						rest_durations_on_this_channel.add(current_rest_duration);
						current_rest_duration = 0.0;
					}
					else if (seconds_of_rest_per_tick[tick] != 0.0)
						current_rest_duration += seconds_of_rest_per_tick[tick];
				}
				if (current_rest_duration != 0.0)
					rest_durations_on_this_channel.add(current_rest_duration);
				
				// Add the list of durations to rest_dration_list
				if (rest_durations_on_this_channel.size() > 0)
					rest_dration_list.add(rest_durations_on_this_channel);
			}
		}
		
		// Fill rest_durations_separated_by_channel based on rest_dration_list, after conversion from seconds
		// to fraction of a quarter note
		if (rest_dration_list.size() > 0)
		{
			rest_durations_separated_by_channel = new double[rest_dration_list.size()][];
			for (int i = 0; i < rest_durations_separated_by_channel.length; i++)
			{
				rest_durations_separated_by_channel[i] = new double[rest_dration_list.get(i).size()];
				for (int j = 0; j < rest_durations_separated_by_channel[i].length; j++)
				{
					double quarter_note_value = rest_dration_list.get(i).get(j) / average_quarter_note_duration_in_seconds;
					rest_durations_separated_by_channel[i][j] = quarter_note_value;
				}
			}
		}
		
		// Filter out all rests shorter than 0.1 of a quarter note.
		if (rest_durations_separated_by_channel != null)
			rest_durations_separated_by_channel = ArrayMethods.removeEntriesLessThan(rest_durations_separated_by_channel, 0.1);
	}
	
	
	/**
	 * Calculate the values of the complete_rest_durations field.
	 */	
	private void generateCompleteRestDurations()
	{
		// A list of the duration of all complete rests (in seconds) in the piece, in the order that they 
		// appear. Channel 10 notes are NOT included.
		ArrayList<Double> complete_rest_durations_list = new ArrayList();
		
		// The number of ticks to examine (the minus 1 is because Java doesn't count the last tick
		int ticks_to_test = note_sounding_on_a_channel_tick_map.length - 1;

		// Note the amount of time during which there is a complete rest on each tick
		double[] seconds_of_rest_per_tick = new double[ticks_to_test];
		for (int tick = 0; tick < ticks_to_test; tick++)
		{
			if (ArrayMethods.doesArrayContainOnlyThisValue(pitch_strength_by_tick_chart[tick], 0))
				seconds_of_rest_per_tick[tick] = duration_of_ticks_in_seconds[tick];
			else seconds_of_rest_per_tick[tick] = 0.0;
		}
			
		// Find the duration of each complete rest
		double current_rest_duration = 0.0;
		for (int tick = 0; tick < seconds_of_rest_per_tick.length; tick++)
		{
			if (seconds_of_rest_per_tick[tick] == 0.0 && current_rest_duration != 0.0)
			{
				complete_rest_durations_list.add(current_rest_duration);
				current_rest_duration = 0.0;
			}
			else if (seconds_of_rest_per_tick[tick] != 0.0)
				current_rest_duration += seconds_of_rest_per_tick[tick];
		}
		if (current_rest_duration != 0.0)
			complete_rest_durations_list.add(current_rest_duration);

		// Fill complete_rest_durations based on complete_rest_durations_list, after conversion from seconds
		// to fraction of a quarter note
		if (complete_rest_durations_list.size() > 0)
		{
			complete_rest_durations = new double[complete_rest_durations_list.size()];
			for (int i = 0; i < complete_rest_durations.length; i++)
			{
				double quarter_note_value = complete_rest_durations_list.get(i) / average_quarter_note_duration_in_seconds;
				complete_rest_durations[i] = quarter_note_value;
			}
		}		
		
		// Filter out all rests shorter than 0.1 of a quarter note.
		if (complete_rest_durations != null)
			complete_rest_durations = ArrayMethods.removeEntriesLessThan(complete_rest_durations, 0.1);
	}
	
	
	/* PRIVATE STATIC METHODS *******************************************************************************/

	
	/**
	 * Look ahead on a MIDI track to find the tick corresponding to the note off (or velocity 0 note on) for
	 * the specified note on.
	 *
	 * @param note_on				The note on message for which the note off is being searched for.
	 * @param note_on_start_tick	The MIDI tick on which note_on occurred.
	 * @param track					The MIDI track on which note_on is found.
	 * @return						The tick of the note off corresponding to note_on. If the note off never
	 *								occurs, then the last tick is returned.
 	 */
	private static int findCorrespondingNoteOffEndTick( ShortMessage note_on,
	                                                    int note_on_start_tick,
	                                                    Track track )
	{
		for (int i = note_on_start_tick + 1; i < track.size(); i++)
		{
			MidiEvent end_event = track.get(i);
			MidiMessage end_message = end_event.getMessage();
			if (end_message instanceof ShortMessage)
			{
				ShortMessage end_short_message = (ShortMessage) end_message;
				if (end_short_message.getChannel() == note_on.getChannel()) // must be on same channel
				{
					if (end_short_message.getCommand() == 0x80) // note off
					{
						if (end_short_message.getData1() == note_on.getData1()) // same pitch
							return (int) end_event.getTick();
					}
					if (end_short_message.getCommand() == 0x90) // note on (with vel 0 is equiv to note off)
					{
						if (end_short_message.getData2() == 0) // velocity 0
							if (end_short_message.getData1() == note_on.getData1()) // same pitch
								return (int) end_event.getTick();
					}
				}
			}
		}
		
		// Default to the total number of ticks if the note off is not found
		return (int) track.ticks();
	}
	
	
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
	 * Finds the number of MIDI ticks corresponding to the duration of a single beat at the given tempo in
	 * beats per minute (assuming the specified average tempo in ticks per second).
	 *
	 * @param	bpm					The tempo to convert, in beats per minute.
	 * @param	ticks_per_second	The number of MIDI ticks in one second.
	 * @return						The number of MIDI ticks in one beat at the given bpm tempo.
	 */
	private static int convertBPMtoTicks(int bpm, double ticks_per_second)
	{
		return (int) ((ticks_per_second * 60.0) / (double) bpm);
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