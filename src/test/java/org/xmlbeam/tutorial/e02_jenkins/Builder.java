package org.xmlbeam.tutorial.e02_jenkins;

public interface Builder {
    @org.xmlbeam.Xpath("child::jvmOptions")
    String getJVMOptions();
}
