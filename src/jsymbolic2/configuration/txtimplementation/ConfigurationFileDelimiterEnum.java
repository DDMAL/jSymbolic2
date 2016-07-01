package jsymbolic2.configuration.txtimplementation;

/**
 * The delimiter use to separate variable names from variable values in
 * the .txt implementation of the configuration file.
 *
 * @author Tristano Tenaglia
 */
public enum ConfigurationFileDelimiterEnum {
    EQUAL("=");

    private final String delimiter;
    ConfigurationFileDelimiterEnum(String delimiter){
        this.delimiter = delimiter;
    }

    /**
     *
     * @return The string format of the enum. In this case an "=" sign.
     */
    @Override
    public String toString() {
        return delimiter;
    }
}
