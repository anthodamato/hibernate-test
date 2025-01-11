package com.test.hibernate.model;

import java.io.Serializable;
import java.time.LocalDate;

public class GuestRoomBookingId implements Serializable {

    private LocalDate dateOf;
    private Integer roomNumber;

    public LocalDate getDateOf() {
        return dateOf;
    }

    public void setDateOf(LocalDate dateOf) {
        this.dateOf = dateOf;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + dateOf.hashCode();
        hash = 31 * hash + roomNumber.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GuestRoomBookingId))
            return false;

        GuestRoomBookingId roomBookingId = (GuestRoomBookingId) obj;
        if (dateOf.equals(roomBookingId.dateOf) && roomNumber.equals(roomBookingId.roomNumber))
            return true;

        return false;
    }

}
