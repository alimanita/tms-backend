package com.transport.tms.domain.enums;

public enum TypeHuile {
    W5_30("5W-30"),
    W5_40("5W-40"),
    W10_40("10W-40"),
    W15_40("15W-40"),
    W20_50("20W-50"),
    SYNTHETIQUE("Synthétique"),
    SEMI_SYNTHETIQUE("Semi-synthétique"),
    MINERALE("Minérale");

    private final String label;

    TypeHuile(String label) { this.label = label; }

    public String getLabel() { return label; }
}