package io.echosystem.ebr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class MarshallerCache {

    private static HashMap<Class, Marshaller> marshallerCache = new HashMap();
    private static HashMap<Class, Unmarshaller> unmarshallerCache = new HashMap();
    
    private static Marshaller loadMarshaller(Class clazz) throws JAXBException{
        if(marshallerCache.containsKey(clazz)){
            return marshallerCache.get(clazz);
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);        
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshallerCache.put(clazz, marshaller);
        return marshaller;
    }
    
    private static Unmarshaller loadUnmarshaller(Class clazz) throws JAXBException{
        if(unmarshallerCache.containsKey(clazz)){
            return unmarshallerCache.get(clazz);
        }
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshallerCache.put(clazz, unmarshaller);
        return unmarshaller;
    }
    
    public static byte[] marshall(Object object) throws JAXBException{
        Marshaller marshaller = MarshallerCache.loadMarshaller(object.getClass());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(object, baos);
        return baos.toByteArray();
    }
    
    public static <T> T unmarshall(InputStream inputStream, Class clazz) throws ParserConfigurationException, JAXBException, SAXException, IOException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        return unmarshall(document, clazz);
    }
    
    public static <T> T unmarshall(Node node, Class clazz) throws JAXBException{
        Unmarshaller unmarshaller = MarshallerCache.loadUnmarshaller(clazz);
        T t = (T) unmarshaller.unmarshal(node);
        return t;
    }
}
