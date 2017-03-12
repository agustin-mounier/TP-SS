package cellindexmethod;

import models.Particle;
import models.Point;

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

    private Map<Particle, Set<Particle>> neighbours;

    // TODO: What's "condiciones periodicas de contorno"
    public CellIndexMethod(double l, double rc, int m, List<Particle> particles) {
        this.l = l;
        this.rc = rc;
        this.m = m;
        cellLenght = l/m;

        insertParticles(m, particles);
        insertNeighbours(particles);
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

    public void insertNeighbours(List<Particle> particles) {
        for (Particle p :
             particles) {
            this.neighbours.put(p, new HashSet<>());
        }
    }

    public Set<Particle> getNeighbours(Particle particle) {

        Point point = particle.getPosition();
        int cellX = (int) (point.x / cellLenght);
        int cellY = (int) (point.y / cellLenght);
        Set<Particle> neighbours = new HashSet<>();

        for(int i = cellX - 1; i <= cellX + 1; i ++) {
            if ( i < 0 || i > m) continue;
            for (int j = cellY - 1; j <= cellY + 1; j++) {
                if (j < 0 || j > m) continue;
                neighbours.addAll(matrix[i][j]);
            }
        }
        neighbours.remove(particle);
        return neighbours;
    }

}
