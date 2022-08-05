package com.foros.util;

import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.TemplateFile;
import com.foros.util.bean.Filter;

/**
 * Copy functionality filter
 *
 * @author: mahendra.bhikyagolu
 * Date: Nov 20, 2008
 */
public class CopyFilter implements Filter<Object> {

    public boolean accept(Object element) {
        //Check DELETED status for classes extending StatusEntityBase
        if (element instanceof StatusEntityBase) {
            StatusEntityBase statusEntity = (StatusEntityBase) element;
            //Different check for Display and Text Campaign Creative
            if (element instanceof CampaignCreative) {
                CampaignCreative campaignCreative = (CampaignCreative) element;

                if (Status.DELETED == campaignCreative.getCreative().getStatus()) {
                    return false;
                }
            }

            if (Status.DELETED == statusEntity.getStatus()) {
                return false;
            }
        } else if (element instanceof CCGKeyword) {
            // Since CCGKeyword is an exception where it is not of type StatusEntityBase.
            //Check DELETED status for CCGKeyword class

            if (Status.DELETED == ((CCGKeyword) element).getStatus()) {
                return false;
            }

        } else if (element instanceof TemplateFile) {
            CreativeSize cs = ((TemplateFile) element).getCreativeSize();
            if (cs != null && cs.getStatus() == Status.DELETED) {
                return false;
            }
        }

        return true;
    }
}
