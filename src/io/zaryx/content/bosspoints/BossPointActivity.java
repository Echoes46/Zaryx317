package io.zaryx.content.bosspoints;

/** Session-only activity: automatic combat never refreshes this clock. */
public final class BossPointActivity {
    public static final long GRACE_NANOS = java.util.concurrent.TimeUnit.MINUTES.toNanos(15);
    private long lastInput;
    private boolean hasInput;
    private boolean warned;

    public void recordInput(long now) { lastInput = now; hasInput = true; }
    public boolean isActive(long now) { return hasInput && now - lastInput >= 0 && now - lastInput < GRACE_NANOS; }
    public boolean warnOnce() { if (warned) return false; warned = true; return true; }
    public boolean resume() { boolean wasPaused = warned; warned = false; return wasPaused; }

    public static boolean isGameplayInput(int opcode) {
        switch (opcode) {
            case 98: case 164: case 248: // Walk clicks, not server-side following.
            case 72: case 131: case 155: case 17: case 21: case 18: // NPC interaction.
            case 132: case 252: case 70: case 228: case 234: // Objects.
            case 122: case 16: case 75: case 41: case 57: case 53: case 192: // Items.
            case 185: case 184: case 213: case 201: case 40: // Gameplay interfaces/dialogue.
            case 209: case 237: case 181: // Special attack, spells.
                return true;
            default: return false;
        }
    }
}
