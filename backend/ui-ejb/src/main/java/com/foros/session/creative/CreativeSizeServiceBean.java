package com.foros.session.creative;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.creative.SizeType;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.site.Tag;
import com.foros.model.template.OptionGroup;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessServiceBean;
import com.foros.session.EntityTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.QuickReferenceHolderService;
import com.foros.session.UtilityService;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.StatusService;
import com.foros.session.template.OptionGroupService;
import com.foros.util.CollectionUtils;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.EntityUtils;
import com.foros.util.bean.Filter;
import com.foros.util.jpa.NativeQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.util.mapper.Converter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless(name = "CreativeSizeService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class CreativeSizeServiceBean extends BusinessServiceBean<CreativeSize> implements CreativeSizeService {

    @EJB
    private CreativePreviewService creativePreviewService;

    @EJB
    private StatusService statusService;

    @EJB
    private AuditService auditService;

    @EJB
    private UtilityService utilityService;

    @EJB
    private UserService userService;

    @EJB
    private QuickReferenceHolderService quickReferenceHolderService;

    @EJB
    private OptionGroupService optionGroupService;

    public CreativeSizeServiceBean() {
        super(CreativeSize.class);
    }

    @Override
    @Restrict(restriction = "CreativeSize.create")
    @Validate(validation = "CreativeSize.create", parameters = "#size")
    @Interceptors({CaptureChangesInterceptor.class})
    public void create(CreativeSize size) {
        auditService.audit(size, ActionType.CREATE);
        size.setStatus(Status.ACTIVE);
        prePersist(size);
        super.create(size);
    }

    @Override
    @Restrict(restriction = "CreativeSize.update", parameters="find('CreativeSize', #size.id)")
    @Validate(validation = "CreativeSize.update", parameters = "#size")
    @Interceptors({CaptureChangesInterceptor.class})
    public CreativeSize update(CreativeSize size) {
        size.unregisterChange("optionGroups");
        prePersist(size);
        size = super.update(size);
        auditService.audit(size, ActionType.UPDATE);
        creativePreviewService.deletePreview(size);
        return size;
    }

    private void prePersist(CreativeSize size) {
        if (size.getSizeType() != null) {
            size.setSizeType(em.find(SizeType.class, size.getSizeType().getId()));
        }
    }

    @Override
    @Restrict(restriction="CreativeSize.createCopy", parameters="find('CreativeSize', #id)")
    @Interceptors({CaptureChangesInterceptor.class})
    public CreativeSize createCopy(Long id) {
        CreativeSize size = findById(id);
        CreativeSize newSize = EntityUtils.clone(size);
        String originalName = size.getDefaultName();
        String defaultName = utilityService.calculateNameForCopy(size, 100, originalName, "defaultName");
        newSize.setDefaultName(defaultName);
        String protocolName = calculateProtocolNameCopy();
        newSize.setProtocolName(protocolName);
        newSize.setOptionGroups(optionGroupService.copyGroups(size.getOptionGroups()));
        for (OptionGroup group : newSize.getOptionGroups()) {
            group.setCreativeSize(newSize);
        }
        auditService.audit(newSize, ActionType.CREATE);
        super.create(newSize);
        return newSize;
    }

    private String calculateProtocolNameCopy() {
        int attemptsLimit = 20;
        String base = "proto";
        long timestamp = (new Date()).getTime();

        int i = 0;
        while (i < attemptsLimit) {
            String copyName = base + timestamp;
            Query q = em.createQuery("select count(cs) from CreativeSize cs where protocolName = ?1");
            q.setParameter(1, copyName);
            Number count = (Number) q.getSingleResult();
            if (count.intValue() == 0) {
                return copyName;
            }
            i++;
            timestamp += i;
        }
        throw new RuntimeException("Can not calculate protocol name for CreativeSize!");
    }

    @Override
    @Restrict(restriction = "CreativeSize.delete", parameters="find('CreativeSize', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        statusService.delete(findById(id));
    }

    @Override
    @Restrict(restriction = "CreativeSize.undelete", parameters="find('CreativeSize', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undelete(Long id) {
    	statusService.undelete(findById(id));
    }

    @Override
    public List<CreativeSize> findByAccountType(AccountType accType) {
        Query q = em.createNamedQuery("CreativeSize.findByAccType");
        q.setParameter("accType", accType);
        @SuppressWarnings("unchecked")
        List<CreativeSize> result = q.getResultList();
        return result;
    }

    @Override
    public List<CreativeSize> findByAccountTypeAndSizeType(Long accountTypeId, Long sizeTypeId) {
        AccountType accountType = em.find(AccountType.class, accountTypeId);
        SizeType sizeType = em.find(SizeType.class, sizeTypeId);

        Set<CreativeSize> sizesSet = new HashSet<>(accountType.getCreativeSizes());
        sizesSet.retainAll(sizeType.getSizes());

        CollectionUtils.filter(sizesSet, new Filter<CreativeSize>() {
            @Override
            public boolean accept(CreativeSize size) {
                return size.getStatus() != Status.DELETED;
            }
        });
        return new ArrayList<>(sizesSet);
    }

    @Override
    public CreativeSize findTextSize() {
        return findById(findTextSizeId());
    }

    @Override
    public CreativeSize findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Restrict(restriction = "CreativeSize.view")
    public CreativeSize view(Long id) {
        return findById(id);
    }

    @Override
    public List<CreativeSize> findAll() {
        return super.findAll();
    }

    @Override
    public Collection<CreativeSizeTO> findAll(boolean withDeleted) {
        String sql = "SELECT cs.size_id, cs.name, cs.status, cs.protocol_name, cs.width, cs.height FROM CreativeSize cs";
        if (!withDeleted) {
            sql += " WHERE cs.status != 'D'";
        }
        List resultList = em.createNativeQuery(sql).getResultList();
        return CollectionUtils.convert(new Converter<Object[], CreativeSizeTO>() {
            @Override
            public CreativeSizeTO item(Object[] row) {
                Long id = ((Number) row[0]).longValue();
                String name = row[1].toString();
                char status = row[2].toString().charAt(0);
                String protocolName = row[3].toString();
                Long width = row[4] != null ? ((Number) row[4]).longValue() : null;
                Long height = row[5] != null ? ((Number) row[5]).longValue() : null;
                return new CreativeSizeTO(id, name, status, protocolName, width, height);
            }
        }, resultList);
    }

    @Override
    public List<CreativeSize> findAllNotDeleted() {
        Query q = em.createNamedQuery("CreativeSize.findAllNotDeleted");
        return q.getResultList();
    }

    @Override
    public List<CreativeSizeTO> findAvailableSizes(Long accountTypeId) {
        ConditionStringBuilder sql = new ConditionStringBuilder()
                .append(" SELECT s.size_id, s.name, s.status, s.width, s.height FROM CreativeSize s")
                .append(" WHERE s.name <> :name")
                .append(" and")
                .append("(")
                .append(" s.status <> 'D'")
                .append(accountTypeId != null, " or s.size_id IN (SELECT size_id FROM AccountTypeCreativeSize WHERE account_type_id = :accountTypeId)")
                .append(")");

        QueryWrapper<Object[]> q = new NativeQueryWrapper<Object[]>(em, sql.toString());
        q.oneIf(accountTypeId != null).setParameter("accountTypeId", accountTypeId);
        q.setParameter("name", CreativeSize.TEXT_SIZE);

        return CollectionUtils.convert(new Converter<Object[], CreativeSizeTO>() {
            @Override
            public CreativeSizeTO item(Object[] row) {
                Long id = ((Number) row[0]).longValue();
                String name = row[1].toString();
                char status = row[2].toString().charAt(0);
                Long width = row[3] != null ? ((Number) row[3]).longValue() : null;
                Long height = row[4] != null ? ((Number) row[4]).longValue() : null;
                return new CreativeSizeTO(id, name, status, width,height);
            }
        }, q.getResultList());
    }

    @Override
    public List<EntityTO> getIndex() {
        StringBuilder sb = new StringBuilder();
        sb.append("select new com.foros.session.EntityTO(s.id, s.defaultName, s.status, 'CreativeSize.' || s.id) from CreativeSize s");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            sb.append(" where s.status <> 'D'");
        }
        return em.createQuery(sb.toString()).getResultList();
    }

    @Override
    public long countExpandableTags(Long sizeId) {
        Query query = em.createNativeQuery(
            "SELECT SUM(countAll.tags) FROM " +
            "  (SELECT COUNT(t.tag_id ) tags FROM tags t " +
            "       LEFT JOIN tag_tagsize ts  ON ts.tag_id = t.tag_id " +
            "       WHERE ts.size_id = :sizeId " +
            "       AND t.ALLOW_EXPANDABLE = 'Y' " +
            "       AND t.status          <> 'D' " +
            "  UNION ALL " +
            "  SELECT COUNT(t.tag_id) tags FROM tags t" +
            "       LEFT JOIN creativesize cs ON t.size_type_id = cs.size_type_id" +
            "       WHERE cs.size_id = :sizeId AND (t.flags & :allSizeFlag) != 0 " +
            "       AND t.ALLOW_EXPANDABLE    = 'Y' " +
            "       AND t.status             <> 'D' " +
            "  ) countAll ")
            .setParameter("sizeId", sizeId)
            .setParameter("allSizeFlag", Tag.ALL_SIZES_FLAG);

        return ((Number)query.getSingleResult()).longValue();
    }

    @Override
    public long countCreativesByExpansions(Long sizeId, Collection<CreativeSizeExpansion> expansions) {
        Query query = em.createQuery("select count(c) from Creative c " +
                "where c.size.id = :sizeId " +
                "and c.status <> 'D' " +
                "and c.expansion in (:expansions) ")
                .setParameter("sizeId", sizeId)
                .setParameter("expansions", expansions);

        return ((Number)query.getSingleResult()).longValue();
    }

    @Override
    public long countExpandableCreatives(Long sizeId) {
        Query query = em.createQuery("select count(c) from Creative c " +
                "where c.size.id = :sizeId " +
                "and c.status <> 'D' " +
                "and c.expandable = true")
                .setParameter("sizeId", sizeId);

        return ((Number)query.getSingleResult()).longValue();
    }

    @Override
    public Long findTextSizeId() {
        return quickReferenceHolderService.getTextSizeId();
    }

    @Override
    public CreativeSize findByName(String name) {
        try {
            return (CreativeSize) em.createQuery("select cs from CreativeSize cs where cs.defaultName = :name").
                    setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
