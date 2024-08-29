package com.hp.aspose;

import com.hp.aspose.license.ExcelLicense;
import com.hp.aspose.license.LicenseLoader;
import com.hp.aspose.license.WordLicense;
import com.hp.aspose.license.XmlLicenseLoader;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

/**
 * @author hp 2023/4/20
 */
@Configuration
@ComponentScan(basePackages = {"com.hp.aspose.license"})
public class AsposeAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    public LicenseLoader xmlLicenseLoader() {
        return new XmlLicenseLoader();
    }

    public void excelLicense(LicenseLoader licenseLoader) {
        final ExcelLicense excelLicense = new ExcelLicense();
        excelLicense.license(licenseLoader);
    }

    public void wordLicense(LicenseLoader licenseLoader) throws Exception {
        final WordLicense wordLicense = new WordLicense();
        wordLicense.license(licenseLoader);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        try {
            final LicenseLoader licenseLoader = this.xmlLicenseLoader();
            this.excelLicense(licenseLoader);
            this.wordLicense(licenseLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
