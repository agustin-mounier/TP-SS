import cellindexmethod.CellIndexMethod;
import models.Particle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;


/**
 * Created by amounier on 3/12/17.
 */
public class main {

    /**
     * La granularidad de la grilla (dada por M) tiene que ser de forma tal que a la hora de tomar una celda
     * aledaña para calcular sus vecinos, esta celda contenga la menor cantidad de particulas cuya distancia a la
     * particula en cuestion sea mayor que Rc.
     */

    private static int CANT_PARTICLES = 1000;
    private static int L = 20;
    private static double RADIUS = 0.25;
    private static final double RC = 1;
    private static final int M = (int) Math.ceil(L / (RC + 2 * RADIUS));
    private static int DISTINGUISHED;
    private static int TIME;

    private static final String STATIC_FILE = System.getProperty("user.dir") + "/Static100.txt";
    private static final String DYNAMIC_FILE = System.getProperty("user.dir") + "/Dynamic100.txt";


    static List<Particle> particles = new ArrayList<>();

    public static void main(String[] args) {


        Random r = new Random();
        DISTINGUISHED = r.nextInt(CANT_PARTICLES);
//        try {
//            PrintWriter writer = new PrintWriter("all-cells.xyz", "UTF-8");
//            writer.println(CANT_PARTICLES);
//            writer.println(L);
//            for (int i = 0; i < CANT_PARTICLES; i++) {
//                double x = L * r.nextDouble();
//                double y = L * r.nextDouble();
//                Particle particle = new Particle(i, RADIUS, x, y);
//                while (!isValid(particle)) {
//                    x = L * r.nextDouble();
//                    y = L * r.nextDouble();
//                    particle = new Particle(i, RADIUS, x, y);
//                }
//
//                writer.println(Particle.getXYZformat(particle, 255, 255, 255));
//                //particles.add(particle);
//            }
//            writer.close();
//        } catch (IOException e) {
//            // do something
//        }

        readStaticFile();
        readDynamicFile();
        CellIndexMethod cellIndexMethod = new CellIndexMethod(L, RC, M, particles, DISTINGUISHED, false);
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

    private static void readStaticFile() {
        BufferedReader bufferedReader = null;
        try {
            String sCurrentLine;
            int i = 0;
            int particleId = 0;
            bufferedReader = new BufferedReader(new FileReader(STATIC_FILE));
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
                double radius = 0, property = 0;
                int auxCount = 0;
                while (tk.hasMoreTokens()) {
                    switch (auxCount) {
                        case 0:
                            radius = Double.parseDouble(tk.nextToken());
                            break;
                        case 1:
                            property = Double.parseDouble(tk.nextToken());
                            break;
                        default:
                            tk.nextToken();
                            break;
                    }
                    auxCount++;
                }
                ;
                particles.add(new Particle(particleId, radius, property, 0, 0));
                particleId++;
            }
            bufferedReader.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void readDynamicFile() {
        BufferedReader bufferedReader = null;
        try {
            String sCurrentLine;
            int i = 0;
            int particleId = 0;
            bufferedReader = new BufferedReader(new FileReader(DYNAMIC_FILE));
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(sCurrentLine);
                if (i == 0) {
                    TIME = Integer.parseInt(sCurrentLine);
                    i++;
                    continue;
                }
                double x = 0, y = 0;
                int auxCount = 0;
                while (tk.hasMoreTokens()) {
                    switch (auxCount) {
                        case 0:
                            x = Double.parseDouble(tk.nextToken());
                            break;
                        case 1:
                            y = Double.parseDouble(tk.nextToken());
                            break;
                        default:
                            tk.nextToken();
                            break;
                    }
                    auxCount++;
                }
                ;
                particles.get(particleId).setPosition(x, y);
                particleId++;
            }
            bufferedReader.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}



