package jsymbolic2.configuration;

import java.util.*;

/**
 * All the headers (i.e. sections) required by the jSymbolic configuration file.
 *
 * @author Tristano Tenaglia
 */
public enum ConfigFileHeaderEnum {
    FEATURE_HEADER ("<features_to_extract>"),
    OPTION_HEADER ("<jSymbolic_options>"),
    INPUT_FILE_HEADER ("<input_files>"),
    OUTPUT_FILE_HEADER ("<output_files>");

    /**
     * Set where all enum elements are stored for easy lookup.
     */
    private static final Set<String> headerNames = new HashSet<>();

    static {
        for (ConfigFileHeaderEnum value : EnumSet.allOf(ConfigFileHeaderEnum.class)) {
            headerNames.add(value.toString());
        }
    }

    /**
     * Check to see if this enum is associated to the appropriate value type.
     *
     * @param name Name of mei element to be checked
     * @return true if the given name is part of this enum
     */
    public static boolean contains(String name) {
        return headerNames.contains(name);
    }

    private final String text;

    private ConfigFileHeaderEnum(final String text) {
        this.text = text;
    }

    /**
     *
     * @return All the enums in this class in a list.
     */
    public static List<ConfigFileHeaderEnum> asList() {
        return Arrays.asList(values());
    }

    @Override
    public String toString() {
        return text;
    }
}
