package io.zaryx.content.combat.formula.rework;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.bonus.BoostScrolls;
import io.zaryx.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.AhrimEffect;
import io.zaryx.content.combat.formula.MagicMaxHit;
import io.zaryx.content.combat.magic.CombatSpellData;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.content.commands.owner.SetAccuracyBonus;
import io.zaryx.content.commands.owner.SetDefenceBonus;
import io.zaryx.content.items.PvpWeapons;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.content.skills.Skill;
import io.zaryx.content.skills.slayer.NewInterface;
import io.zaryx.model.Bonus;
import io.zaryx.model.CombatType;
import io.zaryx.model.Items;
import io.zaryx.model.SpellBook;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.npc.stats.NpcCombatDefinition;
import io.zaryx.model.entity.npc.stats.NpcCombatSkill;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;

import static io.zaryx.content.bosses.nightmare.NightmareStatusNPC.npc;

/**
 * @author Arthur Behesnilian 2:42 PM
 */





public class MagicCombatFormula implements CombatFormula {

    public static MagicCombatFormula STANDARD = new MagicCombatFormula();

    private int maxHit;

    public MagicCombatFormula() {
        this.maxHit = -1;
    }

    public MagicCombatFormula(int maxHit) {
        this.maxHit = maxHit;
    }

    @Override
    public double getAccuracy(Entity attacker, Entity defender, double specialAttackMultiplier,
                              double defenceMultiplier) {

        int attack = getAttackRoll(attacker, defender);
        int defence = (int) ((defender.isPlayer() ? getDefenceRoll(attacker, defender.asPlayer()) :
                getDefenceRoll(defender.asNPC())) * defenceMultiplier);

        double accuracy = 0;

        if (attack > defence) {
            accuracy = 1.0 - (defence + 2.0) / (2.0 * (attack + 1.0));
        } else {
            accuracy = attack / (2.0 * (defence + 1));
        }

        return accuracy;
    }

    private int getAttackRoll(Entity attacker, Entity defender) {
        double effectiveMagicLevel = attacker.isPlayer() ?
                getEffectiveAttackLevel(attacker.asPlayer()) : getEffectiveAttackLevel(attacker.asNPC());

        if (attacker.isPlayer() && defender.isNPC()) {
            effectiveMagicLevel += SetAccuracyBonus.MAGE_ATTACK - 8;
        }

        if(defender.isPlayer()){
            if (Server.getEventHandler().isRunning(defender.asPlayer(), "sapmage")) {
                double howmuchtodecrease = (defender.asPlayer().sapMagic * 0.10);
                effectiveMagicLevel -= howmuchtodecrease*effectiveMagicLevel;
            }
        }
        if(defender.isPlayer()){
            if (Server.getEventHandler().isRunning(defender.asPlayer(), "leechmagic")) {
                double howmuchtodecrease = (defender.asPlayer().leechMagic_enemy * 0.10);
                effectiveMagicLevel -= howmuchtodecrease*effectiveMagicLevel;
            }
        }

        double equipmentAttackBonus = getEquipmentAttackBonus(attacker);

        double maxRoll = effectiveMagicLevel * (equipmentAttackBonus + 64);
        if (attacker.isPlayer()) {
            maxRoll = applyAttackSpecials(attacker.asPlayer(), maxRoll);
            maxRoll = Math.floor(maxRoll);

            if (defender.isNPC()) {
                maxRoll *= getNpcAttackMultiplier(attacker.asPlayer(), defender.asNPC());
                maxRoll = Math.floor(maxRoll);
            }
        }
        return (int) maxRoll;
    }

