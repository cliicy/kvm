package com.arcserve.winrm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.openwsman.Client;
import org.openwsman.ClientOptions;
import org.openwsman.EndPointReference;
import org.openwsman.Filter;
import org.openwsman.OpenWSMan;
import org.openwsman.OpenWSManConstants;
import org.openwsman.XmlDoc;

import com.arcserve.hyperv.CommonUtil;
import com.arcserve.hyperv.Connection;

public abstract class Classes {
    public enum ClassInstancePropertyType {
        SIMPLE_VALUE, SIMPLE_ARRAY_VALUE, REFERENCE_VALUE,
        OBJECT_VALUE, OBJECT_ARRAY_VALUE, UNKNOWN_VALUE
    }
    
    public interface InvocationArgument {
        public String[] getXmlRepresentation(String targetNS);
    }
    
    public interface ClassInstanceProperty {
        public String getPropertyName();
        
        public ClassInstancePropertyType getPropertyType();
        
        public String getValueType();
        
        public String getSimpleValue();
        
        public void setSimpleValue(String value);
        
        public String[] getSimpleArrayValue();
        
        public void setSimpleArrayValue(String[] values);
        
        public ClassReference getReferenceValue();
        
        public void setReferenceValue(ClassReference value);
        
        public ClassInstance getObjectValue();
        
        public void setObjectValue(ClassInstance value);
        
        public ClassInstance[] getObjectArrayValue();
        
        public void setObjectArrayValue(ClassInstance[] values);
    }
    
    public interface ClassInstance {
        public String dumpPropertiesForDebug();
        
        public ClassInstanceProperty[] getProperties();
        
        public ClassInstanceProperty getProperty(String propName);
        
        public String getClassName();
        
        public String getDtdText();
        
        public String getDtdText(String overrideClassName);
    }
    
    public interface ClassReference {
        public String getClassName();
        
        public String getUri();
        
        public ClassReference addSelector(String name, String value);
        
        public ClassReference removeSelector(String name);
        
        public List<Entry<String, String>> getSelectors();
        
        public Classes.ClassInstance getInstance(Client conn);
        
        public Classes.ClassInstance[] enumerateInstances(Client conn);
        
        public Classes.ClassInstance[] enumerateInstances(Client conn, String wql);
        
        public Classes.ClassInstance[] relatedInstances(Client conn) throws Exception;
        
        public Classes.ClassInstance[] relatedInstances(Client conn, String resultClass) throws Exception;
        
        public Classes.ClassInstance[] relatedInstances(Client conn, String resultClass, String assocClass)
            throws Exception;
        
        /**
         * @param conn The openwsman client
         * @param resultClass ClassName
         * @param assocClass AssocClassName
         * @param resutlRole PropertyName
         * @param role PropertyName
         * @throws Exception 
         * */
        public Classes.ClassInstance[] relatedInstances(Client conn, String resultClass, String assocClass,
            String resutlRole, String role) throws Exception;
        
        public String[] invokeMethod(Client conn, String methodName, InvocationArgument... args) throws Exception;
        
        public String getDtdValueRefText();
    }
    
    public interface ClassDefinition {
        public String getClassName();
        
        public String getUri();
        
        public ClassInstance createLocalInstance();
    }
    
    private static final Logger logger = Logger.getLogger(Classes.class);
    
    private static final String commonPrefix = "http://schemas.microsoft.com/wbem/wsman/1/wmi/";
    
    protected abstract Map<String, ClassDefinition> getClassesDefMap();
    
    //private static Map<String, ClassDefinition> classesDefMap  = new HashMap<String, ClassDefinition>();
    
    @SuppressWarnings("unused")
    private static final Namespace ns_ms_wsman    = Namespace.getNamespace("http://www.w3.org/2003/05/soap-envelope");
    @SuppressWarnings("unused")
    private static final Namespace ns_dmtf_wsman  = Namespace.getNamespace("http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd");
    private static final Namespace ns_envelope    = Namespace.getNamespace("http://www.w3.org/2003/05/soap-envelope");
    @SuppressWarnings("unused")
    private static final Namespace ns_addressing  = Namespace.getNamespace("http://schemas.xmlsoap.org/ws/2004/08/addressing");
    private static final Namespace ns_enumeration = Namespace.getNamespace("http://schemas.xmlsoap.org/ws/2004/09/enumeration");
    @SuppressWarnings("unused")
    private static final Namespace ns_xsd         = Namespace.getNamespace("xsd",
                                                      "http://www.w3.org/2001/XMLSchema");
    private static final Namespace ns_xsi         = Namespace.getNamespace("xsi",
                                                      "http://www.w3.org/2001/XMLSchema-instance");
    
