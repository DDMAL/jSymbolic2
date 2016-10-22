package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jsymbolic2.features.*;

/**
 * This class collects all implemented MIDIFeatureExtractor classes (including MEIFeatureExtractor
 * extractors), orders them to match the specifications of the jSymbolic manual, and indicates which ones are
 * set to be extracted and saved by default. This allows for consistent access to implemented features,
 * regardless of whether the jSymbolic GUI, command line interface, API of configuration file is being used.
 *
 * <p> <b>IMPORTANT:</b>All newly implemented MIDIFeatureExtractor objects must be referenced in the
 * all_implemented_feature_extractors and default_features_to_save fields of this class.</p>
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public final class FeatureExtractorAccess
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * An array consisting of one instantiated MIDIFeatureExtractor object for each implemented feature
	 * extractor (including MEIFeatureExtractor features). These are ordered in the same order in which they
	 * are presented in the jSymbolic manual.
	 */
	private static final MIDIFeatureExtractor[] all_implemented_feature_extractors;

	/**
	 * An array with one entry for every feature implemented as a MIDIFeatureExtractor (including
	 * MEIFeatureExtractor features). These are ordered in the same order in which they are presented in the
	 * jSymbolic manual. Each entry is set to true if that feature is to be extracted and saved by default,
	 * and to false if is not.
	 */
	private static final boolean[] default_features_to_save;

	/**
	 * A List consisting of the feature names of every feature implemented as a MIDIFeatureExtractor
	 * (including MEIFeatureExtractor features). These are ordered in the same order in which they are
	 * presented in the jSymbolic manual.
	 */
	private static final List<String> names_of_all_implemented_features;

	/**
	 * A List consisting of the feature names of every feature implemented as a MIDIFeatureExtractor
	 * (including MEIFeatureExtractor features) and that has also been chosen to be saved by default. These
	 * are ordered in the same order in which they are presented in the jSymbolic manual.
	 */
	private static final List<String> names_of_default_features_to_save;

	
	/* STATIC INITIALIZATION BLOCK **************************************************************************/

	
	/**
	 * Initialize the fields of this class.
	 */
	static
	{
		all_implemented_feature_extractors = new MIDIFeatureExtractor[]
		{
			// Add features based on pitch statistics
			new BasicPitchHistogramFeature(),
			new PitchClassHistogramFeature(),
			new FoldedFifthsPitchClassHistogramFeature(),
			new PrevalenceOfMostCommonPitchFeature(),
			new PrevalenceOfMostCommonPitchClassFeature(),
			new RelativePrevalenceOfTopPitchesFeature(),
			new RelativePrevalenceOfTopPitchClassesFeature(),
			new IntervalBetweenMostPrevalenttPitchesFeature(),
			new IntervalBetweenMostPrevalentPitchClassesFeature(),
			new NumberOfCommonPitchesFeature(),
			new PitchVarietyFeature(),
			new PitchClassVarietyFeature(),
			new RangeFeature(),
			new MostCommonPitchFeature(),
			new MeanPitchFeature(),
			new ImportanceOfBassRegisterFeature(),
			new ImportanceOfMiddleRegisterFeature(),
			new ImportanceOfHighRegisterFeature(),
			new MostCommonPitchClassFeature(),
			new DominantSpreadFeature(),
			new StrongTonalCentresFeature(),
			new MajorOrMinorFeature(),
			new GlissandoPrevalenceFeature(),
			new AverageRangeOfGlissandosFeature(),
			new VibratoPrevalenceFeature(),
			new MicrotonePrevalenceFeature(),
			
			// Add features based on melodic intervals
			new MelodicIntervalHistogramFeature(),
			new MostCommonMelodicIntervalFeature(),
			new MeanMelodicIntervalFeature(),
			new NumberOfCommonMelodicIntervalsFeature(),
			new DistanceBetweenMostPrevalentMelodicIntervalsFeature(),
			new PrevalenceOfMostCommonMelodicInterval(),
			new RelativePrevalenceOfMostCommonMelodicIntervals(),
			new AmountOfArpeggiationFeature(),
			new RepeatedNotesFeature(),
			new ChromaticMotionFeature(),
			new StepwiseMotionFeature(),
			new MelodicThirdsFeature(),
			new MelodicFifthsFeature(),
			new MelodicTritonesFeature(),
			new MelodicOctavesFeature(),
			new MelodicEmbellishmentsFeature(),
			new DirectionOfMotionFeature(),
			new AverageLengthOfMelodicArcsFeature(),
			new AverageIntervalSpannedByMelodicArcs(),
			new MelodicPitchVarietyFeature(),
			
			// Add features based on chords and vertical intervals
			new VerticalIntervalHistogramFeature(),
			new WrappedVerticalIntervalHistogramFeature(),
			new ChordTypeHistogramFeature(),
			new AverageNumberOfSimultaneousPitchClassesFeature(),
			new VariabilityOfNumberOfSimultaneousPitchClassesFeature(),
			new MostCommonVerticalIntervalFeature(),
			new SecondMostCommonVerticalIntervalFeature(),
			new DistanceBetweenTwoMostCommonVerticalIntervalsFeature(),
			new PrevalenceOfMostCommonVerticalIntervalFeature(),
			new PrevalenceOfSecondMostCommonVerticalIntervalFeature(),
			new RatioOfPrevalenceOfTwoMostCommonVerticalIntervalsFeature(),
			new UnisonsFeature(),
			new VerticalMinorSecondsFeature(),
			new VerticalThirdsFeature(),
			new VerticalTritonesFeature(),
			new VerticalFifthsFeature(),
			new VerticalOctavesFeature(),
			new FractionOfMinorVerticalIntervalsFeature(),
			new FractionOfMajorVerticalIntervalsFeature(),
			new PerfectVerticalIntervalsFeature(),
			new VerticalDissonanceRatioFeature(),
			new ChordDurationFeature(),
			new PartialChordsFeature(),
			new ComplexChordsFeature(),
			new StandardTriadsFeature(),
			new DiminishedAndAugmentedTriadsFeature(),
			new DominantSeventhChordsFeature(),
			new SeventhChordsFeature(),
			new NonStandardChordsFeature(),
			new MinorMajorTriadRatioFeature(),
			
			// Add features based on rhythm
			new BeatHistogramFeature(),
			new StrongestRhythmicPulseFeature(),
			new SecondStrongestRhythmicPulseFeature(),
			new HarmonicityOfTwoStrongestRhythmicPulsesFeature(),
			new StrengthOfStrongestRhythmicPulseFeature(),
			new StrengthOfSecondStrongestRhythmicPulseFeature(),
			new StrengthRatioOfTwoStrongestRhythmicPulsesFeature(),
			new CombinedStrengthOfTwoStrongestRhythmicPulsesFeature(),
			new NumberOfStrongRhythmicPulsesFeature(),
			new NumberOfModerateRhythmicPulsesFeature(),
			new NumberOfRelativelyStrongRhythmicPulsesFeature(),
			new RhythmicLoosenessFeature(),
			new PolyrhythmsFeature(),
			new RhythmicVariabilityFeature(),
			new NoteDensityFeature(),
			new NoteDensityVariabilityFeature(),
			new AverageNoteDurationFeature(),
			new VariabilityOfNoteDurationsFeature(),
			new MaximumNoteDurationFeature(),
			new MinimumNoteDurationFeature(),
			new AmountOfStaccatoFeature(),
			new AverageTimeBetweenAttacksFeature(),
			new VariabilityOfTimeBetweenAttacksFeature(),
			new AverageTimeBetweenAttacksForEachVoiceFeature(),
			new AverageVariabilityOfTimeBetweenAttacksForEachVoiceFeature(),
			new CompleteRestsFeature(),
			new LongestCompleteRestFeature(),
			new AverageRestFractionPerVoiceFeature(),
			new VariabilityAcrossVoicesOfTotalRestsPerVoice(),
			new InitialTempoFeature(),
			new InitialTimeSignatureFeature(),
			new CompoundOrSimpleMeterFeature(),
			new TripleMeterFeature(),
			new QuintupleMeterFeature(),
			new MetricalDiversity(),
			new DurationFeature(),
			
			// Add features based on instrumentation
			new PitchedInstrumentsPresentFeature(),
			new UnpitchedInstrumentsPresentFeature(),
			new NotePrevalenceOfPitchedInstrumentsFeature(),
			new NotePrevalenceOfUnpitchedInstrumentsFeature(),
			new TimePrevalenceOfPitchedInstrumentsFeature(),
			new VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature(),
			new VariabilityOfNotePrevalenceOfUnpitchedInstrumentsFeature(),
			new NumberOfPitchedInstrumentsFeature(),
			new NumberOfUnpitchedInstrumentsFeature(),
			new PercussionInstrumentPrevalenceFeature(),
			new StringKeyboardPrevalenceFeature(),
			new AcousticGuitarPrevalenceFeature(),
			new ElectricGuitarPrevalenceFeature(),
			new ViolinPrevalenceFeature(),
			new SaxophonePrevalenceFeature(),
			new BrassPrevalenceFeature(),
			new WoodwindsPrevalenceFeature(),
			new OrchestralStringsPrevalenceFeature(),
			new StringEnsemblePrevalenceFeature(),
			new ElectricInstrumentPrevalenceFeature(),
			
			// Add features based on musical texture
			new MaximumNumberOfIndependentVoicesFeature(),
			new AverageNumberOfIndependentVoicesFeature(),
			new VariabilityOfNumberOfIndependentVoicesFeature(),
			new VoiceEqualityNumberOfNotesFeature(),
			new VoiceEqualityNoteDurationFeature(),
			new VoiceEqualityDynamicsFeature(),
			new VoiceEqualityMelodicLeapsFeature(),
			new VoiceEqualityRangeFeature(),
			new ImportanceOfLoudestVoiceFeature(),
			new RelativeRangeOfLoudestVoiceFeature(),
			new RelativeRangeIsolationOfLoudestVoiceFeature(),
			new RelativeRangeOfHighestLineFeature(),
			new RelativeNoteDensityOfHighestLineFeature(),
			new RelativeNoteDurationsOfLowestLineFeature(),
			new RelativeSizeOfMelodicIntervalsInLowestLineFeature(),
			new SimultaneityFeature(),
			new VariabilityOfSimultaneityFeature(),
			new VoiceOverlapFeature(),
			new ParallelMotionFeature(),
			new VoiceSeparationFeature(),
			
			// Add features based on dynamics
			new DynamicRangeFeature(),
			new VariationOfDynamicsFeature(),
			new VariationOfDynamicsInEachVoiceFeature(),
			new AverageNoteToNoteChangeInDynamics(),
			
			// Add MEI-specific features
			new NumberOfGraceNotesMeiFeature(),
			new NumberOfSlursMeiFeature()
		};

		default_features_to_save = new boolean[]
		{
			// Features based on pitch statistics
			false, // BasicPitchHistogramFeature
			false, // PitchClassHistogramFeature
			false, // FoldedFifthsPitchClassHistogramFeature
			true, // PrevalenceOfMostCommonPitchFeature
			true, // PrevalenceOfMostCommonPitchClassFeature
			true, // RelativePrevalenceOfTopPitchesFeature
			true, // RelativePrevalenceOfTopPitchClassesFeature
			true, // IntervalBetweenMostPrevalenttPitchesFeature
			true, // IntervalBetweenMostPrevalentPitchClassesFeature
			true, // NumberOfCommonPitchesFeature
			true, // PitchVarietyFeature
			true, // PitchClassVarietyFeature
			true, // RangeFeature
			true, // MostCommonPitchFeature
			true, // MeanPitchFeature
			true, // ImportanceOfBassRegisterFeature
			true, // ImportanceOfMiddleRegisterFeature
			true, // ImportanceOfHighRegisterFeature
			true, // MostCommonPitchClassFeature
			true, // DominantSpreadFeature
			true, // StrongTonalCentresFeature
			true, // MajorOrMinorFeature
			true, // GlissandoPrevalenceFeature
			true, // AverageRangeOfGlissandosFeature
			true, // VibratoPrevalenceFeature
			true, // MicrotonePrevalenceFeature

			// Features based on melodic intervals
			false, // MelodicIntervalHistogramFeature
			true, // MostCommonMelodicIntervalFeature
			true, // MeanMelodicIntervalFeature
			true, // NumberOfCommonMelodicIntervalsFeature
			true, // DistanceBetweenMostPrevalentMelodicIntervalsFeature
			true, // PrevalenceOfMostCommonMelodicInterval
			true, // RelativePrevalenceOfMostCommonMelodicIntervals
			true, // AmountOfArpeggiationFeature
			true, // RepeatedNotesFeature
			true, // ChromaticMotionFeature
			true, // StepwiseMotionFeature
			true, // MelodicThirdsFeature
			true, // MelodicFifthsFeature
			true, // MelodicTritonesFeature
			true, // MelodicOctavesFeature
			true, // MelodicEmbellishmentsFeature
			true, // DirectionOfMotionFeature
			true, // AverageLengthOfMelodicArcsFeature
			true, // AverageIntervalSpannedByMelodicArcs
			true, // MelodicPitchVarietyFeature

			// Features based on chords and vertical intervals
			false, // VerticalIntervalHistogramFeature
			false, // WrappedVerticalIntervalHistogramFeature
			false, // ChordTypeHistogramFeature			
			true, // AverageNumberOfSimultaneousPitchClassesFeature
			true, // VariabilityOfNumberOfSimultaneousPitchClassesFeature
			true, // MostCommonVerticalIntervalFeature
			true, // SecondMostCommonVerticalIntervalFeature
			true, // DistanceBetweenTwoMostCommonVerticalIntervalsFeature
			true, // PrevalenceOfMostCommonVerticalIntervalFeature
			true, // PrevalenceOfSecondMostCommonVerticalIntervalFeature
			true, // RatioOfPrevalenceOfTwoMostCommonVerticalIntervalsFeature
			true, // UnisonsFeature
			true, // VerticalMinorSecondsFeature
			true, // VerticalThirdsFeature
			true, // VerticalTritonesFeature
			true, // VerticalFifthsFeature
			true, // VerticalOctavesFeature
			true, // FractionOfMinorVerticalIntervalsFeature
			true, // FractionOfMajorVerticalIntervalsFeature
			true, // PerfectVerticalIntervalsFeature
			true, // VerticalDissonanceRatioFeature
			true, // ChordDurationFeature
			true, // PartialChordsFeature
			true, // ComplexChordsFeature
			true, // StandardTriadsFeature
			true, // DiminishedAndAugmentedTriadsFeature
			true, // DominantSeventhChordsFeature
			true, // SeventhChordsFeature
			true, // NonStandardChordsFeature
			true, // MinorMajorTriadRatioFeature

			// Features based on rhythm
			false, // BeatHistogramFeature
			true, // StrongestRhythmicPulseFeature
			true, // SecondStrongestRhythmicPulseFeature
			true, // HarmonicityOfTwoStrongestRhythmicPulsesFeature
			true, // StrengthOfStrongestRhythmicPulseFeature
			true, // StrengthOfSecondStrongestRhythmicPulseFeature
			true, // StrengthRatioOfTwoStrongestRhythmicPulsesFeature
			true, // CombinedStrengthOfTwoStrongestRhythmicPulsesFeature
			true, // NumberOfStrongRhythmicPulsesFeature			
			true, // NumberOfModerateRhythmicPulsesFeature
			true, // NumberOfRelativelyStrongRhythmicPulsesFeature
			true, // RhythmicLoosenessFeature
			true, // PolyrhythmsFeature
			true, // RhythmicVariabilityFeature
			true, // NoteDensityFeature
			true, // NoteDensityVariabilityFeature
			true, // AverageNoteDurationFeature
			true, // VariabilityOfNoteDurationsFeature
			true, // MaximumNoteDurationFeature
			true, // MinimumNoteDurationFeature
			true, // AmountOfStaccatoFeature
			true, // AverageTimeBetweenAttacksFeature
			true, // VariabilityOfTimeBetweenAttacksFeature
			true, // AverageTimeBetweenAttacksForEachVoiceFeature
			true, // AverageVariabilityOfTimeBetweenAttacksForEachVoiceFeature
			true, // CompleteRestsFeature
			true, // LongestCompleteRestFeature
			true, // AverageRestFractionPerVoiceFeature
			true, // VariabilityAcrossVoicesOfTotalRestsPerVoiceFeature
			true, // InitialTempoFeature
			false, // InitialTimeSignatureFeature
			true, // CompoundOrSimpleMeterFeature
			true, // TripleMeterFeature
			true, // QuintupleMeterFeature
			true, // MetricalDiversityFeature
			true, // DurationFeature

			// Fatures based on instrumentation
			false, // PitchedInstrumentsPresentFeature
			false, // UnpitchedInstrumentsPresentFeature
			false, // NotePrevalenceOfPitchedInstrumentsFeature
			false, // NotePrevalenceOfUnpitchedInstrumentsFeature
			false, // TimePrevalenceOfPitchedInstrumentsFeature
			true, // VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature
			true, // VariabilityOfNotePrevalenceOfUnpitchedInstrumentsFeature			
			true, // NumberOfPitchedInstrumentsFeature
			true, // NumberOfUnpitchedInstrumentsFeature
			true, // PercussionInstrumentPrevalenceFeature
			true, // StringKeyboardPrevalenceFeature
			true, // AcousticGuitarPrevalenceFeature
			true, // ElectricGuitarPrevalenceFeature
			true, // ViolinPrevalenceFeature
			true, // SaxophonePrevalenceFeature
			true, // BrassPrevalenceFeature
			true, // WoodwindsPrevalenceFeature
			true, // OrchestralStringsPrevalenceFeature
			true, // StringEnsemblePrevalenceFeature
			true, // ElectricInstrumentPrevalenceFeature

			// Features based on musical texture
			true, // MaximumNumberOfIndependentVoicesFeature
			true, // AverageNumberOfIndependentVoicesFeature
			true, // VariabilityOfNumberOfIndependentVoicesFeature
			true, // VoiceEqualityNumberOfNotesFeature
			true, // VoiceEqualityNoteDurationFeature
			true, // VoiceEqualityDynamicsFeature
			true, // VoiceEqualityMelodicLeapsFeature
			true, // VoiceEqualityRangeFeature
			true, // ImportanceOfLoudestVoiceFeature
			true, // RelativeRangeOfLoudestVoiceFeature
			true, // RelativeRangeIsolationOfLoudestVoiceFeature
			true, // RelativeRangeOfHighestLineFeature
			true, // RelativeNoteDensityOfHighestLineFeature
			true, // RelativeNoteDurationsOfLowestLineFeature
			true, // RelativeSizeOfMelodicIntervalsInLowestLineFeature
			true, // SimultaneityFeature
			true, // VariabilityOfSimultaneityFeature
			true, // VoiceOverlapFeature
			true, // ParallelMotionFeature
			true, // VoiceSeparationFeature

			// Features based on dynamics
			true, // DynamicRangeFeature
			true, // VariationOfDynamicsFeature
			true, // VariationOfDynamicsInEachVoiceFeature
			true, // AverageNoteToNoteChangeInDynamicsFeature

			// MEI-specific features
			false, // NumberOfGraceNotesFeature
			false // NumberOfSlurNotesFeature
		};

		names_of_all_implemented_features = new ArrayList<>();
		List<MIDIFeatureExtractor> all_extractors = Arrays.asList(all_implemented_feature_extractors);
		for (MIDIFeatureExtractor fe : all_extractors)
			names_of_all_implemented_features.add(fe.getFeatureDefinition().name);

		names_of_default_features_to_save = new ArrayList<>();
		for (int i = 0; i < default_features_to_save.length; i++)
			if (default_features_to_save[i] == true)
				names_of_default_features_to_save.add(names_of_all_implemented_features.get(i));
	}
	

	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * @return	An array consisting of one instantiated MIDIFeatureExtractor object for each implemented
	 *			feature extractor (including MEIFeatureExtractor features). These are ordered in the same 
	 *			order in which they are presented in the jSymbolic manual.
	 */
	public static MIDIFeatureExtractor[] getAllImplementedFeatureExtractors()
	{
		return all_implemented_feature_extractors;
	}

	/**
	 * @return	An array with one entry for every feature implemented as a MIDIFeatureExtractor (including 
	 *			MEIFeatureExtractor features). These are ordered in the same order in which they are presented
	 *			in the jSymbolic manual. Each entry is set to true if that feature is to be extracted and
	 *			saved by default, and to false if is not.
	 */
	public static boolean[] getDefaultFeaturesToSave()
	{
		return default_features_to_save;
	}

	/**
	 * @return	A List consisting of the feature names of every feature implemented as a MIDIFeatureExtractor
	 *			(including MEIFeatureExtractor features). These are ordered in the same order in which they 
	 *			are presented in the jSymbolic manual.
	 */
	public static List<String> getNamesOfAllImplementedFeatures()
	{
		return names_of_all_implemented_features;
	}

	/**
	 * @return	A List consisting of the feature names of every feature implemented as a MIDIFeatureExtractor
	 *			(including MEIFeatureExtractor features) and that has also been chosen to be saved by default.
	 *			These are ordered in the same order in which they are presented in the jSymbolic manual.
	 */
	public static List<String> getNamesOfDefaultFeaturesToSave()
	{
		return names_of_default_features_to_save;
	}
}