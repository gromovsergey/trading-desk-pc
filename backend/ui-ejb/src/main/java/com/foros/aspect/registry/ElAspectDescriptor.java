package com.foros.aspect.registry;

import com.foros.aspect.ElAspectInfo;
import com.foros.aspect.el.Expression;
import java.lang.annotation.Annotation;

public class ElAspectDescriptor implements AspectDescriptor {

    private ElAspectInfo aspectInfo;
    private Expression[] parameterExpressions;

    public ElAspectDescriptor(ElAspectInfo aspectInfo, Expression[] parameterExpressions) {
        this.aspectInfo = aspectInfo;
        this.parameterExpressions = parameterExpressions;
    }

    public String getName() {
        return aspectInfo.getName();
    }

    public Expression[] getParameterExpressions() {
        return parameterExpressions;
    }

    public Annotation getAnnotation() {
        return aspectInfo.getAnnotation();
    }

    @Override
    public String toString() {
        return "ElAspect: " + getName() + "(" + aspectInfo.getParameters().toString() + ")";
    }
}
