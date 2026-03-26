package org.trustacean.pubsubqe;

import java.util.List;

import org.trustacean.pubsubqe.core.*;
import org.trustacean.pubsubqe.dataset.*;
import org.trustacean.pubsubqe.dataset.DatasetLoader;
import org.trustacean.pubsubqe.stats.MatchResult;
import org.trustacean.pubsubqe.stats.StatisticsCollector;

import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
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

        StatisticsCollector stats = new StatisticsCollector();
        publisher.addListener(stats, Message.MESSAGE_PUBLISHED_EVENT);
        broker.addListener(stats, MatchResult.MATCH_RESULT_EVENT);
        broker.addListener(stats, Message.MESSAGE_DELIVERED_EVENT);
        simulator.addListener(stats, DevsSimulator.STOP_EVENT);

        generator.start();
    }

    private Publisher createPublisher() {
        return new Publisher();
    }

    private Broker createBroker(Publisher publisher) {
        Broker broker = new Broker();
        publisher.addListener(broker, Message.MESSAGE_PUBLISHED_EVENT);
        return broker;
    }

    private void registerSubscribers(Broker broker) {
        broker.addSubscriber(new Subscriber("chelyabinsk", "meteor", "damage"));
        broker.addSubscriber(new Subscriber("olympics", "drops", "wrestling"));
        broker.addSubscriber(new Subscriber("barbara", "walters", "chicken", "pox"));
        broker.addSubscriber(new Subscriber("muscle", "pain", "from", "statins"));
        broker.addSubscriber(new Subscriber("costa", "concordia", "shipwreck"));
        broker.addSubscriber(new Subscriber("ed", "koch", "death"));
        broker.addSubscriber(new Subscriber("dark", "pool", "trading"));
        broker.addSubscriber(new Subscriber("national", "zoo", "panda", "insemination"));
        broker.addSubscriber(new Subscriber("bulgarian", "protesters", "self", "immolate"));
        broker.addSubscriber(new Subscriber("boko", "haram", "amnesty", "opposition"));
        broker.addSubscriber(new Subscriber("chinua", "achebe", "death"));
        broker.addSubscriber(new Subscriber("sinkhole", "rescues"));
        broker.addSubscriber(new Subscriber("bombing", "police", "headquarters", "kirkuk"));
        broker.addSubscriber(new Subscriber("russian", "meteorite", "conspiracy"));
        broker.addSubscriber(new Subscriber("ron", "weasley", "birthday"));
        broker.addSubscriber(new Subscriber("book", "club", "members"));
        broker.addSubscriber(new Subscriber("hiv", "baby", "cured"));
        broker.addSubscriber(new Subscriber("hostess", "bought", "by", "apollo"));
        broker.addSubscriber(new Subscriber("uk", "passes", "marriage", "bill"));
        broker.addSubscriber(new Subscriber("arrest", "of", "craig", "wilson", "for", "drive-by", "shooting", "in", "d", "c"));
        broker.addSubscriber(new Subscriber("boko", "haram", "kidnapped", "french", "tourists"));
        broker.addSubscriber(new Subscriber("pope", "washed", "muslims", "feet"));
        broker.addSubscriber(new Subscriber("eastern", "australia", "floods"));
        broker.addSubscriber(new Subscriber("whooping", "cough", "epidemic"));
        broker.addSubscriber(new Subscriber("kate", "middleton", "maternity", "wear"));
        broker.addSubscriber(new Subscriber("sotomayor", "prosecutor", "racial", "comments"));
        broker.addSubscriber(new Subscriber("pope", "candidates"));
        broker.addSubscriber(new Subscriber("hubble", "oldest", "star"));
        broker.addSubscriber(new Subscriber("injuries", "by", "pets"));
        broker.addSubscriber(new Subscriber("yarn", "bombing"));
        broker.addSubscriber(new Subscriber("evernote", "hacked"));
        broker.addSubscriber(new Subscriber("organized", "crime", "sports", "doping", "australia"));
        broker.addSubscriber(new Subscriber("argo", "wins", "oscar"));
        broker.addSubscriber(new Subscriber("type", "ii", "diabetes", "research"));
        broker.addSubscriber(new Subscriber("merging", "of", "us", "air", "and", "american"));
        broker.addSubscriber(new Subscriber("dog", "off", "leash"));
        broker.addSubscriber(new Subscriber("mad", "men", "season", "6"));
        broker.addSubscriber(new Subscriber("higgs", "boson", "discovery"));
        broker.addSubscriber(new Subscriber("us", "embassy", "in", "ankara", "bombed"));
        broker.addSubscriber(new Subscriber("snow", "blower", "problems"));
        broker.addSubscriber(new Subscriber("port", "said", "football", "riot", "death", "sentences"));
        broker.addSubscriber(new Subscriber("``", "oz", "the", "great", "and", "powerful", "''", "opens"));
        broker.addSubscriber(new Subscriber("david", "cameron", "apology", "amritsar"));
        broker.addSubscriber(new Subscriber("dorner", "truck", "compensation"));
        broker.addSubscriber(new Subscriber("math", "common", "core"));
        broker.addSubscriber(new Subscriber("cherry", "blossom", "washington"));
        broker.addSubscriber(new Subscriber("care", "of", "iditarod", "dogs"));
        broker.addSubscriber(new Subscriber("downton", "abbey", "lady", "mary", "beau"));
        broker.addSubscriber(new Subscriber("irish", "laundries", "apology"));
        broker.addSubscriber(new Subscriber("commentary", "on", "naming", "storm", "nemo"));
        broker.addSubscriber(new Subscriber("tiger", "woods", "regains", "title"));
        broker.addSubscriber(new Subscriber("shahbag", "protest"));
        broker.addSubscriber(new Subscriber("sherlock", "elementary", "bbc", "cbs"));
        broker.addSubscriber(new Subscriber("election", "of", "hugo", "chavez", "successor"));
        broker.addSubscriber(new Subscriber("us", "fines", "google", "over", "street", "view"));
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
