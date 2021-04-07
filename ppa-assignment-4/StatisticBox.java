import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javafx.event.ActionEvent;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class represents a single StatisticBox component.
 * It shows a single statistic and allows to navigate to other available statistics.
 * @author Jessy Briard
 */

public class StatisticBox extends BorderPane {

    private StatisticsPanel statisticsPanel;
    private Button leftArrow, rightArrow;
    private Label statisticName, statisticValue;
    private int statisticIndex = -1;
    // Collection of all Statistic objects
    private ArrayList<Statistic> statistics;

    public StatisticBox(StatisticsPanel statisticsPanel, ArrayList<Statistic> statistics) {
        super();

        this.statisticsPanel = statisticsPanel;
        this.statistics = statistics;

        initialiseComponents();

        nextStatistic(null);

    }

    /**
     * Create the Statistic Box as a component.
     */
    private void initialiseComponents() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

        this.getStyleClass().add("statistic-cell");

        leftArrow = new Button("<");
        leftArrow.setPrefSize(0.04*screenWidth, 0.4*screenHeight);
        leftArrow.setAlignment(Pos.CENTER);
        leftArrow.setOnAction(this:: previousStatistic);
        setLeft(leftArrow);

        rightArrow = new Button(">");
        rightArrow.setPrefSize(0.04*screenWidth, 0.4*screenHeight);
        rightArrow.setAlignment(Pos.CENTER);
        rightArrow.setOnAction(this:: nextStatistic);
        setRight(rightArrow);


        BorderPane statistic = new BorderPane();
        setCenter(statistic);

        statisticName = new Label("Statistic Name");
        statisticName.setWrapText(true);
        statisticName.setTextAlignment(TextAlignment.CENTER);
        BorderPane.setAlignment(statisticName, Pos.CENTER);
        statistic.setTop(statisticName);

        statisticValue = new Label("Statistic Value");
        statisticValue.setWrapText(true);
        statisticValue.setTextAlignment(TextAlignment.CENTER);
        statistic.setCenter(statisticValue);

    }

    /**
     * Action to execute when the user clicks on the "<" button.
     * The Statistic Box shows the (circularly) previous available statistic.
     * @param event The ActionEvent triggered by the user
     */
    private void previousStatistic(ActionEvent event) {
        statisticIndex = (statisticIndex-1+statistics.size())%(statistics.size());
        while (statisticsPanel.statisticUsed(this, statisticIndex)) {
            statisticIndex = (statisticIndex-1+statistics.size())%(statistics.size());
        }
        setStatistic();
    }

    /**
     * Action to execute when the user clicks on the ">" button.
     * The Statistic Box shows the (circularly) next available statistic.
     * @param event The ActionEvent triggered by the user
     */
    private void nextStatistic(ActionEvent event) {
        statisticIndex = (statisticIndex+1)%(statistics.size());
        while (statisticsPanel.statisticUsed(this, statisticIndex)) {
            statisticIndex = (statisticIndex+1)%(statistics.size());
        }
        setStatistic();
    }

    /**
     * Show the selected statistic on the GUI.
     */
    public void setStatistic() {
        Statistic stat = statistics.get(statisticIndex);
        statisticName.setText(stat.getName());
        statisticValue.setText(stat.getValue());
    }

    /**
     * Get the index of the currently shown statistic.
     * @return The index of the currently shown statistic
     */
    public int getStatisticIndex() {
        return statisticIndex;
    }

}
