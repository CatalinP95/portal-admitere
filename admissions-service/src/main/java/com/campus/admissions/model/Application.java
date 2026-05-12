package com.campus.admissions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "addmissionapplic")
@AllArgsConstructor
@Builder
public class Application implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;

    @NotNull(message = "Forma de finantare este obligatorie")
    private Integer formFunding;

    @OneToOne(targetEntity = EducationType.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "education_type")
    private EducationType educationType;

    // PENDING | APPROVED | WAITING_LIST | CONFIRMED | REJECTED | EXPIRED
    private String status;

    private Integer waitingListPosition;

    // DIPLOMA (buget) sau PAYMENT (taxa)
    private String confirmationType;

    private Date confirmationDeadline;
    private Date confirmationDate;

    // procentul din taxa achitat la confirmare (doar pentru taxa)
    private Float paymentPercentage;

    @ManyToOne(targetEntity = Faculty.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Faculty faculty;

    @ManyToOne(targetEntity = Session.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "session_id")
    private Session session;

    private Long userId;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Application() {}

}
