package com.vulcanth.punish.model;

public class ModelAlreadyExist extends Exception {

    public ModelAlreadyExist(){
        super("Este modelo já existe.");
    }

}
