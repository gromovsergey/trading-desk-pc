package app.programmatic.ui.conversion.service;

import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE_SELECTOR;

import com.foros.rs.client.model.advertising.conversion.ConversionSelector;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.common.foros.service.ForosConversionService;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.conversion.dao.model.Conversion;
import app.programmatic.ui.conversion.dao.model.ConversionDisplayStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ConversionServiceImpl implements ConversionService {

    @Autowired
    private ForosConversionService forosService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Override
    public List<Conversion> getConversions(Long accountId) {
        ConversionSelector conversionSelector = selector();
        conversionSelector.setAdvertiserIds(Arrays.asList(accountId));
        conversionSelector.setConversionStatuses(Arrays.asList(Status.ACTIVE));

        List<com.foros.rs.client.model.advertising.conversion.Conversion> conversionList =
                forosService.getConversionService().get(conversionSelector).getEntities();

        return conversionList.stream()
                .map(forosConversion -> new Conversion(forosConversion))
                .collect(Collectors.toList());
    }

    @Override
    public Conversion find(Long conversionId) {
        ConversionSelector conversionSelector = selector();
        conversionSelector.setConversionIds(Arrays.asList(conversionId));

        List<com.foros.rs.client.model.advertising.conversion.Conversion> result =
                forosService.getConversionService().get(conversionSelector).getEntities();
        if (result.isEmpty()) {
            throw new EntityNotFoundException(conversionId);
        }

        return new Conversion(result.get(0));
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.conversion.validation.ForosConversionViolationsServiceImpl")
    public Long create(Conversion conversion) {
        conversion.getConversion().setStatus(null);
        return createOrUpdate(conversion.getConversion());
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.conversion.validation.ForosConversionViolationsServiceImpl")
    public Long update(Conversion conversion) {
        conversion.getConversion().setStatus(null);
        return createOrUpdate(conversion.getConversion());
    }

    @Override
    public MajorDisplayStatus changeStatus(Long conversionId, StatusOperation operation) {
        com.foros.rs.client.model.advertising.conversion.Conversion conversion =
                new com.foros.rs.client.model.advertising.conversion.Conversion();
        conversion.setId(conversionId);
        ForosHelper.changeEntityStatus(conversion, ForosHelper.isChangeStatusOperation(operation));
        createOrUpdate(conversion);
        return findConversionStatus(conversionId);
    }

    private ConversionSelector selector() {
        ConversionSelector conversionSelector = new ConversionSelector();
        conversionSelector.setPaging(MAX_RESULTS_SIZE_SELECTOR);
        return conversionSelector;
    }

    private Long createOrUpdate(com.foros.rs.client.model.advertising.conversion.Conversion conversion) {
        Operation<com.foros.rs.client.model.advertising.conversion.Conversion> operation = new Operation<>();
        operation.setType(conversion.getId() == null ? OperationType.CREATE : OperationType.UPDATE);
        operation.setEntity(conversion);

        Operations<com.foros.rs.client.model.advertising.conversion.Conversion> operations = new Operations<>();
        operations.setOperations(Arrays.asList(operation));

        return forosService.getConversionService().perform(operations).getIds().get(0);
    }

    private MajorDisplayStatus findConversionStatus(Long conversionId) {
        Integer displayStatusId = jdbcOperations.queryForObject(
                "select display_status_id from action where action_id = ?",
                new Object[] { conversionId },
                Integer.class);
        return ConversionDisplayStatus.valueOf(displayStatusId).getMajorStatus();
    }
}
