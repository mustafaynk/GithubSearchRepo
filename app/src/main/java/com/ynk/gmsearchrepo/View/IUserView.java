package com.ynk.gmsearchrepo.View;

import com.ynk.gmsearchrepo.Model.Repository;
import com.ynk.gmsearchrepo.Model.User;

import java.util.List;

public interface IUserView {
    void getUserDetailResult(User user);
    void getUserRepositories(List<Repository> repositories);
}
