package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

public class UserDataDAOHibernate extends JpaService implements UserDataUserDAO {


    public UserDataDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.USERDATA).createEntityManager());
    }


    @Override
    public int createUserInUserData(UserDataEntity user) {
        persist(user);
        return 0;
    }

    @Override
    public void deleteUserFromUserData(String username) {
        remove(getUserFromUserData(username));
    }

    @Override
    public UserDataEntity getUserFromUserData(String username) {
        return em.createQuery("select u from UserDataEntity u where u.username=:username",
                        UserDataEntity.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public void updateUserInUserData(UserDataEntity user) {
        merge(user);
    }
}
