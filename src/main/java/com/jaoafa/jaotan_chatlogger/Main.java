package com.jaoafa.jaotan_chatlogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Timer;

import com.jaoafa.jaotan_chatlogger.Lib.MySQL;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

public class Main {
	public static String sqlserver;
	public static String sqluser;
	public static String sqlpassword;
	public static Connection c = null;
	public static long ConnectionCreate = 0;

	public static int todaymsgcount = 0;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static void main(String[] args) {
		File f = new File("conf.properties");
		Properties props;
		try{
			InputStream is = new FileInputStream(f);

			// プロパティファイルを読み込む
			props = new Properties();
			props.load(is);
		}catch(FileNotFoundException e){
			// ファイル生成
			props = new Properties();
			props.setProperty("token", "PLEASETOKEN");
			props.setProperty("sqlserver", "PLEASE");
			props.setProperty("sqluser", "PLEASE");
			props.setProperty("sqlpassword", "PLEASE");
			props.setProperty("sqlserver", "PLEASE");
			try {
				props.store(new FileOutputStream("conf.properties"), "Comments");
				System.out.println("Please Config Token!");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		// キーを指定して値を取得する
		String token = props.getProperty("token");
		if(token.equalsIgnoreCase("PLEASETOKEN")){
			System.out.println("Please Token!");
			return;
		}

		sqlserver = props.getProperty("sqlserver");
		sqluser = props.getProperty("sqluser");
		sqlpassword = props.getProperty("sqlpassword");
		if(sqlserver.equalsIgnoreCase("PLEASE") || sqluser.equalsIgnoreCase("PLEASE") || sqlpassword.equalsIgnoreCase("PLEASE")){
			System.out.println("SQL Connect config is missed.");
			return;
		}

		MySQL MySQL = new MySQL(sqlserver, "3306", "jaoafa", sqluser, sqlpassword);

		try {
			c = MySQL.openConnection();
			ConnectionCreate = System.currentTimeMillis() / 1000L;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("MySQL Connect err. [ClassNotFoundException]");
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("MySQL Connect err. [SQLException: " + e.getSQLState() + "]");
			return;
		}
		System.out.println("MySQL Connected");

		IDiscordClient client = createClient(token, true);
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new BotReadyEvent());
		dispatcher.registerListener(new ChatReceiveEvent());
		dispatcher.registerListener(new ChatEditEvent());
		dispatcher.registerListener(new ChatDeleteEvent());

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new SyncTodayMessageCountTask(), 0, 600000); // 10分毎
	}
	public static IDiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord client
		ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
		clientBuilder.withToken(token); // Adds the login info to the builder
		try {
			if (login) {
				return clientBuilder.login(); // Creates the client instance and logs the client in
			} else {
				return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
			}
		} catch (DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ホスト名を返す
	 * @return ホスト名。取得できなければnullを返却
	 */
	public static String getHostName() {
	    try {
	        return InetAddress.getLocalHost().getHostName();
	    }catch (Exception e) {
	        e.printStackTrace();
		    return null;
	    }
	}
}
