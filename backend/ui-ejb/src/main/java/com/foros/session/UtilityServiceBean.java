package com.foros.session;

import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.IdNameEntity;
import com.foros.model.Identifiable;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.model.security.Statusable;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.country.ctra.CTRAlgorithmService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.command.HibernateWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.i18n.LocalizableNameProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.engine.SessionFactoryImplementor;

@Stateless(name = "UtilityService")
public class UtilityServiceBean implements UtilityService {
    private static final String COPY_OF = "Copy of ";

    private static final String EXTRACT_NUMBER_REGEXP = "\\((\\d+)\\)";
    private static final String NAME_PARAMETER = "name";

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private HibernateWorkExecutorService executorService;

    @EJB
    private CountryService countryService;

    @EJB
    private CTRAlgorithmService ctrAlgorithmService;

    /**
     * Creates a new instance of CommonService
     */
    public UtilityServiceBean() {
    }

    private enum BraceParsingState {START, DIGITS, BRACES}

    @Override
    public <E extends IdNameEntity> String calculateNameForCopy(E entity, int maxLength) {
        String originalName = entity.getName();
        return calculateNameForCopy(entity, maxLength, originalName, NAME_PARAMETER);
    }

    @Override
    public <E> String calculateNameForCopy(E entity, int maxLength, String originalName, String nameParameter) {
        String qs =
                "SELECT c." + nameParameter + " FROM " + entity.getClass().getSimpleName() + " c " +
                "WHERE c." + nameParameter + " LIKE :" + nameParameter + " " +
                "ORDER BY c." + nameParameter;

        String checkQS =
                "SELECT c." + nameParameter + " FROM " + entity.getClass().getSimpleName() + " c " +
                "WHERE c." + nameParameter + " = :" + nameParameter + " ";

        String nameForCopy;

        if (originalName.startsWith(COPY_OF)) {
            nameForCopy = removeSuffix(originalName);
        } else {
            nameForCopy = COPY_OF + originalName;
        }

        int curLength;
        for (int i = 0; i < (maxLength > 10?10:maxLength); i++) {
            curLength = maxLength - i;
            if (nameForCopy.length() > curLength) {
                nameForCopy = nameForCopy.substring(0, curLength);
            }

            Query query = em.createQuery(qs);
            query.setParameter(nameParameter, nameForCopy + "%");

            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();

            String suffix = "";

            if (results.size() > 0) {
                 suffix = " (" + calculateNextCopyNumber(results) + ")";
            }

            if (nameForCopy.length() + suffix.length() > maxLength) {
                nameForCopy = nameForCopy.substring(0, maxLength - suffix.length());
            }

            nameForCopy += suffix;

            // checking nameForCopy unique
            query = em.createQuery(checkQS);
            query.setParameter(nameParameter, nameForCopy);
            if (query.getResultList().size() == 0) {
                return nameForCopy;
            }
        }

        throw new RuntimeException("Unable to generate unique nameForCopy for entity "+entity.getClass().getSimpleName());
    }

    String removeSuffix(String name) {
        BraceParsingState state = BraceParsingState.START;

        int index = name.length()-1;
        while (index >= 0) {
            char ch = name.charAt(index);
            switch (state) {
                case START: {
                    if (ch == ')') {
                        state = BraceParsingState.DIGITS;
                        index--;
                    } else {
                        return name;
                    }

                    break;
                }

                case DIGITS: {
                    if ("0123456789".indexOf(ch) != -1) {
                        index--;
                    } else if (ch == '(') {
                        state = BraceParsingState.BRACES;
                        index--;
                    } else {
                        return name;
                    }

                    break;
                }

                case BRACES: {
                    if (ch == ' ') {
                        return name.substring(0, index);
                    } else {
                        return name;
                    }
                }
            }
        }

        return name;
    }

    public int calculateNextCopyNumber(List<String> entityNameList) {
        int result = 1;

        if (entityNameList.size() > 0) {
            Set<Integer> numbers = new HashSet<Integer>();
            for (String entityName : entityNameList) {
                Pattern pattern = Pattern.compile(EXTRACT_NUMBER_REGEXP);

                if (entityName.lastIndexOf("(") > 0)
                    entityName = entityName.substring(entityName.lastIndexOf("("));

                Matcher matcher = pattern.matcher(entityName);
                if (matcher.find()) {
                    String number = matcher.group(1);
                    try {
                        numbers.add(Integer.parseInt(number));
                    } catch (NumberFormatException ex) {
                        //ignore
                    }
                } else {
                    numbers.add(0);
                }
            }
            Integer counter = 1;
            for (int i = 0; i < numbers.size()+1; i++) {
                if (!numbers.contains(counter)) {
                    break;
                }
                counter++;
            }
            result = counter;
        }

        return result;
    }

