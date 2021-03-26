import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javafx.event.ActionEvent;

import java.awt.*;

public class StatisticBox extends BorderPane {

    Button leftArrow, rightArrow;
    Label statisticName, statisticValue;

    public StatisticBox() {
        super();

        initialiseComponents();

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
    }

    private void nextStatistic(ActionEvent event) {
        // SHOW NEXT STATISTIC
    }

    private void setStatistic(String name, String value) {
        statisticName.setText(name);
        statisticValue.setText(value);
    }

}
