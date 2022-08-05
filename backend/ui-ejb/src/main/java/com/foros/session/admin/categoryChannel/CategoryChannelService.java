package com.foros.session.admin.categoryChannel;

import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.session.EntityTO;
import com.foros.util.tree.TreeHolder;

import java.util.List;
import javax.ejb.Local;

@Local
public interface CategoryChannelService {
    public List<CategoryChannelTO> getChannelList(Long parentChannelId);

    public CategoryChannel find(Long id);

    public CategoryChannel view(Long id);

    public Long createChannel(CategoryChannel channel);

    public void updateChannel(CategoryChannel channel);

    public void inactivate(Long id);

    public void activate(Long id);

    public void delete(Long id);

    public void undelete(Long id);

    public List<EntityTO> getChannelAncestorsChain(Long channelId, boolean keepLastChain);

    public TreeHolder<EntityTO> getCategoryChannelTree(long channelId);

    public void updateChannelCategories(Channel channel);

    void updateDiscoverListCategories(DiscoverChannelList dcList);

    List<CategoryChannel> getCategories(Long channelId);

}
