package com.foros.model.creative;

import com.foros.model.Flags;
import com.foros.model.IdNameEntity;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "SIZETYPE")
public class SizeType extends VersionEntityBase implements LocalizableNameEntity {

    @SequenceGenerator(name = "SizeTypeGen", sequenceName = "SIZETYPE_SIZE_TYPE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SizeTypeGen")
    @Column(name = "SIZE_TYPE_ID", nullable = false)
    @IdConstraint
    private Long id;

    @RequiredConstraint
    @StringSizeConstraint(size = 50)
    @Column(name = "NAME")
    private String defaultName;

    @Column(name = "FLAGS", nullable = false)
    @Type(type = "com.foros.persistence.hibernate.type.FlagsType")
    private Flags flags = Flags.ZERO;

    @StringSizeConstraint(size = 1024)
    @Column(name = "TAG_TEMPLATE_FILE")
    private String tagTemplateFile;

    @StringSizeConstraint(size = 1024)
    @Column(name = "TAG_TEMPL_IFRAME_FILE")
    private String tagTemplateIframeFile;

    @StringSizeConstraint(size = 1024)
    @Column(name = "TAG_TEMPL_BRPB_FILE")
    private String tagTemplateBrPbFile;

    @StringSizeConstraint(size = 1024)
    @Column(name = "TAG_TEMPL_IEST_FILE")
    private String tagTemplateIEstFile;

    @StringSizeConstraint(size = 1024)
    @Column(name = "TAG_TEMPL_PREVIEW_FILE")
    private String tagTemplatePreviewFile;

    @OneToMany(mappedBy="sizeType")
    private Set<CreativeSize> sizes = new LinkedHashSet<>();

    public SizeType() {
    }

    public SizeType(String defaultName) {
        this.defaultName = defaultName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        registerChange("id");
    }

    @Override
    public LocalizableName getName() {
        return LocalizableNameProvider.SIZE_TYPE.provide(defaultName, id);
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        this.registerChange("defaultName");
    }

    public String getTagTemplateFile() {
        return tagTemplateFile;
    }

    public void setTagTemplateFile(String tagTemplateFile) {
        this.tagTemplateFile = tagTemplateFile;
        registerChange("tagTemplateFile");
    }

    public String getTagTemplateIframeFile() {
        return tagTemplateIframeFile;
    }

    public void setTagTemplateIframeFile(String tagTemplateIframeFile) {
        this.tagTemplateIframeFile = tagTemplateIframeFile;
        registerChange("tagTemplateIframeFile");
    }

    public String getTagTemplateBrPbFile() {
        return tagTemplateBrPbFile;
    }

    public void setTagTemplateBrPbFile(String tagTemplateBrPbFile) {
        this.tagTemplateBrPbFile = tagTemplateBrPbFile;
        registerChange("tagTemplateBrPbFile");
    }

    public String getTagTemplateIEstFile() {
        return tagTemplateIEstFile;
    }

    public void setTagTemplateIEstFile(String tagTemplateIEstFile) {
        this.tagTemplateIEstFile = tagTemplateIEstFile;
        registerChange("tagTemplateIEstFile");
    }

    public String getTagTemplatePreviewFile() {
        return tagTemplatePreviewFile;
    }

    public void setTagTemplatePreviewFile(String tagTemplatePreviewFile) {
        this.tagTemplatePreviewFile = tagTemplatePreviewFile;
        registerChange("tagTemplatePreviewFile");
    }

    public MultipleSizes getMultipleSizes() {
        return MultipleSizes.valueOf(flags.get(MultipleSizes.MASK));
    }

    public boolean isMultiSize() {
        return MultipleSizes.valueOf(flags.get(MultipleSizes.MASK)) == MultipleSizes.MULTIPLE_SIZES;
    }

    public boolean isSingleSize() {
        return !isMultiSize();
    }

    public void setMultipleSizes(MultipleSizes multipleSizes) {
        flags = flags.set(MultipleSizes.MASK, MultipleSizes.valueOf(true) == multipleSizes);
        registerChange("flags");
    }

    public AdvertiserSizeSelection getAdvertiserSizeSelection() {
        return getAdvertiserSizeSelection(flags.get(AdvertiserSizeSelection.MASK));
    }

    public void setAdvertiserSizeSelection(AdvertiserSizeSelection advertiserSizeSelection) {
        flags = flags.set(AdvertiserSizeSelection.MASK, getAdvertiserSizeSelection(true) == advertiserSizeSelection);
        registerChange("flags");
    }

    private AdvertiserSizeSelection getAdvertiserSizeSelection(boolean flag) {
        return flag ? AdvertiserSizeSelection.TYPE_AND_SIZE_LEVEL : AdvertiserSizeSelection.TYPE_LEVEL;
    }

    public Set<CreativeSize> getSizes() {
        return new ChangesSupportSet<>(this, "sizes", sizes);
    }

    public void setSizes(Set<CreativeSize> sizes) {
        this.sizes = sizes;
        this.registerChange("sizes");
    }

    public enum MultipleSizes {
        ONE_SIZE,
        MULTIPLE_SIZES;

        static final int MASK = 0x1;

        public static MultipleSizes valueOf(boolean flag) {
            return flag ? MultipleSizes.MULTIPLE_SIZES : MultipleSizes.ONE_SIZE;
        }

        public static MultipleSizes valueOf(Flags flags) {
            return valueOf(flags.get(MultipleSizes.MASK));
        }

    }

    public enum AdvertiserSizeSelection {
        TYPE_LEVEL,
        TYPE_AND_SIZE_LEVEL;

        static final int MASK = 0x2;

        public static AdvertiserSizeSelection valueOf(boolean flag) {
            return flag ? AdvertiserSizeSelection.TYPE_AND_SIZE_LEVEL : AdvertiserSizeSelection.TYPE_LEVEL;
        }

        public static AdvertiserSizeSelection valueOf(Flags flags) {
            return valueOf(flags.get(AdvertiserSizeSelection.MASK));
        }
    }
}
