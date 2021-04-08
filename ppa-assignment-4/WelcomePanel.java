import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * This class represents the "Welcome Panel", as a BorderPane.
 * @author Ravshanbek Rozukulov, Jessy Briard
 */
public class WelcomePanel extends BorderPane {

    // The welcome page price Label
    private Label welcomePriceLabel;

    public WelcomePanel() {
        super();

        initialiseWelcomePanel();
    }


    /**
     * Create JavaFX interface for the welcome page of the application
     */
    private void initialiseWelcomePanel()
    {
        setId("welcome-panel");

        VBox welcomeTopPane = new VBox();
        welcomeTopPane.setAlignment(Pos.CENTER);
        welcomeTopPane.setSpacing(10);

        Label welcomeTitleLabel = new Label("Welcome to London Property Marketplace");
        welcomeTitleLabel.setId("welcome-title-label");
        welcomeTitleLabel.setWrapText(true);
        welcomeTitleLabel.setAlignment(Pos.CENTER);

        Label welcomeInfoLabel = new Label("This application shows information about all available " +
                "airbnb properties in every london borough based on the given price range.");
        welcomeInfoLabel.getStyleClass().add("welcome-label");
        welcomeInfoLabel.setWrapText(true);
        welcomeInfoLabel.setAlignment(Pos.CENTER);

        welcomeTopPane.getChildren().addAll(welcomeTitleLabel, welcomeInfoLabel);

        Label welcomeHowTo = new Label("How to use:\n\n1. Select a preferred price range." +
                "\n2. Click on a borough on the borough map to see its listings." +
                "\n3. Click on a property to view its details. " +
                "\n4. Go to the statistics page to view the statistics of listings in the selected price range.");
        welcomeHowTo.setWrapText(true);
        welcomeHowTo.getStyleClass().add("welcome-label");
        welcomeHowTo.setId("welcome-paragraph");
        welcomeHowTo.setAlignment(Pos.CENTER);

        VBox welcomeBottomPane = new VBox();
        welcomeBottomPane.setId("welcome-bottom");
        welcomeBottomPane.setAlignment(Pos.CENTER);


        // These components show the price range currently selected by the user

        Label welcomePriceInfoLabel = new Label("Selected price range: ");
        welcomePriceInfoLabel.setWrapText(true);
        welcomePriceInfoLabel.getStyleClass().addAll("welcome-price");
        welcomePriceInfoLabel.setAlignment(Pos.TOP_CENTER);

        welcomePriceLabel = new Label("No price range selected");
        welcomePriceLabel.setWrapText(true);
        welcomePriceLabel.getStyleClass().addAll("welcome-price");
        welcomePriceLabel.setAlignment(Pos.TOP_CENTER);

        welcomeBottomPane.getChildren().addAll(welcomePriceInfoLabel, welcomePriceLabel);


        BorderPane.setAlignment(welcomeTopPane, Pos.CENTER);
        setTop(welcomeTopPane);

        BorderPane.setAlignment(welcomeHowTo, Pos.CENTER);
        setBottom(welcomeHowTo);

        BorderPane.setAlignment(welcomeBottomPane, Pos.CENTER);
        setCenter(welcomeBottomPane);
    }

    /**
     * Update the label on the welcome page to display the price range selected by the user
     * @param invalid true if the range selected by the user is invalid
     * @param fromPrice The lower bound of the selected price range
     * @param toPrice The higher bound of the selected price range
     */
    public void showPriceRange(boolean invalid, Integer fromPrice, Integer toPrice)
    {
        if(invalid && fromPrice != null && toPrice != null){
            welcomePriceLabel.setText("Invalid");
        }
        else if(fromPrice != null && toPrice != null){
            welcomePriceLabel.setText("\u00A3" + fromPrice + " - \u00A3" + toPrice);
        }
    }



}
