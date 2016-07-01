package jsymbolic2.configuration;

/**
 * The state of all the options taken from the configuration file.
 *
 * @author Tristano Tenaglia
 */
public class ConfigurationOptionState {
    private double window_size;
    private double window_overlap;
    private boolean save_features_for_each_window;
    private boolean save_overall_recording_features;
    private boolean convert_to_arff;
    private boolean convert_to_csv;

    public ConfigurationOptionState(double window_size,
                                    double window_overlap,
                                    boolean save_features_for_each_window,
                                    boolean save_overall_recording_features,
                                    boolean convert_to_arff,
                                    boolean convert_to_csv)
    {
        this.window_size = window_size;
        this.window_overlap = window_overlap;
        this.save_features_for_each_window = save_features_for_each_window;
        this.save_overall_recording_features = save_overall_recording_features;
        this.convert_to_arff = convert_to_arff;
        this.convert_to_csv = convert_to_csv;
    }

    public boolean isConvert_to_csv() {
        return convert_to_csv;
    }

    public boolean isConvert_to_arff() {
        return convert_to_arff;
    }

    public boolean isSave_overall_recording_features() {
        return save_overall_recording_features;
    }

    public boolean isSave_features_for_each_window() {
        return save_features_for_each_window;
    }

    public double getWindow_overlap() {
        return window_overlap;
    }

    public double getWindow_size() {
        return window_size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationOptionState that = (ConfigurationOptionState) o;

        if (Double.compare(that.window_size, window_size) != 0) return false;
        if (Double.compare(that.window_overlap, window_overlap) != 0) return false;
        if (save_features_for_each_window != that.save_features_for_each_window) return false;
        if (save_overall_recording_features != that.save_overall_recording_features) return false;
        if (convert_to_arff != that.convert_to_arff) return false;
        return convert_to_csv == that.convert_to_csv;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(window_size);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(window_overlap);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (save_features_for_each_window ? 1 : 0);
        result = 31 * result + (save_overall_recording_features ? 1 : 0);
        result = 31 * result + (convert_to_arff ? 1 : 0);
        result = 31 * result + (convert_to_csv ? 1 : 0);
        return result;
    }
}
