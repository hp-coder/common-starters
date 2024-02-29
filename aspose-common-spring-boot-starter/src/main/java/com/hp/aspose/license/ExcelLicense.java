package com.hp.aspose.license;

import com.aspose.cells.License;

/**
 * @author hp 2023/4/20
 */
public class ExcelLicense implements AsposeLicense {
    @Override
    public void license(LicenseLoader licenseLoader) {
        AsposeLicense.chinese();
        final License license = new License();
        license.setLicense(licenseLoader.licenseLoader());
    }

}
