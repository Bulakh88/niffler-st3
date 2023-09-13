package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataUserDAOJdbc implements UserDataUserDAO {

    private final static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        try (Connection conn = userdataDs.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement authorityPs = conn.prepareStatement(
                            "DELETE FROM authorities WHERE user_id=?");
                    PreparedStatement usersPs = conn.prepareStatement(
                            "DELETE FROM users WHERE id=?")
            ) {

                usersPs.setObject(1, userId);
                authorityPs.setObject(1, userId);
                authorityPs.executeUpdate();
                usersPs.executeUpdate();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        try (Connection conn = userdataDs.getConnection()) {
            PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users " +
                            "WHERE username = ? ");
            {
                usersPs.setString(1, username);

                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int createUserInUserData(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = userdataDs.getConnection()) {
            PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            {
                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, CurrencyValues.RUB.name());

                createdRows = usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void updateUserInUserData(String oldUserName, String newUserName) {
        try (Connection conn = userdataDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement("UPDATE users set username = ? WHERE username = ?")) {

            usersPs.setString(1, newUserName);
            usersPs.setString(2, oldUserName);

            usersPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
