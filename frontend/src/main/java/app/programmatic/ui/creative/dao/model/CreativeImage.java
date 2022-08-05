package app.programmatic.ui.creative.dao.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "CreativeImage"
)
public class CreativeImage {
    private String name;
    private String path;
    private Dimensions dimensions;

    public CreativeImage() {
    }

    public CreativeImage(String name, String path, Dimensions dimensions) {
        this.name = name;
        this.path = path;
        this.dimensions = dimensions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "Dimensions"
    )
    public static class Dimensions {
        private Long width;
        private Long height;

        public Dimensions() {
        }

        public Dimensions(Long width, Long height) {
            this.width = width;
            this.height = height;
        }

        public Long getWidth() {
            return width;
        }

        public Long getHeight() {
            return height;
        }
    }
}
