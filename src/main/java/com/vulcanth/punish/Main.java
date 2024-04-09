package com.vulcanth.punish;

import com.vulcanth.core.bungee.Bungee;
import com.vulcanth.punish.commands.Commands;
import com.vulcanth.punish.database.Database;
import com.vulcanth.punish.listeners.Listeners;
import com.vulcanth.punish.punish.dao.PunishDao;
import com.vulcanth.punish.thread.PunishThread;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

@Getter
public class Main extends Plugin {

    private Configuration config;

    private static Main instance;

    private PunishDao punishDao;

    private PunishThread punishThread;

    public Configuration getConfig() {
        return config;
    }

    /**
     * Copia um arquivo a partir de um InputStream.
     *
     * @param input O input para ser copiado.
     * @param out   O arquivo destinario.
     */
    public static void copyFile(InputStream input, File out) {
        FileOutputStream ou = null;
        try {
            ou = new FileOutputStream(out);
            byte[] buff = new byte[1024];
            int len;
            while ((len = input.read(buff)) > 0) {
                ou.write(buff, 0, len);
            }
        } catch (IOException ex) {
            getInstance().getLogger().log(Level.WARNING, "Failed at copy file " + out.getName() + "!", ex);
        } finally {
            try {
                if (ou != null) {
                    ou.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Database.setupDatabase();
        AtomicLong ms = new AtomicLong(System.currentTimeMillis());

        punishThread = new PunishThread();
        punishDao = new PunishDao();
        punishDao.loadPunishes();
        Commands.setupCommands();


        this.getProxy().getPluginManager().registerListener(this, new Listeners());

        this.getLogger().info("§aEste plugin foi ativo com sucesso");
    }

    public void saveDefaultConfig() {
        for (String fileName : new String[]{"config"}) {
            File file = new File("plugins/VulcanthPunish/" + fileName + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                copyFile(Bungee.getInstance().getResourceAsStream(fileName + ".yml"), file);
            }

            try {
                if (fileName.equals("config")) {
                    this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                }
            } catch (IOException ex) {
                this.getLogger().log(Level.WARNING, "Cannot load " + fileName + ".yml: ", ex);
            }
        }
    }

    public void onDisable() {
        punishThread.shutdown();

        this.getLogger().info("§aPlugin desativado com sucesso.");
    }

    public static Main getInstance() {
        return instance;
    }
}
