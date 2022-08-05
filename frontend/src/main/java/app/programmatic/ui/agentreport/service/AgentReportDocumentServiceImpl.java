package app.programmatic.ui.agentreport.service;

import app.programmatic.ui.agentreport.dao.model.AgentReport;
import app.programmatic.ui.common.restriction.annotation.Restrict;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

@Service
public class AgentReportDocumentServiceImpl implements AgentReportDocumentService {

    @Value("${staticresource.agentReport.configPath}")
    private String CONFIG_PATH;

    @Override
    @Restrict(restriction = "agentReport.view")
    public byte[] generateDocument(AgentReport report) {
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(createXML(report).toByteArray());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        convertToPDF(xmlStream, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private ByteArrayOutputStream createXML(AgentReport report) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AgentReport.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(report, byteArrayOutputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream;
    }

    private void convertToPDF(ByteArrayInputStream xmlStream, ByteArrayOutputStream byteArrayOutputStream) {
        try {
            Resource xsltFileResource = new DefaultResourceLoader().getResource("classpath:app/programmatic/ui/agentreport/pdf/template.xsl");
            StreamSource xmlSource = new StreamSource(xmlStream);

            Resource cfgFileResource = new DefaultResourceLoader().getResource("classpath:" + CONFIG_PATH);
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder.build(cfgFileResource.getInputStream());
            FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new File(".").toURI()).setConfiguration(cfg);

            FopFactory fopFactory = fopFactoryBuilder.build();
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, byteArrayOutputStream);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFileResource.getInputStream()));

            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(xmlSource, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
