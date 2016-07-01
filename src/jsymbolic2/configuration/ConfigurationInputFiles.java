package jsymbolic2.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A List for both the valid and invalid input files in the configuration file.
 *
 * @author Tristano Tenaglia
 */
public class ConfigurationInputFiles {
    private List<File> validFiles;
    private List<File> invalidFiles;

    public ConfigurationInputFiles() {
        this.validFiles = new ArrayList<>();
        this.invalidFiles = new ArrayList<>();
    }

    public List<File> getInvalidFiles() {
        return invalidFiles;
    }

    public List<File> getValidFiles() {
        return validFiles;
    }

    public void addInvalidFile(File file) {
        invalidFiles.add(file);
    }

    public void addValidFile(File file) {
        validFiles.add(file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationInputFiles that = (ConfigurationInputFiles) o;

        if (validFiles != null ? !validFiles.equals(that.validFiles) : that.validFiles != null) return false;
        return invalidFiles != null ? invalidFiles.equals(that.invalidFiles) : that.invalidFiles == null;

    }

    @Override
    public int hashCode() {
        int result = validFiles != null ? validFiles.hashCode() : 0;
        result = 31 * result + (invalidFiles != null ? invalidFiles.hashCode() : 0);
        return result;
    }
}
