package jsymbolic2.configuration;

/**
 * The state of the output files specified by the configuration file.
 *
 * @author Tristano Tenaglia
 */
public class ConfigurationOutputFiles {
    private String feature_values_save_path;
    private String feature_definition_save_path;

    public ConfigurationOutputFiles(String feature_values_save_path, String feature_definition_save_path) {
        this.feature_values_save_path = feature_values_save_path;
        this.feature_definition_save_path = feature_definition_save_path;
    }

    public String getFeature_definition_save_path() {
        return feature_definition_save_path;
    }

    public String getFeature_values_save_path() {
        return feature_values_save_path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationOutputFiles that = (ConfigurationOutputFiles) o;

        if (feature_values_save_path != null ? !feature_values_save_path.equals(that.feature_values_save_path) : that.feature_values_save_path != null)
            return false;
        return feature_definition_save_path != null ? feature_definition_save_path.equals(that.feature_definition_save_path) : that.feature_definition_save_path == null;

    }

    @Override
    public int hashCode() {
        int result = feature_values_save_path != null ? feature_values_save_path.hashCode() : 0;
        result = 31 * result + (feature_definition_save_path != null ? feature_definition_save_path.hashCode() : 0);
        return result;
    }
}
