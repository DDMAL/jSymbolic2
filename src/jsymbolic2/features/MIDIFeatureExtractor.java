/*
 * MIDIFeatureExtractor.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;


/**
 * The prototype for feature extractors. Each class that extends this class
 * will extract a particular feature from a window of MIDI data. Such
 * classes do not store feature values, only extract them.
 *
 * <p>Classes that extend this class should have a constructor that sets the
 * three protected fields of this class.
 *
 * @author Cory McKay
 */
public abstract class MIDIFeatureExtractor
{
     /* FIELDS ****************************************************************/
     /**
      * Meta-data describing a feature.
      */
     protected	FeatureDefinition  definition;
     
     
     /**
      * The names of other features that are needed in order for a feature
      * to be calculated. Will be null if there are no dependencies.
      */
     protected	String[]           dependencies;
     
     
     /**
      * The offset in windows of each of the features named in the
      * dependencies field. An offset of -1, for example, means that the
      * feature in dependencies with the same indice should be
      * provided to this class's extractFeature method with a value that
      * corresponds to the window prior to the window corresponding to
      * this feature. Will be null if there are no dependencies. This
      * must be null, 0 or a negative number. Positive numbers are not
      * allowed.
      */
     protected	int[]              offsets;
     
     
     
     /* PUBLIC METHODS ********************************************************/
     
     /**
      * Returns meta-data describing this feature.
      *
      * <p><b>IMPORTANT:</b> Note that a value of 0 in the returned dimensions
      * of the FeatureDefinition implies that the feature dimensions are
      * variable, and depend on the analyzed data.
      * @return The definition of this particular feature.
      */
     public FeatureDefinition getFeatureDefinition()
     {
          return definition;
     }
     
     
     /**
      * Returns the names of other features that are needed in order to extract
      * this feature. Will return null if no other features are needed.
      * @return The dependencies of this particular feature.
      */
     public String[] getDepenedencies()
     {
          return dependencies;
     }
     
     
     /**
      * Returns the offsets of other features that are needed in order to 
      * extract this feature. Will return null if no other features are needed.
      *
      * <p>The offset is in windows, and the indice of the retuned array
      * corresponds to the indice of the array returned by the getDependencies
      * method. An offset of -1, for example, means that the feature returned
      * by getDependencies with the same indice should be provided to this
      * class's extractFeature method with a value that corresponds to the
      * window prior to the window corresponding to this feature.
      * @return The dependcy offsets of this particular feature.
      */
     public int[] getDepenedencyOffsets()
     {
          return offsets;
     }
     
     
     /**
      * The prototype function that classes extending this class will
      * override in order to extract their feature from a window of MIDI data.
      *
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
      * @return				The extracted feature value(s).
      * @throws Exception		Throws an informative exception if the
      *					feature cannot be calculated.
      */
     public abstract double[] extractFeature( Sequence sequence,
          MIDIIntermediateRepresentations sequence_info,
          double[][] other_feature_values )
          throws Exception;
}
