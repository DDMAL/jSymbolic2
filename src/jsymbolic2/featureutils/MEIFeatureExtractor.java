package jsymbolic2.featureutils;

import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;

import javax.sound.midi.Sequence;

/**
 * The prototype for feature extractors. Each class that extends this class
 * will extract a particular feature from a window of MEI data. Such
 * classes do not store feature values, only extract them.
 *
 * Notice that this class extends {@link MIDIFeatureExtractor} and thus
 * inherits all the functionality of that class except for the
 * {@link MIDIFeatureExtractor#extractFeature(Sequence, MIDIIntermediateRepresentations, double[][])} function,
 * which has been replaced by the {@link #extractMEIFeature(MeiSpecificStorage, Sequence, MIDIIntermediateRepresentations, double[][])}
 * function and thus will throw an {@link UnsupportedOperationException} if it is called.
 *
 * @author Tristano Tenaglia
 */
public abstract class MEIFeatureExtractor extends MIDIFeatureExtractor{
    /**
     * Feature to be extracted from the mei specific storage object.
     * @param meiSpecificStorage The storage that holds all accessible mei specific data.
     * @param sequence The MIDI sequence that this mei feature corresponds to.
     *                 Notice that this can be taken directly from the {@link org.ddmal.jmei2midi.MeiSequence}.
     * @param sequence_info The jSymbolic intermediate reprentation for MIDI information.
     * @param other_feature_values The values taken from the other features.
     * @return A double array to conform with the MIDI Feature Exctraction.
     */
    public abstract double[] extractMEIFeature(MeiSpecificStorage meiSpecificStorage,
                                               Sequence sequence,
                                               MIDIIntermediateRepresentations sequence_info,
                                               double[][] other_feature_values);

    /**
     * Throws an {@link UnsupportedOperationException} when called as this class
     * is for MEI features and not MIDI features.
     * @param sequence			The MIDI data to extract the feature
     *                                 from.
     * @param sequence_info		Additional data about the MIDI sequence.
     * @param other_feature_values     The values of other features that are
     *					needed to calculate this value. The
     *					order and offsets of these features
     *					must be the same as those returned by
     *					this class's getDependencies and
     *					getDependencyOffsets methods
     *                                 respectively.The first indice indicates
     *                                 the feature/window and the second
     *                                 indicates the value.
     * @return Nothing returned as this functions is not supported for MEI features.
     * @throws UnsupportedOperationException Thrown all the time since this function is not supported
     * for MEI feature extraction.
     */
    @Override
    public double[] extractFeature( Sequence sequence,
                                    MIDIIntermediateRepresentations sequence_info,
                                    double[][] other_feature_values )
            throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Unsupported Operation Exception: " +
                "extractFeature method is only valid for MIDI features, not valid for MEI feature. Please" +
                " use extractMeiFeature method for MEI features.");
    }
}
