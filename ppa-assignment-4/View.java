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
import java.util.*;
import java.util.concurrent.Flow;

/**
 * This class represents the view of our London Property Marketplace Application.
 * @author Jessy Briard, Tihomir Stefanov, Alexandru Bularca, Ravshanbek Rozukulov
 */

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
    private BorderPane welcomePanel;
    private BorderPane mapPanel;
    private GridPane statisticsPanel;
    // The index of the current shown panel
    private int panelIndex = 0;

    // The welcome page
    private Label welcomePriceLabel;

    // The statistics
    private Statistic statAvgReviews = new Statistic("Average number of reviews per property");
    private Statistic statNbOfProperties = new Statistic("Total number of available properties");
    private Statistic statNbOfEntireHomeApartments = new Statistic("Number of entire homes and apartments");
    private Statistic statMostExpensiveBorough = new Statistic("Most expensive borough");
    // ADD 4 ADDITIONAL STATISTICS
    private ArrayList<Statistic> statistics = new ArrayList<>(Arrays.asList(statAvgReviews, statNbOfProperties, statNbOfEntireHomeApartments, statMostExpensiveBorough, new Statistic("TEST")));

    // Collection of all 4 "Statistic Boxes"
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


    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start the JavaFx Application
     * @param primaryStage The stage of the GUI
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        root = new BorderPane();
        primaryStage.setTitle("London Property Marketplace");
        // The size of the window is adapted to the size of the screen

        Scene primaryScene = new Scene(root, 0.8*screenSize.getWidth(), 0.8*screenSize.getHeight());
        primaryScene.getStylesheets().addAll("main.css");
        primaryStage.setScene(primaryScene);
        primaryStage.show();

        //Initialising the "Application Window" in the GUI
        initialiseApplicationWindow();

        //Initialising the "Welcome Panel" in the GUI
        initialiseWelcomePanel();

        //Initialising the "Map Panel" in the GUI
        addBoroughsToMap();
        initialiseMapPanel();

        //Initialising the "Statistics Panel" in the GUI
        computeStatistics();
        initialiseStatisticsPanel();

        centerPanels = new ArrayList<Parent>(Arrays.asList(welcomePanel, mapPanel, statisticsPanel));
        root.setCenter(centerPanels.get(0)); // Show the first panel ("Welcome Panel") in the Application

    }




    // Application window methods

    /**
     * Create the JavaFX interface for the "Application Window" (to navigate between panels and select a price range).
     */
    private void initialiseApplicationWindow() {
        BorderPane topBar = new BorderPane();
        root.setTop(topBar);

        // Common parameters for HBoxes
        int hBoxSpacing = 10;
        Insets hBoxPadding = new Insets(10, 10, 10, 10);

        // Create the "<" and ">" buttons to navigate between panels
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

        // Create the DropDown Lists for the user to select a property price range
        HBox priceRangeComponents = new HBox();
        topBar.setRight(priceRangeComponents);
        priceRangeComponents.setAlignment(Pos.CENTER);
        priceRangeComponents.setSpacing(hBoxSpacing);
        priceRangeComponents.setPadding(hBoxPadding);
        Label fromLabel = new Label("From: ");
        Label toLabel = new Label("To: ");
        fromComboBox = new ComboBox(); // DropDown List for minimum price
        String[] fromItems = new String[] {
                "0", "50", "100", "150", "200", "250", "300", "350", "400", "450", "500", "1000"};
        addItemsToComboBox(fromComboBox, fromItems);
        fromComboBox.setOnAction(this:: fromComboBoxAction);
        toComboBox = new ComboBox(); // DropDown List for maximum price
        String[] toItems = new String[] {"50", "100", "150", "200", "250", "300", "350", "400", "450", "500", "1000", "10000"};
        addItemsToComboBox(toComboBox, toItems);
        toComboBox.setOnAction(this:: toComboBoxAction);
        priceRangeComponents.getChildren().addAll(fromLabel, fromComboBox, toLabel, toComboBox);
    }

    /**
     * Action to execute when the user interacts with the minimum price DropDown List.
     * Adapt the information shown by the Application according to the selected price range.
     * @param event The Event triggered by the user
     */
    private void fromComboBoxAction(Event event) {
        Object item = fromComboBox.getSelectionModel().getSelectedItem(); // Price selected by the user
        if (item != null) {
            fromPrice = Integer.parseInt(item.toString());
        } else {
            fromPrice = null;
        }
        boolean invalidRange = invalidPriceRange();
        enableButtons(invalidRange); // Enable or Disable the navigation between panels depending on the validity of the selected price range
        if (! invalidRange) {
            computeProperties(); // Collect the proprieties that correspond to the selected price range
        }
        showPriceRange(invalidRange);
    }

    /**
     * Action to execute when the user interacts with the minimum price DropDown List.
     * Adapt the information shown by the Application according to the selected price range.
     * @param event The Event triggered by the user
     */
    private void toComboBoxAction(Event event) {
        Object item = toComboBox.getSelectionModel().getSelectedItem(); // Price selected by the user
        if (item != null) {
            toPrice = Integer.parseInt(item.toString());
        } else {
            toPrice = null;
        }
        boolean invalidRange = invalidPriceRange();
        enableButtons(invalidRange); // Enable or Disable the navigation between panels depending on the validity of the selected price range
        if (! invalidRange) {
            computeProperties(); // Collect the proprieties that correspond to the selected price range
        }
        showPriceRange(invalidRange);
    }

    /**
     * Enable or Disable the "<" and ">" buttons to navigate between panels,
     * depending on the validity of the price range selected by the user.
     * @param invalidRange Whether the price range selected by the user is invalid
     */
    private void enableButtons(boolean invalidRange)
    {
        backButton.setDisable(invalidRange);
        forwardButton.setDisable(invalidRange);
    }

    /**
     * Whether the price range selected by the user is invalid.
     * @return true if the selected price range is invalid, else (it is valid) return false
     */
    private boolean invalidPriceRange() {
        if (fromPrice != null && toPrice != null) { // If both boundaries are selected
            if (toPrice-fromPrice < 0) { //If the price range is invalid
                // Show an Alert Dialog
                Alert invalidPriceRangeAlert = new Alert(Alert.AlertType.WARNING);
                invalidPriceRangeAlert.setTitle("Price range is invalid");
                invalidPriceRangeAlert.setHeaderText("The selected price range is invalid.");
                invalidPriceRangeAlert.setContentText("Please select a valid price range.");
                invalidPriceRangeAlert.showAndWait();
            }
        }
        // Return whether the selected price range is invalid
        return (fromPrice == null || toPrice == null || (toPrice-fromPrice) < 0);
    }


    /**
     * Action to execute when the user clicks on the "<" button.
     * The Application shows the previous panel.
     * @param event The ActionEvent triggered by the user
     */
    private void backButtonAction(ActionEvent event) {
        panelIndex = (panelIndex-1+centerPanels.size())%(centerPanels.size()); // Index of previous panel
        root.setCenter(centerPanels.get(panelIndex));
    }

    /**
     * Action to execute when the user clicks on the ">" button.
     * The Application shows the next panel.
     * @param event The ActionEvent triggered by the user
     */
    private void forwardButtonAction(ActionEvent event) {
        panelIndex = (panelIndex+1)%(centerPanels.size()); // Index of next panel
        root.setCenter(centerPanels.get(panelIndex));
    }


    /**
     * Add all items from an array into a ComboBox
     * @param comboBox The ComboBox to add the items into
     * @param items The array of items to add to the ComboBox
     */
    private void addItemsToComboBox(ComboBox comboBox, String[] items)
    {
        for (String item: items) {
            comboBox.getItems().add(item);
        }
    }




    //Welcome window methods
    private void initialiseWelcomePanel()
    {
        Insets welcomeInsets = new Insets(200, 30, 30, 30);

        welcomePanel = new BorderPane();
        welcomePanel.setId("welcome-panel");

        VBox welcomeCenter = new VBox();
        welcomeCenter.setId("welcome-center");
        //welcomeCenter.setPadding(new Insets(150, 20, 20, 20));
        welcomeCenter.setSpacing(20);

        //welcomeCenter.setPrefWidth(2000);

        Pane leftFlow = new VBox();
        leftFlow.getStyleClass().add("welcome-sides");
        //leftFlow.setPadding(welcomeInsets);
        Pane rightFlow = new VBox();
        rightFlow.getStyleClass().add("welcome-sides");
        //rightFlow.setPadding(welcomeInsets);

        Label welcomeTitleLabel = new Label("Welcome to London Property Marketplace");
        welcomeTitleLabel.setId("welcome-title-label");
        welcomeTitleLabel.setWrapText(true);

        Label welcomeInfoLabel = new Label("This application shows information about all available airbnb properties in every london borough based on the given price range.");
        welcomeInfoLabel.setWrapText(true);

        Label welcomeHowToLabel = new Label("How to use:");
        welcomeHowToLabel.setWrapText(true);

        Label welcomeInstructionsLabel = new Label("Select a preferred price range. Click on a borough on the borough map to see its listings. Click on a property to view its details. Go to the statistics page to view the statistics of listings in the selected price range");
        welcomeInstructionsLabel.setWrapText(true);

        Label welcomeArrowsLabel = new Label("Using the arrow keys in the top left corner you can traverse through the following pages in the app: \n\n1. Welcome page \n2. Map of boroughs with their listings \n3. Statistics on the current price range");
        welcomeArrowsLabel.setWrapText(true);

        Label welcomeFilterLabel = new Label("To select a price range use the boxes in the top right corner");
        welcomeFilterLabel.setWrapText(true);

        Label welcomePriceTitleLabel = new Label("Selected price range:");

        welcomePriceLabel = new Label("No price range selected");

        //welcomeCenter.getChildren().addAll(welcomeTitleLabel, welcomeInfoLabel, welcomeHowToLabel, welcomeInstructionsLabel, welcomePriceTitleLabel, welcomePriceLabel);

        //leftFlow.getChildren().addAll(welcomeArrowsLabel);
        //rightFlow.getChildren().addAll(welcomeFilterLabel);

        //BorderPane.setAlignment(welcomeCenter, Pos.CENTER);
        //welcomePanel.setCenter(welcomeCenter);

        Label hugeLabel = new Label(welcomeInfoLabel.getText() + "\n\n" + welcomeHowToLabel.getText() + "\n\n" + welcomeInstructionsLabel.getText() + "\n\n" + welcomePriceTitleLabel.getText() + "\n" + welcomePriceLabel.getText());
        hugeLabel.setWrapText(true);

        BorderPane.setAlignment(welcomeTitleLabel, Pos.CENTER);
        welcomePanel.setTop(welcomeTitleLabel);

        BorderPane.setAlignment(hugeLabel, Pos.CENTER);
        welcomePanel.setCenter(hugeLabel);

        BorderPane.setAlignment(welcomeArrowsLabel, Pos.CENTER);
        welcomePanel.setLeft(welcomeArrowsLabel);

        BorderPane.setAlignment(welcomeFilterLabel, Pos.CENTER);
        welcomePanel.setRight(welcomeFilterLabel);

    }

    private void showPriceRange(boolean invalid)
    {
        if(invalid && fromPrice != null && toPrice != null){
            welcomePriceLabel.setText("Invalid");
        }
        else if(fromPrice != null && toPrice != null){
            welcomePriceLabel.setText("\u00A3" + fromPrice + " - \u00A3" + toPrice);
        }
    }



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

    /**
     * Collect the properties that correspond to the price range selected by the user, into an ArrayList.
     * Only called if the selected price range is valid.
     */
    private void computeProperties() {
        properties = dataLoader.load();

        // We only keep the properties that fit in the price range
        properties.removeIf(p -> (p.getPrice() < fromPrice || p.getPrice() > toPrice));

        computeStatistics(); // Compute the statistics according to the current list of properties

        updateStatistics(); // Update the value statistic shown in each "Statistic Box"

        //Updates the details regarding the map panel
        mapInfo.setPropertyData(properties);

        setColour(mapButtons);
    }

    /**
     * Compute the statistics according to the current list of properties (depending on selected price range).
     */
    private void computeStatistics() {
        // Compute number of reviews
        if (properties.isEmpty()) {
            statAvgReviews.setValue("-");
        } else {
            double count = 0;
            for (AirbnbListing property : properties) {
                count += property.getNumberOfReviews();
            }
            statAvgReviews.setValue(String.valueOf(count/properties.size()));
        }

        // Compute number of available properties
        statNbOfProperties.setValue(String.valueOf(properties.size()));

        // Compute number of entire homes and apartments
        statNbOfEntireHomeApartments.setValue(
            String.valueOf(properties.stream().filter(p -> (p.getRoom_type().equals("Entire home/apt"))).count())
        );

        // Compute most expensive borough
        statMostExpensiveBorough.setValue(mostExpensiveBorough());

        // ADD 4 ADDITIONAL STATS

    }

    /**
     * Create the JavaFX interface for the "Statistics Panel" (show interactive statistic boxes).
     */
    private void initialiseStatisticsPanel() {
        // Create the panel as a 2x2 grid
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

        // Create the 4 custom "Statistic Boxes"
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

    /**
     * Calculates the most expensive borough in London (according to current price range) by comparing
     * the average property price per borough
     * @return The most expensive borough
     */
    private String mostExpensiveBorough() {
        Dictionary<String, Integer> boroughPrices = new Hashtable<>();

        // Calculate the total price of each borough
        for (AirbnbListing property: properties) {
            String neighbourhood = property.getNeighbourhood();
            int price = property.getPrice() * property.getMinimumNights();
            Integer currentBoroughPrice = boroughPrices.get(neighbourhood);
            if (currentBoroughPrice == null) {
                boroughPrices.put(neighbourhood, price);
            } else {
                boroughPrices.put(neighbourhood, currentBoroughPrice + price);
            }
        }

        // Find borough with the largest average property price
        String mostExpensiveBorough = null;
        double largestAveragePropertyPrice = 0;
        Enumeration<String> boroughsEnum = boroughPrices.keys();
        while (boroughsEnum.hasMoreElements()) {
            String borough = boroughsEnum.nextElement();
            double price = 0;
            int count = 0; // Number of properties in borough
            Integer propertyPrice = boroughPrices.get(borough);
            while (propertyPrice != null) {
                price += propertyPrice;
                ++count;
                boroughPrices.remove(borough);
                propertyPrice = boroughPrices.get(borough);
            }
            double averagePropertyPrice = price / count; // Average price per property in borough
            if (averagePropertyPrice > largestAveragePropertyPrice) {
                mostExpensiveBorough = borough;
                largestAveragePropertyPrice = averagePropertyPrice;
            }
        }

        // Return the name of the most expensive borough
        return mostExpensiveBorough;

    }

    /**
     * Get the current statistics as an ArrayList.
     * @return The ArrayList of the current statistics
     */
    public ArrayList<Statistic> getStatistics() {
        return statistics;
    }

    /**
     * Whether a statistic is already shown by another Statistic Box.
     * (2 Statistic Boxes cannot simultaneously show the same statistic)
     * @param statisticBox The requesting Statistic Box
     * @param index The index of the request statistic to show
     * @return true if the statistic is shown by another Statistic Box, else (statistic not shown) returns false
     */
    public boolean statisticUsed(StatisticBox statisticBox, int index) {
        for (StatisticBox box: statisticBoxes) {
            if (box != statisticBox && box.getStatisticIndex() == index) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update the value of the statistic show in each "Statistic Box".
     */
    private void updateStatistics() {
        for (StatisticBox box: statisticBoxes) {
            box.setStatistic();
        }
    }


}
