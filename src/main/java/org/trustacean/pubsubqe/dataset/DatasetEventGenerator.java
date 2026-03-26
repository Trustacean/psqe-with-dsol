package org.trustacean.pubsubqe.dataset;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.trustacean.pubsubqe.core.Message;
import org.trustacean.pubsubqe.core.Publisher;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

public class DatasetEventGenerator {

    private final DevsSimulatorInterface<Double> simulator;
    private final List<Message> dataset;
    private final Publisher publisher;

    private final DistExponential interArrival;

    private int index = 0;

    public DatasetEventGenerator(
            DevsSimulatorInterface<Double> simulator,
            List<Message> dataset,
            Publisher publisher,
            StreamInterface stream) {
        this.simulator = simulator;
        this.dataset = dataset;
        this.publisher = publisher;
        this.interArrival = new DistExponential(stream, 1.0);

        Random rng = new Random(1);
        Collections.shuffle(this.dataset, rng);
    }

    public void start() {
        scheduleNext();
    }

    private void scheduleNext() {
        if (index >= dataset.size()) {
            return;
        }

        double delay = interArrival.draw();

        simulator.scheduleEventRel(
                delay,
                this,
                "generate",
                new Object[]{index == dataset.size() - 1});
    }

    public void generate(boolean isLast) {
        Message msg = dataset.get(index++);
        publisher.publish(msg);
        scheduleNext();

        if (isLast) {
            simulator.endReplication();
        }
    }
}
