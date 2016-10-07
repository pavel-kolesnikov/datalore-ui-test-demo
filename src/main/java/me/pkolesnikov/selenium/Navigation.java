package me.pkolesnikov.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class Navigation {
    private final WebDriver driver;

    public Navigation(WebDriver driver) {
        this.driver = driver;

        selfCheck(driver);
    }

    public static void selfCheck(WebDriver driver) {
        new WebDriverWait(driver, 30)
                .until(ExpectedConditions.elementToBeClickable(By.className("actions-bar")));
    }

    public void createDocument(String name) throws InterruptedException {
        driver.findElement(By.cssSelector("span[title='New Workbook']")).click();

        // try to rename new document
        final WebElement input = new WebDriverWait(driver, 5).until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver input) {
                // FIXME needs apropriate class or id on element
                final WebElement e = driver.switchTo().activeElement();
                final boolean ok = "input".equals(e.getTagName()) && e.getAttribute("value").contains("New Workbook");
                return ok ? e : null;
            }
        });

        input.sendKeys(name);
        input.sendKeys("\n");
    }

    public void removeDocument(String title) {
        getItem(title).click();
        driver.findElement(By.cssSelector("span[title='Delete']")).click();
    }

    public void tryRemoveDocument(String title) {
        try {
            // FIXME if no element created will hang for full implicit timeout
            removeDocument(title);
        } catch (NoSuchElementException ignored) {
        }
    }


    public Document openDocument(String title) throws InterruptedException {
        final WebElement docLink = getItem(title);

        new Actions(driver)
                .doubleClick(docLink)
                .build().perform();

        switchToNewWindow();

        return new Document(title, driver);
    }

    private WebElement getItem(String title) {
        return driver.findElement(
                By.xpath(".//span[contains(@class,'ts-file-name') and contains(text(), '" + title + "')]"));
    }

    private void switchToNewWindow() throws InterruptedException {
        final String mainTab = driver.getWindowHandle();

        new WebDriverWait(driver, 30)
                .until((WebDriver d) -> d.getWindowHandles().size() > 1);

        final String otherTab = driver.getWindowHandles()
                .stream()
                .filter(x -> !x.equals(mainTab))
                .findFirst().orElseThrow(InvalidElementStateException::new);

        driver.switchTo().window(otherTab);
    }

    public void closeDocument() {
        driver.close();
        driver.switchTo().window(driver.getWindowHandles().iterator().next());

        selfCheck(driver);
    }
}
