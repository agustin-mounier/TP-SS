import cellindexmethod.CellIndexMethod;
import models.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amounier on 3/12/17.
 */
public class main {

    private static final int CANT_PARTICLES = 300;
    private static final int L = 20;
    private static final double RADIUS = 0.25;
    private static final double RC = 1;
    /**
     * La granularidad de la grilla (dada por M) tiene que ser de forma tal que a la hora de tomar una celda
     * aledaña para calcular sus vecinos, esta celda contenga la menor cantidad de particulas cuya distancia a la
     * particula en cuestion sea mayor que Rc.
     */
    private static int M = (int) Math.ceil(L / (RC + 2 * RADIUS));


    static List<Particle> particles = new ArrayList<>();

    public static void main(String[] args) {


        Random r = new Random();

        for (int i = 0; i < CANT_PARTICLES; i++) {
            double x = L * r.nextDouble();
            double y = L * r.nextDouble();
            Particle particle = new Particle(i, RADIUS, x, y);
            while (!isValid(particle)) {
                x = L * r.nextDouble();
                y = L * r.nextDouble();
                particle = new Particle(i, RADIUS, x, y);
            }
            particles.add(particle);
        }


//        for(int i = M; i <= 20 ; i++) {
//            CellIndexMethod cellIndexMethod = new CellIndexMethod(L, RC, i, particles, false);
//            cellIndexMethod.calculateDistances();
//        }
        CellIndexMethod cellIndexMethod = new CellIndexMethod(L, RC, M, particles, true);
        cellIndexMethod.calculateDistances();
        cellIndexMethod.calculateDistancesWithBruteForce();
    }

    public static boolean isValid(Particle p) {
        for (Particle p2 : particles) {
            if (Particle.getDistance(p, p2) < 0)
                return false;
        }
        return true;
    }
}



