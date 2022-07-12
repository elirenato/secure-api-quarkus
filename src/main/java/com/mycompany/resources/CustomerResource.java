package com.mycompany.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.mycompany.entities.Customer;
import com.mycompany.services.CustomerService;

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

    @JsonView(Customer.GetJsonView.class)
    @Path("/{id}")
    @GET
    @RolesAllowed({"operators", "managers"})
    public Customer getCustomer(@PathParam("id") Long id) {
        return customerService.getCustomer(id);
    }

    /* As we use @Transaction at the service layer, at this point, the transaction is already closed, so, to avoid
     * a "classic" LazyInitializationException, we can use JsonView to instruct Jackson to do not serialize
     * the relationship stateProvince that is lazy load and it's not fetched for list purpose.
     */
    @JsonView(Customer.ListJsonView.class)
    @GET
    @RolesAllowed({"operators", "managers"})
    public List<Customer> listAllCustomers() {
        return customerService.listAllCustomers();
    }
}