package jsymbolic2.featureutils;

/**
 * An object of this class holds information about a single note that has been parsed from a MIDI stream
 * (including Channel 10 unpitched notes).
 * 
 * @author Tristano Tenaglia and Cory McKay
 */
public class NoteInfo
{
	/* PRIVATE FIELDS ***************************************************************************************/

	
	/**
	 * The MIDI pitch of this note (or, in the case of Channel 10, the instrument identifier).
	 */
	private final int pitch;
	
	/**
	 * The MIDI velocity of this note.
	 */
	private final int velocity;
	
	/**
	 * The MIDI tick on which this note starts.
	 */
	private final int start_tick;

	/**
	 * The MIDI tick on which this note ends.
	 */
	private final int end_tick;
	
	/**
	 * The duration (in MIDI ticks) of this note.
	 */
	private final int duration;
		
	/**
	 * The MIDI track on which this note is found.
	 */
	private final int track;
		
	/**
	 * The MIDI channel on which this note is found.
	 */
	private final int channel;
	
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Instantiate an object holding information about this note.
	 * 
	 * @param pitch			The MIDI pitch of this note (or, in the case of Channel 10, the instrument 
	 *						identifier).
	 * @param velocity		The MIDI velocity of this note.
	 * @param start_tick	The MIDI tick on which this note starts.
	 * @param end_tick		The MIDI tick on which this note ends.
	 * @param track			The MIDI track on which this note is found.
	 * @param channel		The MIDI channel on which this note is found.
	 */
	public NoteInfo(int pitch, int velocity, int start_tick, int end_tick, int track, int channel)
	{
		this.pitch = pitch;
		this.velocity = velocity;
		this.start_tick = start_tick;
		this.end_tick = end_tick;
		this.duration = end_tick - start_tick;
		this.track = track;
		this.channel = channel;
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * @return The MIDI pitch of this note (or, in the case of Channel 10, the instrument identifier).
	 */
	public int getPitch()
	{
		return pitch;
	}

	
	/**
	 * @return The MIDI velocity of this note.
	 */
	public int getVelocity()
	{
		return velocity;
	}

	
	/**
	 * @return The MIDI tick on which this note starts.
	 */
	public int getStartTick()
	{
		return start_tick;
	}
	
	
	/**
	 * @return The MIDI tick on which this note ends.
	 */
	public int getEndTick()
	{
		return end_tick;
	}

	
	/**
	 * @return The duration (in MIDI ticks) of this note.
	 */
	public int getDuration()
	{
		return duration;
	}	
	
	
	/**
	 * @return The MIDI track on which this note is found.
	 */
	public int getTrack()
	{
		return track;
	}

	
	/**
	 * @return The MIDI channel on which this note is found.
	 */
	public int getChannel()
	{
		return channel;
	}
}