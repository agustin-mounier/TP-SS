package offlatice;

import cellindexmethod.CellIndexMethod;
import models.DynamicParticle;
import models.Particle;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.MarshalledObject;
import java.util.*;
import java.util.List;

/**
 * Created by sebastian on 3/18/17.
 */
public class OffLaticeAutomaton extends CellIndexMethod {

    private double n;
    private double vel;
    private Map<Integer, List<DynamicParticle>> simulation = new HashMap<>();
    private Map<Integer, Double> vaEvolution = new HashMap<>();

    public OffLaticeAutomaton(double l, double rc, double radius, List<DynamicParticle> particles, int distinguished, boolean periodicBoundry, double n, double vel) {
        super(l, rc, radius, particles, distinguished, periodicBoundry);
        this.n = n;
        this.vel = vel;
        simulation.put(0, particles);
    }


    public void calculateDistances(int T) {
        List<DynamicParticle> nextStep = new ArrayList<>();
        Random random = new Random();
        double vx = 0;
        double vy = 0;
        for (Particle particle : simulation.get(T)) {
            double deltaTheta = -1 * n / 2 + random.nextDouble() * n;
            double sinSum = 0;
            double cosSum = 0;
            double dx, dy;

            int cant = 0;
            for (Particle neighbour : cellNeighbours.get(particle)) {
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
            sinSum += Math.sin(Math.toDegrees(dynamicParticle.getAngle()));
            cosSum += Math.cos(Math.toDegrees(dynamicParticle.getAngle()));
            cant++;

            double newAngle = Math.atan2(sinSum / cant, cosSum / cant) + deltaTheta;
            dx = Math.cos(newAngle) * dynamicParticle.getVelocity();
            dy = Math.sin(newAngle) * dynamicParticle.getVelocity();
            vx += dx;
            vy += dy;
            nextStep.add(new DynamicParticle(setNewPositionWithBoundry(particle, dx, dy, l), newAngle, dynamicParticle.getVelocity()));
        }
        double va = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)) / (this.particles.size() * vel);
        vaEvolution.put(T+1, va);
        System.out.println(va);
        simulation.put(T + 1, nextStep);
    }

    public void simulate(int TMax) {
        int T = 0;
        while (T < TMax) {
            calculateDistances(T);
            T++;
            cellNeighbours.clear();
            getAllCellNeighbours(simulation.get(T));
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

    private Particle setNewPositionWithBoundry (Particle particle, double dx, double dy, double maxL) {
        double newX = particle.getPosition().x + dx;
        double newY = particle.getPosition().y + dy;

        if (newX > maxL) {
            newX = newX - maxL;
        } else if (newX < 0) {
            newX = maxL + newX;
        }

        if (newY > maxL) {
            newY = newY - maxL;
        } else if (newY < 0) {
            newY = maxL + newY;
        }

        return new Particle(particle.getId(), particle.getRadius(), particle.getRc(), newX, newY);
    }
}
