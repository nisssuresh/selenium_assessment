package com.nisarga;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class FlipkartTest {

    public static void main(String[] args) throws InterruptedException {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get("https://www.flipkart.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Close popup
        try {
            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'✕')]")));
            closeBtn.click();
        } catch (Exception e) {
            driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
        }

        // Search
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
        search.sendKeys("Bluetooth Speakers");
        search.submit();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href,'/p/')]")));

        // Rating filter
        try {
            WebElement rating = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(text(),'4') and contains(text(),'above')]")));
            js.executeScript("arguments[0].click();", rating);
            System.out.println("Rating filter applied");
        } catch (Exception e) {
            System.out.println("Rating filter not found");
        }

        // Brand filter
        try {
            WebElement brand = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[text()='Brand']")));

            js.executeScript("arguments[0].scrollIntoView(true);", brand);
            Thread.sleep(2000);

            List<WebElement> more = driver.findElements(
                    By.xpath("//div[text()='Brand']/following::div[text()='More'][1]"));

            if (!more.isEmpty()) {
                js.executeScript("arguments[0].click();", more.get(0));
            }

            WebElement boat = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[text()='boAt']/ancestor::label")));

            js.executeScript("arguments[0].click();", boat);

            System.out.println("Brand filter applied");

            Thread.sleep(4000);

        } catch (Exception e) {
            System.out.println("Brand filter step skipped");
        }

        // Sort
        try {
            WebElement sort = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(text(),'Low to High')]")));
            js.executeScript("arguments[0].click();", sort);
            System.out.println("Sorting applied");
        } catch (Exception e) {
            System.out.println("Sorting not found");
        }

        // ===== CHECK boAt PRODUCT =====
        List<WebElement> boatProducts = driver.findElements(
                By.xpath("//a[contains(text(),'boAt')]"));

        if (boatProducts.isEmpty()) {

            System.out.println("Scenario 2: boAt product NOT available");

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            screenshot.renameTo(new File("result.png"));

            driver.quit();
            return;
        }

        // ===== OPEN PRODUCT =====
        try {
            WebElement product = boatProducts.get(0);

            js.executeScript("arguments[0].click();", product);

            for (String tab : driver.getWindowHandles()) {
                driver.switchTo().window(tab);
            }

            System.out.println("boAt product opened");

        } catch (Exception e) {
            System.out.println("Error opening product");
        }

        // Check offers
        try {
            int offers = driver.findElements(
                    By.xpath("//span[contains(text(),'Available offers')]")).size();
            System.out.println("Offers count: " + offers);
        } catch (Exception e) {
            System.out.println("Offers not found");
        }

        // ===== ADD TO CART (FIXED SCREENSHOT LOGIC) =====
        try {

            List<WebElement> addToCart = driver.findElements(
                    By.xpath("//button[.//span[contains(text(),'Add')]]"));

            if (!addToCart.isEmpty()) {

                addToCart.get(0).click();
                System.out.println("Product added to cart");

                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                screenshot.renameTo(new File("cart_result.png"));

            } else {

                System.out.println("Add to cart not available");

                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                screenshot.renameTo(new File("result.png"));
            }

        } catch (Exception e) {

            System.out.println("Error in cart step");

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            screenshot.renameTo(new File("error.png"));
        }

        driver.quit();
    }
}