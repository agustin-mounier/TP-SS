package models;

/**
 * Created by sebastian on 3/18/17.
 */
public class DynamicParticle extends Particle {

    private double angle;
    private double velocity;

    public DynamicParticle(int id, double radius, double rc, double x, double y, double angle, double velocity) {
        super(id, radius, rc, x, y);
        this.angle = angle;
        this.velocity = velocity;
    }
}
