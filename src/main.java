import cellindexmethod.CellIndexMethod;
import models.Particle;
import models.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Created by amounier on 3/12/17.
 */
public class main {

    private static int CANT_PARTICLES = 1000;
    private static int L = 20;
    private static double RADIUS = 0.25;
    private static final double RC = 1;
    private static final int M = 20;
    private static int DISTINGUISHED;


    static List<Particle> particles = new ArrayList<>();

    public static void main(String[] args) {


        Random r = new Random();
        DISTINGUISHED = r.nextInt(CANT_PARTICLES);

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

                writer.println(Particle.getXYZformat(particle, 255, 255, 255));
                //particles.add(particle);
            }
            writer.close();
        } catch (IOException e) {
            // do something
        }
        readFromFile();
        CellIndexMethod cellIndexMethod = new CellIndexMethod(L, RC, M, particles, DISTINGUISHED);
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

    private static void readFromFile() {
        BufferedReader bufferedReader = null;
        try {
            String sCurrentLine;
            int i = 0;
            bufferedReader = new BufferedReader(new FileReader("all-cells.xyz"));
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(sCurrentLine);
                if (i == 0) {
                    CANT_PARTICLES = Integer.parseInt(sCurrentLine);
                    Random r = new Random();
                    DISTINGUISHED = r.nextInt(CANT_PARTICLES);
                    i++;
                    continue;
                } else if (i == 1) {
                    L = Integer.parseInt(sCurrentLine);
                    i++;
                    continue;
                }

                int id = 0;
                double radius = 0, x = 0, y = 0;
                int auxCount = 0;
                while( tk.hasMoreTokens() ) {
                    switch (auxCount) {
                        case 0: radius = Double.parseDouble(tk.nextToken());
                                break;
                        case 1: id = Integer.parseInt(tk.nextToken());
                                break;
                        case 2: x = Double.parseDouble(tk.nextToken());
                                break;
                        case 3: y = Double.parseDouble(tk.nextToken());
                                break;
                        default: tk.nextToken();
                                break;
                    }
                    auxCount++;
                };
                particles.add(new Particle(id, radius, x, y));
            }
            bufferedReader.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}



