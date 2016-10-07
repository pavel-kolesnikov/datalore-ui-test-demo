package me.pkolesnikov.selenium;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

@Test
public final class DataloreSmokeTest {
    private static final long IMPLICIT_TIMEOUT_SECONDS = 10;
    private static final String HOME_URL = "https://datalore.io/";
    private static final String CONFIG_FILE = "datalore.properties";

    private static ChromeDriverService service;
    private static Config cfg;
    private WebDriver driver;


    @BeforeTest
    public static void createAndStartService() throws IOException {
        service = new ChromeDriverService.Builder().build();
        service.start();

        try {
            cfg = Config.from(Paths.get(System.getProperty("user.home"), CONFIG_FILE));
        } catch (FileNotFoundException e) {
            // handled in Config, just exit here
            System.exit(1);
        }
    }

    @AfterTest
    public static void createAndStopService() {
        service.stop();
    }

    @BeforeMethod
    public void setupDriver() {
        driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
        driver.manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
        driver.get(HOME_URL);

        new Profile(driver).login(cfg.user, cfg.pass);
        Navigation.selfCheck(driver);
    }

    @AfterMethod
    public void killWebDriver() {
        driver.quit();
    }

    @Test
    public void smoke() throws InterruptedException {
        final Navigation nav = new Navigation(driver);

        final String docTitle = "my rocket science note";
        nav.tryRemoveDocument(docTitle);
        nav.createDocument(docTitle);

        final Document doc = nav.openDocument(docTitle);
        final int cell = 0;
        doc.writeTo(cell, "a = 1 + 1\nprint(a)");
        assertEquals(doc.textFrom(cell), "2\n<empty>");

        nav.closeDocument();

        final Document openSecondTime = nav.openDocument(docTitle);
        assertEquals(openSecondTime.textFrom(cell), "2\n<empty>");
    }

    @Test(dependsOnMethods = {"smoke"})
    public void loadAgain() throws InterruptedException {
        final Navigation nav = new Navigation(driver);

        final String docTitle = "my rocket science note";
        final int cell = 0;
        final Document openSecondTime = nav.openDocument(docTitle);
        assertEquals(openSecondTime.textFrom(cell), "2\n<empty>");
    }
}
