/*
 * MIDIIntermediateRepresentations.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.processing;

import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.featureutils.NoteInfoList;

import java.util.ArrayList;
import	java.util.LinkedList;
import java.util.List;
import  javax.sound.midi.*;


/**
 * Objects of this class take in the path of a MIDI file when they are
 * constructed and automatically parse the file. The fields are then filled with
 * a number of different representations of the MIDI file and statistics about
 * it. These fields can then be accessed by feature objects.
 *
 * <p><b>Important Notes:</b></p>
 *
 * <p>- Patches are numbered one unit lower here than in their General MIDI
 * patch names, so remember to raise by one when processing.</p>
 *
 * <p>- Channesl are numbered 1 lower here than in real MIDI, so check for
 * channel 9 when actually looking for channel 10.</p>
 *
 * <p>- The data starts at tick 0.</p>
 *
 * <p>- MIDI files that use SMPTE time encoding are not compatible with this
 * software.</p>
 *
 * @author Cory McKay
 */
public class MIDIIntermediateRepresentations
{
     /* FIELDS ****************************************************************/
     
     
     /**
      * A listing of meta information. Indices correspond to the following:
      *
      * <p><i>Indice 0:</i> Quality stored as an Integer. 0 indicates major, 1
      * indicates minor and 0 indicates that no key signature is given. NOTE:
      * assumes only one key signature metamessage and considers only the first.
      *
      * <p><i>Indice 1:</i> LinkedList of all time signature numerators in the
      * order that they appeared (as Integers).
      *
      * <p><i>Indice 2:</i> LinkedList of all time signature denominators in the
      * order that they appeared (as Integers).
      *
      * <p><i>Indice 3:</i> Integer storing the initial tempo in beats per minute.
      */
     public    Object[]       meta_data;
     
     /**
      * A table with rows (first indice) corresponding to General MIDI pitched
      * patch numbers. The first column gives the total number of Note Ons
      * played using each patch. The second column gives the total time in
      * seconds (rounded) that at least one note was being held by each patch.
      */
     public    int[][]        pitched_instrumentation_frequencies;
     
     /**
      * A table with rows (first indice) corresponding to MIDI ticks in the MIDI
      * file. The columns (second indice) correspond to the pitched General MIDI
      * patches. An entry is set to true if a particular patch is playing at
      * least one note during a particular MIDI tick.
      */
     public    boolean[][]    pitched_instrumentation_tick_map;
     
     /**
      * An array whose indice corresponds to patches from the MIDI Percussion
      * Key Map. Each entry indicates the number of Note Ons played by the
      * specified patch.
      *
      * <p><b>NOTE:</b></p> values for all 128 note values were collected here,
      * although perhaps only notes 35 to 81 should be used. This is because
      * some recordings actually use these other values, even though they should
      * not. These other values simply duplicate the allowed values.
      */
     public    int[]          non_pitched_instrumentation_frequencies;
     
     /**
      * The total number of Note Ons in the recording.
      */
     public    int            total_number_notes;
     
     /**
      * The total number of Note Ons in the recording that were played using a
      * General MIDI pitched instrument.
      */
     public    int            total_number_pitched_notes;
     
     /**
      * The total number of Note Ons in the recording that were played using a
      * General MIDI Percussion Key Map instrument.
      *
      * <p><b>NOTE:</b></p> values for all 128 note values were collected here,
      * although perhaps only notes 35 to 81 should be used. This is because
      * some recordings actually use these other values, even though they should
      * not. These other values simply duplicate the allowed values.
      */
     public    int            total_number_unpitched_notes;
     
     /**
      * The length in seconds of the recording
      */
     public    int            recording_length;

    /**
     * The length in second of recording as a double
     */
    public double recording_length_double;
     
     /**
      * A table with rows (first indice) corresponding to MIDI ticks in the MIDI
      * file. The columns (second indice) correspond to the MIDI channels. An
      * entry is set to true if one or more notes was sounding on the given
      * channel during the given MIDI tick.
      *
      * <p>NOTE: this includes channel 10, even though it is understood that
      * channel 10 notes are played using percussion patches, not pitch patches.
      */
     public    boolean[][]    channel_tick_map;
     
     /**
      * A table with rows (first indice) corresponding to channels and the
      * following column designations:
      *
      * <p><i>Column 0:</i> total number of Note Ons on given channel
      *
      * <p><i>Column 1:</i> total amount of time in seconds that one or more
      * notes were sounding on given channel
      *
      * <p><i>Column 2:</i> average loudness (velocity scaled by channel volume
      * (still falls between 0 and 127)) of notes on given channel
      *
      * <p><i>Column 3:</i> average melodic leap on given channel in semitones
      * (includes accross rests and ignores direction) (will give erroneous
      * values if there is more than one melody per channel or if a channel is
      * polyphonic)
      *
      * <p><i>Column 4:</i> lowest MIDI pitch on given channel (value of 1000
      * means no pithces on given channel)
      *
      * <p><i>Column 5:</i> highest MIDI pitch on given channel (value of -1000
      * means no pithces on given channel)
      *
      * <p><i>Column 6:</i> average MIDI pitch on given channel (value of 0
      * means no pithces on given channel)
      *
      * <p>NOTE: this includes channel 10, even though it is understood that
      * channel 10 notes are played using percussion patches, not pitch patches.
      */
     public    int[][]       channel_statistics;
     
     /**
      * A normalized histogram with bins corresponding to beats per minute. The
      * frequency of each bin is proportional to the loudnesses of the notes
      * that occur at the bin's rhythmic interval.
      *
      * <p>All bins below 40 BPM are set to 0 because autocorrelation was not
      * performed at these lags. This omission is because the results are too
      * noisy.
      *
      * These measurements were taken without consideration of tempo change
      * messages, in order to emphasize the metrical notation of the recording.
      */
     public    double[]       rhythmic_histogram;
     
     /**
      * Table with rows (first index) corresponding to the bins of
      * rhythmic_histogram. Column 0 is related to bins that have a normalized
      * frequency of over 0.1, column 1 corresponds to bins that have a
      * normalized frequency of over 0.01 and column 2 corresponds to bins that
      * have frequencies at least 30% as high as the frequency of the highest
      * bin.
      *
      * <p>Entries are set to the frequency of the corresponding bin of
      * rhythmic_histogram if the frequency is high enough to meet the column's
      * requirements and to 0 otherwise.
      *
      * <p>This table was processed so that only peaks were included, which is
      * to say that entries for bins that are adjacent to bins with higher
      * frequencies are set to 0.
      */
     public    double[][]     rhythmic_histogram_table;
     
     /**
      * A list of the durations of all notes in seconds. Durations stored as
      * doubles.
      */
     public    LinkedList     note_durations;
     
     /**
      * A table with rows corresponding to MIDI ticks and columns corresponding
      * to MIDI channels. Entries are set to true whenever a Note On event
      * occurred on a given channel, and to false otherwise.
      *
      * <p>A final column (column 16) was added whose entry was set to true if
      * at least one Note On occurred on any channel at the corresponding MIDI
      * tick, and to false otherwise.
      */
     public    boolean[][]    note_beginnings_map;
     
     /**
      * Gives the loudnesses of all notes. The first indice corresponds to
      * channel number and the second indice corresponds to individual notes
      * (they are numbered in the order that they occured on each channel). Each
      * entry gives the velocity of the note scaled by channel volume. Entry
      * values range from 0 to 127.
      */
     public    int[][]        note_loudnesses;
     
