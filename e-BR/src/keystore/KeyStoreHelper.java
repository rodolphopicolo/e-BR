/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keystore;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class KeyStoreHelper {
    public static enum Type {
	PKCS12;
    }
    
    private final PrivateKey privateKey;  
    private final KeyInfo keyInfo; 
    private final java.security.KeyStore keyStore;
    private final XMLSignatureFactory xmlSignatureFactory;
    private final List<Transform> transformList;

    private KeyStoreHelper(PrivateKey privateKey, KeyInfo keyInfo, java.security.KeyStore keyStore, XMLSignatureFactory xmlSignatureFactory, List<Transform> transformList){
	this.privateKey = privateKey; 
	this.keyInfo = keyInfo; 
	this.keyStore = keyStore;
	this.xmlSignatureFactory = xmlSignatureFactory;
	this.transformList = transformList;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public XMLSignatureFactory getXmlSignatureFactory() {
        return xmlSignatureFactory;
    }

    public List<Transform> getTransformList() {
        return transformList;
    }
    
    

    public static KeyStoreHelper getInstance(InputStream certInputStream, char[] password, Type type) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException, InvalidAlgorithmParameterException{  
	java.security.KeyStore ks = java.security.KeyStore.getInstance(type.toString());  
	ks.load(certInputStream, password);  

	PrivateKey privateKey = null;
        java.security.KeyStore.PrivateKeyEntry privateKeyEntry = null;  
        Enumeration<String> aliasesEnum = ks.aliases();  
        while (aliasesEnum.hasMoreElements()) {  
            String alias = (String) aliasesEnum.nextElement();  
            if (ks.isKeyEntry(alias)) {  
                privateKeyEntry = (java.security.KeyStore.PrivateKeyEntry) ks.getEntry(alias,  new java.security.KeyStore.PasswordProtection(password));  
                privateKey = privateKeyEntry.getPrivateKey();  
                break;  
            }  
        }  
  
        X509Certificate cert = (X509Certificate) privateKeyEntry.getCertificate();  
  
	XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM"); 
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();  
        List<X509Certificate> x509Content = new ArrayList<>();  
  
        x509Content.add(cert);  
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);  
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));  
	List<Transform> transformList = transformListFactory(signatureFactory);
	KeyStoreHelper certificate = new KeyStoreHelper(privateKey, keyInfo, ks, signatureFactory, transformList);

	return certificate;
    }    
    
    private static ArrayList<Transform> transformListFactory(XMLSignatureFactory signatureFactory) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {  
        ArrayList<Transform> transformList = new ArrayList<>();  
        TransformParameterSpec tps = null;  
        Transform envelopedTransform = signatureFactory.newTransform(Transform.ENVELOPED, tps);  
        Transform c14NTransform = signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", tps);  
        transformList.add(envelopedTransform);  
        transformList.add(c14NTransform);  
        return transformList;  
    }
}
