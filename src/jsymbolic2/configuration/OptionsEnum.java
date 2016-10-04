package jsymbolic2.configuration;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * All the headers (i.e. sections) required by the jSymbolic configuration file.
 *
 * @author Tristano Tenaglia
 */
public enum OptionsEnum {
    window_size,
    window_overlap,
    save_features_for_each_window,
    save_overall_recording_features,
    convert_to_arff,
    convert_to_csv;

    /**
     * Set where all enum elements are stored for easy lookup.
     */
    private static final Set<String> layerChildHashNames = new HashSet<>();

    static {
        for (OptionsEnum value : EnumSet.allOf(OptionsEnum.class)) {
            layerChildHashNames.add(value.name());
        }
    }

    /**
     * Check to see if this enum is associated to the appropriate value type.
     *
     * @param name Name of mei element to be checked
     * @return true if the given name is part of this enum
     */
    public static boolean contains(String name) {
        return layerChildHashNames.contains(name);
    }

    /**
     *
     * @param values Compare all the enum names to the names given in the values.
     * @return True if all the names are in fact in the input values list, otherwise false.
     */
    public static boolean allOptionsExist(List<String> values) {
        for(String s : layerChildHashNames) {
            if (!values.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check to ensure that the value type  matches the string representation of the value passed in
     * and the corresponding enum type specified in the switch statement.
     * @param value The value that needs to be compared to.
     * @return True if the type of the passed in value matches the corresponding enum type,
     * otherwise if its not one of the enum types, then false.
     */
    public boolean checkValue(String value) {
        switch (this) {
            case window_size:
                //Greater than 0.1 as per the GUI checks
                return value.matches("\\d*\\.?\\d*") && (Double.parseDouble(value) >= 0.0);
            case window_overlap:
                //Between 0 and 1 (not inclusive) as per the GUI checks
                return value.matches("0?.\\d*") && (Double.parseDouble(value) >= 0) && (Double.parseDouble(value) < 1.0);
            case save_features_for_each_window:
                return isBoolean(value);
            case save_overall_recording_features:
                return isBoolean(value);
            case convert_to_arff:
                return isBoolean(value);
            case convert_to_csv:
                return isBoolean(value);
            default:
                return false;
        }
    }

    /**
     *
     * @param value The value string that needs to be checked.
     * @return True if the value passed in is either the string "true" or "false", otherwise false.
     */
    private static boolean isBoolean(String value) {
        return "true".equals(value) || "false".equals(value);
    }
}
