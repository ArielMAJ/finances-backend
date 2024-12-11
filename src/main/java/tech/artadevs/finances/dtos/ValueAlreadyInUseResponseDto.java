package tech.artadevs.finances.dtos;

public class ValueAlreadyInUseResponseDto {
    boolean alreadyInUse;

    public boolean isAlreadyInUse() {
        return alreadyInUse;
    }

    public ValueAlreadyInUseResponseDto setAlreadyInUse(boolean alreadyInUse) {
        this.alreadyInUse = alreadyInUse;
        return this;
    }
}
