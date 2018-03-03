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
 * all_implemented_feature_extractors and default_features_to_save fields of this class. Automatic error
 * checking is performed on all_implemented_feature_extractors and default_features_to_save at instantiation,
 * and potential problems are printed to standard error.</p>
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

	/**
	 * A List consisting of the feature names of every feature implemented as an MEIeatureExtractor. These
	 * are ordered in the same order in which they are presented in the jSymbolic manual.
	 */
	private static final List<String> names_of_mei_specific_features;

	
	/* STATIC INITIALIZATION BLOCK **************************************************************************/

	
	/**
	 * Initialize the fields of this class. Print warnings about any potential initialization problems
	 * detected to standard error, along with an indication of problem severity.
	 */
	static
	{
		all_implemented_feature_extractors = new MIDIFeatureExtractor[]
		{
			// Add features based on pitch statistics
			new BasicPitchHistogramFeature(),
			new PitchClassHistogramFeature(),
			new FoldedFifthsPitchClassHistogramFeature(),
			new NumberOfPitchesFeature(),
			new NumberOfPitchClassesFeature(),
			new NumberOfCommonPitchesFeature(),
			new NumberOfCommonPitchClassesFeature(),
			new RangeFeature(),
			new ImportanceOfBassRegisterFeature(),
			new ImportanceOfMiddleRegisterFeature(),
			new ImportanceOfHighRegisterFeature(),
			new DominantSpreadFeature(),
			new StrongTonalCentresFeature(),
			new MeanPitchFeature(),
			new MeanPitchClassFeature(),
			new MostCommonPitchFeature(),
			new MostCommonPitchClassFeature(),
			new PrevalenceOfMostCommonPitchFeature(),
			new PrevalenceOfMostCommonPitchClassFeature(),
			new RelativePrevalenceOfTopPitchesFeature(),
			new RelativePrevalenceOfTopPitchClassesFeature(),
			new IntervalBetweenMostPrevalenttPitchesFeature(),
			new IntervalBetweenMostPrevalentPitchClassesFeature(),
			new PitchVariabilityFeature(),
			new PitchClassVariabilityFeature(),
			new PitchClassVariabilityAfterFoldingFeature(),
			new PitchSkewnessFeature(),
			new PitchClassSkewnessFeature(),
			new PitchClassSkewnessAfterFoldingFeature(),
			new PitchKurtosisFeature(),
			new PitchClassKurtosisFeature(),
			new PitchClassKurtosisAfterFoldingFeature(),
			new MajorOrMinorFeature(),
			new FirstPitchFeature(),
			new FirstPitchClassFeature(),
			new LastPitchFeature(),
			new LastPitchClassFeature(),
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
			new MelodicPerfectFourthsFeature(),
			new MelodicTritonesFeature(),
			new MelodicPerfectFifthsFeature(),
			new MelodicSixthsFeature(),
			new MelodicSeventhsFeature(),
			new MelodicOctavesFeature(),
			new MelodicLargeIntervalsFeature(),
			new MinorMajorMelodicThirdlRatioFeature(),
			new MelodicEmbellishmentsFeature(),
			new DirectionOfMelodicMotionFeature(),
			new AverageLengthOfMelodicArcsFeature(),
			new AverageIntervalSpannedByMelodicArcs(),
			new MelodicPitchVarietyFeature(),
			
			// Add features based on chords and vertical intervals
			new VerticalIntervalHistogramFeature(),
			new WrappedVerticalIntervalHistogramFeature(),
			new ChordTypeHistogramFeature(),
			new AverageNumberOfSimultaneousPitchClassesFeature(),
			new VariabilityOfNumberOfSimultaneousPitchClassesFeature(),
			new AverageNumberOfSimultaneousPitchesFeature(),
			new VariabilityOfNumberOfSimultaneousPitchesFeature(),
			new MostCommonVerticalIntervalFeature(),
			new SecondMostCommonVerticalIntervalFeature(),
			new DistanceBetweenTwoMostCommonVerticalIntervalsFeature(),
			new PrevalenceOfMostCommonVerticalIntervalFeature(),
			new PrevalenceOfSecondMostCommonVerticalIntervalFeature(),
			new PrevalenceRatioOfTwoMostCommonVerticalIntervalsFeature(),
			new VerticalUnisonsFeature(),
			new VerticalMinorSecondsFeature(),
			new VerticalThirdsFeature(),
			new VerticalTritonesFeature(),
			new VerticalPerfectFourthsFeature(),
			new VerticalPerfectFifthsFeature(),
			new VerticalSixthsFeature(),
			new VerticalSeventhsFeature(),
			new VerticalOctavesFeature(),
			new PerfectVerticalIntervalsFeature(),
			new VerticalDissonanceRatioFeature(),
			new VerticalMinorThirdPrevalenceFeature(),
			new VerticalMajorThirdPrevalenceFeature(),
			new ChordDurationFeature(),
			new PartialChordsFeature(),
			new StandardTriadsFeature(),
			new DiminishedAndAugmentedTriadsFeature(),
			new DominantSeventhChordsFeature(),
			new SeventhChordsFeature(),
			new NonStandardChordsFeature(),
			new ComplexChordsFeature(),
			new MinorMajorTriadRatioFeature(),
			
			// Add features based on rhythm (that do NOT take tempo into account)
			new InitialTimeSignatureFeature(),
			new SimpleInitialMeterFeature(),
			new CompoundMeterFeature(),
			new ComplexInitialMeterFeature(),
			new DupleInitialMeterFeature(),
			new TripleInitialMeterFeature(),
			new QuadrupleInitialMeterFeature(),
			new MetricalDiversityFeature(),
			new TotalNumberOfNotesFeature(),
			new NoteDensityPerQuarterNoteFeature(),
			new NoteDensityPerQuarterNotePerVoiceFeature(),
			new NoteDensityPerQuarterNoteVariabilityFeature(),
			new RhythmicValueHistogramFeature(),
			new RangeOfRhythmicValuesFeature(),
			new NumberOfDifferentRhythmicValuesPresentFeature(),
			new NumberOfCommonRhythmicValuesPresentFeature(),
			new PrevalenceOfVeryShortRhythmicValuesFeature(),
			new PrevalenceOfShortRhythmicValuesFeature(),
			new PrevalenceOfMediumRhythmicValuesFeature(),
			new PrevalenceOfLongRhythmicValuesFeature(),
			new PrevalenceOfVeryLongRhythmicValuesFeature(),
			new PrevalenceOfDottedNotesFeature(),
			new ShortestRhythmicValueFeature(),
			new LongestRhythmicValueFeature(),
			new MeanRhythmicValueFeature(),
			new MostCommonRhythmicValueFeature(),
			new PrevalenceOfMostCommonRhythmicValueFeature(),
			new RelativePrevalenceOfMostCommonRhythmicValuesFeature(),
			new DifferenceBetweenMostCommonRhythmicValuesFeature(),
			new RhythmicValueVariabilityFeature(),
			new RhythmicValueSkewnessFeature(),
			new RhythmicValueKurtosisFeature(),
			new RhythmicValueMedianRunLengthsHistogramFeature(),
			new MeanRhythmicValueRunLengthFeature(),
			new MedianRhythmicValueRunLengthFeature(),
			new VariabilityInRhythmicValueRunLengthsFeature(),
			new RhythmicValueVariabilityInRunLengthsHistogramFeature(),
			new MeanRhythmicValueOffsetFeature(),
			new MedianRhythmicValueOffsetFeature(),
			new VariabilityOfRhythmicValueOffsetsFeature(),
			new CompleteRestsFractionFeature(),
			new PartialRestsFractionFeature(),
			new AverageRestFractionAcrossVoicesFeature(),
			new LongestCompleteRestFeature(),
			new LongestPartialRestFeature(),
			new MeanCompleteRestDurationFeature(),
			new MeanPartialRestDurationFeature(),
			new MedianCompleteRestDurationFeature(),
			new MedianPartialRestDurationFeature(),
			new VariabilityOfCompleteRestDurationsFeature(),
			new VariabilityOfPartialRestDurationsFeature(),
			new VariabilityAcrossVoicesOfCombinedRestsFeature(),
			new BeatHistogramTempoStandardizedFeature(),
			new NumberOfStrongRhythmicPulsesTempoStandardizedFeature(),
			new NumberOfModerateRhythmicPulsesTempoStandardizedFeature(),
			new NumberOfRelativelyStrongRhythmicPulsesTempoStandardizedFeature(),
			new StrongestRhythmicPulseTempoStandardizedFeature(),
			new SecondStrongestRhythmicPulseTempoStandardizedFeature(),
			new HarmonicityOfTwoStrongestRhythmicPulsesTempoStandardizedFeature(),
			new StrengthOfStrongestRhythmicPulseTempoStandardizedFeature(),
			new StrengthOfSecondStrongestRhythmicPulseTempoStandardizedFeature(),
			new StrengthRatioOfTwoStrongestRhythmicPulsesTempoStandardizedFeature(),
			new CombinedStrengthOfTwoStrongestRhythmicPulsesTempoStandardizedFeature(),
			new RhythmicVariabilityTempoStandardizedFeature(),
			new RhythmicLoosenessTempoStandardizedFeature(),
			new PolyrhythmsTempoStandardizedFeature(),
			
			// Add features based on rhythm (that DO take tempo into account)
			new InitialTempoFeature(),
			new MeanTempoFeature(),
			new TempoVariabilityFeature(),
			new DurationInSecondsFeature(),
			new NoteDensityFeature(),
			new NoteDensityVariabilityFeature(),
			new AverageTimeBetweenAttacksFeature(),
			new AverageTimeBetweenAttacksForEachVoiceFeature(),
			new VariabilityOfTimeBetweenAttacksFeature(),
			new AverageVariabilityOfTimeBetweenAttacksForEachVoiceFeature(),
			new MinimumNoteDurationFeature(),
			new MaximumNoteDurationFeature(),
			new AverageNoteDurationFeature(),
			new VariabilityOfNoteDurationsFeature(),
			new AmountOfStaccatoFeature(),
			new BeatHistogramFeature(),
			new NumberOfStrongRhythmicPulsesFeature(),
			new NumberOfModerateRhythmicPulsesFeature(),
			new NumberOfRelativelyStrongRhythmicPulsesFeature(),
			new StrongestRhythmicPulseFeature(),
			new SecondStrongestRhythmicPulseFeature(),
			new HarmonicityOfTwoStrongestRhythmicPulsesFeature(),
			new StrengthOfStrongestRhythmicPulseFeature(),
			new StrengthOfSecondStrongestRhythmicPulseFeature(),
			new StrengthRatioOfTwoStrongestRhythmicPulsesFeature(),
			new CombinedStrengthOfTwoStrongestRhythmicPulsesFeature(),
			new RhythmicVariabilityFeature(),
			new RhythmicLoosenessFeature(),
			new PolyrhythmsFeature(),
			
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
			new UnpitchedPercussionInstrumentPrevalenceFeature(),
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
			new VoiceOverlapFeature(),
			new VoiceSeparationFeature(),
			new VariabilityOfVoiceSeparationFeature(),
			new ParallelMotionFeature(),
			new SimilarMotionFeature(),
			new ContraryMotionFeature(),
			new ObliqueMotionFeature(),
			new ParallelFifthsFeature(),
			new ParallelOctavesFeature(),
			
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
			true, // NumberOfPitchesFeature
			true, // NumberOfPitchClassesFeature
			true, // NumberOfCommonPitchesFeature
			true, // NumberOfCommonPitchClassesFeature
			true, // RangeFeature
			true, // ImportanceOfBassRegisterFeature
			true, // ImportanceOfMiddleRegisterFeature
			true, // ImportanceOfHighRegisterFeature
			true, // DominantSpreadFeature
			true, // StrongTonalCentresFeature
			true, // MeanPitchFeature
			true, // MeanPitchClassFeature
			true, // MostCommonPitchFeature
			true, // MostCommonPitchClassFeature
			true, // PrevalenceOfMostCommonPitchFeature
			true, // PrevalenceOfMostCommonPitchClassFeature
			true, // RelativePrevalenceOfTopPitchesFeature
			true, // RelativePrevalenceOfTopPitchClassesFeature
			true, // IntervalBetweenMostPrevalenttPitchesFeature
			true, // IntervalBetweenMostPrevalentPitchClassesFeature
			true, // PitchVariabilityFeature
			true, // PitchClassVariabilityFeature
			true, // PitchClassVariabilityAfterFoldingFeature
			true, // PitchSkewnessFeature
			true, // PitchClassSkewnessFeature
			true, // PitchClassSkewnessAfterFoldingFeature
			true, // PitchKurtosisFeature
			true, // PitchClassKurtosisFeature
			true, // PitchClassKurtosisAfterFoldingFeature
			true, // MajorOrMinorFeature
			true, // FirstPitchFeature
			true, // FirstPitchClassFeature
			true, // LastPitchFeature
			true, // LastPitchClassFeature
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
			true, // MelodicPerfectFourthsFeature
			true, // MelodicTritonesFeature
			true, // MelodicPerfectFifthsFeature
			true, // MelodicSixthsFeature
			true, // MelodicSeventhsFeature
			true, // MelodicOctavesFeature
			true, // MelodicLargeIntervalsFeature
			true, // MinorMajorMelodicThirdlRatioFeature
			true, // MelodicEmbellishmentsFeature
			true, // DirectionOfMelodicMotionFeature
			true, // AverageLengthOfMelodicArcsFeature
			true, // AverageIntervalSpannedByMelodicArcs
			true, // MelodicPitchVarietyFeature

			// Features based on chords and vertical intervals
			false, // VerticalIntervalHistogramFeature
			false, // WrappedVerticalIntervalHistogramFeature
			false, // ChordTypeHistogramFeature			
			true, // AverageNumberOfSimultaneousPitchClassesFeature
			true, // VariabilityOfNumberOfSimultaneousPitchClassesFeature
			true, // AverageNumberOfSimultaneousPitchesFeature
			true, // VariabilityOfNumberOfSimultaneousPitchesFeature
			true, // MostCommonVerticalIntervalFeature
			true, // SecondMostCommonVerticalIntervalFeature
			true, // DistanceBetweenTwoMostCommonVerticalIntervalsFeature
			true, // PrevalenceOfMostCommonVerticalIntervalFeature
			true, // PrevalenceOfSecondMostCommonVerticalIntervalFeature
			true, // PrevalenceRatioOfTwoMostCommonVerticalIntervalsFeature
			true, // VerticalUnisonsFeature
			true, // VerticalMinorSecondsFeature
			true, // VerticalThirdsFeature
			true, // VerticalTritonesFeature
			true, // VerticalPerfectFourthsFeature
			true, // VerticalPerfectFifthsFeature
			true, // VerticalSixthsFeature
			true, // VerticalSeventhsFeature
			true, // VerticalOctavesFeature
			true, // PerfectVerticalIntervalsFeature
			true, // VerticalDissonanceRatioFeature
			true, // VerticalMinorThirdPrevalenceFeature
			true, // VerticalMajorThirdPrevalenceFeature
			true, // ChordDurationFeature
			true, // PartialChordsFeature
			true, // StandardTriadsFeature
			true, // DiminishedAndAugmentedTriadsFeature
			true, // DominantSeventhChordsFeature
			true, // SeventhChordsFeature
			true, // NonStandardChordsFeature
			true, // ComplexChordsFeature
			true, // MinorMajorTriadRatioFeature
			
			// Features based on rhythm (that do NOT take tempo into account)
			false, // InitialTimeSignatureFeature
			true, // SimpleInitialMeterFeature
			true, // CompoundMeterFeature
			true, // ComplexInitialMeterFeature
			true, // DupleInitialMeterFeature
			true, // TripleInitialMeterFeature
			true, // QuadrupleInitialMeterFeature
			true, // MetricalDiversityFeature
			true, // TotalNumberOfNotesFeature
			true, // NoteDensityPerQuarterNoteFeature
			true, // NoteDensityPerQuarterNotePerVoiceFeature
			true, // NoteDensityPerQuarterNoteVariabilityFeature
			false, // RhythmicValueHistogramFeature
			true, // RangeOfRhythmicValuesFeature
			true, // NumberOfDifferentRhythmicValuesPresentFeature
			true, // NumberOfCommonRhythmicValuesPresentFeature
			true, // PrevalenceOfVeryShortRhythmicValuesFeature
			true, // PrevalenceOfShortRhythmicValuesFeature
			true, // PrevalenceOfMediumRhythmicValuesFeature
			true, // PrevalenceOfLongRhythmicValuesFeature
			true, // PrevalenceOfVeryLongRhythmicValuesFeature
			true, // PrevalenceOfDottedNotesFeature
			true, // ShortestRhythmicValueFeature
			true, // LongestRhythmicValueFeature
			true, // MeanRhythmicValueFeature
			true, // MostCommonRhythmicValueFeature
			true, // PrevalenceOfMostCommonRhythmicValueFeature
			true, // RelativePrevalenceOfMostCommonRhythmicValuesFeature
			true, // DifferenceBetweenMostCommonRhythmicValuesFeature
			true, // RhythmicValueVariabilityFeature
			true, // RhythmicValueSkewnessFeature
			true, // RhythmicValueKurtosisFeature
			false, // RhythmicValueMedianRunLengthsHistogramFeature
			true, // MeanRhythmicValueRunLengthFeature
			true, // MedianRhythmicValueRunLengthFeature
			true, // VariabilityInRhythmicValueRunLengthsFeature
			false, // RhythmicValueVariabilityInRunLengthsHistogramFeature
			true, // MeanRhythmicValueOffsetFeature
			true, // MedianRhythmicValueOffsetFeature
			true, // VariabilityOfRhythmicValueOffsetsFeature
			true, // CompleteRestsFractionFeature
			true, // PartialRestsFractionFeature
			true, // AverageRestFractionAcrossVoicesFeature
			true, // LongestCompleteRestFeature
			true, // LongestPartialRestFeature
			true, // MeanCompleteRestDurationFeature
			true, // MeanPartialRestDurationFeature
			true, // MedianCompleteRestDurationFeature
			true, // MedianPartialRestDurationFeature
			true, // VariabilityOfCompleteRestDurationsFeature
			true, // VariabilityOfPartialRestDurationsFeature
			true, // VariabilityAcrossVoicesOfCombinedRestsFeature
			false, // BeatHistogramTempoStandardizedFeature
			true, // NumberOfStrongRhythmicPulsesTempoStandardizedFeature
			true, // NumberOfModerateRhythmicPulsesTempoStandardizedFeature
			true, // NumberOfRelativelyStrongRhythmicPulsesTempoStandardizedFeature
			true, // StrongestRhythmicPulseTempoStandardizedFeature
			true, // SecondStrongestRhythmicPulseTempoStandardizedFeature
			true, // HarmonicityOfTwoStrongestRhythmicPulsesTempoStandardizedFeature
			true, // StrengthOfStrongestRhythmicPulseTempoStandardizedFeature
			true, // StrengthOfSecondStrongestRhythmicPulseTempoStandardizedFeature
			true, // StrengthRatioOfTwoStrongestRhythmicPulsesTempoStandardizedFeature
			true, // CombinedStrengthOfTwoStrongestRhythmicPulsesTempoStandardizedFeature
			true, // RhythmicVariabilityTempoStandardizedFeature
			true, // RhythmicLoosenessTempoStandardizedFeature
			true, // PolyrhythmsTempoStandardizedFeature			
			
			// Features based on rhythm (that DO take tempo into account)
			true, // InitialTempoFeature
			true, // MeanTempoFeature
			true, // TempoVariabilityFeature
			true, // DurationInSecondsFeature
			true, // NoteDensityFeature
			true, // NoteDensityVariabilityFeature
			true, // AverageTimeBetweenAttacksFeature
			true, // AverageTimeBetweenAttacksForEachVoiceFeature
			true, // VariabilityOfTimeBetweenAttacksFeature
			true, // AverageVariabilityOfTimeBetweenAttacksForEachVoiceFeature
			true, // MinimumNoteDurationFeature
			true, // MaximumNoteDurationFeature
			true, // AverageNoteDurationFeature
			true, // VariabilityOfNoteDurationsFeature
			true, // AmountOfStaccatoFeature
			false, // BeatHistogramFeature
			true, // NumberOfStrongRhythmicPulsesFeature
			true, // NumberOfModerateRhythmicPulsesFeature
			true, // NumberOfRelativelyStrongRhythmicPulsesFeature
			true, // StrongestRhythmicPulseFeature
			true, // SecondStrongestRhythmicPulseFeature
			true, // HarmonicityOfTwoStrongestRhythmicPulsesFeature
			true, // StrengthOfStrongestRhythmicPulseFeature
			true, // StrengthOfSecondStrongestRhythmicPulseFeature
			true, // StrengthRatioOfTwoStrongestRhythmicPulsesFeature
			true, // CombinedStrengthOfTwoStrongestRhythmicPulsesFeature
			true, // RhythmicVariabilityFeature
			true, // RhythmicLoosenessFeature
			true, // PolyrhythmsFeature

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
			true, // UnpitchedPercussionInstrumentPrevalenceFeature
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
			true, // VoiceOverlapFeature
			true, // VoiceSeparationFeature
			true, // VariabilityOfVoiceSeparationFeature
			true, // ParallelMotionFeature
			true, // SimilarMotionFeature
			true, // ContraryMotionFeature
			true, // ObliqueMotionFeature
			true, // ParallelFifthsFeature
			true, // ParallelOctavesFeature
			
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

		names_of_mei_specific_features = new ArrayList<>();
		for (MIDIFeatureExtractor feature : all_implemented_feature_extractors)
		{
			if (feature instanceof MEIFeatureExtractor)
			{
				String mei_feature_name = feature.getFeatureDefinition().name;
				names_of_mei_specific_features.add(mei_feature_name);
			}
		}
		
		// Validation and testing
		printWarningReportIfFeaturesAddedImproperly();
		//printAllFeatures();
		//printAllFeatureDependencies();
		//System.out.println("DEFAULT FEATURES:/n" + getFeatureCatalogueOverviewReport(default_features_to_save));
	}
	

	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
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

	/**
	 * @return	A List consisting of the feature names of every feature implemented as an MEIFeatureExtractor.
	 * 			These are ordered in the same order in which they are presented in the jSymbolic manual.
	 */
	public static List<String> getNamesOfMeiSpecificFeatures()
	{
		return names_of_mei_specific_features;
	}
			
	/**
	 * Given a list of which features should be extracted, return the names of those features that are set to
	 * be extracted by this list.
	 * 
	 * @param features_to_extract	Which features should be extracted. This must correspond in size to the 
	 *								total number of features implemented, and must be ordered in the same 
	 *								order in which they are presented in the jSymbolic manual.
	 * @return						The names of the features to extract.
	 */
	public static List<String> getNamesOfFeaturesToExtract(boolean[] features_to_extract)
	{
		List<String> names_of_all_features = FeatureExtractorAccess.getNamesOfAllImplementedFeatures();
		List<String> feature_names_to_return = new ArrayList<>();
		for (int f = 0; f < names_of_all_features.size(); f++)
			if (features_to_extract[f])
				feature_names_to_return.add(names_of_all_features.get(f));
		return feature_names_to_return;
	}
		
	/**
	 * Take the specified list of feature names and return a boolean array indicating which amongst all the
	 * features jSymbolic can extract have their names included in the specified list.
	 *
	 * @param chosen_feature_names	A list of names of features that should be marked as true in the returned
	 *								array.
	 * @return						An array sized to match the complete list of features that jSymbolic
	 *								can extract, in the order that they are specified in the manual. A given
	 *								entry is set to true if the name of the corresponding feature is contained
	 *								in chosen_feature_names, and to false otherwise.
	 * @throws Exception			An informative Exception is thrown if one of the feature names in
	 *								chosen_feature_names does not correspond to the name of an implemented
	 *								feature.
	 */
	public static boolean[] findSpecifiedFeatures(List<String> chosen_feature_names)
			throws Exception
	{
		List<String> names_of_all_features = FeatureExtractorAccess.getNamesOfAllImplementedFeatures();
		boolean[] chosen_features = new boolean[names_of_all_features.size()];
		for (int i = 0; i < chosen_features.length; i++)
			chosen_features[i] = false;
		for (String this_feature_name : chosen_feature_names)
		{
			if (!names_of_all_features.contains(this_feature_name))
				throw new Exception(this_feature_name + " is not the name of a feature implemented in this version of jSymbolic.");
			int feature_index = names_of_all_features.lastIndexOf(this_feature_name);
			chosen_features[feature_index] = true;
		}
		return chosen_features;
	}
	
	/**
	 * Prepare a formatted report outlining statistics about the breakdown of all features, as well as of only
	 * those features selected as defaults.
     * 
     * @param   features_to_include An array corresponding in size and order to the contents of the
     *                              all_implemented_feature_extractors field of this class. Values of true
     *                              indicate that that feature should be included in this report, and false
     *                              indicate that it should not. If this is null, then a report is generated
     *                              for all implemented features.
	 * @return                      The formatted report.
	 */
	public static String getFeatureCatalogueOverviewReport(boolean[] features_to_include)
	{
        // Set to report on all feautures if no specific features are specified.
        if (features_to_include == null)
        {
            features_to_include = new boolean[all_implemented_feature_extractors.length];
            for (int i = 0; i < features_to_include.length; i++)
                features_to_include[i] = true;
        }
        
		// Information relating to feature dimensions
		int total_unique_features = 0;
        int total_feature_dimensions = 0;
		int total_one_dimensional_features = 0;
		int total_multi_dimensional_features = 0;
		
		// Information relating to sequential features
		int total_sequential_features = 0;
		
		// Information relating to feature types
		String[] feature_type_labels = {"Overall Pitch Statistics", "Melodic Intervals", "Chords and Vertical Intervals", "Rhythm", "Instrumentation", "Musical Texture", "Dynamics", "MEI-Specific"};
		int[] unique_features_by_type = new int[feature_type_labels.length];
		int[] total_dimensions_by_type = new int[feature_type_labels.length];
        for (int i = 0; i < feature_type_labels.length; i++)
        {
            unique_features_by_type[i] = 0;
            total_dimensions_by_type[i] = 0;
        }
		
		// Collect feature stats by going through features one by one
		for (int i = 0; i <  all_implemented_feature_extractors.length; i++)
		{
            if (features_to_include[i])
            {
                MIDIFeatureExtractor feat = all_implemented_feature_extractors[i];

                total_unique_features++;
                
                total_feature_dimensions += feat.getFeatureDefinition().dimensions;
                if (feat.getFeatureDefinition().dimensions == 1)
                    total_one_dimensional_features++;
                else total_multi_dimensional_features++;

                if (feat.getFeatureDefinition().is_sequential)
                    total_sequential_features++;

                char code = feat.getFeatureCode().charAt(0);
                switch (code)
                {
                    case 'P':
                        unique_features_by_type[0]++;
                        total_dimensions_by_type[0] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'M':
                        unique_features_by_type[1]++;
                        total_dimensions_by_type[1] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'C':
                        unique_features_by_type[2]++;
                        total_dimensions_by_type[2] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'R':
                        unique_features_by_type[3]++;
                        total_dimensions_by_type[3] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'I':
                        unique_features_by_type[4]++;
                        total_dimensions_by_type[4] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'T':
                        unique_features_by_type[5]++;
                        total_dimensions_by_type[5] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'D':
                        unique_features_by_type[6]++;
                        total_dimensions_by_type[6] += feat.getFeatureDefinition().dimensions;
                        break;
                    case 'S':
                        unique_features_by_type[7]++;
                        total_dimensions_by_type[7] += feat.getFeatureDefinition().dimensions;
                        break;
                }
            }
		}
		
		// Prepare the part of the report relating to all features
		String report = total_unique_features + " unique features\n";
		report += total_feature_dimensions + " combined feature dimensions\n";
		report += total_one_dimensional_features + " unique one-dimensional features\n";
		report += total_multi_dimensional_features + " unique multi-dimensional features\n";
		report += total_sequential_features + " sequential features\n";
		report += "Feature breakdown by type:\n";
		for (int i = 0; i < feature_type_labels.length; i++)
			report += "\t" + unique_features_by_type[i] + " unique " + feature_type_labels[i] + " features (" + total_dimensions_by_type[i] + " total dimensions)\n";

		// Return the report
		return report;
	}
	

	/* PRIVATE STATIC METHODS *******************************************************************************/
	
	
	/**
	 * Verify that the all_implemented_feature_extractors and default_features_to_save fields have been set
	 * up in ways that are individually self-consistent and compatible with one another. Print warnings about
	 * any potential problems detected to standard error, along with an indication of problem severity. This
	 * is a useful error checker to help make sure that new features added to extend jSymbolic have been added
	 * properly. The following potential issues are checked:
	 * 
	 * 1) Verify that no features have been added to all_implemented_feature_extractors that are dependent on 
	 * features  that have not themselves been added to all_implemented_feature_extractors.
	 * 2) Verify that no more than one feature with any given feature name has been added to 
	 * all_implemented_feature_extractors.
	 * 3) Verify that no more than one feature with any given feature code has been added to 
	 * all_implemented_feature_extractors.
	 * 4) Verify that all features have been added contiguously to all_implemented_feature_extractors, based 
	 * on their feature code groups and numbers, and that all feature codes are properly formatted.
	 * 5) Verify that all_implemented_feature_extractors and default_features_to_save are of the same size.
	 * 6) Verify that, by default, all multi-dimensional features have been set to not be extracted, that all
	 * MEI-specific features have been set to not be extracted, and that all other features have been set to
	 * be extracted.
	 */
	private static void printWarningReportIfFeaturesAddedImproperly()
	{
		// A report of problems encountered
		String problem_report = "";
				
		// The names of all features added, in the order they have been added
		String[] names_of_all_features_added = names_of_all_implemented_features.toArray(new String[names_of_all_implemented_features.size()]);

		// Verify that no features have been added to all_implemented_feature_extractors that are dependent on
		// features that have not themselves been added to all_implemented_feature_extractors
		for (int feat = 0; feat < all_implemented_feature_extractors.length; feat++)
		{
			String[] dependencies = all_implemented_feature_extractors[feat].getDepenedencies();
			if (dependencies != null)
			{
				for (int dep = 0; dep < dependencies.length; dep++)
				{
					boolean found = false;
					for (int name = 0; name < names_of_all_features_added.length; name++)
					{
						if (names_of_all_features_added[name].equals(dependencies[dep]))
						{
							found = true;
							break;
						}
					}
					if (!found)
						problem_report += "WARNING: The feature " + names_of_all_features_added[feat] + " has the following feature dependency: " + dependencies[dep] + ". This dependency has not been added to jSymbolic. This is a serious problem, as a feature cannot be extracted unless all of its dependencies have been added.\n";
				}
			}
		}		
		
		// Verify that no more than one feature with any given feature name has been added to
		// all_implemented_feature_extractors
		int[][] duplicate_names = mckay.utilities.staticlibraries.StringMethods.getIndexesOfDuplicateEntries(names_of_all_features_added);
		if (duplicate_names != null)
		{
			for (int i = 0; i < duplicate_names.length; i++)
			{
				String duplicated_feature_name = names_of_all_features_added[duplicate_names[i][0]];
				int number_of_occurrences = duplicate_names[i].length;
				problem_report += "WARNING: The feature " + duplicated_feature_name + " has been added to jSymbolic " + number_of_occurrences + " times. No feature should be added more than once. This is not a serious problem, but it could result in redundant feature extraction.\n";
			}
		}
		
		// Verify that no more than one feature with any given feature code has been added to
		// all_implemented_feature_extractors
		String[] all_feature_codes = new String[all_implemented_feature_extractors.length];
		for (int i = 0; i < all_feature_codes.length; i++)
			all_feature_codes[i] = all_implemented_feature_extractors[i].getFeatureCode();
		int[][] duplicate_codes = mckay.utilities.staticlibraries.StringMethods.getIndexesOfDuplicateEntries(all_feature_codes);
		if (duplicate_codes != null)
		{
			for (int i = 0; i < duplicate_codes.length; i++)
			{
				String duplicated_feature_code = all_implemented_feature_extractors[duplicate_codes[i][0]].getFeatureCode();
				int number_of_occurrences = duplicate_codes[i].length;
				problem_report += "WARNING: The feature code " + duplicated_feature_code + " has been added to jSymbolic in " + number_of_occurrences + " features. No feature code should be used more than once. This is not a serious problem, but it could result in confusion or redundant feature extraction.\n";
			}
		}
		
		// Verify that all features have been added contiguously to all_implemented_feature_extractors, based
		// on their feature code groups and numbers, and that all feature codes are properly formatted
		String last_group = "";
		int last_number = 0;
		for (int feat = 0; feat < all_implemented_feature_extractors.length; feat++)
		{
			try
			{
				String[] split_code = all_implemented_feature_extractors[feat].getFeatureCode().split("-");
				String this_group = split_code[0];
				int this_number = Integer.parseInt(split_code[1]);

				if (feat != 0)
				{
					if (this_group.equals(last_group))
					{
						if (this_number != (last_number + 1))
							problem_report += "WARNING: The feature " + all_implemented_feature_extractors[feat].getFeatureCode() + " has been added to jSymbolic out of sequence (its code does not numerically follow the previous feature in its group). This is not a serious problem, but it could result in confusion.\n";
					}
					else if (this_number != 1)
						problem_report += "WARNING: The feature " + all_implemented_feature_extractors[feat].getFeatureCode() + " has been added to jSymbolic out of sequence (a new feature group should be numbered as 0). This is not a serious problem, but it could result in confusion.\n";
				}

				last_group = this_group;
				last_number = this_number;
			}
			catch (Exception e)
			{
				problem_report += "WARNING: The feature " + all_implemented_feature_extractors[feat].getFeatureCode() + " has an improperly formatted code. The code should consist of one or more letters identifying the feature group the feature belongs to, followed by a hyphen, followed by the number of the feature within that group. For example, a code of I-7 would be appropriate for the seventh feature of the Instrumentation feature group. This is not a serious problem, but it could result in confusion.\n";
			}
		}
		
		// Verify that all_implemented_feature_extractors and default_features_to_save are of the same size.
		// Verify that, by default, all multi-dimensional features have been set to not be extracted, that all
		// MEI-specific features have been set to not be extracted, and that all other features have been set
		// to be extracted.
		if (default_features_to_save.length != all_implemented_feature_extractors.length)
			problem_report += "WARNING: " + all_implemented_feature_extractors.length + " features have been added to jSymbolic, but settings for " + default_features_to_save.length + " features have been specified as to which features are to be extracted and saved by default. These numbers should be identical. This is a serious problem, as a mismatch could cause jSymbolic to crash.\n";
		else for (int feat = 0; feat < all_implemented_feature_extractors.length; feat++)
		{
			if (!default_features_to_save[feat])
			{
				if ( all_implemented_feature_extractors[feat].getFeatureDefinition().dimensions == 1 &&
			         !(all_implemented_feature_extractors[feat] instanceof MEIFeatureExtractor) )
					problem_report += "WARNING: " + names_of_all_features_added[feat] + " is set to not be extracted and saved by default. Typically, all one-dimensional and non-MEI specific features such as this one are set to be saved by default. This is not a serious issue, but it might cause confusion.\n";
			}
			else
			{
				if (all_implemented_feature_extractors[feat].getFeatureDefinition().dimensions != 1)
					problem_report += "WARNING: " + names_of_all_features_added[feat] + " is set to be extracted and saved by default. Typically, multi-dimensional features such as this one are not set to be saved by default. This is not a serious issue, but it might cause confusion.\n";
				if (all_implemented_feature_extractors[feat] instanceof MEIFeatureExtractor)
					problem_report += "WARNING: " + names_of_all_features_added[feat] + " is set to be extracted and saved by default. Typically, MEI-specific features such as this one are not set to be saved by default. This is not a serious issue, but it might cause confusion.\n";
			}
		}		
		
		// Print error report if a problem was found
		if (!problem_report.isEmpty())
			System.err.println("\n" + problem_report + "\n");
	}
	
	/**
	 * Debugging method that prints the total number of implemented features, including the code and name of
	 * each feature, in the correct listed order.
	 */
	private static void printAllFeatures()
	{
		System.out.println("ALL " + all_implemented_feature_extractors.length + " IMPLEMENTED FEATURES:");
		for (int i = 0; i < all_implemented_feature_extractors.length; i++)
			System.out.println( (i+1) + ":\t" + all_implemented_feature_extractors[i].getFeatureCode() + "\t" +
			                    all_implemented_feature_extractors[i].definition.name);
	}
	
	/**
	 * Debugging method that identifies all features whose calculation depends directly on the use of another
	 * implemented feature, and lists what the dependencies are of each of these features.
	 */	
	private static void printAllFeatureDependencies()
	{
		System.out.println("FEATURES WHOSE CALCULATION DEPENDS ON OTHER FEATURES:");
		for (int i = 0; i < all_implemented_feature_extractors.length; i++)
		{
			if (all_implemented_feature_extractors[i].dependencies != null)
			{
				System.out.println(all_implemented_feature_extractors[i].getFeatureCode() + "\t" +
			                        all_implemented_feature_extractors[i].definition.name);
				for (int j = 0; j < all_implemented_feature_extractors[i].dependencies.length; j++)
					System.out.println("\tDEPENDS ON: " + all_implemented_feature_extractors[i].dependencies[j]);
			}
		}
	}
}