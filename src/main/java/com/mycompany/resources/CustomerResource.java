package com.mycompany.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.mycompany.entities.Customer;
import com.mycompany.services.CustomerService;
import io.quarkus.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import java.util.List;

@Path("/api/customers")
public class CustomerResource {

    private CustomerService customerService;

    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @POST
    @RolesAllowed("managers")
    public void persistCustomer(@Valid Customer customerInput) {
        customerService.persistCustomer(customerInput);
    }

    @Path("/{id}")
    @PUT
    @RolesAllowed("managers")
    public void updateCustomer(@PathParam("id") Long id, @Valid Customer customerInput) {
        customerService.updateCustomer(id, customerInput);
    }

    @Path("/{id}")
    @DELETE
    @RolesAllowed("managers")
    public void deleteCustomer(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
    }

    @Path("/{id}")
    @GET
    @Authenticated
    public Customer getCustomer(@PathParam("id") Long id) {
        return customerService.getCustomer(id);
    }

    @JsonView(Customer.ListJsonView.class)
    @GET
    @Authenticated
    public List<Customer> listAllCustomers() {
        return customerService.listAllCustomers();
    }
}