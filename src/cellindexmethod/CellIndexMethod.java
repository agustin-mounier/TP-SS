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

    private Map<Particle, Set<Particle>> neighbours;

    // TODO: What's
    public CellIndexMethod(double l, double rc, int m, List<Particle> particles) {
        this.l = l;
        this.rc = rc;
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
            Point point = p.getPosition();
            matrix[(int)(point.x / cellLenght)][(int)(point.y / cellLenght)].add(p);
        }
    }

    public void insertNeighbours(List<Particle> particles) {
        for (Particle p :
             particles) {
            this.neighbours.put(p, new HashSet<>());
        }
    }

}
