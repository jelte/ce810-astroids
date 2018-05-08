package com.fossgalaxy.games.asteroids.battle;

import com.fossgalaxy.games.asteroids.math.Vector2d;

import java.awt.*;

public abstract class GameObject {
    protected Vector2d location, velocity;
    protected boolean isTarget;
    protected boolean dead;
    protected double radius;

    protected GameObject(Vector2d location, Vector2d velocity) {
        this.location = new Vector2d(location, true);
        this.velocity = new Vector2d(velocity, true);
    }

    public abstract void update();

    public abstract void draw(Graphics2D g);

    public abstract GameObject copy();

    protected GameObject updateClone(GameObject copyObject) {
        copyObject.location = new Vector2d(location, true);
        copyObject.velocity = new Vector2d(velocity, true);
        copyObject.isTarget = isTarget;
        copyObject.dead = dead;
        copyObject.radius = radius;

        return copyObject;
    }

    public abstract boolean isDead();

    public void hit() {
        dead = true;
    }

    public double radius() {
        return radius;
    }

    public boolean wrappable() {
        // wrap objects by default
        return true;
    }

    public Vector2d getLocation() {
        return location;
    }

    public Vector2d getVelocity() {
        return velocity;
    }
}
