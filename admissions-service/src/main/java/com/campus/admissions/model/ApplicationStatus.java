package com.campus.admissions.model;

public enum ApplicationStatus {
    PENDING,        // cerere depusa, asteapta procesare
    APPROVED,       // aprobata de algoritm, asteapta confirmare student
    WAITING_LIST,   // pe lista de asteptare
    CONFIRMED,      // student a confirmat (diploma sau plata)
    REJECTED,       // respinsa de algoritm sau expirata
    EXPIRED         // termenul de confirmare a expirat → promovare din lista de asteptare
}
