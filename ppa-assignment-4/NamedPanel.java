import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * This class represents a named Panel, it holds a reference to the Panel object and its given name.
 * @author Jessy Briard, Ravshanbek Rozukulov
 */
public class NamedPanel {

    Parent panel;
    Label label;

    public NamedPanel(Parent panel, Label label) {
        this.panel = panel;
        this.label = label;
    }

    /**
     * Get the Panel
     * @return The stored Panel
     */
    public Parent getPanel() {
        return panel;
    }

    /**
     * Get the Panel's given name
     * @return The Panel's name
     */
    public Label getLabel() {
        return label;
    }
}
