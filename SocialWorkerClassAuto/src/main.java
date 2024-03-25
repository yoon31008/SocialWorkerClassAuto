import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.Keys;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class main {

	public static String attendCheck;
	public static String learnCheck;
	public static String attendPercent;
	public static String requiredClassTime;
	public static String finishedClassTime;
	public static String requiredClassMin;
	public static String requiredClassSec;
	public static String finishedClassMin;
	public static String finishedClassSec;

	public static int learn_td;
	public static int attendCheck_td;
	public static int minIdx;
	public static int secIdx;
	public static int requiredClassMiliSec;
	public static int finishedClassMiliSec;
	public static int remainClassMiliSec;

	public static By nextBtn;

	public static WebElement learn = null;
	public static WebElement attend = null;
	public static WebElement classDoor = null;
	public static WebElement iframe = null;

	public static void main(String[] args) throws InterruptedException, AWTException, IOException {
		WebDriverManager.chromedriver().setup();

		// System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");
		WebDriver driver = new ChromeDriver();

		driver.get("https://past-eglobal.gachon.ac.kr/home/login/login.jsp");

		driver.manage().window().maximize();

		Robot robot = new Robot();

		// ȭ�����
		for (int j = 0; j < 4; j++) {
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));

		// ���� â�� ID�� ����
		String mainWindow = driver.getWindowHandle();

		driver.findElement(By.xpath("/html/body/div[1]/ul/li[3]/a[1]")).click();
		wait.until(ExpectedConditions.alertIsPresent()).dismiss();
		driver.findElement(By.xpath("/html/body/ul/li[2]/div[2]")).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("do_certiLogin"))).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("certPwd"))).sendKeys("vkfkd801*)!");
		driver.findElement(By.xpath("//*[@id=\"nx-cert-select\"]/div[4]/button[1]")).click();

		Thread.sleep(2000);

		// �˾�������ݱ�
		for (String handle : driver.getWindowHandles()) {
			if (!handle.equals(mainWindow)) {
				driver.switchTo().window(handle).close();
			}
		}

		driver.switchTo().window(mainWindow);

		Actions action = new Actions(driver);

		for (int a = 1; a <= 17; a++) {
			// ���� ���ǽ� Ŭ��
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/ul/li[3]/a[1]"))).click();

			// ���ǽ� �������� ��ũ�� �̵�
			classDoor = driver.findElement(By.xpath("/html/body/div[5]/div/table/tbody/tr[" + a + "]/td[6]/a/div"));

			action.moveToElement(classDoor).perform();

			// ���ǽ� ���� Ŭ��
			classDoor.click();

			// �� ���� ���� 27����
			for (int i = 1; i <= 27; i++) {

				if (i == 15)
					continue;

				if (i <= 15) {
					if (i % 2 == 1) {
						learn_td = 6;
						attendCheck_td = 5;
					} else {
						learn_td = 5;
						attendCheck_td = 4;
					}
				} else {
					if (i % 2 == 1) {
						learn_td = 5;
						attendCheck_td = 4;
					} else {
						learn_td = 6;
						attendCheck_td = 5;
					}
				}

				// �н��ϱ� ��ũ�� �̵�
				learn = driver.findElement(
						By.xpath("//*[@id=\"lesson_list\"]/table/tbody/tr[" + i + "]/td[" + learn_td + "]/div"));
				action.moveToElement(learn).perform();

				attend = driver.findElement(By
						.xpath("//*[@id=\"lesson_list\"]/table/tbody/tr[" + i + "]/td[" + attendCheck_td + "]/a/div"));

				attendCheck = attend.getAttribute("class");

				learnCheck = learn.getAttribute("class");

				if (attendCheck.equals("study_yet") && learnCheck.equals("class_ent")) {

					attendPercent = attend.getText();

					// �������� 0%�� �ƴϸ�
					if (!attendPercent.equals("0%")) {

						attend.click();

						// �˾�â���� ��Ŀ�� �̵�
						for (String handle : driver.getWindowHandles()) {
							if (!handle.equals(mainWindow)) {
								driver.switchTo().window(handle);
							}
						}

						wait.until(ExpectedConditions
								.presenceOfElementLocated(By.xpath("/html/body/div/table/tbody/tr[4]/td[6]")));

						// ���ؽð� �� ����
						requiredClassTime = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[4]/td[6]"))
								.getText();

						finishedClassTime = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[4]/td[7]"))
								.getText();

						minIdx = requiredClassTime.indexOf("��");
						secIdx = requiredClassTime.indexOf("��");

						requiredClassMin = requiredClassTime.substring(0, minIdx);
						requiredClassSec = requiredClassTime.substring(minIdx + 1, secIdx);

						if (finishedClassTime.contains("��")) {
							minIdx = finishedClassTime.indexOf("��");
							secIdx = finishedClassTime.indexOf("��");
							
							finishedClassMin = finishedClassTime.substring(0, minIdx);
							finishedClassSec = finishedClassTime.substring(minIdx + 1, secIdx);
						} else {
							secIdx = finishedClassTime.indexOf("��");
							
							finishedClassMin = "0";
							finishedClassSec = finishedClassTime.substring(0, secIdx);
						}						

						requiredClassMiliSec = (Integer.parseInt(requiredClassMin) * 60
								+ Integer.parseInt(requiredClassSec)) * 1000;
						finishedClassMiliSec = (Integer.parseInt(finishedClassMin) * 60
								+ Integer.parseInt(finishedClassSec)) * 1000;

						// �����ϴ� ������ ���� �ð�(�и���)
						remainClassMiliSec = requiredClassMiliSec - finishedClassMiliSec + 10000;

						// �����ð� �ȳ� ������ݱ�
						for (String handle : driver.getWindowHandles()) {
							if (!handle.equals(mainWindow)) {
								driver.switchTo().window(handle).close();
							}
						}

						driver.switchTo().window(mainWindow);
					}

					// �н��ϱ� Ŭ��
					learn.click();

					wait.until(ExpectedConditions.alertIsPresent()).dismiss();

					// ���ǽ� iframe ����
					iframe = driver.findElement(By.cssSelector("#content"));

					// Switch to the frame
					driver.switchTo().frame(iframe);

					// ���Ǿ��� �ؽ�Ʈ ��ư Ŭ��
					nextBtn = By.cssSelector("#sideUI > div > div > button.next");

					for (int k = 0; k < 3; k++) {
						wait.until(ExpectedConditions.elementToBeClickable(nextBtn)).click();
						Thread.sleep(2000);
					}

					Thread.sleep(1000);

					// ���Ǳ��̽ð� ��������
					String classLength = wait
							.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
									"#bottomUI > div > div.groub > div.progress > div.time > div.total_time")))
							.getText();

					int idx = classLength.indexOf(":");
					String minute = classLength.substring(0, idx);
					String second = classLength.substring(idx + 1);

					int classLengthMilSecond = (Integer.parseInt(minute) * 60 + Integer.parseInt(second) + 10) * 1000;

					// ���ǽð���ŭ ����
					// ó���� ������ 0%�̸� ���� �ٵ�� �����̶� ���� �������� ���� �ð��� ������
					if (attendPercent.equals("0%")) {
						Thread.sleep(classLengthMilSecond);
					} else {
						Thread.sleep(remainClassMiliSec);
					}

					// ���ǳ��� �� �ؽ�Ʈ ��ư Ŭ��
					driver.findElement(nextBtn).click();

					// ���ϱ� start Ŭ��
					wait.until(ExpectedConditions
							.elementToBeClickable(By.cssSelector("#contents > div > div.page0 > button"))).click();

					// ���ϱ� ��ٸ���
					Thread.sleep(65 * 1000);

					// �򰡳��� �� �ؽ�Ʈ ��ư Ŭ��
					driver.findElement(nextBtn).click();

					// �ǰ��Է� ��ٸ���
					Thread.sleep(22 * 1000);

					// �ǰ��Է� ���� �� �ؽ�Ʈ ��ư Ŭ��
					driver.findElement(nextBtn).click();

					// �н����� ��ٸ���
					Thread.sleep(22 * 1000);

					// �н����� ���� �� �ؽ�Ʈ ��ư Ŭ��
					driver.findElement(nextBtn).click();

					// ���ÿ��� ��ٸ���
					Thread.sleep(1000);

					// iframe ����� ��Ŀ�� ���󺹱�
					driver.switchTo().defaultContent();

					// �н����� Ŭ��
					wait.until(ExpectedConditions
							.elementToBeClickable(By.xpath("//*[@id=\"layer2\"]/div/div/div/input[1]"))).click();

					// ������ �ٽ� �ε�
					driver.get(driver.getCurrentUrl());

				}
			}
		}
		// ��ǻ�� ������� ����
		Runtime.getRuntime().exec("Rundll32.exe powrprof.dll,SetSuspendState Sleep");
	}

}
