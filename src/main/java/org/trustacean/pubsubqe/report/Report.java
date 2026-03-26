package org.trustacean.pubsubqe.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.math.functions.Nan;

import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

public abstract class Report implements EventListener {

    public static final String REPORT_DIR = "./reports/";
    public static final String OUT_SUFFIX = ".csv";
    public static final String INTERVALLED_FORMAT ="%04d" + OUT_SUFFIX; 

    protected PrintWriter out;

    private final String outFileName;
    private String prefix = "";
    private double outputInterval;
    private int precision = 1;
    private int lastOutputSuffix;

    public Report(String namespace) {
        this.outFileName = REPORT_DIR + namespace + OUT_SUFFIX;
        this.outputInterval = -1;
        this.lastOutputSuffix = 0;
        checkDirExistence(namespace);
    }

    protected void init() {		
		if (outputInterval > 0) {
			createSuffixedOutput(outFileName);
		}
		else {
			createOutput(outFileName);
		}
	}

    protected void setPrefix(String txt) {
        this.prefix = txt;
    }

    @Override
    public void notify(Event event) {
        if (event.getType().equals(DevsSimulator.STOP_EVENT)) {
            done();
        }
    }

    private boolean createDirs(File directory) {
        if (directory == null) {
            return true;
        }
        if (directory.exists()) {
            return true;
        } else {
            if (!createDirs(directory.getParentFile())) {
                return false;
            }
            return directory.mkdir();
        }
    }

    private void checkDirExistence(String outFileName) {
        File outFile = new File(outFileName);
        File outDir = outFile.getParentFile();

        if (outDir != null && !outDir.exists()) {
            if (!createDirs(outDir)) {
                throw new Error("Couldn't create report directory '"
                        + outDir.getAbsolutePath() + "'");
            }
        }
    }

    private void createOutput(String outFileName) {
        try {
            this.out = new PrintWriter(new FileWriter(outFileName));
        } catch (IOException e) {
            throw new Error("Couldn't open file '" + outFileName
                    + "' for report output\n" + e.getMessage(), e);
        }
    }

    private void createSuffixedOutput(String outFileName) {
		String suffix = String.format(INTERVALLED_FORMAT,
				this.lastOutputSuffix);
		createOutput(outFileName+suffix);
		this.lastOutputSuffix++;
	}

    protected void write(String txt) {
		if (out == null) {
			init();
		}
        out.println(prefix + txt);
    }

    public void done() {
        if (out != null) {
            out.close();
        }
    }

    protected String format(double value) {
        return String.format("%." + precision + "f", value);
    }

    protected <K> void mapIncrement(Map<K, Integer> map, K key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    public String getAverage(List<Double> values) {
        double sum = 0;
        if (values.isEmpty()) {
            return Nan.NAN.toString();
        }

        for (double dValue : values) {
            sum += dValue;
        }

        return format(sum / values.size());
    }

    public String getIntAverage(List<Integer> values) {
        List<Double> dValues = new ArrayList<>(values.size());
        for (int i : values) {
            dValues.add((double) i);
        }
        return getAverage(dValues);
    }

}
