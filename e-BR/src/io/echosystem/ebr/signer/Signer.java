package io.echosystem.ebr.signer;

import io.echosystem.ebr.exception.FalhaAssinaturaDocumento;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import keystore.KeyStoreHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class Signer {

    private static final String CTE = "CTe";
    private static final String CTE_NAMESPACE = "http://www.portalfiscal.inf.br/cte";
    private static final String INFCTE = "infCte";

    private static final String NFE = "NFe";
    private static final String NFE_NAMESPACE = "http://www.portalfiscal.inf.br/nfe";
    private static final String INFNFE = "infNFe";

    private static final String ID = "Id";

    private static final String CTE_ROOT_ELEMENT_NAME = "CTe";
    private static final String NFE_ROOT_ELEMENT_NAME = "NFe";

    public static void main(String[] args) throws FileNotFoundException, FalhaAssinaturaDocumento, TransformerConfigurationException, TransformerException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Caminho para o documento que deseja assinar:");
        String inputFilePath = sc.nextLine();
        if(inputFilePath == null) return;
        System.out.println("Caminho para o documento assinado:");
        String outputFilePath = sc.nextLine();
        if(outputFilePath == null) return;
        System.out.println("Camino para o certificado digital:");
        String keystorePath = sc.nextLine();
        if(keystorePath == null) return;
        System.out.println("Senha do certificado digital:");
        String keystorePassword = sc.nextLine();
        if(keystorePassword == null) return;
        
        FileInputStream fis = new FileInputStream(inputFilePath);
        if(fis == null){
            throw new FileNotFoundException(inputFilePath);
        }
        
        FileInputStream kis = new FileInputStream(keystorePath);
        if(kis == null){
            throw new FileNotFoundException(keystorePath);
        }
        
        Document doc = sign(fis, kis, keystorePassword.toCharArray());
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        DOMSource xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(new File(outputFilePath));
        transformer.transform(xmlSource, outputTarget);
    }
    

    public static Document sign(InputStream xmlCTeInputStream, InputStream certInputStream, char[] certPassword) throws FalhaAssinaturaDocumento {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document xmlDoc;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDoc = builder.parse(xmlCTeInputStream);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            throw new FalhaAssinaturaDocumento(ex);
        }
        KeyStoreHelper keyStoreHelper;
        try {
            keyStoreHelper = KeyStoreHelper.getInstance(certInputStream, certPassword, KeyStoreHelper.Type.PKCS12);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableEntryException | InvalidAlgorithmParameterException ex) {
            throw new FalhaAssinaturaDocumento(ex);
        }

        Element parentNode;
        String referenceId;

        String documentElementName = xmlDoc.getDocumentElement().getNodeName();
        switch (documentElementName) {
            case CTE_ROOT_ELEMENT_NAME: {
                NodeList nodeList = xmlDoc.getElementsByTagNameNS(CTE_NAMESPACE, CTE);
                parentNode = (Element) nodeList.item(0);
                Element infCTe = (Element) xmlDoc.getDocumentElement().getElementsByTagName(INFCTE).item(0);
                referenceId = "#" + infCTe.getAttribute(ID);
                infCTe.setIdAttribute(ID, true);
                break;
            }
            case NFE_ROOT_ELEMENT_NAME: {
                NodeList nodeList = xmlDoc.getElementsByTagNameNS(NFE_NAMESPACE, NFE);
                parentNode = (Element) nodeList.item(0);
                Element infNFe = (Element) xmlDoc.getDocumentElement().getElementsByTagName(INFNFE).item(0);
                referenceId = "#" + infNFe.getAttribute(ID);
                infNFe.setIdAttribute(ID, true);
                break;
            }
            default:
                throw new UnsupportedOperationException("Assinatura de documento não implementada para " + documentElementName);
        }

        Signer.sign(keyStoreHelper.getXmlSignatureFactory(), keyStoreHelper.getTransformList(), keyStoreHelper.getPrivateKey(), keyStoreHelper.getKeyInfo(), referenceId, (Node) parentNode);
        Document document = ((Node) parentNode).getOwnerDocument();
        return document;
    }

    private static void sign(XMLSignatureFactory fac, List<Transform> transformList, PrivateKey privateKey, KeyInfo keyInfo, String uri, Node nodeToSign) throws FalhaAssinaturaDocumento {
        try {
            Reference ref = fac.newReference(uri, fac.newDigestMethod(DigestMethod.SHA1, null), transformList, null, null);
            SignedInfo signedInfo = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null), fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
            XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
            DOMSignContext dsc = new DOMSignContext(privateKey, nodeToSign);
            signature.sign(dsc);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | MarshalException | XMLSignatureException ex) {
            throw new FalhaAssinaturaDocumento(ex);
        }
    }
}
