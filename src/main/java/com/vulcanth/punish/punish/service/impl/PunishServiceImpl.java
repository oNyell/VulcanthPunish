package com.vulcanth.punish.punish.service.impl;

import com.vulcanth.punish.punish.Punish;
import com.vulcanth.punish.punish.service.PunishService;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class PunishServiceImpl implements PunishService {

    private final List<Punish> punishSet;

    public PunishServiceImpl() {
        punishSet = new LinkedList<>();
    }

    @Override
    public void create(Punish punish) {
        punishSet.add(punish);
    }

    @Override
    public void remove(String integer) {
        punishSet.remove(get(integer));
    }

    @Override
    public Punish get(String integer) {
        return search(integer).filter(punish -> punish.getId().equals(integer)).findFirst().orElse(null);
    }

    @Override
    public Stream<Punish> search(String integer) {
        return punishSet.stream().filter(punish -> punish.getId().equals(integer));
    }

    @Override
    public List<Punish> getPunishes() {
        return punishSet;
    }
}
