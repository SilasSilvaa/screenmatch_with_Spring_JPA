package com.ssilvadev.screenmatch.model;

public enum Catalog {
    ACTION("Action"),
    ROMANCE("Romance"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime");

    private String catalogOmdb;

    Catalog(String catalogOmdb){
        this.catalogOmdb = catalogOmdb;
    }

    public static Catalog fromString(String text) {
        for (Catalog catalog : Catalog.values()) {
            if (catalog.catalogOmdb.equalsIgnoreCase(text)) {
                return catalog;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
