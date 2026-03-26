package org.trustacean.pubsubqe;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

public class Sim {

    public static void main(String[] args) {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<>("PubSubSimulator");
        Model model = new Model(simulator);

        Replication<Double> replication = new SingleReplication<>("rep1", 0.0, 0.0, 100000.0);
        simulator.initialize(model, replication);
        simulator.start();

        System.out.println("Simulation finished.");
    }
}
