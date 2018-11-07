package me.andreaiacono.racinglearning.gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Date;

public class GraphFrame extends JFrame {

    private final JFreeChart chart;
    private long epoch;
    private double totalRewards;
    private TimeSeries rewardSeries;
    private TimeSeries averageSeries;
    private TimeSeries lengthSeries;

    public GraphFrame() {
        super("Rewards Graph");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 250);

        rewardSeries = new TimeSeries("Epoch cumulative rewards");
        averageSeries = new TimeSeries("Average Rewards");
        lengthSeries = new TimeSeries("Length");
        TimeSeriesCollection rewardDataset = new TimeSeriesCollection();
        rewardDataset.addSeries(averageSeries);
        rewardDataset.addSeries(rewardSeries);

        TimeSeriesCollection lengthDataset = new TimeSeriesCollection();
        lengthDataset.addSeries(lengthSeries);

        chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Rewards", "time", "reward", rewardDataset, true, true, false);
        XYPlot plot = chart.getXYPlot();

        final NumberAxis lengthRangeAxis = new NumberAxis("Epoch Length");
        lengthRangeAxis.setAutoRangeIncludesZero(true);

        XYLineAndShapeRenderer lengthRenderer = new XYLineAndShapeRenderer();
        lengthRenderer.setSeriesLinesVisible(0, false);
        lengthRenderer.setSeriesShapesVisible(0, true);
        lengthRenderer.setSeriesShape(0, ShapeUtils.createDiamond(2f));

        plot.setDataset(1, lengthDataset);
        plot.setRangeAxis(1, lengthRangeAxis);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setRenderer(1, lengthRenderer);
        ChartUtils.applyCurrentTheme(chart);

        this.add(new ChartPanel(chart), BorderLayout.CENTER);

        setVisible(true);
    }

    public void addValue(double epochReward, long epochLength) {
        if (epochLength != 0) {
            epoch++;
            totalRewards += epochReward;
            Millisecond now = new Millisecond(new Date());
            rewardSeries.addOrUpdate(now, epochReward);
            lengthSeries.addOrUpdate(now, epochLength);
            double avg = totalRewards / (double) epoch;
            averageSeries.addOrUpdate(now, avg);
            chart.setTitle("Epoch #" + epoch + " - Avg Reward: " + avg);
        }
    }

    public void saveChartAsImage(String filename) throws Exception {

        ChartUtils.saveChartAsPNG(new File(filename), chart, 5000, 3000);

    }
}
