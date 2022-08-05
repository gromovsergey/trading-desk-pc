package com.foros.session.site;

import static com.foros.model.creative.CreativeSizeExpansion.DOWN;
import static com.foros.model.creative.CreativeSizeExpansion.DOWN_LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.DOWN_RIGHT;
import static com.foros.model.creative.CreativeSizeExpansion.LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.RIGHT;
import static com.foros.model.creative.CreativeSizeExpansion.UP;
import static com.foros.model.creative.CreativeSizeExpansion.UP_LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.UP_RIGHT;

import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.site.TagPosition;
import com.foros.util.NumberUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public  class TagPositionChooser {

    public static Set<TagPosition> getAvailablePositions(CreativeSize size) {
        Set<TagPosition> availablePositions = new LinkedHashSet<TagPosition>(Arrays.asList(TagPosition.NA));

        if (size == null || size.getMaxHeight() == null || size.getMaxWidth() == null) {
            return availablePositions;
        }

        Set<CreativeSizeExpansion> expansions = size.getExpansions();

        long height = NumberUtil.safeLong(size.getHeight());
        long width = NumberUtil.safeLong(size.getWidth());
        long maxHeight = NumberUtil.safeLong(size.getMaxHeight());
        long maxWidth = NumberUtil.safeLong(size.getMaxWidth());

        if (height == maxHeight && width < maxWidth) {
            if (expansions.contains(RIGHT)) {
                availablePositions.add(TagPosition.LEFT);
            }
            if (expansions.contains(LEFT)) {
                availablePositions.add(TagPosition.RIGHT);
            }
        }

        if (height < maxHeight && width == maxWidth) {
            if (expansions.contains(DOWN) ) {
                availablePositions.add(TagPosition.TOP);
            }
            if (expansions.contains(UP) && expansions.contains(DOWN)) {
                availablePositions.add(TagPosition.MIDDLE);
            }
            if (expansions.contains(UP)) {
                availablePositions.add(TagPosition.BOTTOM);
            }
        }

        if (height < maxHeight && width < maxWidth) {
            if (expansions.contains(UP_RIGHT)) {
                availablePositions.add(TagPosition.LOWER_LEFT);
            }
            if (expansions.contains(UP_LEFT)) {
                availablePositions.add(TagPosition.LOWER_RIGHT);
            }
            if (expansions.contains(DOWN_RIGHT)) {
                availablePositions.add(TagPosition.UPPER_LEFT);
            }
            if (expansions.contains(DOWN_LEFT)) {
                availablePositions.add(TagPosition.UPPER_RIGHT);
            }
        }

        return availablePositions;
    }

    public static Set<TagPosition> getByExpansions(Collection<CreativeSizeExpansion> expansions) {
        Set<TagPosition> positions = new LinkedHashSet<TagPosition>();

        for (CreativeSizeExpansion expansion : expansions) {
            switch (expansion) {
                case UP: {
                    positions.add(TagPosition.BOTTOM);
                    positions.add(TagPosition.MIDDLE);
                    break;
                }
                case LEFT: {
                    positions.add(TagPosition.RIGHT);
                    break;
                }
                case DOWN: {
                    positions.add(TagPosition.TOP);
                    positions.add(TagPosition.MIDDLE);
                    break;
                }
                case RIGHT: {
                    positions.add(TagPosition.LEFT);
                    break;
                }
                case DOWN_LEFT : {
                    positions.add(TagPosition.UPPER_RIGHT);
                    break;
                }
                case DOWN_RIGHT: {
                    positions.add(TagPosition.UPPER_LEFT);
                    break;
                }
                case UP_LEFT: {
                    positions.add(TagPosition.LOWER_RIGHT);
                    break;
                }
                case  UP_RIGHT: {
                    positions.add(TagPosition.LOWER_LEFT);
                }
            }
        }

        return positions;
    }
}
