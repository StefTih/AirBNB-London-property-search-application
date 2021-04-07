import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the additional "Search Engine Panel", as a SplitPane.
 * @author Jessy Briard, Tihomir Stefanov, Ravshanbek Rozukulov, Alexandru Bularca
 */
public class SearchEnginePanel extends SplitPane {

    private View view;

    //A border pane to separate the properties from their info graphic.
    private BorderPane resultsPanel;
    //A scrollbar for the search engine panel for each neighbourhood
    private ScrollPane propertyScroll;
    //Text field for user to search properties
    private TextField searchField;
    //DropDown List of boroughs
    private ComboBox<String> boroughsComboBox;
    //Currently chosen borough
    private String selectedBorough = null;
    //Label showing the number of properties found in the search
    private Label resultsSize;
    //Stores an object containing the current properties available to show and extract from the map
    private MapInfo mapInfo;
    //Stores the info message displayed in the borough properties method when none is selected
    private Label noPropertySelected;
    //Stores the info message displayed when no properties are found from the search
    private Label noSearchResults;
    //The method to sort the search results, sorted by Relevancy by default
    private String sortMethod = "Relevancy (search similarity)";


    public SearchEnginePanel(View view, MapInfo mapInfo) {
        super();
        this.view = view;
        this.mapInfo = mapInfo;
        initialiseSearchEnginePanel();
    }



    /**
     * Create the JavaFX interface for the "Search Engine Panel" (search for properties by name).
     */
    private void initialiseSearchEnginePanel()
    {
        //Top pane is a BorderPane
        BorderPane topSearchPane = new BorderPane();

        //Top BorderPane holds a SearchBar as HBox
        HBox searchBar = new HBox();
        searchBar.setId("search-bar");
        searchBar.setAlignment(Pos.CENTER);
        topSearchPane.setCenter(searchBar);

        //Top BorderPane holds a Label showing the number of properties in the search results
        resultsSize = new Label();
        resultsSize.setPadding(new Insets(0, 20, 0, 20));
        BorderPane.setAlignment(resultsSize, Pos.CENTER_LEFT);
        topSearchPane.setRight(resultsSize);

        //Top BorderPane holds a HBox that allows the user to choose a sorting method for the results
        HBox sortingMethodsBar = new HBox();
        sortingMethodsBar.getStyleClass().add("top-nav");
        topSearchPane.setLeft(sortingMethodsBar);
        Label sortByLabel = new Label("Sort by: ");
        ComboBox<String> sortingMethods = new ComboBox<>();
        sortingMethods.getItems().addAll("Relevancy (search similarity)", "Number of Reviews", "Price(Low - High)", "Price(High - Low)", "Host Name(A - Z)");
        sortingMethods.setPromptText("Relevancy (search similarity)");
        sortingMethods.setOnAction(event -> sort(sortingMethods.getSelectionModel().getSelectedItem()));
        sortingMethodsBar.getChildren().addAll(sortByLabel, sortingMethods);


        //Pane to display search results

        noSearchResults = new Label("No search results");
        noSearchResults.setAlignment(Pos.CENTER);
        noSearchResults.getStyleClass().add("not-selected");
        noPropertySelected = new Label("No property selected");
        noPropertySelected.setAlignment(Pos.CENTER);
        noPropertySelected.getStyleClass().add("not-selected");

        propertyScroll = new ScrollPane();
        propertyScroll.getStyleClass().add("results-scroll-pane");
        propertyScroll.setPrefWidth(mapInfo.getPrefWidth() + 40);
        resultsPanel = new BorderPane();
        resultsPanel.setId("results-panel");
        resultsPanel.setLeft(propertyScroll);

        getItems().setAll(topSearchPane, resultsPanel);
        setId("search-panel");
        setOrientation(Orientation.VERTICAL);
        setDividerPosition(0, 0.1);

        //Components to allow searching of properties

        boroughsComboBox = new ComboBox<>();
        boroughsComboBox.setPromptText("ALL BOROUGHS");
        boroughsComboBox.setOnAction(this::boroughChanged);
        fillBoroughsComboBox();
        searchField = new TextField();
        searchField.setPromptText("Property name");
        searchField.setPrefWidth(300);
        searchField.setOnKeyPressed(this::searchKeyPressed);
        Button searchButton = new Button("SEARCH");
        searchButton.setOnAction(event -> search(true));
        searchBar.getChildren().addAll(boroughsComboBox, searchField, searchButton);

        clearOldSearch();
    }

    /**
     * Add all boroughs as elements of the BoroughsComboBox.
     */
    private void fillBoroughsComboBox() {
        boroughsComboBox.getItems().add("ALL BOROUGHS");
        for (String[] row: mapInfo.getLondonBoroughs()) {
            String borough = row[0];
            boroughsComboBox.getItems().add(borough);
        }
    }

