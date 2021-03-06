package com.fossgalaxy.games.asteroids.battle;

import com.fossgalaxy.games.asteroids.battle.recorders.NullRecorder;
import com.fossgalaxy.games.asteroids.math.Vector2d;
import com.fossgalaxy.games.asteroids.utilities.JEasyFrame;

import java.awt.*;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import static com.fossgalaxy.games.asteroids.battle.Constants.*;

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
    private int tickLimit = 1000;
    private int bulletInitialVelocity = 5;
    private int bulletTimeToLive = 60;
    private int nAsteroids = 10;
    private int nAsteroidChildren = 2;
    private int maxSpeed = 3;
    private int steerRate = 10;
    private boolean bulletKillShip = false;
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
        this(visible, null);
    }

    public SimpleBattle(boolean visible, int[] params) {
        if (params != null) handleParameters(params);
        this.objects = new ArrayList<>();
        this.visible = visible;

        if (visible) {
            view = new BattleView(this);
            new JEasyFrame(view, "battle");
        }
    }

    private void handleParameters(int[] params) {
        if (params[N_TICKS] != -1) {
            tickLimit = params[N_TICKS];
        }
        if (params[N_MISSILES] != -1) {
            nMissiles = params[N_MISSILES];
        }
        if (params[BULLET_INITIAL_VELOCITY] != -1) {
            bulletInitialVelocity = params[BULLET_INITIAL_VELOCITY];
        }
        if (params[BULLET_TIME_TO_LIVE] != -1) {
            bulletTimeToLive = params[BULLET_TIME_TO_LIVE];
        }
        if (params[N_ASTEROIDS] != -1) {
            nAsteroids = params[N_ASTEROIDS];
        }
        if (params[SHIP_MAX_SPEED] != -1) {
            maxSpeed = params[SHIP_MAX_SPEED];
        }
        if (params[SHIP_STEER_RATE] != -1) {
            steerRate = params[SHIP_STEER_RATE];
        }
        if (params[N_ASTEROID_CHILDREN] != -1) {
            nAsteroidChildren = params[N_ASTEROID_CHILDREN];
        }
        if(params[BULLET_KILL_SHIP] != -1){
            bulletKillShip = params[BULLET_KILL_SHIP] != 0;
        }
    }

    public int getTicks() {
        return currentTick;
    }

    public void playGame(BattleController player) {
        playGame(player, new NullRecorder());
    }

    public void playGame(BattleController player, DataRecorder recorder) {
        this.player = player;
        reset();

        makeAsteroids(nAsteroids);
        objects.add(ship);


        stats = new PlayerStats(0, 0);

        if (this.player instanceof KeyListener) {
            view.addKeyListener((KeyListener) this.player);
            view.setFocusable(true);
            view.requestFocus();
        }
        recorder.stepUpdate(this);
        while (!isGameOver()) {
            update();
            recorder.stepUpdate(this);
        }

        if (this.player instanceof KeyListener) {
            view.removeKeyListener((KeyListener) this.player);
        }
    }

    public void reset() {
        objects.clear();
        ship = buildShip(width / 2, height / 2);
        this.currentTick = 0;

        stats = new PlayerStats(0, 0);
    }

    private Ship buildShip(int x, int y) {
        Vector2d position = new Vector2d(x, y, true);
        Vector2d speed = new Vector2d(true);
        Vector2d direction = new Vector2d(1, 0, true);

        return new Ship(position, speed, direction, steerRate, maxSpeed);
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
        if (action.shoot) fireMissile(ship.getLocation(), ship.direction);

        wrap(ship);
        synchronized (_objects) {
            List<GameObject> spawned = new ArrayList<>();
            // here need to add the game objects ...
            List<GameObject> killList = new ArrayList<>();
            for (GameObject object : objects) {
                object.update();
                wrap(object);
                if (object.isDead()) {
                    killList.add(object);
                    if (object instanceof Asteroid) {
                        Asteroid asteroid = (Asteroid) object;
                        if (asteroid.getSize() < asteroidRadii.length - 1) {
                            // Make new Asteroids
                            for (int i = 0; i < nAsteroidChildren; i++) {
                                Vector2d newVelocity = new Vector2d(asteroid.velocity, true);
                                double rotationAmount = (Math.random() * 60) - 30;
                                newVelocity.rotate(Math.toRadians(rotationAmount));
                                spawned.add(new Asteroid(asteroid.location, newVelocity, asteroid.getSize() + 1));
                            }
                        }
                    }
                }
            }

            objects.removeAll(killList);
            objects.addAll(spawned);
        }
        currentTick++;

        if (visible) {
            view.repaint();
            sleep();
        }
    }


    public SimpleBattle clone() {
        SimpleBattle state = new SimpleBattle(false, null);
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
        if(!bulletKillShip){
            if(first instanceof BattleMissile && second instanceof Ship) return;
            if(first instanceof Ship && second instanceof BattleMissile) return;
        }
        if (overlap(first, second)) {
            if (!(first instanceof Ship || second instanceof Ship)) {
                stats.nPoints += 10;
            }
            first.hit();
            second.hit();
        }
    }

    private boolean overlap(GameObject first, GameObject second) {
        // otherwise do the default check
        double distance = first.getLocation().dist(second.getLocation());
        return distance < (first.radius() + second.radius());
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fireMissile(Vector2d location, Vector2d direction) {
        // need all the usual missile firing code here
        Ship currentShip = ship;
        if (stats.nMissiles < nMissiles) {
            BattleMissile missile = new BattleMissile(location, new Vector2d(0, 0, true), bulletTimeToLive);
            missile.getVelocity().add(direction, bulletInitialVelocity);
            // make it clear the ship
            missile.getVelocity().add(missile.getVelocity(), (currentShip.radius() + missileRadius) * 1.5 / missile.getVelocity().mag());
            objects.add(missile);
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
            for (GameObject gameObject : objects) {
                gameObject.draw(g);
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

    public double getHeuristic() {
        if (this.isGameOver()) {
            return -1000;
        }

        return getPoints();
    }

    public int getMissilesLeft() {
        return nMissiles - stats.nMissiles;
    }

    private void wrap(GameObject gameObject) {
        // only wrap objects which are wrappable
        if (gameObject.wrappable()) {
            gameObject.getLocation().x = (gameObject.getLocation().x + width) % width;
            gameObject.getLocation().y = (gameObject.getLocation().y + height) % height;
        }
    }

    public boolean isGameOver() {
        if (ship.isDead()) return true;
        // Is just the ship left?
        if (objects.size() == 1) {
            return true;
        }

        int asteroidCount = 0;
        for (GameObject object : objects) {
            asteroidCount += (object instanceof Asteroid) ? 1 : 0;
        }
        if (asteroidCount == 0) return true;


        return currentTick >= tickLimit;
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
            if (location.dist(centre) > safeRadius) {
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
