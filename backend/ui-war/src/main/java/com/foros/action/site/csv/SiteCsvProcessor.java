package com.foros.action.site.csv;

import static com.foros.action.site.TagHelper.preparePassback;
import static com.foros.action.site.csv.SiteFieldCsv.PUBLISHER_ID;
import static com.foros.action.site.csv.SiteFieldCsv.PUBLISHER_NAME;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_ID;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_NAME;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_STATUS;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_URL;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_ALLOW_EXPANDABLE;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_ID;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_NAME;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_PASSBACK;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_PASSBACK_TYPE;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_PRICING;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_SIZES;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_SIZE_TYPE;
import static com.foros.util.StringUtil.trimProperty;
import static com.foros.util.UploadUtils.setRowNumber;

import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.site.SiteUploadService;
import com.foros.util.StringUtil;
import com.foros.util.csv.BaseBulkHelper;
import com.foros.util.csv.CsvReader;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Parses and validates input csv file, returns a list of Sites as a result.
 */
public class SiteCsvProcessor {

    public static String ALL_SIZES = "ALL SIZES";

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final char DEFAULT_DELIMITER = ',';

//    private static final Pattern PRICING_PATTERN = Pattern.compile("DEFAULT=.+(;[A-Z]+=.+)*$");
    private static final Pattern PRICING_PATTERN = Pattern.compile("DEFAULT=.+(CPM|RS)(;CC=[A-Z]+,CT=(ALL|DISPLAY|TEXT),RT=(ALL|CPM|CPC|CPA),P=.+(CPM|RS))*$");
    private static final Pattern DEFAULT_PRICING_PATTERN = Pattern.compile("^((DEFAULT)=(.+)(CPM|RS))");
    private static final Pattern COUNTRY_PRICING_PATTERN = Pattern.compile("CC=([A-Z]+),CT=(ALL|DISPLAY|TEXT),RT=(ALL|CPM|CPC|CPA),P=(.+)?(CPM|RS)");

    private NumberFormat numberFormat;
    // Maximum of parsed rows, excluding header. if -1 then no limit
    private int maxRowSize;
    private UploadContext currentStatus;
    private Map<String, Site> sitesByName = new LinkedHashMap<String, Site>();
    private Map<Long, Site> sitesById = new LinkedHashMap<Long, Site>();
    private CsvReader csvReader;
    private Locale locale;
    private Set<Integer> allowedColumnsCount;
    private boolean isInternalMode;

