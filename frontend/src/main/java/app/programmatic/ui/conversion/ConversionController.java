package app.programmatic.ui.conversion;

import org.springframework.beans.factory.annotation.Value;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.tool.TemplateBuilder;
import app.programmatic.ui.conversion.dao.model.Conversion;
import app.programmatic.ui.conversion.service.ConversionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.programmatic.ui.country.service.CountryService;

import java.util.List;


@RestController
public class ConversionController {

    @Value("${conversion.pixel.code}")
    private String pixelCodeTemplate;

    @Autowired
    private CountryService countryService;

    @Autowired
    private ConversionService conversionService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/conversion/stat", produces = "application/json")
    public List<Conversion> getAdvertiserConversions(@RequestParam(value = "accountId") Long accountId) {
        return conversionService.getConversions(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/conversion", produces = "application/json")
    public Conversion getConversion(@RequestParam(value = "conversionId") Long conversionId) {
        return conversionService.find(conversionId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/conversion", produces = "application/json")
    public Long create(@RequestBody Conversion conversion) {
        return conversionService.create(conversion);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/conversion", produces = "application/json")
    public Long update(@RequestBody Conversion conversion) {
        return conversionService.update(conversion);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/conversion/operation", produces = "application/json")
    public MajorDisplayStatus conversionOperation(@RequestParam(value = "operation") StatusOperation operation,
                                                  @RequestParam(value = "conversionId") Long conversionId) {
        return conversionService.changeStatus(conversionId, operation);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/conversion/pixelCode", produces = "application/json")
    public Conversion getPixelCode(@RequestParam(value = "conversionId") Long conversionId) {
        Conversion conversion = conversionService.find(conversionId);
        Long advId = conversion.getConversion().getAccount().getId();
        String domain = countryService.getCountryByAccountId(advId).getConversionDomain();
        if (domain == null) {
            domain = "";
        }
        if(domain.startsWith("http://")) {
            domain = domain.substring(7);
        } else if (domain.startsWith("https://")) {
            domain = domain.substring(8);
        } else if(domain.startsWith("//")) {
            domain = domain.substring(2);
        }
        domain = "//" + domain;

        TemplateBuilder templateBuilder = new TemplateBuilder(pixelCodeTemplate)
                .add("CONVERSION_ID", conversion.getConversion().getId().toString())
                .add("ADVID", advId.toString())
                .add("CONVERSION_DOMAIN", domain);

        conversion.setPixelCode(templateBuilder.generate());
        return conversion;
    }

}
