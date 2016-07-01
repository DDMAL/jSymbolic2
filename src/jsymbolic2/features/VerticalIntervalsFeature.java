package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dinamix on 6/27/16.
 */
public class VerticalIntervalsFeature extends MIDIFeatureExtractor {

    public VerticalIntervalsFeature()
    {
        String name = "Vertical Interval Succession";
        String description = "A feature vector consisting of the bin magnitudes of the\n" +
                "vertical interval histogram";
        boolean is_sequential = true;
        int dimensions = 128; //for each possible MIDI pitch
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = null;
        offsets = null;
    }

    @Override
    public double[] extractFeature(Sequence sequence,
                                   MIDIIntermediateRepresentations sequence_info,
                                   double[][] other_feature_values)
            throws Exception
    {
        // Get duration of piece in corresponding MIDI ticks
        long tick_duration = sequence.getTickLength();

        // Get tracks to be parsed from sequence
        Track[] tracks = sequence.getTracks();

        // A map where the key is the tick and value is a list of all note on messages at that tick
        Map<Long, List<ShortMessage>> all_note_ons = new HashMap<>();

        // Populate the map with key values corresponding to each MIDI tick of this sequence
        for(long i = 0; i <= tick_duration; i++) {
            all_note_ons.put(i, new ArrayList<>());
        }

        // Go through each midi event in each track and verify them, tick by tick
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
                    if (short_message.getCommand() == 0x90 && //note on
                            short_message.getChannel() != 10 - 1 && // not channel 10 (percussion)
                            short_message.getData2() != 0) // not velocity 0
                    {
                        // If short message is a note on, then add it to the mapped tick spot
                        long current_tick = event.getTick();
                        List<ShortMessage> current_tick_message_list = all_note_ons.get(current_tick);
                        current_tick_message_list.add(short_message);
                    }
                }
            }
        }

        // Initialize vertical intervals to 128 == largest possible midi interval
        double[] vertical_intervals = new double[128];

        // Check each interval at each tick accordingly
        for(Long key : all_note_ons.keySet())
        {
            List<ShortMessage> this_tick_messages = all_note_ons.get(key);
            for(ShortMessage this_message : this_tick_messages)
            {
                for(ShortMessage other_message : this_tick_messages)
                {
                    // Avoid comparing to the same message
                    if(this_message.equals(other_message)) continue;
                    // Store at proper interval
                    int this_pitch = this_message.getData1();
                    int other_pitch = other_message.getData1();
                    int interval = Math.abs(this_pitch - other_pitch);
                    vertical_intervals[interval]++;
                }
            }
        }
        return vertical_intervals;
    }
}
