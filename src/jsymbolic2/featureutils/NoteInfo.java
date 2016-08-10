package jsymbolic2.featureutils;

/**
 * A convenience class that holds the data for notes for future processing
 * as an intermediate representation of the note data.
 */
public class NoteInfo {
    private int pitch;
    private int start_tick;
    private int end_tick;
    private int duration;
    private int channel;
    private int track;

    public NoteInfo(int pitch, int start_tick, int end_tick, int channel, int track) {
        this.pitch = pitch;
        this.start_tick = start_tick;
        this.end_tick = end_tick;
        this.duration = end_tick - start_tick;
        this.channel = channel;
        this.track = track;
    }

    public int getTrack() {
        return track;
    }

    public int getChannel() {
        return channel;
    }

    public int getDuration() {
        return duration;
    }

    public int getEnd_tick() {
        return end_tick;
    }

    public int getStart_tick() {
        return start_tick;
    }

    public int getPitch() {
        return pitch;
    }
}