    private double getNpcAttackMultiplier(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Task boosts
        if (attacker.getSlayer().isTaskNpc(defender)) {
            // Slayer Helmet boost
            if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.MELEE)) {
                multiplier += 0.1666;
            }
            if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE) && attacker.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
                multiplier += 0.2666;
            } else if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE)) {
                multiplier += 0.1666;
            }
            //Salve Amulet
            if (getSalveAmuletMultiplier(attacker, defender) != 0) {
                multiplier += getSalveAmuletMultiplier(attacker, defender);
            }

            if (BoostScrolls.checkSlayerBoost(attacker)) {
                multiplier += 0.10;
            }
        }

        if (BoostScrolls.checkDamageBoost(attacker)) {
            multiplier += 0.10;
        }

        //Amulet of avarice
        if (attacker.getItems().isWearingItem(22557) && attacker.isSkulled && Boundary.isIn(attacker, Boundary.REV_CAVE)) {
            multiplier += 0.20;
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 1.0;
            }
        }


        if (attacker.hasFollower && attacker.petSummonId == 25348) {
            multiplier += 0.20;
        }

        return multiplier;
    }

    private double applyAttackSpecials(Player attacker, double base) {
        double hit = base;

        hit *= getEquipmentMultiplier(attacker);
        hit = Math.floor(hit);

        if (attacker.getItems().isWearingItem(Items.MYSTIC_SMOKE_STAFF)) {
            hit *= 1.1;
            hit = Math.floor(hit);
        }

        return hit;
    }

    private double getEquipmentMultiplier(Player attacker) {
        return 1.0;
    }

    private double getEquipmentAttackBonus(Entity attacker) {
        return attacker.getBonus(Bonus.ATTACK_MAGIC);
    }

    private static int[] TRIDENTS = {
            Items.TRIDENT_OF_THE_SEAS, Items.TRIDENT_OF_THE_SEAS_E,
            Items.TRIDENT_OF_THE_SEAS_FULL, Items.TRIDENT_OF_THE_SEAS_FULL_2,
            Items.TRIDENT_OF_THE_SWAMP, Items.TRIDENT_OF_THE_SWAMP_E,
            Items.TRIDENT_OF_THE_SWAMP_E_2
    };

    private double getEffectiveAttackLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.MAGIC.getId()] * getPrayerAttackMultiplier(player));

        //if (player.getItems().hasAnyItem(Player.playerWeapon, TRIDENTS)) {
        //    switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
        //        case ACCURATE:
        //            effectiveLevel += 3.0;
        //            break;
        //        case CONTROLLED:
        //            effectiveLevel += 1.0;
        //            break;
        //    }
        //}

        effectiveLevel += 8.0;

        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1248350477117165598L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getUser().getIdLong()) {
                        effectiveLevel += effectiveLevel * 0.25;
                        effectiveLevel = Math.floor(effectiveLevel);
                    }
                }
            }
        }

        if (player.fullVoidMage()) {
            effectiveLevel += effectiveLevel * 0.10;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullVoidMage()) {
            effectiveLevel += effectiveLevel * 0.075;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullEliteVoidMage()) {
            effectiveLevel += effectiveLevel * 0.15;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullEliteORVoidMage() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.20;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullCeremonial() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.25;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMalar()) {
            effectiveLevel += effectiveLevel * 0.50;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullBloodbark() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.35;
            effectiveLevel = Math.floor(effectiveLevel);
        }


        if (Server.getEventHandler().isRunning(player, "leechmagic")) {
            double howmuchtoincrease = (player.leechMagic * 0.10);
            effectiveLevel+= howmuchtoincrease*effectiveLevel;
        }

        if (player.playerEquipment[Player.playerFeet] == 10556) { //attacker icon
            effectiveLevel += effectiveLevel * 0.10D;
        }

        return Math.floor(effectiveLevel);
    }

    private double getPrayerAttackMultiplier(Player player) {
        if (CombatPrayer.isPrayerOn(player, CombatPrayer.MYSTIC_WILL))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.MYSTIC_LORE))
            return 1.1;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.MYSTIC_MIGHT))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.AUGURY))
            return 1.25;
        else
            return 1.0;
    }

    private double getEffectiveAttackLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.MAGIC) + 8;
    }

    private double getDefenceRoll(Entity attacker, Player player) {
        double effectiveDefenceLevel = getEffectiveDefenceLevel(player);

        if (attacker.isNPC()) {
            effectiveDefenceLevel += SetDefenceBonus.MAGE_DEFENCE - 8;
        }

        effectiveDefenceLevel *= 0.3;
        effectiveDefenceLevel = Math.floor(effectiveDefenceLevel);

        double magicLevel = player.playerLevel[Skill.MAGIC.getId()];
        magicLevel *= getPrayerAttackMultiplier(player);
        magicLevel = Math.floor(magicLevel);

        magicLevel *= 0.7;
        magicLevel = Math.floor(magicLevel);

        double effectiveMagicLevel = Math.floor(effectiveDefenceLevel + magicLevel + 8);
        double equipmentDefenceBonus = getEffectiveDefenceBonus(player);

        double maxRoll = effectiveMagicLevel * (equipmentDefenceBonus + 64);
        return maxRoll;
    }

    private double getEffectiveDefenceLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.DEFENCE.getId()] *
                getPrayerDefenceMultiplier(player));

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case DEFENSIVE:
                effectiveLevel += 3.0;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }

        effectiveLevel += 8.0;
        return Math.floor(effectiveLevel);
    }

    private double getPrayerDefenceMultiplier(Player player) {
        if (CombatPrayer.isPrayerOn(player, CombatPrayer.THICK_SKIN))
            return 1.05;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.ROCK_SKIN))
            return 1.1;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.STEEL_SKIN))
            return 1.15;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.CHIVALRY))
            return 1.2;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.PIETY))
            return 1.25;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.RIGOUR))
            return 1.25;
        else if (CombatPrayer.isPrayerOn(player, CombatPrayer.AUGURY))
            return 1.25;
        else
            return 1.0;
    }

    private int getDefenceRoll(NPC npc) {
        double effectiveDefenceLevel = getEffectiveDefenceLevel(npc);
        double equipmentDefenceBonus = getEffectiveDefenceBonus(npc);

        double maxRoll = effectiveDefenceLevel * (equipmentDefenceBonus + 64);

        return (int) maxRoll;
    }

    public int getEffectiveDefenceLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.MAGIC) + 9;
    }

    private double getEffectiveDefenceBonus(Entity entity) {
        return entity.getBonus(Bonus.DEFENCE_MAGIC);
    }

    @Override
    public int getMaxHit(Entity attacker, Entity defender, double specialAttackMultiplier,
                         double specialPassiveMultiplier) {
        int[] spellData = null;
        double hit = 0;
        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();
            if (player.getSpellId() > -1 && !player.usingSpecial) {
                spellData = CombatSpellData.MAGIC_SPELLS[player.getSpellId()];
            } else {
                // Nightmare staff max hit
                int magicLevel = player.playerLevel[Skill.MAGIC.getId()];
                /*if (player.getCombatItems().usingNightmareStaffSpecial())
                    hit = MagicMaxHit.getNightmareSpecialMaxHit((magicLevel/5), 44);
                else if(player.getCombatItems().usingEldritchStaffSpecial())
                    hit = MagicMaxHit.getNightmareSpecialMaxHit((magicLevel/5), 39);
                else*/ if (this.hasSanguinestiCharges(player)) {
                    hit = this.getSanguinestuMaxHit(player);
                } else if (this.hasTridentCharges(player)) {
                    hit = this.getTridentMaxHit(player);
                }
            }
        }
        if (this.maxHit != -1) {
            hit = this.maxHit;
        } else if (spellData != null && hit == 0) {
            hit = CombatSpellData.getBaseDamage(spellData);
        } else if (attacker.isNPC()) {
            NPC npc = attacker.asNPC();
            hit = npc.maxHit;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();

            // God Spell bonus
            if (CombatSpellData.godSpells(player)) {
                if (System.currentTimeMillis() - player.godSpellDelay < Configuration.GOD_SPELL_CHARGE) {
                    hit += 10;
                }
            }

            if (spellData != null) {
                if (player.getItems().isWearingItem(Player.playerHands, Items.CHAOS_GAUNTLETS)) {
                    boolean isBoltSpell = CombatSpellData.isBoltSpell(spellData[0]);
                    if (isBoltSpell)
                        hit += 3;
                }
            }

            int magicDamageBonus = attacker.getBonus(Bonus.MAGIC_DMG);

            if (!player.getCombatItems().usingNightmareStaffSpecial() && player.playerEquipment[Player.playerWeapon] == 24424) {
                if (magicDamageBonus > 100) {
                    magicDamageBonus = 50;
                }
            } else if(!player.getCombatItems().usingEldritchStaffSpecial() && player.playerEquipment[Player.playerWeapon] == 24425) {
                if (magicDamageBonus > 100) {
                    magicDamageBonus = 50;
                }
            }

            double multiplier = 1.0 + (magicDamageBonus / 100.0);

            // Tome of Fire bonus
            if (player.getCombatItems().hasTomeOfFire()) {
                boolean usingNightMareStaffSpecial = player.getCombatItems().usingNightmareStaffSpecial();
                if (CombatSpellData.fireSpells(player) && spellData != null) {
                    multiplier += 0.5;
                    player.getTomeOfFire().decrCharge();
                }
            }

            if (CombatPrayer.isPrayerOn(player, CombatPrayer.AUGURY)) {
                multiplier += 0.25;
            }

            if (player.getItems().hasAnyItem(Player.playerAmulet, Items.AMULET_OF_THE_DAMNED_FULL,
                    Items.AMULET_OF_THE_DAMNED)) {
                boolean hasExtra = AhrimEffect.INSTANCE.hasExtraRequirement(player);
                if (hasExtra && Misc.random(4) == 1) {
                    multiplier += 0.3;
                }
            }

            if (player.getItems().isWearingItem(Player.playerWeapon, Items.MYSTIC_SMOKE_STAFF)
                    && player.getSpellBook() == SpellBook.MODERN) {
                multiplier += 0.1;
            }

            if (player.getItems().isWearingItem(27275, Player.playerWeapon)) {
                multiplier += 1.000;
            }

            if (player.petSummonId == 27352) {
                multiplier += 0.15;
            }

            if (player.getItems().isWearingItem(22555, Player.playerWeapon) || player.getItems().isWearingItem(27679, Player.playerWeapon)) {
                multiplier += 1.000;
            }

            if (player.getItems().isWearingItem(33205, Player.playerWeapon)) {
                multiplier += 1.000;
            }

            if (player.getItems().isWearingItem(33149, Player.playerWeapon)) {
                multiplier -= 0.430;
            }

            if (player.getItems().isWearingItem(39000, Player.playerWeapon)) {
                multiplier += 1.100;
            }

            if (player.getItems().isWearingItem(28585, Player.playerWeapon)) {
                multiplier += 1.425;
            }

            if (player.getCombatItems().usingNightmareStaffSpecial())
                hit = MagicMaxHit.getNightmareSpecialMaxHit((25), 44);
            else if(player.getCombatItems().usingEldritchStaffSpecial())
                hit = MagicMaxHit.getNightmareSpecialMaxHit((21), 39);

            if (player.playerLevel[Skill.MAGIC.getId()] > 100) {
                int mageLevel = (player.playerLevel[Skill.MAGIC.getId()] - 100);
                multiplier += ((double) mageLevel / 10);
            }
            if (player.armourofozmage()) {
                multiplier += 1.50;
            }

            if (player.fullEliteVoidMage()) {
                multiplier += 0.075;
            } else if (player.fullEliteORVoidMage() && !player.getPosition().inWild()) {
                multiplier += 0.125;
            } else if (player.fullVoidMage() && !player.getPosition().inWild()) {
                multiplier += 0.035;
            } else if (player.fullCeremonial() && !player.getPosition().inWild()) {
                multiplier += 0.100;
            } else if (player.fullMalar()) {
                multiplier += 0.150;
            } else if (player.fullTectonic() && !player.getPosition().inWild()) {
                multiplier += 0.150;
            } else if (player.fullBloodbark() && !player.getPosition().inWild()) {
                multiplier += 0.162;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.DAMAGE_BONUS1) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)) {
                multiplier += 0.03D;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.DAMAGE_BONUS2) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)) {
                multiplier += 0.03D;
            }

            if (player.armourofozmage()) {
                if (Misc.trueRand(3) == 1) {
                    player.getHealth().increase(( 10 / 3));
                }
            }
            if (player.getRights().contains(Right.FACTION_G)) {
                if (Misc.trueRand(3) == 1) {
                    player.getHealth().increase(( 10 / 3));
                }
            }
            if (PrestigePerks.hasRelic(player, PrestigePerks.DAMAGE_BONUS3) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)) {
                multiplier += 0.03D;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.EXPERIENCE_DAMAGE_BONUS1) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)) {
                if (player.getMode().isOsrs() || player.getMode().is5x()) {
                    multiplier += 0.12D;
                } else {
                    multiplier += 0.06D;
                }
            }

            if (player.usingRage && !player.getPosition().inWild()) {
                multiplier += 0.25D;
            }

            hit *= multiplier;
            hit = Math.floor(hit);

        } else if (attacker.isNPC()) {
            double multiplier = 1.0 + (attacker.getBonus(Bonus.MAGIC_DMG) / 100.0);
            hit += multiplier;
            hit = Math.floor(hit);

        }

        if (defender.isPlayer()) {
            Player player = defender.asPlayer();
            if (CombatPrayer.isPrayerOn(player, CombatPrayer.PROTECT_FROM_MAGIC) || CombatPrayer.isPrayerOn(player, CombatPrayer.DEFLECT_MAGIC)) {
                hit *= 0.6D;
                hit = Math.floor(hit);
            }
        }

        hit *= getDamageDealMultiplier(attacker);
        hit = Math.floor(hit);

        hit *= getDamageTakenMultiplier(defender);
        hit = Math.floor(hit);

        if (attacker.isPlayer() && defender.isNPC()) {
            hit *= getNpcMultipliers(attacker.asPlayer(), defender.asNPC());
            hit = Math.floor(hit);
        }

        return (int) hit;
    }

    private double getNpcMultipliers(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Helmet boost
        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE) && attacker.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
            multiplier += 0.2666;
        } else if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE)) {
            multiplier += 0.1666;
        } else {
            // Salve Amulet boosts
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.25;
            }
        }

        // Pet boosts
        boolean hasDarkMagePet = PetHandler.hasDarkMagePet(attacker);
        boolean hasMagePet = PetHandler.hasMagePet(attacker);
        if (hasDarkMagePet)
            multiplier += 0.10;
        else if (hasMagePet && Misc.isLucky(50))
            multiplier += 0.10;

        if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33103)) {
            multiplier += 0.05D;
        } else if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33106)) {
            multiplier += 0.10D;
        } else if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33222)) {
            multiplier += 0.15D;
        } else if (PrestigePerks.hasRelic(attacker, PrestigePerks.ZERK_RANGE_MAGE) && attacker.wildLevel < 0) {
            multiplier += 0.10D;
        }
        int npcId = npc.getNpcId();
        boolean hasTricksterEquipped = attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33216);

