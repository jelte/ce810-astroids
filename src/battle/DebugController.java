package battle;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Draw debug stuff from the center of the ship rather than top left of the world.
 */
public abstract class DebugController implements RenderableBattleController {

    @Override
    public void render(Graphics2D graphics2D, Ship ship) {
        AffineTransform at = graphics2D.getTransform();
        graphics2D.translate(ship.location.x, ship.location.y);
        double rot = Math.atan2(ship.direction.y, ship.direction.x) + Math.PI / 2;
        graphics2D.rotate(rot);
        render(graphics2D);
        graphics2D.setTransform(at);
    }

    public abstract void render(Graphics2D g);

}
