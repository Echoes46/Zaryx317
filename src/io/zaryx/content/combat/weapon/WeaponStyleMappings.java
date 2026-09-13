package io.zaryx.content.combat.weapon;
import java.util.Map;
/** Explicit style-only mappings for weapon variants; does not change ammo or damage rules. */
final class WeaponStyleMappings {
    private WeaponStyleMappings() { }
    static void register(Map<Integer, WeaponData> map) {
        // AXE
        add(map, 10129, WeaponData.AXE); // Barb-tail harpoon
        add(map, 13242, WeaponData.AXE); // Infernal axe (uncharged)
        add(map, 21031, WeaponData.AXE); // Infernal harpoon
        add(map, 21033, WeaponData.AXE); // Infernal harpoon (uncharged)
        add(map, 23675, WeaponData.AXE); // Crystal axe (inactive)
        add(map, 23762, WeaponData.AXE); // Crystal harpoon
        add(map, 23764, WeaponData.AXE); // Crystal harpoon (inactive)
        add(map, 23821, WeaponData.AXE); // Corrupted axe
        add(map, 23823, WeaponData.AXE); // Corrupted harpoon
        add(map, 23862, WeaponData.AXE); // Crystal axe
        add(map, 23864, WeaponData.AXE); // Crystal harpoon
        add(map, 25059, WeaponData.AXE); // Infernal Harpoon (or)
        add(map, 25066, WeaponData.AXE); // Infernal axe (or)
        add(map, 25110, WeaponData.AXE); // Trailblazer axe
        add(map, 25114, WeaponData.AXE); // Trailblazer Harpoon
        // BATTLEAXE
        add(map, 1375, WeaponData.BATTLEAXE); // Bronze battleaxe
        add(map, 4886, WeaponData.BATTLEAXE); // Dharok's greataxe 100
        add(map, 4887, WeaponData.BATTLEAXE); // Dharok's greataxe 75
        add(map, 4888, WeaponData.BATTLEAXE); // Dharok's greataxe 50
        add(map, 4889, WeaponData.BATTLEAXE); // Dharok's greataxe 25
        add(map, 4890, WeaponData.BATTLEAXE); // Dharok's greataxe 0
        add(map, 5060, WeaponData.BATTLEAXE); // Konar's Axe (Blessed)
        add(map, 6589, WeaponData.BATTLEAXE); // White battleaxe
        add(map, 20552, WeaponData.BATTLEAXE); // Rune battleaxe
        // BOW
        add(map, 4212, WeaponData.BOW); // Crystal bow
        add(map, 4236, WeaponData.BOW); // Signed oak bow
        add(map, 4827, WeaponData.BOW); // Comp ogre bow
        add(map, 4934, WeaponData.BOW); // Karil's crossbow 100
        add(map, 4935, WeaponData.BOW); // Karil's crossbow 75
        add(map, 4936, WeaponData.BOW); // Karil's crossbow 50
        add(map, 4937, WeaponData.BOW); // Karil's crossbow 25
        add(map, 4938, WeaponData.BOW); // Karil's crossbow 0
        add(map, 10280, WeaponData.BOW); // Willow comp bow
        add(map, 10282, WeaponData.BOW); // Yew comp bow
        add(map, 10284, WeaponData.BOW); // Magic comp bow
        add(map, 11748, WeaponData.BOW); // New crystal bow (i)
        add(map, 11749, WeaponData.BOW); // Crystal bow full (i)
        add(map, 11750, WeaponData.BOW); // Crystal bow 9/10 (i)
        add(map, 11751, WeaponData.BOW); // Crystal bow 8/10 (i)
        add(map, 11752, WeaponData.BOW); // Crystal bow 7/10 (i)
        add(map, 11753, WeaponData.BOW); // Crystal bow 6/10 (i)
        add(map, 11754, WeaponData.BOW); // Crystal bow 5/10 (i)
        add(map, 11755, WeaponData.BOW); // Crystal bow 4/10 (i)
        add(map, 11756, WeaponData.BOW); // Crystal bow 3/10 (i)
        add(map, 11757, WeaponData.BOW); // Crystal bow 2/10 (i)
        add(map, 11758, WeaponData.BOW); // Crystal bow 1/10 (i)
        add(map, 20401, WeaponData.BOW); // Yew shortbow
        add(map, 20403, WeaponData.BOW); // Maple shortbow
        add(map, 20408, WeaponData.BOW); // Dark bow
        add(map, 20558, WeaponData.BOW); // Magic shortbow
        add(map, 22547, WeaponData.BOW); // Craw's bow (u)
        add(map, 23357, WeaponData.BOW); // Rain bow
        add(map, 23856, WeaponData.BOW); // Corrupted bow (attuned)
        add(map, 23901, WeaponData.BOW); // Crystal bow (basic)
        add(map, 23903, WeaponData.BOW); // Crystal bow (perfected)
        add(map, 26237, WeaponData.BOW); // Zaryte bow
        add(map, 28128, WeaponData.BOW); // Love crossbow
        add(map, 29599, WeaponData.BOW); // Corrupted Dark bow
        add(map, 33163, WeaponData.BOW); // AOE Tier (1) Bow
        add(map, 33164, WeaponData.BOW); // AOE Tier (2) Bow
        add(map, 33165, WeaponData.BOW); // AOE Tier (3) Bow
        add(map, 33166, WeaponData.BOW); // AOE Tier (4) Bow
        add(map, 33167, WeaponData.BOW); // AOE Tier (5) Bow
        add(map, 33168, WeaponData.BOW); // AOE God Tier Bow
        add(map, 33271, WeaponData.BOW); // Ruthless bow
        add(map, 33434, WeaponData.BOW); // Wraith Bow
        add(map, 33435, WeaponData.BOW); // Wraith Crossbow
        // CLAWS
        add(map, 3098, WeaponData.CLAWS); // Black claws
        add(map, 7673, WeaponData.CLAWS); // Boxing gloves
        add(map, 11706, WeaponData.CLAWS); // Beach boxing gloves
        add(map, 13652, WeaponData.CLAWS); // Dragon claws
        add(map, 23206, WeaponData.CLAWS); // Dual sai
        // CRUSH_SWORD
        add(map, 1311, WeaponData.CRUSH_SWORD); // Steel 2h sword
        add(map, 6609, WeaponData.CRUSH_SWORD); // White 2h sword
        add(map, 12808, WeaponData.CRUSH_SWORD); // Sara's blessed sword (full)
        add(map, 20555, WeaponData.CRUSH_SWORD); // Rune 2h sword
        add(map, 20559, WeaponData.CRUSH_SWORD); // Dragon 2h sword
        add(map, 20593, WeaponData.CRUSH_SWORD); // Armadyl godsword
        add(map, 27021, WeaponData.CRUSH_SWORD); // Colossal Blade
        add(map, 37200, WeaponData.CRUSH_SWORD); // Saradomin's Sword(f)
        add(map, 37201, WeaponData.CRUSH_SWORD); // Zamorak's Sword(f)
        add(map, 37202, WeaponData.CRUSH_SWORD); // Guthix's Sword(f)
        // HALBERD
        add(map, 13080, WeaponData.HALBERD); // New crystal halberd full (i)
        add(map, 13081, WeaponData.HALBERD); // Crystal halberd full (i)
        add(map, 13082, WeaponData.HALBERD); // Crystal halberd 9/10 (i)
        add(map, 13083, WeaponData.HALBERD); // Crystal halberd 8/10 (i)
        add(map, 13084, WeaponData.HALBERD); // Crystal halberd 7/10 (i)
        add(map, 13085, WeaponData.HALBERD); // Crystal halberd 6/10 (i)
        add(map, 13086, WeaponData.HALBERD); // Crystal halberd 5/10 (i)
        add(map, 13087, WeaponData.HALBERD); // Crystal halberd 4/10 (i)
        add(map, 13088, WeaponData.HALBERD); // Crystal halberd 3/10 (i)
        add(map, 13089, WeaponData.HALBERD); // Crystal halberd 2/10 (i)
        add(map, 13090, WeaponData.HALBERD); // Crystal halberd 1/10 (i)
        add(map, 13093, WeaponData.HALBERD); // Crystal halberd 9/10
        add(map, 13094, WeaponData.HALBERD); // Crystal halberd 8/10
        add(map, 13095, WeaponData.HALBERD); // Crystal halberd 7/10
        add(map, 13096, WeaponData.HALBERD); // Crystal halberd 6/10
        add(map, 13097, WeaponData.HALBERD); // Crystal halberd 5/10
        add(map, 13098, WeaponData.HALBERD); // Crystal halberd 4/10
        add(map, 13099, WeaponData.HALBERD); // Crystal halberd 3/10
        add(map, 13100, WeaponData.HALBERD); // Crystal halberd 2/10
        add(map, 13101, WeaponData.HALBERD); // Crystal halberd 1/10
        add(map, 23849, WeaponData.HALBERD); // Corrupted halberd (basic)
        add(map, 23850, WeaponData.HALBERD); // Corrupted halberd (attuned)
        add(map, 23851, WeaponData.HALBERD); // Corrupted halberd (perfected)
        add(map, 23895, WeaponData.HALBERD); // Crystal halberd (basic)
        add(map, 23896, WeaponData.HALBERD); // Crystal halberd (attuned)
        add(map, 23897, WeaponData.HALBERD); // Crystal halberd (perfected)
        add(map, 23987, WeaponData.HALBERD); // Crystal halberd
        add(map, 29796, WeaponData.HALBERD); // Noxious halberd
        add(map, 33442, WeaponData.HALBERD); // 100% Thrills
        // MACE
        add(map, 4982, WeaponData.MACE); // Verac's flail 100
        add(map, 4983, WeaponData.MACE); // Verac's flail 75
        add(map, 4984, WeaponData.MACE); // Verac's flail 50
        add(map, 4985, WeaponData.MACE); // Verac's flail 25
        add(map, 4986, WeaponData.MACE); // Verac's flail 0
        add(map, 6601, WeaponData.MACE); // White mace
        add(map, 10888, WeaponData.MACE); // Barrelchest anchor
        add(map, 22398, WeaponData.MACE); // Ivandis flail
        add(map, 22542, WeaponData.MACE); // Viggora's chainmace (u)
        add(map, 27855, WeaponData.MACE); // Strongman's Anchor
        // PICKAXE
        add(map, 12797, WeaponData.PICKAXE); // Dragon pickaxe
        add(map, 13244, WeaponData.PICKAXE); // Infernal pickaxe (uncharged)
        add(map, 23276, WeaponData.PICKAXE); // Gilded pickaxe
        add(map, 23682, WeaponData.PICKAXE); // Crystal pickaxe (inactive)
        add(map, 23822, WeaponData.PICKAXE); // Corrupted pickaxe
        add(map, 23863, WeaponData.PICKAXE); // Crystal pickaxe
        add(map, 25063, WeaponData.PICKAXE); // Infernal Pickaxe (or)
        add(map, 25112, WeaponData.PICKAXE); // Trailblazer Pickaxe
        // SCYTHE
        add(map, 22486, WeaponData.SCYTHE); // Scythe of vitur (uncharged)
        add(map, 33280, WeaponData.SCYTHE); // Heaven scythe
        add(map, 33342, WeaponData.SCYTHE); // shadow scythe
        add(map, 33431, WeaponData.SCYTHE); // Wraith Scythe
        // SLASH_SWORD
        add(map, 4503, WeaponData.SLASH_SWORD); // Decorative sword
        add(map, 4508, WeaponData.SLASH_SWORD); // Decorative sword
        add(map, 6313, WeaponData.SLASH_SWORD); // Opal machete
        add(map, 6315, WeaponData.SLASH_SWORD); // Jade machete
        add(map, 6317, WeaponData.SLASH_SWORD); // Red topaz machete
        add(map, 6607, WeaponData.SLASH_SWORD); // White longsword
        add(map, 6611, WeaponData.SLASH_SWORD); // White scimitar
        add(map, 6721, WeaponData.SLASH_SWORD); // Barbarian's Scimitar
        add(map, 6745, WeaponData.SLASH_SWORD); // Silverlight
        add(map, 6818, WeaponData.SLASH_SWORD); // Bow-sword
        add(map, 7140, WeaponData.SLASH_SWORD); // Lucky cutlass
        add(map, 7141, WeaponData.SLASH_SWORD); // Harry's cutlass
        add(map, 20402, WeaponData.SLASH_SWORD); // Rune scimitar
        add(map, 20406, WeaponData.SLASH_SWORD); // Dragon scimitar
        add(map, 21646, WeaponData.SLASH_SWORD); // Granite longsword
        add(map, 22316, WeaponData.SLASH_SWORD); // Cosmetic sword
        add(map, 23330, WeaponData.SLASH_SWORD); // Rune scimitar
        add(map, 23332, WeaponData.SLASH_SWORD); // Rune scimitar
        add(map, 23334, WeaponData.SLASH_SWORD); // Rune scimitar
        add(map, 23997, WeaponData.SLASH_SWORD); // Blade of saeldor (inactive)
        add(map, 24157, WeaponData.SLASH_SWORD); // Decorative sword (l)
        add(map, 24219, WeaponData.SLASH_SWORD); // Swift blade
        add(map, 24537, WeaponData.SLASH_SWORD); // Carrot sword
        add(map, 24539, WeaponData.SLASH_SWORD); // '24-carat' sword
        add(map, 24551, WeaponData.SLASH_SWORD); // Blade Of Saeldor (c)
        add(map, 29084, WeaponData.SLASH_SWORD); // Sulphur blades
        add(map, 33273, WeaponData.SLASH_SWORD); // Rutheless sword
        add(map, 33329, WeaponData.SLASH_SWORD); // Starlight sword
        add(map, 33335, WeaponData.SLASH_SWORD); // Paradise sword
        add(map, 33417, WeaponData.SLASH_SWORD); // Gifted Sulpher Blades
        add(map, 33430, WeaponData.SLASH_SWORD); // Wraith Sword
        // SOTD
        add(map, 27277, WeaponData.SOTD); // Toxic staff of the dead
        // SPEAR
        add(map, 3170, WeaponData.SPEAR); // Bronze spear(kp)
        add(map, 3171, WeaponData.SPEAR); // Iron spear(kp)
        add(map, 3172, WeaponData.SPEAR); // Steel spear(kp)
        add(map, 3173, WeaponData.SPEAR); // Mithril spear(kp)
        add(map, 3174, WeaponData.SPEAR); // Adamant spear(kp)
        add(map, 3175, WeaponData.SPEAR); // Rune spear(kp)
        add(map, 3176, WeaponData.SPEAR); // Dragon spear(kp)
        add(map, 4584, WeaponData.SPEAR); // Black spear(kp)
        add(map, 4910, WeaponData.SPEAR); // Guthan's warspear 100
        add(map, 4911, WeaponData.SPEAR); // Guthan's warspear 75
        add(map, 4912, WeaponData.SPEAR); // Guthan's warspear 50
        add(map, 4913, WeaponData.SPEAR); // Guthan's warspear 25
        add(map, 4914, WeaponData.SPEAR); // Guthan's warspear 0
        add(map, 6760, WeaponData.SPEAR); // Guthix mjolnir
        add(map, 6762, WeaponData.SPEAR); // Saradomin mjolnir
        add(map, 6764, WeaponData.SPEAR); // Zamorak mjolnir
        add(map, 7804, WeaponData.SPEAR); // Zaros mjolnir
        add(map, 11381, WeaponData.SPEAR); // Bronze hasta(kp)
        add(map, 11388, WeaponData.SPEAR); // Iron hasta(kp)
        add(map, 11395, WeaponData.SPEAR); // Steel hasta(kp)
        add(map, 11402, WeaponData.SPEAR); // Mithril hasta(kp)
        add(map, 11409, WeaponData.SPEAR); // Adamant hasta(kp)
        add(map, 11416, WeaponData.SPEAR); // Rune hasta(kp)
        add(map, 20158, WeaponData.SPEAR); // Gilded spear
        add(map, 20397, WeaponData.SPEAR); // Spear
        add(map, 21649, WeaponData.SPEAR); // Merfolk trident
        add(map, 22734, WeaponData.SPEAR); // Dragon hasta(p)
        add(map, 22737, WeaponData.SPEAR); // Dragon hasta(p+)
        add(map, 22740, WeaponData.SPEAR); // Dragon hasta(p++)
        add(map, 22743, WeaponData.SPEAR); // Dragon hasta(kp)
        add(map, 33432, WeaponData.SPEAR); // Wraith Spear
        // STAB_SWORD
        add(map, 1235, WeaponData.STAB_SWORD); // Poisoned dagger(p)
        add(map, 6591, WeaponData.STAB_SWORD); // White dagger
        add(map, 6593, WeaponData.STAB_SWORD); // White dagger(p)
        add(map, 6595, WeaponData.STAB_SWORD); // White dagger(p+)
        add(map, 6597, WeaponData.STAB_SWORD); // White dagger(p++)
        add(map, 6605, WeaponData.STAB_SWORD); // White sword
        add(map, 8872, WeaponData.STAB_SWORD); // Bone dagger
        add(map, 8874, WeaponData.STAB_SWORD); // Bone dagger (p)
        add(map, 8876, WeaponData.STAB_SWORD); // Bone dagger (p+)
        add(map, 8878, WeaponData.STAB_SWORD); // Bone dagger (p++)
        add(map, 10582, WeaponData.STAB_SWORD); // Keris(p)
        add(map, 10583, WeaponData.STAB_SWORD); // Keris(p+)
        add(map, 10584, WeaponData.STAB_SWORD); // Keris(p++)
        add(map, 20407, WeaponData.STAB_SWORD); // Dragon dagger
        add(map, 20554, WeaponData.STAB_SWORD); // Toktz-xil-ak
        add(map, 20779, WeaponData.STAB_SWORD); // Hunting knife
        add(map, 21206, WeaponData.STAB_SWORD); // Dragon sword
        add(map, 27810, WeaponData.STAB_SWORD); // Dragon candle dagger
        add(map, 27861, WeaponData.STAB_SWORD); // Abyssal dagger (bh)
        add(map, 27871, WeaponData.STAB_SWORD); // giant bronze dagger
        // STAFF
        add(map, 772, WeaponData.STAFF); // Dramen staff
        add(map, 1379, WeaponData.STAFF); // Staff
        add(map, 1389, WeaponData.STAFF); // Magic staff
        add(map, 1391, WeaponData.STAFF); // Battlestaff
        add(map, 1410, WeaponData.STAFF); // Iban's staff
        add(map, 4862, WeaponData.STAFF); // Ahrim's staff 100
        add(map, 4863, WeaponData.STAFF); // Ahrim's staff 75
        add(map, 4864, WeaponData.STAFF); // Ahrim's staff 50
        add(map, 4865, WeaponData.STAFF); // Ahrim's staff 25
        add(map, 4866, WeaponData.STAFF); // Ahrim's staff 0
        add(map, 6526, WeaponData.STAFF); // Toktz-mej-tal
        add(map, 6603, WeaponData.STAFF); // White magic staff
        add(map, 7639, WeaponData.STAFF); // Rod of ivandis (10)
        add(map, 7640, WeaponData.STAFF); // Rod of ivandis (9)
        add(map, 7641, WeaponData.STAFF); // Rod of ivandis (8)
        add(map, 7642, WeaponData.STAFF); // Rod of ivandis (7)
        add(map, 7643, WeaponData.STAFF); // Rod of ivandis (6)
        add(map, 7644, WeaponData.STAFF); // Rod of ivandis (5)
        add(map, 7645, WeaponData.STAFF); // Rod of ivandis (4)
        add(map, 7646, WeaponData.STAFF); // Rod of ivandis (3)
        add(map, 7647, WeaponData.STAFF); // Rod of ivandis (2)
        add(map, 7648, WeaponData.STAFF); // Rod of ivandis (1)
        add(map, 8841, WeaponData.STAFF); // Void knight mace
        add(map, 9013, WeaponData.STAFF); // Skull sceptre
        add(map, 9044, WeaponData.STAFF); // Pharaoh's sceptre (3)
        add(map, 9046, WeaponData.STAFF); // Pharaoh's sceptre (2)
        add(map, 9048, WeaponData.STAFF); // Pharaoh's sceptre (1)
        add(map, 9050, WeaponData.STAFF); // Pharaoh's sceptre
        add(map, 9084, WeaponData.STAFF); // Lunar staff
        add(map, 9091, WeaponData.STAFF); // Lunar staff - pt1
        add(map, 9092, WeaponData.STAFF); // Lunar staff - pt2
        add(map, 9093, WeaponData.STAFF); // Lunar staff - pt3
        add(map, 10440, WeaponData.STAFF); // Saradomin crozier
        add(map, 10442, WeaponData.STAFF); // Guthix crozier
        add(map, 10444, WeaponData.STAFF); // Zamorak crozier
        add(map, 11709, WeaponData.STAFF); // Cursed goblin staff
        add(map, 11905, WeaponData.STAFF); // Trident of the seas (full)
        add(map, 11908, WeaponData.STAFF); // Uncharged trident
        add(map, 12199, WeaponData.STAFF); // Ancient crozier
        add(map, 12263, WeaponData.STAFF); // Armadyl crozier
        add(map, 12275, WeaponData.STAFF); // Bandos crozier
        add(map, 12439, WeaponData.STAFF); // Royal sceptre
        add(map, 12795, WeaponData.STAFF); // Steam battlestaff
        add(map, 12796, WeaponData.STAFF); // Mystic steam staff
        add(map, 12900, WeaponData.STAFF); // Uncharged toxic trident
        add(map, 13074, WeaponData.STAFF); // Pharaoh's sceptre (8)
        add(map, 13075, WeaponData.STAFF); // Pharaoh's sceptre (7)
        add(map, 13076, WeaponData.STAFF); // Pharaoh's sceptre (6)
        add(map, 13077, WeaponData.STAFF); // Pharaoh's sceptre (5)
        add(map, 13078, WeaponData.STAFF); // Pharaoh's sceptre (4)
        add(map, 20431, WeaponData.STAFF); // Ancient staff
        add(map, 20473, WeaponData.STAFF); // Void knight mace (broken)
        add(map, 20553, WeaponData.STAFF); // Beginner wand
        add(map, 20556, WeaponData.STAFF); // Apprentice wand
        add(map, 20560, WeaponData.STAFF); // Master wand
        add(map, 20730, WeaponData.STAFF); // Mist battlestaff
        add(map, 20733, WeaponData.STAFF); // Mystic mist staff
        add(map, 21200, WeaponData.STAFF); // Mystic lava staff
        add(map, 21276, WeaponData.STAFF); // Skull sceptre (i)
        add(map, 22288, WeaponData.STAFF); // Trident of the seas (e)
        add(map, 22290, WeaponData.STAFF); // Uncharged trident (e)
        add(map, 22292, WeaponData.STAFF); // Trident of the swamp (e)
        add(map, 22294, WeaponData.STAFF); // Uncharged toxic trident (e)
        add(map, 22368, WeaponData.STAFF); // Bryophyta's staff (uncharged)
        add(map, 22370, WeaponData.STAFF); // Bryophyta's staff
        add(map, 22481, WeaponData.STAFF); // Sanguinesti staff (uncharged)
        add(map, 23363, WeaponData.STAFF); // Staff of bob the cat
        add(map, 23820, WeaponData.STAFF); // Corrupted sceptre
        add(map, 23852, WeaponData.STAFF); // Corrupted staff (basic)
        add(map, 23853, WeaponData.STAFF); // Corrupted staff (attuned)
        add(map, 23854, WeaponData.STAFF); // Corrupted staff (perfected)
        add(map, 23861, WeaponData.STAFF); // Crystal sceptre
        add(map, 23898, WeaponData.STAFF); // Crystal staff (basic)
        add(map, 23899, WeaponData.STAFF); // Crystal staff (attuned)
        add(map, 23900, WeaponData.STAFF); // Crystal staff (perfected)
        add(map, 24181, WeaponData.STAFF); // Void knight mace (l)
        add(map, 25733, WeaponData.STAFF); // Holy Sanguinesti Staff (uncharged)
        add(map, 27275, WeaponData.STAFF); // Tumeken's Shadow
        add(map, 27645, WeaponData.STAFF); // Mystic cards
        add(map, 28547, WeaponData.STAFF); // Corrupted tumeken's shadow
        add(map, 28583, WeaponData.STAFF); // Warped Sceptre (uncharged)
        add(map, 28796, WeaponData.STAFF); // staff of amazement
        add(map, 29594, WeaponData.STAFF); // Purging Staff
        add(map, 33169, WeaponData.STAFF); // AOE Tier (1) Staff
        add(map, 33170, WeaponData.STAFF); // AOE Tier (2) Staff
        add(map, 33171, WeaponData.STAFF); // AOE Tier (3) Staff
        add(map, 33172, WeaponData.STAFF); // AOE Tier (4) Staff
        add(map, 33173, WeaponData.STAFF); // AOE God Tier Staff
        add(map, 33174, WeaponData.STAFF); // AOE Tier (5) Staff
        add(map, 33272, WeaponData.STAFF); // Rutheless staff
        add(map, 33318, WeaponData.STAFF); // Wolf staff
        add(map, 33387, WeaponData.STAFF); // Alecs staff
        add(map, 33433, WeaponData.STAFF); // Wraith Staff
        add(map, 37300, WeaponData.STAFF); // Saradomin's Staff (f)
        add(map, 37302, WeaponData.STAFF); // Zamorak's Staff (f)
        add(map, 37304, WeaponData.STAFF); // Guthix's Staff (f)
        // THROWN
        add(map, 818, WeaponData.THROWN); // Poisoned dart(p)
        add(map, 21207, WeaponData.THROWN); // Dragon thrownaxe
        add(map, 22806, WeaponData.THROWN); // Dragon knife(p)
        add(map, 22808, WeaponData.THROWN); // Dragon knife(p+)
        add(map, 22810, WeaponData.THROWN); // Dragon knife(p++)
        add(map, 25849, WeaponData.THROWN); // Amethyst dart
        add(map, 33177, WeaponData.THROWN); // Golden blowpipe
        // WARHAMMER
        add(map, 4599, WeaponData.WARHAMMER); // Oak blackjack
        add(map, 4600, WeaponData.WARHAMMER); // Willow blackjack
        add(map, 4958, WeaponData.WARHAMMER); // Torag's hammers 100
        add(map, 4959, WeaponData.WARHAMMER); // Torag's hammers 75
        add(map, 4960, WeaponData.WARHAMMER); // Torag's hammers 50
        add(map, 4961, WeaponData.WARHAMMER); // Torag's hammers 25
        add(map, 4962, WeaponData.WARHAMMER); // Torag's hammers 0
        add(map, 5018, WeaponData.WARHAMMER); // Bone club
        add(map, 6408, WeaponData.WARHAMMER); // Oak blackjack(o)
        add(map, 6410, WeaponData.WARHAMMER); // Oak blackjack(d)
        add(map, 6412, WeaponData.WARHAMMER); // Willow blackjack(o)
        add(map, 6414, WeaponData.WARHAMMER); // Willow blackjack(d)
        add(map, 6416, WeaponData.WARHAMMER); // Maple blackjack
        add(map, 6418, WeaponData.WARHAMMER); // Maple blackjack(o)
        add(map, 6420, WeaponData.WARHAMMER); // Maple blackjack(d)
        add(map, 6613, WeaponData.WARHAMMER); // White warhammer
        add(map, 20557, WeaponData.WARHAMMER); // Granite maul
        add(map, 20785, WeaponData.WARHAMMER); // Dragon warhammer
        add(map, 21205, WeaponData.WARHAMMER); // Elder maul
        add(map, 23235, WeaponData.WARHAMMER); // Tzhaar-ket-om (t)
        add(map, 27100, WeaponData.WARHAMMER); // Elder maul (or)
        add(map, 33441, WeaponData.WARHAMMER); // Frying pan
        // WHIP
        add(map, 20405, WeaponData.WHIP); // Abyssal whip
    }
    private static void add(Map<Integer, WeaponData> map, int id, WeaponData style) {
        WeaponData previous = map.putIfAbsent(id, style);
        if (previous != null && previous != style) throw new IllegalStateException("Conflicting weapon styles for " + id);
    }
}
