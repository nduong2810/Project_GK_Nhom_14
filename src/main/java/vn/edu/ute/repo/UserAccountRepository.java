package vn.edu.ute.repo;

import vn.edu.ute.model.UserAccount;

public interface UserAccountRepository {
    UserAccount findByUsername(String username);
}