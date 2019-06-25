package com.jaoafa.jaotan_chatlogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.jaoafa.jaotan_chatlogger.Lib.MySQL;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.handle.obj.StatusType;

public class ChatReceiveEvent {
	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		if(event.getGuild().getLongID() != 189377932429492224L){
			return;
		}

		System.out.println("Msg: " + event.getAuthor().getName() + " " + event.getMessage().getContent());

		// 複数動作時のチェックのため、軽く遅延させる(0～2s)
		Random random = new Random();
		try {
			int sleep = random.nextInt(1000);
			System.out.println("SLEEP: " + sleep + "msec.");
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String id = event.getMessage().getStringID();
		String text = event.getMessage().getFormattedContent();
		String rawtext = event.getMessage().getContent();
		String guild_id = event.getGuild().getStringID();
		String channel_name = event.getChannel().getName();
		String channel_id = event.getChannel().getStringID();
		String author_name = event.getMessage().getAuthor().getName();
		String author_nickname = event.getMessage().getAuthor().getNicknameForGuild(event.getGuild());
		if(author_nickname == null) author_nickname = "";
		String author_id = event.getMessage().getAuthor().getStringID();
		String author_discriminator = event.getMessage().getAuthor().getDiscriminator();
		boolean author_bot = event.getMessage().getAuthor().isBot();
		String msgtype = event.getMessage().getType().name();
		String type = "new"; // new: 新規投稿 / edit: 編集 / delete: 削除
		LinkedList<String> attachmentsList = new LinkedList<>();
		for(Attachment attachment : event.getMessage().getAttachments()){
			attachmentsList.add(attachment.getUrl());
		}
		String attachments = implode(attachmentsList, "\n");
		String timestamp = event.getMessage().getTimestamp().toString();

		ChatLoggerNotice.run(event.getMessage());
		Main.todaymsgcount++;

		event.getClient().changePresence(StatusType.ONLINE, ActivityType.WATCHING, "Today message count: " + Main.todaymsgcount + "");

		try {
			PreparedStatement statement_exists = MySQL.getNewPreparedStatement("SELECT COUNT(rowid) FROM discordchat WHERE id = ? AND type = ? AND timestamp = ?");
			statement_exists.setString(1, id);
			statement_exists.setString(2, type);
			statement_exists.setString(3, timestamp);
			ResultSet res = statement_exists.executeQuery();
			if(res.next()){
				int i = res.getInt(1);
				if(i >= 1){
					System.out.println("exists");
					return;
				}
			}
			System.out.println("not exists");

			PreparedStatement statement = MySQL.getNewPreparedStatement("INSERT INTO discordchat "
					+ "(id, text, rawtext, guild_id, channel_name, channel_id, author_name, author_nickname, author_id, author_discriminator, author_bot, msgtype, type, attachments, machine, timestamp) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			statement.setString(1, id);
			statement.setString(2, text);
			statement.setString(3, rawtext);
			statement.setString(4, guild_id);
			statement.setString(5, channel_name);
			statement.setString(6, channel_id);
			statement.setString(7, author_name);
			statement.setString(8, author_nickname);
			statement.setString(9, author_id);
			statement.setString(10, author_discriminator);
			statement.setBoolean(11, author_bot);
			statement.setString(12, msgtype);
			statement.setString(13, type);
			statement.setString(14, attachments);
			statement.setString(15, Main.getHostName());
			statement.setString(16, timestamp);
			statement.executeUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	public static <T> String implode(List<T> list, String glue) {
		if(list.size() == 0){
			return "";
		}
	    StringBuilder sb = new StringBuilder();
	    for (T e : list) {
	        sb.append(glue).append(e);
	    }
	    return sb.substring(glue.length());
	}
}