     /**
      * A normalized histogram with bins corresponding to MIDI pitches (0 to
      * 127). The frequency of each bin corresponds to the number of Note Ons in
      * the recording at the pitch of the bin. Any notes on Channel 10
      * (percussion) are ignored.
      */
     public    double[]       basic_pitch_histogram;
     
     /**
      * A normalized histogram with bins corresponding to MIDI pitch classes (0
      * to 11). The frequency of each bin corresponds to the number of Note Ons
      * in the recording at the pitch class of the bin. Any notes on Channel 10
      * (percussion) are ignored. Enharmonic equivalents are assigned the same
      * pitch class number.
      */
     public    double[]       pitch_class_histogram;
     
     /**
      * A normalized histogram with bins corresponding to MIDI pitch classes (0
      * to 11). The bins are ordered such that adjacent bins are separated by a
      * perfect fifth rather than a semi-tone, as is the case with the
      * pitch_class_histogram. The frequency of each bin corresponds to the
      * number of Note Ons in the recording at the pitch class of the bin. Any
      * notes on Channel 10 (percussion) are ignored. Enharmonic equivalents are
      * assigned the same pitch class number.
      */
     public    double[]       fifths_pitch_histogram;
     
     /**
      * A list of lists of pitch bends associated with notes. Each entry of the
      * root list corresponds to a note that has at least one pitch bend message
      * associated with it. Each such entry contains a list of all pitchbend
      * messages (second MIDI data byte stored as an Integer) associated with
      * the note, in the order that they occured.
      */
     public    LinkedList     pitch_bends_list;
     
     /**
      * A normalized histogram with bins corresponding to melodic intervals
      * measured in number of semi-tones. The frequency of each bin corresponds
      * to the number of melodic intervals that occured of the kind referred to
      * by the bin. Rising and falling intervals are treated as identical.
      * Intervals are held as Integers. Any notes on Channel 10 (percussion) are
      * ignored. It is assumed that there is only one melody per channel during
      * generation of the melodic histogram.
      */
     public    double[]       melodic_histogram;
     
     /**
      * An array of lists of all melodic intervals occuring in each channel.
      * Indice value corresponds to channel. Units are semi-tones, with positive
      * values for upwards motion and negative values for downwards motion.
      * Order of occurence is preserved. Any notes on Channel 10 (percussion)
      * are ignored. It is assumed that there is only one melody per channel
      * during generation of the melodic histogram.
      */
     public    LinkedList[]   melody_list;
     
     /**
      * An array with an entry for each tick. The value at each indice gives the
      * duration of a tick in seconds at that particular point in the recording.
      */
     public    double[]       seconds_per_tick;
     
     /**
      * A table with rows (first indice) corresponding to MIDI ticks in the MIDI
      * file. The columns (second indice) correspond to the MIDI channels. An
      * entry is set to the channel volume as set by channel volume controller
      * messages divided by 127.
      *
      * <p>NOTE: the default is set to 1.0, which corresponds to a controller
      * value of 127.
      */
     public    double[][]     volumes;

     /**
      * A chart containing ticks in the first index and MIDI pitches in the second index
      * Each tick corresponds to all 128 possible midi ticks.
      * The cumulative velocities of each tick-pitch combination are stored in each
      * array value for the given MIDI sequence.
      */
     public short[][] vertical_interval_chart;

    /**
     * Total number of unisons notes where the first index is the tick and
     * the second is the pitch. A value of 1 means on note is playing.
     * A value of 2 means 2 notes are playing and thus 1 unison.
     * A value of 3 means 3 notes are playing and thus 2 unisons...
     */
    public short[][] unison;

    /**
     * Total velocity for all unisons in the entire piece.
     */
    public int total_unison_velocity;

    /**
     * TODO NOT USED CURRENTLY TO MUCH HEAP SPACE
     * The first index contains the track number of the given sequence.
     * It contains ticks in the second index and MIDI pitches in the third index
     * Each tick corresponds to all 128 possible midi ticks.
     * The cumulative velocities of each tick-pitch combination are stored in each
     * array value for the given MIDI sequence.
     */
    public int[][][] vertical_interval_track_chart;

    /**
     * TODO NOTE USED CURRENTLY TO MUCH HEAP SPACE
     * The first index contains the different channel numbers.
     * The second index contains the ticks for each specified channel.
     * The third index contains the pitch that occurs at the given
     * channel and tick.
     * This is derived from the vertical_interval_track_chart and the
     * channel_track arrays.
     */
    public int[][][] channel_tick_interval_chart;

    /**
     * The first index contains the channel number and the second index
     * contains the track indices related to the specified channel.
     * This can be used together with the vertical_interval_track_chart
     * in order to find note pitches at specified ticks on a channel.
     */
    public boolean[][] channel_track;

    /**
     * The first list contains a list for each channel. 
     * Each channel's list then contains the integer value of the
     * pitches for each note in the given channel list.
     * It is worth noting that order within each channel is not preserved.
     */
    public List<List<Integer>> channel_pitches;

    public NoteInfoList all_note_info;
     
     /**
      * Data loaded from a MIDI file
      */
     private   Sequence       sequence;
     
     /**
      * Data loaded from a MIDI file
      */
     private   Track[]        tracks;
     
     /**
      * Average number of MIDI ticks corresponding to 1 second of score time
      * Tempo change messages can cause variations, which is why this is a mean
      */
     private   double         mean_ticks_per_sec;


