import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class represents the "Map Panel", as a BorderPane.
 * @author Tihomir Stefanov, Alexandru Bularca, Jessy Briard, Ravshanbek Rozukulov
 */
public class MapPanel extends BorderPane {

    private View view;

    //Stores an object containing the current properties available to show and extract from the map
    private MapInfo mapInfo;
    //Collection of London boroughs (and their index on the map)
    private String[][] londonBoroughs;
    //Stores an object of the array which contains all the borough names and their grid pane coordinates
    private BorderPane root2;
    //A scrollbar for the property search window for each neighbourhood
    private ScrollPane scrollBar;
    //Stores the buttons that represent the neighbourhoods on the map
    private ArrayList<Button> mapButtons;
    //Stores the buttons that represent the properties in each neighbourhood
    private ArrayList<ToggleButton> propertyButtons;
    //Stores the info message displayed in the borough properties method when none is selected
    private Label noPropertySelected;


    public MapPanel(View view, MapInfo mapInfo) {
        super();
        this.view = view;
        this.mapInfo = mapInfo;
        addBoroughsToMap();
        initialiseMapPanel();
    }


    /**
     This method creates the user interface for the map panel.
     **/
    private void initialiseMapPanel()
    {

        setId("map-panel");
        //Create a top label with a message in it
        Label top = new Label("Map of London boroughs and relative availability of " +
                "properties at each borough at the given price range.");
        top.setId("map-label");
        //It will center the label in the center
        top.setMaxWidth(Double.MAX_VALUE);
        top.setAlignment(Pos.CENTER);
        top.setFont(Font.font("Arial", FontWeight.BOLD,16));
        top.setTextFill(Color.INDIANRED);
        setTop(top);
        BorderPane.setMargin(top,new Insets(5));

        //Creates the map of the London boroughs in the center panel
        GridPane center = createMap();
        //It will center the label in the center
        center.setMaxWidth(Double.MAX_VALUE);
        center.setAlignment(Pos.CENTER);
        //Creates a verticaly gap between the buttons
        center.setVgap(10);
        //Sets the padding between the map and the edges of the window
        center.setPadding(new Insets(0, 50, 0, 50));
        setCenter(center);
        BorderPane.setMargin(center,new Insets(5));

        //Create a VBOX pane which stores all the labels for the key
        //each one explaining the volume of properties in each borough
        VBox bottom = new VBox();

        Font font = Font.font("Arial",FontWeight.BOLD,12);

        Label key = new Label("Key:");
        key.setFont(font);

        Label noProperty = new Label("No corresponding Properties: grey");
        noProperty.setFont(font);
        noProperty.styleProperty().set(mapInfo.getNoProperty());

        Label lowVol = new Label("Low Volume of Properties: red");
        lowVol.setFont(font);
        lowVol.styleProperty().set(mapInfo.getLowVol());

        Label medVol = new Label("Medium Volume of Properties: yellow");
        medVol.styleProperty().set(mapInfo.getMedVol());
        medVol.setFont(font);

        Label highVol = new Label("High Volume of Properties: green");
        highVol.styleProperty().set(mapInfo.getHighVol());
        highVol.setFont(font);

        bottom.getChildren().addAll(key, noProperty, lowVol,medVol,highVol);


        setBottom(bottom);
        BorderPane.setMargin(bottom,new Insets(5));

    }

    /**
     * This method populates the map with neighbourhoods.
     */
    private void addBoroughsToMap()
    {
        mapInfo.addBoroughs(0, "Enfield", "7", "0");
        mapInfo.addBoroughs(1, "Barnet", "4", "1");
        mapInfo.addBoroughs(2, "Haringey", "6", "1");
        mapInfo.addBoroughs(3, "Waltham Forest", "8", "1");
        mapInfo.addBoroughs(4, "Harrow", "1", "2");
        mapInfo.addBoroughs(5, "Brent", "3", "2");
        mapInfo.addBoroughs(6, "Camden", "5", "2");
        mapInfo.addBoroughs(7, "Islington", "7", "2");
        mapInfo.addBoroughs(8, "Hackney", "9", "2");
        mapInfo.addBoroughs(9, "Redbridge", "11", "2");
        mapInfo.addBoroughs(10, "Havering", "13", "2");
        mapInfo.addBoroughs(11, "Hillingdon", "0", "3");
        mapInfo.addBoroughs(12, "Ealing", "2", "3");
        mapInfo.addBoroughs(13, "Kensington and Chelsea", "4", "3");
        mapInfo.addBoroughs(14, "Westminster", "6", "3");
        mapInfo.addBoroughs(15, "Tower Hamlets", "8", "3");
        mapInfo.addBoroughs(16, "Newham", "10", "3");
        mapInfo.addBoroughs(17, "Barking and Dagenham", "12", "3");
        mapInfo.addBoroughs(18, "Hounslow", "1", "4");
        mapInfo.addBoroughs(19, "Hammersmith and Fulham", "3", "4");
        mapInfo.addBoroughs(20, "Wandsworth", "5", "4");
        mapInfo.addBoroughs(21, "City of London", "7", "4");
        mapInfo.addBoroughs(22, "Greenwich", "9", "4");
        mapInfo.addBoroughs(23, "Bexley", "11", "4");
        mapInfo.addBoroughs(24, "Richmond upon Thames", "2", "5");
        mapInfo.addBoroughs(25, "Merton", "4", "5");
        mapInfo.addBoroughs(26, "Lambeth", "6", "5");
        mapInfo.addBoroughs(27, "Southwark", "8", "5");
        mapInfo.addBoroughs(28, "Lewisham", "10", "5");
        mapInfo.addBoroughs(29, "Kingston upon Thames", "3", "6");
        mapInfo.addBoroughs(30, "Sutton", "5", "6");
        mapInfo.addBoroughs(31, "Croydon", "7", "6");
        mapInfo.addBoroughs(32, "Bromley", "9", "6");


        linkAbbreviations();

        londonBoroughs = mapInfo.getLondonBoroughs();

    }

