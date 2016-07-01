package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configuration.OptionsEnum;

/**
 * Convenience class to split optionName and optionValue for the .txt
 * implementation of the configuration file.
 *
 * @author Tristano Tenaglia
 */
public class ConfigurationOption {
    private OptionsEnum optionName;
    private String optionValue;

    public ConfigurationOption(OptionsEnum optionName, String optionValue) {
        this.optionName = optionName;
        this.optionValue = optionValue;
    }

    public OptionsEnum getOptionName() {
        return optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }
}
