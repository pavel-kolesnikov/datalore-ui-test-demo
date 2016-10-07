package me.pkolesnikov.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Document {
    private static final int COMPUTATION_TIMEOUT = 60;
    private static final int DOCUMENT_LOAD_TIMEOUT = 60;

    private final String title;
    private final WebDriver driver;

    public Document(String title, WebDriver driver) {
        this.title = title;
        this.driver = driver;
        selfCheck(driver);
    }

    public void selfCheck(WebDriver driver) {
        // wait document initialize its server backend
        new WebDriverWait(driver, DOCUMENT_LOAD_TIMEOUT)
                .until((WebDriver d) -> d.getPageSource().contains("Python Process Started"));

        // check name
        final WebElement docTitle = driver.findElement(By.xpath("//a[@title='Rename WorkBook']"));
        Assert.assertEquals(docTitle.getText(), title);
    }

    public void writeTo(int i, String text) {
        // calculating input cell index
        // cells are separated by trash div in between each pair
        i = 2 * i + 1;
        // FIXME begs for human-readable classnames
        final By locator = By.xpath("/html/body/div[4]/div[2]/div/div/div/div[1]/div/div[1]/div/div/div[2]/div/div/div[1]/div[" + i + "]");

        final WebElement cell = driver.findElement(locator);
        new Actions(driver)
                .sendKeys(cell, text)
                .build().perform();

        // check computation started
        // FIXME use class names, Luke
        final By progressLocator = By.xpath("/html/body/div[4]/div[2]/div/div/div/div[1]/div/div[3]/div/div/div/div[2]/div[2]/div/div/div/span/span");
        new WebDriverWait(driver, 10, 20)
                .until((WebDriver driver) -> driver.findElement(progressLocator));
    }

    public String textFrom(int i) {
        // xpath indexes are from 1
        i = i + 1;
        // FIXME begs for human-readable classnames
        final By locator = By.xpath("/html/body/div[4]/div[2]/div/div/div/div[1]/div/div[3]/div/div/div/div[2]/div[2]/div/div/div[" + i + "]");

        // wait calculation to complete
        return new WebDriverWait(driver, COMPUTATION_TIMEOUT).until((WebDriver wd) -> {
            final String text = driver.findElement(locator).getText();
            return text.isEmpty() ? null : text;
        });
    }
}
