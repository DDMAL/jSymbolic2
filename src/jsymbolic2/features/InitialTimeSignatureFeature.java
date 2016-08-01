/*
 * InitialTimeSignatureFeature.java
 * Version 2.0
 *
 * Last modified on April 11, 2010.
 * McGill University
 */

package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;


/**
 * A feature exractor that finds a feature array with two elements. The first is
 * the numerator of the first occurring time signature and the second is the
 * denominator of the first occurring time signature. Both are set to 0 if no 
 * time signature is present.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class InitialTimeSignatureFeature
     extends MIDIFeatureExtractor
{
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Basic constructor that sets the definition and dependencies (and their
      * offsets) of this feature.
      */
     public InitialTimeSignatureFeature()
     {
          String name = "Initial Time Signature";
          String description = "A feature array with two elements. The first is the numerator\n" +
               "of the first occurring time signature and the second is the\n" +
               "denominator of the first occurring time signature. Both are set\n" +
               "to 0 if no time signature is present.";
          boolean is_sequential = true;
          int dimensions = 2;
          definition = new FeatureDefinition( name,
               description,
               is_sequential,
               dimensions );
          
          dependencies = null;
          
          offsets = null;
     }
     
     
     /* PUBLIC METHODS ********************************************************/
     
     
     /**
      * Extracts this feature from the given MIDI sequence given the other
      * feature values.
      *
      * <p>In the case of this feature, the other_feature_values parameters
      * are ignored.
      *
      * @param sequence			The MIDI sequence to extract the feature
      *                                 from.
      * @param sequence_info		Additional data about the MIDI sequence.
      * @param other_feature_values	The values of other features that are
      *					needed to calculate this value. The
      *					order and offsets of these features
      *					must be the same as those returned by
      *					this class's getDependencies and
      *					getDependencyOffsets methods
      *                                 respectively. The first indice indicates
      *                                 the feature/window and the second
      *                                 indicates the value.
      * @return				The extracted feature value(s).
      * @throws Exception		Throws an informative exception if the
      *					feature cannot be calculated.
      */
     public double[] extractFeature( Sequence sequence,
          MIDIIntermediateRepresentations sequence_info,
          double[][] other_feature_values )
          throws Exception
     {
          double[] result = null;
          if (sequence_info != null)
          {
               result = new double[2];
               
               if (((LinkedList) sequence_info.meta_data[1]).size() == 0)
               {
                    result[0] = 0.0;
                    result[1] = 0.0;
               }
               
               else
               {
                    Object[] numerators_objects = ((LinkedList) sequence_info.meta_data[1]).toArray();
                    double[] numerators = new double[numerators_objects.length];
                    for (int i = 0; i < numerators.length; i++)
                         numerators[i] = ((Integer) numerators_objects[i]).doubleValue();
                    Object[] denominators_objects = ((LinkedList) sequence_info.meta_data[2]).toArray();
                    double[] denominators = new double[denominators_objects.length];
                    for (int i = 0; i < denominators.length; i++)
                         denominators[i] = ((Integer) denominators_objects[i]).doubleValue();
                    
                    result[0] = numerators[0];
                    result[1] = denominators[0];
               }
          }
          return result;
     }
}