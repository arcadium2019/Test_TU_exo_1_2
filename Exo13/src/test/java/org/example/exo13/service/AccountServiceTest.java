package org.example.exo13.service;

import org.example.exo13.exception.AccountAlreadyExistsException;
import org.example.exo13.exception.AccountNotFoundException;
import org.example.exo13.exception.InsufficientFundsException;
import org.example.exo13.exception.InvalidAmountException;
import org.example.exo13.model.BankAccount;
import org.example.exo13.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    // ---- Création ----

    @Test
    void create_account_succeeds_and_returns_account_with_zero_balance() {
        // Arrange
        BankAccount account = new BankAccount("Alice");
        when(repository.existsByNumber(anyString())).thenReturn(false);
        when(repository.save(any())).thenReturn(account);

        // Act
        BankAccount result = service.createAccount("Alice");

        // Assert
        assertThat(result.getHolder()).isEqualTo("Alice");
        assertThat(result.getBalance()).isEqualTo(0.0);
    }

    @Test
    void create_account_with_duplicate_number_throws_already_exists() {
        // Arrange - simulate UUID collision
        when(repository.existsByNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.createAccount("Alice"))
                .isInstanceOf(AccountAlreadyExistsException.class);
    }

    // ---- Consultation ----

    @Test
    void find_existing_account_by_number_returns_account() {
        // Arrange
        BankAccount account = new BankAccount("ACC-001", "Bob");
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(account));

        // Act
        BankAccount result = service.findByNumber("ACC-001");

        // Assert
        assertThat(result.getHolder()).isEqualTo("Bob");
    }

    @Test
    void find_non_existent_account_throws_not_found() {
        // Arrange
        when(repository.findByNumber("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.findByNumber("unknown"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void find_all_accounts_returns_complete_list() {
        // Arrange
        List<BankAccount> accounts = List.of(
                new BankAccount("Alice"),
                new BankAccount("Bob")
        );
        when(repository.findAll()).thenReturn(accounts);

        // Act
        List<BankAccount> result = service.findAll();

        // Assert
        assertThat(result).hasSize(2);
    }

    // ---- Dépôt ----

    @Test
    void deposit_valid_amount_increases_balance() {
        // Arrange
        BankAccount account = new BankAccount("ACC-001", "Carol");
        account.setBalance(100.0);
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(account));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BankAccount result = service.deposit("ACC-001", 50.0);

        // Assert
        assertThat(result.getBalance()).isEqualTo(150.0);
    }

    @Test
    void deposit_zero_amount_throws_invalid_amount() {
        // Act & Assert
        assertThatThrownBy(() -> service.deposit("ACC-001", 0))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("strictly positive");
    }

    @Test
    void deposit_negative_amount_throws_invalid_amount() {
        // Act & Assert
        assertThatThrownBy(() -> service.deposit("ACC-001", -10))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("strictly positive");
    }

    // ---- Retrait ----

    @Test
    void withdraw_valid_amount_decreases_balance() {
        // Arrange
        BankAccount account = new BankAccount("ACC-002", "Dave");
        account.setBalance(200.0);
        when(repository.findByNumber("ACC-002")).thenReturn(Optional.of(account));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BankAccount result = service.withdraw("ACC-002", 80.0);

        // Assert
        assertThat(result.getBalance()).isEqualTo(120.0);
    }

    @Test
    void withdraw_zero_amount_throws_invalid_amount() {
        // Act & Assert
        assertThatThrownBy(() -> service.withdraw("ACC-002", 0))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("strictly positive");
    }

    @Test
    void withdraw_negative_amount_throws_invalid_amount() {
        // Act & Assert
        assertThatThrownBy(() -> service.withdraw("ACC-002", -5))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("strictly positive");
    }

    @Test
    void withdraw_with_insufficient_funds_throws() {
        // Arrange
        BankAccount account = new BankAccount("ACC-003", "Eve");
        account.setBalance(50.0);
        when(repository.findByNumber("ACC-003")).thenReturn(Optional.of(account));

        // Act & Assert
        assertThatThrownBy(() -> service.withdraw("ACC-003", 100.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    // ---- Virement ----

    @Test
    void transfer_valid_amount_moves_funds_between_accounts() {
        // Arrange
        BankAccount from = new BankAccount("ACC-F", "Frank");
        from.setBalance(300.0);
        BankAccount to = new BankAccount("ACC-T", "Grace");
        to.setBalance(100.0);
        when(repository.findByNumber("ACC-F")).thenReturn(Optional.of(from));
        when(repository.findByNumber("ACC-T")).thenReturn(Optional.of(to));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.transfer("ACC-F", "ACC-T", 150.0);

        // Assert
        assertThat(from.getBalance()).isEqualTo(150.0);
        assertThat(to.getBalance()).isEqualTo(250.0);
    }

    @Test
    void transfer_zero_amount_throws_invalid_amount() {
        // Act & Assert
        assertThatThrownBy(() -> service.transfer("ACC-F", "ACC-T", 0))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("strictly positive");
    }

    @Test
    void transfer_negative_amount_throws_invalid_amount() {
        // Act & Assert
        assertThatThrownBy(() -> service.transfer("ACC-F", "ACC-T", -50))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("strictly positive");
    }

    @Test
    void transfer_with_insufficient_funds_throws() {
        // Arrange
        BankAccount from = new BankAccount("ACC-F", "Henry");
        from.setBalance(50.0);
        BankAccount to = new BankAccount("ACC-T", "Iris");
        when(repository.findByNumber("ACC-F")).thenReturn(Optional.of(from));
        when(repository.findByNumber("ACC-T")).thenReturn(Optional.of(to));

        // Act & Assert
        assertThatThrownBy(() -> service.transfer("ACC-F", "ACC-T", 100.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void transfer_to_non_existent_account_throws_not_found() {
        // Arrange
        BankAccount from = new BankAccount("ACC-F", "Jack");
        from.setBalance(200.0);
        when(repository.findByNumber("ACC-F")).thenReturn(Optional.of(from));
        when(repository.findByNumber("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.transfer("ACC-F", "missing", 50.0))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void transfer_from_non_existent_account_throws_not_found() {
        // Arrange
        when(repository.findByNumber("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.transfer("missing", "ACC-T", 50.0))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
