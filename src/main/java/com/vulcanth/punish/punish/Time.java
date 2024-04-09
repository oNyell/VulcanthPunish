package com.vulcanth.punish.punish;

import com.vulcanth.punish.enums.punish.PunishType;
import com.vulcanth.punish.util.Util;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class Time {
    public static PunishType withTime(PunishType punishType, long time, TimeUnit unit) {
        if (punishType == PunishType.TEMPMUTE || punishType == PunishType.TEMPBAN) {
            String durationText = Util.formatTime(unit.toMillis(time));
            return PunishType.valueOf(punishType.name() + " " + durationText);
        }
        throw new UnsupportedOperationException("PunishType " + punishType + " does not support time duration.");
    }
}

