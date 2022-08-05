package com.foros.session.template;

import com.foros.model.security.AccountType;
import com.foros.model.site.Tag;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.ImpressionInfo;
import com.foros.model.template.Option;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateTO;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

@Local
public interface TemplateService {

    void create(CreativeTemplate template);
    CreativeTemplate update(CreativeTemplate template);

    void create(DiscoverTemplate template);
    DiscoverTemplate update(DiscoverTemplate template);

    Template createCopy(Long id);

    Template findById(Long id);
    Template view(Long id);

    List<Template> findAll();
    List<TemplateTO> findAllCreativeTemplates();
    List<TemplateTO> findAllDiscoverTemplates();
    List<TemplateTO> findAllNonDeletedCreativeTemplates();
    List<TemplateTO> findAllNonDeletedDiscoverTemplates();

    Template delete(Long id);

    Template undelete(Long id);

    List<Template> findByAccountType(AccountType accType);

    List<Long> findIdsByDefaultNames(Collection<String> names);

    void createTemplateFile(TemplateFile tmplFile);

    TemplateFile updateTemplateFile(TemplateFile tmplFile);

    void deleteTemplateFile(Long tempalateFileId);

    TemplateFile findTemplateFileById(Long id);

    CreativeTemplate findTextTemplate();

    Long findTextTemplateId();

    List<TemplateTO> findAvailableCreativeTemplates(Long accountTypeId);

    List<TemplateTO> findAvailableDiscoverTemplates(Long accountTypeId);

    /**
     * Finds list of Creative Templates for the given Creative Size Id
     * and Application Format as "js" & "html" as CreativeTemplates
     * should be linked with CreativeSizes for js & html application formats
     *
     * @param accountTypeId
     * @param sizeId
     * @return List of templates
     */
    List<Template> findJsHTMLTemplatesBySize(Long accountTypeId, Long sizeId);

    /**
     * Finds whether there is any Creatives associated to the given
     * combination of CreativeTemplate and CreativeSize
     *
     * @param templateFile
     * @return
     */
    boolean isCreativeSizeLinkedToCreatives(TemplateFile templateFile);

    /**
     * Finds whether there is any Creatives associated to the given CreativeTemplateId
     *
     * @param creativeTemplateId
     * @return
     */
    boolean isCreativeTemplateLinkedToCreatives(Long creativeTemplateId);

    /**
     * Finds whether the given Creative Size & Creative Template is related
     * for the given accountId for 'js' & 'html' application Formats
     *
     * @param accountId
     * @param creativeTemplateId
     * @param sizeId
     * @return
     */
    boolean isTemplateLinkedToCreativeSize(Long accountId, Long creativeTemplateId, Long sizeId);

    void generatePreview(Long templateId, Long sizeId, String applicationFormat, ImpressionInfo impressionInfo, OutputStream output);

    long countExpandableCreatives(Long templateId);

    List<CreativeTemplate> findTemplatesWithPublisherOptions(Tag tag);

    Option findTextOptionFromTextTemplate(Long id);
}
