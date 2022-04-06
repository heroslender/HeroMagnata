package com.heroslender.magnata.dependencies.hologram;

public interface Hologram {
    void addLine(String line);

    void removeLine(int line);

    void setLine(int line, String text);

    int size();

    void remove();
}
