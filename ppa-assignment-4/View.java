import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * This class represents the view of our London Property Marketplace Application.
 * @author Jessy Briard, Tihomir Stefanov, Alexandru Bularca, Ravshanbek Rozukulov
 */

public class View extends Application {

    private AirbnbDataLoader dataLoader = new AirbnbDataLoader();
    private ArrayList<AirbnbListing> properties = dataLoader.load();

    private BorderPane root;
    private BorderPane topBar;
    private Button backButton;
    private Button forwardButton;
    private ComboBox fromComboBox;
    private ComboBox toComboBox;
    private Integer fromPrice;
    private Integer toPrice;

    private ArrayList<Parent> centerPanels;
    private ArrayList<NamedPanel> namedPanels;
    private WelcomePanel welcomePanel;
    private MapPanel mapPanel;
    private SearchEnginePanel searchEnginePanel;
    private StatisticsPanel statisticsPanel;
    // The index of the current shown panel
    private int panelIndex = 0;

    //Stores an object containing the current properties available to show and extract from the map
    private MapInfo mapInfo;


    // The statistics
    private Statistic statAvgReviews = new Statistic("Average number of reviews per property");
    private Statistic statNbOfProperties = new Statistic("Total number of available properties");
    private Statistic statNbOfEntireHomeApartments = new Statistic("Number of entire homes and apartments");
    private Statistic statMostExpensiveBorough = new Statistic("Most expensive borough");
    private Statistic statAvgPriceViewedProperties = new Statistic("Average price of all viewed properties");
    private Statistic statMostSearchedExpression = new Statistic("Most searched expression\n(Property Search Panel)");
    private Statistic statAvgNbOfPropertiesPerBorough = new Statistic("Average number of properties per borough");
    private Statistic statMinimumExpense = new Statistic("Minimum booking expense");
    // Collection of all Statistic objects
    private ArrayList<Statistic> statistics = new ArrayList<>(Arrays.asList(
            statAvgReviews, statNbOfProperties, statNbOfEntireHomeApartments, statMostExpensiveBorough, statAvgPriceViewedProperties, statMostSearchedExpression, statAvgNbOfPropertiesPerBorough, statMinimumExpense
    ));

    // Collection of viewed properties
    private HashMap<String, AirbnbListing> viewedProperties = new HashMap<>();


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
     */
    @Override
    public void start(Stage primaryStage) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        root = new BorderPane();
        primaryStage.setTitle("London Property Marketplace");
        Scene primaryScene = new Scene(root, 0.8*screenSize.getWidth(), 0.8*screenSize.getHeight());
        primaryScene.getStylesheets().addAll("main.css");
        // The size of the window is adapted to the size of the screen
        primaryStage.setScene(primaryScene);
        primaryStage.show();

        //Initialising the "Application Window" in te GUI
        initialiseApplicationWindow();

        //Initialising the "Welcome Panel" in the GUI
        welcomePanel = new WelcomePanel();

        //Initialising the "Map Panel" in the GUI
        mapInfo = new MapInfo();
        mapPanel = new MapPanel(this, mapInfo);

        //Initialising the "Statistics Panel" in the GUI
        statisticsPanel = new StatisticsPanel(statistics);
        computeStatistics();
        computeMostSearchedExpression();

        //Initialising the "Search Engine Panel" in the GUI
        searchEnginePanel = new SearchEnginePanel(this, mapInfo);