    @Override
    public <E> E findById(Class<? extends E> entityClass, Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        return em.find(entityClass, id);
    }

    @Override
    public Object find(String entityName, Long id) {
        Class<? extends EntityBase> type = findClassByEntityName(entityName);
        return find(type, id);
    }

    private Class<? extends EntityBase> findClassByEntityName(final String name) {
        String className = executorService.execute(new HibernateWork<String>() {
            @Override
            public String execute(Session session) {
                SessionFactoryImplementor factory = (SessionFactoryImplementor) session.getSessionFactory();
                return factory.getImportedClassName(name);
            }
        });

        try {
            //noinspection unchecked
            return (Class<? extends EntityBase>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <E> E find(Class<? extends E> entityClass, Long id) {
        E result = safeFind(entityClass, id);

        if (result == null) {
            throw new EntityNotFoundException(entityClass.getSimpleName() + " with id=" + id + " not found");
        }

        return result;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <E> E safeFind(Class<? extends E> entityClass, Long id) {
        E result;
        if (entityClass.equals(Country.class)) {
            result = (E) countryService.findByCountryId(id);
        } else if (entityClass.equals(CTRAlgorithmData.class)) {
            result = (E) ctrAlgorithmService.findByCountryId(id);
        } else {
            result = findById(entityClass, id);
        }

        return result;
    }

    @Override
    public String getEntityText(Class entityClass, Long id) {
        Object entity = find(entityClass, id);
        String valueText;

        if (entity instanceof IdNameEntity) {
            valueText = ((IdNameEntity) entity).getName();
        } else if (entity instanceof LocalizableNameEntity) {
            valueText = LocalizableNameUtil.getLocalizedValue(((LocalizableNameEntity) entity).getName());
        } else {
            throw new IllegalArgumentException("Can't calculate text for entity " + entity.getClass());
        }

        if (entity instanceof Statusable) {
            valueText = EntityUtils.appendStatusSuffix(valueText, ((Statusable) entity).getStatus());
        }

        return valueText;
    }

    @Override
    public List<String> getEntityTextList(Class entityClass, Collection<Long> ids) {
        List<String> result = new ArrayList<String>();

        if (CollectionUtils.isNullOrEmpty(ids)) {
            return result;
        }

        boolean isIdNameEntity = IdNameEntity.class.isAssignableFrom(entityClass);
        boolean isLocalizableNameEntity = LocalizableNameEntity.class.isAssignableFrom(entityClass);
        boolean isStatusable = Statusable.class.isAssignableFrom(entityClass);

        String nameProperty;
        if (isIdNameEntity) {
            nameProperty = "name";
        } else if (isLocalizableNameEntity) {
            nameProperty = "defaultName";
        } else if (CampaignCreative.class.equals(entityClass)) {
            nameProperty = "creative.name";
        } else {
            throw new IllegalArgumentException("Can't calculate text for entity " + entityClass);
        }

        StringBuilder q = new StringBuilder("SELECT c.id, c." + nameProperty + " ");
        if (isStatusable) {
            q.append(", c.status ");
        }
        q.append("FROM ").append(entityClass.getSimpleName()).append(" c ");
        q.append("WHERE ").append(SQLUtil.formatINClause("c.id", ids));

        Query query = em.createQuery(q.toString());

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        for (Object[] row : resultList) {
            String valueText;
            if(isLocalizableNameEntity) {
                Long id = (Long)row[0];
                LocalizableName localizableName = LocalizableNameProvider.valueOf(entityClass).provide((String) row[1], id);
                valueText = LocalizableNameUtil.getLocalizedValue(localizableName);
            } else {
                valueText = (String) row[1];
            }
            if (isStatusable) {
                valueText = EntityUtils.appendStatusSuffix(valueText, Status.valueOf((Character) row[2]));
            }
            result.add(valueText);
        }

        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(String val1, String val2) {
                return StringUtil.lexicalCompare(val1, val2);
            }
        });

        return result;
    }

    @Override
    public <C extends Collection<T>, T extends Identifiable> C resolveReferences(C source, C result, Class<T> type) {
        if (source != null) {
            for (T item : source) {
                result.add(em.getReference(type, item.getId()));
            }
        }

        return result;
    }

    @Override
    public boolean isEntityExists(Class<? extends EntityBase> type, Long id) {
        return em.find(type, id) != null;
    }
}
