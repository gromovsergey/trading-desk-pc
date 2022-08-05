package app.programmatic.ui.conversion.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.tool.foros.StatusHelper;

public class Conversion {
    private com.foros.rs.client.model.advertising.conversion.Conversion conversion;
    private MajorDisplayStatus displayStatus;
    private String pixelCode;

    public Conversion(com.foros.rs.client.model.advertising.conversion.Conversion conversion) {
        this.conversion = conversion;
        this.displayStatus = StatusHelper.getMajorStatusByRsStatus(conversion.getStatus());
    }

    public Conversion() {
    }

    public com.foros.rs.client.model.advertising.conversion.Conversion getConversion() {
        return conversion;
    }

    public void setConversion(com.foros.rs.client.model.advertising.conversion.Conversion conversion) {
        this.conversion = conversion;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getPixelCode() {
        return pixelCode;
    }

    public void setPixelCode(String pixelCode) {
        this.pixelCode = pixelCode;
    }
}
