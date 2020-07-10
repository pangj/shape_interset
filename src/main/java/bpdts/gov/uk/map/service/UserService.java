package bpdts.gov.uk.map.service;

import bpdts.gov.uk.map.model.User;

import java.util.List;

public interface UserService {
    /**
     * get a list of users that live within 50 miles of London centre
     *
     * @return list of users
     */
    List<User> findUsersNearByEsIndex();
}
