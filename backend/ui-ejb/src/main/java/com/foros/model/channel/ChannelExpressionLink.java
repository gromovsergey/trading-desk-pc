package com.foros.model.channel;

public class ChannelExpressionLink extends Channel {
    private String expression;

    public ChannelExpressionLink() {
    }

    public ChannelExpressionLink(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return null;
    }

    @Override
    public String getChannelType() {
        return null;
    }

}
