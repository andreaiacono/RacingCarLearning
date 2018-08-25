package me.andreaiacono.racinglearning.gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
    private long totalRewards;
    private TimeSeries rewardSeries;
    private TimeSeries averageSeries;


    public GraphFrame() {
        super("Rewards Graph");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 250);

        rewardSeries = new TimeSeries("Rewards");
        averageSeries = new TimeSeries("Average Reward");
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(averageSeries);
        dataset.addSeries(rewardSeries);

        chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Rewards", "time", "reward", dataset, true, true, false);
        this.add(new ChartPanel(chart), BorderLayout.CENTER);
        XYLineAndShapeRenderer plot = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        plot.setSeriesStroke(0, new BasicStroke(2f));
        rewardSeries.addOrUpdate(new Millisecond(new Date()), 1000);

        setVisible(true);
    }

    public void addValue(long epochReward) {
        epoch++;
        totalRewards += epochReward;
        Millisecond now = new Millisecond(new Date());
        rewardSeries.addOrUpdate(now, epochReward);
        int avg = (int) (totalRewards / epoch);
        averageSeries.addOrUpdate(now, avg);
        chart.setTitle("Epoch #" + epoch + " - Avg Reward: " + avg);
    }

    public void saveChartAsImage(String filename) throws Exception {

        ChartUtils.saveChartAsPNG(new File(filename), chart, 3500, 1500);

    }
}
