import cellindexmethod.CellIndexMethod;
import models.Particle;
import models.Point;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Created by amounier on 3/12/17.
 */
public class main {

    private static final int CANT_PARTICLES = 1000;
    private static final int L = 20;
    private static final double RADIUS = 0.25;
    private static final double RC = 1;
    private static final int M = 20;


    static List<Particle> particles = new ArrayList<>();

    public static void main(String[] args) {


        Random r = new Random();

        try {
            PrintWriter writer = new PrintWriter("all-cells.xyz", "UTF-8");
            writer.println(CANT_PARTICLES);
            writer.println(L);
            for (int i = 0; i < CANT_PARTICLES; i++) {
                double x = L * r.nextDouble();
                double y = L * r.nextDouble();
                Particle particle = new Particle(i, RADIUS, x, y);
                while (!isValid(particle)) {
                    x = L * r.nextDouble();
                    y = L * r.nextDouble();
                    particle = new Particle(i, RADIUS, x, y);
                }

                writer.println(Particle.getXYZformat(particle, 0, 0, 0));
                particles.add(particle);
            }
            writer.close();
        } catch (IOException e) {
            // do something
        }

        CellIndexMethod cellIndexMethod = new CellIndexMethod(L, RC, M, particles);
        cellIndexMethod.calculateDistances();
        cellIndexMethod.calculateDistancesWithBruteForce();
    }

    public static boolean isValid(Particle p) {
        for(Particle p2 : particles) {
            if(Particle.getDistance(p, p2) < 0)
                return false;
        }
        return true;
    }
}



