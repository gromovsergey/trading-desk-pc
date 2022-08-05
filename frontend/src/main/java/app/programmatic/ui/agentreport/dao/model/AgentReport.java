package app.programmatic.ui.agentreport.dao.model;

import app.programmatic.ui.agentreport.tool.jaxb.BigDecimalInWordsXmlAdapter;
import app.programmatic.ui.agentreport.tool.jaxb.BigDecimalXmlAdapter;
import app.programmatic.ui.agentreport.tool.jaxb.DateXmlAdapter;
import app.programmatic.ui.common.i18n.MessageInterpolator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name = "report")
public class AgentReport {
    private static final BigDecimal VAT_RATE = new BigDecimal(0.18);

    private static final String CONTRACT_NUMBER = "12345-ABCDEF";
    private static final LocalDate CONTRACT_DATE = LocalDate.of(2017, 06, 15);
    private static final String AGENT_NAME = "ЗАО «ТехноЛиния»";
    private static final String PRINCIPAL_NAME = "ПАО «ВымпелКом»";
    private static final String AGENT_DELEGATE = "Шишова И.В.";
    private static final String PRINCIPAL_DELEGATE = "Оркина А.С.";

    private String contractNumber;
    private LocalDate contractDate;
    private String agentName;
    private String principalName;
    private String agentDelegate;
    private String principalDelegate;

    private LocalDate reportDate;
    private String month;
    private int year;

    private Long contractAmount;
    private Long annulledContractAmount;

    // total values
    private BigDecimal totalAmount;
    private BigDecimal prepaymentAmount;
    private BigDecimal pubAmount;
    private BigDecimal totalNetAmount;
    private BigDecimal agentAmount;
    private BigDecimal principalAmount;

    private List<MonthlyStat> stats;

    public static AgentReport buildAgentReport(Integer year, Integer month, List<MonthlyStat> stats, Long contractAmount) {
        AgentReport report = new AgentReport();
        report.setStats(stats);

        report.setContractNumber(CONTRACT_NUMBER);
        report.setContractDate(CONTRACT_DATE);
        report.setAgentName(AGENT_NAME);
        report.setPrincipalName(PRINCIPAL_NAME);
        report.setAgentDelegate(AGENT_DELEGATE);
        report.setPrincipalDelegate(PRINCIPAL_DELEGATE);

        LocalDate date = LocalDate.of(year, month, 1);
        report.setReportDate(date.withDayOfMonth(date.lengthOfMonth()));
        report.setMonth(MessageInterpolator.getDefaultMessageInterpolator().interpolate("agentReport.month." + month));
        report.setYear(year);

        report.setContractAmount(contractAmount);
        report.setAnnulledContractAmount(0L);

        report.setTotalAmount(stats.stream().map(MonthlyStat::getTotalAmountConfirmed).reduce((x, y) -> x.add(y)).get());
        report.setPrepaymentAmount(stats.stream().map(MonthlyStat::getPrepaymentAmount).reduce((x, y) -> x.add(y)).get());
        report.setPubAmount(stats.stream().map(MonthlyStat::getPubAmountConfirmed).reduce((x, y) -> x.add(y)).get());
        report.setTotalNetAmount(stats.stream().map(MonthlyStat::getTotalNetAmount).reduce((x, y) -> x.add(y)).get());
        report.setAgentAmount(stats.stream().map(MonthlyStat::getAgentAmount).reduce((x, y) -> x.add(y)).get());
        report.setPrincipalAmount(stats.stream().map(MonthlyStat::getPrincipalAmount).reduce((x, y) -> x.add(y)).get());
        
        return report;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public LocalDate getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAgentDelegate() {
        return agentDelegate;
    }

    public void setAgentDelegate(String agentDelegate) {
        this.agentDelegate = agentDelegate;
    }

    public String getPrincipalDelegate() {
        return principalDelegate;
    }

    public void setPrincipalDelegate(String principalDelegate) {
        this.principalDelegate = principalDelegate;
    }

    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Long getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(Long contractAmount) {
        this.contractAmount = contractAmount;
    }

    public Long getAnnulledContractAmount() {
        return annulledContractAmount;
    }

    public void setAnnulledContractAmount(Long annulledContractAmount) {
        this.annulledContractAmount = annulledContractAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getTotalAmountVAT() {
        return totalAmount == null ? null : totalAmount.multiply(VAT_RATE);
    }

    @XmlJavaTypeAdapter(BigDecimalInWordsXmlAdapter.class)
    public BigDecimal getTotalAmountInWords() {
        return totalAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalInWordsXmlAdapter.class)
    public BigDecimal getTotalAmountVATInWords() {
        return getTotalAmountVAT();
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getAgentAmount() {
        return agentAmount;
    }

    public void setAgentAmount(BigDecimal agentAmount) {
        this.agentAmount = agentAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getAgentAmountVAT() {
        return agentAmount == null ? null : agentAmount.multiply(VAT_RATE);
    }

    @XmlJavaTypeAdapter(BigDecimalInWordsXmlAdapter.class)
    public BigDecimal getAgentAmountInWords() {
        return agentAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalInWordsXmlAdapter.class)
    public BigDecimal getAgentAmountVATInWords() {
        return getAgentAmountVAT();
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPrincipalAmountVAT() {
        return principalAmount == null ? null : principalAmount.multiply(VAT_RATE);
    }

    @XmlJavaTypeAdapter(BigDecimalInWordsXmlAdapter.class)
    public BigDecimal getPrincipalAmountInWords() {
        return principalAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalInWordsXmlAdapter.class)
    public BigDecimal getPrincipalAmountVATInWords() {
        return getPrincipalAmountVAT();
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public void setPrepaymentAmount(BigDecimal prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPubAmount() {
        return pubAmount;
    }

    public void setPubAmount(BigDecimal pubAmount) {
        this.pubAmount = pubAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getTotalNetAmount() {
        return totalNetAmount;
    }

    public void setTotalNetAmount(BigDecimal totalNetAmount) {
        this.totalNetAmount = totalNetAmount;
    }

    @XmlElement(name = "row")
    public List<MonthlyStat> getStats() {
        return stats;
    }

    public void setStats(List<MonthlyStat> stats) {
        this.stats = stats;
    }
}
