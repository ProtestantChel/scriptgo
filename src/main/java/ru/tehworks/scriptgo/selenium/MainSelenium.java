package ru.tehworks.scriptgo.selenium;

import com.sun.mail.smtp.SMTPTransport;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.*;
import javax.sound.midi.Soundbank;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import ru.tehworks.scriptgo.DAO.TehDAO;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Marat Sadretdinov
 */
public class MainSelenium {

    private Integer id;
    private String car;
    private String placeOfLoading;
    private String placeOfDelivery;
    private String shipmentStart;
    private String shipmentEnd;
    private String loading;
    private String unloading;
    private String NDS;
    private boolean checkSearch;
    private boolean checkOK;
    private ChromeDriver driver;
    final static Logger logger = LoggerFactory.getLogger(MainSelenium.class);
    public boolean isCheckOK() { return checkOK; }

    public void setCheckOK(boolean checkOK) { this.checkOK = checkOK; }

    public boolean isCheckSearch() {
        return checkSearch;
    }

    public void setCheckSearch(boolean checkSearch) {
        this.checkSearch = checkSearch;
    }

    public String getPlaceOfLoading() {
        return placeOfLoading;
    }

    public void setPlaceOfLoading(String placeOfLoading) {
        this.placeOfLoading = placeOfLoading;
    }
    public String getCar() { return car; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCar(String car) { this.car = car; }

    public String getPlaceOfDelivery() {
        return placeOfDelivery;
    }

    public void setPlaceOfDelivery(String placeOfDelivery) {
        this.placeOfDelivery = placeOfDelivery;
    }

    public String getShipmentStart() {
        return shipmentStart;
    }

    public void setShipmentStart(String shipmentStart) {
        this.shipmentStart = shipmentStart;
    }

    public String getShipmentEnd() {
        return shipmentEnd;
    }

    public void setShipmentEnd(String shipmentEnd) {
        this.shipmentEnd = shipmentEnd;
    }

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public String getUnloading() {
        return unloading;
    }

    public void setUnloading(String unloading) {
        this.unloading = unloading;
    }

    public void sendMail(String mailHost, String transportProtocol, String userMail, String passwordMail){ }

    public String getNDS() { return NDS; }

    public void setNDS(String NDS) { this.NDS = NDS; }
    public void closeDriver(){
        driver.close();
        driver.quit();
    }
    private boolean checkElement(WebElement webElement, String str){
        try {
            webElement.findElement(By.tagName(str));
            return true;
        }catch (NoSuchElementException e){
            return false;
        }
    }
    private String getButton (String aria, String text){
        return "var link = document.querySelector(\"div[" + aria + "]\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/" + text + "/i).test( e.textContent ) ); " + "link[0].click();";
    }

    public boolean checkString(String source, String dist){
        if (dist == null) dist = "";
        if (source == null) source = "";
        List<String> arrSource = new ArrayList<>(Arrays.asList(source.split("\\s*,\\s*")));
        List<String> arrDist = new ArrayList<>(Arrays.asList(dist.split("\\s*,\\s*")));
        int countDist;
        countDist = arrDist.size();
        for (String lineDist : arrDist) {
            if (arrSource.contains(lineDist)) {
                countDist--;
            }
        }
        if (countDist > 0) {
            return true;
        }
        return false;
    }

    private boolean editWebElement(WebDriver driver, List<WebElement> tdList, WebElement tr, TehDAO tehDAO, List<String> list, List<String> listSend, Integer id){

        if (list.size() >= 20) {
            list.remove(0);
        }


        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].click()", tr);
        jse.executeScript("arguments[0].click()", tr);

        WebDriverWait webDriverWaitTable = new WebDriverWait(driver, 10);

        WebElement uiDialog = driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-dogovor']"));
        webDriverWaitTable.until(ExpectedConditions.visibilityOf(uiDialog));

        String dov = id < 10 ? "0" + id : id.toString();

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        WebElement aweb = uiDialog.findElement(By.id("aweb"));
        aweb.sendKeys(getCar());

        WebElement uiAutocomplete = driver.findElement(By.className("ui-autocomplete"));
        try {
            webDriverWaitTable.until(ExpectedConditions.visibilityOf(uiAutocomplete));
        }catch (org.openqa.selenium.TimeoutException ex){
            WebElement uiClose = uiDialog.findElement(By.className("ui-dialog-titlebar-close"));
            jse.executeScript("arguments[0].click()", uiClose);
            driver.navigate().refresh();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            try {
                jse.executeScript("$(\"#chkcnt\").empty();");
            }catch (JavascriptException javascriptException){
                logger.info((new Date()) + " #chkcnt not found");
            }
            tehDAO.insertQuery(getId(), "Не удалось найти на сайте данную машину. Проверьте правильность заполнения поля \"Машина\". Отредактируйте данную заявку. (номер машины может быть указан верно, просто на сайте transport.tn.ru не всегда срабатывает авто подстановка машины.)");
            tehDAO.updateErrorQuery(getId());
            return false;
        }
        WebElement li = uiAutocomplete.findElement(By.className("ui-menu-item"));
        webDriverWaitTable.until(ExpectedConditions.visibilityOf(li));
        li.click();

        try {
            WebElement clickFraht = webDriverWaitTable.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only']//span[text()='Фрахт']")));
            jse.executeScript("arguments[0].click()", clickFraht);
        }catch (TimeoutException | NoSuchElementException e){
            WebElement uiClose = uiDialog.findElement(By.className("ui-dialog-titlebar-close"));
            jse.executeScript("arguments[0].click()", uiClose);
            driver.navigate().refresh();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
            }
            try {
                jse.executeScript("$(\"#chkcnt\").empty();");
            }catch (JavascriptException ex){
                logger.info((new Date()) + " #chkcnt not found");
            }
            return false;
        }
        if(getNDS().equals("0")){
            setNDS(Integer.toString(Integer.parseInt(tdList.get(13).getText()) / 100 * 75));
            logger.info((new Date()) + getNDS());
        }
        WebElement uiDialogNDS = driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-c_price']"));
        webDriverWaitTable.until(ExpectedConditions.visibilityOf(uiDialogNDS));
        WebElement c_amount = driver.findElement(By.id("c_amount"));
        jse.executeScript("arguments[0].value = ''",c_amount);
        c_amount.sendKeys(getNDS());
        try {
            jse.executeScript("SetCPrice(null)");
            jse.executeScript("arguments[0].querySelector('.ui-button-text').click()",uiDialogNDS);
        }catch (JavascriptException javascriptException){
            logger.info("Сбой в работе сайта. Не правильно выставил сумму");
            driver.navigate().refresh();
            return false;
        }


