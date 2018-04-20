package me.andreaiacono.racinglearning.gui;

import com.sun.tools.visualvm.charts.ChartFactory;
import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;

import javax.swing.*;
import java.awt.*;

public class GraphFrame extends JFrame {

    private static final long SLEEP_TIME = 1000;
    private SimpleXYChartSupport support;
    private Generator generator;
    private long epoch;

    public GraphFrame() {
        super("Graph");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new BorderLayout());
        createModels();
        add(support.getChart(), BorderLayout.CENTER);
        setVisible(true);
    }

    private void createModels() {
        SimpleXYChartDescriptor descriptor = SimpleXYChartDescriptor.decimal(0, 1000, 1000, 1d, true, 100000);

        descriptor.addLineFillItems("Reward");

        descriptor.setDetailsItems(new String[]{"Epoch #"});
        descriptor.setChartTitle("<html><font size='+1'><b>Rewards</b></font></html>");
        descriptor.setXAxisDescription("<html>Time</html>");
        descriptor.setYAxisDescription("<html>Reward</html>");

        support = ChartFactory.createSimpleXYChart(descriptor);

        generator = new Generator(support);
        generator.start();
    }

    public void addValue(long epochReward) {
        generator.addValue(epoch, -epochReward);
        epoch++;
    }

    private static class Generator extends Thread {

        private SimpleXYChartSupport support;
        private long epoch;

        public void run() {
            while (true) {
                try {
                    support.updateDetails(new String[]{"" + epoch});
                    Thread.sleep(SLEEP_TIME);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        private Generator(SimpleXYChartSupport support) {
            this.support = support;
        }

        public void addValue(long epoch, long epochReward) {
            this.epoch = epoch;
            support.addValues(System.currentTimeMillis(), new long[] {epochReward} );
        }
    }

}
