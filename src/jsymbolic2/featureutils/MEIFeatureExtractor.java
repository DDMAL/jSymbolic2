package jsymbolic2.featureutils;

import javax.sound.midi.Sequence;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;

/**
 * The prototype class for features designed to extract features from MEI data (and MIDI data generated
 * elsewhere from this MEI data). Each class that extends this class will extract a particular feature from
 * some set of MEI data and its associated sequence of converted MIDI data. Such classes do not store feature
 * values, only extract them. Classes that extend this class should have a constructor that sets the five
 * protected fields of this class.
 *
 * <p>Note that this class extends {@link MIDIFeatureExtractor} and thus inherits that class' information,
 * except for the {@link MIDIFeatureExtractor#extractFeature(Sequence, MIDIIntermediateRepresentations, 
 * double[][])} method, which has been replaced by the {@link #extractMEIFeature(MeiSpecificStorage, Sequence,
 * MIDIIntermediateRepresentations, double[][])} method; the old method will thus will throw an 
 * {@link UnsupportedOperationException} if it is called.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public abstract class MEIFeatureExtractor
		extends MIDIFeatureExtractor
{
	/* PUBLIC METHODS ***************************************************************************************/
	

	/**
	 * The prototype method that classes extending this class will override in order to extract their
	 * feature from a section of MEI data (and the associated MIDI data generated from it).
	 *
	 * @param meiSpecificStorage	Holds all accessible MEI-specific data from which to extract this feature.
	 * @param sequence				The MIDI data generated from the MEI data, and which can abe used to help
	 *								extract this feature. Notice that this can be taken directly from the 
	 *								{@link org.ddmal.jmei2midi.MeiSequence}.
	 * @param sequence_info			Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that are needed to calculate this value. The
	 *								order and offsets of these features must be the same as those returned by 
	 *								this class's getDependencies and getDependencyOffsets methods
	 *								respectively.The first indice indicates the feature/window and the second
	 *								indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if the feature cannot be calculated.
	 */
	public abstract double[] extractMEIFeature(	MeiSpecificStorage meiSpecificStorage,
												Sequence sequence,
												MIDIIntermediateRepresentations sequence_info,
												double[][] other_feature_values )
	throws Exception;

	
	/**
	 * Throws an {@link UnsupportedOperationException} when called, as this class is for MEI features and not
	 * MIDI-only features.
	 *
	 * @param sequence							The MIDI data to extract the feature from.
	 * @param sequence_info						Additional data about the MIDI sequence.
	 * @param other_feature_values				The values of other features that may be needed to calculate
	 *											this feature. The order and offsets of these features must be
	 *											the same as those returned by this class' getDependencies and
	 *											getDependencyOffsets methods, respectively. The first indice
	 *											indicates the feature/window, and the second indicates the
	 *											value.
	 * @return									The extracted feature value(s).
	 * @throws UnsupportedOperationException	Informative exception automatically thrown if this method is
	 *											called.
	 */
	@Override
	public double[] extractFeature( Sequence sequence,
									MIDIIntermediateRepresentations sequence_info,
									double[][] other_feature_values )
	throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Unsupported Operation Exception: "
				+ "The extractFeature method is only valid for MIDI features, not valid for MEI feature. "
				+ "Use the extractMeiFeature method instead for MEI features.");
	}
}