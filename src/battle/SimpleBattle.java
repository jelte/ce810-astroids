package battle;

import math.Vector2d;
import utilities.JEasyFrame;

import java.awt.*;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import static battle.Constants.*;

/**
 * Created by simon lucas on 10/06/15.
 * <p>
 * Aim here is to have a simple battle class
 * that enables ships to fish with each other
 * <p>
 * Might start off with just two ships, each with their own types of missile.
 */

public class SimpleBattle {

    private final Object _objects = new Object();
    private int nMissiles = 100;
    private int nTicks = 1000;
    private int releaseVelocity = 5;
    private boolean visible = true;
    private List<GameObject> objects;
    private PlayerStats stats;

    private Ship ship;
    private BattleController player;
    private BattleView view;
    private int currentTick;

    public SimpleBattle() {
        this(true);
    }

    public SimpleBattle(boolean visible) {
        this.objects = new ArrayList<>();
        this.visible = visible;

        if (visible) {
            view = new BattleView(this);
            new JEasyFrame(view, "battle");
        }
    }

    public int getTicks() {
        return currentTick;
    }

    public void playGame(BattleController player) {
        this.player = player;
        reset();

        makeAsteroids(10);
        objects.add(ship);


        stats = new PlayerStats(0, 0);

        if (this.player instanceof KeyListener) {
            view.addKeyListener((KeyListener) this.player);
            view.setFocusable(true);
            view.requestFocus();
        }

        while (!isGameOver()) {
            update();
        }

        if (this.player instanceof KeyListener) {
            view.removeKeyListener((KeyListener) this.player);
        }
    }

    public void reset() {
        objects.clear();
        ship = buildShip(width / 2, height / 2, 0);
        this.currentTick = 0;

        stats = new PlayerStats(0, 0);
    }

    protected Ship buildShip(int x, int y, int playerID) {
        Vector2d position = new Vector2d(x, y, true);
        Vector2d speed = new Vector2d(true);
        Vector2d direction = new Vector2d(1, 0, true);

        return new Ship(position, speed, direction, playerID);
    }

    public void update() {
        update(player.getAction(this.clone()));
    }

    public BattleController getPlayer() {
        return player;
    }

    public void update(Action action) {
        // now apply them to the ships
        ship.update(action);

        synchronized (_objects) {
            for (int first = 0; first < objects.size() - 1; first++) {
                for (int second = first + 1; second < objects.size(); second++) {
                    checkCollision(objects.get(first), objects.get(second));
                }
            }
        }


        // and fire any missiles as necessary
        if (action.shoot) fireMissile(ship.getLocation(), ship.d);

        wrap(ship);

        synchronized (_objects) {
            // here need to add the game objects ...
            List<GameObject> killList = new ArrayList<>();
            for (GameObject object : objects) {
                object.update();
                wrap(object);
                if (object.isDead()) {
                    killList.add(object);
                }
            }

            objects.removeAll(killList);
        }
        currentTick++;

        if (visible) {
            view.repaint();
            sleep();
        }
    }


    public SimpleBattle clone() {
        SimpleBattle state = new SimpleBattle(false);
        state.objects = copyObjects();
        state.stats = new PlayerStats(this.stats.nMissiles, this.stats.nPoints);
        state.currentTick = currentTick;
        state.visible = false; //stop MCTS people having all the games :p

        state.ship = ship.copy();
        return state;
    }

    private List<GameObject> copyObjects() {
        List<GameObject> objectClone = new ArrayList<>();
        for (GameObject object : objects) {
            objectClone.add(object.copy());
        }

        return objectClone;
    }

    private void checkCollision(GameObject first, GameObject second) {
        if (first == second) return;
        if (first instanceof Asteroid && second instanceof Asteroid) return;
        if (first instanceof BattleMissile && second instanceof BattleMissile) return;
        if (overlap(first, second)) {
//            System.out.println(first.getClass().getSimpleName() + " hit " + second.getClass().getSimpleName());
            if (!(first instanceof Ship || second instanceof Ship)) {
                stats.nPoints += 10;
            }
            first.hit();
            second.hit();
        }
    }

    private boolean overlap(GameObject first, GameObject second) {
        // otherwise do the default check
        double dist = first.getLocation().dist(second.getLocation());
        boolean ret = dist < (first.radius() + second.radius());
        return ret;
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fireMissile(Vector2d s, Vector2d d) {
        // need all the usual missile firing code here
        Ship currentShip = ship;
        if (stats.nMissiles < nMissiles) {
            BattleMissile missile = new BattleMissile(s, new Vector2d(0, 0, true), 0);
            missile.getVelocity().add(d, releaseVelocity);
            // make it clear the ship
            missile.getVelocity().add(missile.getVelocity(), (currentShip.radius() + missileRadius) * 1.5 / missile.getVelocity().mag());
            objects.add(missile);
            // System.out.println("Fired: " + m);
            // sounds.fire();
            stats.nMissiles++;
        }
    }

    public void draw(Graphics2D g) {
        // for (Object ob : objects)
        if (ship == null) {
            return;
        }

        // System.out.println("In draw(): " + n);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);

        synchronized (_objects) {
            for (GameObject go : objects) {
                go.draw(g);
            }
        }

        if (!ship.isDead()) {
            ship.draw(g);
        }
        if (player instanceof RenderableBattleController) {
            RenderableBattleController rbc = (RenderableBattleController) player;
            rbc.render(g, ship.copy());
        }
    }

    public Ship getShip() {
        return ship.copy();
    }

    public List<GameObject> getObjects() {
        return new ArrayList<>(objects);
    }

    public int getPoints() {
        return stats.nPoints;
    }

    public int getMissilesLeft() {
        return stats.nMissiles - nMissiles;
    }

    private void wrap(GameObject ob) {
        // only wrap objects which are wrappable
        if (ob.wrappable()) {
            ob.getLocation().x = (ob.getLocation().x + width) % width;
            ob.getLocation().y = (ob.getLocation().y + height) % height;
        }
    }

    public boolean isGameOver() {
        if (ship.isDead()) return true;
        if (getMissilesLeft() >= 0 && getMissilesLeft() >= 0) {
            //ensure that there are no bullets left in play
            if (objects.isEmpty()) {
                return true;
            }
        }

        return currentTick >= nTicks;
    }

    private void makeAsteroids(int nAsteroids) {
        Vector2d centre = new Vector2d(width / 2, height / 2, true);
        // assumes that the game object list is currently empty
        int target = objects.size() + nAsteroids;
        while (objects.size() < target) {
            // choose a random position and velocity
            Vector2d location = new Vector2d(rand.nextDouble() * width,
                    rand.nextDouble() * height, true);
            Vector2d velocity = new Vector2d(rand.nextGaussian(), rand.nextGaussian(), true);
            if (location.dist(centre) > safeRadius && velocity.mag() > 0.5) {
                Asteroid a = new Asteroid(location, velocity, 0);
                objects.add(a);
            }
        }
    }

    public PlayerStats getStats() {
        return stats;
    }

    static class PlayerStats {
        int nMissiles;
        int nPoints;

        public PlayerStats(int nMissiles, int nPoints) {
            this.nMissiles = nMissiles;
            this.nPoints = nPoints;
        }

        public int getMissilesFired() {
            return nMissiles;
        }

        public int getPoints() {
            return nPoints;
        }

        public String toString() {
            return nMissiles + " : " + nPoints;
        }
    }
}
