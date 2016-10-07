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

    // FIXME use the Class Names, Luke
    public static final By COMPUTATION_MARKER_1ST_CELL_XPATH = By.xpath("/html/body/div[4]/div[2]/div/div/div/div[1]/div/div[3]/div/div/div/div[2]/div[2]/div/div/div/span/span");

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

    // puts a text into input cell
    // awaits computation start
    public void writeTo(final int i, String text) {
        // calculating input cell index
        // cells are separated by trash div in between each pair
        final int realI = 2 * i + 1;
        // FIXME begs for human-readable classnames
        final By locator = By.xpath("/html/body/div[4]/div[2]/div/div/div/div[1]/div/div[1]/div/div/div[2]/div/div/div[1]/div[" + realI + "]");

        final WebElement cell = driver.findElement(locator);
        new Actions(driver)
                .sendKeys(cell, text)
                .build().perform();

        // check computation started
        final By xpath = By.xpath(getOutputCellXPathLocator(i));
        new WebDriverWait(driver, 10, 20)
                .until((WebDriver driver) -> driver.findElement(xpath));
    }

    // extracts any text from computation result cell
    // awais for computation completion
    public String textFrom(int i) {

        // check computation ended
        final By xpath = By.xpath(getOutputCellXPathLocator(i) + "/span/span");
        new WebDriverWait(driver, COMPUTATION_TIMEOUT, 100)
                .until((WebDriver driver) -> driver.findElements(xpath).isEmpty());

        final By cellLocator = By.xpath(getOutputCellXPathLocator(i));

        // try extract some text
        return new WebDriverWait(driver, COMPUTATION_TIMEOUT).until((WebDriver wd) -> {
            final String text = driver.findElement(cellLocator).getText();
            return text.isEmpty() ? null : text;
        });
    }

    // FIXME begs for human-readable class names
    private String getOutputCellXPathLocator(int i) {
        // xpath indexes are from 1
        i = i + 1;
        return "/html/body/div[4]/div[2]/div/div/div/div[1]/div/div[3]/div/div/div/div[2]/div[2]/div/div/div[" + i + "]";
    }
}
