package jsymbolic2.gui;

/**
 * Created by dinamix on 5/30/16.
 */
public enum FeaturePanelEnum {
    horizontal_gap ("6"),
    vertical_gap ("11"),
    save_window_label ("Save Features For Each Window"),
    save_window_default ("false"),
    save_overall_label ("Save For Overall Recordings"),
    save_overall_default ("true"),
    convert_arff_label ("Convert from ACE XML to ARFF"),
    convert_arff_default ("true"),
	convert_csv_label ("Convert from ACE XML to CSV"),
    convert_csv_default ("true"),
	window_length_label ("Window Length (seconds):"),
    window_length_default ("0.0"),
    window_overlap_label ("Window Overlap (fraction):"),
    window_overlap_default ("0.0"),
    value_save_path_label ("ACE XML Feature Values Save Path:"),
    value_save_path_default ("feature_values_1.xml"),
    definition_save_path_label ("ACE XML Feature Definitions Save Path:"),
    definition_save_path_default ("feature_definitions_1.xml"),
    extract_feature_label ("Extract Features"),
    configuration_file_label ("Save GUI Configuration"),
    configuration_file_default ("jsymbolic_configuration.txt");

    private final String data;
    FeaturePanelEnum(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }
}
