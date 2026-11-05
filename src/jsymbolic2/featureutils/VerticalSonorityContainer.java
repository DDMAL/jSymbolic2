package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import mckay.utilities.staticlibraries.MathAndStatsMethods;

/**
 * This class bundles all functionality related to vertical sonority. Objects of this class consist of the vertical
 * sonority sounding at every note onset slice, represented in several different ways.
 * 
 * @author Jasper Teunen
 */
public class VerticalSonorityContainer {

    /* PUBLIC FIELDS ****************************************************************************************/

	
	/**
	 * The total number of unique sonorities consisting of compound chromatic intervals. 
	 */
    public final int NUMBER_OF_COMPOUND_CHROMATIC_SONORITIES;

    /**
	 * The total number of unique sonorities consisting of simple chromatic intervals. 
	 */
	public final int NUMBER_OF_SIMPLE_CHROMATIC_SONORITIES;

    /**
	 * The total number of unique sonorities consisting of compound diatonic intervals. 
	 */
	public final int NUMBER_OF_COMPOUND_DIATONIC_SONORITIES;

    /**
	 * The total number of unique sonorities consisting of simple diatonic intervals. 
	 */
	public final int NUMBER_OF_SIMPLE_DIATONIC_SONORITIES;

	
	/* PRIVATE FIELDS ***************************************************************************************/

	private int[][] basic_sonority_by_slice;

	private int[][] sonorities_chromatic_compound;

	private int[][] sonorities_chromatic_simple;

	private int[][] sonorities_diatonic_compound;

	private int[][] sonorities_diatonic_simple;

	private int[][] unique_sonorities_chromatic_compound;

	private int[][] unique_sonorities_chromatic_simple;

	private int[][] unique_sonorities_diatonic_compound;

	private int[][] unique_sonorities_diatonic_simple;

	private double[] sonority_histogram_chromatic_compound;

	private double[] sonority_histogram_chromatic_simple;

	private double[] sonority_histogram_diatonic_compound;

	private double[] sonority_histogram_diatonic_simple;

    private LinkedList<Double> sonority_durations;

    private int n_vson;

    private boolean count_tritones_as_fifths;


