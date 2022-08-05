package com.foros.aspect.registry;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.ElAspectInfo;
import com.foros.aspect.ValidatorAspectInfo;
import com.foros.aspect.el.Expression;
import com.foros.aspect.el.ExpressionException;
import com.foros.aspect.el.ExpressionLanguageService;

import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "AspectDescriptorFactoryService")
public class AspectDescriptorFactoryServiceBean implements AspectDescriptorFactoryService {

    @EJB
    private ExpressionLanguageService elService;

    @Override
    public AspectDescriptor create(AspectInfo aspectInfo) {
        if (aspectInfo instanceof ElAspectInfo) {
            ElAspectInfo info = (ElAspectInfo) aspectInfo;

            return new ElAspectDescriptor(info,
                    compileExpressions(info.getParameters()));
        } else if (aspectInfo instanceof ValidatorAspectInfo) {
            return new ValidatorAspectDescriptor((ValidatorAspectInfo) aspectInfo);
        }

        throw new IllegalArgumentException("AspectDescriptorFactory not support aspect type: " + aspectInfo.getClass());
    }

    private Expression[] compileExpressions(List<String> parameters) {
        Expression[] expressions = new Expression[parameters.size()];

        Iterator<String> iterator = parameters.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            try {
                expressions[i] = elService.compileExpression(iterator.next());
            } catch (ExpressionException e) {
                throw new RegistryInitializeException("Can't parse expression", e);
            }
        }
        return expressions;
    }

}