        try{
            driver.switchTo().alert();
            String msg = driver.switchTo().alert().getText();
            tehDAO.insertQuery(getId(), msg);
            tehDAO.updateErrorQuery(getId());
            driver.switchTo().alert().accept();
            driver.navigate().refresh();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            try {
                jse.executeScript("$(\"#chkcnt\").empty();");
            }catch (JavascriptException ex){
                logger.info((new Date()) + " #chkcnt not found");
            }
            logger.info("----- alert ui-autocomplete false --- ");
            return false;
        }catch (NoAlertPresentException | UnhandledAlertException ex){

        }

        jse.executeScript(getButton("aria-labelledby='ui-dialog-title-dogovor'","Подтвердить"));

        try{
            driver.switchTo().alert().accept();
        }catch (NoAlertPresentException ex){

        }
        WebElement dlgdoveren = webDriverWaitTable.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgdoveren']")));

        WebElement dovnumfield = dlgdoveren.findElement(By.id("dovnumfield"));
        dovnumfield.sendKeys(dov);
        WebElement dovdatefield = dlgdoveren.findElement(By.id("dovdatefield"));
        jse.executeScript("arguments[0].value = '" + date + "'",dovdatefield);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        jse.executeScript("var link = document.querySelector(\"div[aria-labelledby='ui-dialog-title-dlgdoveren']\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/ОК/i).test( e.textContent ) ); " + "link[0].click();");

