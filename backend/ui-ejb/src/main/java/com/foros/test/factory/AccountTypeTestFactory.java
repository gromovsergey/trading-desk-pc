package com.foros.test.factory;

import com.foros.model.channel.DeviceChannel;
import com.foros.model.security.AccountType;
import com.foros.session.admin.accountType.AccountTypeService;

import javax.ejb.EJB;
import java.util.HashSet;
import java.util.Set;

public abstract class AccountTypeTestFactory extends TestFactory<AccountType> {
    @EJB
    private AccountTypeService accountTypeService;

    @EJB
    private DeviceChannelTestFactory deviceChannelTF;

    public void applyFlags(AccountType accountType, long ... accountTypeFlags) {
        // reset flags
        accountType.setFlags(0);

        // set
        if (accountTypeFlags != null) {
            for (long flag : accountTypeFlags) {
                accountType.setFlags(accountType.getFlags() | flag);
            }
        }
    }

    public void populate(AccountType accountType) {
        accountType.setName(getTestEntityRandomName());

        Set<DeviceChannel> accountDeviceChannels = new HashSet<>();
        addAllDevicesChildren(deviceChannelTF.getApplicationsChannel(), accountDeviceChannels);
        addAllDevicesChildren(deviceChannelTF.getBrowsersChannel(), accountDeviceChannels);
        accountType.setDeviceChannels(accountDeviceChannels);
    }

    @Override
    public void persist(AccountType type) {
        accountTypeService.create(type);
        entityManager.flush();
    }

    public void update(AccountType type) {
        accountTypeService.update(type);
        entityManager.flush();
    }

    public AccountType find(Long id) {
        return findAny(AccountType.class, new QueryParam("id", id));
    }

    private void addAllDevicesChildren(DeviceChannel rootChannel, Set<DeviceChannel> result) {
        result.add(rootChannel);
        for (DeviceChannel childChannel : rootChannel.getChildChannels()) {
            addAllDevicesChildren(childChannel, result);
        }
    }
}
