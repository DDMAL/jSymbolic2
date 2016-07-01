package jsymbolic2.processing;

/**
 * A convenience state class to hold the state of converted files for jSymbolic if necessary.
 *
 * @author Tristano Tenaglia
 */
public class AceConversionPaths {
    private final String arff_file_path;
    private final String csv_arff_file_path;

    public AceConversionPaths(String arff_file_path, String csv_arff_file_path) {
        this.arff_file_path = arff_file_path;
        this.csv_arff_file_path = csv_arff_file_path;
    }

    public String getArff_file_path() {
        return arff_file_path;
    }

    public String getCsv_arff_file_path() {
        return csv_arff_file_path;
    }
}