    protected void setResourceForClasses(String resPath) {
        // com/arcserve/winrm/res/classes-v1.res
        // com/arcserve/winrm/res/classes-v2.res
        getClassesDefMap().clear();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(resPath)));
            
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                ClassDefinition classDef = instantiateClassDefinition(line);
                getClassesDefMap().put(classDef.getClassName(), classDef);
            }
            
            br.close();
        } catch (Exception e) {
            logger.error("Failed to set classes resource", e);
        }
    }
    
    public InvocationArgument ARG(String argName, String argValue) throws Exception {
        return instantiateInvocationArgument(argName, argValue);
    }
    
    public InvocationArgument ARG(String argName, String[] argValueArr) throws Exception {
        return instantiateInvocationArgument(argName, argValueArr);
    }
    
    public InvocationArgument ARG(String argName, ClassReference refValue) throws Exception {
        return instantiateInvocationArgument(argName, refValue);
    }
    
    public InvocationArgument ARG(String argName, ClassReference[] refValueArr) throws Exception {
        return instantiateInvocationArgument(argName, refValueArr);
    }
    
    public InvocationArgument ARG(String argName, ClassInstance instValue) throws Exception {
        return instantiateInvocationArgument(argName, (instValue != null) ? instValue.getDtdText() : "");
    }
    
    public InvocationArgument ARG(String argName, ClassInstance[] instValueArr) throws Exception {
        ArrayList<String> argValue = new ArrayList<String>();
        if (instValueArr != null)
            for (ClassInstance inst : instValueArr)
                argValue.add(inst.getDtdText());
        return instantiateInvocationArgument(argName, argValue.toArray(new String[argValue.size()]));
    }
    
    @SuppressWarnings("unchecked")
    public ClassInstance createInstanceFromSoapResponseElement(Element itemElem) {
        JDomUtils.removeNamespaces(itemElem);
        Classes.ClassInstance inst = createInstance(itemElem.getName());
        
        for (Classes.ClassInstanceProperty prop : inst.getProperties()) {
            /* This property is not present in response */
            Element targetProp = itemElem.getChild(prop.getPropertyName());
            if (targetProp == null)
                continue;
            
            switch (prop.getPropertyType()) {
            case SIMPLE_VALUE:
                prop.setSimpleValue(targetProp.getText());
                break;
            case SIMPLE_ARRAY_VALUE:
                ArrayList<String> propArray = new ArrayList<String>();
                for (Element e : (List<Element>) itemElem.getChildren(prop.getPropertyName()))
                    propArray.add(e.getText());
                prop.setSimpleArrayValue(propArray.toArray(new String[propArray.size()]));
                break;
            case OBJECT_VALUE:
                logger.debug(" Begin OBJECT_VALUE: ");
                logger.debug(JDomUtils.getXml(targetProp));
                logger.debug("  End  OBJECT_VALUE: ");
                break;
            case OBJECT_ARRAY_VALUE:
                logger.debug(" Begin OBJECT_ARRAY_VALUE: ");
                logger.debug(JDomUtils.getXml(targetProp));
                logger.debug("  End  OBJECT_ARRAY_VALUE: ");
                break;
            case REFERENCE_VALUE:
                prop.setReferenceValue(createReferenceFromSoapResponseElement(targetProp));
                break;
            default:
                logger.error("Unexpected property type " + prop.getPropertyType());
            }
        }
        return inst;
    }
    
    @SuppressWarnings("unchecked")
    public ClassReference createReferenceFromSoapResponseElement(Element rootElem) {
        //Example:
        //<p:ResultingSystem xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/virtualization/v2/Msvm_VirtualSystemManagementService">
        //    <a:ReferenceParameters xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing">
        //      <w:ResourceURI xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd">
        //          http://schemas.microsoft.com/wbem/wsman/1/wmi/root/virtualization/v2/Msvm_EthernetSwitchPort
        //      </w:ResourceURI>
        //      <w:SelectorSet xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd">
        //          <w:Selector Name="CreationClassName">Msvm_EthernetSwitchPort</w:Selector>
        //          <w:Selector Name="DeviceID">Microsoft:E4DB59E5-E899-443A-9F80-B1C39A997B38</w:Selector>
        //          <w:Selector Name="SystemCreationClassName">Msvm_VirtualEthernetSwitch</w:Selector>
        //          <w:Selector Name="SystemName">D41C6206-2931-4CC6-B761-1EE0FA5C7089</w:Selector>
        //      </w:SelectorSet>
        //    </a:ReferenceParameters>
        //</p:ResultingSystem>
        if (rootElem == null)
            return null;
        JDomUtils.removeNamespaces(rootElem);
        Element soapRefParamsElem = rootElem.getChild("ReferenceParameters");
        if (soapRefParamsElem == null)
            return null;
        
        Element resourceUri = soapRefParamsElem.getChild("ResourceURI");
        Element selectorSet = soapRefParamsElem.getChild("SelectorSet");
        HashMap<String, String> selectors = new HashMap<String, String>();
        String className;
        
        if (resourceUri == null)
            return null;
        else {
            String[] parts = resourceUri.getText().split("/");
            className = parts[parts.length - 1];
        }
        
        if (selectorSet != null)
            for (Element selElem : (List<Element>) selectorSet.getChildren("Selector"))
                selectors.put(selElem.getAttributeValue("Name"), selElem.getText());
        
        return instantiateClassReference(getClassesDefMap().get(className),
            selectors.entrySet().toArray(new Map.Entry[selectors.size()]));
    }
    
    public ClassReference createReference(String className) {
        return instantiateClassReference(getClassesDefMap().get(className), null);
    }
    
    public ClassDefinition[] getAvailableClasses() {
        Collection<ClassDefinition> defs = getClassesDefMap().values();
        return defs.toArray(new ClassDefinition[defs.size()]);
    }
    
    public ClassInstance createInstance(String className) {
        ClassDefinition def = getClassesDefMap().get(className);
        if (def != null)
            return def.createLocalInstance();
        else
            return null;
    }
    
    /*
     * Below method contains classes creation code.
     */
    private InvocationArgument instantiateInvocationArgument(String name, Object value) throws Exception {
        final String argName = new StringBuilder(name).toString();
        final List<String> strVals = new ArrayList<String>();
        final List<ClassReference> refVals = new ArrayList<ClassReference>();
        
        if (value instanceof String) {
            strVals.add((String) value);
        }
        else if (value instanceof String[]) {
            strVals.addAll(Arrays.asList((String[]) value));
        }
        else if (value instanceof ClassReference) {
            refVals.add((ClassReference) value);
        }
        else if (value instanceof ClassReference[]) {
            for (ClassReference ref : (ClassReference[]) value)
                if (ref != null)
                    refVals.add(ref);
        } else
            throw new Exception("Invalid invacation argument value.");
        
        return new InvocationArgument() {
            @Override
            public String[] getXmlRepresentation(String targetNs) {
                List<String> resultArr = new ArrayList<String>();
                
                for (String val : strVals)
                    if ((val != null) && !val.isEmpty())
                        resultArr.add(JDomUtils.getXml((new Element(argName, targetNs).setText(val))));
                    else
                        resultArr.add(JDomUtils.getXml((new Element(argName, targetNs).setAttribute("nil", "true",
                            ns_xsi))));
                
                for (ClassReference ref : refVals) {
                    if (ref == null)
                        continue;
                    
                    EndPointReference epr = new EndPointReference(ref.getUri());
                    
                    for (Entry<String, String> sel : ref.getSelectors())
                        epr.add_selector(sel.getKey(), sel.getValue());
                    
                    resultArr.add(epr.to_xml(targetNs, argName));
                }
                return resultArr.toArray(new String[resultArr.size()]);
            }
        };
    }
    
    private ClassReference instantiateClassReference(ClassDefinition classDef,
        Entry<String, String>... initselecctors) {
        final LinkedList<Entry<String, String>> selectorsSorted = new LinkedList<Entry<String, String>>();
        final String classUri = new StringBuilder(classDef.getUri()).toString();
        final String className = classUri.substring(classUri.lastIndexOf('/') + 1);
        final Namespace ns_class = Namespace.getNamespace(classUri);
        
        ClassReference result = new ClassReference() {
            @Override
            public String getClassName() {
                return className;
            }
            
            @Override
            public String getUri() {
                return classUri;
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public ClassReference addSelector(String name, String value) {
                for (Entry<String, String> s : selectorsSorted) {
                    if (s.getKey().equals(name)) {
                        s.setValue(value);
                        return this;
                    }
                }
                selectorsSorted.add(new AbstractMap.SimpleEntry<String, String>(name, value));
                return this;
            }
            
            @Override
            public ClassReference removeSelector(String name) {
                for (Entry<String, String> s : selectorsSorted) {
                    if (s.getKey().equals(name)) {
                        selectorsSorted.remove(s);
                        return this;
                    }
                }
                return this;
            }
            
            @Override
            public List<Entry<String, String>> getSelectors() {
                List<Entry<String, String>> res = new ArrayList<Entry<String, String>>();
                for (Entry<String, String> e : selectorsSorted)
                    res.add(e);
                return res;
            }
            
            public String getDtdValueRefText() {
                Element result = new Element("VALUE.REFERENCE");
                Element instancePath = new Element("INSTANCEPATH");
                Element namespacePath = new Element("NAMESPACEPATH");
                Element localNamespacePath = new Element("LOCALNAMESPACEPATH");
                ClassDefinition classDef = getClassesDefMap().get(className);
                String[] namespacePathStrs = classDef.getUri().replace(commonPrefix, "").replace(className, "").split(
                    "/");
                
                for (int i = 0; i < namespacePathStrs.length; i++) {
                    Element namespace = new Element("NAMESPACE");
                    namespace.setAttribute("NAME", namespacePathStrs[i]);
                    localNamespacePath.addContent(namespace);
                }
                namespacePath.addContent(localNamespacePath);
                instancePath.addContent(namespacePath);
                
                Element instanceName = new Element("INSTANCENAME");
                instanceName.setAttribute("CLASSNAME", className);
                
                for (Entry<String, String> e : selectorsSorted) {
                    Element keyBinding = new Element("KEYBINDING");
                    keyBinding.setAttribute("NAME", e.getKey());
                    Element keyValue = new Element("KEYVALUE");
                    keyValue.setAttribute("VALUETYPE", "string");
                    keyValue.setText(e.getValue());
                    keyBinding.addContent(keyValue);
                    instanceName.addContent(keyBinding);
                }
                instancePath.addContent(instanceName);
                
                result.addContent(instancePath);
                
                return JDomUtils.getXml(result);
            }
            
            @Override
            public ClassInstance getInstance(Client conn) {
                synchronized (conn) {
                    long start = System.nanoTime();
                    String enterLeavePrefix = "[" + start + "][" + className + "]";
                    logger.debug(enterLeavePrefix + "Enter getInstance");
                    
                    ClientOptions options = new ClientOptions();
                    EndPointReference erp = new EndPointReference(classUri);
                    
                    /** options.set_dump_request(); */
                    
                    for (Entry<String, String> e : selectorsSorted)
                        erp.add_selector(e.getKey(), e.getValue());
                    
                    XmlDoc res = conn.get_from_epr(options, erp);
                    if ((res == null) || res.isFault())
                        logger.error(String.format("%s\n%s\n[%d] Get failed: %s", 
                            conn.host(), className, (System.nanoTime() - start), ((res != null) ? res.fault().reason() : "?")));
                    else {
                        //logerr(res.encode("UTF-8"));
                        Element envelopeElem = JDomUtils.buildElementFromString(res.encode("UTF-8"));
                        if (envelopeElem == null)
                            return null;
                        Element responseBody = envelopeElem.getChild("Body", ns_envelope);
                        if (responseBody == null)
                            return null;
                        Element itemElem = responseBody.getChild(className, ns_class);
                        if (itemElem == null)
                            return null;

                        logger.debug(enterLeavePrefix+"Leave getInstance[" + (System.nanoTime() - start) +"]");   
                        
                        return createInstanceFromSoapResponseElement(itemElem);
                    }
                    
                    return null;
                }
            }
            
            @Override
            public Classes.ClassInstance[] relatedInstances(Client conn) throws Exception {
                return this.relatedInstances(conn, null, null, null, null);
            }
            
            @Override
            public Classes.ClassInstance[] relatedInstances(Client conn, String resultClass) throws Exception {
                return this.relatedInstances(conn, resultClass, null, null, null);
            }
            
            @Override
            public Classes.ClassInstance[] relatedInstances(Client conn, String resultClass, String assocClass)
                throws Exception {
                return this.relatedInstances(conn, resultClass, assocClass, null, null);
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public Classes.ClassInstance[] relatedInstances(Client conn, String resultClass, String assocClass,
                String resutlRole, String role) throws Exception {
                List<ClassInstance> result = new ArrayList<ClassInstance>();
                
                synchronized (conn) {                    
                    long start = System.nanoTime();
                    String enterLeavePrefix = "[" + start + "][" + resultClass + "][" + assocClass + "]";
                    logger.debug(enterLeavePrefix + "Enter relatedInstances");
                    
                    String kvps = "";
                    /** ex: Msvm_ExternalEthernetPort.CreationClassName="Msvm_ExternalEthernetPort",DeviceID="Microsoft:{9493DFC3-DCE0-40EE-B8D2-DC8482E2EA1A}" */
                    for (Entry<String, String> e : selectorsSorted)
                        kvps += e.getKey() + "='" + e.getValue() + "',";
                    if (kvps.isEmpty())
                        throw new Exception();
                    kvps = kvps.substring(0, kvps.length() - 1);
                    /** get rid of the last comma. */
                    
                    String primary_wql = "ASSOCIATORS OF {" + className + "." + kvps + "}";
                    
                    String conditions = "";
                    if ((assocClass != null) && !assocClass.isEmpty())
                        conditions += " AssocClass = " + assocClass;
                    if ((resultClass != null) && !resultClass.isEmpty())
                        conditions += " ResultClass = " + resultClass;
                    if ((resutlRole != null) && !resutlRole.isEmpty())
                        conditions += " ResultRole = " + resutlRole;
                    if ((role != null) && !role.isEmpty())
                        conditions += " Role = " + role;
                                        
                    ClientOptions options = new ClientOptions();
                    Filter filter = new Filter();
                    
                    /** options.set_dump_request(); */
                    
                    if ((conditions != null) && !conditions.isEmpty())
                        filter.wql(primary_wql + " WHERE" + conditions);
                    else
                        filter.wql(primary_wql);
                    
                    XmlDoc res = conn.enumerate(options, filter, classUri.replace(className, "*"));
                    
                    if ((res == null) || res.isFault())
                        logger.error(String.format("%s\n%s\n[%d] Get associators failed: %s", 
                            conn.host(), resultClass, (System.nanoTime() - start), ((res != null) ? res.fault().reason() : "?")));
                    else {
                        String context = res.context();
                        while (context != null) {
                            res = conn.pull(options, null, OpenWSManConstants.CIM_ALL_AVAILABLE_CLASSES, context);
                            if (res == null || res.isFault()) {
                                logger.error("Pull failed: " + ((res != null) ? res.fault().reason() : "?"));
                                context = null;
                                continue;
                            }
                            
                            Element envelopeElem = JDomUtils.buildElementFromString(res.encode("UTF-8"));
                            if (envelopeElem == null) {
                                logger.error("ERR: envelopeElem is null");
                                return null;
                            }
                            Element responseBody = envelopeElem.getChild("Body", ns_envelope);
                            if (responseBody == null) {
                                logger.error("ERR: responseBody is null");
                                return null;
                            }
                            Element pullResponseElem = responseBody.getChild("PullResponse", ns_enumeration);
                            if (pullResponseElem == null) {
                                logger.error("ERR: pullResponseElem is null");
                                return null;
                            }
                            Element itemsElem = pullResponseElem.getChild("Items", ns_enumeration);
                            if (itemsElem == null) {
                                logger.error("ERR: itemsElem is null");
                                return null;
                            }
                            
                            for (Element itemElem : (List<Element>) itemsElem.getChildren()) {
                                ClassInstance inst = createInstanceFromSoapResponseElement(itemElem);
                                if (inst == null)
                                    continue;
                                else
                                    result.add(inst);
                            }
                            context = res.context();
                        }
                    }
                    
                    logger.debug(enterLeavePrefix+"Leave relatedInstances[" + (System.nanoTime() - start) +"]");   
                }
                
                return result.toArray(new Classes.ClassInstance[result.size()]);
            }
            
            @Override
            public ClassInstance[] enumerateInstances(Client conn) {
                return enumerateInstances(conn, null);
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public ClassInstance[] enumerateInstances(Client conn, String wql) {
                List<ClassInstance> result = new ArrayList<ClassInstance>();

                synchronized (conn) {              
                    long start = System.nanoTime();
                    String enterLeavePrefix = "[" + start + "][" + className + "]";
                    logger.debug(enterLeavePrefix + "Enter enumerateInstances");
                    
                    ClientOptions options = new ClientOptions();
                    Filter filter = new Filter();
                    String enumUri = new StringBuilder(classUri).toString();
                    
                    /* options.set_dump_request(); */
                    
                    if ((wql != null) && !wql.isEmpty()) {
                        enumUri = enumUri.replace(className, "*");
                        filter.wql("SELECT * FROM " + className + " WHERE " + wql);
                    }
                    XmlDoc res = conn.enumerate(options, filter, enumUri);
                    
                    if ((res == null) || res.isFault())
                        logger.error(String.format("%s\n%s\n[%d] Enumeration failed: %s", 
                            conn.host(), enumUri, (System.nanoTime() - start), ((res != null) ? res.fault().reason() : "?")));
                    else {
                        String context = res.context();
                        while (context != null) {
                            res = conn.pull(options, null, OpenWSManConstants.CIM_ALL_AVAILABLE_CLASSES, context);
                            if (res == null || res.isFault()) {
                                logger.error("Pull failed: " + ((res != null) ? res.fault().reason() : "?"));
                                context = null;
                                continue;
                            }
                            
                            /** logerr(res.encode("UTF-8")); */
                            
                            Element envelopeElem = JDomUtils.buildElementFromString(res.encode("UTF-8"));
                            if (envelopeElem == null) {
                                logger.error("ERR: envelopeElem is null");
                                return null;
                            }
                            Element responseBody = envelopeElem.getChild("Body", ns_envelope);
                            if (responseBody == null) {
                                logger.error("ERR: responseBody is null");
                                return null;
                            }
                            Element pullResponseElem = responseBody.getChild("PullResponse", ns_enumeration);
                            if (pullResponseElem == null) {
                                logger.error("ERR: pullResponseElem is null");
                                return null;
                            }
                            Element itemsElem = pullResponseElem.getChild("Items", ns_enumeration);
                            if (itemsElem == null) {
                                logger.error("ERR: itemsElem is null");
                                return null;
                            }
                            
                            for (Element itemElem : (List<Element>) itemsElem.getChildren(className, ns_class)) {
                                ClassInstance inst = createInstanceFromSoapResponseElement(itemElem);
                                if (inst == null) {
                                    context = null;
                                    continue;
                                } else
                                    result.add(inst);
                            }
                            context = res.context();
                        }
                    }

                    logger.debug(enterLeavePrefix+"Leave enumerateInstances[" + (System.nanoTime() - start) +"]");                    
                }
                return result.toArray(new Classes.ClassInstance[result.size()]);
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public String[] invokeMethod(Client conn, String methodName, InvocationArgument... args) throws Exception {
                ArrayList<String> result = new ArrayList<String>();
                
                synchronized (conn) {
                    long start = System.nanoTime();
                    String enterLeavePrefix = "[" + start + "][" + className + "][" + methodName + "]";
                    logger.debug(enterLeavePrefix + "Enter invokeMethod");
                    
                    ClientOptions options = new ClientOptions();
                    Element requestContext = new Element(methodName + "_INPUT", classUri);
                    
                    //                if((methodName == "DefineSystem") || (methodName == "DefineVirtualSystem"))
                    //                    options.set_dump_request();
                    
                    for (Entry<String, String> e : selectorsSorted) {
                        //                    logerr("adding " + e.getKey() + ": " + e.getValue());
                        options.add_selector(e.getKey(), e.getValue());
                    }
                    
                    for (InvocationArgument arg : args)
                        for (String item : arg.getXmlRepresentation(classUri))
                            requestContext.addContent(JDomUtils.buildElementFromString(item));
                    
                    String requestString = JDomUtils.getXml(requestContext);
                    
                    XmlDoc res = conn.invoke(
                        options, classUri, methodName,
                        OpenWSMan.create_doc_from_string(requestString, classUri));
                    
                    if ((res == null) || res.isFault()) {
                        //                    logerr("Invoke failed: " + ((res != null) ? res.fault().reason() : "?"));
                        //                    logerr((res != null) ? res.toString() : "");
                        String errMessage = "Invoke failed: " + ((res != null) ? res.fault().reason() : "?") + "\n";
                        errMessage += "Request:\n" + requestString + "\n";
                        errMessage += "Reponse:\n" + ((res != null) ? res.toString() : "");
                        throw new Exception(errMessage);
                    } else {
                        Element responseElem = JDomUtils.buildElementFromString(res.encode("UTF-8"));
                        //logerr(JDomUtils.getPrettyXml(responseElem));
                        
                        for (Element elem : (List<Element>) responseElem.getChild("Body", ns_envelope).
                            getChild(methodName + "_OUTPUT", ns_class).getChildren())
                            result.add(JDomUtils.getXml(elem, true, true));
                    }
                    //                return new String[0];
                    logger.debug(enterLeavePrefix+"Leave invokeMethod[" + (System.nanoTime() - start) +"]");    
                }
                
                return result.toArray(new String[result.size()]);
            }
        };
        
        if (initselecctors != null)
            for (Entry<String, String> sel : initselecctors)
                result.addSelector(sel.getKey(), sel.getValue());
        
        return result;
    }
    
    private ClassInstanceProperty instantiateClassInstanceProperty(final Element e) {
        return new ClassInstanceProperty() {
            @Override
            public ClassInstancePropertyType getPropertyType() {
                String name = e.getName();
                if (name.endsWith(".ARRAY"))
                    return ClassInstancePropertyType.SIMPLE_ARRAY_VALUE;
                else if (name.endsWith(".REFERENCE"))
                    return ClassInstancePropertyType.REFERENCE_VALUE;
                else if (name.endsWith(".OBJECT"))
                    return ClassInstancePropertyType.OBJECT_VALUE;
                else if (name.endsWith(".OBJECTARRAY"))
                    return ClassInstancePropertyType.OBJECT_ARRAY_VALUE;
                else if (name.contains("."))
                    return ClassInstancePropertyType.UNKNOWN_VALUE;
                else
                    return ClassInstancePropertyType.SIMPLE_VALUE;
            }
            
            @Override
            public String getSimpleValue() {
                if (this.getPropertyType() != ClassInstancePropertyType.SIMPLE_VALUE)
                    throw new java.lang.IllegalAccessError();
                return e.getChildText("VALUE");
            }
            
            @Override
            public void setSimpleValue(String value) {
                if (this.getPropertyType() != ClassInstancePropertyType.SIMPLE_VALUE)
                    throw new java.lang.IllegalAccessError();
                Element valElem = e.getChild("VALUE");
                if (value != null) {
                    if (value.isEmpty()) {
                        if (valElem != null)
                            e.removeChild("VALUE");
                    } else {
                        if (this.getValueType().toLowerCase().equals("boolean"))
                            value = value.toUpperCase();
                        if (valElem != null)
                            valElem.setText(value);
                        else {
                            valElem = new Element("VALUE");
                            valElem.setText(value);
                            e.addContent(valElem);
                        }
                    }
                }
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public String[] getSimpleArrayValue() {
                if (this.getPropertyType() != ClassInstancePropertyType.SIMPLE_ARRAY_VALUE)
                    throw new java.lang.IllegalAccessError();
                List<String> result = new ArrayList<String>();
                Element valArrElem = e
                    .getChild("VALUE.ARRAY");
                if (valArrElem != null)
                    for (Element elem : (List<Element>) valArrElem.getChildren("VALUE"))
                        result.add(elem.getText());
                return result.toArray(new String[result.size()]);
            }
            
            @Override
            public void setSimpleArrayValue(String[] value) {
                if (this.getPropertyType() != ClassInstancePropertyType.SIMPLE_ARRAY_VALUE)
                    throw new java.lang.IllegalAccessError();
                Element valArrElem = e
                    .getChild("VALUE.ARRAY");
                if (valArrElem == null) {
                    valArrElem = new Element("VALUE.ARRAY");
                    e.addContent(valArrElem);
                }
                valArrElem.removeChildren("VALUE");
                Boolean isBoolean = this.getValueType().toLowerCase().equals("boolean");
                if (value != null)
                    for (String i : value)
                        valArrElem.addContent(new Element("VALUE").setText(isBoolean ? i.toUpperCase() : i));
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public ClassReference getReferenceValue() {
                if (this.getPropertyType() != ClassInstancePropertyType.REFERENCE_VALUE)
                    throw new java.lang.IllegalAccessError();
                //Example:
                //
                //<VALUE.REFERENCE>
                //  <INSTANCEPATH>
                //      <NAMESPACEPATH>
                //          <HOST>LUVYU01-HYPERV</HOST>
                //          <LOCALNAMESPACEPATH>
                //              <NAMESPACE NAME="root"/>
                //              <NAMESPACE NAME="virtualization"/>
                //              <NAMESPACE NAME="v2"/>
                //          </LOCALNAMESPACEPATH>
                //      </NAMESPACEPATH>
                //      <INSTANCENAME CLASSNAME="Msvm_Memory">
                //          <KEYBINDING NAME="CreationClassName">
                //              <KEYVALUE VALUETYPE="string">Msvm_Memory</KEYVALUE>
                //          </KEYBINDING>
                //          <KEYBINDING NAME="DeviceID">
                //              <KEYVALUE VALUETYPE="string">Microsoft:4764334E-E001-4176-82EE-5594EC9B530E\0</KEYVALUE>
                //          </KEYBINDING>
                //      </INSTANCENAME>
                //  </INSTANCEPATH>
                //</VALUE.REFERENCE>
                Element refValElem = e.getChild("VALUE.REFERENCE");
                if (refValElem == null)
                    return null;
                else {
                    Element instancePath = refValElem.getChild("INSTANCEPATH");
                    Element instanceName = instancePath.getChild("INSTANCENAME");
                    String className = instanceName.getAttributeValue("CLASSNAME");
                    HashMap<String, String> selectors = new HashMap<String, String>();
                    
                    for (Element elem : (List<Element>) instanceName.getChildren("KEYBINDING"))
                        selectors.put(elem.getAttributeValue("NAME"), elem.getChildText("KEYVALUE"));
                    
                    return instantiateClassReference(getClassesDefMap().get(className),
                        selectors.entrySet().toArray(new Map.Entry[selectors.size()]));
                }
            }
            
            @Override
            public void setReferenceValue(ClassReference value) {
                if (this.getPropertyType() != ClassInstancePropertyType.REFERENCE_VALUE)
                    throw new java.lang.IllegalAccessError();
                e.removeChild("VALUE.REFERENCE");
                if (value != null)
                    e.addContent(JDomUtils.buildElementFromString(value.getDtdValueRefText()));
            }
            
            @Override
            public String getPropertyName() {
                return e.getAttributeValue("NAME");
            }
            
            @Override
            public String getValueType() {
                if (this.getPropertyType() == ClassInstancePropertyType.REFERENCE_VALUE ||
                    this.getPropertyType() == ClassInstancePropertyType.OBJECT_VALUE ||
                    this.getPropertyType() == ClassInstancePropertyType.OBJECT_ARRAY_VALUE)
                    return e.getAttributeValue("REFERENCECLASS");
                else
                    return e.getAttributeValue("TYPE");
            }
            
            @Override
            public ClassInstance getObjectValue() {
                if (this.getPropertyType() != ClassInstancePropertyType.OBJECT_VALUE)
                    throw new java.lang.IllegalAccessError();
                
                Element valObj = e.getChild("VALUE.OBJECT");
                if (valObj == null)
                    return null;
                else
                    return instantiateClassInstance(new Document().setRootElement((Element) valObj.getChild("INSTANCE").clone()));
            }
            
            @Override
            public void setObjectValue(ClassInstance value) {
                if (this.getPropertyType() != ClassInstancePropertyType.OBJECT_VALUE)
                    throw new java.lang.IllegalAccessError();
                
                e.removeChild("VALUE.OBJECT");
                if (value != null) {
                    Element valObjElem = new Element("VALUE.OBJECT");
                    valObjElem.addContent(JDomUtils.buildElementFromString(value.getDtdText()));
                    e.addContent(valObjElem);
                }
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public ClassInstance[] getObjectArrayValue() {
                if (this.getPropertyType() != ClassInstancePropertyType.OBJECT_ARRAY_VALUE)
                    throw new java.lang.IllegalAccessError();
                
                Element valArrObj = e.getChild("VALUE.OBJECTARRAY");
                if (valArrObj == null)
                    return null;
                else {
                    ArrayList<ClassInstance> res = new ArrayList<ClassInstance>();
                    for (Element valObj : (List<Element>) valArrObj.getChildren("VALUE.OBJECT"))
                        res.add(instantiateClassInstance(JDomUtils.buildDocumentFromString(JDomUtils.getXml(valObj.getChild("INSTANCE")))));
                    return res.toArray(new ClassInstance[res.size()]);
                }
            }
            
            @Override
            public void setObjectArrayValue(ClassInstance[] values) {
                if (this.getPropertyType() != ClassInstancePropertyType.OBJECT_ARRAY_VALUE)
                    throw new java.lang.IllegalAccessError();
                
                e.removeChild("VALUE.OBJECTARRAY");
                if (values == null)
                    return;
                
                Element objArrayElem = new Element("VALUE.OBJECTARRAY");
                for (ClassInstance inst : values)
                    objArrayElem.addContent(JDomUtils.buildElementFromString(inst.getDtdText()));
                
                e.removeChild("VALUE.OBJECTARRAY");
                e.addContent(objArrayElem);
            }
        };
    }
    
    private ClassInstance instantiateClassInstance(final Document instDoc) {
        return new ClassInstance() {
            private List<ClassInstanceProperty> props;
            
            @Override
            public ClassInstanceProperty getProperty(String propName) {
                for (ClassInstanceProperty p : this.props)
                    if (p.getPropertyName().equals(propName))
                        return p;
                return null;
            }
            
            @Override
            public String dumpPropertiesForDebug() {
                String result = "";
                for (ClassInstanceProperty p : this.getProperties()) {
                    result += p.getPropertyName() + "\n";
                    result += "  " + p.getPropertyType().toString() + ": " + p.getValueType() + "\n";
                    switch (p.getPropertyType()) {
                    case SIMPLE_VALUE:
                        result += "    " + p.getSimpleValue() + "\n";
                        break;
                    case SIMPLE_ARRAY_VALUE:
                        for (String v : p.getSimpleArrayValue())
                            result += "    " + v + "\n";
                        break;
                    default:
                        continue;
                    }
                }
                return result;
            }
            
            @Override
            public ClassInstanceProperty[] getProperties() {
                return this.props.toArray(new ClassInstanceProperty[this.props.size()]);
            }
            
            @Override
            public String getDtdText() {
                return JDomUtils.getXml(instDoc);
            }
            
            @Override
            public String getDtdText(String overrideClassName) {
                Document cloneDoc = (Document) instDoc.clone();
                cloneDoc.getRootElement().setAttribute("CLASSNAME", overrideClassName);
                return JDomUtils.getXml(cloneDoc);
            }
            
            @Override
            public String getClassName() {
                return instDoc.getRootElement().getAttributeValue("CLASSNAME");
            }
            
            @SuppressWarnings("unchecked")
            ClassInstance initProps() {
                this.props = new ArrayList<ClassInstanceProperty>();
                
                for (Element e : (List<Element>) instDoc.getRootElement().getChildren())
                    if (!e.getName().contains("PROPERTY"))
                        continue;
                    else {
                        ClassInstanceProperty cip = instantiateClassInstanceProperty(e);
                        cip.getValueType();
                        props.add(cip);
                    }
                return this;
            }
        }.initProps();
    }
    
    private ClassDefinition instantiateClassDefinition(String classDefXml) {
        final String[] parts = classDefXml.split(":", 2);
        final String className = parts[1].split("\"", 3)[1];
        
        return new ClassDefinition() {
            
            @Override
            public String getUri() {
                return commonPrefix + parts[0].replace('\\', '/') + "/" + this.getClassName();
            }
            
            @Override
            public String getClassName() {
                return className;
            }
            
            @Override
            public ClassInstance createLocalInstance() {
                return instantiateClassInstance(JDomUtils.buildDocumentFromString(parts[1]));
            }
        };
    }
}
