package dao;

import com.sun.deploy.util.SessionState;
import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() {
        List<BankClient> bankClients = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_client")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                bankClients.add(new BankClient(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("password"), resultSet.getLong("money")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankClients;
    }

    public boolean validateClient(String name, String password) {
        boolean resultBoolean = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ? AND password= ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                resultBoolean = result.getString("name").equals(name) && result.getString("password").equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultBoolean;
    }


    public void updateClientsMoney(String name, String password, Long transactValue) {

        BankClient bankClient = getClientByName(name);
        if (validateClient(name, password)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bank_client SET money=? WHERE name=? AND password= ?")) {
                preparedStatement.setLong(1,bankClient.getMoney()+transactValue );
                preparedStatement.setString(2, bankClient.getName());
                preparedStatement.setString(3, bankClient.getPassword());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public BankClient getClientById(long id) {
        BankClient bankClient = new BankClient();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_client where id= ?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            bankClient.setId(resultSet.getLong("id"));
            bankClient.setName(resultSet.getString("name"));
            bankClient.setPassword(resultSet.getString("password"));
            bankClient.setMoney(resultSet.getLong("money"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankClient;
    }

    public boolean isClientHasSum(String name, Long expectedSum) {
        if (getClientByName(name).getMoney() >= expectedSum) {
            return true;
        } else {
            return false;
        }
    }

    public long getClientIdByName(String name) throws SQLException {
        return getClientByName(name).getId();
    }

    public BankClient getClientByName(String name) {
        BankClient clientResult = new BankClient();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ?")) {
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            clientResult.setId(result.getLong("id"));
            clientResult.setName(result.getString("name"));
            clientResult.setPassword(result.getString("password"));
            clientResult.setMoney(result.getLong("money"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientResult;
    }

    public void addClient(BankClient client) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bank_client (name, password, money) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setLong(3, client.getMoney());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteClientByName(String name) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM bank_client WHERE name= ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }

    public boolean nameIsExist(String name) {
        return getAllBankClient()
                .stream()
                .map(BankClient::getName)
                .anyMatch(x -> x.equals(name));
    }
}