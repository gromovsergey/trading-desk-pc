package com.foros.session.channel.service;

import com.foros.model.channel.CategoryChannel;

import java.util.List;


public interface CategoryOwnedChannelService {

    List<CategoryChannel> getCategories(Long channelId);
}
