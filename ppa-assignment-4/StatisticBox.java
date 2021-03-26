import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javafx.event.ActionEvent;

import java.awt.*;
import java.util.ArrayList;

public class StatisticBox extends BorderPane {

    private View view;
    private Button leftArrow, rightArrow;
    private Label statisticName, statisticValue;
    private int statisticIndex = -1;
    private ArrayList<Statistic> statistics;

    public StatisticBox(View view) {
        super();

        this.view = view;

        initialiseComponents();

        nextStatistic(null);

    }

    private void initialiseComponents() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

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
        statisticName.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(statisticName, Pos.CENTER);
        statistic.setTop(statisticName);

        statisticValue = new Label("Statistic Value");
        statistic.setCenter(statisticValue);

    }


    private void previousStatistic(ActionEvent event) {
        // SHOW PREVIOUS STATISTIC
        statistics = view.getStatistics();
        statisticIndex = (statisticIndex-1+statistics.size())%(statistics.size());
        while (view.statisticUsed(this, statisticIndex)) {
            statisticIndex = (statisticIndex-1+statistics.size())%(statistics.size());
        }
        setStatistic();
    }

    private void nextStatistic(ActionEvent event) {
        // SHOW NEXT STATISTIC
        statistics = view.getStatistics();
        statisticIndex = (statisticIndex+1)%(statistics.size());
        while (view.statisticUsed(this, statisticIndex)) {
            statisticIndex = (statisticIndex+1)%(statistics.size());
        }
        setStatistic();
    }

    private void setStatistic() {
        Statistic stat = statistics.get(statisticIndex);
        statisticName.setText(stat.getName());
        statisticValue.setText(stat.getValue());
    }

    public int getStatisticIndex() {
        return statisticIndex;
    }

    public void update() {
        statistics = view.getStatistics();
        setStatistic();
    }

}
