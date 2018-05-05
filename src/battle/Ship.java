package battle;

import math.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static battle.Constants.*;

public class Ship extends GameObject {

    public static double scale = 5;
    public static double maxRelease = 10;
    // define the shape of the ship
    static int[] xp = {-2, 0, 2, 0};
    static int[] yp = {2, -2, 2, 0};
    // this is the thrust poly that will be drawn when the ship
    // is thrusting
    static int[] xpThrust = {-2, 0, 2, 0};
    static int[] ypThrust = {2, 3, 2, 0};
    // define how quickly the ship will rotate
    private double steerStep;
    private double maxSpeed;
    // this is the friction that makes the ship slow down over time
    private double loss = 0.99;
    private static double gravity = 0.0;
    public Vector2d direction;
    double minVelocity = 2;
    private double releaseVelocity = 0;
    private Color color = Color.white;
    private boolean thrusting = false;


    public Ship(Vector2d location, Vector2d velocity, Vector2d direction, int steerRate, int maxSpeed) {
        super(new Vector2d(location, true), new Vector2d(velocity, true));
        this.direction = new Vector2d(direction, true);
        this.steerStep = steerRate * Math.PI / 180;
        this.maxSpeed = maxSpeed;
    }

    private static double clamp(double speed, double min, double max) {
        if (speed > max) {
            return max;
        }

        if (speed < min) {
            return min;
        }

        return speed;
    }

    public Ship copy() {
        Ship ship = new Ship(location, velocity, direction, (int)(this.steerStep / (Math.PI *180)), (int)(this.maxSpeed));
        ship.releaseVelocity = releaseVelocity;
        return ship;
    }

    public double radius() {
        return scale * 2.4;
    }

    public void reset() {
        location.set(width / 2, height / 2);
        velocity.zero();
        direction.set(0, -1);
        dead = false;
        // System.out.println("Reset the ship ");
    }

    public Ship update(Action action) {

        // what if this is always on?

        // action has fields to specify thrust, turn and shooting

        // action.thrust = 1;

        thrusting = action.thrust > 0;

        //prevent people from cheating
        double thrustSpeed = clamp(action.thrust, 0, 1);
        double turnAngle = clamp(action.turn, -1, 1);

        direction.rotate(turnAngle * steerStep);
        velocity.add(direction, thrustSpeed * t * 0.3 / 2);
        velocity.y += gravity;
        // v.x = 0.5;
        velocity.multiply(loss);

        // This is fairly basic, but it'll do for now...
        velocity.x = clamp(velocity.x, -maxSpeed, maxSpeed);
        velocity.y = clamp(velocity.y, -maxSpeed, maxSpeed);

        location.add(velocity);

        return this;
    }

    public String toString() {
        return location + "\t " + velocity;
    }

    @Override
    public void update() {

    }

    public void draw(Graphics2D g) {
        color = Color.green;
        AffineTransform at = g.getTransform();
        g.translate(location.x, location.y);
        double rot = Math.atan2(direction.y, direction.x) + Math.PI / 2;
        g.rotate(rot);
        g.scale(scale, scale);
        g.setColor(color);
        g.fillPolygon(xp, yp, xp.length);
        if (thrusting) {
            g.setColor(Color.red);
            g.fillPolygon(xpThrust, ypThrust, xpThrust.length);
        }
        g.setTransform(at);
    }

    public void hit() {
        // super.hit();
//         System.out.println("Ship destroyed");
        dead = true;
        // sounds.play(sounds.bangLarge);
    }

    public boolean isDead() {
        return dead;
    }


}
