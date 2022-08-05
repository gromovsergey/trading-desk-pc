package com.foros.action.admin.creativeSize;

import static com.foros.model.creative.CreativeSizeExpansion.DOWN;
import static com.foros.model.creative.CreativeSizeExpansion.DOWN_LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.DOWN_RIGHT;
import static com.foros.model.creative.CreativeSizeExpansion.LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.RIGHT;
import static com.foros.model.creative.CreativeSizeExpansion.UP;
import static com.foros.model.creative.CreativeSizeExpansion.UP_LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.UP_RIGHT;
import com.foros.action.BaseActionSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.creative.SizeType;
import com.foros.model.template.Option;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.SizeTypeService;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;

public class CreativeSizeModelSupport extends BaseActionSupport implements ModelDriven<CreativeSize> {
    @EJB
    protected CreativeSizeService service;

    @EJB
    protected SizeTypeService sizeTypeService;

    protected Set<CreativeSizeExpansion> availableExpansions;

    protected CreativeSize entity = new CreativeSize();

    private List<SizeType> sizeTypes;

    public Collection<Option> getSelectedOptions() {
        return entity.getAllOptions();
    }

    public CreativeSize getModel() {
        return entity;
    }

    public Set<CreativeSizeExpansion> getAvailableExpansions() {
        if (availableExpansions == null) {
            availableExpansions = new LinkedHashSet<CreativeSizeExpansion>();

            if (entity.getHeight() == null || entity.getMaxHeight() == null || entity.getWidth() == null || entity.getMaxWidth() == null ) {
                return availableExpansions;
            }

            long height = entity.getHeight();
            long width = entity.getWidth();
            long maxHeight = entity.getMaxHeight();
            long maxWidth = entity.getMaxWidth();

            boolean expandableWidth = width < maxWidth;
            boolean expandableHeight = height < maxHeight;
            boolean fullyExpandable = expandableWidth && expandableHeight;
            if (expandableWidth) {
                availableExpansions.add(LEFT);
                availableExpansions.add(RIGHT);
            }
            if (expandableHeight) {
                availableExpansions.add(UP);
                availableExpansions.add(DOWN);
            }
            if (fullyExpandable) {
                availableExpansions.add(DOWN_LEFT);
                availableExpansions.add(DOWN_RIGHT);
                availableExpansions.add(UP_LEFT);
                availableExpansions.add(UP_RIGHT);
            }
        }
        return availableExpansions;
    }

    public Set<CreativeSizeExpansion> getSelectedExpansions() {
        return entity.getExpansions();
    }

    public void setSelectedExpansions(Set<CreativeSizeExpansion> selectedExpansions) {
        entity.setExpansions(selectedExpansions);
    }

    public List<SizeType> getSizeTypes() {
        if (sizeTypes == null) {
            sizeTypes = sizeTypeService.findAll();
        }
        return  sizeTypes;
    }
}