// Now check against each NPC ID individually and whether the player has Trickster equipped
        if ((npcId == 4923 && hasTricksterEquipped) ||
                (npcId == 5126 && hasTricksterEquipped) ||
                (npcId == 5169 && hasTricksterEquipped) ||
                (npcId == 1377 && hasTricksterEquipped) ||
                (npcId == 8198 && hasTricksterEquipped) ||
                (npcId == 11246 && hasTricksterEquipped)) {

            attacker.sendMessage("@red@You must take off trickster to attack this npc");

            } else if(attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33216) && Misc.isLucky(1)) {
                multiplier += 999.00D;
            }

        return multiplier;
    }

    private double getDamageTakenMultiplier(Entity defender) {
        if (defender.isPlayer()) {
            Player player = defender.asPlayer();

            if (player.getCombatItems().elyProc())
                return 0.75;

        }
        return 1.0;
    }

    private double getDamageDealMultiplier(Entity attacker) {
        return 1.0;
    }

    public boolean hasTridentCharges(Player player) {
        if (player.getItems().hasAnyItem(Player.playerWeapon, TRIDENTS)) {
            return player.getToxicTridentCharge() > 0 || player.getTridentCharge() > 0;
        }
        return false;
    }

    private static int[] TOXIC_TRIDENTS = {
            Items.TRIDENT_OF_THE_SWAMP, Items.TRIDENT_OF_THE_SWAMP_E, Items.TRIDENT_OF_THE_SEAS_FULL
    };

    private boolean isToxic(int weaponId) {
        return Arrays.stream(TOXIC_TRIDENTS).anyMatch(id -> id == weaponId);
    }

    public int getTridentMaxHit(Player player) {
        int weaponId = player.getItems().getWeapon();
        if (weaponId == -1)
            return 0;

        boolean isToxic = isToxic(weaponId);
        int baseHit = isToxic ? 23 : 20;
        int magicLevel = Math.max(25, player.getLevelForXP(player.playerXP[Skill.MAGIC.getId()]));
        int extraHits = (int) ((double) (magicLevel - 75) / 3);
        return baseHit + extraHits;
    }

    public boolean hasSanguinestiCharges(Player player) {
        if (player.getItems().hasAnyItem(Player.playerWeapon, Items.SANGUINESTI_STAFF,
                Items.SANGUINESTI_STAFF_UNCHARGED)) {
            return player.getSangStaffCharge() > 0;
        }
        return false;
    }

    public int getSanguinestuMaxHit(Player player) {
        int baseHit = 12;
        int magicLevel = Math.max(player.playerLevel[Skill.MAGIC.getId()], 75);
        int extraHits = (int) ((double) (magicLevel - 75) / 3);
        return baseHit + extraHits;
    }

}
