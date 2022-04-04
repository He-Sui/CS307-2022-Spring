import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class DatabaseManipulation implements DataManipulation {
    private Connection con = null;
    private ResultSet resultSet;
    private String host = "172.27.144.1";
    private String dbname = "contract";
    private String user = "checker";
    private String pwd = "123456";
    private String port = "5432";

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
    public int addOneMovie(String str) {
        int result = 0;
        String sql = "insert into movies (title, country,year_released,runtime) " +
                "values (?,?,?,?)";
        String[] movieInfo = str.split(";");
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, movieInfo[0]);
            preparedStatement.setString(2, movieInfo[1]);
            preparedStatement.setInt(3, Integer.parseInt(movieInfo[2]));
            preparedStatement.setInt(4, Integer.parseInt(movieInfo[3]));
            System.out.println(preparedStatement.toString());

            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String allContinentNames() {
        StringBuilder sb = new StringBuilder();
        String sql = "select continent from countries group by continent";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append(resultSet.getString("continent")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public String continentsWithCountryCount() {
        StringBuilder sb = new StringBuilder();
        String sql = "select continent, count(*) countryNumber from countries group by continent;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append(resultSet.getString("continent")).append("\t");
                sb.append(resultSet.getString("countryNumber"));
                sb.append(System.lineSeparator());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public String FullInformationOfMoviesRuntime(int min, int max) {
        StringBuilder sb = new StringBuilder();
        String sql = "select m.title,c.country_name country,c.continent ,m.runtime " +
                "from movies m " +
                "join countries c on m.country=c.country_code " +
                "where m.runtime between ? and ? order by runtime;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, min);
            preparedStatement.setInt(2, max);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString("runtime")).append("\t");
                sb.append(String.format("%-18s", resultSet.getString("country")));
                sb.append(resultSet.getString("continent")).append("\t");
                sb.append(resultSet.getString("title")).append("\t");
                sb.append(System.lineSeparator());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public String findMovieById(int id) {
        StringBuilder sb = new StringBuilder();
        String sql = "select m.title, c.country_name, m.year_released, m.runtime from movies m" +
                " join countries c " +
                " on m.country = c.country_code " +
                "where m.movieid = ? ;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append("runtime: ").append(resultSet.getString("runtime")).append("\n");
                sb.append("country_name: ").append(resultSet.getString("country_name")).append("\n");
                sb.append("year_released :").append(resultSet.getString("year_released")).append("\n");
                sb.append("title: ").append(resultSet.getString("title")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public int importData() {
        int result = 0;
        String line;
        Set<String> area = new HashSet<>();
        Set<String> enterprise_name = new HashSet<>();
        Set<String> salesman_number = new HashSet<>();
        Set<String> product_code = new HashSet<>();
        Set<String> model = new HashSet<>();
        Set<String> contract = new HashSet<>();
        ArrayList<Client> client_list = new ArrayList<>();
        ArrayList<Contract> contract_list = new ArrayList<>();
        ArrayList<Supply> supply_list = new ArrayList<>();
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
                    supply_list.add(new Supply(info));
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
            importSupply(supply_list);
            importClient(client_list);
            importSalesman(salesman_list);
            importProduct(product_list);
            importModel(model_list);
            importContract(contract_list);
            importOrder(order_list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importClient(ArrayList<Client> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into Client (enterprise_name, country, industry, city) values (?,?,?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Client client : list) {
                preparedStatement.setString(index++, client.enterprise_name);
                preparedStatement.setString(index++, client.country);
                preparedStatement.setString(index++, client.industry);
                preparedStatement.setString(index++, client.city);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importSupply(ArrayList<Supply> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into Supply (area, director_firstname, director_surname) values (?,?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Supply supply : list) {
                preparedStatement.setString(index++, supply.area);
                preparedStatement.setString(index++, supply.firstname);
                preparedStatement.setString(index++, supply.surname);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importSalesman(ArrayList<Salesman> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into salesman (number, firstname, surname, phone_number, gender, age) values (?,?,?,?,?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Salesman salesman : list) {
                preparedStatement.setString(index++, salesman.number);
                preparedStatement.setString(index++, salesman.first_name);
                preparedStatement.setString(index++, salesman.surname);
                preparedStatement.setString(index++, salesman.phone_number);
                preparedStatement.setString(index++, salesman.gender);
                preparedStatement.setInt(index++, salesman.age);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importProduct(ArrayList<Product> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into product (code, name) values (?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Product product : list) {
                preparedStatement.setString(index++, product.code);
                preparedStatement.setString(index++, product.name);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importModel(ArrayList<Model> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into model (model, product_code) values (?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Model model : list) {
                preparedStatement.setString(index++, model.model);
                preparedStatement.setString(index++, model.product_code);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importOrder(ArrayList<Order> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into orders (product_model, contract_number, salesman_number, quantity, unit_price, estimated_delivery_date, lodgement_date) values (?,?,?,?,?,?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Order order : list) {
                preparedStatement.setString(index++, order.product_model);
                preparedStatement.setString(index++, order.contract_number);
                preparedStatement.setString(index++, order.salesman_number);
                preparedStatement.setInt(index++, order.quantity);
                preparedStatement.setInt(index++, order.unit_price);
                preparedStatement.setDate(index++, order.estimated_delivery_date);
                preparedStatement.setDate(index++, order.lodgement_date);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int importContract(ArrayList<Contract> list) {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        int total = list.size();
        final String sql = "insert into contract (number, client_name, supply_area, date) values (?,?,?,?);";
        for (int i = 0; i < total; ++i)
            sb.append(sql);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sb.toString());
            int index = 1;
            for (Contract contract : list) {
                preparedStatement.setString(index++, contract.number);
                preparedStatement.setString(index++, contract.client_name);
                preparedStatement.setString(index++, contract.supply_area);
                preparedStatement.setDate(index++, contract.date);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int createTable() {
        String sql = "create table client(\n" +
                "    enterprise_name  varchar primary key,\n" +
                "    country varchar not null,\n" +
                "    industry varchar not null,\n" +
                "    city varchar\n" +
                "\n" +
                ");\n" +
                "create table supply(\n" +
                "    area varchar primary key,\n" +
                "    director_firstname varchar not null,\n" +
                "    director_surname varchar not null\n" +
                ");\n" +
                "create table contract(\n" +
                "   number varchar primary key,\n" +
                "   client_name varchar not null constraint contract_fk1 references client(enterprise_name),\n" +
                "   supply_area  varchar not null constraint contract_fk2 references supply(area)              ,\n" +
                "   date DATE not null\n" +
                ");\n" +
                "create table product(\n" +
                "    code varchar primary key,\n" +
                "    name varchar not null\n" +
                ");\n" +
                "\n" +
                "create table model(\n" +
                "    model varchar primary key ,\n" +
                "    product_code varchar not null constraint model_fk references product(code)\n" +
                ");\n" +
                "create table salesman(\n" +
                "    number varchar(8) primary key,\n" +
                "    firstname varchar not null ,\n" +
                "    surname varchar not null ,\n" +
                "    phone_number varchar(11) not null ,\n" +
                "    gender varchar not null ,\n" +
                "    age integer not null\n" +
                ");\n" +
                "\n" +
                "\n" +
                "create table orders(\n" +
                "    product_model varchar not null constraint orders_fk1 references model(model),\n" +
                "    contract_number varchar not null constraint orders_fk2 references contract(number),\n" +
                "    salesman_number varchar(8) not null constraint orders_fk references salesman(number),\n" +
                "    quantity  integer not null,\n" +
                "    unit_price integer not null,\n" +
                "    estimated_delivery_date DATE not null ,\n" +
                "    lodgement_date DATE,\n" +
                "    constraint uq unique (product_model,contract_number),\n" +
                "    constraint orders_pk primary key (product_model,contract_number)\n" +
                ");\n";
        int result = 0;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
