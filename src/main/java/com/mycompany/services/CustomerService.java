package com.mycompany.services;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.mycompany.entities.Customer;
import com.mycompany.repositories.CustomerRepository;
import com.mycompany.repositories.StateProvinceRepository;
import io.quarkus.panache.common.Sort;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@ApplicationScoped
public class CustomerService {
    private StateProvinceRepository stateProvinceRepository;
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, StateProvinceRepository stateProvinceRepository) {
        this.customerRepository = customerRepository;
        this.stateProvinceRepository = stateProvinceRepository;
    }

    @Transactional
    public Customer persistCustomer(Customer customerInput) {
        Customer customer = new Customer();
        try {
            BeanUtils.copyProperties(customer, customerInput);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        customerRepository.persist(customer);
        return customer;
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customerInput) {
        Customer customer = customerRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
        customerInput.setId(id);
        try {
            BeanUtils.copyProperties(customer, customerInput);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        Customer customer = customerRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
        return customer;
    }

    public List<Customer> listAllCustomers() {
        return customerRepository.listAll(Sort.ascending("lastName", "firstName"));
    }
}
