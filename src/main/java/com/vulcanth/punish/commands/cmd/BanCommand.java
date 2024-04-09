package com.vulcanth.punish.commands.cmd;

import com.vulcanth.core.utils.StringUtils;
import com.vulcanth.punish.commands.Commands;
import com.vulcanth.punish.enums.punish.PunishType;
import com.vulcanth.punish.enums.reason.Reason;
import com.vulcanth.punish.punish.Punish;
import com.vulcanth.punish.punish.dao.PunishDao;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.stream.Stream;

public class BanCommand extends Commands {
    public BanCommand() {
        super("ban", "vban", "banip", "ban-ip");
    }

    public static Punish punish;

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("role.gerente")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cSomente Gerente ou superior podem executar este comando."));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize \"/ban <user> <duração> [motivo]"));
            return;
        }
        String target = args[0];
        if (impossibleToBan(target)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode banir esse usuário"));
            return;
        }

        String format = StringUtils.formatColors(StringUtils.join((Object[])args, " "));
        if (format == null) {
            sender.sendMessage(TextComponent.fromLegacyText("\n§eBanimento aplicado com sucesso."));
        }
        ProxyServer.getInstance().getPlayers().stream().filter(player -> player.hasPermission("punishapi.punir")).forEach(player -> {
            player.sendMessage(TextComponent.fromLegacyText("§c- " + punish.getPlayerName() + " §cfoi banido por " + sender.getName() +
                    "\n§c- Motivo: " + format +
                    "\n§c- Duração: Permanente\n"));
        });
        PunishDao punish = new PunishDao();
        //apply(punish.createPunish(target, sender.getName(), Reason.valueOf("HACK"), null, PunishType.BAN.name(), occ), ProxyServer.getInstance().getPlayer(target), sender.getName());
    }
    private void apply(Punish punish, ProxiedPlayer player, String staffer) {
        ProxyServer.getInstance().getPlayers().stream().filter(online -> online.hasPermission("punishapi.punir")).forEach(online -> {
            online.sendMessage(TextComponent.fromLegacyText("§c- " + punish.getPlayerName() + " §cfoi banido por " + staffer+
                    "\n§c- Motivo: " + punish.getReason() +
                    "\n§c- Duração: Permanente\n"));
        });
    }
    private static boolean impossibleToBan(String nickName) {
        return Stream.of("oNyell", "oJVzinn").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }
}
