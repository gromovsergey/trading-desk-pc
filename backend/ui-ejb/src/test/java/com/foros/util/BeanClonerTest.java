package com.foros.util;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.Status;
import com.foros.model.campaign.*;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.creative.Creative;
import com.foros.util.copy.BeanCloner;
import com.foros.util.copy.SerializeCloner;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

@Category( Unit.class )
public class BeanClonerTest {
    @Test
    @SuppressWarnings("unchecked")
    public void beanClone() throws Exception {
        FooClassA sourceRoot = new FooClassA();
        FooClassB child = new FooClassB();
        child.setId(7L);
        child.setExcludedNumber(777L);
        child.setBackFooA(sourceRoot);
        sourceRoot.setB(child);

        List<FooClassB> foos = new ArrayList<FooClassB>();
        child.map.put(sourceRoot, foos);
        foos.add(child);
        sourceRoot.setFoos(foos);
        sourceRoot.setShallowFoos(foos);

        FooClassA clone = (FooClassA)BeanCloner.clone(sourceRoot, new CopyFilter());

        //check user cloner
        assertEquals(FooClassB.USER_CLONER_TEST, clone.getSerializeB().getA());
        //check standard clone
        assertEquals(FooClassB.USER_CLONE_TEST, clone.getStandardCloneB().getB());
        //check deep
        assertNotSame(sourceRoot.getB(), clone.getB());
        foos.get(0).setA("ZZZ");
        assertNotSame("ZZZ", ((List<FooClassB>)clone.getFoos()).get(0).getA());
        assertTrue(clone.getB().map.containsKey(clone));
        List<FooClassB> mapFoos = (List<FooClassB>)clone.getB().map.get(clone);
        mapFoos.get(0).setA("QQQ");
        assertNotSame(foos.get(0).getA(), mapFoos.get(0).getA());

        //check shallow copy
        assertEquals(sourceRoot.getShallowFoos(), clone.getShallowFoos());
        List<FooClassB> shalowFoos = (List<FooClassB>)sourceRoot.getShallowFoos();
        shalowFoos.get(0).setA("ZZZ");
        assertEquals("ZZZ", ((List<FooClassB>)clone.getShallowFoos()).get(0).getA());

        //check back ref
        assertEquals(clone, clone.getB().getBackFooA());

        //check set null
        assertEquals(null, clone.getB().getId());
        assertEquals(sourceRoot.getB().getId(), new Long(7l));

        //check excluded 
        assertEquals(clone.getB().getExcludedNumber(), null);
        assertEquals(sourceRoot.getB().getExcludedNumber(), new Long(777l));

        //check type annotation
        assertEquals(LinkedList.class, clone.getFoos().getClass());
    }

