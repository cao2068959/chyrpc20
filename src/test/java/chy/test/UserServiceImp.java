package chy.test;

import chy.test.bean.Query;
import chy.test.bean.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserServiceImp implements UserService {



    @Override
    public String getUser(Integer id) {
        return "getUser:"+id;
    }

    @Override
    public User getUser(Query query) {
        User user = new User();
        user.setDate(new Date());
        user.setName(query.getName());
        user.setAge(query.getAge());
        return user;
    }


    @Override
    public List list(){
        List l = new ArrayList();
        l.add("xxx");
        l.add("44444");
        l.add(565345);
        return l;
    }


}
