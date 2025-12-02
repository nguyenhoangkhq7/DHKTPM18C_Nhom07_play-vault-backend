package fit.iuh.services.impl;

import fit.iuh.dtos.CustomerDto;
import fit.iuh.mappers.CustomerMapper;
import fit.iuh.models.Customer;
import fit.iuh.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements fit.iuh.services.CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toCustomerDtoList(customerRepository.findAll());
    }

}
