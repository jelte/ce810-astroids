package com.fossgalaxy.games.asteroids.battle;

import java.awt.*;

/**
 * Created by jwalto on 12/06/2015.
 */
public interface RenderableBattleController extends BattleController {

    public void render(Graphics2D g, Ship s);
}
