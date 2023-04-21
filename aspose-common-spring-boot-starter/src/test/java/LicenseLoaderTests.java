import com.luban.aspose.license.ExcelLicense;
import com.luban.aspose.license.XmlLicenseLoader;
import org.junit.jupiter.api.Test;

/**
 * @author hp 2023/4/20
 */
public class LicenseLoaderTests {
    @Test
    public void test_loader() {

        final XmlLicenseLoader xmlLicenseLoader = new XmlLicenseLoader();
        final ExcelLicense excelLicense = new ExcelLicense();
        excelLicense.license(xmlLicenseLoader);
    }
}
