package org.example.demo.constants;

public class ContractConstants {
    public static final String HelloWorldAbi;
    public static final String HelloWorldBinary;
    public static final String HelloWorldGmBinary;

    public static final String PatientAbi;
    public static final String PatientBinary;

    static {
        try {
            HelloWorldAbi =
                    org.apache.commons.io.IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("abi/HelloWorld.abi"));
            HelloWorldBinary =
                    org.apache.commons.io.IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("bin/HelloWorld.bin"));
            HelloWorldGmBinary =
                    org.apache.commons.io.IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("bin/HelloWorldSM.bin"));
            PatientAbi = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("abi/Patient.abi"));
            PatientBinary = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("bin/Patient.bin"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
