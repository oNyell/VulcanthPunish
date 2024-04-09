package com.vulcanth.punish.enums.punish;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum PunishType {
    BAN("Banimento permanente"),
    TEMPBAN("Banimento temporário"),
    MUTE("Mute permanente"),
    TEMPMUTE("Mute temporário");

    private final String text;

    PunishType(String text) {
        this.text = text;
    }
}
