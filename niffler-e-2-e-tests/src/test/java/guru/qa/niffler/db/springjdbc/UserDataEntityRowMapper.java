package guru.qa.niffler.db.springjdbc;

import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataEntityRowMapper implements RowMapper<UserDataEntity> {

    public static final UserDataEntityRowMapper instance = new UserDataEntityRowMapper();

    @Override
    public UserDataEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDataEntity user = new UserDataEntity();
        user.setId(rs.getObject("id", UUID.class));
        user.setUsername(rs.getString("username"));
        user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        user.setFirstname(rs.getString("firstname"));
        user.setSurname(rs.getString("surname"));
        user.setPhoto(rs.getBytes("photo"));
        return user;
    }
}