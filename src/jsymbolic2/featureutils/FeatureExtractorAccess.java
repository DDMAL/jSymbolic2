package jsymbolic2.featureutils;

import jsymbolic2.features.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A central access point to obtain all feature information, where features are
 * accessible in the correct order with respect to the boolean array for features to save.
 * This allows for consistent comparisons of all the input features from any source,
 * i.e. command line, gui or configuration file.
 *
 * @author Tristano Tenaglia
 */
public final class FeatureExtractorAccess {

	/**
	 * An array with all the available feature extractors.
	 */
    private static final MIDIFeatureExtractor[] allExtractors;

	/**
	 * An array with the default features to save in the correct order
	 * where the order corresponds to the allExtractors array.
	 */
    private static final boolean[] defaultFeaturesToSave;

	/**
	 * A List of all the default feature names.
	 */
	private static final List<String> defaultFeatureNamesToSave;

	/**
	 * The List of all available feature names, corresponding
	 * to the names of each feature designated in the Feature Definition.
	 */
	private static final List<String> featureNameList;

    /**
     * @return Returns an array of all the available feature extractors.
     */
    public static MIDIFeatureExtractor[] getAllFeatureExtractors() {
        return allExtractors;
    }

    /**
     * @return Return boolean array of which features to save, which
     * are in the same order as the allExtractors[].
     */
    public static boolean[] getDefaultFeaturesToSave() {
        return defaultFeaturesToSave;
    }

	/**
	 * Returns all feature names in the correct order. The creation of this
	 * list is performed in the static block, thus on first evaluation.
	 * @return The list of all available feature names.
	 */
	public static List<String> getFeatureNameList() {
		return featureNameList;
	}

	/**
	 * Returns the names of all default features specified in the boolean array.
	 * @return A List of all feature names that are given by default.
     */
	public static List<String> getDefaultFeatureNamesToSave() {
		return defaultFeatureNamesToSave;
	}

