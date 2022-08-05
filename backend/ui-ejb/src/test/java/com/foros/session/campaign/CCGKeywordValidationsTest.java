package com.foros.session.campaign;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;

import com.foros.AbstractValidationsTest;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.session.UploadStatus;
import com.foros.test.factory.TextCCGTestFactory;

import group.Db;
import group.Validation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class CCGKeywordValidationsTest extends AbstractValidationsTest {
    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    private CCGKeywordService ccgKeywordService;

    private CampaignCreativeGroup ccg;
    private String validationName;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        ccg = textCCGTF.create();
        ccg.setTgtType(TGTType.KEYWORD);
        textCCGTF.persist(ccg);
    }

    @Test
    public void testValidateCreateEmpty() throws Exception {
        CCGKeyword keyword = new CCGKeyword();
        validate("CCGKeyword.createOrUpdateAll", Arrays.asList(keyword), ccg.getId());
        assertHasViolation("ccgKeywords[0].originalKeyword");
    }

    @Test
    public void testValidateCreateAll() throws Exception {
        validationName = "CCGKeyword.createOrUpdateAll";
        validateOriginalKeyword();
        validateBid();
        validateClickURL();
    }

    @Test
    public void testValidateCreate() throws Exception {
        validationName = "CCGKeyword.createOrUpdate";
        validateOriginalKeyword();
        validateBid();
        validateClickURL();
    }

    @Test
    public void testValidateUpdateAll() throws Exception {
        ccgKeywordService.update(Collections.singleton(createKeyword()), ccg.getId(), ccg.getVersion());
        commitChanges();

        validationName = "CCGKeyword.createOrUpdateAll";
        validateOriginalKeyword();
        validateBid();
        validateClickURL();
    }


    @Test
    public void testValidateAll() throws Exception {
        // inconsistent tgtType
        CCGKeyword keyword = textCCGTF.createCcgKeyword("keyword");
        keyword.setCreativeGroup(ccg);
        ccgKeywordService.validateAll(ccg, Collections.singleton(keyword), TGTType.CHANNEL);
        assertEquals(UploadStatus.REJECTED, keyword.getProperty(UPLOAD_CONTEXT).getStatus());
    }

    @Test
    public void testValidateUpdate() throws Exception {
        ccgKeywordService.update(Collections.singleton(createKeyword()), ccg.getId(), ccg.getVersion());
        commitChanges();

        validationName = "CCGKeyword.createOrUpdate";
        validateOriginalKeyword();
        validateBid();
        validateClickURL();
    }

    @Test
    public void testValidateExactMatch() throws Exception {
        validationName = "CCGKeyword.createOrUpdate";

        CCGKeyword keyword = createKeyword(KeywordTriggerType.SEARCH_KEYWORD);
        keyword.setOriginalKeyword("[originalKeyword]");
        testField(keyword, "originalKeyword", 0);

        keyword = createKeyword(KeywordTriggerType.PAGE_KEYWORD);
        keyword.setOriginalKeyword("[originalKeyword]");
        testField(keyword, "originalKeyword", 1);

        keyword = createKeyword(KeywordTriggerType.PAGE_KEYWORD);
        testField(keyword, "originalKeyword", 0);
    }

    private void validateOriginalKeyword() {
        CCGKeyword keyword = createKeyword();
        testField(keyword, "originalKeyword", 0);

        keyword = createKeyword();
        keyword.setOriginalKeyword(null);
        testField(keyword, "originalKeyword", 1);

        keyword = createKeyword();
        keyword.setOriginalKeyword("test \n test");
        testField(keyword, "originalKeyword", 1);

        keyword = createKeyword();
        keyword.setOriginalKeyword(StringUtils.repeat("a", 513));
        testField(keyword, "originalKeyword", 1);
    }

    private void validateBid() {
        CCGKeyword keyword = createKeyword();
        keyword.setMaxCpcBid(null);
        testField(keyword, "maxCpcBid", 0);

        keyword = createKeyword();
        keyword.setMaxCpcBid(new BigDecimal("-10"));
        testField(keyword, "maxCpcBid", 1);

        keyword = createKeyword();
        keyword.setMaxCpcBid(new BigDecimal("10000000"));
        testField(keyword, "maxCpcBid", 1);

        keyword = createKeyword();
        keyword.setMaxCpcBid(new BigDecimal("10.999"));
        testField(keyword, "maxCpcBid", 1);

        keyword = createKeyword();
        keyword.setMaxCpcBid(new BigDecimal("-10000000.999"));
        testField(keyword, "maxCpcBid", 2);
    }

    private void validateClickURL() {
        CCGKeyword keyword = createKeyword();
        keyword.setClickURL(null);
        testField(keyword, "clickURL", 0);

        keyword = createKeyword();
        keyword.setClickURL("badUrl");
        testField(keyword, "clickURL", 1);
    }

    private void testField(CCGKeyword keyword, String path, int errors) {
        if ("CCGKeyword.createOrUpdateAll".equals(validationName)) {
            validate(validationName, Arrays.asList(keyword), ccg.getId());
        } else {
            validate(validationName, keyword, ccg, ccg.getTgtType());
        }

        if (errors != 0) {
            path = "CCGKeyword.createOrUpdateAll".equals(validationName) ? "ccgKeywords[0]." + path : path;
            assertHasViolation(path);
        }
        assertViolationsCount(errors);
    }

    private CCGKeyword createKeyword() {
        CCGKeyword keyword = textCCGTF.createCcgKeyword("keyword");
        keyword.setCreativeGroup(new CampaignCreativeGroup(ccg.getId()));
        return keyword;
    }

    private CCGKeyword createKeyword(KeywordTriggerType triggerType) {
        CCGKeyword keyword = textCCGTF.createCcgKeyword("keyword", triggerType);
        keyword.setCreativeGroup(new CampaignCreativeGroup(ccg.getId()));
        return keyword;
    }
}
