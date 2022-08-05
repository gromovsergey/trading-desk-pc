package com.foros.session.restriction;

import com.foros.model.restriction.Predicates;
import com.foros.model.restriction.RestrictionCommand;
import com.foros.model.restriction.RestrictionCommandsOperation;
import com.foros.model.restriction.RestrictionParameter;
import com.foros.restriction.RestrictionService;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless(name = "LookupRestrictionService")
public class LookupRestrictionServiceBean implements LookupRestrictionService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private RestrictionService restrictionService;

    public Predicates lookupAndInvoke(RestrictionCommandsOperation operation) {
        List<Boolean> result = new ArrayList<>(operation.getRestrictionCommands().size());

        for (RestrictionCommand command : operation.getRestrictionCommands()) {
            String restrictionName = command.getName();
            if (command.getParams() == null || command.getParams().isEmpty()) {
                result.add(restrictionService.isPermitted(restrictionName));
                continue;
            }

            Object[] params = findParams(command.getParams());
            result.add(restrictionService.isPermitted(restrictionName, params));
        }

        return new Predicates(result);
    }

    private Object[] findParams(List<RestrictionParameter> params) {
        Object[] result = new Object[params.size()];
        int i = 0;

        for (RestrictionParameter param : params) {
            Object obj = param.getId() == null ? param.getName() : findObjectWithId(param);
            result[i++] = obj;
        }
        return result;
    }

    private Object findObjectWithId(RestrictionParameter param) {
        Object obj = null;

        if (param.getName() == null) {
            return param.getId();
        }

        try {
            obj = em.find(Class.forName(param.getName()), param.getId());
        } catch (Exception e) {
        }

        if (obj == null) {
            throw ConstraintViolationException.newBuilder("errors.entity.notFound")
                    .withError(BusinessErrors.ENTITY_NOT_FOUND)
                    .withValue(param.getName() + '#' + param.getId())
                    .build();
        }

        return obj;
    }
}
