package com.jaoafa.jaotan_chatlogger;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class OnCommand {
	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		if(event.getChannel().getLongID() != 528025838663499809L){
			return;
		}

		//System.out.println("[MESSAGE] " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ": " + event.getMessage().getContent());

		if(!event.getMessage().getContent().equalsIgnoreCase("/duplicate")){
			return;
		}

		if(event.getAuthor().getLongID() != 221991565567066112L){
			return;
		}

		Thread thread = new DuplicateProcessingTask(event.getClient());
		thread.start();
	}
}
