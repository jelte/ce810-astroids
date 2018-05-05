package battle;

import math.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static battle.Constants.*;

public class Ship extends GameObject {

    // define the shape of the ship
    static int[] xp = {-2, 0, 2, 0};
    static int[] yp = {2, -2, 2, 0};

    // this is the thrust poly that will be drawn when the ship
    // is thrusting
    static int[] xpThrust = {-2, 0, 2, 0};
    static int[] ypThrust = {2, 3, 2, 0};
    public static double scale = 5;

    // define how quickly the ship will rotate
    static double steerStep = 10 * Math.PI / 180;
    static double maxSpeed = 3;

    // this is the friction that makes the ship slow down over time
    static double loss = 0.99;

    private double releaseVelocity = 0;
    double minVelocity = 2;
    public static double maxRelease = 10;
    private Color color = Color.white;
    private boolean thrusting = false;

    private static double gravity = 0.0;

    // position and velocity
    public Vector2d d;

    // played id (used for drawing)
    private int playerID;


    public Ship(Vector2d location, Vector2d velocity, Vector2d d, int playerID) {
        super(new Vector2d(location, true), new Vector2d(velocity, true));
        this.d = new Vector2d(d, true);
        this.playerID = playerID;
    }

    public Ship copy() {
        Ship ship = new Ship(location, velocity, d, playerID);
        ship.releaseVelocity = releaseVelocity;
        return ship;
    }

    public double radius() {
        return scale * 2.4;
    }

//    public Ship() {
//        super(new Vector2d(), new Vector2d());
//        d = new Vector2d(0, -1);
//    }
//

    public void reset() {
        location.set(width / 2, height / 2);
        velocity.zero();
        d.set(0, -1);
        dead = false;
        // System.out.println("Reset the ship ");
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

    public Ship update(Action action) {

        // what if this is always on?

        // action has fields to specify thrust, turn and shooting

        // action.thrust = 1;

        thrusting = action.thrust > 0;

        //prevent people from cheating
        double thrustSpeed = clamp(action.thrust, 0, 1);
        double turnAngle = clamp(action.turn, -1, 1);

        d.rotate(turnAngle * steerStep);
        velocity.add(d, thrustSpeed * t * 0.3 / 2);
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
        color = playerID == 0 ? Color.green : Color.blue;
        AffineTransform at = g.getTransform();
        g.translate(location.x, location.y);
        double rot = Math.atan2(d.y, d.x) + Math.PI / 2;
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
