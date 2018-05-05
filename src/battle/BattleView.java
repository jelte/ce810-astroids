package battle;


import javax.swing.*;
import java.awt.*;

import static battle.Constants.font;
import static battle.Constants.size;


public class BattleView extends JComponent {
    private SimpleBattle game;

    public BattleView(SimpleBattle game) {
        this.game = game;
    }

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        game.draw(g);
        paintState(g);
    }


    private void paintState(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(font);

        SimpleBattle.PlayerStats p1Stats = game.getStats();
        String strScores = "Score:    " + p1Stats.getPoints();
        String strMissiles = "Missiles: " + p1Stats.getMissilesFired();
        String strTicks = "Ticks:    " + game.getTicks();
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
