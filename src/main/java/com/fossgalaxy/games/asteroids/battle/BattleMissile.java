package com.fossgalaxy.games.asteroids.battle;

import com.fossgalaxy.games.asteroids.math.Vector2d;

import java.awt.*;

import static com.fossgalaxy.games.asteroids.battle.Constants.pColors;

public class BattleMissile extends GameObject {

    private int timeToLive;
    private Color color;

    public BattleMissile(Vector2d s, Vector2d v, int timeToLive) {
        super(s, v);
        color = pColors[0];
        this.timeToLive = timeToLive;
        radius = 4;
    }

    @Override
    public void update() {
        if (!isDead()) {
            location.add(velocity);
            timeToLive--;
        }
    }

    @Override
    public BattleMissile copy() {
        BattleMissile copy = new BattleMissile(location, velocity, timeToLive);
        updateClone(copy);
        copy.timeToLive = timeToLive;
        copy.color = color;
        return copy;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (location.x - radius), (int) (location.y - radius), (int) radius * 2, (int) radius * 2);
    }

    public boolean isDead() {
        return timeToLive <= 0;
    }

    public void hit() {
        // kill it by setting timeToLive to zero
        timeToLive = 0;
    }

    public String toString() {
        return timeToLive + " :> " + location;
    }


}