        centerPanels = new ArrayList<Parent>(Arrays.asList(welcomePanel, mapPanel, statisticsPanel, searchEnginePanel));
        namedPanels = new ArrayList<>(
                Arrays.asList(new NamedPanel(welcomePanel, new Label("Welcome")), new NamedPanel(mapPanel, new Label("Borough Map")),
                        new NamedPanel(statisticsPanel, new Label("Statistics")),  new NamedPanel(searchEnginePanel, new Label("Search"))));
        root.setCenter(centerPanels.get(panelIndex)); // Show the first panel ("Welcome Panel") in the Application
        populatePanelNames((Pane) topBar.getCenter());
        namedPanels.get(panelIndex).getLabel().setTextFill(Color.color(0.286, 0.592, 0.922));
    }




    // Application window methods

    /**
     * Create the JavaFX interface for the "Application Window" (to navigate between panels and select a price range).
     */
    private void initialiseApplicationWindow() {
        topBar = new BorderPane();
        topBar.setId("main-top");
        root.setTop(topBar);

        // Common padding parameter for HBoxes
        Insets hBoxPadding = new Insets(10, 10, 10, 10);

        // Create the "<" and ">" buttons to navigate between panels
        HBox navigationButtons = new HBox();
        topBar.setLeft(navigationButtons);
        navigationButtons.setSpacing(10);
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
        priceRangeComponents.setSpacing(5);
        priceRangeComponents.setPadding(hBoxPadding);
        Label fromLabel = new Label("From:   \u00A3");
        Label toLabel = new Label("To:   \u00A3");
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

        // Label showing the name of the current center panel
        Pane activePanels = new FlowPane();
        activePanels.setId("nav-bar");
        activePanels.setPadding(new Insets(0, 20, 0, 20));
        BorderPane.setAlignment(activePanels, Pos.BOTTOM_LEFT);
        topBar.setCenter(activePanels);
    }

    /**
     * Add the labels of all panels of the application to the top navigation bar of the window
     */
    private void populatePanelNames(Pane pane)
    {
        int count = 0;
        for(NamedPanel panel : namedPanels){
            if(count++ > 0){
                pane.getChildren().add(new Label("|"));
            }
            pane.getChildren().add(panel.getLabel());
        }
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
        changedPriceRangeAction(); // Check price range and execute relevant actions
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
        changedPriceRangeAction(); // Check price range and execute relevant actions
    }

    /**
     * Check the selected price range and execute relevant actions
     * (enable buttons, compute properties and show the price range).
     */
    private void changedPriceRangeAction() {
        boolean invalidRange = invalidPriceRange();
        enableButtons(invalidRange); // Enable or Disable the navigation between panels depending on the validity of the selected price range
        if (! invalidRange) {
            computeProperties(); // Collect the proprieties that correspond to the selected price range
            searchEnginePanel.searchUpdate(root.getCenter());
        }
        welcomePanel.showPriceRange(invalidRange, fromPrice, toPrice); // Show the price range in the Welcome Panel
    }

    /**
     * Get the Panel situated at the Center of the BorderPane root.
     * @return The Center Panel
     */
    public Node getCenterPanel() {
        return root.getCenter();
    }

    /**
     * Enable or Disable the "<" and ">" buttons to navigate between panels, depending on the validity of the price range selected by the user.
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
    public boolean invalidPriceRange() {
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
        int previousIndex = panelIndex;
        panelIndex = (panelIndex-1+centerPanels.size())%(centerPanels.size()); // Index of previous panel
        setCenterPanel(panelIndex);
        updatePanelsLabel(previousIndex, panelIndex);
    }

    /**
     * Action to execute when the user clicks on the ">" button.
     * The Application shows the next panel.
     * @param event The ActionEvent triggered by the user
     */
    private void forwardButtonAction(ActionEvent event) {
        int previousIndex = panelIndex;
        panelIndex = (panelIndex+1)%(centerPanels.size()); // Index of next panel
        setCenterPanel(panelIndex);
        updatePanelsLabel(previousIndex, panelIndex);
    }

    /**
     * Change the center panel to the panel corresponding to the index passed as parameter.
     * @param panelIndex The index of the panel to set as center panel
     */
    private void setCenterPanel(int panelIndex) {
        Node panel = centerPanels.get(panelIndex);
        root.setCenter(panel);
        //panelName.setText(panelNames.get(panel));
        if (panel == statisticsPanel) {
            computeStatistics();
            computeMostSearchedExpression();
        }
    }

    /**
     * Update the panels labels to show the currently active panel that the user is on
     * @param prevIndex the panel index of the application the user switched away from
     * @param currentIndex the panel index of the application the user switched to
     */
    private void updatePanelsLabel(int prevIndex, int currentIndex)
    {
        namedPanels.get(prevIndex).getLabel().setTextFill(new Color(0, 0, 0, 1));
        namedPanels.get(currentIndex).getLabel().setTextFill(new Color(0.286, 0.592, 0.922, 1));
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


    // General methods

    /**
     * Get the list of properties in the current selected price range.
     * @return The list of all properties in the current selected price range.
     */
    public ArrayList<AirbnbListing> getProperties() {
        return properties;
    }

    /**
     * Add a seen property to the list of properties viewed by the user (if not already viewed).
     * @param property The property to add to the list of viewed properties
     */
    public void addViewedProperty(AirbnbListing property) {
        viewedProperties.put(property.getId(), property);
        computeAvgPriceViewedProperties();
    }


    /**
     * Collect the properties that correspond to the price range selected by the user, into an ArrayList.
     * Only called if the selected price range is valid.
     */
    private void computeProperties() {
        properties = dataLoader.load();

        // We only keep the properties that fit in the price range
        properties.removeIf(p -> (p.getPrice() < fromPrice || p.getPrice() > toPrice));

        computeStatistics(); // Compute the statistics according to the current list of properties

        //Updates the details regarding the map panel
        mapInfo.setPropertyData(properties);

        mapPanel.setColour();
    }


    /**
     * Compute the statistics according to the current list of properties (depending on selected price range).
     */
    private void computeStatistics() {
        // Compute number of reviews
        if (properties.isEmpty()) {
            statAvgReviews.setValue(null);
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

        // Compute average number of properties per borough
        statAvgNbOfPropertiesPerBorough.setValue(String.valueOf(properties.size()/mapPanel.getLondonBoroughs().length));

        // Compute minimum booking expense (price to pay to book the cheapest property
        AirbnbListing cheapestProperty = properties.get(0);
        int cheapestPrice = cheapestProperty.getPrice() * cheapestProperty.getMinimumNights();
        for (AirbnbListing property: properties) {
            int price = property.getPrice() * property.getMinimumNights();
            if (price < cheapestPrice) {
                cheapestProperty = property;
                cheapestPrice = price;
            }
        }
        statMinimumExpense.setValue("£" + cheapestPrice + " (" + cheapestProperty.getName() + ", " + cheapestProperty.getNeighbourhood() + ")");

        // Update the statistic value shown in each "Statistic Box"
        statisticsPanel.updateStatistics();

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
     * Calculates the average price of all properties viewed by the user.
     */
    private void computeAvgPriceViewedProperties() {
        if (viewedProperties.isEmpty()) {
            statAvgPriceViewedProperties.setValue(null);
        } else {
            statAvgPriceViewedProperties.setValue(
                    "\u00A3" + String.valueOf(viewedProperties.values().stream().map(property -> property.getPrice()).reduce(0, (count, price) -> count + price) / viewedProperties.size())
            );
        }

        // Update the statistic value shown in each "Statistic Box"
        statisticsPanel.updateStatistics();
    }


    /**
     * Find the most searched expression by reading from the "search-words.txt" file.
     * Then update
     */
    private void computeMostSearchedExpression() {
        List<String> searchedExpressions = readSearchedExpressionsFromFile();

        // Count the number of occurrences of each expression
        HashMap<String, Integer> expressionOccurrences = new HashMap<>();
        for (String expression: searchedExpressions) {
            Integer occurrences = expressionOccurrences.get(expression);
            if (occurrences != null) {
                expressionOccurrences.put(expression, occurrences+1);
            } else {
                expressionOccurrences.put(expression, 1);
            }
        }

        // Get the most searched expression
        String mostSearched = null;
        Integer maxOccurrences = 0;
        for (String expression: expressionOccurrences.keySet()) {
            Integer occurrences = expressionOccurrences.get(expression);
            if (occurrences > maxOccurrences) {
                mostSearched = expression;
                maxOccurrences = occurrences;
            }
        }

        // Update the value of the statistic
        statMostSearchedExpression.setValue(mostSearched);

        // Update the statistic value shown in each "Statistic Box"
        statisticsPanel.updateStatistics();
    }

    /**
     * Read the "search-words.txt" file and construct the List of the previous searched expressions.
     * @return The list of previous searched expressions
     */
    private List<String> readSearchedExpressionsFromFile() {
        List<String> searchedExpressions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("search-words.txt"))) {
            String line = reader.readLine();
            while (line != null && ! line.equals("")) {
                searchedExpressions.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return searchedExpressions;
    }



}