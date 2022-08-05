package app.programmatic.ui.channel.tool;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import app.programmatic.ui.account.service.SearchAccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.channel.dao.model.Channel;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.channel.view.ExpressionChannelView;
import app.programmatic.ui.common.config.TestConfig;
import app.programmatic.ui.common.foros.service.TestCurUserTokenKeyService;
import app.programmatic.ui.common.testtools.TestEnvironment;
import app.programmatic.ui.common.testtools.TestEnvironmentVariables;
import app.programmatic.ui.common.validation.exception.ExpectedForosViolationsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE, classes = {TestConfig.class})
public class ChannelBuilderTest extends Assert {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private TestCurUserTokenKeyService curUserTokenKeyService;

    @Autowired
    private SearchAccountService searchAccountService;

    @Autowired
    private AuthorizationService authorizationService;

    private TestEnvironmentVariables vars;


    @Before
    public void initialize() {
        vars = TestEnvironment.initialize(curUserTokenKeyService, searchAccountService);
    }

    @Test
    public void testCreateWithEmptyAudience() {
        ExpressionChannelView channelView = createChannelView("testCreateWithEmptyAudience");
        List<List<Channel>> testList = new ArrayList<>();
        testList.add(getEmptyAudience());
        testList.add(getPredefinedAudience());

        channelView.setIncludedChannels(testList);

        ExpressionChannel channel = ChannelBuilder.buildExpressionChannel(channelView);
        channelService.createOrUpdateAsAdmin(channel);
    }

    private ExpressionChannelView createChannelView(String name) {
        ExpressionChannelView channelView = new ExpressionChannelView();

        channelView.setAccountId(vars.getAgencyId());
        channelView.setCountry("GB");
        channelView.setName("ChannelBuilderTest." + name + "." + vars.getTimestamp());

        return channelView;
    }

    private List<Channel> getEmptyAudience() {
        return Collections.emptyList();
    }

    private List<Channel> getPredefinedAudience() {
        List<Channel> result = new ArrayList<>();

        Channel expChannel = new Channel();
        expChannel.setId(5893125l);
        result.add(expChannel);

        return result;
    }

    private List<Channel> getPredefinedInternalAudience() {
        List<Channel> result = new ArrayList<>();

        Channel expChannel = new Channel();
        expChannel.setId(5893095l);
        result.add(expChannel);

        return result;
    }

    @Test
    public void testExternalDefaultVisibility() {
        // Default Visibility
        ExpressionChannelView channelView = createChannelView("testExternalVisibilityPRI");
        List<List<Channel>> testList = new ArrayList<>();
        testList.add(getPredefinedAudience());
        channelView.setIncludedChannels(testList);

        ExpressionChannel channel = ChannelBuilder.buildExpressionChannel(channelView);
        Long channelId = channelService.createOrUpdateAsAdmin(channel);

        channel = channelService.findExpressionUnchecked(channelId);
        assertEquals("PRI", channel.getVisibility());;
    }

    @Test
    public void testExternalPubVisibility() {
        // PUB Visibility
        ExpressionChannelView channelView = createChannelView("testExternalVisibilityPUB");
        List<List<Channel>> testList = new ArrayList<>();
        testList.add(getPredefinedAudience());
        channelView.setIncludedChannels(testList);
        channelView.setVisibility("PUB");
        ExpressionChannel channel2 = ChannelBuilder.buildExpressionChannel(channelView);

        try {
            channelService.createOrUpdateAsAdmin(channel2);
            assertTrue("Agency channels can't be public", false);
        } catch (ExpectedForosViolationsException e) {
        }
    }

    @Test
    public void testInternalDefaultVisibility() {
        // Default Visibility
        ExpressionChannelView channelView = createChannelView("testInternalVisibilityPRI");
        List<List<Channel>> testList = new ArrayList<>();
        testList.add(getPredefinedInternalAudience());
        channelView.setIncludedChannels(testList);
        channelView.setAccountId(authorizationService.getAuthUser().getAccountId());

        ExpressionChannel channel = ChannelBuilder.buildExpressionChannel(channelView);
        Long channelId = channelService.createOrUpdateAsAdmin(channel);

        channel = channelService.findExpressionUnchecked(channelId);
        assertEquals("PRI", channel.getVisibility());;
    }

    @Test
    public void testInternalPubVisibility() {
        // Default Visibility
        ExpressionChannelView channelView = createChannelView("testInternalVisibilityPUB");
        List<List<Channel>> testList = new ArrayList<>();
        testList.add(getPredefinedInternalAudience());
        channelView.setIncludedChannels(testList);
        channelView.setAccountId(authorizationService.getAuthUser().getAccountId());
        channelView.setVisibility("PUB");

        ExpressionChannel channel = ChannelBuilder.buildExpressionChannel(channelView);
        Long channelId = channelService.createOrUpdateAsAdmin(channel);

        channel = channelService.findExpressionUnchecked(channelId);
        assertEquals("PUB", channel.getVisibility());;
    }
}
