package com.amithkoujalgi.selenium.entry;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

public class Application {
	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		try {
			String credsArray = Configuration.getInstance().getProperty(Configuration.ConfigKey.CREDENTIALS);
			JSONUtils.print(credsArray.split(","));
			String[] userCredCombo = credsArray.split(",");
			for (String ucc : userCredCombo) {
				String[] userCreds = ucc.split("::");
				String username = userCreds[0];
				String password = userCreds[1];
				executor.execute(new FBWorker(username, password));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}
}

class FBWorker implements Runnable {
	private String username, password;

	public FBWorker(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		FirefoxDriver d = new FirefoxDriver();
		d.manage().window().maximize();
		d.get("http://www.facebook.com");
		d.findElement(By.id("email")).sendKeys(username);
		d.findElement(By.id("pass")).sendKeys(password);
		d.findElement(By.id("loginbutton")).click();
		if (d.getCurrentUrl().contains("login_attempt")) {
			System.err.println("The credentials for '" + username
					+ "' doesn't seem to be right. Terminating the browser window...");
			d.close();
		} else {
			try {
				String userProfileBtnXpath = "//div[@id='u_0_2']/div/div/div/a/img";
				waitUp(d, By.xpath(userProfileBtnXpath), 10);
				d.findElement(By.xpath(userProfileBtnXpath)).click();

				String userProfilePicXpath = "//a[@id='u_jsonp_2_5']/img";
				waitUp(d, By.xpath(userProfilePicXpath), 20);
				String profilePicURL = d.findElement(By.xpath(userProfilePicXpath)).getAttribute("src");
				System.out.println("Downloading profile pic of '" + username + "'...");
				downloadImg(profilePicURL);

				if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
					d.findElement(By.xpath(userProfileBtnXpath)).sendKeys(Keys.COMMAND + "t");
				} else
					d.findElement(By.xpath(userProfileBtnXpath)).sendKeys(Keys.CONTROL + "t");
				Thread.sleep(1000);
				ArrayList<String> tabs = new ArrayList<String>(d.getWindowHandles());
				d.switchTo().window(tabs.get(tabs.size() - 1));
				d.get("http://www.facebook.com/saved");
				File scrFile = ((TakesScreenshot) d).getScreenshotAs(OutputType.FILE);
				System.out.println("Screenshot: " + scrFile.getAbsolutePath());
				signout(d);
			} catch (Exception e) {
				if (e instanceof java.net.ConnectException) {
					System.err.println("Browser window for '" + username + "' was closed abruptly. ");
				} else {
					System.err.println("Error for '" + username + "': " + e.getMessage());
					// e.printStackTrace();
				}
			} finally {
				d.close();
			}
		}
	}

	public void signout(WebDriver d) {
		d.findElement(By.id("logoutMenu")).click();
		waitUp(d, By.linkText("Log Out"), 10);
		d.findElement(By.linkText("Log Out")).click();
		System.out.println("Signed out of account '" + username + "'");
	}

	public void waitUp(WebDriver d, By by, int timeoutSeconds) {
		d.manage().timeouts().implicitlyWait(timeoutSeconds, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(d, timeoutSeconds);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		// while (true) {
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		// try {
		// WebElement elem = d.findElement(by);
		// if (elem != null) {
		// break;
		// }
		// } catch (Exception e) {
		// System.out.println("Still waiting " + username);
		// }
		// }

	}

	public void downloadImg(String imgurl) throws IOException {
		String dir = System.getProperty("user.home");
		File parentDir = new File(dir + File.separator + "facebook-profile-pics");
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File profileImg = new File(parentDir.getAbsolutePath() + File.separator + username + ".jpg");

		URL url = new URL(imgurl);
		InputStream in = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1 != (n = in.read(buf))) {
			out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] response = out.toByteArray();

		FileOutputStream fos = new FileOutputStream(profileImg.getAbsolutePath());
		fos.write(response);
		fos.close();
		System.out.println("Downloaded file " + profileImg.getAbsolutePath());
	}
}