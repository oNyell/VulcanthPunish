package com.vulcanth.punish.punish.service;

import com.vulcanth.punish.model.Model;
import com.vulcanth.punish.punish.Punish;

import java.util.List;

public interface PunishService extends Model<String, Punish> {

    List<Punish> getPunishes();

}
