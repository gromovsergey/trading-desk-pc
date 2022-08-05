package app.programmatic.ui.localization.service;

import static app.programmatic.ui.localization.validation.LocalizationValidateMethod.DELETE;
import static app.programmatic.ui.localization.validation.LocalizationValidateMethod.UPDATE;

import app.programmatic.ui.creative.dao.model.Creative;
import app.programmatic.ui.creative.dao.model.CreativeCategory;
import app.programmatic.ui.creative.dao.model.CreativeSizeStat;
import app.programmatic.ui.creative.dao.model.CreativeStat;
import app.programmatic.ui.creative.dao.model.CreativeTemplate;
import app.programmatic.ui.creative.dao.model.CreativeTemplateStat;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkStat;

import com.foros.rs.client.model.advertising.template.CreativeSize;
import app.programmatic.ui.localization.dao.model.Localization;
import app.programmatic.ui.localization.validation.ValidateLocalization;

import java.util.List;
import java.util.Map;

public interface LocalizationService {

    List<Localization> findChannelLocalizationsById(Long id);

    Map<Long, String> findRuChannelLocalizationsByIds(Long... ids);

    void updateLocalizations(@ValidateLocalization(UPDATE) List<Localization> localizations);

    void updateLocalizationsUnrestricted(@ValidateLocalization(UPDATE) List<Localization> localizations);

    void deleteLocalizations(@ValidateLocalization(DELETE) List<Localization> localizations);

    Creative processCreative(Creative creative);

    List<CreativeStat> processCreativesStats(List<CreativeStat> creativesStats);

    List<CreativeLinkStat> processCreativeLinksStats(List<CreativeLinkStat> creativeLinkStats);

    CreativeTemplate processTemplate(CreativeTemplate template);

    List<CreativeTemplateStat> processTemplatesStats(List<CreativeTemplateStat> templatesStats);

    CreativeSize processSize(CreativeSize size);

    List<CreativeSizeStat> processSizesStats(List<CreativeSizeStat> sizesStats);

    List<CreativeCategory> processCategories(List<CreativeCategory> categories);
}
