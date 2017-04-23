package jsymbolic2.gui;

import java.io.File;
import javax.sound.midi.Sequence;

/**
 * A class for holding a reference to a symbolic music file and MIDI data associated with (or generated from)
 * it. This is typically a MIDI file or an MEI file.
 *
 * @author Cory McKay
 */
public class SymbolicMusicFile
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The path of the symbolic music file. Should be unique.
	 */
	public final String file_path;

	/**
	 * The name of this symbolic music file (not including the complete path).
	 */
	public final String file_name;

	/**
	 * A MIDI sequence parsed (or generated) from this symbolic music file. May sometimes be set to null in
	 * order to save space, but in such a case it can still be parsed from the symbolic music file.
	 */
	public Sequence midi_sequence;


	/* CONSTRUCTOR ******************************************************************************************/
	

	/**
	 * Instantiate a SymbolicMusicFile and store its associated sequence, if provided.
	 * 
	 * @param symbolic_music_file	The symbolic music file to store a reference to. Typically a MIDI or MEI
	 *								file.
	 * @param midi_sequence			A MIDI sequence associated with the symbolic_music_file. Typically parsed
	 *								from it (in the case of MIDI files) or generated from it (other symbolic
	 *								music file types). May be null if this information is not available or is
	 *								not to be stored.
	 */
	public SymbolicMusicFile(File symbolic_music_file, Sequence midi_sequence)
	{
		file_name = symbolic_music_file.getName();
		file_path = symbolic_music_file.getPath();
		this.midi_sequence = midi_sequence;
	}
}