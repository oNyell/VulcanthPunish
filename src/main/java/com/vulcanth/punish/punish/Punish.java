package com.vulcanth.punish.punish;

import com.vulcanth.punish.enums.reason.Reason;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Punish {

    @Setter
    private String id;
    private final String playerName, stafferName, proof, type;
    private final long date, expire;
    private final int occurrence;
    private final Reason reason;
    private final long time;
    private final String text;

    public boolean isLocked() {
        if (expire != 0) {
            return (System.currentTimeMillis() < expire);
        }
        return true;
    }
}
