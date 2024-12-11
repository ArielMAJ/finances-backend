package tech.artadevs.finances.dtos;

public class ValueAlreadyInUseResponseDTO {
    boolean alreadyInUse;

    public boolean isAlreadyInUse() {
        return alreadyInUse;
    }

    public ValueAlreadyInUseResponseDTO setAlreadyInUse(boolean alreadyInUse) {
        this.alreadyInUse = alreadyInUse;
        return this;
    }
}
