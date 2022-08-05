package com.foros.util.context;

public class NullSwitchContextListener implements SwitchContextListener {
    public static final SwitchContextListener INSTANCE = new NullSwitchContextListener();
    @Override
    public void onSwitchTo(ContextBase context) {
        // do nothing
    }
}
