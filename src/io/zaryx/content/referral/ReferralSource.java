package io.zaryx.content.referral;

import io.zaryx.util.Misc;

public enum ReferralSource {
    RSPS_LIST,
    RUNE_LOCUS,
    TOP_G,
    DISCORD,
    YOUTUBE,
    RUNE_SERVER,
    REF_PANEL
    ;

    @Override
    public String toString() {
        return Misc.formatPlayerName(name().toLowerCase().replaceAll("_", "-"));
    }
}