        WebElement dlgterminal= webDriverWaitTable.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgterminal']")));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        if(isCheckOK()) {
            logger.info("Применить ОК");
//            jse.executeScript("var link = document.querySelector(\"div[aria-labelledby='ui-dialog-title-dlgterminal']\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/ОК/i).test( e.textContent ) ); " + "link[0].click();");
        } else
            jse.executeScript("var link = document.querySelector(\"div[aria-labelledby='ui-dialog-title-dlgterminal']\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/Отмена/i).test( e.textContent ) ); " + "link[0].click();");
        list.add(tdList.get(2).getText());
        WebElement uiClose = uiDialog.findElement(By.className("ui-dialog-titlebar-close"));
        jse.executeScript("arguments[0].click()", uiClose);
        tehDAO.updateSuccessQuery(getId(), list.get(list.size()-1));
        listSend.add("Создана заявка №" + list.get(list.size()-1) + " для машины " + getCar() + ", место погрузки - " + getPlaceOfLoading() + ", место доставки - " + getPlaceOfDelivery() + ", cумма - " + getNDS() + " (Поле №" + getId() + ").");
        return true;

    }

    public void getHtml(String url, String user, String password, String mailHost,
                        String mailFromEmail, String mailToEmail, String mailUser,
                        String mailPassword, String mailPort, String mailSmtpAuth) throws ParseException, InterruptedException, WebDriverException, UnreachableBrowserException {


        TehDAO tehDAO = new TehDAO();

        try {
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver\\chromedriver.exe");
            System.setProperty("webdriver.chrome.whitelistedIps", "127.0.0.1");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--start-maximized");
            chromeOptions.addArguments("--headless");
            driver = new ChromeDriver(chromeOptions);

//        try {
//            WebDriver driver = null;
//
//            try {
//                driver = new RemoteWebDriver(
//                        new URL("http://127.0.0.1:9515"),
//                        chromeOptions);
//                tehDAO.setErrorDriver("");
//            } catch (MalformedURLException | UnreachableBrowserException e) {
//                logger.info((new Date()) + " ChromeDriver не доступен. Проверьте запущен ли драйвер или доступен ли порт 9515 (127.0.0.1:9515)");
//                throw new UnreachableBrowserException((new Date()) + " ChromeDriver не доступен. Проверьте запущен ли драйвер или доступен ли порт 9515 (127.0.0.1:9515)");
//            }

            driver.get(url);
            driver.manage().timeouts().pageLoadTimeout(20,TimeUnit.SECONDS);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try {
                WebElement userSend = driver.findElement(By.id("ctl00_Main_tbUserName"));
                userSend.sendKeys(user);
                WebElement passwordSend = driver.findElement(By.id("ctl00_Main_tbPassword"));
                passwordSend.sendKeys(password);

                WebElement clickLogin = driver.findElement(By.id("ctl00_Main_btnLogin"));
                js.executeScript("arguments[0].click()", clickLogin);
                Thread.sleep(1000);

                logger.info((new Date()).toString());
                if(driver.getCurrentUrl().contains("login")) {
                     try {
                         WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
                         WebElement jGrowlMessage = driver.findElement(By.xpath("//div[@class='jGrowl-message']"));
                         webDriverWait.until(ExpectedConditions.visibilityOf(jGrowlMessage));
                         if (jGrowlMessage.isDisplayed()) {
                             tehDAO.insertQuery(599999995, jGrowlMessage.getText() + " Исправьте значения в файле setting.properties и перезагрузите приложение(службу)");
                             driver.close();
                             driver.quit();
                             throw new InterruptedException("NoLogin");
                         }
                     } catch (NoSuchElementException ex) {

                        ex.printStackTrace();
                     }
                }
            }catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e){
                logger.info("Неудалось загрузить страницу входа в систему");
                throw new UnreachableBrowserException((new Date() + " Неудалось загрузить страницу входа в систему"));
            }

            List<String> listSend = new ArrayList<>();
            List<String> list = new ArrayList<>();

            //------ Остановка таймера обновления таблицы ------
            try {
                js.executeScript("$(\"#chkcnt\").empty();");
            }catch (JavascriptException ex){
                logger.info((new Date()) + " #chkcnt not found");
            }
            while (isCheckSearch()) {
                try {
                    js.executeScript("$(\"#chkcnt\").empty();");
                }catch (JavascriptException ex){
                    logger.info((new Date()) + " #chkcnt not found");
                }
                tehDAO.deleteQuery();
                ResultSet resultSet = tehDAO.getListQuery(); //Получения данных с БД
                //------ Получение таблицы и работа над ней
                try {
                    int a[] = new int[7];
                    WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
                    WebElement treeTable = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeTable")));
                    WebElement tbody = treeTable.findElement(By.tagName("tbody"));
                    List<WebElement> trList = webDriverWait.until(ExpectedConditions.visibilityOfAllElements(tbody.findElements(By.tagName("tr"))));
                    WebElement thead = treeTable.findElement(By.tagName("thead"));
                    WebElement trHead = thead.findElement(By.tagName("tr"));
                    List<WebElement> thHead = trHead.findElements(By.tagName("th"));
                    for (int i = 0; i < thHead.size(); i++) {
                        if (thHead.get(i).getText().equalsIgnoreCase("Место погрузки")) a[0] = i;
                        if (thHead.get(i).getText().equalsIgnoreCase("Место доставки")) a[1] = i;
                        if (thHead.get(i).getText().equalsIgnoreCase("Отгрузка")) a[2] = i;
                        if (thHead.get(i).getText().equalsIgnoreCase("Погрузка")) a[3] = i;
                        if (thHead.get(i).getText().equalsIgnoreCase("Разгрузка")) a[4] = i;
                        if (thHead.get(i).getText().equalsIgnoreCase("Номер")) a[5] = i;
                        if (thHead.get(i).getText().equalsIgnoreCase("Моя")) a[6] = i;
                    }

                    //------ Перебор строк с данными из БД
                    while (resultSet.next()) {
                        setId(resultSet.getInt("id"));
                        setCar(resultSet.getString("car"));
                        setPlaceOfLoading(resultSet.getString("placeOfLoading"));
                        setPlaceOfDelivery(resultSet.getString("placeOfDelivery"));
                        setShipmentStart(resultSet.getString("shipmentStart"));
                        setShipmentEnd(resultSet.getString("shipmentEnd"));
                        setLoading(resultSet.getString("loading"));
                        setNDS(Integer.toString(resultSet.getInt("SummInNDS")));

                        DateFormat formatd = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                        DateFormat formaShipment = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        Date dateStart = (!getShipmentStart().equals("")) ? (Date) formaShipment.parse(getShipmentStart()) : (Date) formaShipment.parse("2021-12-01 00:00");
                        Date dateEnd = (!getShipmentEnd().equals("")) ? (Date) formaShipment.parse(getShipmentEnd()) : (Date) formaShipment.parse("2030-12-31 23:59");

                        for (WebElement tr : trList) {
                            try {
                                List<WebElement> tdList = tr.findElements(By.tagName("td"));
                                if (tdList.size() < a.length)break;
                                Date tdDate = formatd.parse(tdList.get(a[2]).getText());
                                if (
                                        !checkElement(tdList.get(0), "img") && !list.contains(tdList.get(a[5]).getText())
                                                && tdList.get(a[5]).getText().indexOf("К") != 0 && tdList.get(a[5]).getText().indexOf("Ф") != 0
                                                && (tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && (getPlaceOfDelivery().equals("") || tdList.get(a[1]).getText().equalsIgnoreCase(getPlaceOfDelivery()))
                                                && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime())
                                                && (getLoading().equals("") || checkString(getLoading(), tdList.get(a[3]).getText()) && checkString(getLoading(), tdList.get(a[4]).getText())))
                                ) {
                                    if (!editWebElement(driver, tdList, tr, tehDAO, list, listSend, getId())) {
                                        logger.info((new Date()) + "Ошибка в editWebElement");
                                        break;
                                    }
                                }
                            } catch (StaleElementReferenceException | NoSuchElementException | IndexOutOfBoundsException ex) {
                                break;
                            }
                        }
                    }
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                }
                String lineEnd = "";
                try {
                    if (!listSend.isEmpty()) {
                        logger.info((new Date()) + "---- Email-send ---");
                        Properties props = System.getProperties();
                        props.put("mail.smtps.host", mailHost.trim());
                        props.put("mail.smtps.port", mailPort.trim());
                        props.put("mail.smtps.auth", mailSmtpAuth.trim());
                        Session session = Session.getInstance(props, null);
                        Message msg = new MimeMessage(session);
                        msg.setFrom(new InternetAddress(mailFromEmail.trim()));
                        msg.setSubject("Созданы новые заявки");
                        for (String line : listSend) {
                            lineEnd = lineEnd + line + " \n";
                        }
                        msg.setText(lineEnd);
                        msg.setHeader("Content-Transfer-Encoding", "base64");
                        msg.setSentDate(new Date());
                        SMTPTransport t =
                                (SMTPTransport) session.getTransport("smtps");
                        try {
                            t.connect(mailHost.trim(), mailUser.trim(), mailPassword.trim());
                        } catch (AuthenticationFailedException authenticationFailedException) {
                            tehDAO.insertQuery(599999995, "Не удалось отправить почту. Ошибка авторизации. Проверьте правильность введенных данных: email отправителя, логин и пароль.");
                        }
                        String[] mailList = mailToEmail.trim().split(",");
                        for (String email : mailList) {
                            try {
                                msg.setRecipients(Message.RecipientType.CC,
                                        InternetAddress.parse(email.trim(), false));
                                t.sendMessage(msg, msg.getAllRecipients());
                                logger.info("Response " + email + ": " + t.getLastServerResponse());
                            } catch (SendFailedException sendFailedException) {
                                tehDAO.insertQuery(599999995, "Не удалось отправить почту. Не верно указан Email " + email + "получателя.");
                                logger.info("Не удалось отправить почту. Не верно указан Email " + email + "получателя.");
                            } catch (IllegalStateException illegalStateException) {
                                tehDAO.insertQuery(599999995, "Не удалось отправить почту. Not connected");
                            }
                        }
                        t.close();
                        listSend.clear();
                    }
                } catch (MessagingException e) {
                    tehDAO.insertQuery(599999995, "Не удалось отправить почту.");
                    e.printStackTrace();
                }

                js.executeScript("Ajax();");
                Thread.sleep(5000);

            }
            driver.close();
            driver.quit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }catch (SessionNotCreatedException sessionNotCreatedException){
            logger.info((new Date()) + " не удалось создать сессию для ChromeDriver");
            sessionNotCreatedException.printStackTrace();
        }
    }
}
