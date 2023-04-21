package com.luban.aspose.license;

import com.aspose.words.License;

/**
 * @author hp 2023/4/20
 */
public class WordLicense implements AsposeLicense {
    @Override
    public void license(LicenseLoader licenseLoader) throws Exception {
        AsposeLicense.chinese();
        final License license = new  License();
        license.setLicense(licenseLoader.licenseLoader());
    }
}
