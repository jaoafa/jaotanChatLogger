package com.jaoafa.jaotan_chatlogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.jaoafa.jaotan_chatlogger.Lib.MySQL;

import sx.blah.discord.handle.obj.IMessage;

public class ChatLoggerNotice {
	static String oldDate = null;
	public static void run(IMessage message){
		String id = message.getStringID();
		String timestamp = message.getTimestamp().toString();
		ZoneId zoneId = ZoneId.of("Asia/Tokyo");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String date_str = message.getTimestamp().atZone(zoneId).format(dtf);

		if(oldDate == null){
			oldDate = date_str;
		}
		if(oldDate.equals(date_str)){
			// 一緒だったら無視
			return;
		}

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			PreparedStatement statement_exists = MySQL.getNewPreparedStatement("SELECT COUNT(rowid) FROM discordchat WHERE type = ? AND date LIKE ?");
			statement_exists.setString(1, "new");
			statement_exists.setString(2, sdf.format(new Date()) + "%");
			ResultSet res = statement_exists.executeQuery();
			if(res.next()){
				int i = res.getInt(1);
				System.out.println("[SyncTodayMessageCount] Synced : " + Main.todaymsgcount + " -> " + i);
				Main.todaymsgcount = i;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		/*
		2019/04/02無効化。PHPで処理させるように？

		// 既に投稿されていないか
		try {
			PreparedStatement statement_exists = MySQL.getNewPreparedStatement("SELECT COUNT(rowid) FROM discordchat WHERE id = ? AND timestamp = ?");
			statement_exists.setString(1, id);
			statement_exists.setString(2, timestamp);
			ResultSet res = statement_exists.executeQuery();
			if(res.next()){
				int i = res.getInt(1);
				if(i >= 1){
					System.out.println("notice exists");
					return;
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		message.getClient().getChannelByID(528025838663499809L).sendMessage("**[" + sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "Yesterday message count: " + Main.todaymsgcount);
		oldDate = date_str;

		Thread thread = new DuplicateProcessingTask(message.getClient());
		thread.start();
		*/
	}
}
