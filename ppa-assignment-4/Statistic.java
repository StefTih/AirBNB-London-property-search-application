/**
 * This class represents a single statistic.
 * It holds a statistic name and its value.
 * @author Jessy Briard
 */

public class Statistic {

    private String name;
    private String value;

    public Statistic (String name) {
        this.name = name;
        this.value = "-";
    }

    /**
     * Get the name of the statistic.
     * @return The name of the statistic
     */
    public String getName() {
        return name;
    }

    /**
     * Get the statistic's value.
     * @return The statistic's value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the statistic's new value.
     * @param newValue The statistic's new value
     */
    public void setValue(String newValue) {
        if (newValue != null) {
            value = newValue;
        } else {
            value = "-";
        }
    }
}
