package app.programmatic.ui.account;

import app.programmatic.ui.account.dao.model.*;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.account.service.SearchAccountService;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.common.tool.foros.StatusHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.programmatic.ui.user.service.UserService;

import java.math.BigDecimal;
import java.net.URLConnection;
import java.util.List;


@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SearchAccountService searchAccountService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertising", produces = "application/json")
    public AdvertisingAccount findAdvertisingAccount(@RequestParam(value = "accountId") Long accountId) {
        return accountService.findAdvertising(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertising/availableBudget", produces = "application/json")
    public String getAdvertisingAccountAvailableBudget(@RequestParam(value = "accountId") Long accountId) {
        BigDecimal result = accountService.getAccountAvailableBudget(accountId);
        return result == null ? "null" : result.toString();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertisers", produces = "application/json")
    public List<AdvertisingAccount> findAdvertisers(@RequestParam(value = "userId", required = false) Long userId,
                                                    @RequestParam(value = "accountId", required = false) Long accountId)
                                                    throws MissingServletRequestParameterException {
        if (accountId == null && userId == null) {
            throw new MissingServletRequestParameterException("userId or accountId", "long");
        }

        if (userId != null) {
            accountId = userService.findUnrestricted(userId).getAccountId();
        }
        return accountService.findAdvertisersByAgency(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/channelOwners", produces = "application/json")
    public List<IdName> findAllChannelOwners() {
        return accountService.findAllChannelOwners();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/internal/list", produces = "application/json")
    public List<IdName> findInternalAccounts() {
        return accountService.findInternalAccounts();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/publisher", produces = "application/json")
    public PublisherAccount findPublisherAccount(@RequestParam(value = "accountId") Long accountId) {
        return accountService.findPublisherUnchecked(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/publisher/list", produces = "application/json")
    public List<IdName> findPublishers() {
        return accountService.findPublishers();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/publisher/list/referrer", produces = "application/json")
    public List<IdName> findPublishersForReferrerReport() {
        return accountService.findPublishersForReferrerReport();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertising/list", produces = "application/json")
    public List<AccountEntity> findAdvertisingList() {
        return accountService.findAdvertisingList();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertising/search/params", produces = "application/json")
    public AdditionalSearchParams searchAdvertisingAccounts() {
        return searchAccountService.getAdditionalSearchParams();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertising/search", produces = "application/json")
    public List<AccountStats> searchAdvertisingAccounts(@RequestParam(value = "name") String name,
                                                        @RequestParam(value = "displayStatuses") AccountDisplayStatusParam displayStatusesParam,
                                                        @RequestParam(value = "country", required = false) String countryCode,
                                                        @RequestParam(value = "accountRole") AccountRoleParam accountRole) {
        return searchAccountService.searchAdvertisingAccounts(name, countryCode, displayStatusesParam.getDisplayStatuses(), accountRole);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/advertising/searchInAgency", produces = "application/json")
    public List<AdvertiserInAgencyStats> searchInAgencyAdvertisingAccounts(@RequestParam(value = "agencyId") Long agencyId) {
        return searchAccountService.searchInAgencyAdvertisingAccounts(agencyId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/account/advertising", produces = "application/json")
    public Long createAdvertiserInAgency(@RequestBody AdvertisingAccount advertiserInAgency) {
        return accountService.createAdvertiserInAgency(advertiserInAgency);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/account/advertising", produces = "application/json")
    public Long updateAdvertiserInAgency(@RequestBody AdvertisingAccount advertiserInAgency) {
        return accountService.updateAdvertiserInAgency(advertiserInAgency).getId();
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/account/advertising/status", produces = "application/json")
    public AdvertisingAccount updateStatusAdvertiserInAgency(@RequestParam(value = "accountId") Long accountId,
                                                             @RequestParam(value = "operation") String operation) {
        AdvertisingAccount currentAdv = accountService.findAdvertising(accountId);
        currentAdv.setDisplayStatus(StatusHelper.getMajorStatusByOperation(operation).name());

        return accountService.updateAdvertiserInAgency(currentAdv);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/documentsList", produces = "application/json")
    public List<String> listDocuments(@RequestParam(value = "accountId") Long accountId) {
        return accountService.listDocuments(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/checkDocuments", produces = "application/json")
    public Boolean checkDocuments(@RequestParam(value = "accountId") Long accountId) {
        return accountService.checkDocuments(accountId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/account/uploadDocument")
    public void uploadDocument(@RequestParam(value = "accountId") Long accountId,
                               @RequestParam("file") MultipartFile file) {
        accountService.uploadDocument(file, accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/account/downloadDocument")
    public ResponseEntity downloadDocument(@RequestParam(value = "accountId") Long accountId,
                                           @RequestParam(value = "name") String name) {
        byte[] contents = accountService.downloadDocument(name, accountId);

        HttpHeaders headers = new HttpHeaders();
        String mimeType = URLConnection.guessContentTypeFromName(name);
        if (mimeType != null) {
            headers.setContentType(MediaType.valueOf(mimeType));
        }
        headers.setContentLength(contents.length);
        return new ResponseEntity<>(
                contents,
                headers,
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rest/account/document", produces = "application/json")
    public void deleteDocument(@RequestParam(value = "accountId") Long accountId,
                               @RequestParam(value = "name") String name) {
        accountService.deleteDocument(name, accountId);
    }
}
