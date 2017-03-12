package models;

public class Particle {

  private int id;
  private double radius;
  private Point position;

  public Particle(int id, double radius, double x, double y) {
    this.id = id;
    this.radius = radius;
    this.position = new Point(x, y);
  }

  public Point getPosition() {
    return this.position;
  }

  public double getRadius() {
    return this.radius;
  }

  public int getId() {
    return this.id;
  }

  public static double getDistance(Particle p, Particle q) {
    double dx = Math.abs(p.getPosition().x - q.getPosition().x);
    double dy = Math.abs(p.getPosition().y - q.getPosition().y);
    double hyp = Math.sqrt(dx*dx +dy*dy);
    return hyp - p.getRadius() - q.getRadius();
  }
}
