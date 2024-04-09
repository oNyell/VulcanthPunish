package com.vulcanth.punish.commands.cmd;

import com.vulcanth.core.player.role.Role;
import com.vulcanth.punish.Main;
import com.vulcanth.punish.commands.Commands;
import com.vulcanth.punish.punish.Punish;
import com.vulcanth.punish.punish.dao.PunishDao;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;

public class DespunirIDCommand extends Commands {

    public DespunirIDCommand() {
        super("uhsauhsuahusa", "wiwueioa");
    }

    private static PunishDao punishDao;
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("punishapi.cp")) {
            player.sendMessage(TextComponent.fromLegacyText("§fComando desconhecido."));
            return;
        }

        if (args.length < 1) {
            return;
        }
        String id = args[0];
        Punish punish = punishDao.getPunishService().get(id);

        if (punish == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cNão existe punição com este id."));
            return;
        }
        String playerName = punish.getPlayerName();
        String staffName = punish.getStafferName();
        punishDao.disablePunish(id);
        TextComponent text = new TextComponent(TextComponent.fromLegacyText("§cO jogador " + playerName + " §cacabou de ter sua punição revogada."));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("" + Role.getColored(playerName) +
                "\n\n§fID: §e#" + punish.getId() +
                "\n§fMotivo: §7" + punish.getReason().getText() +
                "\n§fAutor da revogação: §7" + staffName +
                "\n§fTipo de punição: §7" + punish.getReason().getPunishType().getText().replace("TEMP", ""))));

        sender.sendMessage(TextComponent.fromLegacyText("§aVocê revogou a punição do jogador " + playerName + "§a."));
        ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("role.admin")).forEach(o -> o.sendMessage(text));
    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }
}
