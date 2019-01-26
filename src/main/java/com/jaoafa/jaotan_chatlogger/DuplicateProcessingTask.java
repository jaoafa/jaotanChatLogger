package com.jaoafa.jaotan_chatlogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

		channel.sendMessage("**[" + Main.sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "チャットデータの重複削除処理を開始します。");
		int success = 0;
		int error = 0;
		int processed = 0;
		try {
			PreparedStatement statement_distinct = MySQL.getNewPreparedStatement("SELECT DISTINCT id,type FROM discordchat");
			ResultSet res_distinct = statement_distinct.executeQuery();
			while(res_distinct.next()){
				String id = res_distinct.getString("id");
				String type = res_distinct.getString("type");

				PreparedStatement statement_chatCheck = MySQL.getNewPreparedStatement("SELECT * FROM discordchat WHERE id = ? AND type = ?");
				statement_chatCheck.setString(1, id);
				statement_chatCheck.setString(2, type);
				ResultSet res_chatCheck = statement_chatCheck.executeQuery();
				res_chatCheck.next(); // 1つめは飛ばす
				try {
					while(res_chatCheck.next()){
						int rowid = res_chatCheck.getInt("rowid");

						PreparedStatement statement_disable = MySQL.getNewPreparedStatement("UPDATE discordchat SET status = ? WHERE rowid = ?");
						statement_disable.setBoolean(1, false);
						statement_disable.setInt(2, rowid);
						statement_disable.executeUpdate();

						success++;
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					error++;
				}
				processed++;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		channel.sendMessage("**[" + Main.sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "チャットデータの重複削除処理を完了しました。\n処理数: " + processed + "\n処理成功数: " + success + "\n処理失敗数: " + error);
	}
}
