import java.sql.Date;
import java.util.ArrayList;

public interface DataManipulation {
    public int importData();
    public int createTable();
    public void openDatasource();

    public void closeDatasource();

    public int addOneMovie(String str);

    public String allContinentNames();

    public String continentsWithCountryCount();

    public String FullInformationOfMoviesRuntime(int min, int max);

    public String findMovieById(int id);

    public int importClient(ArrayList<Client> list);

    public int importSupply(ArrayList<Supply> list);

    public int importSalesman(ArrayList<Salesman> list);

    public int importProduct(ArrayList<Product> list);

    public int importModel(ArrayList<Model> list);

    public int importOrder(ArrayList<Order> list);

    public int importContract(ArrayList<Contract> list);

    class Client {
        public String enterprise_name;
        public String country;
        public String city;
        public String industry;

        public Client(String[] info) {
            enterprise_name = info[1];
            country = info[3];
            city = info[4];
            industry = info[5];
        }
    }

    class Supply {
        public String area;
        public String surname;
        public String firstname;

        public Supply(String[] info) {
            area = info[2];
            String[] name = info[14].split(" ");
            surname = name[1];
            firstname = name[0];
        }
    }

    class Salesman {
        public String number;
        public String first_name;
        public String surname;
        public String phone_number;
        public String gender;
        int age;

        public Salesman(String[] info) {
            number = info[16];
            String[] name = info[15].split(" ");
            first_name = name[0];
            surname = name[1];
            phone_number = info[19];
            gender = info[17];
            age = Integer.parseInt(info[18]);
        }
    }

    class Product {
        public String code;
        public String name;

        public Product(String[] info) {
            code = info[6];
            name = info[7];
        }
    }

    class Model {
        public String model;
        public String product_code;

        public Model(String[] info) {
            model = info[8];
            product_code = info[6];
        }
    }

    class Contract {
        String number;
        String client_name;
        String supply_area;
        Date date;

        public Contract(String[] info) {
            number = info[0];
            client_name = info[1];
            supply_area = info[2];
            date = Date.valueOf(info[11]);
        }
    }

    class Order {
        String product_model;
        String contract_number;
        String salesman_number;
        int quantity;
        int unit_price;
        Date estimated_delivery_date;
        Date lodgement_date;

        public Order(String[] info) {
            product_model = info[8];
            contract_number = info[0];
            salesman_number = info[16];
            quantity = Integer.parseInt(info[10]);
            unit_price = Integer.parseInt(info[9]);
            estimated_delivery_date = Date.valueOf(info[12]);
            if (!info[13].equals(""))
                lodgement_date = Date.valueOf(info[13]);
        }
    }
}
