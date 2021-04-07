import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

/**
 * This class represents a custom ToggleButton to show a property.
 * @author Jessy Briard, Ravshanbek Rozukulov
 */
public class PropertyButton extends ToggleButton {

    Insets vBoxPadding = new Insets(10, 10, 10, 10);


    public PropertyButton(AirbnbListing property, MapInfo mapInfo, ToggleGroup toggleGroup) {
        super("Host of the property: "+property.getHost_name()
                + "\nPrice: "+property.getPrice()
                + "\nNumber of reviews: "+property.getNumberOfReviews()
                + "\nMinimum nights: "+property.getMinimumNights());

        this.setToggleGroup(toggleGroup);
        this.getStyleClass().add("property-button");
        setPadding(vBoxPadding);
        setPrefWidth(mapInfo.getPrefWidth());
        setAlignment(Pos.BASELINE_LEFT);
    }

}
