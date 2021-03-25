import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class View extends Application {

    BorderPane root;
    Button backButton;
    Button forwardButton;
    ComboBox fromComboBox;
    ComboBox toComboBox;
    Integer fromPrice;
    Integer toPrice;

    ArrayList<Parent> centerPanels;
    Parent welcomePanel = new Label("Welcome");
    Parent mapPanel = new Label("Map");
    Parent statisticsPanel = new Label("Statistics");
    int panelIndex = 0;



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
    }

    private void toComboBoxAction(Event event) {
        Object item = toComboBox.getSelectionModel().getSelectedItem();
        if (item != null) {
            toPrice = Integer.parseInt(item.toString());
        } else {
            toPrice = null;
        }
        enableButtons();
    }

    private void enableButtons()
    {
        boolean invalidPriceRange = checkInvalidPriceRange();
        backButton.setDisable(invalidPriceRange);
        forwardButton.setDisable(invalidPriceRange);
    }

    private boolean checkInvalidPriceRange() {
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


}
