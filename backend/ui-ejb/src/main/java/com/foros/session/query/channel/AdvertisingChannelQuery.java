package com.foros.session.query.channel;

import com.foros.model.channel.ChannelVisibility;
import com.foros.security.AccountRole;
import com.foros.session.channel.service.AdvertisingChannelType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AdvertisingChannelQuery extends ChannelQuery<AdvertisingChannelQuery> {
    
    AdvertisingChannelQuery type(Collection<AdvertisingChannelType> types);

    AdvertisingChannelQuery visibility(Collection<ChannelVisibility> visibility);

    AdvertisingChannelQuery managedOrPublic(Long userId, Long accountId);

    AdvertisingChannelQuery ownedOrPublic(Long accountId);

    AdvertisingChannelQuery hasCategoryChannel(Long categoryChannelId);

    AdvertisingChannelQuery byAccountRolesOrPublic(List<AccountRole> roles);

    AdvertisingChannelQuery excludeTestAccounts();

    AdvertisingChannelQuery onlyTestAccounts();

    AdvertisingChannelQuery orderByName();

    AdvertisingChannelQuery restrictedByAccountIdsOrPublic(Set<Long> accountIds);

}
