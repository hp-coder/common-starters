package com.luban.aspose.license;

import java.io.InputStream;

/**
 * @author hp 2023/4/20
 */
public class XmlLicenseLoader implements LicenseLoader {

    private static final String LICENSE = "License.xml";

    @Override
    public InputStream licenseLoader() {
        return LicenseLoader.class.getClassLoader().getResourceAsStream(LICENSE);
    }
}
