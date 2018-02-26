package com.arcserve.winrm;

public interface MethodOutputWithJob extends MethodOutput {
    public ConcreteJob getConcreteJobRef();
}
