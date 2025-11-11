package io.zaryx.util.discord;

public enum DiscordChannelType {

    /**
     * Guild
     */
    GUILD_ID(1182479719095078914L),

    HELP_CHAT(1437598078394699886L),
    RARE_DROPS(1427687582774595695L),
    SKILLING_EVENTS(1436964893730537473L),
    //GE_LISTINGS(),
    BOT_SPAM(1429893722618728499L),
    STAFF_LOGS(1437598078394699886L),
    TRADE_LOGS(1437597996043993338L),
    CHAT_LOGS(1437597885070966926L),
    WORLD_EVENTS(1429883544695472128L),
    SERVER_STATUS(1437137744286711868L),
    PVP_KILLS(1427704620016336916L),
    CHARACTER_LINKING(1429871489372389478L),
    ACHIEVEMENTS_LEVEL_UPS(1427687494723567646L),
    STAFF_COMMAND_CHANNEL(1437597510674940035L),
    ;
    private final long channelId;

    DiscordChannelType(long channelId) {
        this.channelId = channelId;
    }

    public long getChannelId() {
        return channelId;
    }

    public static long getGuildId() {
        return GUILD_ID.channelId;
    }
}
