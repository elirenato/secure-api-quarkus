package com.mycompany.services;

import com.mycompany.entities.Customer;
import com.mycompany.repositories.CustomerRepository;
import com.mycompany.repositories.StateProvinceRepository;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class CustomerService {
    private StateProvinceRepository stateProvinceRepository;
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, StateProvinceRepository stateProvinceRepository) {
        this.customerRepository = customerRepository;
        this.stateProvinceRepository = stateProvinceRepository;
    }

    private void copyInfo(Customer customerInput, Customer customer) {
        customer.setFirstName(customerInput.getFirstName());
        customer.setLastName(customerInput.getLastName());
        customer.setAddress(customerInput.getAddress());
        customer.setAddress2(customerInput.getAddress2());
        customer.setPostalCode(customerInput.getPostalCode());
        customer.setStateProvince(stateProvinceRepository.findByIdOptional(customerInput.getStateProvince().getId())
                .orElseThrow(NotFoundException::new));
        customer.setEmail(customerInput.getEmail());
    }

    @Transactional
    public Customer persistCustomer(Customer customerInput) {
        Customer customer = new Customer();
        copyInfo(customerInput, customer);
        customerRepository.persist(customer);
        return customer;
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customerInput) {
        Customer customer = customerRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
        copyInfo(customerInput, customer);
        return customerRepository.getEntityManager().merge(customer);
    }

    @Transactional
    public Customer deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
        customerRepository.delete(customer);
        return customer;
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<Customer> listAllCustomers() {
        return customerRepository.listAll(Sort.by("lastName", "firstName"));
    }
}
