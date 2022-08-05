package com.foros.framework.tiles;

import javax.servlet.ServletContext;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.servlet.wildcard.WildcardServletTilesApplicationContext;
import org.apache.tiles.startup.AbstractTilesInitializer;

public class ForosTilesInitializer extends AbstractTilesInitializer {

    @Override
    protected TilesApplicationContext createTilesApplicationContext(
            TilesApplicationContext preliminaryContext) {
        return new WildcardServletTilesApplicationContext(
                (ServletContext) preliminaryContext.getContext());
    }

    @Override
    protected AbstractTilesContainerFactory createContainerFactory(
            TilesApplicationContext context) {
        return new ForosTilesContainerFactory();
    }

}
