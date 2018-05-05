package battle;

import math.Vector2d;

import java.awt.*;

import static battle.Constants.*;

public class BattleMissile extends GameObject {

    int ttl;
    int id;
    Color color;

    public BattleMissile(Vector2d s, Vector2d v, int id) {
        super(s, v);
        this.id = id;
        color = pColors[id];
        ttl = missileTTL;
        radius = 4;
    }

    @Override
    public void update() {
        if (!isDead()) {
            location.add(velocity);
            ttl--;
        }
    }

    @Override
    public BattleMissile copy() {
        BattleMissile copy = new BattleMissile(location, velocity, id);
        updateClone(copy);
        copy.ttl = ttl;
        copy.color = color;
        return copy;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (location.x-radius), (int) (location.y-radius), (int) radius * 2, (int) radius * 2);
    }

    public boolean isDead() {
        return ttl <= 0;
    }

    public void hit() {
        // kill it by setting ttl to zero
        ttl = 0;
    }

    public String toString() {
        return ttl + " :> " + location;
    }


}
