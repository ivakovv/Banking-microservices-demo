package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.account.AccountDto;
import org.example.account_processing.mapper.AccountMapper;
import org.example.account_processing.model.Account;
import org.example.account_processing.repository.AccountRepository;
import org.example.account_processing.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public Account createAccount(Account account) {
        log.info("Creating account from ready object: {}", account);
        
        Account existingAccount = accountRepository.findByClientIdAndProductId(
                account.getClientId(), account.getProductId());
        if (existingAccount != null) {
            log.warn("Account already exists for clientId: {}, productId: {}", 
                    account.getClientId(), account.getProductId());
            return existingAccount;
        }

        Account savedAccount = accountRepository.save(account);
        
        log.info("Successfully created account with id: {} for clientId: {}, productId: {}, cardExist: {}",
                savedAccount.getId(), account.getClientId(), account.getProductId(), 
                savedAccount.getCardExist());
        
        return savedAccount;
    }

    @Override
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    @Override
    @Transactional
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public AccountDto getAccountByClientAndProductId(String clientId, String productId) {
        Account account = accountRepository.findByClientIdAndProductId(
                clientId, productId);
        if (account == null){
            throw new IllegalArgumentException(
                    String.format("Account not found for clientId %s with product %s", clientId, productId));
        }
        return accountMapper.toDto(account);
    }
}
