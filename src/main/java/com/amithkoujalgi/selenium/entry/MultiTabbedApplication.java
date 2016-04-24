package com.amithkoujalgi.selenium.entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amithkoujalgi.selenium.config.Configuration;
import com.amithkoujalgi.selenium.utils.JSONUtils;

public class MultiTabbedApplication {
	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		executor.execute(new TabbedWorker());
		executor.shutdown();
	}
}

class TabbedWorker implements Runnable {

	public TabbedWorker() {
	}

	@Override
	public void run() {
		FirefoxDriver d = new FirefoxDriver();
		d.manage().window().maximize();
		d.get("https://www.bing.com/search?q=");
		int tabsOpened = 0;
		try {
			String queriesString = Configuration.getInstance().getProperty(Configuration.ConfigKey.QUERIES);
			String[] queriesArray = queriesString.split(",");
			for (String q : queriesArray) {
				openTab(d);
				tabsOpened += 1;
				d.get("https://www.bing.com/search?q=" + q.replaceAll("\\s+", "+"));
				captureScreenshot(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tabsOpened; i++) {
			closeTab(d);
		}
		d.close();
	}

	public static int randomNumber(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public void waitUp(WebDriver d, By by, int timeoutSeconds) {
		d.manage().timeouts().implicitlyWait(timeoutSeconds, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(d, timeoutSeconds);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public String openTab(WebDriver d) {
		System.out.println("Opening new tab...");
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
			d.findElement(By.tagName("body")).sendKeys(Keys.COMMAND + "t");
			d.get("https://www.bing.com/search?q=");
			for (String winHandle : d.getWindowHandles()) {
				d.switchTo().window(winHandle);
			}
		} else {
			d.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "t");
			d.get("http://ask.com");
			for (String winHandle : d.getWindowHandles()) {
				d.switchTo().window(winHandle);
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return d.getWindowHandle();
	}

	public void closeTab(WebDriver d) {
		System.out.println("Closing tab...");
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
			d.findElement(By.tagName("body")).sendKeys(Keys.COMMAND + "w");
		} else
			d.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "w");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<String> tabs = new ArrayList<String>(d.getWindowHandles());
		d.switchTo().window(tabs.get(tabs.size() - 1));
	}

	public void captureScreenshot(WebDriver d) {
		File srcFile = ((TakesScreenshot) d).getScreenshotAs(OutputType.FILE);
		String dir = System.getProperty("user.home");
		File destDir = new File(dir + File.separator + "selenium" + File.separator + "screenshots");
		try {
			FileUtils.copyFileToDirectory(srcFile, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Screenshot: " + destDir.getAbsolutePath());
	}
}