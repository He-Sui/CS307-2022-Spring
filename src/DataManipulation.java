import java.sql.Date;
import java.util.ArrayList;

public interface DataManipulation {
    public void importData();

    public void createTable();

    public void deleteTable();

    public void openDatasource();

    public void closeDatasource();


    public void importClient(ArrayList<Client> list);

    public void importSupplyCenter(ArrayList<SupplyCenter> list);

    public void importSalesman(ArrayList<Salesman> list);

    public void importProduct(ArrayList<Product> list);

    public void importModel(ArrayList<Model> list);

    public void importOrder(ArrayList<Order> list);

    public void importContract(ArrayList<Contract> list);

    public String findOrdersSoldBySalesmen(String firstname, String surname);

    public String findOrdersByProductModel(String model);

    public String findOrdersByModelAndContract(String model, String contract_number);

    public String selectDistinctModelContractSalesman(int index);

    public String findNumberOfProductInContractsMoreThanXInOrder(int x);

    public void updateQuantity(String ContractNumber, String model, int newQuantity);

    public void deleteModel(String model);

    class Client {
        public String enterprise_name;
        public String country;
        public String city;
        public String industry;

        public Client(String[] info) {
            enterprise_name = info[1];
            country = info[3];
            if (!info[4].equals("NULL"))
                city = info[4];
            industry = info[5];
        }

        public Client(String enterprise_name, String country, String city, String industry) {
            this.enterprise_name = enterprise_name;
            if (!(city.equals("NULL") || city.equals("null")))
                this.city = city;
            this.country = country;
            this.industry = industry;
        }
    }

    class SupplyCenter {
        public String area;
        public String surname;
        public String firstname;

        public SupplyCenter(String[] info) {
            area = info[2];
            String[] name = info[14].split(" ");
            surname = name[1];
            firstname = name[0];
        }

        public SupplyCenter(String area, String surname, String firstname) {
            this.area = area;
            this.surname = surname;
            this.firstname = firstname;
        }
    }

    class Salesman {
        public String number;
        public String first_name;
        public String surname;
        public String phone_number;
        public String gender;
        public String supply_center;
        int age;

        public Salesman(String[] info) {
            number = info[16];
            String[] name = info[15].split(" ");
            first_name = name[0];
            surname = name[1];
            phone_number = info[19];
            gender = info[17];
            age = Integer.parseInt(info[18]);
            supply_center = info[2];
        }

        public Salesman(String number, String first_name, String surname, String phone_number, String gender, String age, String supply_center) {
            this.number = number;
            this.first_name = first_name;
            this.surname = surname;
            this.phone_number = phone_number;
            this.gender = gender;
            this.supply_center = supply_center;
            this.age = Integer.parseInt(age);
        }
    }

    class Product {
        public String code;
        public String name;

        public Product(String[] info) {
            code = info[6];
            name = info[7];
        }

        public Product(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    class Model {
        public String model;
        public String product_code;
        int unit_price;

        public Model(String[] info) {
            model = info[8];
            product_code = info[6];
            unit_price = Integer.parseInt(info[9]);
        }

        public Model(String model, String price, String product_code) {
            this.model = model;
            this.product_code = product_code;
            unit_price = Integer.parseInt(price);
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

        public Contract(String number, String client_name, String supply_area, String date) {
            this.number = number;
            this.client_name = client_name;
            this.supply_area = supply_area;
            this.date = Date.valueOf(date);
        }
    }

    class Order {
        int id;
        String product_model;
        String contract_number;
        String salesman_number;
        int quantity;
        Date estimated_delivery_date;
        Date lodgement_date;

        public Order(String[] info) {
            product_model = info[8];
            contract_number = info[0];
            salesman_number = info[16];
            quantity = Integer.parseInt(info[10]);
            if (!info[12].equals(""))
                estimated_delivery_date = Date.valueOf(info[12]);
            if (!info[13].equals(""))
                lodgement_date = Date.valueOf(info[13]);
        }

        public Order(String product_model, String contract_number, String salesman_number, String quantity, String estimated_delivery_date, String lodgement_date) {
            this.product_model = product_model;
            this.contract_number = contract_number;
            this.salesman_number = salesman_number;
            this.quantity = Integer.parseInt(quantity);
            this.estimated_delivery_date = Date.valueOf(estimated_delivery_date);
            if (!(lodgement_date.equals("") || lodgement_date.equals("null")))
                this.lodgement_date = Date.valueOf(lodgement_date);

        }

        public Order(String id, String product_model, String contract_number, String salesman_number, String quantity, String estimated_delivery_date, String lodgement_date) {
            this.id = Integer.parseInt(id);
            this.product_model = product_model;
            this.contract_number = contract_number;
            this.salesman_number = salesman_number;
            this.quantity = Integer.parseInt(quantity);
            this.estimated_delivery_date = Date.valueOf(estimated_delivery_date);
            if (!(lodgement_date.equals("") || lodgement_date.equals("null")))
                this.lodgement_date = Date.valueOf(lodgement_date);

        }
    }
}
