package com.campus.userservice.dto;

import com.campus.userservice.model.UserProfile;
import java.io.Serializable;
import java.time.LocalDate;

public class UserProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String cnp;
    private LocalDate dateOfBirth;
    private String phone;

    public static UserProfileDto from(UserProfile p) {
        UserProfileDto dto = new UserProfileDto();
        dto.id = p.getId();
        dto.userId = p.getUser().getId();
        dto.firstName = p.getFirstName();
        dto.lastName = p.getLastName();
        dto.cnp = p.getCnp();
        dto.dateOfBirth = p.getDateOfBirth();
        dto.phone = p.getPhone();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCnp() { return cnp; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPhone() { return phone; }
}