    /**
     * This method links each (String) borough to its abbreviation.
     */
    private void linkAbbreviations()
    {
        for (String[] neighbourhoods: mapInfo.getLondonBoroughs())
        {
            mapInfo.addAbbreviations(neighbourhoods[0].substring(0,4).toUpperCase(),neighbourhoods[0]);
        }
    }


    /**
     * This method creates a new grid pane which will help in the creation of the map.
     * @return  The created Map (as GridPane)
     */
    private GridPane createMap()
    {
        GridPane boroughMap = new GridPane();
        boroughMap.setId("map-grid");
        Integer xCoordinate;
        Integer yCoordinate;

        mapButtons = new ArrayList<>();
        for(String[] borough: mapInfo.getLondonBoroughs())
        {

            xCoordinate = mapInfo.convertInt(borough[1]);
            yCoordinate = mapInfo.convertInt(borough[2]);
            if (xCoordinate!=-1 && yCoordinate!=-1)
            {
                Button BOR = new Button(mapInfo.getAbbreviation(borough[0]));
                BOR.setOnAction(p -> showPropertiesInBorough(borough[0]));
                mapButtons.add(BOR);
                boroughMap.add(BOR,xCoordinate,yCoordinate);
            }
            else
            {
                System.out.println("The coordinates cannot be entered because they are not of Int type!");
            }

        }
        setColour();
        return boroughMap;

    }

    /**
     * This method creates a new window if a button is clicked on the map. This window will show every
     * property in the neighbourhood chosen that is for rent at that given price.
     * @param boroughName the name of the borough is returned.
     */
    private void showPropertiesInBorough(String boroughName)
    {
        if (mapInfo.getNumberOfOccurrences(boroughName) != 0) {
            //Setting up the new stage
            Stage secondaryStage = new Stage();
            secondaryStage.setTitle(boroughName);

            //Getting the dimensions of the screen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            //Creating the new Border pane
            root2 = new BorderPane();
            root2.setId("borough-pane");

            HBox topNav = new HBox();
            topNav.getStyleClass().add("top-nav");

            Label sortLabel = new Label("Sort by: ");

            ComboBox<String> sortCombo = new ComboBox();
            sortCombo.setPromptText("Number of Reviews");
            sortCombo.getItems().addAll("Number of Reviews", "Price(Low - High)", "Price(High - Low)", "Host Name(A - Z)");
            sortCombo.setOnAction(event -> sortSelected(sortCombo.getSelectionModel().getSelectedItem(), boroughName));
            topNav.getChildren().addAll(sortLabel, sortCombo);

            root2.setTop(topNav);

            //Creating the scrollbar and setting it to the left of the pane
            scrollBar = new ScrollPane();
            scrollBar.getStyleClass().add("results-scroll-pane");
            scrollBar.setPrefWidth(mapInfo.getPrefWidth() + 40);
            scrollBar.setContent(addPropertyInfo(boroughName));
            sortByNumReviews(boroughName);
            root2.setLeft(scrollBar);

            //Create the info label displayed when no property is selected
            noPropertySelected = new Label("No property selected");
            noPropertySelected.setAlignment(Pos.CENTER);
            noPropertySelected.getStyleClass().add("not-selected");
            root2.setCenter(noPropertySelected);

            //Creating the scene of the stage
            Scene secondaryScene = new Scene(root2, 0.6*screenSize.getWidth(), 0.6*screenSize.getHeight());
            secondaryScene.getStylesheets().add("main.css");
            secondaryStage.setScene(secondaryScene);
            secondaryStage.show();
        } else {
            // Show an Alert Dialog
            Alert noPropertyAlert = new Alert(Alert.AlertType.ERROR);
            noPropertyAlert.setTitle("No Property");
            noPropertyAlert.setHeaderText("There is no property in this borough corresponding to the selected price range.");
            noPropertyAlert.setContentText("Please select another borough or price range.");
            noPropertyAlert.showAndWait();
        }
    }

