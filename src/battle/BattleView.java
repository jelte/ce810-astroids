package battle;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

import static battle.Constants.*;
import static java.awt.Color.black;


public class BattleView extends JComponent {
    static int offset = 0;
    int scale;
    // static int carSize = 5;
    static Color bg = black;
    SimpleBattle game;
    // Font font;

    Ship ship;

    static double viewScale = 1.0;


    public BattleView(SimpleBattle game) {
        this.game = game;
        scale = size.width - 2 * offset;

    }

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        AffineTransform at = g.getTransform();
        g.translate((1 - viewScale) * width / 2, (1-viewScale)*height / 2);

        // this was an experiment to turn it into a side-scroller
        // but it produces a weird moving screen effect
        // needs more logic in the drawing process
        // to wrap the asteroids that have been projected off the screen
        // g.translate(-(game.ship.s.x - width/2), 0);

        g.scale(viewScale, viewScale);

        game.draw(g);
        g.setTransform(at);
        paintState(g);
    }


    public void paintState(Graphics2D g) {
//
//        for (GameObject object : game.getObjects()) {
//            object.draw(g);
//        }

        g.setColor(Color.white);
        g.setFont(font);

        SimpleBattle.PlayerStats p1Stats = game.getStats();
        String strScores    = "Score:    " + p1Stats.getPoints();
        String strMissiles  = "Missiles: " + p1Stats.getMissilesFired();
        String strTicks     = "Ticks:    " + game.getTicks();
        String p1 = "P1 Green " + game.getPlayer().getClass().getSimpleName();
        g.drawString(strScores, 10, 20);
        g.drawString(strMissiles, 10, 50);
        g.drawString(strTicks, 10, 80);
        g.drawString(p1, 10, 110);
    }


    public Dimension getPreferredSize() {
        return size;
    }


}
