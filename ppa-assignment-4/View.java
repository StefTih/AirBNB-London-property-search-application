import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.MenuBar;
import javafx.scene.text.*;

import java.awt.*;
//import java.awt.ScrollPane;
//import java.awt.MenuBar;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class View extends Application {

    private AirbnbDataLoader dataLoader = new AirbnbDataLoader();
    private ArrayList<AirbnbListing> properties = dataLoader.load();

    private BorderPane root;
    private Button backButton;
    private Button forwardButton;
    private ComboBox fromComboBox;
    private ComboBox toComboBox;
    private Integer fromPrice;
    private Integer toPrice;

    private ArrayList<Parent> centerPanels;
    private Parent welcomePanel = new Label("Welcome");
    private BorderPane mapPanel;
    private GridPane statisticsPanel;
    private int panelIndex = 0;

    private ArrayList<Statistic> statistics;
    private ArrayList<StatisticBox> statisticBoxes;

    //Stores an object of the array which contains all the borough names and their grid pane coordinates
    private BorderPane root2;
    //Stores an object containing the current properties available to show and extract from the map
    private MapInfo mapInfo;
    //A scrollbar for the property search window for each neighbourhood
    private ScrollPane scrollBar;
    //Stores the buttons that represent the neighbourhoods on the map
    private ArrayList<Button> mapButtons;
    //Stores the buttons that represent the properties in each neighbourhood
    private ArrayList<Button> propertyButtons;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        root = new BorderPane();
        primaryStage.setTitle("London Property Marketplace");
        primaryStage.setScene(new Scene(root, 0.8*screenSize.getWidth(), 0.8*screenSize.getHeight()));
        primaryStage.show();

        initialiseApplicationWindow();

        computeStatistics();
        initialiseStatisticsPanel();

        //Initialising the map panel in the GUI
        addBoroughsToMap();
        initialiseMapPanel();

        centerPanels = new ArrayList<Parent>(Arrays.asList(welcomePanel, mapPanel, statisticsPanel));
        root.setCenter(centerPanels.get(0));

    }

    // Application window methods

    private void initialiseApplicationWindow() {
        BorderPane topBar = new BorderPane();
        root.setTop(topBar);

        int hBoxSpacing = 10;
        Insets hBoxPadding = new Insets(10, 10, 10, 10);

        HBox navigationButtons = new HBox();
        topBar.setLeft(navigationButtons);
        navigationButtons.setSpacing(hBoxSpacing);
        navigationButtons.setPadding(hBoxPadding);
        backButton = new Button("<");
        backButton.setOnAction(this:: backButtonAction);
        backButton.setDisable(true);
        forwardButton = new Button(">");
        forwardButton.setOnAction(this:: forwardButtonAction);
        forwardButton.setDisable(true);
        navigationButtons.getChildren().addAll(backButton, forwardButton);

        HBox priceRangeComponents = new HBox();
        topBar.setRight(priceRangeComponents);
        priceRangeComponents.setAlignment(Pos.CENTER);
        priceRangeComponents.setSpacing(hBoxSpacing);
        priceRangeComponents.setPadding(hBoxPadding);
        Label fromLabel = new Label("From: ");
        Label toLabel = new Label("To: ");
        fromComboBox = new ComboBox();
        String[] fromItems = new String[] {"0", "100", "200"};
        addItemsToComboBox(fromComboBox, fromItems);
        fromComboBox.setOnAction(this:: fromComboBoxAction);
        toComboBox = new ComboBox();
        String[] toItems = new String[] {"100","200", "300"};
        addItemsToComboBox(toComboBox, toItems);
        toComboBox.setOnAction(this:: toComboBoxAction);
        priceRangeComponents.getChildren().addAll(fromLabel, fromComboBox, toLabel, toComboBox);
    }

    private void fromComboBoxAction(Event event) {
        Object item = fromComboBox.getSelectionModel().getSelectedItem();
        if (item != null) {
            fromPrice = Integer.parseInt(item.toString());
        } else {
            fromPrice = null;
        }
        enableButtons();
        computeProperties();
    }

    private void toComboBoxAction(Event event) {
        Object item = toComboBox.getSelectionModel().getSelectedItem();
        if (item != null) {
            toPrice = Integer.parseInt(item.toString());
        } else {
            toPrice = null;
        }
        enableButtons();
        computeProperties();
    }

    private void enableButtons()
    {
        boolean invalidRange = invalidPriceRange();
        backButton.setDisable(invalidRange);
        forwardButton.setDisable(invalidRange);
    }

    private boolean invalidPriceRange() {
        if (fromPrice != null && toPrice != null) {
            //If the price range is invalid
            if (toPrice-fromPrice < 0) {
                Alert invalidPriceRangeAlert = new Alert(Alert.AlertType.WARNING);
                invalidPriceRangeAlert.setTitle("Price range is invalid");
                invalidPriceRangeAlert.setHeaderText("The selected price range is invalid.");
                invalidPriceRangeAlert.setContentText("Please select a valid price range.");
                invalidPriceRangeAlert.showAndWait();
            }
        }
        return (fromPrice == null || toPrice == null || (toPrice-fromPrice) < 0);
    }


    private void backButtonAction(ActionEvent event) {
        panelIndex = (panelIndex-1+centerPanels.size())%(centerPanels.size());
        root.setCenter(centerPanels.get(panelIndex));
    }

    private void forwardButtonAction(ActionEvent event) {
        panelIndex = (panelIndex+1)%(centerPanels.size());
        root.setCenter(centerPanels.get(panelIndex));
    }


    private void addItemsToComboBox(ComboBox comboBox, String[] items)
    {
        for (String item: items) {
            comboBox.getItems().add(item);
        }
    }

    //Welcome window methods

    //Map window methods

    /**
     * This method populates the map with neighbourhoods
     */
    private void addBoroughsToMap()
    {
        mapInfo = new MapInfo();
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

    }

    private void linkAbbreviations()
    {
        for (String[] neighbourhoods: mapInfo.getLondonBoroughs())
        {
            mapInfo.addAbbreviations(neighbourhoods[0].substring(0,4).toUpperCase(),neighbourhoods[0]);
        }
    }

    /**
    This method creates the user interface for the map panel.
    **/
    private void initialiseMapPanel()
    {

        mapPanel = new BorderPane();
        //Create a top label with a message in it
        Label top = new Label("This the map of all the London boroughs and a relative " +
                "comparison to number of available properties in each borough");
        //It will center the label in the center
        top.setMaxWidth(Double.MAX_VALUE);
        top.setAlignment(Pos.CENTER);
        top.setFont(Font.font("Arial",FontWeight.BOLD,16));
        top.setTextFill(Color.INDIANRED);
        mapPanel.setTop(top);
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
        mapPanel.setCenter(center);
        BorderPane.setMargin(center,new Insets(5));

        //Create a VBOX pane which stores all the labels for the key
        //each one explaining the volume of properties in each borough
        VBox bottom = new VBox();

        Label key = new Label("Key:");
        key.setFont(Font.font("Arial",FontWeight.BOLD,12));

        Label lowVol = new Label("Low Volume of Properties: red " );
        lowVol.setFont(Font.font("Arial",FontWeight.BOLD,12));
        lowVol.styleProperty().set(mapInfo.getLowVol());

        Label medVol = new Label("Medium Volume of Properties: yellow");
        medVol.styleProperty().set(mapInfo.getMedVol());
        medVol.setFont(Font.font("Arial",FontWeight.BOLD,12));

        Label highVol = new Label("High Volume of Properties: green");
        highVol.styleProperty().set(mapInfo.getHighVol());
        highVol.setFont(Font.font("Arial",FontWeight.BOLD,12));

        bottom.getChildren().addAll(key,lowVol,medVol,highVol);


        mapPanel.setBottom(bottom);
        BorderPane.setMargin(bottom,new Insets(5));

    }

    /**
     * This method creates a new grid pane which will help in the creation of the map
     */
    private GridPane createMap()
    {
        GridPane boroughMap = new GridPane();
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
        setColour(mapButtons);
        return boroughMap;

    }

    /**
     * This method creates a new window if a button is clicked on the map. This window will show every
     * property in the neighbourhood chosen that is for rent at that given price.
     * @param boroughName the name of the borough is returned.
     */
    private void showPropertiesInBorough(String boroughName)
    {
        //Setting up the new stage
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle(boroughName);

        //Getting the dimensions of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //Creating the new Border pane
        root2 = new BorderPane();

        //Creating the top menu options
        makeMenuBar(boroughName);

        //Creating the scrollbar and setting it to the center of the pane
        scrollBar = new ScrollPane();
        scrollBar.setContent(addPropertyInfo(boroughName));
        root2.setCenter(scrollBar);

        //Creating the scene of the stage
        secondaryStage.setScene(new Scene(root2, 0.6*screenSize.getWidth(), 0.6*screenSize.getHeight()));
        secondaryStage.show();

    }

    /**
     * This method creates the menu bar which contains the sorting options for the user
     */
    private void makeMenuBar(String boroughName)
    {
        MenuBar menuBar = new MenuBar();

        // create the sort menu
        Menu sortMenu = new Menu("Sort By");

        MenuItem numReviews = new MenuItem("Number of Reviews");
        numReviews.setOnAction(p -> sortByNumReviews(boroughName));

        MenuItem price = new MenuItem("Price - cheapest to most expensive");
        price.setOnAction(p -> sortByPrice(boroughName));

        MenuItem hostName = new MenuItem("Host name");
        hostName.setOnAction(p -> sortByHostName(boroughName));

        sortMenu.getItems().addAll(numReviews,price,hostName);
        menuBar.getMenus().addAll(sortMenu);
        root2.setTop(menuBar);

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
    private void sortByPrice(String boroughName)
    {
        mapInfo.sortPropertyByPrice();
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
        mapInfo.setPropertyData(properties);
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

        Insets vBoxPadding = new Insets(10, 10, 10, 10);
        vBox.setAlignment(Pos.TOP_CENTER);
        propertyButtons = new ArrayList<>();


        for(AirbnbListing propertyInfo: mapInfo.getPropertyList(boroughName))
        {

            Button property = new Button("Host of the property: "+propertyInfo.getHost_name()
                    + "\nPrice: "+propertyInfo.getPrice()
                    +"\nNumber of reviews: "+propertyInfo.getNumberOfReviews()
                    +"\nMinimum number of nights that someone can stay: "+propertyInfo.getMinimumNights());
            property.setPadding(vBoxPadding);
            propertyButtons.add(property);
            property.setOnAction(p ->showDescription(propertyInfo.getId()));
        }

        vBox.getChildren().addAll(propertyButtons);
        return vBox;
    }

    /**
     * This method shows the description of each property on the right side of the border pane
     * @param propertyID holds the id of the property as a string
     */
    private void showDescription(String propertyID)
    {
        Label propertyDescription = new Label(mapInfo.showPropertyDescription(propertyID));
        root2.setRight(propertyDescription);
    }

    /**
     *Updates the colour of the map every time the user changes the price of the property
     * @param buttons It stores as a parameter an ArrayList of all the buttons which represent boroughs
     *                in the borough map.
     */
    private void setColour(ArrayList<Button> buttons)
    {
        for(Button button: buttons)
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

    //Statistics window methods

    private void computeProperties() {
        if (! invalidPriceRange()) {
            properties = dataLoader.load();

            properties.removeIf(p -> (p.getPrice() < fromPrice || p.getPrice() > toPrice));

            computeStatistics();

            updateStatistics();

            //Updates the details regarding the map panel
            mapInfo.setPropertyData(properties);

            setColour(mapButtons);

        }
    }


    private void computeStatistics() {
        statistics = new ArrayList<>();

        // Compute number of reviews
        String statName = "Average number of reviews per property";
        if (properties.isEmpty()) {
            statistics.add(new Statistic(statName, "-"));
        } else {
            double count = 0;
            for (AirbnbListing property : properties) {
                count += property.getNumberOfReviews();
            }
            statistics.add(new Statistic(statName, String.valueOf(count/properties.size())));
        }

        // Compute number of available properties
        statName = "Total number of available properties";
        statistics.add(new Statistic(statName, String.valueOf(properties.size())));

        // Compute number of entire homes and apartments
        statName = "Number of entire homes and apartments";
        statistics.add(new Statistic(statName,
            String.valueOf(properties.stream().filter(p -> (p.getRoom_type().equals("Entire home/apt"))).count())
        ));

        // Compute most expensive borough
        statName = "Most expensive borough";
        // TO CHANGE
        statistics.add(new Statistic(statName, "Waiting for list of boroughs"));

        // TEST TO REMOVE
        statistics.add(new Statistic("Test", "Test Value"));

    }


    private void initialiseStatisticsPanel() {
        statisticsPanel = new GridPane();
        statisticsPanel.setPadding(new Insets(10,10,10,10));
        statisticsPanel.setHgap(50);
        statisticsPanel.setVgap(50);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        statisticsPanel.getColumnConstraints().add(columnConstraints);
        statisticsPanel.getColumnConstraints().add(columnConstraints);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(50);
        statisticsPanel.getRowConstraints().add(rowConstraints);
        statisticsPanel.getRowConstraints().add(rowConstraints);


        statisticBoxes = new ArrayList<>();
        StatisticBox statisticBox1 = new StatisticBox(this);
        statisticsPanel.add(statisticBox1, 0, 0);
        statisticBoxes.add(statisticBox1);
        StatisticBox statisticBox2 = new StatisticBox(this);
        statisticsPanel.add(statisticBox2, 0, 1);
        statisticBoxes.add(statisticBox2);
        StatisticBox statisticBox3 = new StatisticBox(this);
        statisticsPanel.add(statisticBox3, 1, 0);
        statisticBoxes.add(statisticBox3);
        StatisticBox statisticBox4 = new StatisticBox(this);
        statisticsPanel.add(statisticBox4, 1, 1);
        statisticBoxes.add(statisticBox4);

    }

    public ArrayList<Statistic> getStatistics() {
        return statistics;
    }

    public boolean statisticUsed(StatisticBox statisticBox, int index) {
        for (StatisticBox box: statisticBoxes) {
            if (box != statisticBox && box.getStatisticIndex() == index) {
                return true;
            }
        }
        return false;
    }

    private void updateStatistics() {
        for (StatisticBox box: statisticBoxes) {
            box.update();
        }
    }


}
