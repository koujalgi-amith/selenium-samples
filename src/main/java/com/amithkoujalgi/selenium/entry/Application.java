package com.amithkoujalgi.selenium.entry;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
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
			System.out.println("Found " + userCredCombo.length + " Facebook credentials.");
			for (String ucc : userCredCombo) {
				String[] userCreds = ucc.split("::");
				String username = userCreds[0].trim();
				String password = userCreds[1].trim();
				executor.execute(new FBWorker(username, password));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		executor.shutdown();
		while (true) {
			if (executor.isTerminated()) {
				System.out.println("All tasks have completed.");
				break;
			}
		}
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
		System.out.println("Opening browser window for '" + username + "'...");
		FirefoxDriver d = new FirefoxDriver();
		System.out.println("Maximizing browser window for '" + username + "'...");
		d.manage().window().maximize();
		System.out.println("Opening Facebook page for '" + username + "'...");
		d.get("http://www.facebook.com");
		System.out.println("Entering Facebook credentials for '" + username + "'...");
		d.findElement(By.id("email")).sendKeys(username);
		d.findElement(By.id("pass")).sendKeys(password);
		System.out.println("Signing into Facebook for '" + username + "'...");
		d.findElement(By.id("loginbutton")).click();

		waitUp(d, By.tagName("body"), 10);

		if (d.getPageSource().contains("trying too often")) {
			System.err.println("Too many sign in attempts for '" + username
					+ "'. Couldn't login. (You won't be able to even login manually. You can try logging in after sometime though) Terminating browser window...");
			try {
				d.close();
			} catch (Exception e) {
			}
			return;
		}
		if (d.getCurrentUrl().contains("login_attempt")) {
			System.err.println("The credentials [" + username + ", " + password
					+ "] doesn't seem to be right. Terminating the browser window...");
			try {
				d.close();
			} catch (Exception e) {
			}
			return;
		} else {
			try {
				System.out.println("Clicking on the user profile button for '" + username + "'...");
				String userProfileBtnXpath = "//div[@id='u_0_2']/div/div/div/a/img";
				waitUp(d, By.xpath(userProfileBtnXpath), 10);
				d.findElement(By.xpath(userProfileBtnXpath)).click();

				System.out.println("Finding profile image for '" + username + "'...");
				String userProfilePicXpath = "//div[@id='fbProfileCover']/div[2]/div[3]/div/div/a/img";
				waitUp(d, By.xpath(userProfilePicXpath), 20);
				String profilePicURL = d.findElement(By.xpath(userProfilePicXpath)).getAttribute("src");
				System.out.println("Downloading profile pic of '" + username + "'...");
				downloadImg(profilePicURL, "profile-pic");
			} catch (Exception e) {
				if (e instanceof java.net.ConnectException) {
					System.err.println("Browser window for '" + username + "' was closed abruptly. ");
				} else {
					System.err.println("Error for '" + username + "': " + e.getMessage());
				}
			} finally {
				System.out.println("Closing browser window for '" + username + "'...");
				d.close();
			}
		}
	}

	public void waitUp(WebDriver d, By by, int timeoutSeconds) {
		d.manage().timeouts().implicitlyWait(timeoutSeconds, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(d, timeoutSeconds);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public void downloadImg(String imgurl, String name) throws IOException {
		String dir = System.getProperty("user.home");
		File parentDir = new File(
				dir + File.separator + "Selenium" + File.separator + "facebook" + File.separator + username);
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File profileImg = new File(parentDir.getAbsolutePath() + File.separator + name + ".jpg");

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