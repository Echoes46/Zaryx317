package io.zaryx.util.discord.impl;

/**
 * Slash command: /tpdelete name:<string>
 * Deletes a player's Trading Post listings and refunds remaining (unsold) quantity to offline storage.
 */
//public class TpDelete extends ListenerAdapter {
//
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
//        if (!"tpdelete".equals(e.getName())) {
//            return;
//        }
//
//        OptionMapping nameOpt = e.getOption("name");
//        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
//            e.reply("Usage: `/tpdelete name:<player>`").setEphemeral(true).queue();
//            return;
//        }
//
//        final String rawName = nameOpt.getAsString().trim();
//        final String normalizedName = rawName.toLowerCase();
//
//        // Fetch sales for the player
//        List<Sale> sales = Listing.getSales(normalizedName);
//        if (sales == null || sales.isEmpty()) {
//            e.reply("No Trading Post listings found for **" + Misc.capitalizeJustFirst(normalizedName) + "**.")
//                    .setEphemeral(true).queue();
//            return;
//        }
//
//        int totalRefundedStacks = 0;
//        int totalRefundedItems = 0;
//
//        for (Sale sale : sales) {
//            int unsold = sale.getQuantity() - sale.getTotalSold();
//            if (unsold > 0) {
//                ItemCollection.add(normalizedName, new GameItem(sale.getId(), unsold));
//                totalRefundedStacks++;
//                totalRefundedItems += unsold;
//            }
//
//            // Mark as closed/collected and persist
//            sale.setHasSold(true);
//            sale.setLastCollectedSold(0);
//            Listing.save(sale);
//        }
//
//        String pretty = Misc.capitalizeJustFirst(normalizedName);
//        Discord.writeGiveLog("[TP-Delete] " + e.getUser().getName() + " deleted " + pretty
//                + "'s Trading Post listings (refund stacks=" + totalRefundedStacks
//                + ", items=" + totalRefundedItems + ").");
//
//        e.reply("🗑️ Deleted **" + pretty + "**’s Trading Post listings. "
//                        + (totalRefundedItems > 0
//                        ? "Refunded **" + totalRefundedItems + "** item(s) across **" + totalRefundedStacks + "** stack(s) to offline storage."
//                        : "No items to refund."))
//                .queue();
//    }
//}
