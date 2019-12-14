package jsymbolic2.featureutils;

import jsymbolic2.features.dynamics.*;
import jsymbolic2.features.instrumentation.*;
import jsymbolic2.features.meispecific.*;
import jsymbolic2.features.melodicintervals.*;
import jsymbolic2.features.pitchstatistics.*;
import jsymbolic2.features.rhythm.*;
import jsymbolic2.features.texture.*;
import jsymbolic2.features.verticalintervals.*;
import jsymbolic2.features.ngrams.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class collects all implemented MIDIFeatureExtractor classes (including MEIFeatureExtractor
 * extractors), orders them to match the specifications of the jSymbolic manual, and indicates which ones are
 * set to be extracted and saved by default and which ones can typically still be considered secure even if
 * improperly encoded symbolic files are being processed. This allows for consistent access to implemented 
 * features, regardless of whether the jSymbolic GUI, command line interface, API of configuration file is 
 * being used.
 *
 * <p><b>IMPORTANT:</b>All newly implemented MIDIFeatureExtractor objects must be referenced in the
 * all_implemented_feature_extractors field of this class. Automatic error checking is performed on
 * all_implemented_feature_extractors at instantiation, and potential problems are printed to standard error.
 * </p>
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
	 * and to false if it is not.
	 */
	private static final boolean[] default_features_to_save;
	
	/**
	 * An array with one entry for every feature implemented as a MIDIFeatureExtractor (including
	 * MEIFeatureExtractor features). These are ordered in the same order in which they are presented in the
	 * jSymbolic manual. Each entry is set to true if that feature is considered a secure feature, and to
	 * false if it is not.
	 */
	private static final boolean[] secure_features;
	
	/**
	 * An array with one entry for every feature implemented as a MIDIFeatureExtractor (including
	 * MEIFeatureExtractor features). These are ordered in the same order in which they are presented in the
	 * jSymbolic manual. Each entry is set to true if that feature has more than one dimension, and to
	 * false if it does not.
	 */
	private static final boolean[] multi_dimensional_features;
	
	/**
	 * An array with one entry for every feature implemented as a MIDIFeatureExtractor (including
	 * MEIFeatureExtractor features). These are ordered in the same order in which they are presented in the
	 * jSymbolic manual. Each entry is set to true if that feature is an MEI-specific feature, and to
	 * false if it is not.
	 */
	private static final boolean[] mei_specific_features;
	
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
	 * A List consisting of the feature names of every feature implemented as a MIDIFeatureExtractor
	 * (including MEIFeatureExtractor features) and that are considered to be secure features. These
	 * are ordered in the same order in which they are presented in the jSymbolic manual.
	 */
	private static final List<String> names_of_secure_features_to_save;

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
			new PitchClassHistogramUntransposedFeature(),
			new PitchClassHistogramTransposedFeature(),
			new FoldedFifthsPitchClassHistogramFeature(),
			new NumberOfPitchesFeature(),
			new NumberOfPitchClassesFeature(),
			new NumberOfCommonPitchesFeature(),
			new NumberOfCommonPitchClassesFeature(),
			new RangeFeature(),
			new LowestPitchFeature(),
			new HighestPitchFeature(),
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
			new KeySignatureDiversityFeature(),
			new PitchDeviationFromFirstKeySignatureFeature(),
			new PitchDeviationFromKeySignatureFeature(),
			new MajorOrMinorFeature(),
			new NumberOfFlatsInFirstKeySignatureFeature(),
			new NumberOfSharpsInFirstKeySignatureFeature(),
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
			new WrappedMelodicIntervalHistogramFeature(),
			new WrappedMelodicIntervalHistogramRisingIntervalsOnlyFeature(),
			new WrappedMelodicIntervalHistogramFallingIntervalsOnlyFeature(),
			new MostCommonMelodicIntervalFeature(),
			new MostCommonWrappedMelodicIntervalFeature(),
			new MostCommonRisingWrappedMelodicIntervalFeature(),
			new MostCommonFallingWrappedMelodicIntervalFeature(),
			new MeanMelodicIntervalFeature(),
			new MeanWrappedMelodicIntervalFeature(),
			new MeanRisingWrappedMelodicIntervalFeature(),
			new MeanFallingWrappedMelodicIntervalFeature(),
			new NumberOfMelodicIntervalsFeature(),
			new NumberOfWrappedMelodicIntervalsFeature(),
			new NumberOfRisingWrappedMelodicIntervalsFeature(),
			new NumberOfFallingWrappedMelodicIntervalsFeature(),
			new NumberOfCommonMelodicIntervalsFeature(),
			new NumberOfCommonWrappedMelodicIntervalsFeature(),
			new NumberOfCommonRisingWrappedMelodicIntervalsFeature(),
			new NumberOfCommonFallingWrappedMelodicIntervalsFeature(),
			new NumberOfVeryCommonMelodicIntervalsFeature(),
			new NumberOfVeryCommonWrappedMelodicIntervalsFeature(),
			new NumberOfVeryCommonRisingWrappedMelodicIntervalsFeature(),
			new NumberOfVeryCommonFallingWrappedMelodicIntervalsFeature(),
			new DistanceBetweenMostPrevalentMelodicIntervalsFeature(),
			new DistanceBetweenMostPrevalentWrappedMelodicIntervalsFeature(),
			new DistanceBetweenMostPrevalentRisingWrappedMelodicIntervalsFeature(),
			new DistanceBetweenMostPrevalentFallingWrappedMelodicIntervalsFeature(),
			new PrevalenceOfMostCommonMelodicIntervalFeature(),
			new PrevalenceOfMostCommonWrappedMelodicIntervalFeature(),
			new PrevalenceOfMostCommonRisingWrappedMelodicIntervalFeature(),
			new PrevalenceOfMostCommonFallingWrappedMelodicIntervalFeature(),
			new RelativePrevalenceOfMostCommonMelodicIntervalsFeature(),
			new RelativePrevalenceOfMostCommonWrappedMelodicIntervalsFeature(),
			new RelativePrevalenceOfMostCommonRisingWrappedMelodicIntervalsFeature(),
			new RelativePrevalenceOfMostCommonFallingWrappedMelodicIntervalsFeature(),
			new OverallMelodicVariabilityFeature(),
			new OverallWrappedMelodicVariabilityFeature(),
			new OverallRisingWrappedMelodicVariabilityFeature(),
			new OverallFallingWrappedMelodicVariabilityFeature(),
			new OverallMelodicSkewnessFeature(),
			new OverallWrappedMelodicSkewnessFeature(),
			new OverallMelodicKurtosisFeature(),
			new OverallWrappedMelodicKurtosisFeature(),
			new SmallestMelodicIntervalFeature(),
			new SmallestRisingMelodicIntervalFeature(),
			new SmallestFallingMelodicIntervalFeature(),
			new LargestMelodicIntervalFeature(),
			new LargestWrappedMelodicIntervalFeature(),
			new LargestRisingMelodicIntervalFeature(),
			new LargestFallingMelodicIntervalFeature(),
			new RepeatedNotesFeature(),
			new ChromaticMotionFeature(),
			new StepwiseMotionFeature(),
			new MelodicThirdsFeature(),
			new MelodicIntervalsLargerThanAThirdFeature(),
			new MelodicPerfectFourthsFeature(),
			new MelodicTritonesFeature(),
			new MelodicPerfectFifthsFeature(),
			new MelodicIntervalsLargerThanAFifthFeature(),
			new MelodicSixthsFeature(),
			new MelodicSeventhsFeature(),
			new MelodicOctavesFeature(),
			new MelodicIntervalsLargerThanAnOctaveFeature(),
			new MinorMajorMelodicThirdRatioFeature(),
			new MelodicDissonanceRatioFourthsNotDissonantFeature(),
			new MelodicDissonanceRatioFourthsDissonantFeature(),
			new AmountOfArpeggiationFeature(),
			new MelodicEmbellishmentsOneSidedFeature(),
			new MelodicEmbellishmentsTwoSidedFeature(),
			new AmountOfRisingMelodicMotionFeature(),
			new AmountOfFallingMelodicMotionFeature(),
			new AverageLengthOfRisingMelodicRunsFeature(),
			new AverageLengthOfFallingMelodicRunsFeature(),
			new VariabilityInLengthOfRisingMelodicRunsFeature(),
			new VariabilityInLengthOfFallingMelodicRunsFeature(),
			new AverageIntervalSpannedByRisingMelodicRunsFeature(),
			new AverageIntervalSpannedByFallingMelodicRunsFeature(),
			new VariabilityInIntervalSpannedByRisingMelodicRunsFeature(),
			new VariabilityInIntervalSpannedByFallingMelodicRunsFeature(),
			new AverageLengthOfMelodicArcsFeature(),
			new AverageLengthOfMelodicHalfArcsFeature(),
			new VariabilityInLengthOfMelodicArcsFeature(),
			new VariabilityInLengthOfMelodicHalfArcsFeature(),
			new AverageIntervalSpannedByMelodicArcsFeature(),
			new AverageIntervalSpannedByMelodicHalfArcsFeature(),
			new VariabilityOfIntervalSpannedByMelodicArcsFeature(),
			new VariabilityOfIntervalSpannedByMelodicHalfArcsFeature(),
			new MelodicPitchVarietyFeature(),
			new WrappedMelodicIntervalHistogramForHighestLineFeature(),
			new WrappedMelodicIntervalHistogramForHighestLineRisingIntervalsOnlyFeature(),
			new WrappedMelodicIntervalHistogramForHighestLineFallingIntervalsOnlyFeature(),
			new MelodicNoteDensityPerQuarterNoteInHighestLineFeature(),
			new NumberOfDistinctMelodicIntervalsInHighestLineFeature(),
			new MeanMelodicIntervalOfHighestLineFeature(),
			new PrevalenceOfMostCommonMelodicIntervalInHighestLineFeature(),
			new MelodicVariabilityOfHighestLineFeature(),
			new SmallestMelodicIntervalInHighestLineFeature(),
			new LargestMelodicIntervalInHighestLineFeature(),
			new AmountOfFallingMelodicMotionInHighestLineFeature(),
			new AverageLengthOfRisingMelodicRunsInHighestLineFeature(),
			new AverageLengthOfFallingMelodicRunsInHighestLineFeature(),
			new VariabilityInLengthOfRisingMelodicRunsInHighestLineFeature(),
			new VariabilityInLengthOfFallingMelodicRunsInHighestLineFeature(),
			new AverageIntervalSpannedByRisingMelodicRunsInHighestLineFeature(),
			new AverageIntervalSpannedByFallingMelodicRunsInHighestLineFeature(),
			new VariabilityInIntervalSpannedByRisingMelodicRunsInHighestLineFeature(),
			new VariabilityInIntervalSpannedByFallingMelodicRunsInHighestLineFeature(),
			new AverageLengthOfMelodicArcsInHighestLineFeature(),
			new AverageLengthOfMelodicHalfArcsInHighestLineFeature(),
			new VariabilityInLengthOfMelodicArcsInHighestLineFeature(),
			new VariabilityInLengthOfMelodicHalfArcsInHighestLineFeature(),
			new AverageIntervalSpannedByMelodicArcsInHighestLineFeature(),
			new AverageIntervalSpannedByMelodicHalfArcsInHighestLineFeature(),
			new VariabilityOfIntervalSpannedByMelodicArcsInHighestLineFeature(),
			new VariabilityOfIntervalSpannedByMelodicHalfArcsInHighestLineFeature(),
			new MelodicPitchVarietyInHighestLineFeature(),
			new WrappedMelodicIntervalHistogramForLowestLineFeature(),
			new WrappedMelodicIntervalHistogramForLowestLineRisingIntervalsOnlyFeature(),
			new WrappedMelodicIntervalHistogramForLowestLineFallingIntervalsOnlyFeature(),
			new MelodicNoteDensityPerQuarterNoteInLowestLineFeature(),
			new NumberOfDistinctMelodicIntervalsInLowestLineFeature(),
			new MeanMelodicIntervalOfLowestLineFeature(),
			new PrevalenceOfMostCommonMelodicIntervalInLowestLineFeature(),
			new MelodicVariabilityOfLowestLineFeature(),
			new SmallestMelodicIntervalInLowestLineFeature(),
			new LargestMelodicIntervalInLowestLineFeature(),
			new AmountOfFallingMelodicMotionInLowestLineFeature(),
			new AverageLengthOfRisingMelodicRunsInLowestLineFeature(),
			new AverageLengthOfFallingMelodicRunsInLowestLineFeature(),
			new VariabilityInLengthOfRisingMelodicRunsInLowestLineFeature(),
			new VariabilityInLengthOfFallingMelodicRunsInLowestLineFeature(),
			new AverageIntervalSpannedByRisingMelodicRunsInLowestLineFeature(),
			new AverageIntervalSpannedByFallingMelodicRunsInLowestLineFeature(),
			new VariabilityInIntervalSpannedByRisingMelodicRunsInLowestLineFeature(),
			new VariabilityInIntervalSpannedByFallingMelodicRunsInLowestLineFeature(),
			new MelodicPitchVarietyInLowestLineFeature(),
			
			// Add features based on chords and vertical intervals
			new VerticalIntervalHistogramFeature(),
			new WrappedVerticalIntervalHistogramFeature(),
			new AverageVerticalNoteDensityFeature(),
			new AverageNumberOfSimultaneousPitchesFeature(),
			new AverageNumberOfSimultaneousPitchClassesFeature(),
			new AverageAmountOfVerticalPitchClassDoublingFeature(),
			new VariabilityOfVerticalNoteDensityFeature(),
			new VariabilityOfNumberOfSimultaneousPitchesFeature(),
			new VariabilityOfNumberOfSimultaneousPitchClassesFeature(),
			new VariabilityOfAmountOfVerticalPitchClassDoublingFeature(),
			new NumberOfDistinctVerticalIntervalsFeature(),
			new NumberOfDistinctWrappedVerticalIntervalsFeature(),
			new NumberOfCommonVerticalIntervalsFeature(),
			new NumberOfCommonWrappedVerticalIntervalsFeature(),
			new NumberOfVeryCommonVerticalIntervalsFeature(),
			new NumberOfVeryCommonWrappedVerticalIntervalsFeature(),
			new SmallestVerticalIntervalFeature(),
			new LargestVerticalIntervalFeature(),
			new LargestWrappedVerticalIntervalFeature(),
			new MeanVerticalIntervalFeature(),
			new MeanWrappedVerticalIntervalFeature(),
			new MostCommonVerticalIntervalFeature(),
			new MostCommonWrappedVerticalIntervalFeature(),
			new SecondMostCommonVerticalIntervalFeature(),
			new SecondMostCommonWrappedVerticalIntervalFeature(),
			new DistanceBetweenTwoMostCommonVerticalIntervalsFeature(),
			new DistanceBetweenTwoMostCommonWrappedVerticalIntervalsFeature(),
			new PrevalenceOfMostCommonVerticalIntervalFeature(),
			new PrevalenceOfMostCommonWrappedVerticalIntervalFeature(),
			new PrevalenceOfSecondMostCommonVerticalIntervalFeature(),
			new PrevalenceOfSecondMostCommonWrappedVerticalIntervalFeature(),
			new PrevalenceRatioOfTwoMostCommonVerticalIntervalsFeature(),
			new PrevalenceRatioOfTwoMostCommonWrappedVerticalIntervalsFeature(),
			new VerticalIntervalVariabilityFeature(),
			new WrappedVerticalIntervalVariabilityFeature(),
			new VerticalIntervalSkewnessFeature(),
			new WrappedVerticalIntervalSkewnessFeature(),
			new VerticalIntervalKurtosisFeature(),
			new WrappedVerticalIntervalKurtosisFeature(),
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
			new VerticalDissonanceRatioFeatureFourthsNotDissonantFeature(),
			new VerticalDissonanceRatioFourthsDissonantFeature(),
			new PrevalenceOfMinorVerticalIntervalsFeature(),
			new PrevalenceOfMajorVerticalIntervalsFeature(),
			new MinorToMajorVerticalIntervalsRatioFeature(),
			new VerticalMinorThirdPrevalenceFeature(),
			new VerticalMajorThirdPrevalenceFeature(),
			new MinorToMajorVerticalThirdsRatioFeature(),
			new AverageInstantaneousVerticalRangeFeature(),
			new VariabilityOfInstantaneousVerticalRangeFeature(),
			new ChordTypeHistogramFeature(),
			new NumberOfDistinctChordTypesFeature(),
			new NumberOfCommonChordTypesFeature(),
			new NumberOfVeryCommonChordTypesFeature(),
			new MostCommonChordTypeFeature(),
			new SecondMostCommonChordTypeFeature(),
			new PrevalenceOfMostCommonChordTypeFeature(),
			new PrevalenceOfSecondMostCommonChordTypeFeature(),
			new PrevalenceRatioOfTwoMostCommonChordTypesFeature(),
			new ChordTypeVariabilityFeature(),
			new PartialChordsFeature(),
			new StandardTriadsFeature(),
			new DiminishedAndAugmentedTriadsFeature(),
			new DominantSeventhChordsFeature(),
			new SeventhChordsFeature(),
			new NonStandardChordsFeature(),
			new ComplexChordsFeature(),
			new MinorMajorTriadRatioFeature(),
			new ChordDurationFeature(),
			
			// Add features based on rhythm (that do NOT take tempo into account)
			new InitialTimeSignatureFeature(),
			new SimpleInitialMeterFeature(),
			new CompoundInitialMeterFeature(),
			new ComplexInitialMeterFeature(),
			new DupleInitialMeterFeature(),
			new TripleInitialMeterFeature(),
			new QuadrupleInitialMeterFeature(),
			new MetricalDiversityFeature(),
			new DurationInQuarterNotesFeature(),
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
			new RangeOfRhythmicPulsesTempoStandardizedFeature(),
			new NumberOfRhythmicPulsesTempoStandardizedFeature(),
			new NumberOfStrongRhythmicPulsesTempoStandardizedFeature(),
			new NumberOfModerateRhythmicPulsesTempoStandardizedFeature(),
			new NumberOfRelativelyStrongRhythmicPulsesTempoStandardizedFeature(),
			new PrevalenceOfSlowerRhythmicPulsesTempoStandardizedFeature(),
			new PrevalenceOfMidTempoRhythmicPulsesTempoStandardizedFeature(),
			new PrevalenceOfFasterRhythmicPulsesTempoStandardizedFeature(),
			new MeanRhythmicPulseTempoStandardizedFeature(),
			new MedianRhythmicPulseTempoStandardizedFeature(),
			new StrongestRhythmicPulseTempoStandardizedFeature(),
			new SecondStrongestRhythmicPulseTempoStandardizedFeature(),
			new HarmonicityOfTwoStrongestRhythmicPulsesTempoStandardizedFeature(),
			new StrengthOfStrongestRhythmicPulseTempoStandardizedFeature(),
			new StrengthOfSecondStrongestRhythmicPulseTempoStandardizedFeature(),
			new StrengthRatioOfTwoStrongestRhythmicPulsesTempoStandardizedFeature(),
			new CombinedStrengthOfTwoStrongestRhythmicPulsesTempoStandardizedFeature(),
			new RhythmicVariabilityTempoStandardizedFeature(),
			new RhythmicPulseSkewnessTempoStandardizedFeature(),
			new RhythmicPulseKurtosisTempoStandardizedFeature(),
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
			new RangeOfRhythmicPulsesFeature(),
			new NumberOfRhythmicPulsesFeature(),
			new NumberOfStrongRhythmicPulsesFeature(),
			new NumberOfModerateRhythmicPulsesFeature(),
			new NumberOfRelativelyStrongRhythmicPulsesFeature(),
			new PrevalenceOfSlowerRhythmicPulsesFeature(),
			new PrevalenceOfMidTempoRhythmicPulsesFeature(),
			new PrevalenceOfFasterRhythmicPulsesFeature(),
			new MeanRhythmicPulseFeature(),
			new MedianRhythmicPulseFeature(),
			new StrongestRhythmicPulseFeature(),
			new SecondStrongestRhythmicPulseFeature(),
			new HarmonicityOfTwoStrongestRhythmicPulsesFeature(),
			new StrengthOfStrongestRhythmicPulseFeature(),
			new StrengthOfSecondStrongestRhythmicPulseFeature(),
			new StrengthRatioOfTwoStrongestRhythmicPulsesFeature(),
			new CombinedStrengthOfTwoStrongestRhythmicPulsesFeature(),
			new RhythmicVariabilityFeature(),
			new RhythmicPulseSkewnessFeature(),
			new RhythmicPulseKurtosisFeature(),
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
			new NumberOfCommonPitchedInstrumentsFeature(),
			new NumberOfCommonUnpitchedInstrumentsFeature(),
			new NumberOfVeryCommonPitchedInstrumentsFeature(),
			new NumberOfVeryCommonUnpitchedInstrumentsFeature(),
			new MostCommonPitchedInstrumentFeature(),
			new MostCommonUnpitchedInstrumentFeature(),
			new PrevalenceOfMostCommonPitchedInstrumentFeature(),
			new PrevalenceOfMostCommonUnpitchedInstrumentFeature(),
			new UnpitchedPercussionInstrumentPrevalenceFeature(),
			new HasLyricsFeature(),
			new VoicePrevalenceFeature(),
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
			new NumberOfSlursMeiFeature(),
            
            // Add features based on melodic interval n-grams
            new MostCommonMelodicInterval3GramTypeFeature(),
            new SecondMostCommonMelodicInterval3GramTypeFeature(),
            new PrevalenceOfMostCommonMelodicInterval3GramTypeFeature(),
            new PrevalenceOfSecondMostCommonMelodicInterval3GramTypeFeature(),
            new PrevalenceOfMedianMelodicInterval3GramTypeFeature(),
            new NumberOfDistinctMelodicInterval3GramTypesFeature(),
            new NumberOfRareMelodicInterval3GramTypesFeature(),
            new NumberOfCommonMelodicInterval3GramTypesFeature(),
            new NumberOfVeryCommonMelodicInterval3GramTypesFeature(),
            new PrevalenceOfMelodicInterval3GramTypesOccurringOnlyOnceFeature(),
            new PrevalenceOfRareMelodicInterval3GramTypesFeature(),
            new PrevalenceOfCommonMelodicInterval3GramTypesFeature(),
            new PrevalenceOfVeryCommonMelodicInterval3GramTypesFeature()
		};

		default_features_to_save = new boolean[all_implemented_feature_extractors.length];
		for (int i = 0; i < default_features_to_save.length; i++)
			default_features_to_save[i] = all_implemented_feature_extractors[i].getIsDefault();

		secure_features = new boolean[all_implemented_feature_extractors.length];
		for (int i = 0; i < secure_features.length; i++)
			secure_features[i] = all_implemented_feature_extractors[i].getIsSecure();
		
		multi_dimensional_features = new boolean[all_implemented_feature_extractors.length];
		for (int i = 0; i < multi_dimensional_features.length; i++)
		{
			if (all_implemented_feature_extractors[i].getFeatureDefinition().dimensions > 1)
				multi_dimensional_features[i] = true;
			else multi_dimensional_features[i] = false;
		}
		
		mei_specific_features = new boolean[all_implemented_feature_extractors.length];
		for (int i = 0; i < secure_features.length; i++)
		{
			if (all_implemented_feature_extractors[i] instanceof MEIFeatureExtractor)
				mei_specific_features[i] = true;
			else mei_specific_features[i] = false;
		}
		
		names_of_all_implemented_features = new ArrayList<>();
		List<MIDIFeatureExtractor> all_extractors = Arrays.asList(all_implemented_feature_extractors);
		for (MIDIFeatureExtractor fe : all_extractors)
			names_of_all_implemented_features.add(fe.getFeatureDefinition().name);

		names_of_default_features_to_save = new ArrayList<>();
		for (int i = 0; i < default_features_to_save.length; i++)
			if (default_features_to_save[i])
				names_of_default_features_to_save.add(names_of_all_implemented_features.get(i));
		
		names_of_secure_features_to_save = new ArrayList<>();
		for (int i = 0; i < secure_features.length; i++)
			if (secure_features[i])
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
	 * @return	An array with one entry for every feature implemented as a MIDIFeatureExtractor (including 
	 *			MEIFeatureExtractor features). These are ordered in the same order in which they are presented
	 *			in the jSymbolic manual. Each entry is set to true if that feature is safe to be extracted and
	 *			saved even when dealing with input symbolic music files that may be improperly or
	 *			inconsistently encoded, and to false if it is not.
	 */
	public static boolean[] getSecureFeatures()
	{
		return secure_features;
	}

	/**
	 * @return	An array with one entry for every feature implemented as a MIDIFeatureExtractor (including 
	 *			MEIFeatureExtractor features). These are ordered in the same order in which they are presented
	 *			in the jSymbolic manual. Each entry is set to true if that feature is a multi-dimensional 
	 *			feature, and to false if it is not.
	 */
	public static boolean[] getMultiDimensionalFeatures()
	{
		return multi_dimensional_features;
	}
	
	/**
	 * @return	An array with one entry for every feature implemented as a MIDIFeatureExtractor (including 
	 *			MEIFeatureExtractor features). These are ordered in the same order in which they are presented
	 *			in the jSymbolic manual. Each entry is set to true if that feature is an MEI-specific feature,
	 *			and to false if it is not.
	 */
	public static boolean[] getMeiSpecificFeatures()
	{
		return mei_specific_features;
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
	 * @return	A List consisting of the feature names of every feature implemented as a MIDIFeatureExtractor
	 *			(including MEIFeatureExtractor features) and that has also been marked as safe to be extracted
	 *			and saved, even when dealing with input symbolic music files that may be improperly or 
	 *			inconsistently encoded. These are ordered in the same order in which they are presented in the
	 *			jSymbolic manual.
	 */
	public static List<String> getNamesOfSecureFeaturesToSave()
	{
		return names_of_secure_features_to_save;
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

                char code = feat.getFeatureDefinition().code.charAt(0);
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
	 * Verify that the all_implemented_feature_extractors has been set up in a way that is self-consistent and
	 * compatible. Print warnings about any potential problems detected to standard error, along with an
	 * indication of problem severity. This is a useful error checker to help make sure that new features
	 * added to extend jSymbolic have been added properly. The following potential issues are checked:
	 * 
	 * 1) Verify that no features have been added to all_implemented_feature_extractors that are dependent on 
	 * features  that have not themselves been added to all_implemented_feature_extractors.
	 * 2) Verify that no more than one feature with any given feature name has been added to 
	 * all_implemented_feature_extractors.
	 * 3) Verify that no more than one feature with any given feature code has been added to 
	 * all_implemented_feature_extractors.
	 * 4) Verify that no feature names have commas in them (since this can cause problems when saved CSV
	 * or ARFF files are parsed).
	 * 5) Verify that no feature names have underscores in them (since this can cause problems when saved CSV
	 * or ARFF files are parsed).
	 * 6) Verify that all features have been added contiguously to all_implemented_feature_extractors, based 
	 * on their feature code groups and numbers, and that all feature codes are properly formatted.
	 * 7) Verify that, by default all MEI-specific features have been set to not be extracted, and that all
	 * other features have been set to be extracted.
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
			all_feature_codes[i] = all_implemented_feature_extractors[i].getFeatureDefinition().code;
		int[][] duplicate_codes = mckay.utilities.staticlibraries.StringMethods.getIndexesOfDuplicateEntries(all_feature_codes);
		if (duplicate_codes != null)
		{
			for (int i = 0; i < duplicate_codes.length; i++)
			{
				String duplicated_feature_code = all_implemented_feature_extractors[duplicate_codes[i][0]].getFeatureDefinition().code;
				int number_of_occurrences = duplicate_codes[i].length;
				problem_report += "WARNING: The feature code " + duplicated_feature_code + " has been added to jSymbolic in " + number_of_occurrences + " features. No feature code should be used more than once. This is not a serious problem, but it could result in confusion or redundant feature extraction.\n";
			}
		}
		
		// Verify that no feature names have commas in them (since this can cause problems when saved CSV
		// or ARFF files are parsed).
		ArrayList<String> names_of_features_with_commas = new ArrayList();
		for (String name : names_of_all_features_added)
			if (name.contains(",")) names_of_features_with_commas.add(name);
		if (!names_of_features_with_commas.isEmpty())
			for (String name : names_of_features_with_commas)
				problem_report += "WARNING: The feature " + name + " contains a comma in its name. This will cause problems when saved CSV or ARFF files are parsed (but will not affect saved ACE XML files). It is strongly suggested that the feature be renamed, without a comma. This is a moderately serious problem.\n";
		
		// Verify that no feature names have underscores in them (since this can cause problems when saved CSV
		// or ARFF files are parsed).
		ArrayList<String> names_of_features_with_underscores = new ArrayList();
		for (String name : names_of_all_features_added)
			if (name.contains("_")) names_of_features_with_underscores.add(name);
		if (!names_of_features_with_underscores.isEmpty())
			for (String name : names_of_features_with_underscores)
				problem_report += "WARNING: The feature " + name + " contains an underscore in its name. This will cause problems when saved CSV or ARFF files are parsed (but will not affect saved ACE XML files). It is strongly suggested that the feature be renamed, without a comma. This is a moderately serious problem.\n";
		
		// Verify that all features have been added contiguously to all_implemented_feature_extractors, based
		// on their feature code groups and numbers, and that all feature codes are properly formatted
		String last_group = "";
		int last_number = 0;
		for (int feat = 0; feat < all_implemented_feature_extractors.length; feat++)
		{
			try
			{
				String[] split_code = all_implemented_feature_extractors[feat].getFeatureDefinition().code.split("-");
				String this_group = split_code[0];
				int this_number = Integer.parseInt(split_code[1]);

				if (feat != 0)
				{
					if (this_group.equals(last_group))
					{
						if (this_number != (last_number + 1))
							problem_report += "WARNING: The feature " + all_implemented_feature_extractors[feat].getFeatureDefinition().code + " has been added to jSymbolic out of sequence (its code does not numerically follow the previous feature in its group). This is not a serious problem, but it could result in confusion.\n";
					}
					else if (this_number != 1)
						problem_report += "WARNING: The feature " + all_implemented_feature_extractors[feat].getFeatureDefinition().code + " has been added to jSymbolic out of sequence (a new feature group should be numbered as 0). This is not a serious problem, but it could result in confusion.\n";
				}

				last_group = this_group;
				last_number = this_number;
			}
			catch (Exception e)
			{
				problem_report += "WARNING: The feature " + all_implemented_feature_extractors[feat].getFeatureDefinition().code + " has an improperly formatted code. The code should consist of one or more letters identifying the feature group the feature belongs to, followed by a hyphen, followed by the number of the feature within that group. For example, a code of I-7 would be appropriate for the seventh feature of the Instrumentation feature group. This is not a serious problem, but it could result in confusion.\n";
			}
		}
		
		// Verify that, by default, all MEI-specific features have been set to not be extracted, and that all
		// other features have been set to be extracted.
		for (int feat = 0; feat < all_implemented_feature_extractors.length; feat++)
		{
			if ( !default_features_to_save[feat] && 
			     !(all_implemented_feature_extractors[feat] instanceof MEIFeatureExtractor) )
				problem_report += "WARNING: " + names_of_all_features_added[feat] + " is set to not be extracted and saved by default. Typically, all non-MEI specific features such as this one are set to be saved by default. This is not a serious issue, but it might cause confusion.\n";
			else if ( default_features_to_save[feat] &&
			          all_implemented_feature_extractors[feat] instanceof MEIFeatureExtractor )
				problem_report += "WARNING: " + names_of_all_features_added[feat] + " is set to be extracted and saved by default. Typically, MEI-specific features such as this one are not set to be saved by default. This is not a serious issue, but it might cause confusion.\n";
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
			System.out.println( (i+1) + ":\t" + all_implemented_feature_extractors[i].getFeatureDefinition().code + "\t" +
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
				System.out.println(all_implemented_feature_extractors[i].getFeatureDefinition().code + "\t" +
			                        all_implemented_feature_extractors[i].definition.name);
				for (int j = 0; j < all_implemented_feature_extractors[i].dependencies.length; j++)
					System.out.println("\tDEPENDS ON: " + all_implemented_feature_extractors[i].dependencies[j]);
			}
		}
	}
}