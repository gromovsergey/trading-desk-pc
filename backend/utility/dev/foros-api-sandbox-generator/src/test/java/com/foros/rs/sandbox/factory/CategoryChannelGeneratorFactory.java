package com.foros.rs.sandbox.factory;

import com.foros.model.account.InternalAccount;
import com.foros.model.channel.CategoryChannel;
import com.foros.test.factory.CategoryChannelTestFactory;

public class CategoryChannelGeneratorFactory extends BaseGeneratorFactory<CategoryChannel, CategoryChannelTestFactory> {
    private final InternalAccount account;

    public CategoryChannelGeneratorFactory(CategoryChannelTestFactory categoryChannelTestFactory, InternalAccount account) {
        super(categoryChannelTestFactory);
        this.account = account;
    }

    @Override
    protected CategoryChannel createDefault() {
        CategoryChannel channel = factory.create(account);
        channel.setName(entityName);
        return channel;
    }
}
