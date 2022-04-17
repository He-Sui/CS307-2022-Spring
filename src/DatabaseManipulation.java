
import java.io.*;
import java.sql.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManipulation implements DataManipulation {
    private Connection con = null;
    private ResultSet resultSet;

    private String host = "localhost";
    private String dbname = "contract";
    private String user = "checker";
    private String pwd = "123456";
    private String port = "5432";
    private final int BATCH_SIZE = 1000;

    @Override
    public void openDatasource() {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            System.exit(1);
        }

        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
            con = DriverManager.getConnection(url, user, pwd);
            con.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void closeDatasource() {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String findOrdersSoldBySalesmen(String firstname, String surname) {
        StringBuilder sb = new StringBuilder();
        String sql = "select * from orders o join salesman s on o.salesman_number = s.number and s.firstname= ? and s.surname = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, surname);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString("id")).append("\t");
                sb.append(resultSet.getString("product_model")).append("\t");
                sb.append(resultSet.getString("contract_number")).append("\t");
                sb.append(resultSet.getString("salesman_number")).append("\t");
                sb.append(resultSet.getString("quantity")).append("\t");
                sb.append(resultSet.getString("estimated_delivery_date")).append("\t");
                sb.append(resultSet.getString("lodgement_date")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public String findOrdersByProductModel(String model) {
        StringBuilder sb = new StringBuilder();
        String sql = "select id, contract_number, salesman_number, quantity, estimated_delivery_date, lodgement_date from orders where product_model= ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, model);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString("id")).append("\t");
                sb.append(resultSet.getString("contract_number")).append("\t");
                sb.append(resultSet.getString("salesman_number")).append("\t");
                sb.append(resultSet.getString("quantity")).append("\t");
                sb.append(resultSet.getString("estimated_delivery_date")).append("\t");
                sb.append(resultSet.getString("lodgement_date")).append("\t");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public String findOrdersByModelAndContract(String model, String contract_number) {
        StringBuilder sb = new StringBuilder();
        String sql;
        sql = "select * from orders o where o.product_model= ? and o.contract_number= ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, model);
            preparedStatement.setString(2, contract_number);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString("id")).append("\t");
                sb.append(resultSet.getString("product_model")).append("\t");
                sb.append(resultSet.getString("contract_number")).append("\t");
                sb.append(resultSet.getString("salesman_number")).append("\t");
                sb.append(resultSet.getString("quantity")).append("\t");
                sb.append(resultSet.getString("estimated_delivery_date")).append("\t");
                sb.append(resultSet.getString("lodgement_date")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public String selectDistinctModelContractSalesman(int index) {
        String sql;
        String column;
        StringBuilder sb = new StringBuilder();
        if (index == 1) {
            sql = "select distinct product_model from orders";
            column = "product_model";
        } else if (index == 2) {
            sql = "select distinct contract_number from orders";
            column = "contract_number";
        } else if (index == 3) {
            sql = "select distinct salesman_number from orders";
            column = "salesman_number";
        } else
            return null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                sb.append(resultSet.getString(column)).append("\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public void updateQuantity(String contractNumber, String model, int newQuantity) {
        String sql = "update orders set quantity = ? where contract_number = ? and product_model = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setString(2, contractNumber);
            preparedStatement.setString(3, model);
            preparedStatement.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteModel(String model) {
        String sql = "delete from orders where orders.product_model= ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, model);
            preparedStatement.execute();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String findNumberOfProductInContractsMoreThanXInOrder(int x) {
        StringBuilder sb = new StringBuilder();
        String sql = "select p.name, count(*) from orders join model m on orders.product_model = m.model join product p on p.code = m.product_code group by p.name having count(*) > ? order by count(*);";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, x);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString("name")).append("\t");
                sb.append(resultSet.getString("count")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void importData() {
        String line;
        Set<String> area = new HashSet<>();
        Set<String> enterprise_name = new HashSet<>();
        Set<String> salesman_number = new HashSet<>();
        Set<String> product_code = new HashSet<>();
        Set<String> model = new HashSet<>();
        Set<String> contract = new HashSet<>();
        ArrayList<Client> client_list = new ArrayList<>();
        ArrayList<Contract> contract_list = new ArrayList<>();
        ArrayList<SupplyCenter> supply_list = new ArrayList<>();
        ArrayList<Salesman> salesman_list = new ArrayList<>();
        ArrayList<Product> product_list = new ArrayList<>();
        ArrayList<Model> model_list = new ArrayList<>();
        ArrayList<Order> order_list = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("contract_info.csv"))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] info = line.split(",");
                if (!area.contains(info[2])) {
                    area.add(info[2]);
                    supply_list.add(new SupplyCenter(info));
                }
                if (!enterprise_name.contains(info[1])) {
                    enterprise_name.add(info[1]);
                    client_list.add(new Client(info));
                }
                if (!salesman_number.contains(info[16])) {
                    salesman_number.add(info[16]);
                    salesman_list.add(new Salesman(info));
                }
                if (!product_code.contains(info[6])) {
                    product_code.add(info[6]);
                    product_list.add(new Product(info));
                }
                if (!model.contains(info[8])) {
                    model.add(info[8]);
                    model_list.add(new Model(info));
                }
                if (!contract.contains(info[0])) {
                    contract.add(info[0]);
                    contract_list.add(new Contract(info));
                }
                order_list.add(new Order(info));
            }
            long start, end;
            start = System.currentTimeMillis();
            importSupplyCenter(supply_list);
            importClient(client_list);
            importSalesman(salesman_list);
            importProduct(product_list);
            importModel(model_list);
            importContract(contract_list);
            importOrder(order_list);
            end = System.currentTimeMillis();
            System.out.println(end - start);
        } catch (IOException e) {
            System.err.println("Failed to import data from file.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void importClient(ArrayList<Client> list) {
        String sql = "insert into Client (enterprise_name, country, industry, city) values (?,?,?,?)";
        try {
            int cnt = 0;
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (Client client : list) {
                preparedStatement.setString(1, client.enterprise_name);
                preparedStatement.setString(2, client.country);
                preparedStatement.setString(3, client.industry);
                preparedStatement.setString(4, client.city);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
          con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void importSupplyCenter(ArrayList<SupplyCenter> list) {
        String sql = "insert into supply_center (area, director_firstname, director_surname) values (?,?,?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            int cnt = 0;
            for (SupplyCenter supply : list) {
                preparedStatement.setString(1, supply.area);
                preparedStatement.setString(2, supply.firstname);
                preparedStatement.setString(3, supply.surname);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
            con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @Override
    public void importSalesman(ArrayList<Salesman> list) {
        String sql = "insert into salesman (number, firstname, surname, phone_number, gender, age, supply_center) values (?,?,?,?,?,?,?)";
        try {
            int cnt = 0;
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (Salesman salesman : list) {
                preparedStatement.setString(1, salesman.number);
                preparedStatement.setString(2, salesman.first_name);
                preparedStatement.setString(3, salesman.surname);
                preparedStatement.setString(4, salesman.phone_number);
                preparedStatement.setString(5, salesman.gender);
                preparedStatement.setInt(6, salesman.age);
                preparedStatement.setString(7, salesman.supply_center);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
           con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void importProduct(ArrayList<Product> list) {
        String sql = "insert into product (code, name) values (?,?)";
        try {
            int cnt = 0;
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (Product product : list) {
                preparedStatement.setString(1, product.code);
                preparedStatement.setString(2, product.name);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
       con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @Override
    public void importModel(ArrayList<Model> list) {
        String sql = "insert into model (model, unit_price,product_code) values (?,?,?)";
        try {
            int cnt = 0;
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (Model model : list) {
                preparedStatement.setString(1, model.model);
                preparedStatement.setString(3, model.product_code);
                preparedStatement.setInt(2, model.unit_price);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
          con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @Override
    public void importOrder(ArrayList<Order> list) {
        try {
            int cnt = 0;
            PreparedStatement preparedStatement = con.prepareStatement(
                    "insert into orders (product_model, contract_number, salesman_number, quantity, estimated_delivery_date, lodgement_date) " +
                            "values (?,?,?,?,?,?)");
            for (Order order : list) {
                preparedStatement.setString(1, order.product_model);
                preparedStatement.setString(2, order.contract_number);
                preparedStatement.setString(3, order.salesman_number);
                preparedStatement.setInt(4, order.quantity);
                preparedStatement.setDate(5, order.estimated_delivery_date);
                preparedStatement.setDate(6, order.lodgement_date);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
           con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @Override
    public void importContract(ArrayList<Contract> list) {
        String sql = "insert into contract (number, client_name, supply_area, date) values (?,?,?,?)";
        try {
            int cnt = 0;
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (Contract contract : list) {
                preparedStatement.setString(1, contract.number);
                preparedStatement.setString(2, contract.client_name);
                preparedStatement.setString(3, contract.supply_area);
                preparedStatement.setDate(4, contract.date);
                preparedStatement.addBatch();
                if (++cnt % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            if (cnt % BATCH_SIZE != 0)
                preparedStatement.executeBatch();
            con.commit();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void deleteTable() {
        String sql = "drop table orders;\n" +
                "drop table contract;\n" +
                "drop table client;\n" +
                "drop table salesman;\n" +
                "drop table supply_center;\n" +
                "drop table model;\n" +
                "drop table product;\n";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            try {
                con.rollback();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

    }

    @Override
    public void createTable() {
        String sql = "create table client(\n" +
                "    enterprise_name  varchar primary key,\n" +
                "    country varchar not null,\n" +
                "    industry varchar not null,\n" +
                "    city varchar\n" +
                "\n" +
                ");\n" +
                "create table supply_center(\n" +
                "    area varchar primary key,\n" +
                "    director_firstname varchar not null,\n" +
                "    director_surname varchar not null\n" +
                ");\n" +
                "create table contract(\n" +
                "   number varchar primary key,\n" +
                "   client_name varchar not null constraint contract_fk1 references client(enterprise_name),\n" +
                "   supply_area  varchar not null constraint contract_fk2 references supply_center(area),\n" +
                "   date DATE not null\n" +
                ");\n" +
                "create table product(\n" +
                "    code varchar primary key,\n" +
                "    name varchar not null\n" +
                ");\n" +
                "\n" +
                "create table model(\n" +
                "    model varchar primary key ,\n" +
                "    unit_price integer not null,\n" +
                "    product_code varchar not null constraint model_fk references product(code)\n" +
                ");\n" +
                "create table salesman(\n" +
                "    number varchar(8) primary key,\n" +
                "    firstname varchar not null ,\n" +
                "    surname varchar not null ,\n" +
                "    phone_number varchar(11) not null ,\n" +
                "    gender varchar not null ,\n" +
                "    age integer not null,\n" +
                "    supply_center varchar not null constraint salesman_fk references supply_center(area)\n" +
                ");\n" +
                "\n" +
                "\n" +
                "create table orders(\n" +
                " id       serial  primary key," +
                "    product_model varchar not null constraint orders_fk1 references model(model),\n" +
                "    contract_number varchar not null constraint orders_fk2 references contract(number),\n" +
                "    salesman_number varchar(8) not null constraint orders_fk references salesman(number),\n" +
                "    quantity  integer not null,\n" +

                "    estimated_delivery_date DATE not null ,\n" +
                "    lodgement_date DATE,\n" +
                "    constraint uq unique (product_model,contract_number)\n" +
                ");\n";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
