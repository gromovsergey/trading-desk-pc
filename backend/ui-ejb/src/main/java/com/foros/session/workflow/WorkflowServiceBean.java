package com.foros.session.workflow;

import com.foros.model.Approvable;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.Statusable;
import com.foros.model.security.User;
import com.foros.model.template.Template;
import com.foros.session.StatusAction;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.status.ApprovalAction;
import com.foros.util.workflow.WorkflowScheme;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "WorkflowService")
public class WorkflowServiceBean implements WorkflowService {

    @EJB
    private AdvertiserEntityRestrictions restrictionsService;


    WorkflowScheme<ApproveStatus, ApprovalAction> standardApprovalWS = new WorkflowScheme<ApproveStatus, ApprovalAction>(
            new ApproveStatus[]{ApproveStatus.APPROVED, ApproveStatus.DECLINED, ApproveStatus.HOLD},
            new Object[][]{
                    {ApproveStatus.HOLD, ApprovalAction.APPROVE, ApproveStatus.APPROVED},
                    {ApproveStatus.HOLD, ApprovalAction.DECLINE, ApproveStatus.DECLINED},
                    {ApproveStatus.APPROVED, ApprovalAction.DECLINE, ApproveStatus.DECLINED},
                    {ApproveStatus.DECLINED, ApprovalAction.APPROVE, ApproveStatus.APPROVED}
            });

    WorkflowScheme<Status, StatusAction> commonStatusWS = new WorkflowScheme<Status, StatusAction>(
            new Status[]{Status.ACTIVE, Status.INACTIVE,Status.DELETED},
            new Object[][]{
                    {Status.ACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.ACTIVE, StatusAction.INACTIVATE, Status.INACTIVE},
                    {Status.INACTIVE, StatusAction.ACTIVATE, Status.ACTIVE},
                    {Status.INACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.DELETED, StatusAction.UNDELETE, Status.INACTIVE},
            });

    WorkflowScheme<Status, StatusAction> commonStatusWSWithPending = new WorkflowScheme<Status, StatusAction>(
            new Status[]{Status.ACTIVE, Status.INACTIVE, Status.PENDING, Status.DELETED},
            new Object[][]{
                    {Status.ACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.ACTIVE, StatusAction.INACTIVATE, Status.INACTIVE},
                    {Status.INACTIVE, StatusAction.ACTIVATE, Status.ACTIVE},
                    {Status.INACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.DELETED, StatusAction.UNDELETE, Status.INACTIVE},
                    {Status.PENDING, StatusAction.ACTIVATE, Status.ACTIVE},
                    {Status.PENDING, StatusAction.INACTIVATE, Status.INACTIVE},
                    {Status.PENDING, StatusAction.DELETE, Status.DELETED}
            });
    WorkflowScheme<Status, StatusAction> commonStatusWSWithPendingForInternal = new WorkflowScheme<Status, StatusAction>(
            new Status[]{Status.ACTIVE, Status.INACTIVE, Status.PENDING, Status.DELETED},
            new Object[][]{
                    {Status.ACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.ACTIVE, StatusAction.INACTIVATE, Status.INACTIVE},
                    // Commented for now. See OUI-25720
//                    {Status.INACTIVE, StatusAction.ACTIVATE, Status.PENDING},//this is the difference with non-internal workflow (commonStatusWSWithPending)
                    {Status.INACTIVE, StatusAction.ACTIVATE, Status.ACTIVE},
                    {Status.INACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.DELETED, StatusAction.UNDELETE, Status.INACTIVE},
                    {Status.PENDING, StatusAction.ACTIVATE, Status.ACTIVE},
                    {Status.PENDING, StatusAction.INACTIVATE, Status.INACTIVE},
                    {Status.PENDING, StatusAction.DELETE, Status.DELETED}

            });

