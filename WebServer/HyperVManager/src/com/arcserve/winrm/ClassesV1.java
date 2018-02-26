package com.arcserve.winrm;

import java.util.HashMap;
import java.util.Map;

public class ClassesV1 extends Classes {
    private Map<String, ClassDefinition> classesDefMap = new HashMap<String, ClassDefinition>();
    
    @Override
    protected Map<String, ClassDefinition> getClassesDefMap() { return classesDefMap; }
    
    public ClassesV1() {
        setResourceForClasses("com/arcserve/winrm/res/classes-v1.res");
    }
}
