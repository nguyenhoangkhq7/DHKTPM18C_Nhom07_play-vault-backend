package fit.iuh.services;

import fit.iuh.dtos.CustomerDto;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> findAll();
}