    WorkflowScheme<Status, StatusAction> commonStatusWSWithPendingForInternalNoActivate = new WorkflowScheme<Status, StatusAction>(
            new Status[]{Status.ACTIVE, Status.INACTIVE, Status.PENDING, Status.DELETED},
            new Object[][]{
                    {Status.ACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.ACTIVE, StatusAction.INACTIVATE, Status.INACTIVE},
                    // Commented for now. See OUI-25720
//                    {Status.INACTIVE, StatusAction.ACTIVATE, Status.PENDING},//this is the difference with non-internal workflow (commonStatusWSWithPending)
                    {Status.INACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.DELETED, StatusAction.UNDELETE, Status.INACTIVE},
                    {Status.PENDING, StatusAction.INACTIVATE, Status.INACTIVE},
                    {Status.PENDING, StatusAction.DELETE, Status.DELETED}

            });

    WorkflowScheme<Status, StatusAction> cmpChannelStatusWS = new WorkflowScheme<Status, StatusAction>(
            new Status[]{Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION},
            new Object[][]{
                    {Status.ACTIVE, StatusAction.INACTIVATE, Status.PENDING_INACTIVATION},
                    {Status.INACTIVE, StatusAction.ACTIVATE, Status.ACTIVE},
                    {Status.INACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.DELETED, StatusAction.UNDELETE, Status.INACTIVE},
                    {Status.PENDING_INACTIVATION, StatusAction.ACTIVATE, Status.ACTIVE},
            });

    WorkflowScheme<Status, StatusAction> basicStatusWS = new WorkflowScheme<Status, StatusAction>(
            new Status[]{Status.ACTIVE, Status.DELETED},
            new Object[][]{
                    {Status.ACTIVE, StatusAction.DELETE, Status.DELETED},
                    {Status.DELETED, StatusAction.UNDELETE, Status.ACTIVE},
            });


    private WorkflowMatcher<Status, StatusAction> statusMatcher =
            root(basicStatusWS)
                .nested(
                    byClass(commonStatusWSWithPending, BehavioralChannel.class, ExpressionChannel.class, AudienceChannel.class)
                        .nested(new CmpChannelMatcher<Status, StatusAction>(cmpChannelStatusWS)),
                    byClass(commonStatusWSWithPending, DiscoverChannel.class, DiscoverChannelList.class, CategoryChannel.class),
                    byClass(commonStatusWS, Account.class, User.class),
                    byClass(commonStatusWS, CampaignCreative.class, Campaign.class, CCGKeyword.class),
                    byClass(commonStatusWS, DeviceChannel.class),
                    byClass(basicStatusWS, Template.class, CreativeSize.class),
                    byClass(commonStatusWSWithPending, Creative.class, CampaignCreativeGroup.class)
                        .nested(new InternalUserWorkflowMatcher<Status, StatusAction>(commonStatusWSWithPendingForInternal)
                            .nested(new InternalUserWithoutActivateWorkflowMatcher<Status, StatusAction>(commonStatusWSWithPendingForInternalNoActivate)))


                );

    private WorkflowMatcher<ApproveStatus, ApprovalAction> approvalMatcher =
        root(standardApprovalWS);

    @Override
    public ApprovalWorkflow getApprovalWorkflow(Approvable approvable) {
        return new ApprovalWorkflow(approvalMatcher.findMatched(approvable), approvable.getQaStatus());
    }

    @Override
    public StatusWorkflow getStatusWorkflow(Statusable statusable) {
        return new StatusWorkflow(statusMatcher.findMatched(statusable), statusable.getStatus());
    }

    public <S, A> RootWorkflowMatcher<S, A> root(WorkflowScheme<S, A> scheme) {
        return new RootWorkflowMatcher<S, A>(scheme);
    }

    public <S, A> ClassMatcher<S, A> byClass(WorkflowScheme<S, A> scheme, Class... classes) {
        return new ClassMatcher<S, A>(scheme, classes);
    }

    private class InternalUserWithoutActivateWorkflowMatcher<S,A> extends BaseWorkflowMatcher<S,A> {

        public InternalUserWithoutActivateWorkflowMatcher(WorkflowScheme<S, A> scheme) {
            super(scheme);
        }

        @Override
        protected boolean isThisMatched(Object entity) {
            return !restrictionsService.canActivatePending();
        }

    }
}
