package com.foros.session.creative;

import com.foros.model.Flags;
import com.foros.model.LocalizableName;
import com.foros.session.NamedTO;
import com.foros.util.i18n.LocalizableNameProvider;

import static com.foros.model.creative.SizeType.AdvertiserSizeSelection;
import static com.foros.model.creative.SizeType.MultipleSizes;

public class SizeTypeTO extends NamedTO {

    private MultipleSizes multipleSizes;
    private AdvertiserSizeSelection advertiserSizeSelection;

    public SizeTypeTO(Long id, String name, Flags flags) {
        this(id, name, MultipleSizes.valueOf(flags), AdvertiserSizeSelection.valueOf(flags));
    }

    public SizeTypeTO(Long id, String name, MultipleSizes multipleSizes, AdvertiserSizeSelection advertiserSizeSelection) {
        super(id, name);
        this.multipleSizes = multipleSizes;
        this.advertiserSizeSelection = advertiserSizeSelection;
    }

    public LocalizableName getLocalizableName() {
        return LocalizableNameProvider.SIZE_TYPE.provide(getName(), getId());
    }

    public MultipleSizes getMultipleSizes() {
        return multipleSizes;
    }

    public AdvertiserSizeSelection getAdvertiserSizeSelection() {
        return advertiserSizeSelection;
    }

    public boolean isAllowMultiSize() {
        return multipleSizes == MultipleSizes.MULTIPLE_SIZES;
    }
}
