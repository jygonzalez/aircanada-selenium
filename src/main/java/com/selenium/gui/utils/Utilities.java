package com.selenium.gui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utilities {

	public static final int IMPLICIT_WAIT_TIMEOUT = 10;
	public static final int EXPLICIT_WAIT_TIMEOUT = 20;
	public static final int PAGE_LOAD_TIMEOUT = 10;
	public static final int CLICKABLE_ELEMENT_WAIT_TIMEOUT = 1;

	public static Properties loadConfigProperties() {

		Properties configProperties = new Properties();
		File configPropertiesFile = new File(
				System.getProperty("user.dir") + "\\src\\main\\java\\com\\selenium\\gui\\config\\config.properties");

		try {
			FileInputStream fileInputStream = new FileInputStream(configPropertiesFile);
			configProperties.load(fileInputStream);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return configProperties;
	}

	public static String captureScreenshot(WebDriver driver, String testName) {

		File srcScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String destScreenshotPath = System.getProperty("user.dir") + "\\Screenshots\\" + testName + ".png";

		try {
			FileHandler.copy(srcScreenshot, new File(destScreenshotPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return destScreenshotPath;
	}

	public static void waitAndClick(WebDriver driver, WebElement element, int timeoutInSeconds) {

		Actions actions = new Actions(driver);
		actions.moveToElement(element).perform();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
	}

	public static void waitInvisibilityOfElement(WebDriver driver, WebElement element, int timeoutInSeconds) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
		wait.until(ExpectedConditions.invisibilityOf(element));
	}

	public static void selectOptionFromDropdown(WebDriver driver, WebElement dropdown, List<WebElement> options,
			String value) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT));

		wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

		for (WebElement option : options) {
			if (option.getText().trim().equalsIgnoreCase(value)) {
				option.click();
				return;
			}
		}

		throw new NoSuchElementException("Option not found: " + value);
	}
}
