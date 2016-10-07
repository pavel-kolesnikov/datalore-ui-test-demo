package me.pkolesnikov.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class Profile {
    private final WebDriver driver;

    public Profile(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys(username);
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("button.login-button")).click();
    }
}
