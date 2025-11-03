package io.zaryx.model.items.bank;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a single tab in a player's bank.
 * Thread-safe, optimized, and fixes item stacking & placeholder issues.
 */
public class BankTab {

    private final List<BankItem> bankItems = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Bank bank;
    private int tabId;

    public BankTab(int tabId, Bank bank) {
        this.tabId = tabId;
        this.bank = bank;
    }

    /**
     * Adds an item to this tab, stacking when possible.
     */
    public void add(BankItem bankItem) {
        if (bankItem == null || bankItem.getId() <= 0 || bankItem.getAmount() <= 0)
            return;

        lock.lock();
        try {
            for (int i = 0; i < bankItems.size(); i++) {
                BankItem item = bankItems.get(i);
                if (item.getId() == bankItem.getId()) {
                    long total = (long) item.getAmount() + (long) bankItem.getAmount();
                    item.setAmount(total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total);
                    bankItems.set(i, item); // replace for safety
                    return;
                }
            }
            bankItems.add(new BankItem(bankItem.getId(), bankItem.getAmount()));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes an item from this tab.
     */
    public void remove(BankItem bankItem, boolean placeholder) {
        if (bankItem == null || bankItem.getId() <= 0)
            return;

        lock.lock();
        try {
            Iterator<BankItem> it = bankItems.iterator();
            while (it.hasNext()) {
                BankItem item = it.next();
                if (item.getId() == bankItem.getId()) {
                    int remaining = item.getAmount() - bankItem.getAmount();
                    if (remaining <= 0) {
                        if (placeholder) {
                            item.setAmount(0);
                        } else {
                            it.remove();
                        }
                    } else {
                        item.setAmount(remaining);
                    }
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(BankItem item) {
        return bankItems.stream().anyMatch(i -> i.getId() == item.getId());
    }

    public boolean containsAmount(BankItem item) {
        return bankItems.stream().anyMatch(i -> i.getId() == item.getId() && i.getAmount() >= item.getAmount());
    }

    public int getItemAmount(BankItem item) {
        return bankItems.stream()
                .filter(i -> i.getId() == item.getId())
                .mapToInt(BankItem::getAmount)
                .sum();
    }

    public int size() {
        return bankItems.size();
    }

    public int freeSlots() {
        return Math.max(0, bank.getBankCapacity() - bank.getItemCount());
    }

    public List<BankItem> getItems() {
        return bankItems;
    }

    public BankItem getItem(int slot) {
        return slot >= 0 && slot < bankItems.size() ? bankItems.get(slot) : null;
    }

    public void setItem(int slot, BankItem item) {
        if (slot >= 0 && slot < bankItems.size())
            bankItems.set(slot, item);
    }

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    /**
     * Checks if the specified item can be added to this bank tab.
     * Considers stacking, free slots, and Integer.MAX_VALUE overflow.
     *
     * @param bankItem The item being checked.
     * @return true if the item can fit safely.
     */
    public boolean spaceAvailable(BankItem bankItem) {
        if (bankItem == null || bankItem.getId() <= 0 || bankItem.getAmount() <= 0)
            return false;

        lock.lock();
        try {
            // 1. If the item already exists, check for stack overflow
            for (BankItem existing : bankItems) {
                if (existing.getId() == bankItem.getId()) {
                    long total = (long) existing.getAmount() + (long) bankItem.getAmount();
                    return total <= Integer.MAX_VALUE;
                }
            }

            // 2. If it doesn't exist, check for free slots in the bank
            int totalItems = bank.getItemCount();
            if (totalItems >= bank.getBankCapacity()) {
                return false;
            }

            return true;
        } finally {
            lock.unlock();
        }
    }

}