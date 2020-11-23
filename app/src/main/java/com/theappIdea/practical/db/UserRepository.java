package com.theappIdea.practical.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.theappIdea.practical.model.User;

import java.util.List;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Application application) {
        UserDatabase userDatabase = UserDatabase.getDatabase(application);
        userDao = userDatabase.userDao();
    }

    public LiveData<List<User>> getAllUserList() {
        return userDao.getUserList();
    }

    public void insertUser(User user) {
        new InsertUser(userDao).execute(user);
    }

    private static class InsertUser extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public InsertUser(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.insertUser(users[0]);
            return null;
        }
    }

    public void updateUser(User user) {
        new UpdateUser(userDao).execute(user);
    }

    private static class UpdateUser extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public UpdateUser(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.updateUser(users[0]);
            return null;
        }
    }

    public void deleteUser(User user) {
        new DeleteUser(userDao).execute(user);
    }

    private static class DeleteUser extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public DeleteUser(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.deleteUser(users[0]);
            return null;
        }
    }


}