    /* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Parse the onset slices into the vertical sonority information contained within this object.
	 *
	 * @param	onset_slices				A list of pitches sounding at each onset slice
     * @param   n_vson                      The number of intervals included in each vertical sonority.
	 */
	public VerticalSonorityContainer(
        NoteOnsetSliceContainer onset_slice_container,
        int n_vson,
        boolean count_tritones_as_fifths)
	{
        sonority_durations = onset_slice_container.getSmallestRhythmicValueByOnsetSlice();
        this.n_vson = n_vson;
        this.count_tritones_as_fifths = count_tritones_as_fifths;

		// Initialize intermediate representations of sonority by onset slice to correct size
		basic_sonority_by_slice = new int[onset_slice_container.NUMBER_OF_ONSET_SLICES][];
		sonorities_chromatic_compound = new int[onset_slice_container.NUMBER_OF_ONSET_SLICES][n_vson];
		sonorities_chromatic_simple = new int[onset_slice_container.NUMBER_OF_ONSET_SLICES][n_vson];
		sonorities_diatonic_compound = new int[onset_slice_container.NUMBER_OF_ONSET_SLICES][n_vson];
		sonorities_diatonic_simple = new int[onset_slice_container.NUMBER_OF_ONSET_SLICES][n_vson];

        // Initialize hashmap that translated chormatic intervals to diatonic intervals
        HashMap<Integer, Integer> diatonic_translator = this.createDiatonicHashmap();

        // Initialize onset_slices
        LinkedList<LinkedList<Integer>> onset_slices = onset_slice_container.getNoteOnsetSlicesDuplicatePitchesIncluded();

		// Iterate through note onset slices
		for (int slice = 0; slice < onset_slice_container.NUMBER_OF_ONSET_SLICES; slice++){
			// Get slice, slices are already sorted low to high
			LinkedList<Integer> current_slice = onset_slices.get(slice);
			
			// Initialize current vertical sonority and vertical sonority without duplicate intervals
			int[] current_chromatic = new int[current_slice.size()];
			ArrayList<Integer> current_chromatic_no_dupes = new ArrayList<>();

			// Set first element of vertical sonority to lowest pitch
			current_chromatic[0] = current_slice.get(0);
			current_chromatic_no_dupes.add(current_slice.get(0));

			// Set other elements to chromatic intervals
			if (current_slice.size() > 1){
				for (int i = 1; i < current_slice.size(); i++){
					int interval_chromatic = current_slice.get(i) - current_slice.get(0);
					current_chromatic[i] = interval_chromatic;
					if(interval_chromatic != 0 && interval_chromatic != current_chromatic[i-1]){
						current_chromatic_no_dupes.add(interval_chromatic);
					}
				}
			}


			// Add to vertical sonority list
			basic_sonority_by_slice[slice] = current_chromatic;

			for (int j=0; j < n_vson; j++){
				if (j < current_chromatic_no_dupes.size()){
					sonorities_chromatic_compound[slice][j] = current_chromatic_no_dupes.get(j);
				} else {
					sonorities_chromatic_compound[slice][j] = -1;
				}
			}

			// Translate to diatonic
			int[] current_diatonic = new int[n_vson];
			Arrays.fill(current_diatonic, -1);
			current_diatonic[0] = current_chromatic_no_dupes.get(0);
			for (int j=1; j < Math.min(n_vson -1, current_chromatic_no_dupes.size()); j++){
				current_diatonic[j] = diatonic_translator.get(current_chromatic_no_dupes.get(j));
			}

            // Transform compound intervals into simple intervals, removing duplicates
            int[] current_chromatic_simple = this.getUniqueSimpleIntervals(current_chromatic, true);
            int[] current_diatonic_simple = this.getUniqueSimpleIntervals(current_diatonic, false);

			// Put sonorities in relevant arrays
			sonorities_chromatic_simple[slice] = current_chromatic_simple;
			sonorities_diatonic_simple[slice] = current_diatonic_simple;
			sonorities_diatonic_compound[slice] = current_diatonic;
		}

		unique_sonorities_chromatic_compound = this.getUniqueSonorities(sonorities_chromatic_compound);
        unique_sonorities_chromatic_simple = this.getUniqueSonorities(sonorities_chromatic_simple);
        unique_sonorities_diatonic_compound = this.getUniqueSonorities(sonorities_diatonic_compound);
        unique_sonorities_diatonic_simple = this.getUniqueSonorities(sonorities_diatonic_simple);

		NUMBER_OF_COMPOUND_CHROMATIC_SONORITIES = unique_sonorities_chromatic_compound.length;
		NUMBER_OF_SIMPLE_CHROMATIC_SONORITIES = unique_sonorities_chromatic_simple.length;
		NUMBER_OF_COMPOUND_DIATONIC_SONORITIES = unique_sonorities_diatonic_compound.length;
		NUMBER_OF_SIMPLE_DIATONIC_SONORITIES = unique_sonorities_diatonic_simple.length;

        // Initialize histograms
		sonority_histogram_chromatic_compound = this.initializeHistogram(
            sonorities_chromatic_compound,
            unique_sonorities_chromatic_compound,
            NUMBER_OF_COMPOUND_CHROMATIC_SONORITIES);

		sonority_histogram_chromatic_simple = this.initializeHistogram(
            sonorities_chromatic_simple,
            unique_sonorities_chromatic_simple,
            NUMBER_OF_SIMPLE_CHROMATIC_SONORITIES);

		sonority_histogram_diatonic_compound = this.initializeHistogram(
            sonorities_diatonic_compound,
            unique_sonorities_diatonic_compound,
            NUMBER_OF_COMPOUND_DIATONIC_SONORITIES);

		sonority_histogram_diatonic_simple = this.initializeHistogram(
            sonorities_diatonic_simple,
            unique_sonorities_diatonic_simple,
            NUMBER_OF_SIMPLE_DIATONIC_SONORITIES);

		// Normalize histograms
		sonority_histogram_chromatic_compound = MathAndStatsMethods.normalize(sonority_histogram_chromatic_compound);
		sonority_histogram_chromatic_simple = MathAndStatsMethods.normalize(sonority_histogram_chromatic_simple);
		sonority_histogram_diatonic_compound = MathAndStatsMethods.normalize(sonority_histogram_diatonic_compound);
		sonority_histogram_diatonic_simple = MathAndStatsMethods.normalize(sonority_histogram_diatonic_simple);

	}

	/* PUBLIC METHODS **************************************************************************************/
	
    public int[][] getChromaticCompoundSonorities(){
        return this.sonorities_chromatic_compound;
    }

	public int[][] getChromaticSimpleSonorities(){
        return this.sonorities_chromatic_simple;
    }

	public int[][] getDiatonicCompoundSonorities(){
        return this.sonorities_diatonic_compound;
    }

	public int[][] getDiatonicSimpleSonorities(){
        return this.sonorities_diatonic_simple;
    }

    public int[][] getUniqueChromaticCompoundSonorities(){
        return this.unique_sonorities_chromatic_compound;
    }

