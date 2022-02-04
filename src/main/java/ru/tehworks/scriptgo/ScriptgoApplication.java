package ru.tehworks.scriptgo;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.tehworks.scriptgo.DAO.TehDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;


@SpringBootApplication
public class ScriptgoApplication {
	public static final String ICON_STR = "file:///" + System.getProperty("user.dir").replace("\\","/") +  "/depo.png";

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setTrayIcon();
				System.out.println(ICON_STR);
			}
		});
		context = SpringApplication.run(ScriptgoApplication.class, args);
	}

	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			context.getBean(TehDAO.class).setSearchCheck("1009");
			context.close();
			context = SpringApplication.run(ScriptgoApplication.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}

	private static void setTrayIcon() {
		if(! SystemTray.isSupported() ) {
			return;
		}

		PopupMenu trayMenu = new PopupMenu();
		MenuItem item = new MenuItem("Exit");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				context.getBean(TehDAO.class).setSearchCheck("1009");
				System.exit(0);
			}
		});
		MenuItem item1 = new MenuItem("Restart");
		item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restart();
			}
		});
		MenuItem item2 = new MenuItem("Settings");
		item2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec(new String[] { "c:\\windows\\notepad.exe", "setting.properties" });
				} catch (Exception exception) {
				}
			}
		});
		trayMenu.add(item2);
		trayMenu.add(item1);
		trayMenu.add(item);


		URL imageURL = null;
		try {
			imageURL = new URL(ICON_STR);
		} catch (MalformedURLException e) {
		}
		Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
		TrayIcon trayIcon = new TrayIcon(icon, "ТН поиск", trayMenu);
		trayIcon.setImageAutoSize(true);

		SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}

		trayIcon.displayMessage("ТН поиск", "Приложение запущено!",
				TrayIcon.MessageType.INFO);
	}
}
