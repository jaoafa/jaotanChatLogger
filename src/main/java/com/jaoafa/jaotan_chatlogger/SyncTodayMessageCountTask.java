package com.jaoafa.jaotan_chatlogger;

import java.util.TimerTask;

public class SyncTodayMessageCountTask extends TimerTask {
	@Override
	public void run() {
		/*
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
		*/
	}
}
