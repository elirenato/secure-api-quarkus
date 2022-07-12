package com.mycompany.entities;

import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="customers")
public class Customer {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public StateProvince getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(StateProvince stateProvince) {
        this.stateProvince = stateProvince;
    }

    public static class ListJsonView {
        private ListJsonView() {}
    }
    public static class GetJsonView extends ListJsonView {
        private GetJsonView() {}
    }
}
