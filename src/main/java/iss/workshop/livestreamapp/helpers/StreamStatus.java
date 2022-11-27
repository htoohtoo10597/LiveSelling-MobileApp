package iss.workshop.livestreamapp.helpers;

public enum StreamStatus {
    PENDING,
    ONGOING,
    CANCELLED,
    COMPLETED,
    DELETED;

    @Override
    public String toString() {
        return name();
    }
}
