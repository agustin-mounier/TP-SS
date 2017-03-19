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

    public DynamicParticle(Particle p, double angle, double velocity) {
        super(p.getId(), p.getRadius(), p.getRc(), p.getPosition().x, p.getPosition().y);
        this.angle = angle;
        this.velocity = velocity;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getAngle() {
        return angle;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return getId() + " " + getPosition().x + " " + getPosition().y + " " + "0.05 " + angle + " " + velocity;
    }
}
