import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;

/**
 * This class represents the "Statistics Panel", as a GridPane.
 * @author Jessy Briard, Ravshanbek Rozukulov
 */
public class StatisticsPanel extends GridPane {

    // Collection of all 4 "Statistic Boxes"
    private ArrayList<StatisticBox> statisticBoxes;
    // Collection of all Statistic objects
    private ArrayList<Statistic> statistics;

    public StatisticsPanel(ArrayList<Statistic> statistics) {
        super();
        this.statistics = statistics;
        initialiseStatisticsPanel();
    }



    /**
     * Create the JavaFX interface for the "Statistics Panel" (show interactive statistic boxes).
     */
    private void initialiseStatisticsPanel() {
        // Create the panel as a 2x2 grid
        setId("statistics-panel");
        setPadding(new Insets(45,45,45,45));
        setHgap(50);
        setVgap(50);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        getColumnConstraints().add(columnConstraints);
        getColumnConstraints().add(columnConstraints);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(50);
        getRowConstraints().add(rowConstraints);
        getRowConstraints().add(rowConstraints);

        // Create the 4 custom "Statistic Boxes"
        statisticBoxes = new ArrayList<>();
        StatisticBox statisticBox1 = new StatisticBox(this, statistics);
        add(statisticBox1, 0, 0);
        statisticBoxes.add(statisticBox1);
        StatisticBox statisticBox2 = new StatisticBox(this, statistics);
        add(statisticBox2, 0, 1);
        statisticBoxes.add(statisticBox2);
        StatisticBox statisticBox3 = new StatisticBox(this, statistics);
        add(statisticBox3, 1, 0);
        statisticBoxes.add(statisticBox3);
        StatisticBox statisticBox4 = new StatisticBox(this, statistics);
        add(statisticBox4, 1, 1);
        statisticBoxes.add(statisticBox4);

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
     * Update the value of the statistic shown in each "Statistic Box".
     */
    public void updateStatistics() {
        for (StatisticBox box: statisticBoxes) {
            box.setStatistic();
        }
    }



}
