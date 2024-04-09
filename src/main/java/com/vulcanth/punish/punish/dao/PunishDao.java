package com.vulcanth.punish.punish.dao;

import com.vulcanth.punish.punish.service.PunishService;
import com.vulcanth.punish.punish.service.impl.PunishServiceImpl;
import com.vulcanth.punish.Main;
import com.vulcanth.punish.database.Database;
import com.vulcanth.punish.database.MySQLDatabase;
import com.vulcanth.punish.enums.punish.PunishType;
import com.vulcanth.punish.enums.reason.Reason;
import com.vulcanth.punish.punish.Punish;
import com.vulcanth.punish.thread.PunishThread;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PunishDao {

    private final PunishThread thread;
    @Getter
    private final PunishService punishService;
    @Getter
    private final List<Punish> lastHourPunishes;

    public PunishDao() {
        this.punishService = new PunishServiceImpl();
        this.lastHourPunishes = new ArrayList<>();
        this.thread = Main.getInstance().getPunishThread();
    }

    public Punish createPunish(String targetName, String stafferName, Reason reason, String proof, String type, int occurrences) {
        long expirationTime = reason.calculateExpirationTime(occurrences);
        long currentTime = System.currentTimeMillis();
        Punish punish = Punish.builder()
                .id(UUID.randomUUID().toString().substring(0, 6))
                .playerName(targetName)
                .stafferName(stafferName)
                .reason(reason)
                .type(type)
                .proof(proof)
                .date(currentTime)
                .expire(expirationTime != 0 ? (currentTime + expirationTime) : 0)
                .build();
        CompletableFuture.runAsync(() -> {
            while (getPunishService().getPunishes().stream().anyMatch(p -> p.getId().equals(punish.getId()))) {
                punish.setId(UUID.randomUUID().toString().substring(0, 6));
            }
            punishService.create(punish);
            lastHourPunishes.add(punish);
            Database.getInstance().execute("INSERT INTO `VulcanthPunish` VALUES (?, ?, ?, ?, ?, ?, ?, ?)", punish.getId(), punish.getPlayerName(), punish.getStafferName(), punish.getReason().name(), punish.getType(), punish.getProof(), punish.getDate(), punish.getExpire());
            //Bungee.getPlugin().getProxy().getScheduler().schedule(Bungee.getPlugin(), ()-> ReportManager.sendDeletereport(punish.getPlayerName()), 1L, TimeUnit.SECONDS);
        }, thread);
        return punish;
    }



    public void loadPunishes() {
        try {
            Statement statement = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM VulcanthPunish");
            while (resultSet.next()) {
                punishService.create(Punish.builder().id(resultSet.getString("id")).playerName(resultSet.getString("playerName")).stafferName(resultSet.getString("stafferName")).reason(Reason.valueOf(resultSet.getString("reason"))).proof(resultSet.getString("proof")).date(resultSet.getLong("date")).expire(resultSet.getLong("expires")).build());
            }

            Main.getInstance().getLogger().info("§ePunições ativa com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disablePunish(String id) {
        CompletableFuture.runAsync(() -> {
            Database.getInstance().execute("DELETE FROM `VulcanthPunish` WHERE id = ?", id);
            punishService.remove(id);
            Main.getInstance().getLogger().info("Punish #" + id + " deletado com sucesso");
        }, thread);
    }
    public void clearPunishes(String player) {
        getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getExpire() > 0 && (System.currentTimeMillis() >= punish.getExpire())).forEach(punish -> disablePunish(punish.getId()));
    }

    public Stream<Punish> isBanned(String player) {
        return punishService.getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getReason().getPunishType() == PunishType.TEMPBAN || punish.getReason().getPunishType() == PunishType.BAN).filter(Punish::isLocked);
    }

    public Stream<Punish> isMuted(String player) {
        return punishService.getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getReason().getPunishType() == PunishType.TEMPMUTE || punish.getReason().getPunishType() == PunishType.MUTE).filter(Punish::isLocked);
    }
}