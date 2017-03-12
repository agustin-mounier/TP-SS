package cellindexmethod;

import models.Particle;
import models.Point;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private List<Particle> particles;

    // TODO: What's "condiciones periodicas de contorno"
    public CellIndexMethod(double l, double rc, int m, List<Particle> particles) {
        this.l = l;
        this.rc = rc;
        this.m = m;
        cellLenght = l/m;
        this.particles = particles;
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
            matrix[(int)(position.x / cellLenght)][(int)(position.y / cellLenght)].add(p);
        }
    }

    public Set<Particle> getNeighbours(Particle particle) {

        Point point = particle.getPosition();
        int cellX = (int) (point.x / cellLenght);
        int cellY = (int) (point.y / cellLenght);
        Set<Particle> neighbours = new HashSet<>();

        for(int i = cellX - 1; i <= cellX + 1; i ++) {
            if ( i < 0 || i >= m) continue;
            for (int j = cellY - 1; j <= cellY + 1; j++) {
                if (j < 0 || j >= m) continue;
                neighbours.addAll(matrix[i][j]);
            }
        }
        neighbours.remove(particle);
        return neighbours;
    }

    public void calculateDistances() {
        long start = System.currentTimeMillis();
        String s;

        try{
            PrintWriter writer = new PrintWriter("cellIndexMethod.txt", "UTF-8");

            for(Particle particle : particles) {
                for(Particle neighbour : getNeighbours(particle)) {
                    double distance = Particle.getDistance(particle, neighbour);
                    if(distance < rc) {
                        s = "Particle " + particle.getId() + " to Particle " + neighbour.getId() + ": " + distance;
                        writer.println(s);
                    }
                }
            }
            writer.println("***************************************************************************************");
            writer.println("Elapsed time: " + (System.currentTimeMillis() - start));
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }

    public void calculateDistancesWithBruteForce() {
        long start = System.currentTimeMillis();
        String s;
        try{
            PrintWriter writer = new PrintWriter("bruteForceMethod.txt", "UTF-8");

            for(Particle particle : particles) {
                for(Particle neighbour : particles) {
                    if (neighbour == particle) continue;
                    double distance = Particle.getDistance(particle, neighbour);
                    if(distance < rc) {
                        s = "Particle " + particle.getId() + " to Particle " + neighbour.getId() + ": " + distance;
                        writer.println(s);
                    }
                }
            }
            writer.println("***************************************************************************************");
            writer.println("Elapsed time: " + (System.currentTimeMillis() - start));
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }


}
