package io.echosystem.ebr.config;

import io.echosystem.ebr.MarshallerCache;
import io.echosystem.ebr.exception.FalhaLeituraConfiguracoes;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
@XmlRootElement(name = "config")
public class Config {
    
    private static final String CONFIG_XML = "config.xml";
    
    public static Config load() throws FalhaLeituraConfiguracoes{
        InputStream configInputStream = Config.class.getResourceAsStream(CONFIG_XML);
        Config config;
        try {
            config = MarshallerCache.unmarshall(configInputStream, Config.class);
        } catch (ParserConfigurationException | JAXBException | SAXException | IOException ex) {
            throw new FalhaLeituraConfiguracoes(ex);
        }
        
        return config;
    }

    @XmlElement(name = "doc", required = true)
    private List<Documento> documentos;

    public static class Documento {
        @XmlAttribute(name = "nome")
        private String nome;

        @XmlAttribute(name = "versao")
        private String versao;

        @XmlAttribute(name = "descricao")
        private String descricao;

        @XmlElement(name="uf", required = true)
        private List<UF> uf;
    }

    @XmlType(name="io.echosystem.ebr.Config.UF")
    public static class UF {
        @XmlAttribute(name = "sigla")
        private io.echosystem.ebr.UF sigla;

        @XmlElement(name = "ambiente")
        private List<Ambiente> ambiente;
    }

    @XmlType(name="io.echosystem.ebr.Config.Ambiente")
    public static class Ambiente {
        @XmlAttribute(name = "nome")
        private io.echosystem.ebr.Ambiente nome;
        
        @XmlElement(name="servico")
        private List<Servico> servicos;
    }
    
    public static class Servico {
        @XmlAttribute(name="nome")
        private String nome;
        
        @XmlAttribute(name="versao")
        private String versao;
        
        @XmlAttribute(name="url")
        private String url;
    }
}