     /* CONSTRUCTORS **********************************************************/
     
     
     /**
      * Parses the given MIDI sequence and fills the fields with the appropriate
      * values extracted from this sequence.
      *
      * <p>Throws exceptions if an error is encountered when parsing the file.
      * These exceptions contain informative information about the error.
      *
      * @param      midi_sequence  The MIDI sequence to extract information
      *                            from.
      * @throws     Exception      Informative exceptions are thrown if problems
      *                            are encountered during parsing.
      */
     public MIDIIntermediateRepresentations(Sequence midi_sequence)
     throws Exception
     {
          // Check the MIDI sequence. Throw exceptions if the it uses SMPTE timing
          // or if it is too big. Fill sequence and tracks fields otherwise.
          sequence = midi_sequence;
          tracks = sequence.getTracks();
          if (sequence.getDivisionType() != Sequence.PPQ)
               throw new Exception("The current MIDI sequence uses SMPTE time encoding." +
                    "\nOnly PPQ time encoding is excepted here.");
          if ( ((double) sequence.getTickLength()) > ((double) Integer.MAX_VALUE) - 1.0)
               throw new Exception("The currentMIDI sequence could not be processed because it is too big.");
          
          // Caclulate timing information
          mean_ticks_per_sec = ((double) sequence.getTickLength()) / ((double) sequence.getMicrosecondLength() / 1000000.0);
          
          // Make sure that tempo change messages are accounted for in seconds_per_tick
          generateTempoAndVolumeMaps();
          
//for (int i = 0; i < seconds_per_tick.length; i++)
//	System.out.println(i + " " + seconds_per_tick[i]);
          
/*
for (int i = 0 ;i < volumes.length; i++)
{
        System.out.print("\nTick: " + i + "     ");
        for (int j = 0; j < volumes[i].length; j++)
        {
                System.out.print("j: " + j + " vol: " + volumes[i][j] + "    ");
        }
}
 */
          
          // Fill in the public fields of this class
          
          generateMetaInfo();
          
/*
int quality = ((Integer) meta_data[0]).intValue();
Object[] numerators_objects = ((LinkedList) meta_data[1]).toArray();
int[] numerators = new int[numerators_objects.length];
for (int i = 0; i < numerators.length; i++)
        numerators[i] = ((Integer) numerators_objects[i]).intValue();
Object[] denominators_objects = ((LinkedList) meta_data[2]).toArray();
int[] denominators = new int[denominators_objects.length];
for (int i = 0; i < denominators.length; i++)
        denominators[i] = ((Integer) denominators_objects[i]).intValue();
int tempo = ((Integer) meta_data[3]).intValue();
System.out.println(quality);
for (int i = 0; i < numerators.length; i++)
        System.out.print(numerators[i] + " ");
for (int i = 0; i < denominators.length; i++)
        System.out.print(denominators[i] + " ");
System.out.println("\n" + tempo);
System.out.println();
 */
          
          generatePitchedInstrumentationIntermediateRepresentations();
          
//for (int i = 0 ; i < pitched_instrumentation_frequencies.length; i ++)
//	System.out.println("INST: " + i + "   N Ons: " + pitched_instrumentation_frequencies[i][0] + "    Time: " + pitched_instrumentation_frequencies[i][1]);
          
          
/*
for (int i = 0; i < pitched_instrumentation_tick_map.length; i++)
{
        System.out.print("Tick: " + i + " - ");
        for (int j = 0; j < pitched_instrumentation_tick_map[i].length; j++)
                System.out.print("Inst " + j + ": " + pitched_instrumentation_tick_map[i][j] + "  |  ");
        System.out.print("\n");
}
 */
          
          generateNonPitchedInstrumentationIntermediateRepresentation();
          
//for (int i = 0; i < non_pitched_instrumentation_frequencies.length; i++)
//	System.out.println(i + " " + non_pitched_instrumentation_frequencies[i]);
          
          
          generateNoteCountsIntermediateRepresentations();
          
//System.out.println(total_number_notes + " " + total_number_pitched_notes + " " + total_number_unpitched_notes);
          
          generateDurationIntermediateRepresentation();

          generateDurationDoubleIntermediateRepresentation();
          
//System.out.println(recording_length);
          
          
          generateTextureIntermediateRepresentation();
          
          
/*
for (int i = 0; i < channel_tick_map.length; i++)
{
        System.out.print("Tick: " + i + " - ");
        for (int j = 0; j < channel_tick_map[i].length; j++)
                System.out.print("Channel " + j + ": " + channel_tick_map[i][j] + "  |  ");
        System.out.print("\n");
}
 */
          
/*
for (int i = 0; i < channel_statistics.length; i++)
{
        System.out.print("Channel: " + i + "  ");
        for (int j = 0; j < channel_statistics[i].length; j++)
                System.out.print(channel_statistics[i][j] + "    ");
        System.out.print("\n");
}
 */
          
          generateRhythmicHistogramIntermediateRepresentation();
          
//for (int i = 0; i < rhythmic_histogram.length; i++)
//	System.out.println("BPM: " + i + ": " + rhythmic_histogram[i]);
          
          
          generateRhythmicHistogramTableIntermediateRepresentation();
          
/*
for (int i = 0 ;i < rhythmic_histogram_table.length; i++)
{
        System.out.print("\nBPM: " + i + "     ");
        for (int j = 0; j < rhythmic_histogram_table[i].length; j++)
                System.out.print("   " + rhythmic_histogram_table[i][j]);
}
 */
          
          generateNoteDurationsIntermediateRepresentation();
          
/*
for (int i = 0; i < note_durations.size(); i++)
{
        double duration = ((Double) (note_durations.get(i))).doubleValue();
        System.out.println(duration);
}
 */
          
          generateNoteBeginningsMapIntermediateRepresentation();
          
/*
for (int i = 0 ;i < note_beginnings_map.length; i++)
{
        System.out.print("\ntick: " + i + "     ");
        for (int j = 0; j < note_beginnings_map[i].length; j++)
                System.out.print("   " + note_beginnings_map[i][j]);
}
 */
          
          generateNoteLoudnesses();
          
/*
for (int i = 0 ;i < note_loudnesses.length; i++)
{
        System.out.print("\nCHAN: " + i + "  ");
        for (int j = 0; j < note_loudnesses[i].length; j++)
                System.out.print("   " + note_loudnesses[i][j]);
}
System.out.println("\n");
 */
          
          generatePitchHistogramsIntermediateRepresentations();
          
/*
System.out.println("basic_pitch_histogram");
for (int i = 0; i < basic_pitch_histogram.length; i++)
        System.out.println(i + ": " + basic_pitch_histogram[i]);
System.out.println("pitch_class_histogram");
for (int i = 0; i < pitch_class_histogram.length; i++)
        System.out.println(i + ": " + pitch_class_histogram[i]);
System.out.println("fifths_pitch_histogram");
for (int i = 0; i < fifths_pitch_histogram.length; i++)
        System.out.println(i + ": " + fifths_pitch_histogram[i]);
 */
          
          generatePitchBendsList();
          
/*
Object[] notes_objects = pitch_bends_list.toArray();
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
System.out.print("\n\n--\n\n");
 */
          
          generateMelodicIntermediateRepresentations();
          
/*
for (int i = 0; i < melodic_histogram.length; i++)
        System.out.println(i + ": " + melodic_histogram[i]);
 */
          
/*
for (int i = 0 ;i < melody_list.length; i++)
{
        System.out.print("\nChannel: " + i + "     ");
        for (int j = 0; j < melody_list[i].size(); j++)
                System.out.print(" " + ((Integer) melody_list[i].get(j)).intValue());
}
 */
          //generateVerticalIntervalChart();
          generateLowSpaceVerticalIntervalChart();

          //Could be useful but not used right now
          //generateChannelTickIntervalChart();
     }
     
     
     /* PUBLIC METHODS ********************************************************/
     
     
     /**
      * Returns the fraction of Note Ons in the given sequence detailed by the
      * sequence_info parameter that are played by one of the General MIDI
      * patches included in the instruments parameter.
      *
      * @param	instruments        An array holding the General MIDI patches to
      *                            look for.
      * @param	sequence_info      Additional data about the MIDI sequence.
      * @return The value of the instrument group frequency.
      */
     public static double calculateInstrumentGroupFrequency( int[] instruments,
          MIDIIntermediateRepresentations sequence_info)
     {
          int notes_played = 0;
          for (int i = 0; i < instruments.length; i++)
               notes_played += sequence_info.pitched_instrumentation_frequencies[ instruments[i] ][0];
          
          return ((double) notes_played) / ((double) sequence_info.total_number_notes);
     }
     
     
     /* PRIVATE METHODS *******************************************************/
     
     
     /**
      * Look through the recording in order to find tempo change and channel
      * volume controller messages. Fill in seconds_per_tick and volumes based
      * on these messages.
      */
     private void generateTempoAndVolumeMaps()
     {
          // Instantiate seconds_per_tick and initialize entries to the average
          // number of ticks per second
          seconds_per_tick = new double[ (int) sequence.getTickLength() + 1];
          for (int i = 0; i < seconds_per_tick.length; i++)
               seconds_per_tick[i] = 1.0 / mean_ticks_per_sec;
          
          // Instantiate volumes and initialize entries to 1.0
          volumes = new double[ (int) sequence.getTickLength() + 1][ 16 ];
          for (int i = 0; i < volumes.length; i++)
               for (int j = 0; j < volumes[i].length; j++)
                    volumes[i][j] = 1.0;
          
          // Fill in tempo changes
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
                              byte[]	meta_data = meta_message.getData();
                              int	microseconds_per_beat = ((meta_data[0] & 0xFF) << 16)
                              | ((meta_data[1] & 0xFF) << 8)
                              | (meta_data[2] & 0xFF);
                              
                              // Find the number of seconds per tick
                              double current_seconds_per_tick = ((double) microseconds_per_beat) / ((double) ticks_per_beat);
                              current_seconds_per_tick = current_seconds_per_tick / 1000000.0;
                              
//System.out.println("Tick: " + event.getTick() + "  Current: " + current_seconds_per_tick  + "   Average: " + (1.0 / mean_ticks_per_sec));
                              
                              // Make all subsequent tempos be at the current_seconds_per_tick rate
                              for (int i = (int) event.getTick(); i < seconds_per_tick.length; i++)
                                   seconds_per_tick[i] = current_seconds_per_tick;
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
                                   // Make all subsequent volumes for the given channel
                                   // to the given volume
                                   for (int i = (int) event.getTick(); i < seconds_per_tick.length; i++)
                                        volumes[i][ short_message.getChannel() ] = ((double) short_message.getData2()) / 127.0;
                                   
//System.out.println("-> " + event.getTick() + " " + short_message.getChannel() + " " + short_message.getData2());
                              }
                         }
                    }
               }
          }
     }
     
     
     /**
      * Generate meta information
      */
     private void generateMetaInfo()
     {
          // Instantiat meta_data
          meta_data = new Object[4];
          meta_data[0] = new Integer(0); // major or minor
          meta_data[1] = new LinkedList(); // time signature numerator
          meta_data[2] = new LinkedList(); // time signature denominator
          meta_data[3] = new Integer(0);
          
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
                    
                    // If message is a MetaMessage
                    if (message instanceof MetaMessage)
                    {
                         byte[] data = ((MetaMessage) message).getData();
                         
                         // Check if major or minor
                         if (((MetaMessage) message).getType() == 0x59)
                         {
                              if (!key_sig_found)
                              {
                                   if (data[1] == 0) // major
                                        meta_data[0] = new Integer(0);
                                   else if (data[1] == 1) // minor
                                        meta_data[0] = new Integer(1);
                                   
                                   key_sig_found = true;
                              }
                         }
                         
                         // Check time signature
                         if (((MetaMessage) message).getType() ==  0x58)
                         {
                              ((LinkedList) meta_data[1]).add(new Integer((int) (data[0] & 0xFF)));
                              ((LinkedList) meta_data[2]).add(new Integer((int) (1 << (data[1] & 0xFF))));
                         }
                         
                         // Check the initial tempo
                         if (((MetaMessage) message).getType() == 0x51)
                         {
                              if (!tempo_found)
                              {
                                   // Find tempo in microseconds per beat
                                   int	ms_tempo = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);
                                   
                                   // Convert to beats per minute
                                   float ms_tempo_float = (float) ms_tempo;
                                   if (ms_tempo_float <= 0)
                                        ms_tempo_float = 0.1f;
                                   float bpm = 60000000.0f / ms_tempo_float;
                                   meta_data[3] = new Integer((int) bpm);
                                   
                                   tempo_found = true;
                              }
                         }
                    }
               }
          }
     }
     
     
     /**
      * Find the correct contents for the pitched_instrumentation_tick_map and
      * the pitched_instrumentation_frequencies fields
      */
     private void generatePitchedInstrumentationIntermediateRepresentations()
     {
          // Instantiate pitched_instrumentation_frequencies and initialize entries to 0
          pitched_instrumentation_frequencies = new int[128][2];
          for (int i = 0; i < pitched_instrumentation_frequencies.length; i++)
          {
               pitched_instrumentation_frequencies[i][0] = 0;
               pitched_instrumentation_frequencies[i][1] = 0;
          }
          
          // Instantiate pitched_instrumentation_tick_map and initialize entries to false
          pitched_instrumentation_tick_map = new boolean[(int) sequence.getTickLength() + 1][128];
          for (int i = 0; i < pitched_instrumentation_tick_map.length; i++)
               for (int j = 0; j < pitched_instrumentation_tick_map[i].length; j++)
                    pitched_instrumentation_tick_map[i][j] = false;
          
          // Fill in fields
          for (int n_track = 0; n_track < tracks.length; n_track++)
          {
               // Keep track of what patch is being used for each channel.
               // Default is 0.
               int[] current_patch_numbers = new int[16];
               for (int i = 0; i < current_patch_numbers.length; i++)
                    current_patch_numbers[i] = 0;
               
               // Go through all the events in the current track, searching for
               // note ons, note offs and program change messages
               Track track = tracks[n_track];
               for (int n_event = 0; n_event < track.size(); n_event++)
               {
                    // Get the MIDI message corresponding to the next MIDI event
                    MidiEvent event = track.get(n_event);
                    MidiMessage message = event.getMessage();
                    
                    // If message is a ShortMessage (which Note Ons, Note Offs and
                    // Program Change messages are)
                    if (message instanceof ShortMessage)
                    {
                         ShortMessage short_message = (ShortMessage) message;
                         if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
                         {
                              // If a Program Change message is encountered, then
                              // update current_patch_numbers
                              if (short_message.getCommand() == 0xc0)
                                   current_patch_numbers[ short_message.getChannel() ] = short_message.getData1();
                              
                              // If a Note On message is encountered, then increment first column of
                              // pitched_instrumentation_frequencies and note that have started playing
                              // the appropriate instrument
                              if (short_message.getCommand() == 0x90)
                              {
                                   if (short_message.getData2() != 0) // not velocity 0
                                   {
                                        // Increment the Note On count in pitched_instrumentation_frequencies
                                        pitched_instrumentation_frequencies[ current_patch_numbers[ short_message.getChannel() ] ][ 0 ]++;
                                        
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
                                                            if (end_short_message.getData2() == 0) // velocity 0
                                                                 if (end_short_message.getData1() == short_message.getData1()) // same pitch
                                                                 {
                                                            event_end_tick = (int) end_event.getTick();
                                                            i = track.size() + 1; // exit loop
                                                                 }
                                                  }
                                             }
                                        }
                                        
                                        // Fill in pitched_instrumentation_tick_map for all the ticks corresponding to this note
                                        for (int i = event_start_tick ; i < event_end_tick ; i++)
                                             pitched_instrumentation_tick_map[ i ][ current_patch_numbers[ short_message.getChannel() ] ] = true;
                                   }
                              }
                         }
                    }
               }
          }
          
          // Record the total time that each instrument was sounding in pitched_instrumentation_frequencies
          double[] total = new double[pitched_instrumentation_frequencies.length];
          for (int i = 0; i < total.length; i++)
               total[i] = 0.0;
          for (int instrument = 0; instrument < pitched_instrumentation_frequencies.length; instrument++)
               for (int tick = 0; tick < pitched_instrumentation_tick_map.length; tick++)
                    if (pitched_instrumentation_tick_map[tick][instrument])
                         total[instrument] = total[instrument] + seconds_per_tick[tick];
          for (int i = 0; i < total.length; i++)
               pitched_instrumentation_frequencies[i][1] = (int) total[i];
     }
     
     
     /**
      * Find the correct contents for the
      * non_pitched_instrumentation_frequencies field. Note that all 128 note
      * values are collected, although in general only note values 35 to 81
      * should be used.
      */
     private void generateNonPitchedInstrumentationIntermediateRepresentation()
     {
          // Instantiate non_pitched_instrumentation_frequencies and initialize entries to 0
          non_pitched_instrumentation_frequencies = new int[128];
          for (int i = 0; i < non_pitched_instrumentation_frequencies.length; i++)
               non_pitched_instrumentation_frequencies[i] = 0;
          
          // Fill in non_pitched_instrumentation_frequencies
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
                              // non_pitched_instrumentation_frequencies
                              if (short_message.getCommand() == 0x90)
                              {
                                   if (short_message.getData2() != 0) // not velocity 0
                                   {
                                        // Increment the Note On count in non_pitched_instrumentation_frequencies
                                        non_pitched_instrumentation_frequencies[ short_message.getData1() ]++;
                                   }
                              }
                         }
                    }
               }
          }
     }
     
     
     /**
      * Find the correct values for the total_number_notes,
      * total_number_pitched_notes and total_number_unpitched_notes fields
      */
     private void generateNoteCountsIntermediateRepresentations()
     {
          // Calculate total_number_notes
          total_number_notes = 0;
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
                                   total_number_notes++;
                    }
               }
          }
          
          // Calculate total_number_pitched_notes
          total_number_pitched_notes = 0;
          for (int i = 0; i < pitched_instrumentation_frequencies.length; i++)
               total_number_pitched_notes = total_number_pitched_notes + pitched_instrumentation_frequencies[i][0];
          
          // Calculate total_number_unpitched_notes
          total_number_unpitched_notes = 0;
          for (int i = 0; i < non_pitched_instrumentation_frequencies.length; i++)
               total_number_unpitched_notes = total_number_unpitched_notes + non_pitched_instrumentation_frequencies[i];
     }
     
     
     /**
      * Find the correct value for recording_length
      */
     private void generateDurationIntermediateRepresentation()
     {
          recording_length = (int) (sequence.getMicrosecondLength() / 1000000);
     }

     /**
      * Find the correct value for recording_length
      */
     private void generateDurationDoubleIntermediateRepresentation()
     {
          recording_length_double = sequence.getMicrosecondLength() / 1000000.0;
     }
     
     
     /**
      * Find the correct contents for the channel_tick_map and
      * channel_statistics fields. Note that channel 10 is included.
      */
     private void generateTextureIntermediateRepresentation()
     {
          // All note info
          all_note_info = new NoteInfoList();

          // Instantiate channel_tick_map and initialize entries to false
          channel_tick_map = new boolean[(int) sequence.getTickLength() + 1 ][16];
          for (int i = 0; i < channel_tick_map.length; i++)
               for (int j = 0; j < channel_tick_map[i].length; j++)
                    channel_tick_map[i][j] = false;

         // Instantiate channel_pitches with appropriate lists for each channel
         channel_pitches = new ArrayList<>(16);
         for(int channel = 0; channel < 16; channel++) {
             channel_pitches.add(new ArrayList<>());
         }
         
          // Instantiate channel_statistics and initialize entries to 0
          channel_statistics = new int[16][7];
          for (int i = 0; i < channel_statistics.length; i++)
               for (int j = 0; j < channel_statistics[i].length; j++)
                    channel_statistics[i][j] = 0;
          
          // Previous pitches encountered on channel
          int[] previous_pitches = new int[16];
          for (int i = 0; i < previous_pitches.length; i++)
               previous_pitches[i] = -1;
          
          // Total of intervals encountered on channel
          int[] interval_totals = new int[16];
          for (int i = 0; i < interval_totals.length; i++)
               interval_totals[i] = 0;
          
          // Number of intervals encountered on channel
          int[] number_intervals = new int[16];
          for (int i = 0; i < number_intervals.length; i++)
               number_intervals[i] = 0;
          
          // Last tick note_on was encounterd on channel
          int[] last_tick = new int[16];
          for (int i = 0; i < last_tick.length; i++)
               last_tick[i] = -1;
          
          // Lowest pitches encountered for each channel
          int[] lowest_pitches = new int[16];
          for (int i = 0; i < lowest_pitches.length; i++)
               lowest_pitches[i] = 1000;
          
          // Highest pitches encountered for each channel
          int[] highest_pitches = new int[16];
          for (int i = 0; i < highest_pitches.length; i++)
               highest_pitches[i] = -1000;
          
          // Sum of pitches of each channel
          int[] sum_of_pitches = new int[16];
          for (int i = 0; i < sum_of_pitches.length; i++)
               sum_of_pitches[i] = 0;
          
          // Fill in channel_tick_map
          for (int n_track = 0; n_track < tracks.length; n_track++)
          {
               // Go through all the events in the current track, searching for
               // note ons and note offs
               Track track = tracks[n_track];
               for (int n_event = 0; n_event < track.size(); n_event++)
               {
                    // Get the MIDI message corresponding to the next MIDI event
                    MidiEvent event = track.get(n_event);
                    MidiMessage message = event.getMessage();
                    
                    // If message is a ShortMessage (which Note Ons and Note Offs are)
                    if (message instanceof ShortMessage)
                    {
                         ShortMessage short_message = (ShortMessage) message;
                         
                         // If a Note On message is encountered
                         if (short_message.getCommand() == 0x90)
                         {
                              if (short_message.getData2() != 0) // not velocity 0
                              {
                                  // Add this note pitch to the channel_pitch list
                                  int this_channel = short_message.getChannel();
                                  int this_note_pitch = short_message.getData1();
                                  channel_pitches.get(this_channel).add(this_note_pitch);

                                   // Total the number of note ons per channel and thereby fill out column 0
                                   // of channel_statistics
                                   channel_statistics[ short_message.getChannel() ][ 0 ]++;

                                   // Total the loudnesses of Note Ons for each channel
                                   channel_statistics[ short_message.getChannel() ][ 2 ] += (int) (((double) short_message.getData2()) * volumes[ (int) event.getTick() ][ short_message.getChannel() ]);

                                   // Total the melodic semitones for each channel and adjust previous_pitches
                                   int current_tick = (int) event.getTick();
                                   if (previous_pitches[ short_message.getChannel() ] != -1)
                                   {
                                        // Check if the note is occuring on the same tick as the previous note
                                        // on this channel (which would indicate a vertical interval, not a melodic leap)
                                        if (current_tick != last_tick[ short_message.getChannel() ])
                                        {
                                             interval_totals[ short_message.getChannel() ] +=
                                                  Math.abs( previous_pitches[short_message.getChannel()] - short_message.getData1() );
                                             number_intervals[ short_message.getChannel() ]++;
                                        }
                                   }
                                   last_tick[ short_message.getChannel() ] = current_tick;
                                   previous_pitches[ short_message.getChannel() ] = short_message.getData1();

                                   // Update highest_pitches if appropriate
                                   if (short_message.getData1() > highest_pitches[short_message.getChannel()])
                                        highest_pitches[short_message.getChannel()] = short_message.getData1();

                                   // Update lowest_pitches if appropriate
                                   if (short_message.getData1() < lowest_pitches[short_message.getChannel()])
                                        lowest_pitches[short_message.getChannel()] = short_message.getData1();

                                   // Update sum_of_pitches
                                   sum_of_pitches[short_message.getChannel()] += short_message.getData1();

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
                                                           int pitch = end_short_message.getData1();
                                                            event_end_tick = (int) end_event.getTick();
                                                           NoteInfo this_note = new NoteInfo(pitch,
                                                                   event_start_tick,
                                                                   event_end_tick,
                                                                   end_short_message.getChannel(),
                                                                   n_track);
                                                           all_note_info.addNote(this_note);
                                                            i = track.size() + 1; // exit loop
                                                       }
                                                  }
                                                  if (end_short_message.getCommand() == 0x90) // note on (with vel 0 is equiv to note off)
                                                       if (end_short_message.getData2() == 0) // velocity 0
                                                            if (end_short_message.getData1() == short_message.getData1()) // same pitch
                                                            {
                                                                int pitch = end_short_message.getData1();
                                                                event_end_tick = (int) end_event.getTick();
                                                                NoteInfo this_note = new NoteInfo(pitch,
                                                                        event_start_tick,
                                                                        event_end_tick,
                                                                        end_short_message.getChannel(),
                                                                        n_track);
                                                                all_note_info.addNote(this_note);
                                                                i = track.size() + 1; // exit loop
                                                            }
                                             }
                                        }
                                   }

                                   // Fill in channel_tick_map for all the ticks corresponding to this note
                                   for (int i = event_start_tick ; i < event_end_tick ; i++)
                                        channel_tick_map[ i ][ short_message.getChannel() ] = true;
                              }
                         }
                    }
               }
          }
          
          // Find the total amount of time that one or more notes were playing on each
          // channel and fill out column 1
          double[] total = new double[channel_statistics.length];
          for (int i = 0; i < total.length; i++)
               total[i] = 0.0;
          for (int i = 0; i < channel_tick_map.length; i++)
               for (int j = 0; j < channel_tick_map[i].length; j++)
                    if (channel_tick_map[i][j])
                         total[j] = total[j] + seconds_per_tick[i];
          for (int i = 0; i < channel_statistics.length; i++)
               channel_statistics[i][1] = (int) total[i];
          
          // Fill column 2 by dividing the total scaled velocities by the number of Note Onts
          // for each channel
          for (int i = 0; i < channel_statistics.length; i++)
               channel_statistics[i][2] = (int) (((double) channel_statistics[i][2]) / ((double) channel_statistics[i][0]));
          
          // Fill column 3 by dividing the total melodic leaps in semi-tones by the number
          // of melodic intervals for each channel
          for (int i = 0; i < channel_statistics.length; i++)
               channel_statistics[i][3] = (int) (((double) interval_totals[i]) / ((double) number_intervals[i]));
          
          // Fill columns 4 and 5 (lowest and highest pitches) of channel_statistics
          for (int i = 0; i < channel_statistics.length; i++)
          {
               channel_statistics[i][4] = lowest_pitches[i];
               channel_statistics[i][5] = highest_pitches[i];
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
      * Find the contents for the rhythmic_histogram field
      */
     private void generateRhythmicHistogramIntermediateRepresentation()
     {
          // Set the minimum and maximum tempos that will be used in the autocorrelation
          int min_BPM = 40;
          int max_BPM = 200;
          
          // Instantiate rhythmic_histogram and initialize entries to 0
          rhythmic_histogram = new double[max_BPM + 1];
          for (int i = 0; i < rhythmic_histogram.length; i++)
               rhythmic_histogram[i] = 0.0;
          
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
                              rhythm_score[current_tick] += (int) (((double) short_message.getData2()) * volumes[ current_tick ][ short_message.getChannel() ]);
                    }
               }
          }
          
          // Histogram based on tick interval bins
          double[] tick_histogram = new double[convertBPMtoTicks(min_BPM - 1)];
          for (int lag = convertBPMtoTicks(max_BPM); lag < tick_histogram.length; lag++)
               tick_histogram[lag] = autoCorrelate(rhythm_score, lag);
          
          // Histogram with tick intervals collected into beats per minute bins
          for (int bin = min_BPM; bin <= max_BPM; bin++)
          {
               rhythmic_histogram[bin] = 0.0;
               for (int tick = convertBPMtoTicks(bin); tick < convertBPMtoTicks(bin-1); tick++)
                    rhythmic_histogram[bin] += tick_histogram[tick];
          }
          
          // Normalize rhythmic_histogram
          double sum = 0;
          for (int i = 0; i < rhythmic_histogram.length; i++)
               sum += rhythmic_histogram[i];
          for (int i = 0; i < rhythmic_histogram.length; i++)
               rhythmic_histogram[i] = rhythmic_histogram[i] / sum;
     }
     
     
     /**
      * Find the contents for the rhythmic_histogram_table field
      */
     private void generateRhythmicHistogramTableIntermediateRepresentation()
     {
          // Instantiate rhythmic_histogram_table and set entries to 0
          rhythmic_histogram_table = new double[rhythmic_histogram.length][3];
          for (int i = 0; i < rhythmic_histogram_table.length; i++)
               for (int j = 0; j < rhythmic_histogram_table[i].length; j++)
                    rhythmic_histogram_table[i][j] = 0.0;
          
          // Find the highest frequency in rhythmic histogram
          double highest_frequency = rhythmic_histogram[ getIndexOfHighest(rhythmic_histogram) ];
          
          // Fill out rhythmic_histogram_table
          for (int i = 0; i < rhythmic_histogram.length; i++)
          {
               if (rhythmic_histogram[i] > 0.1)
                    rhythmic_histogram_table[i][0] = rhythmic_histogram[i];
               if (rhythmic_histogram[i] > 0.01)
                    rhythmic_histogram_table[i][1] = rhythmic_histogram[i];
               if (rhythmic_histogram[i] > (0.3 * highest_frequency) )
                    rhythmic_histogram_table[i][2] = rhythmic_histogram[i];
          }
          
          // Make sure all values refer to peaks, and are not adjacent
          for (int i = 1; i < rhythmic_histogram_table.length; i++)
          {
               for (int j = 0; j < rhythmic_histogram_table[i].length; j++)
               {
                    if (rhythmic_histogram_table[i][j] > 0.0 && rhythmic_histogram_table[i-1][j] > 0.0)
                    {
                         if (rhythmic_histogram_table[i][j] > rhythmic_histogram_table[i-1][j])
                              rhythmic_histogram_table[i-1][j] = 0.0;
                         else
                              rhythmic_histogram_table[i][j] = 0.0;
                    }
               }
          }
     }
     
     
     /**
      * Find the contents for the note_durations field
      */
     private void generateNoteDurationsIntermediateRepresentation()
     {
          // Instantiate note_durations as an empty list
          note_durations = new LinkedList();
          
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
                                                  if (end_short_message.getCommand() == 0x90) // note on (with vel 0 is equiv to note off)
                                                       if (end_short_message.getData2() == 0) // velocity 0
                                                            if (end_short_message.getData1() == short_message.getData1()) // same pitch
                                                            {
                                                       event_end_tick = (int) end_event.getTick();
                                                       i = track.size() + 1; // exit loop
                                                            }
                                             }
                                        }
                                   }
                                   
                                   // Calculate duration of note
                                   double duration = 0;
                                   for (int i = event_start_tick ; i < event_end_tick ; i++)
                                        duration += seconds_per_tick[i];
                                   
                                   // Add note to list
                                   note_durations.add(new Double(duration));
                              }
                         }
                    }
               }
          }
     }
     
     
     /**
      * Find the contents for the note_beginnings_map and note_endings_map
      * fields
      */
     private void generateNoteBeginningsMapIntermediateRepresentation()
     {
          // Instantiate note_beginnings_map and set entries to false
          note_beginnings_map = new boolean[((int) sequence.getTickLength()) + 1][17];
          for (int i = 0; i < note_beginnings_map.length; i++)
               for (int j = 0; j < note_beginnings_map[i].length; j++)
                    note_beginnings_map[i][j] = false;
          
          // Fill in note_beginnings_map for all channels
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
                         {
                              if (short_message.getData2() != 0) // not velocity 0
                                   note_beginnings_map[ (int) event.getTick() ][ short_message.getChannel() ] = true;
                         }
                    }
               }
          }
          
          // Fill in column 16 of note_beginnings_map to show if any Note Ons occured
          // on at least one channel on the corresponding tick
          for (int i = 0; i < note_beginnings_map.length; i++)
          {
               for (int j = 0; j < note_beginnings_map[i].length - 1; j++)
               {
                    if (note_beginnings_map[i][j])
                    {
                         note_beginnings_map[i][16] = true;
                         j = note_beginnings_map[i].length; // exit loop
                    }
               }
          }
     }
     
     
     /**
      * Find the contents for the note_loudnesses field
      */
     private void generateNoteLoudnesses()
     {
          // Instantiate note_loudnesses
          note_loudnesses = new int[16][];
          for (int i = 0; i < note_loudnesses.length; i++)
               note_loudnesses[i] = new int[ channel_statistics[i][0] ];
          
          // Keep track of how many notes have occured on each channel
          int[] notes_so_far = new int[16];
          for (int i = 0; i < notes_so_far.length; i++)
               notes_so_far[i] = 0;
          
          // Fill in note_loudnesses
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
                         if (short_message.getCommand() == 0x90) // note on
                         {
                              if (short_message.getData2() != 0) // not velocity 0
                              {
                                   int channel = short_message.getChannel();
                                   int tick = (int) event.getTick();
                                   note_loudnesses[channel][notes_so_far[channel]] = (int) (((double) short_message.getData2()) * volumes[tick][channel]);
                                   notes_so_far[channel]++;
                              }
                         }
                    }
               }
          }
     }
     
     
     /**
      * Find the contents of the basic_pitch_histogram, pitch_class_histogram
      * and fifths_pitch_histogram fields.
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
                         {
                              if (short_message.getCommand() == 0x90) // note on
                              {
                                   if (short_message.getData2() != 0) // not velocity 0
                                        basic_pitch_histogram[short_message.getData1()]++;
                              }
                         }
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
               pitch_class_histogram[i%12] += basic_pitch_histogram[i];
          
          // Generate fifths_pitch_histogram
          fifths_pitch_histogram = new double[12];
          for (int i = 0; i < fifths_pitch_histogram.length; i++)
               fifths_pitch_histogram[i] = 0;
          for (int i = 0; i < fifths_pitch_histogram.length; i++)
               fifths_pitch_histogram[(7*i)%12] += pitch_class_histogram[i];
     }
     
     
     /**
      * Find the contents of the pitch_bends_list feature
      */
     private void generatePitchBendsList()
     {
          // The list of lists of pitch bends
          pitch_bends_list = new LinkedList();
          
          // The lists of pitch bends for the last note on each channel. An entry
          // is null unless a pitch bend message has been received on the given
          // channel since the last Note Off on that channel
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
                                   // If a pitch bend has already been given for this note
                                   if (going[short_message.getChannel()] != null)
                                   {
                                        int pitch_bend_value = short_message.getData2();
                                        going[short_message.getChannel()].add(new Integer(pitch_bend_value));
                                   }
                                   
                                   // If a pitch bend has not already been given for this note
                                   else
                                   {
                                        int pitch_bend_value = short_message.getData2();
                                        LinkedList this_note = new LinkedList();
                                        this_note.add(new Integer(pitch_bend_value));
                                        pitch_bends_list.add(this_note);
                                        
                                        going[short_message.getChannel()] = this_note;
                                   }
                              }
                              
                              // If message is a Note Off
                              if (short_message.getCommand() == 0x80) // note off
                                   going[short_message.getChannel()] = null;
                              else if (short_message.getCommand() == 0x90) // note on
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
      * Find the contents of the melodic_histogram and melody_list features.
      */
     private void generateMelodicIntermediateRepresentations()
     {
          // Initialize melodic_histogram
          melodic_histogram = new double[128];
          for (int i = 0; i < melodic_histogram.length; i++)
               melodic_histogram[i] = 0.0;
          
          // Initialize melody_list
          melody_list = new LinkedList[16];
          for (int i = 0; i < melody_list.length; i++)
               melody_list[i] = new LinkedList();
          
          // Previous pitches encountered on channel
          int[] previous_pitches = new int[16];
          for (int i = 0; i < previous_pitches.length; i++)
               previous_pitches[i] = -1;
          
          // Last tick note_on was encounterd on channel
          int[] last_tick = new int[16];
          for (int i = 0; i < last_tick.length; i++)
               last_tick[i] = -1;
          
          // Fill melodic_histogram and melody_list
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
                         if (short_message.getCommand() == 0x90) // note on
                         {
                              if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
                              {
                                   if (short_message.getData2() != 0) // not velocity 0
                                   {
                                        int current_tick = (int) event.getTick();
                                        if (previous_pitches[ short_message.getChannel() ] != -1)
                                        {
                                             // Check if the note is occuring on the same tick as the previous note
                                             // on this channel (which would indicate a vertical interval, not a melodic leap)
                                             if (current_tick != last_tick[ short_message.getChannel() ])
                                             {
                                                  int interval =  short_message.getData1() - previous_pitches[short_message.getChannel()] ;
                                                  melodic_histogram[ Math.abs(interval) ]++;
                                                  melody_list[short_message.getChannel()].add(new Integer(interval));
                                             }
                                        }
                                        last_tick[ short_message.getChannel() ] = current_tick;
                                        previous_pitches[ short_message.getChannel() ] = short_message.getData1();
                                   }
                              }
                         }
                    }
               }
          }
          
          // Normalize melodic_histogram
          double sum = 0.0;
          for (int i = 0; i < melodic_histogram.length; i++)
               sum += melodic_histogram[i];
          for (int i = 0; i < melodic_histogram.length; i++)
               melodic_histogram[i] = melodic_histogram[i] / sum;
     }

    /**
     * Generate the vertical interval chart described in the variable declarations.
     */
    private void generateVerticalIntervalChart() {
        // Instantiate channel_tracks, automatically initialized to 0
        int number_of_tracks = sequence.getTracks().length;
        channel_track = new boolean[16][number_of_tracks];

         // Get duration of piece in corresponding MIDI ticks
         int tick_duration = (int)sequence.getTickLength() + 1;
         // Get tracks to be parsed from sequence
         Track[] tracks = sequence.getTracks();

         // Maximum number of pitches possible in MIDI
         int number_of_pitches = 128;

         // Array for tracks by ticks by pitches and note off markers
         // All arrays initialized to 0 according to Java language spec
         int[][][] tracks_by_ticks_by_pitch = new int[tracks.length][tick_duration][number_of_pitches];
         boolean[][][] note_off = new boolean[tracks.length][tick_duration][number_of_pitches];

         // Go through each midi event in each track and verify them, tick by tick
         for (int n_track = 0; n_track < tracks.length; n_track++) {
              Track track = tracks[n_track];
              // To keep track of velocities for separate pitches
              for (int n_event = 0; n_event < track.size(); n_event++) {
                   MidiEvent event = track.get(n_event);
                   MidiMessage message = event.getMessage();
                   if (message instanceof ShortMessage) {
                        ShortMessage short_message = (ShortMessage) message;
                        int current_tick = (int) event.getTick();
                        channel_track[short_message.getChannel()][n_track] = true;
                        // Clearly only true if this is a note on or off
                        int current_pitch = short_message.getData1();
                        int current_velocity = short_message.getData2();
                        // If note off message then we mark it
                        if (short_message.getCommand() == 0x80 && // note off
                                short_message.getChannel() != 10 - 1) // not channel 10
                        {
                             note_off[n_track][current_tick][current_pitch] = true;
                        }
                        else if (short_message.getCommand() == 0x90 && // note on
                                short_message.getChannel() != 10 - 1 && // not channel 10 (percussion)
                                short_message.getData2() != 0) // not velocity 0
                        {
                             //add the velocity at this current tick and pitch
                             tracks_by_ticks_by_pitch[n_track][current_tick][current_pitch] += current_velocity;
                        }
                   }
              }
         }

         // Now copy over all events for each tick when the notes are still on for each track
         // Turn them off whenever we find a note off check (i.e. set them back to 0 since no other notes played there)
         // Then, Copy over all tick-pitch velocity values for each track to a global tick-pitch array
         short[][] ticks_by_pitch = new short[tick_duration][number_of_pitches];
         for(int track = 0; track < tracks_by_ticks_by_pitch.length; track++) {
              for (int tick = 0; tick < tracks_by_ticks_by_pitch[track].length; tick++) {
                   for (int pitch = 0; pitch < tracks_by_ticks_by_pitch[track][tick].length; pitch++) {
                        boolean note_off_check = note_off[track][tick][pitch];
                        if (note_off_check &&
                                tracks_by_ticks_by_pitch[track][tick][pitch] > 0) {
                             //do nothing since there was a note-on here because we have a velocity from before
                        }
                        else if (note_off_check) {
                             tracks_by_ticks_by_pitch[track][tick][pitch] = 0;
                        }
                        else if (tick > 0) {
                             int previous_value = tracks_by_ticks_by_pitch[track][tick - 1][pitch];
                             tracks_by_ticks_by_pitch[track][tick][pitch] += previous_value;
                        }
                        ticks_by_pitch[tick][pitch] += tracks_by_ticks_by_pitch[track][tick][pitch];
                   }
              }
         }
         vertical_interval_track_chart = tracks_by_ticks_by_pitch;
         vertical_interval_chart = ticks_by_pitch;
     }

    /**
     * Generate the vertical interval chart described in the variable declarations.
     */
    private void generateLowSpaceVerticalIntervalChart() {
        // Instantiate channel_tracks, automatically initialized to 0
        int number_of_tracks = sequence.getTracks().length;
        channel_track = new boolean[16][number_of_tracks];

        // Get duration of piece in corresponding MIDI ticks
        int tick_duration = (int)sequence.getTickLength() + 1;
        // Get tracks to be parsed from sequence
        Track[] tracks = sequence.getTracks();

        // Maximum number of pitches possible in MIDI
        int number_of_pitches = 128;

        unison = new short[tick_duration][number_of_pitches];
        vertical_interval_chart = new short[tick_duration][number_of_pitches];
        
        // Go through each midi event in each track and verify them, tick by tick
        for (int n_track = 0; n_track < tracks.length; n_track++) {
            Track track = tracks[n_track];
            // To keep track of velocities for separate pitches
            for (int on_event_index = 0; on_event_index < track.size(); on_event_index++) {
                MidiEvent on_event = track.get(on_event_index);
                MidiMessage on_message = on_event.getMessage();
                if (on_message instanceof ShortMessage) {
                    ShortMessage short_on_message = (ShortMessage) on_message;
                    int on_tick = (int) on_event.getTick();
                    // Clearly only true if this is a note on or off
                    int on_pitch = short_on_message.getData1();
                    int on_velocity = short_on_message.getData2();
                    int on_channel = short_on_message.getChannel();
                    if (short_on_message.getCommand() == 0x90 && // note on
                            short_on_message.getChannel() != 10 - 1 && // not channel 10 (percussion)
                            short_on_message.getData2() != 0) // not velocity 0
                    {
                        //lookahead for note offs here and add velocity to each pitch accordingly
                        lookAheadToNoteOffAndFill(on_event_index,
                                                    track,
                                                    on_pitch,
                                                    on_channel,
                                                    on_velocity,
                                                    on_tick,
                                                    vertical_interval_chart);
                    }
                }
            }
        }
    }

    private void lookAheadToNoteOffAndFill(int on_event_index,
                                  Track track,
                                  int on_pitch,
                                  int on_channel,
                                  int on_velocity,
                                  int on_tick,
                                  short[][] ticks_by_pitch) {
        //Lookahead and add velocities until we find corresponding note off event
        for(int off_event_index = on_event_index; off_event_index < track.size(); off_event_index++) {
            MidiEvent off_event = track.get(off_event_index);
            MidiMessage off_message = off_event.getMessage();
            if(off_message instanceof ShortMessage) {
                ShortMessage short_off_message = (ShortMessage) off_message;
                int off_tick = (int) off_event.getTick();
                int off_pitch = short_off_message.getData1();
                int off_channel = short_off_message.getChannel();
                if (((short_off_message.getCommand() == 0x80 && // note off
                        short_off_message.getChannel() != 10 - 1) || // not channel 10
                        (short_off_message.getCommand() == 0x90 && // note off
                         short_off_message.getChannel() != 10 - 1 && // not channel 10
                         short_off_message.getData2() == 0)) && //note on with velocity 0
                        off_pitch == on_pitch && // pitches are the same
                        off_channel == on_channel) // channel is the same
                {
                    //This note has ended so fill up chart and stop lookahead
                    for(int tick = on_tick; tick < off_tick; tick++) {
                        //Check for unisons
                        if(ticks_by_pitch[tick][on_pitch] > 0 &&
                                on_velocity > 0) {
                            int number_of_unisons = unison[tick][on_pitch];
                            if(number_of_unisons == 1) {
                                unison[tick][on_pitch]++;
                                total_unison_velocity += ticks_by_pitch[tick][on_pitch] + on_velocity;
                            } else if(number_of_unisons > 1) {
                                unison[tick][on_pitch]++;
                                total_unison_velocity += on_velocity;
                            }
                        } else if (ticks_by_pitch[tick][on_pitch] == 0 &&
                                on_velocity > 0){
                            unison[tick][on_pitch]++;
                        }
                       ticks_by_pitch[tick][on_pitch] += on_velocity;
                    }
                    break; //reached off of note so we dont need to add velocities anymore and return
                }
            }
        }
    }
    
    /**
     * Generate the vertical channel tick interval chart.
     */
    private void generateChannelTickIntervalChart() {
         for(int track = 0; track < vertical_interval_track_chart.length; track++) {
             for(int tick = 0; tick < vertical_interval_track_chart[track].length; tick++) {
                 for(int pitch = 0; pitch < vertical_interval_track_chart[track][tick].length; pitch++) {
                     for(int channel = 0; channel < channel_track.length; channel++) {
                         if(channel_track[channel][track]) {
                             int current_velocity = vertical_interval_track_chart[track][tick][pitch];
                             channel_tick_interval_chart[channel][tick][pitch] += current_velocity;
                             break;
                         }
                     }
                 }
             }
         }
     }

     /**
      * Return the index of the highest value in data
      * @param data The data that is being checked.
      * @return The highest index from the inputted data.
      */
     private int getIndexOfHighest(double[] data)
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
      * Returns the number of ticks corresponding to a beat at the given tempo
      * in beats per minute using the average tempo.
      * @param BPM The original BPM value.
      * @return The integer value of bpm to ticks.
      */
     private int convertBPMtoTicks(int BPM)
     {
          return (int) ((mean_ticks_per_sec * 60) / BPM);
     }
     
     
     /**
      * Perform an autocorrelation on data with the given lag:
      *
      * y[lag] = (1/N) SUM(n to N){ x[n] * x[n-lag] }
      * @param data The data to be correlated.
      * @param lag The lag for each data point.
      * @return The corresponding auto correlation.
      */
     private double autoCorrelate(int[] data, int lag)
     {
          double result = 0.0;
          
          for (int i = lag; i < data.length; i++)
               result += (double) (data[i] * data[i-lag]);
          
          return result / (double) data.length; // divide by N
     }
}
