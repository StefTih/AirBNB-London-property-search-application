import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.awt.*;
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
    private Parent mapPanel = new Label("Map");
    private GridPane statisticsPanel;
    private int panelIndex = 0;

    private ArrayList<Statistic> statistics;
    private ArrayList<StatisticBox> statisticBoxes;


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

        centerPanels = new ArrayList<Parent>(Arrays.asList(welcomePanel, mapPanel, statisticsPanel));
        root.setCenter(centerPanels.get(0));

    }

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

    private void computeProperties() {
        if (! invalidPriceRange()) {
            properties = dataLoader.load();

            properties.removeIf(p -> (p.getPrice() < fromPrice || p.getPrice() > toPrice));

            computeStatistics();

            updateStatistics();
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
