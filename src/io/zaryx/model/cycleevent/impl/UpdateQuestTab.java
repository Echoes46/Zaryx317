package io.zaryx.model.cycleevent.impl;

import io.zaryx.model.cycleevent.Event;
import io.zaryx.util.Misc;

import java.util.concurrent.TimeUnit;

public class UpdateQuestTab extends Event<Object> {


	private static final int INTERVAL = Misc.toCycles(5, TimeUnit.SECONDS);

	
	public UpdateQuestTab() {
		super("", new Object(), INTERVAL);
	}	

	@Override
	public void execute() {
/*		PlayerHandler.nonNullStream().forEach(player -> {
			player.getQuestTab().updateInformationTab();
		});*/
	}
} 