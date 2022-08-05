package app.programmatic.ui.creative.tool;

import com.foros.rs.client.model.advertising.template.OptionGroup;
import com.foros.rs.client.model.advertising.template.OptionGroupType;
import app.programmatic.ui.creative.dao.model.CreativeTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreativeTemplateBuilder {
    public static CreativeTemplate buildFromForosTemplate(com.foros.rs.client.model.advertising.template.CreativeTemplate src) {
        CreativeTemplate result = new CreativeTemplate();

        result.setId(src.getId());
        result.setName(src.getDefaultName());
        result.setExpandable(src.getExpandable());
        result.setOptionGroups(filterOptionGroups(src.getOptionGroups()));

        return result;
    }

    public static List<OptionGroup> filterOptionGroups(List<OptionGroup> src) {
        if (src == null || src.isEmpty()) {
            return Collections.emptyList();
        }
        return src.stream()
                .filter( group -> group.getType() == OptionGroupType.Advertiser )
                .collect(Collectors.toList());
    }
}
