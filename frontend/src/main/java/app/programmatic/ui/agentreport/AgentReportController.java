package app.programmatic.ui.agentreport;

import app.programmatic.ui.agentreport.dao.model.AgentReport;
import app.programmatic.ui.agentreport.dao.model.AgentReportStat;
import app.programmatic.ui.agentreport.dao.model.AgentReportStatus;
import app.programmatic.ui.agentreport.dao.model.MonthlyStat;
import app.programmatic.ui.agentreport.dao.model.MonthlyStats;
import app.programmatic.ui.agentreport.dao.model.TotalStat;
import app.programmatic.ui.agentreport.service.AgentReportDocumentService;
import app.programmatic.ui.agentreport.service.AgentReportService;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AgentReportController {
    private static final int FIRST_DAY_OF_MONTH = 1;

    @Value("${staticresource.agentReport.configPath}")
    private String CONFIG_PATH;

    @Autowired
    private AgentReportService agentReportService;

    @Autowired
    private AgentReportDocumentService agentReportDocumentService;


    @RequestMapping(method = RequestMethod.GET, path = "/rest/agentreport/total", produces = "application/json")
    public List<TotalStat> getTotalStat() {
        return agentReportService.getTotalStats();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/agentreport/monthly", produces = "application/json")
    public MonthlyStats getMonthlyStat(@RequestParam(value = "year") Integer year,
                                       @RequestParam(value = "month") Integer month) {

        List<MonthlyStat> stats = agentReportService.getMonthlyStats(year, month);

        AgentReportStatus status = stats.stream().allMatch(
                stat -> AgentReportStatus.CLOSED.equals(stat.getStatus())) ? AgentReportStatus.CLOSED : AgentReportStatus.OPEN;

        MonthlyStat total = stats.stream()
                .collect(MonthlyStat::new,
                        (response, element) -> aggregateMonthlyStats(response, element),
                        (response1, response2) -> aggregateMonthlyStats(response1, response2)
                );

        return new MonthlyStats(status, stats, total);
    }

    private static void aggregateMonthlyStats (MonthlyStat result, MonthlyStat element) {
        result.setTotalAmount(sum(element.getTotalAmount(), result.getTotalAmount()));
        result.setTotalAmountConfirmed(sum(element.getTotalAmountConfirmed(), result.getTotalAmountConfirmed()));
        result.setPubAmount(sum(element.getPubAmount(), result.getPubAmount()));
        result.setPubAmountConfirmed(sum(element.getPubAmountConfirmed(), result.getPubAmountConfirmed()));
        result.setAgentAmount(sum(element.getAgentAmount(), result.getAgentAmount()));
        result.setPrincipalAmount(sum(element.getPrincipalAmount(), result.getPrincipalAmount()));
    }

    private static BigDecimal sum(BigDecimal first, BigDecimal second) {
        if (first != null) {
            if (second != null) {
                return second.add(first);
            }
            return first;
        }
        return second;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/agentreport/monthly", produces = "application/json")
    public void updateMonthlyStat(@RequestParam(value = "year") Integer year,
                                  @RequestParam(value = "month") Integer month,
                                  @RequestBody List<MonthlyStat> monthlyStats) {
        List<AgentReportStat> stats = monthlyStats.stream()
                .map(monthlyStat -> monthlyToAgentReportStat(monthlyStat, year, month))
                .collect(Collectors.toList());

        agentReportService.updateStats(stats);
    }

    private static AgentReportStat monthlyToAgentReportStat(MonthlyStat monthlyStat, int year, int month) {
        AgentReportStat stat = new AgentReportStat();
        if (monthlyStat.getId() == null) {
            stat.setCampaignId(monthlyStat.getCampaignId());
            stat.setDate(LocalDate.of(year, month, FIRST_DAY_OF_MONTH));
            stat.setRateType(monthlyStat.getRateType());
            stat.setRateValue(monthlyStat.getRateValue());
            stat.setStatus(AgentReportStatus.OPEN.getLetter().charAt(0));
        } else {
            stat.setId(monthlyStat.getId());
            stat.setVersion(XmlDateTimeConverter.convertEpochToTimestamp(monthlyStat.getVersion()));
        }
        stat.setStatus(monthlyStat.getStatus().getLetter().charAt(0));
        stat.setInventoryAmountConfirmed(monthlyStat.getInventoryAmountConfirmed());
        stat.setInventoryAmountComment(monthlyStat.getInventoryAmountComment());
        stat.setInvoiceNumber(monthlyStat.getInvoiceNumber());
        stat.setPubAmountConfirmed(monthlyStat.getPubAmountConfirmed());
        stat.setPubAmountComment(monthlyStat.getPubAmountComment());
        return stat;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/agentreport/close", produces = "application/json")
    public void closeMonthlyStat(@RequestParam(value = "year") Integer year,
                                 @RequestParam(value = "month") Integer month,
                                 @RequestBody List<MonthlyStat> monthlyStats) {
        monthlyStats.stream()
                .forEach(stat -> stat.setStatus(AgentReportStatus.CLOSED));

        updateMonthlyStat(year, month, monthlyStats);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/agentreport/file")
    public ResponseEntity getMonthlyStatFile(@RequestParam(value = "year") Integer year,
                                             @RequestParam(value = "month") Integer month) {

        List<MonthlyStat> stats = agentReportService.getMonthlyStats(year, month);
        Long contractAmount = agentReportService.getContractAmount(year, month);
        AgentReport report = AgentReport.buildAgentReport(year, month, stats, contractAmount);
        byte[] reportData = agentReportDocumentService.generateDocument(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(reportData.length);
        return new ResponseEntity<>(
                reportData,
                headers,
                HttpStatus.OK);
    }
}
