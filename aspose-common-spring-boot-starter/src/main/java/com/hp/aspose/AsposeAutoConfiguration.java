package com.hp.aspose;

import com.hp.aspose.license.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hp 2023/4/20
 */
@Configuration
@ComponentScan(basePackages = {"com.hp.aspose.license"})
public class AsposeAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    public LicenseLoader xmlLicenseLoader() {
        return new XmlLicenseLoader();
    }

    public AsposeLicense excelLicense(LicenseLoader licenseLoader) {
        final ExcelLicense excelLicense = new ExcelLicense();
        excelLicense.license(licenseLoader);
        return excelLicense;
    }

    public AsposeLicense wordLicense(LicenseLoader licenseLoader) throws Exception {
        final WordLicense wordLicense = new WordLicense();
        wordLicense.license(licenseLoader);
        return wordLicense;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            final LicenseLoader licenseLoader = this.xmlLicenseLoader();
            this.excelLicense(licenseLoader);
            this.wordLicense(licenseLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