    /**
     * Sort the list of properties according to the sorting method chosen by the user.
     * @param type The selected sorting method
     * @param boroughName The name of the concerned borough
     */
    private void sortSelected(String type, String boroughName)
    {
        switch (type) {
            case "Number of Reviews":
                sortByNumReviews(boroughName);
                break;
            case "Price(Low - High)":
                sortByPriceLowToHigh(boroughName);
                break;
            case "Price(High - Low)":
                sortByPriceHighToLow(boroughName);
                break;
            case "Host Name(A - Z)":
                sortByHostName(boroughName);
                break;
        }
    }


    /**
     * This method calls the sorting method in the MapInfo class based on number of reviews.
     * @param boroughName the parameter which stores the name of the neighbourhood
     */
    private void sortByNumReviews(String boroughName)
    {
        mapInfo.sortPropertyByNumReviews();
        refresh(boroughName);
    }

    /**
     * This method calls the sorting method in the MapInfo class based on price.
     * @param boroughName the parameter which stores the name of the neighbourhood
     */
    private void sortByPriceLowToHigh(String boroughName)
    {
        mapInfo.sortPropertyByPriceLowToHigh();
        refresh(boroughName);
    }

    /**
     * This method calls the sorting method in the MapInfo class based on price.
     * @param boroughName the parameter which stores the name of the neighbourhood
     */
    private void sortByPriceHighToLow(String boroughName)
    {
        mapInfo.sortPropertyByPriceHighToLow();
        refresh(boroughName);
    }

    /**
     * This method calls the sorting method in the MapInfo class based on host name alphabetically.
     * @param boroughName the parameter which stores the name of the neighbourhood
     */
    private void sortByHostName(String boroughName)
    {
        mapInfo.sortPropertyByHostName();
        refresh(boroughName);
    }

    /**
     * resets the contents inside the ScrollPane to represent the new sorted list of properties
     * @param boroughName the parameter which stores the name of the neighbourhood
     */
    private void refresh(String boroughName)
    {
        scrollBar.setContent(addPropertyInfo(boroughName));
        mapInfo.setPropertyData(view.getProperties());
    }

    /**
     * This method includes all the property info for each property for a specific neighbourhood
     * and represents its details as buttons.
     * @param boroughName the name of the neighbourhood as a String
     * @return the vBox pane which stores all the buttons stacked one on to of the other.
     */
    private VBox addPropertyInfo(String boroughName)
    {
        VBox vBox = new VBox();

        vBox.setAlignment(Pos.TOP_CENTER);
        propertyButtons = new ArrayList<>();

        ToggleGroup propertiesToggleGroup = new ToggleGroup();


        for(AirbnbListing propertyInfo: mapInfo.getPropertyList(boroughName))
        {
            ToggleButton property = new PropertyButton(propertyInfo, mapInfo, propertiesToggleGroup);
            propertyButtons.add(property);
            property.setOnAction(p -> boroughPropertyToggled(property, propertyInfo));
        }

        vBox.getChildren().addAll(propertyButtons);
        return vBox;
    }

    /**
     * Toggle the display of information about clicked property in the borough window.
     * @param button Button clicked
     * @param property Property linked to the button
     */
    private void boroughPropertyToggled(ToggleButton button, AirbnbListing property)
    {
        if (button.isSelected()) {
            showDescription(property);
        } else {
            root2.setCenter(noPropertySelected);
        }
    }

    /**
     * This method shows the description of each property on the right side of the border pane
     * @param property holds the property as an instance of AirbnbListing
     */
    private void showDescription(AirbnbListing property)
    {
        Label propertyDescription = new Label(mapInfo.showPropertyDescription(property.getId()));
        propertyDescription.getStyleClass().add("property-description");
        root2.setCenter(propertyDescription);

        view.addViewedProperty(property);
    }

    /**
     *Updates the colour of the map every time the user changes the price of the property
     */
    public void setColour()
    {
        for(Button button: mapButtons)
        {
            button.styleProperty().set(changeColour(mapInfo.getNeighbourhood(button.getText())));
        }

    }

    /**
     * This method stores as a parameter the name of the borough/neighbourhood and using the name calls
     * a public method from the MapInfo class which will return instead a string with the appropriate
     * colour to set for this button on the map.
     * @param neighbourhood a string which contains the name of the neighbourhood
     * @return a CSS colour style string.
     */
    private String changeColour(String neighbourhood)
    {
        return mapInfo.propertyVolumeColour(neighbourhood);
    }

    /**
     * Get the collection of London boroughs (and their index on the map).
     * @return The collection of London boroughs
     */
    public String[][] getLondonBoroughs() {
        return londonBoroughs;
    }


}