	public int[][] getUniqueChromaticSimpleSonorities(){
        return this.unique_sonorities_chromatic_simple;
    }

	public int[][] getUniqueDiatonicCompoundSonorities(){
        return this.unique_sonorities_diatonic_compound;
    }

	public int[][] getUniqueDiatonicSimpleSonorities(){
        return this.unique_sonorities_diatonic_simple;
    }

	public double[] getChromaticCompoundSonorityHistogram(){
        return this.sonority_histogram_chromatic_compound;
    }

	public double[] getChromaticSimpleSonorityHistogram(){
        return this.sonority_histogram_chromatic_simple;
    }

	public double[] getDiatonicCompoundSonorityHistogram(){
        return this.sonority_histogram_diatonic_compound;
    }

	public double[] getDiatonicSimpleSonorityHistogram(){
        return this.sonority_histogram_diatonic_simple;
    }

    /**
     * Translation of chromatic to diatonic intervals. This process occurs enharmonically, meaning that no 
     * differentiation between enharmonic equivalents occurs. 
     * 
     * Unisons are labelled 1, seconds 2, thirds 3 and so on.
     * 
     * The class variable count_tritones_as_fifths decides wether or not tritones are counted as fifths or fourths
     * 
     * @return A hashmap that can be used to translate chromatic intervals to diatonic intervals by calling
     * hashmap.get(chromatic_interval)
     */
    private HashMap<Integer, Integer> createDiatonicHashmap(){
		// Translation for simple intervals (smaller than octave)
		HashMap<Integer, Integer> diatonic_translation_simple = new HashMap<Integer, Integer>();
		diatonic_translation_simple.put(0, 1); // enharmonic unison to diatonic unison
		diatonic_translation_simple.put(1, 2); // enharmonic minor second to diatonic second
		diatonic_translation_simple.put(2, 2); // enharmonic major second to diatonic second
		diatonic_translation_simple.put(3, 3); // enharmonic minor third to diatonic third
		diatonic_translation_simple.put(4, 3); // enharmonic major third to diatonic third
		diatonic_translation_simple.put(5, 4); // enharmonic perfect fourth to diatonic fourth

        // Tritone counted as fifth or fourth, depending on parameter "countTritonesAsFifths"
		if (count_tritones_as_fifths) diatonic_translation_simple.put(6, 5); // tritone counted as diatonic fifth
        else diatonic_translation_simple.put(6,4); // tritone counted as diatonic fourth

		diatonic_translation_simple.put(7, 5); // enharmonic perfect fifth to diatonic fifth
		diatonic_translation_simple.put(8, 6); // enharmonic minor sixth to diatonic sixth
		diatonic_translation_simple.put(9, 6); // enharmonic major sixth to diatonic sixth
		diatonic_translation_simple.put(10, 7); // enharmonic minor seventh to diatonic seventh
		diatonic_translation_simple.put(11, 7); // enharmonic major seventh to diatonic seventh

		// Exanding to compound intervals
		HashMap<Integer, Integer> diatonic_translation_compound = new HashMap<Integer, Integer>();
		for (int i=0; i < 128; i++){
            // Translate the simple interval to its diatonic equivalent
            int simple_interval = diatonic_translation_simple.get(i%12);

            // Place the simple interval in its correct octave to obtain the compound interval
			diatonic_translation_compound.put(i, simple_interval + (i/12) * 7);
		}
        return diatonic_translation_compound;
    }

    /**
     * Returns all unique intervals in the given sonority. Unisons are never returned and octaves are given as 12
     * for chromatic sonorities or 8 for diatonic sonorities instead of 0 and 1 respectively to clearly indicated
     * that they are (compound) octaves and not unisons
     * 
     * @param compound_sonority     The original sonority to be translated
     * @param is_chromatic          Whether or not this sonority is chromatic or diatonic
     * @return                      An array with only the unique intervals
     */
    private int[] getUniqueSimpleIntervals(int[] compound_sonority, boolean is_chromatic){
        // Get unique simple intervals
        TreeSet<Integer> simple_interval_set = new TreeSet<>();
        for (int j=1; j<compound_sonority.length; j++){
            // Map to octave range
            if (is_chromatic && compound_sonority[j] != 0 && compound_sonority[j] != -1){
                /**
                 * Chromatic intervals range from 1 - 12. 0 is not included, because it implies a doubling of the
                 * lowest note and this function is only concerned with unique pitches. Octaves are represented as
                 * 12 and not as 0 to clearly differentiate them from unisons
                 */ 
                simple_interval_set.add(Math.floorMod(compound_sonority[j]-1, 12)+1);
            } else if (!is_chromatic && compound_sonority[j] != 1 && compound_sonority[j] != -1){
                /**
                 * For similar reasons, diatonic intervals range from 2 - 8. 1, the unison, is again not included
                 * and octaves, 8, are represented as such to differentiate them from unisons.
                 */
                simple_interval_set.add(Math.floorMod(compound_sonority[j]-2, 7)+2);
            }
        }
        // Initialize the actual sonority array
        int[] simple_sonority = new int[n_vson];
		Arrays.fill(simple_sonority, -1);

        // Set first element to lowest note
        simple_sonority[0] = compound_sonority[0];

        // Fill the rest of the array, upto size n_vson, with simple intervals
        int j = 1;
        for (int value : simple_interval_set){
            if (j >= n_vson) break;
            simple_sonority[j++] = value;
        }

        return simple_sonority;
    }

