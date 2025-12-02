package fit.iuh.services.impl;

import fit.iuh.models.Account;
import fit.iuh.models.PublisherRequest;
import fit.iuh.models.enums.AccountStatus;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.repositories.PublisherRequestRepository;
import fit.iuh.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PublisherRequestServiceImpl implements fit.iuh.services.PublisherRequestService {
    private final PublisherRequestRepository repository;
    private final EmailService emailService;

    @Override
    public List<PublisherRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PublisherRequest> getPublisherRequestByUserName(String userName) {
        return repository.findTopByAccountUsername_UsernameOrderByCreatedAtDesc(userName);
    }

    @Override
    @Transactional
    public Optional<PublisherRequest> updateStatus(Long id, RequestStatus status){
        return repository.findById(id).map(req -> {
            Account account = req.getAccountUsername();
            String username = account.getUsername();
            String email = account.getEmail();
            req.setStatus(status);
            req.setUpdatedAt(LocalDate.now());
            if(status == RequestStatus.APPROVED){
                account.setStatus(AccountStatus.ACTIVE);
                emailService.sendPublisherApprovedEmail(email, username);
            }
            else if(status == RequestStatus.REJECTED){
                emailService.sendPublisherRejectedEmail(email, username);
            }

            return repository.save(req); // tự động update DB
        });
    }


}
