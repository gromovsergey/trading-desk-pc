package app.programmatic.ui.channel.service;

import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import com.foros.rs.client.model.advertising.channel.Visibility;
import org.springframework.web.multipart.MultipartFile;
import app.programmatic.ui.channel.dao.model.*;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.view.IdName;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface ChannelService {

    BehavioralChannel findBehavioralUnchecked(Long id);

    ExpressionChannelStat fetchExpressionStat(Long id);

    BehavioralChannelStat fetchBehavioralStat(Long id);

    ExpressionChannel findExpressionUnchecked(Long id);

    ExpressionChannel findExpressionAsAdmin(Long id);

    List<com.foros.rs.client.model.advertising.channel.Channel> findChannels(List<Long> ids);

    List<BehavioralChannel> findAllBehavioral(List<Long> ids);

    List<Channel> findByAccountId(Long accountId);

    List<Channel> findAllChannels(String name, Long accountId, ChannelType type, ChannelVisibility visibility);

    List<Channel> findByIdsForExternal(Long extAccountId, List<Long> ids);

    List<Channel> findByIdsUnrestricted(List<Long> ids);

    List<Channel> findByIdsWithUniqUsersInfoUnrestricted(List<Long> ids);

    List<ChannelStat> getStatsByLineItemId(Long lineItemId);

    List<ChannelStat> getStatsByFlightId(Long flightId);

    Long createOrUpdate(ExpressionChannel channel);

    Long createOrUpdateAsAdmin(ExpressionChannel channel);

    Long createOrUpdate(BehavioralChannel channel);

    Long createOrUpdateAsAdmin(BehavioralChannel channel);

    List<Long> createOrUpdate(List<BehavioralChannel> channel);

    List<Long> createOrUpdateExpressions(List<ExpressionChannel> channels);

    List<Long> filterActive(List<Long> ids);

    List<Channel> searchByName(String countryCode, Long accountId, String name);

    Collection<ChannelNameId> searchByNames(Set<ChannelName> name);

    Collection<ChannelNode> getChannelNodeList(Long parentId, String language);

    Collection<ChannelNode> getChannelRubricNodeList(String source, String countryCode, String language);

    MajorDisplayStatus changeStatus(Long channelId, ChannelOperation operation);

    List<IdName> findByName(String name, String countryCode, ChannelType channelType, Visibility visibility);

    Long createSpecialChannel(Long lineItemId);

    boolean checkSpecialChannelConstraints(Long channelId, Long lineItemId);

    void uploadChannelReport(MultipartFile file);

    void uploadChannelReport(MultipartFile file, Long accountId);

    byte[] downloadChannelReport(String name);

    byte[] downloadChannelReport(String name, Long accountId);

    List<String> channelReportList();

    List<String> channelReportList(Long accountId);
}
