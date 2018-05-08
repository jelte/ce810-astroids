package com.fossgalaxy.games.asteroids.battle;

import com.fossgalaxy.games.asteroids.math.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static com.fossgalaxy.games.asteroids.battle.Constants.asteroidRadii;
import static com.fossgalaxy.games.asteroids.battle.Constants.rand;

public class Asteroid extends GameObject {
    static int nPoints = 16;
    static double radialRange = 0.6;
    static Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private int[] px, py;
    private double rotRate;
    private double rot;
    private boolean dead;
    private int size;

    public Asteroid(Vector2d location, Vector2d velocity, int size) {
        super(location, velocity);
        rotRate = (rand.nextDouble() - 0.5) * Math.PI / 20;
        rot = 0;
        this.size = size;
        radius = asteroidRadii[size];
        setPolygon();
    }

    public boolean isDead() {
        return dead;
    }

    public void setPolygon() {
        px = new int[nPoints];
        py = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            // generate within certain ranges
            //  in polar coords (radians)
            // then transform to cartesian
            double theta = (Math.PI * 2 / nPoints)
                    * (i + rand.nextDouble());
            double rad = radius * (1 - radialRange / 2
                    + rand.nextDouble() * radialRange);
            px[i] = (int) (rad * Math.cos(theta));
            py[i] = (int) (rad * Math.sin(theta));
        }
//        System.out.println(Arrays.toString(px));
//        System.out.println(Arrays.toString(py));
    }

    public void draw(Graphics2D g) {
        // store coordinate system
        AffineTransform at = g.getTransform();
        if (isTarget) {
            g.setColor(Color.yellow);
        } else {
            g.setColor(Color.magenta);
        }
        g.translate(location.x, location.y);
        // System.out.println("Drawing at " + s);
        g.rotate(rot);
        // g.fillPolygon(px, py, px.length);
        g.setColor(Color.white);
        g.setStroke(stroke);
//        g.drawPolygon(px, py, px.length);
        g.drawOval(
                (int) (-radius),
                (int) (-radius),
                (int) (radius * 2),
                (int) (radius * 2)
        );
        // restore original coordinate system
        g.setTransform(at);
    }

    @Override
    public GameObject copy() {
        Asteroid asteroid = new Asteroid(location, velocity, size);
        updateClone(asteroid);
        return asteroid;
    }

    public void update() {
        location.add(velocity);
        rot += rotRate;
    }

    public String toString() {
        return location.toString();
    }

    public void hit() {
        dead = true;
    }

    public int getSize() {
        return size;
    }
}
