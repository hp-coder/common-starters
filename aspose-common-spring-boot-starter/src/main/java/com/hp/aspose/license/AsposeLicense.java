package com.hp.aspose.license;

import java.util.Locale;

/**
 * @author hp 2023/4/20
 */
public interface AsposeLicense {

    void license(LicenseLoader licenseLoader) throws Exception;

    static void chinese() {
        Locale locale = new Locale("zh", "cn");
        Locale.setDefault(locale);
    }
}
