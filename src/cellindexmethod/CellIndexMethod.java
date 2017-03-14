package cellindexmethod;

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

    public double cellLenght;

    private double l;
    private double rc;
    private int m;
    private int distinguished;

    private List<Particle> particles;
    private boolean periodicBoundry = false;
    private Map<Integer, Set<Integer>> neighbours = new HashMap<>();

    public CellIndexMethod(double l, double rc, int m, List<Particle> particles, int distinguished, boolean periodicBoundry) {
        this.l = l;
        this.rc = rc;
        this.m = m;
        this.distinguished = distinguished;
        cellLenght = l / m;
        this.particles = particles;
        this.periodicBoundry = periodicBoundry;
        insertParticles(m, particles);
    }

    private void insertParticles(int m, List<Particle> particles) {
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
                neighbours.add(new Particle(p.getId(), p.getRadius(), p.getRc(), p.getPosition().x + deltaX, p.getPosition().y + deltaY));
            }
        }
    }

    public void calculateDistances() {
        long start = System.currentTimeMillis();
        int cant = 0;
        for (Particle particle : particles) {
            for (Particle neighbour : getNeighbours(particle)) {
                if (alreadyCalculated(particle, neighbour)) continue;
                double distance = Particle.getDistance(particle, neighbour);
                if (distance < rc) {
                    cant++;
                    if (neighbours.containsKey(particle.getId())) {
                        neighbours.get(particle.getId()).add(neighbour.getId());

                        if (neighbours.containsKey(neighbour.getId())) {
                            neighbours.get(neighbour.getId()).add(particle.getId());
                        } else {
                            Set<Integer> neighbourSet = new HashSet<>();
                            neighbourSet.add(particle.getId());
                            neighbours.put(neighbour.getId(), neighbourSet);
                        }
                    } else {
                        Set<Integer> particleSet = new HashSet<>();
                        particleSet.add(neighbour.getId());
                        neighbours.put(particle.getId(), particleSet);

                        Set<Integer> neighbourSet = new HashSet<>();
                        neighbourSet.add(particle.getId());
                        neighbours.put(neighbour.getId(), neighbourSet);
                    }
                }
            }
        }

        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " M: " + m + " N: " + particles.size() + " cant: " + cant);
        for (Integer p : neighbours.keySet()) {
            System.out.print(p + " ");
            for (Integer n : neighbours.get(p)) {
                System.out.print(n + " ");
            }
            System.out.print("\n");
        }

    }

    public void writeResults() {
        String s = "";

        try {
            PrintWriter writer = new PrintWriter("cellIndexMethod.txt", "UTF-8");

            for (Integer particleId : neighbours.keySet()) {
                s = particleId + " ";
                for (Integer neighbourId : neighbours.get(particleId)) {
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

    private boolean alreadyCalculated(Particle particle, Particle neighbour) {
        return neighbours.containsKey(particle) && neighbours.get(particle).contains(neighbour);
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
                Set<Integer> particleNeighbours = neighbours.get(selectedOne.getId());

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
