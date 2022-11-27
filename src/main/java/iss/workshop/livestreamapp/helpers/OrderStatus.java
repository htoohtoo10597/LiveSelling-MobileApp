package iss.workshop.livestreamapp.helpers;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
}
