package cellindexmethod;

import models.DynamicParticle;
import models.Particle;
import models.Point;

import java.util.*;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by sebastian on 3/12/17.
 */
public class CellIndexMethod {

    Set<Particle>[][] matrix;

    protected double cellLenght;

    protected double l;
    protected double rc;
    protected int m;
    protected int distinguished;

    protected List<? extends Particle> particles;
    protected boolean periodicBoundry = false;
    protected Map<Integer, Set<Integer>> interactionNeighbours = new HashMap<>();
    protected Map<Particle, Set<Particle>> cellNeighbours = new HashMap<>();

    public CellIndexMethod(double l, double rc, double radius, List<? extends Particle> particles, int distinguished, boolean periodicBoundry) {
        this.l = l;
        this.rc = rc;
        this.m = (int) Math.ceil(l / (rc + 2 * radius));
        this.distinguished = distinguished;
        cellLenght = l / m;
        this.particles = particles;
        this.periodicBoundry = periodicBoundry;
        insertParticles(m, particles);
        getAllCellNeighbours(particles);
    }

    private void insertParticles(int m, List<? extends Particle> particles) {
        matrix = new Set[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = new HashSet<>();
            }
        }
        for (Particle p :
                particles) {
            Point position = p.getPosition();
            matrix[(int) (position.x / cellLenght)][(int) (position.y / cellLenght)].add(p);
        }
    }

    public Set<Particle> getNeighbours(Particle particle) {

        Point point = particle.getPosition();
        int cellX = (int) (point.x / cellLenght);
        int cellY = (int) (point.y / cellLenght);
        Set<Particle> neighbours = new HashSet<>();

        for (int i = cellX - 1; i <= cellX + 1; i++) {
            if (!periodicBoundry && (i < 0 || i >= m)) continue;

            for (int j = cellY - 1; j <= cellY + 1; j++) {
                if (!periodicBoundry && (j < 0 || j >= m)) continue;

                if (periodicBoundry)
                    addNeighboursWithPeriodicBoundries(neighbours, i, j);
                else
                    neighbours.addAll(matrix[i][j]);
            }
        }
        neighbours.remove(particle);
        return neighbours;
    }

    protected void getAllCellNeighbours(List<? extends Particle> particles) {
        cellNeighbours.clear();
        for (Particle particle :
                particles) {
            Point point = particle.getPosition();
            int cellX = (int) (point.x / cellLenght);
            int cellY = (int) (point.y / cellLenght);

            cellNeighbours.put(particle, new HashSet<Particle>());

            for(int i = cellX -1; i <= cellX +1 ; i++) {
                if (!periodicBoundry && (i < 0 || i >= m)) continue;
                for(int j = cellY; j <= cellY + 1; j++) {
                    if ((!periodicBoundry && (j < 0 || j >= m)) || (i == cellX -1 && j == cellY)) continue;

                    if (periodicBoundry)
                        addNeighboursWithPeriodicBoundries(cellNeighbours.get(particle), i, j);
                    else
                        cellNeighbours.get(particle).addAll(matrix[i][j]);
                }
            }
            cellNeighbours.get(particle).remove(particle);
        }
    }

    private void addNeighboursWithPeriodicBoundries(Set<Particle> neighbours, int i, int j) {
        Set<Particle> neighbourCell = new HashSet<>();
        double deltaX = 0;
        double deltaY = 0;

        if (i < 0 && j < 0) {
            neighbourCell = matrix[i + m][j + m];
            deltaX = l * -1;
            deltaY = l * -1;
        } else if (i >= m && j >= m) {
            neighbourCell = matrix[i - m][j - m];
            deltaX = l;
            deltaY = l;
        } else if (i >= m && j < 0) {
            neighbourCell = matrix[i - m][j + m];
            deltaX = l;
            deltaY = l * -1;
        } else if (i < 0 && j >= m) {
            neighbourCell = matrix[i + m][j - m];
            deltaX = l * -1;
            deltaY = l;
        } else if (i < 0 && (j >= 0 && j < m)) {
            neighbourCell = matrix[i + m][j];
            deltaX = l * -1;
            deltaY = 0;
        } else if (i >= m && (j >= 0 && j < m)) {
            neighbourCell = matrix[i - m][j];
            deltaX = l;
            deltaY = 0;
        } else if (j < 0 && (i >= 0 && i < m)) {
            neighbourCell = matrix[i][j + m];
            deltaX = 0;
            deltaY = l * -1;
        } else if (j >= m && (i >= 0 && i < m)) {
            neighbourCell = matrix[i][j - m];
            deltaX = 0;
            deltaY = l;
        }

        if (deltaX == 0 && deltaY == 0) {
            neighbours.addAll(matrix[i][j]);
        } else {
            for (Particle p : neighbourCell) {
                DynamicParticle dp = (DynamicParticle) p;
                neighbours.add(new DynamicParticle(p.getId(), p.getRadius(), p.getRc(), p.getPosition().x + deltaX, p.getPosition().y + deltaY, dp.getAngle(), dp.getVelocity()));
            }
        }
    }

    public void calculateDistances() {
        long start = System.currentTimeMillis();
        for (Particle particle : particles) {
            for (Particle neighbour : cellNeighbours.get(particle)) {
                if (alreadyCalculated(particle, neighbour)) continue;
                double distance = Particle.getDistance(particle, neighbour);
                if (distance < rc) {
                    addNeighbourToMap(particle, neighbour);
                }
            }
        }

        System.out.println(m + "\t" + (System.currentTimeMillis() - start) + "\t" + particles.size() + "\t" + l);
        //System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " M: " + m + " N: " + particles.size() + " cant: " + cant);
        /*
        for (Integer p : interactionNeighbours.keySet()) {

            System.out.print(p + " ");
            for (Integer n : interactionNeighbours.get(p)) {
                System.out.print(n + " ");
            }
            System.out.print("\n");
        }
        */

    }

    protected void addNeighbourToMap(Particle particle, Particle neighbour) {
        if (interactionNeighbours.containsKey(particle.getId())) {
            interactionNeighbours.get(particle.getId()).add(neighbour.getId());

            if (interactionNeighbours.containsKey(neighbour.getId())) {
                interactionNeighbours.get(neighbour.getId()).add(particle.getId());
            } else {
                Set<Integer> neighbourSet = new HashSet<>();
                neighbourSet.add(particle.getId());
                interactionNeighbours.put(neighbour.getId(), neighbourSet);
            }
        } else {
            Set<Integer> particleSet = new HashSet<>();
            particleSet.add(neighbour.getId());
            interactionNeighbours.put(particle.getId(), particleSet);

            Set<Integer> neighbourSet = new HashSet<>();
            neighbourSet.add(particle.getId());
            interactionNeighbours.put(neighbour.getId(), neighbourSet);
        }
    }

    private void writeResults() {
        String s = "";

        try {
            PrintWriter writer = new PrintWriter("cellIndexMethod.txt", "UTF-8");

            for (Integer particleId : interactionNeighbours.keySet()) {
                s = particleId + " ";
                for (Integer neighbourId : interactionNeighbours.get(particleId)) {
                    s += neighbourId + ", ";

                }
            }
            writer.println(s);
            writer.println("***************************************************************************************");
            writer.close();
        } catch (IOException e) {
            // do something
        }

    }

    protected boolean alreadyCalculated(Particle particle, Particle neighbour) {
        return interactionNeighbours.containsKey(particle) && interactionNeighbours.get(particle).contains(neighbour);
    }

    public void calculateDistancesWithBruteForce() {
        long start = System.currentTimeMillis();
        int cant = 0;
        for (Particle particle : particles) {
            for (Particle neighbour : particles) {
                if (particle == neighbour) continue;
                double distance = Particle.getDistance(particle, neighbour);
                if (distance < rc)
                    cant++;
            }
        }

        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " cant " + cant);
    }


    public void generateFileWithDistinction(int id, List<Particle> particles) {
        Set<Particle> aux = new HashSet<>();

        try {
            PrintWriter painter = new PrintWriter("cell-and-neig.xyz", "UTF-8");

            painter.println((int) (particles.size()));
            painter.println(this.l);

            Particle selectedOne = particles.get(id);
            painter.println(Particle.getXYZformat(selectedOne, 110, 110, 0));
            for (Particle neighbour :
                    this.getNeighbours(selectedOne)) {
                Set<Integer> particleNeighbours = interactionNeighbours.get(selectedOne.getId());

                /*
                 * Neighbours with distance < rc
                 */
                if (particleNeighbours != null && particleNeighbours.contains(neighbour.getId())) {
                    painter.println(Particle.getXYZformat(neighbour, 110, 0, 0));
                } else {
                    /*
                     * Neighbours in surrounding cells with distance > rc
                     */
                    painter.println(Particle.getXYZformat(neighbour, 0, 0, 110));
                }
                aux.add(neighbour);
            }

            aux.add(selectedOne);

            for (Particle particle :
                    particles) {
                if (aux.contains(particle)) continue;
                painter.println(Particle.getXYZformat(particle, 255, 255, 255));
            }
            painter.close();
        } catch (IOException e) {

        }
    }

}
