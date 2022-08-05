package app.programmatic.ui.localization.service;

import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.CATEGORY;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.CHANNEL;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.OPTION_GROUP_LABEL;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.OPTION_GROUP_NAME;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.OPTION_LABEL;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.OPTION_NAME;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.SIZE;
import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.TEMPLATE;

import com.foros.rs.client.model.advertising.template.CreativeSize;
import com.foros.rs.client.model.advertising.template.Option;
import com.foros.rs.client.model.advertising.template.OptionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.creative.dao.model.Creative;
import app.programmatic.ui.creative.dao.model.CreativeCategory;
import app.programmatic.ui.creative.dao.model.CreativeSizeStat;
import app.programmatic.ui.creative.dao.model.CreativeStat;
import app.programmatic.ui.creative.dao.model.CreativeTemplate;
import app.programmatic.ui.creative.dao.model.CreativeTemplateStat;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkStat;
import app.programmatic.ui.localization.dao.LocalizationRepository;
import app.programmatic.ui.localization.dao.model.Localization;
import app.programmatic.ui.localization.dao.model.LocalizationLanguage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class LocalizationServiceImpl implements LocalizationService {

    @Autowired
    private LocalizationRepository localizationRepository;


    @Override
    public List<Localization> findChannelLocalizationsById(Long id) {
        return localizationRepository.findByKey(CHANNEL.getPrefix() + id);
    }

    @Override
    public Map<Long, String> findRuChannelLocalizationsByIds(Long... ids) {
        Map<String, Long> keysMap = Arrays.asList(ids).stream()
                .collect(Collectors.toMap(id -> CHANNEL.getPrefix() + id, id -> id));

        return localizationRepository.findByLangAndKeyIn(LocalizationLanguage.ru, keysMap.keySet()).stream()
                .collect(Collectors.toMap(r -> keysMap.get(r.getKey()), r -> r.getValue()));
    }

    @Override
    @Restrict(restriction = "localization.update")
    public void updateLocalizations(List<Localization> localizations) {
        updateLocalizationsUnrestricted(localizations);
    }

    @Override
    public void updateLocalizationsUnrestricted(List<Localization> localizations) {
        localizationRepository.saveAll(localizations);
    }

    @Override
    @Restrict(restriction = "localization.update")
    public void deleteLocalizations(List<Localization> localizations) {
        localizationRepository.deleteAll(localizations);
    }

    @Override
    public Creative processCreative(Creative creative) {
        ArrayList<String> keys = new ArrayList<>();

        String templateKey = addTemplateKey(creative.getTemplateId(), keys);
        String sizeKey = addSizeKey(creative.getSizeId(), keys);
        addCategoriesKeys(creative.getContentCategories(), keys);
        addCategoriesKeys(creative.getVisualCategories(), keys);

        Map<String, String> localizedByKeys = fetchLocalizedNames(keys);

        creative.setTemplateName(getLocalized(localizedByKeys, templateKey, creative.getTemplateName()));
        creative.setSizeName(getLocalized(localizedByKeys, sizeKey, creative.getSizeName()));
        updateCategoriesNames(creative.getContentCategories(), localizedByKeys);
        updateCategoriesNames(creative.getVisualCategories(), localizedByKeys);

        return creative;
    }

    @Override
    public List<CreativeStat> processCreativesStats(List<CreativeStat> creativesStats) {
        HashSet<String> keys = new HashSet<>();

        for (CreativeStat stat: creativesStats) {
            addTemplateKey(stat.getTemplateId(), keys);
            addSizeKey(stat.getSizeId(), keys);
        }

        Map<String, String> localizedByKeys = fetchLocalizedNames(new ArrayList(keys));

        for (CreativeStat stat: creativesStats) {
            stat.setTemplateName(getLocalized(localizedByKeys, TEMPLATE.getPrefix() + stat.getTemplateId(), stat.getTemplateName()));
            stat.setSizeName(getLocalized(localizedByKeys, SIZE.getPrefix() + stat.getSizeId(), stat.getSizeName()));
        }

        return creativesStats;
    }

    @Override
    public List<CreativeLinkStat> processCreativeLinksStats(List<CreativeLinkStat> creativeLinkStats) {
        HashSet<String> keys = new HashSet<>();

        for (CreativeLinkStat stat: creativeLinkStats) {
            addTemplateKey(stat.getTemplateId(), keys);
            addSizeKey(stat.getSizeId(), keys);
        }

        Map<String, String> localizedByKeys = fetchLocalizedNames(new ArrayList(keys));

        for (CreativeLinkStat stat: creativeLinkStats) {
            stat.setTemplateName(getLocalized(localizedByKeys, TEMPLATE.getPrefix() + stat.getTemplateId(), stat.getTemplateName()));
            stat.setSizeName(getLocalized(localizedByKeys, SIZE.getPrefix() + stat.getSizeId(), stat.getSizeName()));
        }

        return creativeLinkStats;
    }

    @Override
    public CreativeTemplate processTemplate(CreativeTemplate template) {
        ArrayList<String> keys = new ArrayList<>();

        String templateKey = addTemplateKey(template.getId(), keys);
        addCategoriesKeys(template.getVisualCategories(), keys);
        addOptionGroupsKeys(template.getOptionGroups(), keys);

        Map<String, String> localizedByKeys = fetchLocalizedNames(keys);

        template.setName(getLocalized(localizedByKeys, templateKey, template.getName()));
        updateCategoriesNames(template.getVisualCategories(), localizedByKeys);
        updateOptionGroupsNames(template.getOptionGroups(), localizedByKeys);

        return template;
    }

    @Override
    public List<CreativeTemplateStat> processTemplatesStats(List<CreativeTemplateStat> templatesStats) {
        ArrayList<String> keys = new ArrayList<>();

        templatesStats.stream().forEach( t -> addTemplateKey(t.getId(), keys) );
        Map<String, String> localizedByKeys = fetchLocalizedNames(keys);
        templatesStats.stream().forEach( t -> t.setName(getLocalized(localizedByKeys, TEMPLATE.getPrefix() + t.getId(), t.getName())) );

        return templatesStats;
    }

    @Override
    public CreativeSize processSize(CreativeSize size) {
        ArrayList<String> keys = new ArrayList<>();

        String sizeKey = addSizeKey(size.getId(), keys);
        addOptionGroupsKeys(size.getOptionGroups(), keys);

        Map<String, String> localizedByKeys = fetchLocalizedNames(keys);

        size.setDefaultName(getLocalized(localizedByKeys, sizeKey, size.getDefaultName()));
        updateOptionGroupsNames(size.getOptionGroups(), localizedByKeys);

        return size;
    }

    @Override
    public List<CreativeSizeStat> processSizesStats(List<CreativeSizeStat> sizesStats) {
        ArrayList<String> keys = new ArrayList<>();

        sizesStats.stream().forEach( s -> addSizeKey(s.getId(), keys) );
        Map<String, String> localizedByKeys = fetchLocalizedNames(keys);
        sizesStats.stream().forEach( s -> s.setName(getLocalized(localizedByKeys, SIZE.getPrefix() + s.getId(), s.getName())) );

        return sizesStats;
    }

    @Override
    public List<CreativeCategory> processCategories(List<CreativeCategory> categories) {
        ArrayList<String> keys = new ArrayList<>();
        addCategoriesKeys(categories, keys);
        updateCategoriesNames(categories, fetchLocalizedNames(keys));
        return categories;
    }

    private static String addTemplateKey(Long templateId, Collection<String> keys) {
        String templateKey = TEMPLATE.getPrefix() + templateId;
        keys.add(templateKey);
        return templateKey;
    }

    private static String addSizeKey(Long sizeId, Collection<String> keys) {
        String sizeKey = SIZE.getPrefix() + sizeId;
        keys.add(sizeKey);
        return sizeKey;
    }

    private static void addCategoriesKeys(List<CreativeCategory> categories, ArrayList<String> keys) {
        categories.stream()
                .forEach( c -> keys.add(CATEGORY.getPrefix() + c.getId()));
    }

    private static void addOptionGroupsKeys(List<OptionGroup> groups, ArrayList<String> keys) {
        groups.stream().forEach( g -> {
            keys.add(OPTION_GROUP_NAME.getPrefix() + g.getId());
            keys.add(OPTION_GROUP_LABEL.getPrefix() + g.getId());

            addOptionsKeys(g.getOptions(), keys);
        });
    }

    private static void addOptionsKeys(List<Option> options, ArrayList<String> keys) {
        options.stream().forEach( o -> {
            keys.add(OPTION_NAME.getPrefix() + o.getId());
            keys.add(OPTION_LABEL.getPrefix() + o.getId());
        });
    }

    private static void updateCategoriesNames(List<CreativeCategory> categories, Map<String, String> localizedByKeys) {
        categories.stream()
            .forEach( c -> c.setName(getLocalized(localizedByKeys, CATEGORY.getPrefix() + c.getId(), c.getName())));
    }

    private static void updateOptionGroupsNames(List<OptionGroup> groups, Map<String, String> localizedByKeys) {
        groups.stream()
            .forEach( g -> {
                g.setDefaultName(getLocalized(localizedByKeys, OPTION_GROUP_NAME.getPrefix() + g.getId(), g.getDefaultName()));
                g.setDefaultLabel(getLocalized(localizedByKeys, OPTION_GROUP_LABEL.getPrefix() + g.getId(), g.getDefaultLabel()));

                updateOptionsNames(g.getOptions(), localizedByKeys);
            });
    }

    private static void updateOptionsNames(List<Option> options, Map<String, String> localizedByKeys) {
        options.stream()
            .forEach( o -> {
                o.setDefaultName(getLocalized(localizedByKeys, OPTION_NAME.getPrefix() + o.getId(), o.getDefaultName()));
                o.setDefaultLabel(getLocalized(localizedByKeys, OPTION_LABEL.getPrefix() + o.getId(), o.getDefaultLabel()));
            });
    }

    private static String getLocalized(Map<String, String> values, String key, String defaultValue) {
        String localizedValue = values.get(key);
        if (localizedValue != null) {
            return localizedValue;
        }

        return defaultValue;
    }

    private Map<String, String> fetchLocalizedNames(List<String> keys) {
        List<Localization> resources = localizationRepository.findByLangAndKeyIn(LocalizationLanguage.ru, keys);
        return resources.stream()
                .collect(Collectors.toMap( r -> r.getKey(), r -> r.getValue() ));
    }
}
