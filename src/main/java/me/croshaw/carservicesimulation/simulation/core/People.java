package me.croshaw.carservicesimulation.simulation.core;

public class People {
    private String surname;
    private String name;
    private String patronymic;
    public People(String surname, String name, String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(surname, name, patronymic);
    }
    public String toShortString() {
        return "%s %c. %c.".formatted(surname, name.charAt(0), patronymic.charAt(0));
    }
}
