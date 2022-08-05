package app.programmatic.ui.creativelink.service;

import com.foros.rs.client.model.advertising.campaign.CreativeLink;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkOperation;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkStat;

import java.util.List;


public interface CreativeLinkService {

    List<CreativeLink> findByCcgId(Long ccgId);

    void createOrUpdate(List<CreativeLink> links);

    List<CreativeLinkStat> getStatsByCcgId(Long ccgId);

    MajorDisplayStatus changeStatusByCreativeId(Long ccgId, Long creativeId, CreativeLinkOperation operation);
}