    /**
     * This method is triggered when a user choses a borough from the DropDown List.
     * It triggers an update of the search results with the new borough as a parameter.
     * @param event The ActionEvent triggered by the user
     */
    private void boroughChanged(ActionEvent event) {
        String newSelectedBorough = boroughsComboBox.getSelectionModel().getSelectedItem();
        if (! newSelectedBorough.equals(selectedBorough)) {
            selectedBorough = newSelectedBorough;
            searchUpdate(view.getCenterPanel());
        }
    }

    /**
     * Update the search results with the new price range or borough selection.
     * @param centerPane The Center Panel of the view
     */
    public void searchUpdate(Node centerPane) {
        if (centerPane == this && propertyScroll.getContent() != null && !searchField.getCharacters().toString().trim().equals("")) {
            search(false);
        }
    }

    /**
     * Search for properties if the user presses the Enter key while writing in the Search Text Field.
     * @param event The KeyEvent triggered by the user
     */
    private void searchKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            search(true);
        }
    }

    /**
     * Clear the right side of the screen to not display details of any property
     */
    private void clearSelectedProperty()
    {
        if(resultsPanel.getCenter() != noPropertySelected)
        {
            resultsPanel.setCenter(noPropertySelected);
        }
    }

    /**
     * Clear the properties search result list on the left side of the screen
     */
    private void clearSearchResults()
    {
        if(propertyScroll.getContent() != noSearchResults){
            propertyScroll.setContent(noSearchResults);
        }
    }

    /**
     * Checks whether the center pane of the border pane is filled with old info search before
     * making a new search and removes the old search from the screen.
     */
    private void clearOldSearch()
    {
        clearSelectedProperty();
        clearSearchResults();
        resultsSize.setText("");
    }

    /**
     * Sort the search results according to the sorting method selected by the user.
     * @param sortingMethod The select method to sort the search results
     */
    private void sort(String sortingMethod) {
        sortMethod = sortingMethod;
        if (propertyScroll.getContent() != null) {
            search(false);
        }
    }

    /**
     * Search for properties according to the prefix specified in the Text Field.
     * @param storeExpression Whether the expression should be stored in the search-words.txt file
     */
    private void search(boolean storeExpression) {
        clearOldSearch();
        if (! (searchField.getCharacters().toString().trim().equals("") || view.invalidPriceRange())) {
            // Search for properties within the selected price range and corresponding with the search prefix
            String searchWord = searchField.getCharacters().toString().trim().toLowerCase();

            // Collection of all properties in the current selected price range
            ArrayList<AirbnbListing> properties = view.getProperties();

            List<AirbnbListing> searchResults =  properties.stream().filter(p -> p.getName().toLowerCase().contains(searchWord)).collect(Collectors.toList());

            // Sort the search results according the method selected by the user
            switch (sortMethod) {
                case "Relevancy (search similarity)" -> searchResults = sortByRelevancy(properties, searchWord);
                case "Number of Reviews" -> sortByReviews(searchResults);
                case "Price(Low - High)" -> sortByPriceLowToHigh(searchResults);
                case "Price(High - Low)" -> sortByPriceHighToLow(searchResults);
                case "Host Name(A - Z)" -> sortByHostName(searchResults);
            }

            // If a specific borough is selected
            if (selectedBorough != null && (! selectedBorough.equals("ALL BOROUGHS"))) {
                searchResults = searchResults.stream().filter(p -> p.getNeighbourhood().equals(selectedBorough)).collect(Collectors.toList());
            }

            // Show an Alert Dialog if the search finds no corresponding properties
            if (searchResults.isEmpty()) {
                showEmptyResultsAlert();
            } else {
                int size = searchResults.size();
                String suffix = " properties found";
                if (size == 1) {
                    suffix = " property found";
                }
                resultsSize.setText(size + suffix);
            }

            showSearchResults(searchResults);

            // Only store the expression within the list of searched expressions if the search is initiated
            // by the user (not by an update from a price range or borough change)
            if (storeExpression) {
                appendSearchWordToFile(searchWord);
            }

        } else if (searchField.getCharacters().isEmpty()) {
            showEmptyFieldAlert();
        }
    }

    /**
     * Sort the search results by relevancy (first the properties with the same name, then the properties whose name
     * starts with the expression and then finally those whose name contains the expression).
     * @param properties The list of all properties within the specified price range
     * @param searchWord The expression searched by the user
     * @return The list of search results sorted by relevancy
     */
    private List<AirbnbListing> sortByRelevancy(ArrayList<AirbnbListing> properties, String searchWord) {
        // Properties whose name EQUALS the searched expression (order of pertinance)
        List<AirbnbListing> searchResults =  properties.stream().filter(p -> p.getName().toLowerCase().equals(searchWord)).collect(Collectors.toList());
        // Properties whose name STARTSWITH the searched expression
        properties.stream().filter(p -> p.getName().toLowerCase().startsWith(searchWord) && !(p.getName().toLowerCase().equals(searchWord))).forEach(searchResults::add);
        // Properties whose name CONTAINS the searched expression
        properties.stream().filter(p -> p.getName().toLowerCase().contains(searchWord) && !(p.getName().toLowerCase().startsWith(searchWord))).forEach(searchResults::add);

        return searchResults;
    }

    /**
     * Sort the search results by the number of reviews of each property.
     * @param searchResults The non-sorted search results
     */
    private void sortByReviews(List<AirbnbListing> searchResults) {
        searchResults.sort(new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2) {
                return Integer.compare(property2.getNumberOfReviews(), property1.getNumberOfReviews());
            }
        });
    }

    /**
     * Sort the search results by the price of each property, from low to high.
     * @param searchResults The non-sorted search results
     */
    private void sortByPriceLowToHigh(List<AirbnbListing> searchResults) {
        searchResults.sort(new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2) {
                return Integer.compare(property1.getPrice(), property2.getPrice());
            }
        });
    }

    /**
     * Sort the search results by the price of each property, from high to low.
     * @param searchResults The non-sorted search results
     */
    private void sortByPriceHighToLow(List<AirbnbListing> searchResults) {
        searchResults.sort(new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2) {
                return -Integer.compare(property1.getPrice(), property2.getPrice());
            }
        });
    }

    /**
     * Sort the search results by the the host name of each property.
     * @param searchResults The non-sorted search results
     */
    private void sortByHostName(List<AirbnbListing> searchResults) {
        searchResults.sort(new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2) {
                return String.valueOf(property1.getHost_name()).compareTo(property2.getHost_name());
            }
        });
    }

    /**
     * Add the search word to the "search-words.txt" file.
     * @param searchWord The searched expression to append to the file
     */
    private void appendSearchWordToFile(String searchWord) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("search-words.txt", true))) {
            writer.write(searchWord.trim().toLowerCase() + "\n");
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Show the search results.
     * @param searchResults The search results to display to the user
     */
    private void showSearchResults(List<AirbnbListing> searchResults)
    {
        if (searchResults.isEmpty()){
            clearSearchResults();
            return;
        }

        VBox vBox = new VBox();

        vBox.setAlignment(Pos.TOP_CENTER);
        ToggleGroup propertiesToggleGroup = new ToggleGroup();
        ArrayList<ToggleButton> searchedResultsButtons = new ArrayList<>();

        // Create buttons to display the search results
        for (AirbnbListing property: searchResults) {
            ToggleButton propertyInfo = new PropertyButton(property, mapInfo, propertiesToggleGroup);
            propertyInfo.setOnAction(p -> searchPropertyToggled(propertyInfo, property));
            searchedResultsButtons.add(propertyInfo);
        }
        vBox.getChildren().addAll(searchedResultsButtons);
        propertyScroll.setContent(vBox);
    }

    /**
     * Toggle the information about the pressed property
     * @param button the property that button was toggeled
     * @param property the property linked to the button
     */
    private void searchPropertyToggled(ToggleButton button, AirbnbListing property)
    {
        if(button.isSelected()){
            showDetails(property);
        }
        else{
            clearSelectedProperty();
        }
    }

    /**
     * This method finds all the details regarding the property searched and outputs them onto
     * the center of the border pane
     * @param property stores the object of type AirbnbListing which contains all the
     *                 details about this specific property.
     */
    private void showDetails(AirbnbListing property)
    {
        Label propertyDescription = new Label(mapInfo.showPropertyDescription(property.getId()));
        propertyDescription.getStyleClass().add("property-description");
        resultsPanel.setCenter(propertyDescription);

        view.addViewedProperty(property);
    }

    /**
     * Show an Alert Dialog to state that the Search has found no properties has a result.
     */
    private void showEmptyResultsAlert() {
        // Show an Alert Dialog
        Alert emptyResultsAlert = new Alert(Alert.AlertType.ERROR);
        emptyResultsAlert.setTitle("Empty Result");
        emptyResultsAlert.setHeaderText("The Search has found no corresponding property.");
        emptyResultsAlert.setContentText("There is no property corresponding to the input characteristics.");
        emptyResultsAlert.showAndWait();
    }

    /**
     * Show an Alert Dialog to state that the Search Field is empty (not specified by user).
     */
    private void showEmptyFieldAlert() {
        // Show an Alert Dialog
        Alert emptyFieldAlert = new Alert(Alert.AlertType.WARNING);
        emptyFieldAlert.setTitle("Empty Search Field");
        emptyFieldAlert.setHeaderText("The Search Field is empty.");
        emptyFieldAlert.setContentText("Please provide an expression to search the properties.");
        emptyFieldAlert.showAndWait();
    }


}
