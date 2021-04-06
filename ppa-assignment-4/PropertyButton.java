import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;

/**
 * This class represents a custom ToggleButton to show a property.
 * @author Jessy Briard, Ravshanbek Rozukulov
 */
public class PropertyButton extends ToggleButton {

    Insets vBoxPadding = new Insets(10, 10, 10, 10);


    public PropertyButton(AirbnbListing property, MapInfo mapInfo) {
        super("Host of the property: "+property.getHost_name()
                + "\nPrice: "+property.getPrice()
                +"\nNumber of reviews: "+property.getNumberOfReviews()
                +"\nMinimum number of nights that someone can stay: "+property.getMinimumNights());

        setPadding(vBoxPadding);
        setPrefWidth(mapInfo.getPrefWidth());
        setAlignment(Pos.BASELINE_LEFT);
    }

}