    public SiteCsvProcessor(Locale locale, Integer maxRowSize, boolean isInternalMode) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale can't be null");
        } else {
            numberFormat = NumberFormat.getInstance(locale);
            this.locale = locale;
        }
        this.maxRowSize = maxRowSize;
        this.isInternalMode = isInternalMode;

        if (this.isInternalMode) {
            allowedColumnsCount = new HashSet<Integer>(Arrays.asList(
                SiteFieldCsv.INTERNAL_EXPORT_METADATA.getColumns().size(),
                SiteFieldCsv.INTERNAL_REVIEW_METADATA.getColumns().size()));
        } else {
            allowedColumnsCount = new HashSet<Integer>(Arrays.asList(
                SiteFieldCsv.EXTERNAL_EXPORT_METADATA.getColumns().size(),
                SiteFieldCsv.EXTERNAL_REVIEW_METADATA.getColumns().size()));
        }
    }

    /**
     * Parses input string for Site and tags definitions, put them into list and returns
     * <p/>
     * <b>Assumptions:</b>
     * <li>if SiteId is empty or null then site is expected to be new and will be created</li>
     * <li>if SiteId is not empty then corresponding site will be updated with provided values</li>
     * <li>if parsed row doesn't conatin any tag information, then tag will not be removed</li>
     *
     * @param inputStream input stream to parse objects from
     * @return a list of Site objects.
     * @throws SiteParserException e
     * @throws java.io.IOException e
     */
    public List<Site> parse(InputStream inputStream) throws IOException, SiteParserException {
        MessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);

        Reader reader = new InputStreamReader(inputStream, DEFAULT_CHARSET);
        List<Site> siteList = new ArrayList<Site>();

        csvReader = new CsvReader(reader, DEFAULT_DELIMITER);

        currentStatus = new UploadContext();
        // We assume that names are unique (basically it's true for single account)

        if (!csvReader.readHeaders()) {
            throw new SiteParserException("errors.invalid.header");
        } else {
            validateHeader(csvReader.getHeaders());
        }

        // following 1 changed to 0
        int rowNumber = 1;
        long curFakeId = -1;

        while (csvReader.readRecord()) {
            rowNumber++;

            currentStatus = new UploadContext();

            if (rowNumber > maxRowSize + 1 && maxRowSize != -1) {
                throw new SiteParserException("site.error.tooManyRows");
            }

            int columnCount = csvReader.getColumnCount();

            if (!allowedColumnsCount.contains(columnCount)) {
                currentStatus.addFatal("errors.invalid.rowFormat")
                    .withParameters(allowedColumnsCount, columnCount);
                Site site = new Site(curFakeId--);
                sitesById.put(site.getId(), site);

                setRowNumber(site, (long) rowNumber);
                assignOriginalValues(site);
                currentStatus.flush(interpolator);
                site.setProperty(SiteUploadService.UPLOAD_CONTEXT, currentStatus);

                if (isInternalMode) {
                    parseAccount(site);
                }

                //Some fields will be written from site and some - from tag, so we had to create a dummy tag too
                Tag tag = new Tag();
                tag.setSite(site);
                site.getTags().add(tag);
                setRowNumber(tag, (long)rowNumber);
                assignOriginalValues(tag);
                tag.setProperty(SiteUploadService.UPLOAD_CONTEXT, new UploadContext(UploadStatus.REJECTED));

                continue;
            }

            Site site = getSite();

            setRowNumber(site, (long)rowNumber);

            currentStatus.flush(interpolator);
            if (currentStatus.getStatus() == UploadStatus.REJECTED) {
                site.setProperty(SiteUploadService.UPLOAD_CONTEXT, currentStatus);
            }
            assignOriginalValues(site);

            currentStatus = new UploadContext();
            Tag tag = getTag(site);

            if (tag != null) {
                setRowNumber(tag, (long)rowNumber);
                currentStatus.flush(interpolator);
                if (currentStatus.getStatus() == UploadStatus.REJECTED) {
                    tag.setProperty(SiteUploadService.UPLOAD_CONTEXT, currentStatus);
                }
                assignOriginalValues(tag);
                site.getTags().add(tag);
            }
        }

        siteList.addAll(sitesByName.values());
        siteList.addAll(sitesById.values());

        return siteList;
    }

    private void parseAccount(Site site) throws IOException {
        PublisherAccount publisherAccount = new PublisherAccount();
        publisherAccount.setId(readLong(PUBLISHER_ID));
        publisherAccount.setName(readString(PUBLISHER_NAME));
        site.setAccount(publisherAccount);
    }

    private void validateHeader(String[] headers) throws SiteParserException {
        if (headers == null || !allowedColumnsCount.contains(headers.length)) {
            throw new SiteParserException("errors.invalid.header");
        }
    }

    private Tag getTag(Site site) throws IOException {
        Long tagId = readLong(TAG_ID);

        if (tagId != null || StringUtil.isPropertyNotEmpty(readString(TAG_NAME))) {
            // good to parse tag data
            for (Tag tag : site.getTags()) {
                if (tag.getId() != null && tag.getId().equals(tagId)) {
                    // tag with current id already exists
                    addError(TAG_ID, "errors.duplicated.siteTag.pair");
                    tagId = null;
                    break;
                }
            }

            Tag newTag = new Tag(tagId);

            newTag.setName(readString(TAG_NAME));
            newTag.setStatus(Status.ACTIVE);
            Collection<CreativeSize> sizes = new TreeSet<>(CreativeSize.BY_PROTOCOL_COMPARATOR);
            sizes.addAll(getTagSizes());

            CreativeSize allSizesMarker = new CreativeSize();
            allSizesMarker.setProtocolName(SiteCsvProcessor.ALL_SIZES);

            if (sizes.contains(allSizesMarker)) {
                newTag.setAllSizesFlag(true);
            } else {
                newTag.getSizes().addAll(sizes); // Creative size will be fetched with exisings
                newTag.setAllSizesFlag(false);
            }
            newTag.setTagPricings(getTagPricings(newTag));
            newTag.setSite(site);
            newTag.setSizeType(new SizeType(trimProperty(readString(TAG_SIZE_TYPE))));
            newTag.setAllowExpandable(getAllowExpandable());

            PassbackType passbackType = readPassbackType();
            if (passbackType != null) {
                newTag.setPassbackType(passbackType);
                String passBack = getTagPassback();
                if (passbackType == PassbackType.HTML_URL) {
                    newTag.setPassback(passBack);
                } else {
                    newTag.setPassbackHtml(passBack);
                    preparePassback(newTag); // registerChange("passbackHtml") if value is changed
                }
            }

            return newTag;
        }

        return null;
    }

    private PassbackType readPassbackType() throws IOException {
        String pbtString = readString(TAG_PASSBACK_TYPE);

        if (pbtString != null) {
            try {
                return PassbackType.valueOf(pbtString);
            } catch (IllegalArgumentException e) {
            }
        }

        addError(TAG_PASSBACK_TYPE, "errors.field.invalid");
        return null;

    }

    private Site getSite() throws IOException {
        Site site;

        Long siteId = readLong(SITE_ID);
        String siteName = readString(SITE_NAME);

        if (siteId != null) {
            site = new Site(siteId);
            moveTags(site, sitesById.get(siteId));
            sitesById.put(siteId, site);
        } else { // Site id is null, hence this site is assumed to be created.
            site = new Site(null, siteName);
            moveTags(site, sitesByName.get(siteName));
            sitesByName.put(siteName, site);
        }

        // make use of latest values
        site.setSiteUrl(readString(SITE_URL, false));
        site.setName(siteName);

        if (site.getId() != null) {
            // site is being updated
            String status = trimProperty(readString(SITE_STATUS));
            if (StringUtil.isPropertyEmpty(status)) {
                site.setStatus(Status.ACTIVE);
            } else {
                try {
                    site.setStatus(BaseBulkHelper.parseStatus(status));
                } catch (IllegalArgumentException e) {
                    addError(SITE_STATUS, "errors.site.status.invalid").withParameters(status);
                }
            }
        } else {
            // new site is being created
            site.setStatus(Status.ACTIVE);
        }

        if (isInternalMode) {
            parseAccount(site);
        }

        return site;
    }

    private static void moveTags(Site site, Site old) {
        if (old != null) {
            Set<Tag> tags = old.getTags();
            for (Tag tag : tags) {
                tag.setSite(site);
            }
            site.getTags().addAll(tags);
        }
    }

    private String getTagPassback() throws IOException {
        return readString(TAG_PASSBACK);
    }

    private List<TagPricing> getTagPricings(Tag tag) throws IOException {
        String pricing = readString(TAG_PRICING);

        if (StringUtil.isPropertyEmpty(pricing)) {
            return new LinkedList<TagPricing>();
        } else if (!PRICING_PATTERN.matcher(pricing.toUpperCase()).matches()) {
            addError(TAG_PRICING, "errors.tagPricing.format");
            return new LinkedList<TagPricing>();
        }

        String[] countryPricings = pricing.split(";");
        List<TagPricing> tagPricings = new ArrayList<TagPricing>(countryPricings.length);

        for (String countryPricing : countryPricings) {
            Matcher defaultMatcher = DEFAULT_PRICING_PATTERN.matcher(countryPricing.toUpperCase());
            Matcher matcher = COUNTRY_PRICING_PATTERN.matcher(countryPricing.toUpperCase());

            if (defaultMatcher.matches() && defaultMatcher.groupCount() == 4) {
                TagPricing tagPricing = new TagPricing();
                tagPricing.setCountry(null);
                tagPricing.setCcgType(null);
                tagPricing.setCcgRateType(null);

                BigDecimal rateNumber = parseRateNumber(defaultMatcher.group(3));
                String siteRateTypeString = defaultMatcher.group(4);
                SiteRate rate = parseRate(rateNumber, siteRateTypeString);
                if (rate == null) {
                    continue;
                }

                tagPricing.setSiteRate(rate);
                rate.setTagPricing(tagPricing);

                tagPricing.setTags(tag);
                tagPricings.add(tagPricing);
            } else if (matcher.matches() && matcher.groupCount() == 5) {

                TagPricing tagPricing = new TagPricing();
                String countryString = matcher.group(1);
                if ("ALL".equals(countryString.toUpperCase())) {
                    tagPricing.setCountry(null);
                } else {

                    Country country = new Country(countryString);
                    tagPricing.setCountry(country);
                }
                String ccgTypeString = matcher.group(2);
                String ccgRateTypeString = matcher.group(3);

                BigDecimal rateNumber = parseRateNumber(matcher.group(4));
                String siteRateTypeString = matcher.group(5);
                SiteRate rate = parseRate(rateNumber, siteRateTypeString);
                if (rate == null) {
                    continue;
                }

                tagPricing.setSiteRate(rate);

                if (ccgRateTypeString.equals("ALL")) {
                    tagPricing.setCcgRateType(null);
                } else {
                    try {
                        tagPricing.setCcgRateType(RateType.valueOf(ccgRateTypeString));
                    } catch (IllegalArgumentException e) {
                        addError(TAG_PRICING, "errors.invalid.country.price.format");
                        continue;
                    }
                }

                if (ccgTypeString.equals("ALL")) {
                    tagPricing.setCcgRateType(null);
                } else {
                    try {
                        tagPricing.setCcgType(CCGType.valueOf(ccgTypeString));
                    } catch (IllegalArgumentException e) {
                        addError(TAG_PRICING, "errors.invalid.country.price.format");
                        continue;
                    }
                }

                for (TagPricing tp: tagPricings) {
                    if (new EqualsBuilder().append(tp.getCcgType(), tagPricing.getCcgType())
                            .append(tp.getCcgRateType(), tagPricing.getCcgRateType())
                            .append(tp.getCountry(), tagPricing.getCountry())
                            .isEquals()) {
                        addError(TAG_PRICING, "errors.duplicated.tagPricing");
                        break;
                    }
                }

                tagPricing.setTags(tag);
                tagPricings.add(tagPricing);
            } else {
                addError(TAG_PRICING, "errors.invalid.country.price.format");
            }
        }
        return new LinkedList<TagPricing>(tagPricings);
    }

    private SiteRate parseRate(BigDecimal rateNumber, String siteRateTypeString) {
        SiteRate rate = new SiteRate();
        try {
            rate.setRateType(SiteRateType.valueOf(siteRateTypeString));
        } catch (IllegalArgumentException e) {
            addError(TAG_PRICING, "errors.invalid.country.price.format");
            return null;
        }

        switch (rate.getRateType()) {
        case RS:
            rate.setRatePercent(rateNumber);
            break;
        default:
            rate.setRate(rateNumber);
            break;
        }

        return rate;
    }

    private BigDecimal parseRateNumber(String cpm) {
        ParsePosition pos = new ParsePosition(0);
        Number n = numberFormat.parse(cpm, pos);

        if (pos.getIndex() != cpm.length() || !checkRate(n)) {
            addError(TAG_PRICING, "errors.cost");
            return null;
        } else {
            return new BigDecimal(n.toString());
        }
    }

    private Collection<CreativeSize> getTagSizes() throws IOException {
        String tagSizes = trimProperty(readString(TAG_SIZES, true));
        Collection<CreativeSize> creativeSizes = new ArrayList<>();
        if (StringUtil.isPropertyNotEmpty(tagSizes)) {
            String[] sizes = tagSizes.split(",");
            for (String name : sizes) {
                CreativeSize size = new CreativeSize();
                size.setProtocolName(name);
                creativeSizes.add(size);
            }
        }
        return creativeSizes;
    }

    private boolean checkRate(Number n) {
        BigDecimal d = BigDecimal.valueOf(n.doubleValue());
        return d.compareTo(BigDecimal.valueOf(0)) >= 0 && !(d.precision() - d.scale() > 7 || d.scale() > 2);
    }

    private Long readLong(SiteFieldCsv column, String messageKey) throws IOException {
        String str = readString(column);
        try {
            return StringUtil.isPropertyEmpty(str) ? null : Long.parseLong(str);
        } catch (Exception e) {
            addError(column, messageKey);
            return null;
        }
    }

    private Long readLong(SiteFieldCsv column) throws IOException {
        return readLong(column, "errors.field.integer");
    }

    private String readString(SiteFieldCsv column, boolean required) throws IOException {
        String str = StringUtil.trimProperty(csvReader.get(isInternalMode ? column.ordinal() : column.ordinal() - SiteFieldCsv.SHIFT));
        if (StringUtil.isPropertyEmpty(str) && required) {
            addError(column, "errors.field.required");
        }
        return "".equals(str) ? null : str;
    }

    private String readString(SiteFieldCsv column) throws IOException {
        return readString(column, false);
    }

    private ConstraintViolationBuilder addError(SiteFieldCsv field, String msg) {
        return currentStatus.addError(msg).withPath(field.getFieldPath());
    }

    private void assignOriginalValues(EntityBase entity) throws IOException {
        String[] record = new String[csvReader.getHeaders().length];
        List<SiteFieldCsv> columns;
        if (isInternalMode) {
            columns = SiteFieldCsv.INTERNAL_REVIEW_METADATA.getColumns();
        } else {
            columns = SiteFieldCsv.EXTERNAL_REVIEW_METADATA.getColumns();
        }

        for (SiteFieldCsv column : columns) {
            int index = isInternalMode ? column.ordinal() : column.ordinal() - SiteFieldCsv.SHIFT;
            if (csvReader.getColumnCount() > index && record.length > index) {
                record[index] = csvReader.get(index);
            }
        }
        entity.setProperty(SiteUploadService.ORIGINAL_VALUES, record);
    }

    public boolean getAllowExpandable() throws IOException {
        String result = readString(SiteFieldCsv.TAG_ALLOW_EXPANDABLE, true);
        if (result == null) {
            return false;
        }
        if ("y".equalsIgnoreCase(result.toLowerCase())) {
            return true;
        } else if ("n".equalsIgnoreCase(result)) {
            return false;
        } else {
            addError(TAG_ALLOW_EXPANDABLE, "tag.error.allowExpandable");
            return false;
        }
    }
}
