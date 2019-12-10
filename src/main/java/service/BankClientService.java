package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) {
        return getBankClientDAO().getClientById(id);
    }

    public BankClient getClientByName(String name) {
        return getBankClientDAO().getClientByName(name);
    }

    public List<BankClient> getAllClient() {
        return getBankClientDAO().getAllBankClient();
    }

    public boolean deleteClient(String name) {
        return getBankClientDAO().deleteClientByName(name);
    }



    public boolean addClient(BankClient client) {
        System.out.println("Class: BankClientDAO; method: addClient; Params: client: " + client.toString() + "\n");
        BankClientDAO dao = getBankClientDAO();
        if (!dao.nameIsExist(client.getName())) {
            dao.addClient(client);
            return true;
        }
        return false;
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        BankClientDAO dao = getBankClientDAO();
        if (dao.isClientHasSum(sender.getName(), value) & dao.validateClient(sender.getName(), sender.getPassword())) {
            dao.updateClientsMoney(sender.getName(), sender.getPassword(), -value);
            dao.updateClientsMoney(name, dao.getClientByName(name).getPassword(), value);
            return true;
        } else {
            return false;
        }
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").           //login
                    append("password=admin").       //password
                    append("&serverTimezone=UTC");

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}