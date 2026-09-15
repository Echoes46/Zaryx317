package io.zaryx.model.entity.player;

import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.packets.npcoptions.*;
import io.zaryx.model.entity.player.packets.objectoptions.ObjectOptionFour;
import io.zaryx.model.entity.player.packets.objectoptions.ObjectOptionOne;
import io.zaryx.model.entity.player.packets.objectoptions.ObjectOptionThree;
import io.zaryx.model.entity.player.packets.objectoptions.ObjectOptionTwo;

public class ActionHandler {

	private final Player c;

	public ActionHandler(Player Client) {
		this.c = Client;
	}

	public void firstClickObject(int objectType, int obX, int obY) {
        if (io.zaryx.content.holiday.HolidayEvents.clickObject(c, objectType, obX, obY)) return;
		ObjectOptionOne.handleOption(c, objectType, obX, obY);
	}

	public void secondClickObject(int objectType, int obX, int obY) {
        if (io.zaryx.content.holiday.HolidayEvents.clickObject(c, objectType, obX, obY)) return;
		ObjectOptionTwo.handleOption(c, objectType, obX, obY);
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
        if (io.zaryx.content.holiday.HolidayEvents.clickObject(c, objectType, obX, obY)) return;
		ObjectOptionThree.handleOption(c, objectType, obX, obY);
	}

	public void fourthClickObject(int objectType, int obX, int obY) {
        if (io.zaryx.content.holiday.HolidayEvents.clickObject(c, objectType, obX, obY)) return;
		ObjectOptionFour.handleOption(c, objectType, obX, obY);
	}

	public void firstClickNpc(NPC npc) {
        if (io.zaryx.content.holiday.HolidayEvents.clickNpc(c, npc)) return;
		NpcOptionOne.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 1);
	}

	public void secondClickNpc(NPC npc) {
        if (io.zaryx.content.holiday.HolidayEvents.clickNpc(c, npc)) return;
		NpcOptionTwo.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 2);
	}

	public void thirdClickNpc(NPC npc) {
        if (io.zaryx.content.holiday.HolidayEvents.clickNpc(c, npc)) return;
		NpcOptionThree.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 3);
	}

	public void fourthClickNpc(NPC npc) {
        if (io.zaryx.content.holiday.HolidayEvents.clickNpc(c, npc)) return;
		NpcOptionFour.handleOption(c, npc.getNpcId());
		NpcOptions.handle(c, npc, 4);
	}

}