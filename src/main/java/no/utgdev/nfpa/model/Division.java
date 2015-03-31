package no.utgdev.nfpa.model;

import java.util.List;

public class Division {
    private static int counterID = 0;

    public final int id;
    public final String name, description;
    public final List<DivisionOption> options;
    public final double x, y;

    public Division(String name, String description, List<DivisionOption> options, double x, double y) {
        this.id = ++counterID;
        this.name = name;
        this.description = description;
        this.options = options;
        this.x = x;
        this.y = y;
    }
}
