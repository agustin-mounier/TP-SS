import cellindexmethod.CellIndexMethod;
import models.Particle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


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
    private static double L = 20;
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

        readStaticFile();
        readDynamicFile();
        CellIndexMethod cellIndexMethod = new CellIndexMethod(L, RC, M, particles, DISTINGUISHED, false);
        cellIndexMethod.calculateDistances();
        cellIndexMethod.calculateDistancesWithBruteForce();
        cellIndexMethod.generateFileWithDistinction(DISTINGUISHED, particles);
        //generateRandomFiles(1000,25,0.25,1);
    }

    public static boolean isValid(Particle p, List<Particle> particles) {
        for (Particle p2 : particles) {
            if (Particle.getDistance(p, p2) < 0)
                return false;
        }
        return true;
    }

    private static void readStaticFile() {
        BufferedReader bufferedReader;
        try {
            String sCurrentLine;
            int i = 0;
            int particleId = 0;
            bufferedReader = new BufferedReader(new FileReader("static-random.txt"));
            List<Double> aux;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(sCurrentLine);

                if (i == 0) {
                    CANT_PARTICLES = Integer.parseInt(sCurrentLine);
                    Random r = new Random();
                    DISTINGUISHED = r.nextInt(CANT_PARTICLES);
                    i++;
                    continue;
                } else if (i == 1) {
                    L = Double.parseDouble(sCurrentLine);
                    i++;
                    continue;
                }

                double radius, property;
                aux = reader(tk);
                radius = aux.get(0);
                property = aux.get(1);

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
            List<Double> aux;
            bufferedReader = new BufferedReader(new FileReader("dynamic-random.txt"));
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(sCurrentLine);
                if (i == 0) {
                    TIME = Integer.parseInt(sCurrentLine);
                    i++;
                    continue;
                }
                double x, y;
                aux = reader(tk);
                x = aux.get(0);
                y = aux.get(1);
                particles.get(particleId).setPosition(x, y);
                particleId++;
            }
            bufferedReader.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /*
     * Recieves a String Tokenizer reprsenting a line.
     * Reads from 2 column text and saves the first column value on v1
     * and the second value on v2 of a returned arraylist.
     *
     */
    private static List<Double> reader(StringTokenizer tk) {
        List<Double> answer = new ArrayList<>();
        int auxCount = 0;
        while (tk.hasMoreTokens()) {
            switch (auxCount) {
                case 0:
                    answer.add(Double.parseDouble(tk.nextToken()));
                    break;
                case 1:
                    answer.add(Double.parseDouble(tk.nextToken()));
                    break;
                default:
                    tk.nextToken();
                    break;
            }
            auxCount++;
        }
        return answer;
    }

    private static void generateRandomFiles(int cant_particles, double l, double radius, double rc) {
        Random r = new Random();
        List<Particle> particles = new ArrayList<>(cant_particles);
        try {
            PrintWriter staticWriter = new PrintWriter("static-random.txt", "UTF-8");
            PrintWriter dynamicWriter = new PrintWriter("dynamic-random.txt", "UTF-8");

            staticWriter.println(cant_particles);
            staticWriter.println(l);
            dynamicWriter.println(0);

            for (int i = 0; i < cant_particles; i++) {
                double x = l * r.nextDouble();
                double y = l * r.nextDouble();
                Particle particle = new Particle(i, radius, rc, x, y);
                while (!isValid(particle, particles)) {
                    x = l * r.nextDouble();
                    y = l * r.nextDouble();
                    particle = new Particle(i, radius, rc, x, y);
                }
                staticWriter.println(particle.getRadius() + "\t" + particle.getRc());
                dynamicWriter.println(particle.getPosition().x + "\t" + particle.getPosition().y);
                particles.add(particle);
            }
            staticWriter.close();
            dynamicWriter.close();
        } catch (IOException e) {
            // do something
       }
    }
}



