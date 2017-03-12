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
}
