package chy.test;

import chy.test.bean.Query;
import chy.test.bean.User;

import java.util.List;

public interface UserService {

    public String getUser(Integer id);

    public List list();

    public User getUser(Query query);

}
