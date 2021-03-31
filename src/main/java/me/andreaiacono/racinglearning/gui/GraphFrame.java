package me.andreaiacono.racinglearning.gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.time.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class GraphFrame extends JFrame {

    private static final int MOVING_WINDOW = 1000;
    private final JFreeChart chart;
    private final TimeSeriesCollection rewardDataset;
    private int epoch;
    private TimeSeries epochRewardsSeries;
    private TimeSeries averageRewardsSeries;
    private TimeSeries epochLengthSeries;
    private DecimalFormat decimalFormat = new DecimalFormat("000.000");
    private double totalSum;

    public GraphFrame() {
        super("Rewards Graph");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocation(400,200);

        rewardDataset = new TimeSeriesCollection();

        averageRewardsSeries = new TimeSeries("Average Rewards");
        rewardDataset.addSeries(averageRewardsSeries);

        epochRewardsSeries = new TimeSeries("Epoch cumulative rewards");
        rewardDataset.addSeries(epochRewardsSeries);

        chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Rewards", "time", "reward", rewardDataset, true, true, true);
        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer rewardsRenderer = new XYLineAndShapeRenderer();
        rewardsRenderer.setSeriesShapesVisible(0, false);
        rewardsRenderer.setSeriesShapesVisible(1, false);
        rewardsRenderer.setSeriesStroke(0, new BasicStroke(3f));  // average line is thicker
        plot.setRenderer(0, rewardsRenderer);

        final NumberAxis lengthRangeAxis = new NumberAxis("Epoch Length");
        lengthRangeAxis.setAutoRangeIncludesZero(true);

        TimeSeriesCollection epochLengthsDataset = new TimeSeriesCollection();
        epochLengthSeries = new TimeSeries("Length");
        epochLengthsDataset.addSeries(epochLengthSeries);

        XYLineAndShapeRenderer lengthRenderer = new XYLineAndShapeRenderer();
        lengthRenderer.setSeriesLinesVisible(0, false);
        lengthRenderer.setSeriesShapesVisible(0, true);
        lengthRenderer.setSeriesShape(0, ShapeUtils.createDiamond(2f));
        plot.setDataset(1, epochLengthsDataset);
        plot.setRangeAxis(1, lengthRangeAxis);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setRenderer(1, lengthRenderer);
//        ChartUtils.applyCurrentTheme(chart);

        this.add(new ChartPanel(chart), BorderLayout.CENTER);

        setVisible(true);
    }

    public void addValue(double epochReward, long epochLength) {
        if (epochLength != 0) {
            epoch++;
            Millisecond now = new Millisecond(new Date());
            epochRewardsSeries.addOrUpdate(now, epochReward);
            epochLengthSeries.addOrUpdate(now, epochLength);
            List<TimeSeriesDataItem> items = epochRewardsSeries.getItems();

            totalSum += items.get(items.size()-1).getValue().doubleValue();
            if (epoch > MOVING_WINDOW) {
                int indexToDelete = items.size() - MOVING_WINDOW;
                totalSum -= items.get(indexToDelete).getValue().doubleValue();
            }
            double average = totalSum / (epoch > MOVING_WINDOW ? MOVING_WINDOW : items.size());
            averageRewardsSeries.addOrUpdate(now, average);
            chart.setTitle("Epoch #" + epoch + " - Avg Reward: " + decimalFormat.format(average));
        }
    }

    public void saveChartAsImage(String filename) throws Exception {

        ChartUtils.saveChartAsPNG(new File(filename), chart, 5000, 3000);

    }
}
