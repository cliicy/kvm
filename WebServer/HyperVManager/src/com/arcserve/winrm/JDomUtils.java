package com.arcserve.winrm;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public abstract class JDomUtils {
    public static Element buildElementFromString(String xml) {
        return buildDocumentFromString(xml).detachRootElement();
    }
    
    public static Document buildDocumentFromString(String xml) {
        try {
            return new SAXBuilder().build(new StringReader(xml));
        } catch (JDOMException | IOException e) {
            System.err.println("========start=======");
            e.printStackTrace();
            System.err.println(xml);
            System.err.println("=========end========");
            return null;
        }
    }
    
    public static String getPrettyXml(Element elem) {
        return getXml((Object) elem, true, false, true);
    }
    
    public static String getXml(Element elem) {
        return getXml((Object) elem, true, false, false);
    }
    
    public static String getXml(Element elem, Boolean omitDeclaration) {
        return getXml((Object) elem, omitDeclaration, false, false);
    }
    
    public static String getXml(Element elem, Boolean omitDeclaration, Boolean noNamespace) {
        return getXml((Object) elem, omitDeclaration, noNamespace, false);
    }
    
    public static String getPrettyXml(Document doc) {
        return getXml((Object) doc, true, false, true);
    }
    
    public static String getXml(Document doc) {
        return getXml((Object) doc, true, false, false);
    }
    
    public static String getXml(Document doc, Boolean omitDeclaration) {
        return getXml((Object) doc, omitDeclaration, false, false);
    }
    
    public static String getXml(Document doc, Boolean omitDeclaration, Boolean noNamespace) {
        return getXml((Object) doc, omitDeclaration, noNamespace, false);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void removeNamespaces(Element element) {
        element.setNamespace(Namespace.NO_NAMESPACE);
        List tmp = new ArrayList(element.getAdditionalNamespaces());
        for (Iterator i = tmp.iterator(); i.hasNext();)
            element.removeNamespaceDeclaration((Namespace) i.next());
        
        for (Iterator i = element.getChildren().iterator(); i.hasNext();)
            removeNamespaces((Element) i.next());
    }
    
    public static String getXml(Object target, Boolean omitDeclaration, Boolean noNamespace, Boolean pretty) {
        StringWriter sw = new StringWriter();
        XMLOutputter xo = new XMLOutputter();
        
        try {
            xo.setFormat(xo.getFormat().setOmitDeclaration(omitDeclaration));
            if (pretty)
                xo.setFormat(xo.getFormat().setIndent("  "));
            
            if (target instanceof Element) {
                if (noNamespace)
                    removeNamespaces((Element) target);
                xo.output((Element) target, sw);
            } else if (target instanceof Document) {
                if (noNamespace)
                    removeNamespaces(((Document) target).getRootElement());
                xo.output((Document) target, sw);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sw.toString().trim();
    }
}
