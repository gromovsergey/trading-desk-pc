package com.foros.framework.tiles;

import org.apache.tiles.startup.TilesInitializer;
import org.apache.tiles.web.startup.AbstractTilesListener;

public class ForosTilesListener extends AbstractTilesListener {
    @Override
    protected TilesInitializer createTilesInitializer() {
        return new ForosTilesInitializer();
    }
}
