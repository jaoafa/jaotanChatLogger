package com.jaoafa.jaotan_chatlogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jaoafa.jaotan_chatlogger.Lib.MySQL;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public class DuplicateProcessingTask extends Thread {
	IDiscordClient client;
	public DuplicateProcessingTask(IDiscordClient client){
		this.client = client;
	}
	public void run() {
		// 重複整理
		IChannel channel = client.getChannelByID(528025838663499809L);

		if(!Main.getHostName().equalsIgnoreCase("jaoMain")){
			return;
		}

		channel.sendMessage("**[" + Main.sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "チャットデータの重複削除処理を開始します。");
		int success = 0;
		int alreadysuccess = 0;
		int error = 0;
		int processed = 0;
		try {
			PreparedStatement statement_distinct = MySQL.getNewPreparedStatement("SELECT DISTINCT id,type FROM discordchat WHERE date LIKE ? OR date LIKE ?");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -2);
			statement_distinct.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + "%"); // 一昨日
			Calendar cal_yesterday = Calendar.getInstance();
			cal_yesterday.add(Calendar.DATE, -1);
			statement_distinct.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(cal_yesterday.getTime()) + "%"); // 昨日
			statement_distinct.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%");
			ResultSet res_distinct = statement_distinct.executeQuery();
			while(res_distinct.next()){
				String id = res_distinct.getString("id");
				String type = res_distinct.getString("type");

				System.out.println("[DuplicateProcessingTask|START|" + processed + "] " + id + " " + type + " (Success: " + success + "|Error: " + error + ")");

				PreparedStatement statement_chatCheck = MySQL.getNewPreparedStatement("SELECT * FROM discordchat WHERE id = ? AND type = ?");
				statement_chatCheck.setString(1, id);
				statement_chatCheck.setString(2, type);
				ResultSet res_chatCheck = statement_chatCheck.executeQuery();
				res_chatCheck.next(); // 1つめは飛ばす
				try {
					while(res_chatCheck.next()){
						int rowid = res_chatCheck.getInt("rowid");
						boolean status = res_chatCheck.getBoolean("status");

						PreparedStatement statement_disable = MySQL.getNewPreparedStatement("UPDATE discordchat SET status = ? WHERE rowid = ?");
						statement_disable.setBoolean(1, false);
						statement_disable.setInt(2, rowid);
						statement_disable.executeUpdate();

						if(status){
							success++;
						}else{
							alreadysuccess++;
						}
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					error++;
				}
				processed++;

				System.out.println("[DuplicateProcessingTask|END|" + processed + "] " + id + " " + type + " (Success: " + success + "|Error: " + error + ")");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		channel.sendMessage("**[" + Main.sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "チャットデータの重複削除処理を完了しました。\n処理数: " + processed + "\n処理成功数: " + success + "\n既処理数: " + alreadysuccess + "\n処理失敗数: " + error);
	}
}
