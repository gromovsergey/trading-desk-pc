package com.foros.changes.inspection;

public abstract class ChangeNodeSupport implements ChangeNode {
    protected abstract void prepareInternal(PrepareChangesContext context);

    @Override
    public final void prepare(PrepareChangesContext context) {
        context.push(this);
        prepareInternal(context);
        context.pop();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[changeType=" + getChangeType() + ", value=" + getLastDefinedValue() + "]";
    }
}
