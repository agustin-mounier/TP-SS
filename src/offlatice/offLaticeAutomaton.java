package offlatice;

import cellindexmethod.CellIndexMethod;
import models.DynamicParticle;
import models.Particle;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.MarshalledObject;
import java.util.*;

/**
 * Created by sebastian on 3/18/17.
 */
public class OffLaticeAutomaton extends CellIndexMethod {

    protected double deltaTheta;
    private double n;
    private Map<Integer, List<DynamicParticle>> simulation = new HashMap<>();

    public OffLaticeAutomaton(double l, double rc, double radius, List<DynamicParticle> particles, int distinguished, boolean periodicBoundry, double n) {
        super(l, rc, radius, particles, distinguished, periodicBoundry);
        this.n = n;
        simulation.put(0, particles);
    }


    public void calculateDistances(int T) {
        List<DynamicParticle> nextStep = new ArrayList<>();
        Random random = new Random();
        deltaTheta = -1 * n / 2 + random.nextDouble() * n;
        for (Particle particle : simulation.get(T)) {
            double sinSum = 0;
            double cosSum = 0;
            int cant = 0;
            for (Particle neighbour : getNeighbours(particle)) {
                if (alreadyCalculated(particle, neighbour)) continue;
                double distance = Particle.getDistance(particle, neighbour);
                if (distance < rc) {
                    addNeighbourToMap(particle, neighbour);
                    DynamicParticle dynamicNeighbour = (DynamicParticle) neighbour;
                    sinSum += Math.sin(dynamicNeighbour.getAngle());
                    cosSum += Math.cos(dynamicNeighbour.getAngle());
                    cant++;
                }
            }
            DynamicParticle dynamicParticle = (DynamicParticle) particle;
            sinSum += Math.sin(dynamicParticle.getAngle());
            cosSum += Math.cos(dynamicParticle.getAngle());
            cant++;
            double newAngle = Math.atan2(sinSum / cant, cosSum / cant) + deltaTheta;
            particle.setPosition(particle.getPosition().x + Math.cos(newAngle), particle.getPosition().y + Math.sin(newAngle));
            nextStep.add(new DynamicParticle(particle, newAngle, dynamicParticle.getVelocity()));
        }
        simulation.put(T + 1, nextStep);
    }

    public void simulate(int TMax) {
        int T = 0;
        while (T < TMax) {
            calculateDistances(T);
            T++;
        }
        createSimulationFile();
    }

    private void createSimulationFile() {
        try {
            PrintWriter painter = new PrintWriter("OffLaticeSimulation.xyz", "UTF-8");
            for (Integer t : simulation.keySet()) {
                painter.println((int) (particles.size()));
                painter.println(t);
                for (DynamicParticle p : simulation.get(t)) {
                    painter.println(p.toString());
                }
            }
            painter.close();
        } catch (Exception e) {

        }
    }




}
