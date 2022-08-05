package com.foros.test.factory;

import com.foros.model.account.InternalAccount;
import com.foros.model.channel.CategoryChannel;
import com.foros.session.admin.categoryChannel.CategoryChannelService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class CategoryChannelTestFactory extends TestFactory<CategoryChannel> {
    @EJB
    private CategoryChannelService categoryChannelService;

    @EJB
    private InternalAccountTestFactory internalAccountTF;

    public void populate(CategoryChannel channel) {
        channel.setName(getTestEntityRandomName());
    }

    @Override
    public CategoryChannel create() {
        InternalAccount account = internalAccountTF.createPersistent();
        return create(account);
    }

    public CategoryChannel createChild(CategoryChannel parent) {
        CategoryChannel child = create((InternalAccount) parent.getAccount());
        child.setParentChannelId(parent.getId());
        persist(child);
        return child;
    }

    public CategoryChannel create(InternalAccount account) {
        CategoryChannel channel = new CategoryChannel();
        channel.setAccount(account);
        populate(channel);
        return channel;
    }

    @Override
    public void persist(CategoryChannel channel) {
        categoryChannelService.createChannel(channel);
    }

    public void update(CategoryChannel channel) {
        categoryChannelService.updateChannel(channel);
    }

    @Override
    public CategoryChannel createPersistent() {
        CategoryChannel channel = create();
        persist(channel);
        return channel;
    }

    public CategoryChannel createPersistent(InternalAccount account) {
        CategoryChannel channel = create(account);
        persist(channel);
        return channel;
    }

    public void delete(CategoryChannel channel) {
        categoryChannelService.delete(channel.getId());
    }
}