    @Test
    public void copyActiveCampaign() {
        Campaign campaign = getCampaign(Status.ACTIVE);

        Set<CampaignCreativeGroup> ccgCollection = new LinkedHashSet<CampaignCreativeGroup>();
        campaign.setCreativeGroups(ccgCollection);

        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupText = getCampaignCreativeGroup(Status.ACTIVE, CCGType.TEXT);
        ccgCollection.add(campaignCreativeGroupText);

        // Set CCGKeyword collection
        Set<CCGKeyword> cckKeywordCollection = new LinkedHashSet<CCGKeyword>();
        campaignCreativeGroupText.setCcgKeywords(cckKeywordCollection);

        cckKeywordCollection.add(getCCGKeyWord(Status.ACTIVE));
        cckKeywordCollection.add(getCCGKeyWord(Status.DELETED));

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupText.setCampaignCreatives(campaignCreativeCollection);

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupText);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupText);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        Campaign cloneCampaign = (Campaign)BeanCloner.clone(campaign, new CopyFilter());

        assertNotNull(cloneCampaign);
        assertTrue(cloneCampaign.getCreativeGroups().size() == 1);
    }

    @Test
    public void copyDeletedCampaign() {
        Campaign campaign = getCampaign(Status.DELETED);

        Set<CampaignCreativeGroup> ccgCollection = new LinkedHashSet<CampaignCreativeGroup>();
        campaign.setCreativeGroups(ccgCollection);

        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupText = getCampaignCreativeGroup(Status.ACTIVE, CCGType.TEXT);
        ccgCollection.add(campaignCreativeGroupText);

        // Set CCGKeyword collection
        Set<CCGKeyword> cckKeywordCollection = new LinkedHashSet<CCGKeyword>();
        campaignCreativeGroupText.setCcgKeywords(cckKeywordCollection);

        cckKeywordCollection.add(getCCGKeyWord(Status.ACTIVE));
        cckKeywordCollection.add(getCCGKeyWord(Status.DELETED));

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupText.setCampaignCreatives(campaignCreativeCollection);

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupText);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupText);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        Campaign cloneCampaign = (Campaign)BeanCloner.clone(campaign, new CopyFilter());

        assertNull(cloneCampaign);
    }

    @Test
    public void copyCampaignWithActiveCCGDisplay() {
        Campaign campaign = getCampaign(Status.ACTIVE);

        Set<CampaignCreativeGroup> ccgCollection = new LinkedHashSet<CampaignCreativeGroup>();
        campaign.setCreativeGroups(ccgCollection);

        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupDisplay = getCampaignCreativeGroup(Status.ACTIVE, CCGType.DISPLAY);
        ccgCollection.add(campaignCreativeGroupDisplay);

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupDisplay.setCampaignCreatives(campaignCreativeCollection);

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupDisplay);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupDisplay);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        Campaign cloneCampaign = (Campaign)BeanCloner.clone(campaign, new CopyFilter());

        assertNotSame(campaign, cloneCampaign);
        assertTrue(cloneCampaign.getCreativeGroups().size() == 1);
    }

    @Test
    public void copyTextCCGActive() {
        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupText = getCampaignCreativeGroup(Status.ACTIVE, CCGType.TEXT);

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupText.setCampaignCreatives(campaignCreativeCollection);

        // Set CCGKeyword collection
        Set<CCGKeyword> cckKeywordCollection = new LinkedHashSet<CCGKeyword>();
        campaignCreativeGroupText.setCcgKeywords(cckKeywordCollection);

        cckKeywordCollection.add(getCCGKeyWord(Status.ACTIVE));
        cckKeywordCollection.add(getCCGKeyWord(Status.DELETED));

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupText);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupText);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        CampaignCreativeGroup cloneCampaignCreativeGroup = (CampaignCreativeGroup)BeanCloner.clone(campaignCreativeGroupText, new CopyFilter());

        assertNotNull(cloneCampaignCreativeGroup);
        assertTrue(cloneCampaignCreativeGroup.getCampaignCreatives().size() == 1);
        assertTrue(cloneCampaignCreativeGroup.getCcgKeywords().size() == 1);
    }

    @Test
    public void copyTextCCGDeleted() {
        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupText = getCampaignCreativeGroup(Status.DELETED, CCGType.TEXT);

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupText.setCampaignCreatives(campaignCreativeCollection);

        // Set CCGKeyword collection
        Set<CCGKeyword> cckKeywordCollection = new LinkedHashSet<CCGKeyword>();
        campaignCreativeGroupText.setCcgKeywords(cckKeywordCollection);

        cckKeywordCollection.add(getCCGKeyWord(Status.ACTIVE));
        cckKeywordCollection.add(getCCGKeyWord(Status.DELETED));

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupText);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupText);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        CampaignCreativeGroup cloneCampaignCreativeGroup = (CampaignCreativeGroup)BeanCloner.clone(campaignCreativeGroupText, new CopyFilter());

        assertNull(cloneCampaignCreativeGroup);
    }

    @Test
    public void copyDisplayCCGActive() {
        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupDisplay = getCampaignCreativeGroup(Status.ACTIVE, CCGType.DISPLAY);

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupDisplay.setCampaignCreatives(campaignCreativeCollection);

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupDisplay);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupDisplay);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        CampaignCreativeGroup cloneCampaignCreativeGroup = (CampaignCreativeGroup)BeanCloner.clone(campaignCreativeGroupDisplay, new CopyFilter());

        assertNotNull(cloneCampaignCreativeGroup);
        assertTrue(cloneCampaignCreativeGroup.getCampaignCreatives().size() == 1);
    }

    @Test
    public void copyDisplayCCGDeleted() {
        // Set the CCG Collection
        CampaignCreativeGroup campaignCreativeGroupDisplay = getCampaignCreativeGroup(Status.DELETED, CCGType.DISPLAY);

        Set<CampaignCreative> campaignCreativeCollection = new LinkedHashSet<CampaignCreative>();
        campaignCreativeGroupDisplay.setCampaignCreatives(campaignCreativeCollection);

        // Set campaign creative values
        CampaignCreative campaignCreativeActive = getCampaignCreative(Status.ACTIVE, campaignCreativeGroupDisplay);
        CampaignCreative campaignCreativeDeleted = getCampaignCreative(Status.DELETED, campaignCreativeGroupDisplay);

        campaignCreativeCollection.add(campaignCreativeActive);
        campaignCreativeCollection.add(campaignCreativeDeleted);

        CampaignCreativeGroup cloneCampaignCreativeGroup = (CampaignCreativeGroup)BeanCloner.clone(campaignCreativeGroupDisplay, new CopyFilter());

        assertNull(cloneCampaignCreativeGroup);
    }

    @Test
    public void copyDisplayCCGDeletedTargetChannel() {
        CampaignCreativeGroup campaignCreativeGroupDisplay = getCampaignCreativeGroup(Status.ACTIVE, CCGType.DISPLAY);
        Channel targetChannel = new ExpressionChannel();
        targetChannel.setStatus(Status.DELETED);
        campaignCreativeGroupDisplay.setChannel(targetChannel);

        CampaignCreativeGroup cloneCampaignCreativeGroup = (CampaignCreativeGroup)BeanCloner.clone(campaignCreativeGroupDisplay, new CopyFilter());
        assertNull(cloneCampaignCreativeGroup.getChannel());
    }

    @Test
    public void copyCreativeActive() {
        // Set campaign creative values
        Creative creativeActive = getCreative(Status.ACTIVE);

        Creative cloneCreative = (Creative)BeanCloner.clone(creativeActive, new CopyFilter());

        assertNotNull(cloneCreative);
    }

    @Test
    public void copyCreativeDeleted() {
        // Set campaign creative values
        Creative creativeActive = getCreative(Status.DELETED);

        Campaign cloneCreative = (Campaign)BeanCloner.clone(creativeActive, new CopyFilter());

        assertNull(cloneCreative);
    }

    private Campaign getCampaign(Status status) {
        Campaign campaign = new Campaign();
        campaign.setId((new Date()).getTime());
        campaign.setStatus(status);
        campaign.setDateStart(new Date());
        return campaign;
    }

    private CampaignCreativeGroup getCampaignCreativeGroup(Status status, CCGType ccgType) {
        CampaignCreativeGroup campaignCreativeGroup = new CampaignCreativeGroup();
        campaignCreativeGroup.setId((new Date()).getTime());
        campaignCreativeGroup.setCcgType(ccgType);
        campaignCreativeGroup.setStatus(status);
        return campaignCreativeGroup;
    }

    private CampaignCreative getCampaignCreative(Status status, CampaignCreativeGroup campaignCreativeGroup) {
        CampaignCreative campaignCreative = new CampaignCreative();
        campaignCreative.setCreative(getCreative(status));
        campaignCreative.setId((new Date()).getTime());
        campaignCreative.setCreativeGroup(campaignCreativeGroup);
        campaignCreative.setStatus(status);
        return campaignCreative;
    }

    private Creative getCreative(Status status) {
        Creative creative = new Creative();
        creative.setId((new Date()).getTime());
        creative.setStatus(status);
        return creative;
    }

    private CCGKeyword getCCGKeyWord(Status status) {
        CCGKeyword ccgKeyword = new CCGKeyword();
        ccgKeyword.setId((new Date()).getTime());
        ccgKeyword.setStatus(status);
        return ccgKeyword;
    }

    public static class FooClassA implements Serializable {
        private String A = "A";
        private String onlyWithGetterField = "this field has only getter";

        @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedList.class)
        private Collection<FooClassB> foos;

        private FooClassB B;

        @CopyPolicy(strategy = CopyStrategy.SHALLOW)
        private Collection<FooClassB> shallowFoos;

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        public FooClassB getSerializeB() {
            return serializeB;
        }

        @CopyPolicy(strategy = CopyStrategy.CLONE, cloner = SerializeCloner.class)
        private FooClassB serializeB = new FooClassB();

        public FooClassB getStandardCloneB() {
            return standardCloneB;
        }

        @CopyPolicy(strategy = CopyStrategy.CLONE)
        private FooClassB standardCloneB = new FooClassB();

        public FooClassA() {
        }

        public String getA() {
            return A;
        }

        public void setA(String value) {
            this.A = value;
        }

        public FooClassB getB() {
            return this.B;
        }

        public void setB(FooClassB value) {
            this.B = value;
        }

        public Collection<FooClassB> getFoos() {
            return foos;
        }

        public void setFoos(Collection<FooClassB> value) {
            this.foos = value;
        }

        public Collection<FooClassB> getShallowFoos() {
            return shallowFoos;
        }

        public void setShallowFoos(Collection<FooClassB> value) {
            this.shallowFoos = value;
        }

        public String getOnlyWithGetterField() {
            return onlyWithGetterField;
        }
    }

    public static class FooClassB implements Serializable, Cloneable {
        public static final String USER_CLONE_TEST = "user clone test";
        public static final String USER_CLONER_TEST = "user cloner test";

        @CopyPolicy(strategy = CopyStrategy.SETNULL)
        private Long id = 7l;

        private String A = "B";
        private String B = "B";
        private FooClassA backFooA;

        @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
        private Long excludedNumber;

        Map map = new HashMap();

        public FooClassB() {
        }

        public String getA() {
            return A;
        }

        public void setA(String value) {
            this.A = value;
        }

        public void setBackFooA(FooClassA value) {
            this.backFooA = value;
        }

        public FooClassA getBackFooA() {
            return this.backFooA;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long value) {
            this.id = value;
        }

        public Long getExcludedNumber() {
            return this.excludedNumber;
        }

        public void setExcludedNumber(Long value) {
            this.excludedNumber = value;
        }

        private void writeObject(ObjectOutputStream os) throws IOException {
           os.write(USER_CLONER_TEST.getBytes());
        }

        private void readObject(ObjectInputStream is) throws IOException {
            byte[] bytes = new byte[USER_CLONER_TEST.getBytes().length];
            is.read(bytes);

            A = new String(bytes);
        }

        public String getB() {
            return B;
        }

        public void setB(String b) {
            B = b;
        }

        @SuppressWarnings({"CloneDoesntCallSuperClone"})
        @Override
        public Object clone() throws CloneNotSupportedException {
            FooClassB b = new FooClassB();
            b.setB(USER_CLONE_TEST);

            return b;
        }
    }
}
