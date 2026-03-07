package org.trustacean.pubsubqe;

import java.util.List;

import org.trustacean.pubsubqe.core.Broker;
import org.trustacean.pubsubqe.core.Message;
import org.trustacean.pubsubqe.core.Publisher;
import org.trustacean.pubsubqe.core.Subscriber;
import org.trustacean.pubsubqe.util.DatasetEventGenerator;
import org.trustacean.pubsubqe.util.DatasetLoader;

import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

public class Model extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>> {

    public Model(DevsSimulatorInterface<Double> simulator) {
        super(simulator);
    }

    @Override
    public void constructModel() {
        Publisher publisher = createPublisher();
        Broker broker = createBroker(publisher);

        registerSubscribers(broker);

        List<Message> dataset = loadDataset();

        DatasetEventGenerator generator
                = createGenerator(dataset, publisher);

        generator.start();
    }

    private Publisher createPublisher() {
        return new Publisher();
    }

    private Broker createBroker(Publisher publisher) {
        Broker broker = new Broker();
        publisher.addListener(broker, Message.MESSAGE_EVENT);
        return broker;
    }

    private void registerSubscribers(Broker broker) {
        broker.addSubscriber(new Subscriber("AI"));
        broker.addSubscriber(new Subscriber("robot"));
    }

    private List<Message> loadDataset() {
        try {
            return DatasetLoader.load();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load dataset", e);
        }
    }

    private DatasetEventGenerator createGenerator(List<Message> dataset, Publisher publisher) {
        StreamInterface stream
                = this.getStreamInformation().getStream("default");

        return new DatasetEventGenerator(
                getSimulator(),
                dataset,
                publisher,
                stream
        );
    }
}
