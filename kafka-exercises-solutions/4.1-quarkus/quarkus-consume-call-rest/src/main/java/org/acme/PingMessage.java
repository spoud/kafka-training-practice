package org.acme;


public class PingMessage {

    private int number;

    public PingMessage() {
    }

    public PingMessage(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "PingMessage{" +
                "number=" + number +
                '}';
    }
}
