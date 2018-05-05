package battle;

import java.awt.*;
import java.util.Random;

/**
 * Created by Simon M. Lucas
 * sml@essex.ac.uk
 * Date: 26/12/11
 * Time: 12:00
 */
public interface Constants {
    int width = 640;
    int height = 480;
    Dimension size = new Dimension(width, height);
    int safeRadius = height / 20;
    Color bg = Color.black;
    Font font = new Font("Courier", Font.PLAIN, 20);

    Color[] pColors = {Color.blue, Color.red};

    int delay = 20;
    double t = 1.0;
    Random rand = new Random();
    int[] asteroidRadii = {30, 20, 10};

    int missileRadius = 2;
    int missileTTL = 60;

    int N_TICKS = 0;
    int N_MISSILES = 1;
    int BULLET_INITIAL_VELOCITY = 2;
    int BULLET_TIME_TO_LIVE = 3;
    int N_ASTEROIDS = 4;
    int SHIP_MAX_SPEED = 5;
    int SHIP_STEER_RATE = 6;
    int N_PARAMS = 7;

}