	/**
     * Initialize Feature Extractors, default features and cleaned up feature names
	 * to save at startup with a main singleton access here.
     */
    static {

        allExtractors = new MIDIFeatureExtractor[]
        {
                // Add non-sequential features
                new DurationFeature(),

                // Add one-dimensional sequential features
                new AcousticGuitarFractionFeature(),
                new AmountOfArpeggiationFeature(),
                new AverageMelodicIntervalFeature(),
                new AverageNoteDurationFeature(),
                new AverageNoteToNoteDynamicsChangeFeature(),
                new AverageNumberOfIndependentVoicesFeature(),
                new AverageRangeOfGlissandosFeature(),
                new AverageTimeBetweenAttacksFeature(),
                new AverageTimeBetweenAttacksForEachVoiceFeature(),
                new AverageVariabilityOfTimeBetweenAttacksForEachVoiceFeature(),
                new BrassFractionFeature(),
                new ChangesOfMeterFeature(),
                new ChromaticMotionFeature(),
                new CombinedStrengthOfTwoStrongestRhythmicPulsesFeature(),
                new CompoundOrSimpleMeterFeature(),
                new DirectionOfMotionFeature(),
                new DistanceBetweenMostCommonMelodicIntervalsFeature(),
                new DominantSpreadFeature(),
                new DurationOfMelodicArcsFeature(),
                new ElectricGuitarFractionFeature(),
                new ElectricInstrumentFractionFeature(),
                new GlissandoPrevalenceFeature(),
                new HarmonicityOfTwoStrongestRhythmicPulsesFeature(),
                new ImportanceOfBassRegisterFeature(),
                new ImportanceOfHighRegisterFeature(),
                new ImportanceOfLoudestVoiceFeature(),
                new ImportanceOfMiddleRegisterFeature(),
                new InitialTempoFeature(),
                new IntervalBetweenStrongestPitchClassesFeature(),
                new IntervalBetweenStrongestPitchesFeature(),
                new MaximumNoteDurationFeature(),
                new MaximumNumberOfIndependentVoicesFeature(),
                new MelodicFifthsFeature(),
                new MelodicIntervalsInLowestLineFeature(),
                new MelodicOctavesFeature(),
                new MelodicThirdsFeature(),
                new MelodicTritonesFeature(),
                new MinimumNoteDurationFeature(),
                new MostCommonMelodicIntervalFeature(),
                new MostCommonMelodicIntervalPrevalenceFeature(),
                new MostCommonPitchClassFeature(),
                new MostCommonPitchClassPrevalenceFeature(),
                new MostCommonPitchFeature(),
                new MostCommonPitchPrevalenceFeature(),
                new NoteDensityFeature(),
                new NumberOfCommonMelodicIntervalsFeature(),
                new NumberOfCommonPitchesFeature(),
				new NumberOfGraceNotesFeature(),
				new NumberOfSlurNotesFeature(),
                new NumberOfModeratePulsesFeature(),
                new NumberOfPitchedInstrumentsFeature(),
                new NumberOfRelativelyStrongPulsesFeature(),
                new NumberOfStrongPulsesFeature(),
                new NumberOfUnpitchedInstrumentsFeature(),
                new OrchestralStringsFractionFeature(),
                new OverallDynamicRangeFeature(),
                new PercussionPrevalenceFeature(),
                new PitchClassVarietyFeature(),
                new PitchVarietyFeature(),
                new PolyrhythmsFeature(),
                new PrimaryRegisterFeature(),
                new QualityFeature(),
                new QuintupleMeterFeature(),
                new RangeFeature(),
                new RangeOfHighestLineFeature(),
                new RelativeNoteDensityOfHighestLineFeature(),
                new RelativeRangeOfLoudestVoiceFeature(),
                new RelativeStrengthOfMostCommonIntervalsFeature(),
                new RelativeStrengthOfTopPitchClassesFeature(),
                new RelativeStrengthOfTopPitchesFeature(),
                new RepeatedNotesFeature(),
                new RhythmicLoosenessFeature(),
                new RhythmicVariabilityFeature(),
                new SaxophoneFractionFeature(),
                new SecondStrongestRhythmicPulseFeature(),
                new SizeOfMelodicArcsFeature(),
                new StaccatoIncidenceFeature(),
                new StepwiseMotionFeature(),
                new StrengthOfSecondStrongestRhythmicPulseFeature(),
                new StrengthOfStrongestRhythmicPulseFeature(),
                new StrengthRatioOfTwoStrongestRhythmicPulsesFeature(),
                new StringEnsembleFractionFeature(),
                new StringKeyboardFractionFeature(),
                new StrongTonalCentresFeature(),
                new StrongestRhythmicPulseFeature(),
                new TripleMeterFeature(),
                new VariabilityOfNoteDurationFeature(),
                new VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature(),
                new VariabilityOfNotePrevalenceOfUnpitchedInstrumentsFeature(),
                new VariabilityOfNumberOfIndependentVoicesFeature(),
                new VariabilityOfTimeBetweenAttacksFeature(),
                new VariationOfDynamicsFeature(),
                new VariationOfDynamicsInEachVoiceFeature(),
                new VibratoPrevalenceFeature(),
                new ViolinFractionFeature(),
                new VoiceEqualityDynamicsFeature(),
                new VoiceEqualityMelodicLeapsFeature(),
                new VoiceEqualityNoteDurationFeature(),
                new VoiceEqualityNumberOfNotesFeature(),
                new VoiceEqualityRangeFeature(),
                new VoiceSeparationFeature(),
                new WoodwindsFractionFeature(),
				new MostCommonVerticalIntervalFeature(),
				new SecondMostCommonVerticalIntervalFeature(),
				new DistanceBetweenTwoMostCommonVerticalIntervalsFeature(),
				new PrevalenceOfMostCommonVerticalIntervalFeature(),
				new PrevalenceOfSecondMostCommonVerticalIntervalFeature(),
				new RatioOfPrevalenceOfTwoMostCommonVerticalIntervalsFeature(),
				new AverageNumberOfSimultaneousPitchClassesFeature(),
				new VariabilityOfNumberOfSimultaneousPitchClassesFeature(),
				new FractionOfMinorVerticalIntervalsFeature(),
				new FractionOfMajorVerticalIntervalsFeature(),
				new UnisonsFeature(),
				new VerticalMinorSecondsFeature(),
				new VerticalThirdsFeature(),
				new VerticalFifthsFeature(),
				new VerticalTritonesFeature(),
				new VerticalOctavesFeature(),
				new VerticalDissonanceRatioFeature(),
				new PartialChordsFeature(),
				new MinorMajorTriadRatioFeature(),
				new StandardTriadsFeature(),
				new DiminishedAndAugmentedTriadsFeature(),
				new DominantSeventhChordsFeature(),
				new SeventhChordsFeature(),
				new ComplexChordsFeature(),
				new NonStandardChordsFeature(),
				new ChordDurationFeature(),
				new RelativeRangeIsolationOfLoudestVoiceFeature(),
				new RelativeNoteDurationsOfLowestLineFeature(),
				new SimultaneityFeature(),
				new VariabilityOfSimultaneityFeature(),
				new ParallelMotionFeature(),

                // Add multi-dimensional sequential features
                new BasicPitchHistogramFeature(),
                new BeatHistogramFeature(),
                new FifthsPitchHistogramFeature(),
                new InitialTimeSignatureFeature(),
                new MelodicIntervalHistogramFeature(),
                new NotePrevalenceOfPitchedInstrumentsFeature(),
                new NotePrevalenceOfUnpitchedInstrumentsFeature(),
                new PitchClassDistributionFeature(),
                new PitchedInstrumentsPresentFeature(),
                new TimePrevalenceOfPitchedInstrumentsFeature(),
                new UnpitchedInstrumentsPresentFeature(),
				new VerticalIntervalWrappedHistogramFeature(),
				new VerticalIntervalHistogramFeature(),
				new ChordTypesHistogramFeature()
        };
        
        defaultFeaturesToSave = new boolean[]
         {
                // Add non-sequential features
                true, //DurationFeature

                // Add one-dimensional sequential features
                true, //AcousticGuitarFractionFeature
        		true, //AmountOfArpeggiationFeature
        		true, //AverageMelodicIntervalFeature
        		true, //AverageNoteDurationFeature
        		true, //AverageNoteToNoteDynamicsChangeFeature
        		true, //AverageNumberOfIndependentVoicesFeature
        		true, //AverageRangeOfGlissandosFeature
        		true, //AverageTimeBetweenAttacksFeature
        		true, //AverageTimeBetweenAttacksForEachVoiceFeature
        		true, //AverageVariabilityOfTimeBetweenAttacksForEachVoiceFeature
        		true, //BrassFractionFeature
        		true, //ChangesOfMeterFeature
        		true, //ChromaticMotionFeature
        		true, //CombinedStrengthOfTwoStrongestRhythmicPulsesFeature
        		true, //CompoundOrSimpleMeterFeature
        		true, //DirectionOfMotionFeature
        		true, //DistanceBetweenMostCommonMelodicIntervalsFeature
        		true, //DominantSpreadFeature
        		true, //DurationOfMelodicArcsFeature
        		true, //ElectricGuitarFractionFeature
        		true, //ElectricInstrumentFractionFeature
        		true, //GlissandoPrevalenceFeature
        		true, //HarmonicityOfTwoStrongestRhythmicPulsesFeature
        		true, //ImportanceOfBassRegisterFeature
        		true, //ImportanceOfHighRegisterFeature
        		true, //ImportanceOfLoudestVoiceFeature
        		true, //ImportanceOfMiddleRegisterFeature
        		true, //InitialTempoFeature
        		true, //IntervalBetweenStrongestPitchClassesFeature
        		true, //IntervalBetweenStrongestPitchesFeature
        		true, //MaximumNoteDurationFeature
        		true, //MaximumNumberOfIndependentVoicesFeature
        		true, //MelodicFifthsFeature
        		true, //MelodicIntervalsInLowestLineFeature
        		true, //MelodicOctavesFeature
        		true, //MelodicThirdsFeature
        		true, //MelodicTritonesFeature
        		true, //MinimumNoteDurationFeature
        		true, //MostCommonMelodicIntervalFeature
        		true, //MostCommonMelodicIntervalPrevalenceFeature
        		true, //MostCommonPitchClassFeature
        		true, //MostCommonPitchClassPrevalenceFeature
        		true, //MostCommonPitchFeature
        		true, //MostCommonPitchPrevalenceFeature
        		true, //NoteDensityFeature
        		true, //NumberOfCommonMelodicIntervalsFeature
        		true, //NumberOfCommonPitchesFeature
        		false, //NumberOfGraceNotesFeature
				false, //NumberOfSlurNotesFeature
        		true, //NumberOfModeratePulsesFeature
        		true, //NumberOfPitchedInstrumentsFeature
        		true, //NumberOfRelativelyStrongPulsesFeature
        		true, //NumberOfStrongPulsesFeature
        		true, //NumberOfUnpitchedInstrumentsFeature
        		true, //OrchestralStringsFractionFeature
        		true, //OverallDynamicRangeFeature
        		true, //PercussionPrevalenceFeature
        		true, //PitchClassVarietyFeature
        		true, //PitchVarietyFeature
        		true, //PolyrhythmsFeature
        		true, //PrimaryRegisterFeature
        		true, //QualityFeature
        		true, //QuintupleMeterFeature
        		true, //RangeFeature
        		true, //RangeOfHighestLineFeature
        		true, //RelativeNoteDensityOfHighestLineFeature
        		true, //RelativeRangeOfLoudestVoiceFeature
        		true, //RelativeStrengthOfMostCommonIntervalsFeature
        		true, //RelativeStrengthOfTopPitchClassesFeature
        		true, //RelativeStrengthOfTopPitchesFeature
        		true, //RepeatedNotesFeature
        		true, //RhythmicLoosenessFeature
        		true, //RhythmicVariabilityFeature
        		true, //SaxophoneFractionFeature
        		true, //SecondStrongestRhythmicPulseFeature
        		true, //SizeOfMelodicArcsFeature
        		true, //StaccatoIncidenceFeature
        		true, //StepwiseMotionFeature
        		true, //StrengthOfSecondStrongestRhythmicPulseFeature
        		true, //StrengthOfStrongestRhythmicPulseFeature
        		true, //StrengthRatioOfTwoStrongestRhythmicPulsesFeature
        		true, //StringEnsembleFractionFeature
        		true, //StringKeyboardFractionFeature
        		true, //StrongTonalCentresFeature
        		true, //StrongestRhythmicPulseFeature
        		true, //TripleMeterFeature
        		true, //VariabilityOfNoteDurationFeature
        		true, //VariabilityOfNotePrevalenceOfPitchedInstrumentsFeature
        		true, //VariabilityOfNotePrevalenceOfUnpitchedInstrumentsFeature
        		true, //VariabilityOfNumberOfIndependentVoicesFeature
        		true, //VariabilityOfTimeBetweenAttacksFeature
        		true, //VariationOfDynamicsFeature
        		true, //VariationOfDynamicsInEachVoiceFeature
        		true, //VibratoPrevalenceFeature
        		true, //ViolinFractionFeature
        		true, //VoiceEqualityDynamicsFeature
        		true, //VoiceEqualityMelodicLeapsFeature
        		true, //VoiceEqualityNoteDurationFeature
        		true, //VoiceEqualityNumberOfNotesFeature
        		true, //VoiceEqualityRangeFeature
        		true, //VoiceSeparationFeature
        		true, //WoodwindsFractionFeature
				true, //MostCommonVerticalIntervalFeature
				true, //SecondMostCommonVerticalIntervalFeature
				true, //DistanceBetweenTwoMostCommonVerticalIntervalsFeature
				true, //PrevalenceOfMostCommonVerticalIntervalFeature
				true, //PrevalenceOfSecondMostCommonVerticalIntervalFeature
				true, //RatioOfPrevalenceOfTwoMostCommonVerticalIntervalsFeature
				true, //AverageNumberOfSimultaneousPitchClassesFeature
				true, //VariabilityOfNumberOfSimultaneousPitchClassesFeature
				true, //FractionOfMinorVerticalIntervalsFeature
				true, //FractionOfMajorVerticalIntervalsFeature
				true, //UnisonsFeature
				true, //VerticalMinorSecondsFeature
				true, //VerticalThirdsFeature
				true, //VerticalFifthsFeature
				true, //VerticalTritonesFeature
				true, //VerticalOctavesFeature
				true, //VerticalDissonanceRatioFeature
				true, //PartialChordsFeature
				true, //MinorMajorTriadRatioFeature
				true, //StandardTriadsFeature
				true, //DiminishedAndAugmentedTriadsFeature
				true, //DominantSeventhChordsFeature
				true, //SeventhChordsFeature
				true, //ComplexChordsFeature
				true, //NonStandardChordsFeature
				true, //ChordDurationFeature
				true, //RelativeRangeIsolationOfLoudestVoiceFeature
				true, //RelativeNoteDurationsOfLowestLineFeature
				true, //SimultaneityFeature
				true, //VariabilityOfSimultaneityFeature
				true, //ParallelMotionFeature

                // Add multi-dimensional sequential features
                false, //BasicPitchHistogramFeature
                false, //BeatHistogramFeature
                false, //FifthsPitchHistogramFeature
                false, //InitialTimeSignatureFeature
                false, //MelodicIntervalHistogramFeature
                false, //NotePrevalenceOfPitchedInstrumentsFeature
                false, //NotePrevalenceOfUnpitchedInstrumentsFeature
                false, //PitchClassDistributionFeature
                false, //PitchedInstrumentsPresentFeature
                false, //TimePrevalenceOfPitchedInstrumentsFeature
                false, //UnpitchedInstrumentsPresentFeature
				false, //VerticalIntervalWrappedHistogramFeature
				false, //VerticalIntervalHistogramFeature
				false //ChordTypesHistogramFeature
        };

		//Setup the cleaned feature list
		//Description explained in javadoc comments
		List<MIDIFeatureExtractor> extractorList = Arrays.asList(allExtractors);
		featureNameList = new ArrayList<>();
		for(MIDIFeatureExtractor fe : extractorList) {
			//Replace all non-alphanumeric characters with empty string
			String feName = fe.getFeatureDefinition().name;
			featureNameList.add(feName);
		}

		//Add all corresponding default feature names to default feature name list
		defaultFeatureNamesToSave = new ArrayList<>();
		for(int i = 0; i < defaultFeaturesToSave.length; i++) {
			if(defaultFeaturesToSave[i] == true) {
				String featureName = featureNameList.get(i);
				defaultFeatureNamesToSave.add(featureName);
			}
		}
    }
}