    // Get all unique chromatic sonorities and put them in unique_vertical_sonorities_chromatic
		// Bass notes are removed as they don't define sonority
    /**
     * Get all unique sonorities. These are reduced representations that don't include the lowest note, as a
     * differing lowest note does not a unique sonority make. This enables easy grouping of sonorities with
     * identical intervals. The sonorities are automatically sorted with "sortUniqueSonorities"
     * 
     * @param sonorities    All sonorities
     * @return              A sorted array of all unique values of "sonorities"
     */
    private int[][] getUniqueSonorities(int[][] sonorities){
        Set<String> seen = new HashSet<>();
        // Return sorted version of unique sonority array
		return this.sortUniqueSonorities(Arrays.stream(sonorities)
			.map(arr -> Arrays.copyOfRange(arr, 1, arr.length)) // removes lowest note
			.filter(arr -> seen.add(Arrays.toString(arr))) // removes duplicates
			.toArray(int[][]::new));	// converts back to array
    }

    /**
     * Counts the amount of elements of a sonority that represent notes, i.e. all elements that are not -1.
     * 
     * @param son   The sonority whose notes will be counted
     * @return      The number of notes in son
     */
	private static int countNotesInSonority(int[] son) {
		for (int i=0; i<son.length; i++){
			if (son[i] == -1){
				return son.length - i - 1;
			}
		}
		return son.length;
	}

	/**
     * Compares sonorities so that they can be ordered in sonority arrays. Every element is checked one by one,
     * from left to right, with smaller intervals ordered before larger intervals.
     * 
     * @param son_1     first sonority
     * @param son_2     second sonority
     * @return          comparison between first value that differs between the sonorities
     */
	private static int compareSonorities(int[] son_1, int[] son_2) {
		for (int i = 0; i <= son_1.length; i++) {
			int result = Integer.compare(son_1[i], son_2[i]);
			if (result != 0) {return result;}
		}
		return 0;
	}

    /**
     * Sorts the unique sonorities. They are first sorted by the amount of notes that they contain and then by
     * ascending interval size. If the first interval is the same size, the second interval is compared. If that
     * is also identical, the third interval is compared and so on.
     * 
     * @param unique_sonorities All unique vertical sonorities
     * @return                  Sorted array of the unique sonorities
     */
    private int[][] sortUniqueSonorities(int[][] unique_sonorities){
        Arrays.sort(unique_sonorities, Comparator
			.comparingInt(VerticalSonorityContainer::countNotesInSonority)
			.thenComparing(VerticalSonorityContainer::compareSonorities));
        return unique_sonorities;
    }

    /**
     * Create a histogram for the prevalence of each unique sonority. The histogram is weighted by the duration
     * of each sonority.
     * 
     * @param all_sonorities                All sonorities
     * @param unique_sonorities             All unique interval combinations in all_sonorities
     * @param NUMBER_OF_UNIQUE_SONORITIES   The number of unique interval combinations
     * @return                              A weighted histogram of the prevalence of the unique sonorities
     */
    private double[] initializeHistogram(
        int[][] all_sonorities,
        int[][] unique_sonorities,
        int NUMBER_OF_UNIQUE_SONORITIES) {

        double[] histogram = new double[NUMBER_OF_UNIQUE_SONORITIES];
        
        // Go through all unique sonorities
        for (int i = 0; i < NUMBER_OF_UNIQUE_SONORITIES; i++){
            // Go through all sonorities
            for (int j= 0; j < all_sonorities.length; j++){
                // Check if the intervals in unique_sonorities match those from sonorities
                if (Arrays.equals(unique_sonorities[i],
                    Arrays.copyOfRange(all_sonorities[j], 1, n_vson))){
                    // Scale by duration
                    histogram[i] += sonority_durations.get(j);
                }
            }
        }
        return histogram;
    }
}
