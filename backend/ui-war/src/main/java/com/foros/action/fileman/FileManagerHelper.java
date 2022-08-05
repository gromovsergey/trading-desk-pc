package com.foros.action.fileman;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.fileman.FileManager;
import com.foros.session.fileman.FileManagerUIService;

public class FileManagerHelper {
    public static final String MODE_CREATIVE = "creative";
    public static final String MODE_TEXT_AD = "textAd";
    public static final String MODE_TEMPLATE = "template";
    public static final String MODE_PUBLISHER_ACCOUNT = "publisher";
    public static final String MODE_KWMTOOL = "kwmtool";

    private FileManagerHelper() {

    }

    public static FileManager getFileManager(String mode, Long accountId)  {
        FileManager fileManager;

        AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
        FileManagerUIService fileManagerUIService = ServiceLocator.getInstance().lookup(FileManagerUIService.class);

        if (MODE_CREATIVE.equals(mode)) {
            // Account folder mode
            AdvertiserAccount account = accountService.findAdvertiserAccount(accountId);
            fileManager = accountService.getCreativesFileManager(account);
        } else if (MODE_TEXT_AD.equals(mode)) {
            AdvertiserAccount account = accountService.findAdvertiserAccount(accountId);
            fileManager = accountService.getTextAdImagesFileManager(account);
        } else if (MODE_TEMPLATE.equals(mode)) {
            // Templates mode
            fileManager = fileManagerUIService.getTemplatesFileManager();
        } else if (MODE_PUBLISHER_ACCOUNT.equals(mode)) {
            // Publisher account mode (WD tags)
            Account account = accountService.view(accountId);
            fileManager = accountService.getPublisherAccountFileManager(account);
        } else if (MODE_KWMTOOL.equals(mode)) {
            // KWM Tool mode
            fileManager = fileManagerUIService.getKwmToolFileManager();
        } else {
            // Admin mode
            fileManager = fileManagerUIService.getRootFileManager();
        }

        return fileManager;
    }

}
