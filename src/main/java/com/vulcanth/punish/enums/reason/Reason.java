package com.vulcanth.punish.enums.reason;

import com.vulcanth.punish.enums.punish.PunishType;
import com.vulcanth.punish.punish.Punish;
import com.vulcanth.punish.punish.dao.PunishDao;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public enum Reason {
    AMEACA("Ameaça", PunishType.TEMPBAN, "Ameaçar outros jogadores", TimeUnit.DAYS.toMillis(7)),
    ANTIJOGOGAME("Anti jogo (Jogo)", PunishType.TEMPBAN, "Realizar ações que prejudiquem a experiência de jogo", TimeUnit.HOURS.toMillis(7)),
    DISCRIMINACAO("Atitude de discriminação", PunishType.TEMPMUTE, "Realizar comentários discriminatórios", TimeUnit.DAYS.toMillis(7)),
    COMERCIO("Comércio", PunishType.TEMPMUTE, "Realizar comércio indevido", TimeUnit.DAYS.toMillis(3)),
    CONSTRUCAO_INADEQUADA("Construção inadequada", PunishType.TEMPBAN, "Realizar construções inadequadas", TimeUnit.DAYS.toMillis(3)),
    DESINFORMACAO("Desinformação", PunishType.TEMPBAN, "Divulgar informações falsas", TimeUnit.DAYS.toMillis(3)),
    DIVULGACAO_SIMPLES("Divulgação Simples", PunishType.TEMPMUTE, "Divulgar servidores ou conteúdo não relacionado", TimeUnit.DAYS.toMillis(1)),
    DIVULGACAO_GRAVE("Divulgação Grave", PunishType.BAN, "Divulgar servidores ou conteúdo não relacionado", 0),
    FLOOD("Spam ou Flood", PunishType.TEMPMUTE, "Realizar spam ou flood no chat", TimeUnit.HOURS.toMillis(1)),
    HACK("Hack", PunishType.BAN, "Utilizar hacks ou cheats", TimeUnit.DAYS.toMillis(90), 0),
    CONTA_FAKE("Conta Fake", PunishType.BAN, "Utilizar contas falsas", 0),
    ESTORNO_DE_PAGAMENTO("Estorno de pagamento", PunishType.BAN, "Realizar estorno de pagamento", 0),
    CONTA_ALTERNATIVA("Conta alternativa", PunishType.BAN, "Utilizar contas alternativas", 0),
    NICKNAMEINADEQUADO("Nickname inapropriado", PunishType.BAN, "Utilizar nicknames inadequados", 0),
    OFENSA_JOGADOR("Ofensa a jogador", PunishType.TEMPMUTE, "Ofender outros jogadores", TimeUnit.HOURS.toMillis(3)),
    OFENSA_STAFF("Ofensa a staff/servidor", PunishType.TEMPMUTE, "Ofender a equipe de staff ou o servidor", TimeUnit.DAYS.toMillis(5)),
    CROSS_TEAMING("Time ou aliança", PunishType.TEMPBAN, "Realizar alianças ou colaborações entre times", TimeUnit.DAYS.toMillis(7)),
    ABUSODEBUGS("Abuso de bugs", PunishType.TEMPBAN, "Explorar bugs do jogo", TimeUnit.DAYS.toMillis(15)),
    SKININAPROPRIADA("Skin inapropriada", PunishType.TEMPBAN, "Utilizar skins inadequadas", TimeUnit.DAYS.toMillis(1)),
    INCENTIVARFLOOD("Incentivar flood", PunishType.TEMPMUTE, "Incentivar outros jogadores a realizar flood no chat", TimeUnit.DAYS.toMillis(3), TimeUnit.DAYS.toMillis(5)),
    CONVERSAEXPLICITA("Conversa explícita", PunishType.TEMPMUTE, "Realizar conversas explícitas", TimeUnit.DAYS.toMillis(3));

    private final String text;
    private final PunishType punishType;
    private final String function;
    private final long[] occurrenceTime;

    Reason(String text, PunishType punishType, String function, long... occurrenceTime) {
        this.text = text;
        this.punishType = punishType;
        this.function = function;
        this.occurrenceTime = occurrenceTime;
    }
    public long calculatePunishmentTime(int occurrences) {
        if (occurrences >= 0 && occurrences < getOccurrenceTime().length) {
            return getOccurrenceTime()[occurrences];
        } else if (getOccurrenceTime().length > 0) {
            long lastOccurrenceTime = getOccurrenceTime()[getOccurrenceTime().length - 1];
            if (lastOccurrenceTime > 0) {
                return lastOccurrenceTime * (occurrences - getOccurrenceTime().length + 2);
            }
        }
        return 0; // Banimento permanente para qualquer outra ocorrência não listada
    }

    public static void setPunishDao(PunishDao dao) {
        punishDao = dao;
    }

    public long calculateExpirationTime(int occurrences) {
        long totalExpirationTime = 0;
        if (occurrences >= 0 && occurrences < occurrenceTime.length) {
            for (int i = 0; i <= occurrences; i++) {
                totalExpirationTime += occurrenceTime[i];
            }
        }
        return totalExpirationTime * 1000;
    }

    private static PunishDao punishDao;
    public int calculateOccurrences(String targetName, Reason reason) {
        List<Punish> punishes = punishDao.getPunishService().getPunishes();
        int count = 0;
        for (Punish punish : punishes) {
            if (punish.getPlayerName().equalsIgnoreCase(targetName) && punish.getReason() == reason && !punish.isLocked()) {
                count++;
            }
        }
        return count;
    }
}