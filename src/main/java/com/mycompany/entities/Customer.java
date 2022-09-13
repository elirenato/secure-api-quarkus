package com.mycompany.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name="customers")
public class Customer {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Size(min = 5, max = 255)
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "address", nullable = false)
    private String address;

    @Size(min = 1, max = 255)
    @Column(name = "address2")
    private String address2;

    @Size(min = 1, max = 30)
    @NotBlank
    @Column(name = "postal_code", nullable = false, length = 30)
    private String postalCode;

    @NotNull
    @JsonView(Customer.GetJsonView.class)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_province_id")
    private StateProvince stateProvince;

    public static class ListJsonView {
        private ListJsonView() {}
    }
    public static class GetJsonView extends ListJsonView {
        private GetJsonView() {}
    }
}
