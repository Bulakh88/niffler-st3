package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO {

    private final static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);

    @Override
    public int createUser(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) " +
                                 "VALUES (?, ?)")) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());

                createdRows = usersPs.executeUpdate();
                UUID generatedUserId;

                try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t obtain id from given ResultSet");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                user.setId(generatedUserId);
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUserById(UUID userId) {
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?");

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "DELETE FROM authorities WHERE user_id = ?")) {

                authorityPs.setObject(1, userId);
                authorityPs.executeUpdate();

                usersPs.setObject(1, userId);
                usersPs.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        try (Connection conn = authDs.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET " +
                             "password=?," +
                             "enabled=?," +
                             "account_non_expired=?," +
                             "account_non_locked=?," +
                             "credentials_non_expired=?" +
                             "WHERE id=?")) {

            ps.setString(1, pe.encode(user.getPassword()));
            ps.setBoolean(2, user.getEnabled());
            ps.setBoolean(3, user.getAccountNonExpired());
            ps.setBoolean(4, user.getAccountNonLocked());
            ps.setBoolean(5, user.getCredentialsNonExpired());
            ps.setObject(6, user.getId());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new IllegalArgumentException("User with id " + user.getId() + " not found");
            }
            return getUserById(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        UserEntity user = new UserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, userId);

                usersPs.execute();
                ResultSet rs = usersPs.getResultSet();

                while (rs.next()) {
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
