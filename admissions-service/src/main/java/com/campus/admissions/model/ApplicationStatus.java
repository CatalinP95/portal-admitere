package com.campus.admissions.model;

public enum ApplicationStatus {
    PENDING("PENDING"),        // cerere depusa, asteapta procesare
    APPROVED("APPROVED"),       // aprobata de algoritm, asteapta confirmare student
    WAITING_LIST("WAITING_LIST"),   // pe lista de asteptare
    CONFIRMED("CONFIRMED"),      // student a confirmat (diploma sau plata)
    REJECTED("REJECTED"),       // respinsa de algoritm sau expirata
    EXPIRED("EXPIRED");         // termenul de confirmare a expirat → promovare din lista de asteptare

    public final String status;

    private ApplicationStatus(String status) {
        this.status = status;
    }
}
