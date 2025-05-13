package org.example.demo.constants;

public class ContractConstants {
    public static final String PatientAbi;
    public static final String PatientBinary;

    public static final String InstitutionAbi;
    public static final String InstitutionBinary;

    public static final String InpatientAbi;
    public static final String InpatientBinary;

    //public static final String OutpatientAbi;
    //public static final String OutpatientBinary;

    static {
        try {
            PatientAbi = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("abi/Patient.abi"));
            PatientBinary = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("bin/Patient.bin"));

            InstitutionAbi = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("abi/Institution.abi"));
            InstitutionBinary = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("bin/Institution.bin"));

            InpatientAbi = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("abi/Inpatient.abi"));
            InpatientBinary = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("bin/Inpatient.bin"));

/*
            OutpatientAbi = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("abi/Outpatient.abi"));
            OutpatientBinary = org.apache.commons.io.IOUtils.toString(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("bin/Outpatient.bin"));*/
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
