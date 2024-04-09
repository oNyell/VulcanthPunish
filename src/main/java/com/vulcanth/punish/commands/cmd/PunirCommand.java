package com.vulcanth.punish.commands.cmd;

import com.vulcanth.core.player.role.Role;
import com.vulcanth.punish.Main;
import com.vulcanth.punish.commands.Commands;
import com.vulcanth.punish.database.Database;
import com.vulcanth.punish.database.MySQLDatabase;
import com.vulcanth.punish.enums.punish.PunishType;
import com.vulcanth.punish.enums.reason.Reason;
import com.vulcanth.punish.punish.Punish;
import com.vulcanth.punish.util.Util;
import com.vulcanth.punish.util.Webhook;
import com.vulcanth.reports.bungee.manager.ReportsManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import com.vulcanth.punish.punish.dao.PunishDao;

import javax.sql.rowset.CachedRowSet;
import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Stream;

public class PunirCommand extends Commands {

    public PunirCommand() {
        super("punir", "punish");
    }
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yy HH:mm");
    private static PunishDao punishDao;

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("punishapi.punir")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente Ajudante ou superior podem executar este comando."));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUso incorreto, use /punir <player> e selecione o motivo."));
            return;
        }

        if (args.length == 1) {
            String targetName = args[0];

            if (targetName.equals(sender.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se punir."));
                return;
            }
            if (impossibleToBan(targetName)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir este jogador."));
                return;
            }


            sender.sendMessage(TextComponent.fromLegacyText("§eTipos de infração disponíveis:\n"));

            boolean a = true;

            for (Reason value : Reason.values()) {
                String punishType = value.getPunishType().name().replace("TEMP", "");

                if (sender.hasPermission("punish.type." + punishType.toLowerCase())) {
                    TextComponent text = new TextComponent((a ? "§f" : "§7") + value.getText());
                    String rank;

                    switch (value.getPunishType()) {
                        case BAN:
                        case MUTE:
                            rank = "§2Moderador";
                            break;
                        case TEMPBAN:
                        case TEMPMUTE:
                        default:
                            rank = "§eAjudante";
                            break;
                    }
                    StringBuilder hoverText = new StringBuilder();
                    hoverText.append("§e").append(value.getText()).append("\n\n");
                    hoverText.append(value.getFunction()).append("\n\n");
                    hoverText.append("§fGrupo mínimo: ").append(rank).append("\n");
                    hoverText.append("§fRedes: §7MINIGAMES").append("\n\n");


                    for (int i = 0; i < value.getOccurrenceTime().length; i++) {
                        hoverText.append("§e").append(Util.getOrdinalNumber(i + 1)).append("§e: ");
                        hoverText.append("§f[" + value.getPunishType().name().replace("TEMP", "") + "§f] ");
                        hoverText.append(value.getOccurrenceTime()[i] > 0 ? Util.fromLongWithoutDiff(value.getOccurrenceTime()[i]) : "Permanente");
                        hoverText.append("\n");
                    }

                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText.toString())));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/punir " + targetName + " " + value.name() + " <prova>"));
                    sender.sendMessage(text);
                    a = !a;
                }
            }


            sender.sendMessage(TextComponent.fromLegacyText(" "));
            return;
        }
        if (args.length == 2) {
            String targetName = args[0];
            Reason reason;
            if (impossibleToBan(targetName)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir este jogador."));
                return;
            }
            if (targetName.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se punir."));
                return;
            }
            try {
                reason = Reason.valueOf(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVerifique se você deixou um espaço em branco extra no motivo."));
                return;
            }
            if (sender.hasPermission("role.admin")) {
                if (punishDao.getPunishService().getPunishes().stream()
                        .filter(punish -> punish.getPlayerName().equalsIgnoreCase(targetName))
                        .filter(punish -> punish.getReason() == reason)
                        .noneMatch(Punish::isLocked)) {
                    Reason.setPunishDao(punishDao);
                    int occurrences = reason.calculateOccurrences(targetName, reason); // Calcular o número de ocorrências aqui
                    apply(punishDao.createPunish(targetName, sender.getName(), reason, null, reason.getPunishType().name(), occurrences),
                            ProxyServer.getInstance().getPlayer(targetName), sender.getName());
                    sender.sendMessage(TextComponent.fromLegacyText("§ePunição aplicada com sucesso."));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para punir sem prova."));
            }
        }
        if (args.length == 3) {
            String targetName = args[0];
            String proof = args[2];
            Reason reason;

            if (impossibleToBan(targetName)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir este jogador."));
                return;
            }
            if (targetName.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se punir."));
                return;
            }
            try {
                reason = Reason.valueOf(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVerifique se você deixou um espaço em branco extra no motivo."));
                return;
            }
            if (reason != Reason.CONTA_ALTERNATIVA) {
                if (!proof.startsWith("https://")) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cToda prova deve começar com §fhttps://§c."));
                    return;
                }
            }

            if (sender.hasPermission("punish.type." + reason.getPunishType().name().replace("TEMP", "").toLowerCase())) {
                if (punishDao.getPunishService().getPunishes().stream()
                        .filter(punish -> punish.getPlayerName().equalsIgnoreCase(targetName))
                        .filter(punish -> punish.getReason() == reason)
                        .noneMatch(Punish::isLocked)) {
                    Reason.setPunishDao(punishDao);
                    int occurrences = reason.calculateOccurrences(targetName, reason); // Calcular o número de ocorrências aqui
                    apply(punishDao.createPunish(targetName, sender.getName(), reason, proof, reason.getPunishType().name(), occurrences),
                            ProxyServer.getInstance().getPlayer(targetName), sender.getName());
                    sender.sendMessage(TextComponent.fromLegacyText("§ePunição aplicada com sucesso."));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para executar esta punição."));
            }
        }
    }

    private static void apply(Punish punish, ProxiedPlayer target, String staffer) {
        final String textString;
        final Reason reason = punish.getReason();
        final String proof = (punish.getProof() == null ? "Nenhuma" : punish.getProof());

        switch (reason.getPunishType()) {
            case BAN:
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido." +
                        "\n§c* Motivo: " + reason.getText() + " (Permanente)";
                TextComponent texto = new TextComponent("\n§a* O jogador " + Role.getPrefixed(punish.getPlayerName()) + "§a, reportado por você recentemente, acaba de ser punido por " + reason.getText() + "§a. Obrigado por contribuir com a nossa comunidade. Clique ");
                TextComponent add = new TextComponent("§a§lAQUI");
                add.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://vulcanth.com/" + punish.getId()));
                add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§fClique para saber mais.")));
                add.addExtra("§apara mais informações.");
                texto.addExtra(add);
                break;
            case MUTE:
                textString = "§c* " + punish.getPlayerName() + " §cfoi silenciado por " + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: Permanente\n";
                break;
            case TEMPBAN:
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido." +
                        "\n§c* Motivo: " + reason.getText() + " (" + Util.fromLongWithoutDiff(punish.getReason().getOccurrenceTime()) + ")";
                break;

            case TEMPMUTE:
                textString = "\n§c* " + punish.getPlayerName() + " §cfoi silenciado por " + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: " + Util.fromLongWithoutDiff(punish.getReason().getOccurrenceTime());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + reason.getPunishType());
        }
        final TextComponent text = new TextComponent(textString);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkpunir " + punish.getPlayerName()));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§fClique para mais informações.")));

        ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("punishapi.punir")).forEach(o -> {
            o.sendMessage(TextComponent.fromLegacyText(" "));
            o.sendMessage(text);
            o.sendMessage(TextComponent.fromLegacyText(" "));
        });
        if (target != null) {
            target.sendMessage(TextComponent.fromLegacyText(" "));
            target.sendMessage(text);
            target.sendMessage(TextComponent.fromLegacyText(" "));

            if (reason.getPunishType() == PunishType.TEMPBAN) {
                target.disconnect(TextComponent.fromLegacyText("§c§lVULCANTH\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: " + Util.fromLong(punish.getExpire()) +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em §evulcanth.com/forum"));
                return;
            }
            if (reason.getPunishType() == PunishType.BAN) {
                target.disconnect(TextComponent.fromLegacyText("§c§lVULCANTH\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: Permanente" +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em §evulcanth.com/forum"));
            }
        }
    }

    static {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        punishDao = Main.getInstance().getPunishDao();
    }

    private static boolean impossibleToBan(String nickName) {
        return Stream.of("oNyell", "oJVzinn").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }

}

