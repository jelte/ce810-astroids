package battle;

import asteroids.*;
import math.Vector2d;
import utilities.JEasyFrame;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.awt.*;
import java.util.List;


import static asteroids.Constants.*;

/**
 * Created by simon lucas on 10/06/15.
 * <p>
 * Aim here is to have a simple battle class
 * that enables ships to fish with each other
 * <p>
 * Might start off with just two ships, each with their own types of missile.
 */

public class SimpleBattle {

    // play a time limited game with a strict missile budget for
    // each player
    static int nMissiles = 100;
    static int nTicks = 1000;
    static int pointsPerKill = 10;
    static int releaseVelocity = 5;

    boolean visible = true;

    List<GameObject> objects;
    PlayerStats stats;

    private Ship s1;
    private BattleController p1;
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

    public int playGame(BattleController player) {
        this.p1 = player;
        reset();

        makeAsteroids(10);
        objects.add(s1);


        stats = new PlayerStats(0, 0);

        if (p1 instanceof KeyListener) {
            view.addKeyListener((KeyListener) p1);
            view.setFocusable(true);
            view.requestFocus();
        }

        while (!isGameOver()) {
            update();
        }

        if (p1 instanceof KeyListener) {
            view.removeKeyListener((KeyListener) p1);
        }

        return 0;
    }

    public void reset() {
        objects.clear();
        s1 = buildShip(width / 2, height / 2, 0);
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
        // get the actions from each player

        // apply them to each player's ship, taking actions as necessary
        Action a1 = p1.getAction(this.clone());
        update(a1);
    }

    public BattleController getP1() {
        return p1;
    }

    public void update(Action a1) {
        // now apply them to the ships
        s1.update(a1);

        // TODO This isn't going to work anymore
//        checkCollision(s1);

        for(int first = 0; first < objects.size() - 1; first++){
            for(int second = first + 1; second < objects.size(); second++){
                checkCollision(objects.get(first), objects.get(second));
            }
        }


        // and fire any missiles as necessary
        if (a1.shoot) fireMissile(s1.s, s1.d);

        wrap(s1);

        // here need to add the game objects ...
        List<GameObject> killList = new ArrayList<>();
        for (GameObject object : objects) {
            object.update();
            wrap(object);
            if (object.dead()) {
                killList.add(object);
            }
        }

        objects.removeAll(killList);
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

        state.s1 = s1.copy();
        return state;
    }

    protected List<GameObject> copyObjects() {
        List<GameObject> objectClone = new ArrayList<>();
        for (GameObject object : objects) {
            objectClone.add(object.copy());
        }

        return objectClone;
    }

    protected void checkCollision(GameObject first, GameObject second){
        if(first == second) return;
        if(first instanceof Asteroid && second instanceof Asteroid) return;
        if(first instanceof BattleMissile && second instanceof BattleMissile) return;
        if(overlap(first, second)) {
//            System.out.println(first.getClass().getSimpleName() + " hit " + second.getClass().getSimpleName());
            if(!(first instanceof Ship || second instanceof Ship)){
                stats.nPoints += 10;
            }
            first.hit();
            second.hit();
        }
    }

    private boolean overlap(GameObject first, GameObject second) {
        // otherwise do the default check
        double dist = first.s.dist(second.s);
        boolean ret = dist < (first.r() + second.r());
        return ret;
    }

    public void sleep() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fireMissile(Vector2d s, Vector2d d) {
        // need all the usual missile firing code here
        Ship currentShip = s1;
        if (stats.nMissiles < nMissiles) {
            Missile m = new Missile(s, new Vector2d(0, 0, true));
            m.v.add(d, releaseVelocity);
            // make it clear the ship
            m.s.add(m.v, (currentShip.r() + missileRadius) * 1.5 / m.v.mag());
            objects.add(m);
            // System.out.println("Fired: " + m);
            // sounds.fire();
            stats.nMissiles++;
        }
    }

    public void draw(Graphics2D g) {
        // for (Object ob : objects)
        if (s1 == null) {
            return;
        }

        // System.out.println("In draw(): " + n);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);

        for (GameObject go : objects) {
            go.draw(g);
        }

        if(!s1.dead()) {
            s1.draw(g);
        }
        if (p1 instanceof RenderableBattleController) {
            RenderableBattleController rbc = (RenderableBattleController) p1;
            rbc.render(g, s1.copy());
        }
    }

    public Ship getShip() {
        return s1.copy();
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
            ob.s.x = (ob.s.x + width) % width;
            ob.s.y = (ob.s.y + height) % height;
        }
    }

    public boolean isGameOver() {
        if(s1.dead()) return true;
        if (getMissilesLeft() >= 0 && getMissilesLeft() >= 0) {
            //ensure that there are no bullets left in play
            if (objects.isEmpty()) {
                return true;
            }
        }

        return currentTick >= nTicks;
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

}
