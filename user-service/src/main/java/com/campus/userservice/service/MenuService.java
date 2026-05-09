package com.campus.userservice.service;

import com.campus.userservice.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MenuService {

    public List<Map<String, String>> getMenuForRole(Role role) {
        return switch (role) {
            case ADMIN -> List.of(
                Map.of("label", "Dashboard", "route", "/dashboard"),
                Map.of("label", "Utilizatori", "route", "/users"),
                Map.of("label", "Anunturi", "route", "/announcements"),
                Map.of("label", "Algoritm Admitere", "route", "/algorithm"),
                Map.of("label", "Sesiuni", "route", "/sessions"),
                Map.of("label", "Statistici", "route", "/statistics")
            );
            case SECRETARIAT -> List.of(
                Map.of("label", "Dashboard", "route", "/dashboard"),
                Map.of("label", "Cereri Admitere", "route", "/applications"),
                Map.of("label", "Anunturi", "route", "/announcements"),
                Map.of("label", "Contracte", "route", "/contracts"),
                Map.of("label", "Lista Asteptare", "route", "/waiting-list")
            );
            case STUDENT -> List.of(
                Map.of("label", "Dashboard", "route", "/dashboard"),
                Map.of("label", "Profilul Meu", "route", "/profile"),
                Map.of("label", "Depune Cerere", "route", "/apply"),
                Map.of("label", "Rezultate", "route", "/results"),
                Map.of("label", "Anunturi", "route", "/announcements"),
                Map.of("label", "Cazare", "route", "/dormitory")
            );
            case CAMIN_ADMIN -> List.of(
                Map.of("label", "Dashboard", "route", "/dashboard"),
                Map.of("label", "Cereri Cazare", "route", "/block-requests"),
                Map.of("label", "Camere", "route", "/rooms"),
                Map.of("label", "Paturi", "route", "/beds"),
                Map.of("label", "Chitante", "route", "/receipts")
            );
        };
    }
}
