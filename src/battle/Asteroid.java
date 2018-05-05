package battle;

import math.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static battle.Constants.radii;
import static battle.Constants.rand;

public class Asteroid extends GameObject {
    static int nPoints = 16;
    static double radialRange = 0.6;
    static Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private int[] px, py;
    private double rotRate;
    private double rot;
    private boolean dead;
    private int size;

    public Asteroid( Vector2d s, Vector2d v, int size) {
        super(s, v);
        rotRate = (rand.nextDouble() - 0.5) * Math.PI / 20;
        rot = 0;
        this.size = size;
        r = radii[size];
        setPolygon();
    }

    public boolean dead() {
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
            double rad = r * (1 - radialRange / 2
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
        g.translate(s.x, s.y);
        // System.out.println("Drawing at " + s);
        g.rotate(rot);
        // g.fillPolygon(px, py, px.length);
        g.setColor(Color.white);
        g.setStroke(stroke);
//        g.drawPolygon(px, py, px.length);
        g.drawOval(
                (int)(-r),
                (int)(-r),
                (int)(r * 2),
                (int)(r * 2)
        );
        // restore original coordinate system
        g.setTransform(at);
    }

    @Override
    public GameObject copy() {
        Asteroid asteroid = new Asteroid(s, v, size);
        updateClone(asteroid);
        return asteroid;
    }

    public void update() {
        s.add(v);
        rot += rotRate;
    }
    public String toString() {
        return s.toString();
    }

    public void hit() {
        dead = true;
    }
}
