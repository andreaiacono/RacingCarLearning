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

    private static final int MOVING_WINDOW = 10;
    private final JFreeChart chart;
    private final TimeSeriesCollection rewardDataset;
    private long epoch;
    private TimeSeries rewardSeries;
    private TimeSeries averageSeries;
    private TimeSeries lengthSeries;
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");
    private double avg;

    public GraphFrame() {
        super("Rewards Graph");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 250);

        rewardSeries = new TimeSeries("Epoch cumulative rewards");
        averageSeries = new TimeSeries("Average Rewards");
        rewardDataset = new TimeSeriesCollection();
        rewardDataset.addSeries(rewardSeries);
        rewardDataset.addSeries(averageSeries);

        lengthSeries = new TimeSeries("Length");
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
            Millisecond now = new Millisecond(new Date());
            rewardSeries.addOrUpdate(now, epochReward);
            lengthSeries.addOrUpdate(now, epochLength);
            chart.setTitle("Epoch #" + epoch + " - Avg Reward: " + decimalFormat.format(avg));
            if (epoch % MOVING_WINDOW == 0) {
                avg = 0;
                List<TimeSeriesDataItem> items = rewardSeries.getItems();
                for (TimeSeriesDataItem item: items.subList(items.size() - MOVING_WINDOW, items.size()) ) {
                    avg += item.getValue().doubleValue();
                }
                averageSeries.addOrUpdate(now, avg / (double)MOVING_WINDOW);
            }
        }
    }

    public void saveChartAsImage(String filename) throws Exception {

        ChartUtils.saveChartAsPNG(new File(filename), chart, 5000, 3000);

    }
}
