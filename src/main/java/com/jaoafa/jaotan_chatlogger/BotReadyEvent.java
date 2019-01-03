package com.jaoafa.jaotan_chatlogger;

import java.util.Date;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class BotReadyEvent {
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("Ready: " + event.getClient().getOurUser().getName());
		event.getClient().getChannelByID(528025838663499809L).sendMessage("**[" + Main.sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "Start jaotanChatLogger");
		Runtime.getRuntime().addShutdownHook(new Thread(
            () -> event.getClient().getChannelByID(528025838663499809L).sendMessage("**[" + Main.sdf.format(new Date()) + " | " + Main.getHostName() + "]** " + "End jaotanChatLogger")
        ));
	}
}
