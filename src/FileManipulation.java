import java.io.*;
import java.util.*;

public class FileManipulation implements DataManipulation {
    private ArrayList<Client> clientList = new ArrayList<>();
    private ArrayList<Contract> contractList = new ArrayList<>();
    private ArrayList<Model> modelList = new ArrayList<>();
    private ArrayList<Order> orderList = new ArrayList<>();
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<Salesman> salesmanList = new ArrayList<>();
    private ArrayList<SupplyCenter> supplyCenterList = new ArrayList<>();
    private Set<String> areaSet = new HashSet<>();
    private Set<String> enterpriseNameSet = new HashSet<>();
    private Set<String> salesmanNumberSet = new HashSet<>();
    private Set<String> productCodeSet = new HashSet<>();
    private Set<String> modelSet = new HashSet<>();
    private Set<String> contractSet = new HashSet<>();
    private boolean clientFlag = false;
    private boolean contractFlag = false;
    private boolean modelFlag = false;
    private boolean orderFlag = false;
    private boolean productFlag = false;
    private boolean salesmanFlag = false;
    private boolean supplyCenterFlag = false;

    @Override
    public void openDatasource() {
        createTable();
        String line;
        try {
            BufferedReader bf;
            String[] info;
            bf = new BufferedReader(new FileReader("client.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                clientList.add(new Client(info[0], info[1], info[2], info[3]));
                enterpriseNameSet.add(info[0]);
            }
            bf = new BufferedReader(new FileReader("contract.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                contractList.add(new Contract(info[0], info[1], info[2], info[3]));
                contractSet.add(info[0]);
            }
            bf = new BufferedReader(new FileReader("supply_center.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                supplyCenterList.add(new SupplyCenter(info[0], info[1], info[2]));
                areaSet.add(info[0]);
            }
            bf = new BufferedReader(new FileReader("product.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                productList.add(new Product(info[0], info[1]));
                productCodeSet.add(info[0]);
            }
            bf = new BufferedReader(new FileReader("model.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                modelList.add(new Model(info[0], info[1], info[2]));
                modelSet.add(info[0]);
            }
            bf = new BufferedReader(new FileReader("salesman.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                salesmanList.add(new Salesman(info[0], info[1], info[2], info[3], info[4], info[5], info[6]));
                salesmanNumberSet.add(info[0]);
            }
            bf = new BufferedReader(new FileReader("orders.txt"));
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                orderList.add(new Order(info[0], info[1], info[2], info[3], info[4], info[5], info[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ArrayList<Client> importClient_list = new ArrayList<>();
        ArrayList<Contract> importContract_list = new ArrayList<>();
        ArrayList<SupplyCenter> importSupply_list = new ArrayList<>();
        ArrayList<Salesman> importSalesman_list = new ArrayList<>();
        ArrayList<Product> importProduct_list = new ArrayList<>();
        ArrayList<Model> importModel_list = new ArrayList<>();
        ArrayList<Order> importOrder_list = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("contract_info.csv"))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] info = line.split(",");
                if (!area.contains(info[2])) {
                    area.add(info[2]);
                    String[] name = info[14].split(" ");
                    importSupply_list.add(new SupplyCenter(info[2], name[1], name[0]));
                }
                if (!enterprise_name.contains(info[1])) {
                    enterprise_name.add(info[1]);
                    importClient_list.add(new Client(info[1], info[3], info[4], info[5]));
                }
                if (!salesman_number.contains(info[16])) {
                    salesman_number.add(info[16]);
                    String[] name = info[15].split(" ");
                    importSalesman_list.add(new Salesman(info[16], name[0], name[1], info[19], info[17], info[18], info[2]));
                }
                if (!product_code.contains(info[6])) {
                    product_code.add(info[6]);
                    importProduct_list.add(new Product(info[6], info[7]));
                }
                if (!model.contains(info[8])) {
                    model.add(info[8]);
                    importModel_list.add(new Model(info[8], info[9], info[6]));
                }
                if (!contract.contains(info[0])) {
                    contract.add(info[0]);
                    importContract_list.add(new Contract(info[0], info[1], info[2], info[11]));
                }
                importOrder_list.add(new Order(info[8], info[0], info[16], info[10], info[12], info[13]));
            }
            long start, end;
            start = System.currentTimeMillis();
            importSupplyCenter(importSupply_list);
            importClient(importClient_list);
            importSalesman(importSalesman_list);
            importProduct(importProduct_list);
            importModel(importModel_list);
            importContract(importContract_list);
            importOrder(importOrder_list);
            commit();
            end = System.currentTimeMillis();
            System.out.println(end - start);
        } catch (IOException e) {
            System.err.println("Failed to import data from file.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void createTable() {
        try {
            File client = new File("client.txt");
            if (!client.exists()) {
                client.createNewFile();
                FileWriter fw = new FileWriter(client);
                fw.write("enterprise_name;country;city;industry\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File contract = new File("contract.txt");
            if (!contract.exists()) {
                contract.createNewFile();
                FileWriter fw = new FileWriter(contract);
                fw.write("number;client_name;supply_area;date\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File supply_center = new File("supply_center.txt");
            if (!supply_center.exists()) {
                supply_center.createNewFile();
                FileWriter fw = new FileWriter(supply_center);
                fw.write("area;director_firstname;director_surname\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File product = new File("product.txt");
            if (!product.exists()) {
                product.createNewFile();
                FileWriter fw = new FileWriter(product);
                fw.write("code;name\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File model = new File("model.txt");
            if (!model.exists()) {
                model.createNewFile();
                FileWriter fw = new FileWriter(model);
                fw.write("model;unity_price;product_code\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File salesman = new File("salesman.txt");
            if (!salesman.exists()) {
                salesman.createNewFile();
                FileWriter fw = new FileWriter(salesman);
                fw.write("number;firstname;surname;phone_number;gender;age;supply_center\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File orders = new File("orders.txt");
            if (!orders.exists()) {
                orders.createNewFile();
                FileWriter fw = new FileWriter(orders);
                fw.write("id;product_model;contract_number;salesman_number;quantity;estimated_delivery_date;lodgement_date\n");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTable() {
        File client = new File("client.txt");
        client.delete();
        File contract = new File("contract.txt");
        contract.delete();
        File supply_center = new File("supply_center.txt");
        supply_center.delete();
        File product = new File("product.txt");
        product.delete();
        File model = new File("model.txt");
        model.delete();
        File salesman = new File("salesman.txt");
        salesman.delete();
        File orders = new File("orders.txt");
        orders.delete();
    }

    public void commit() {
        BufferedWriter bw;
        if (clientFlag)
            try {
                bw = new BufferedWriter(new FileWriter("client.txt", false));
                bw.write("enterprise_name;country;city;industry\n");

                for (Client client : clientList)
                    bw.write(String.format("%s;%s;%s;%s\n", client.enterprise_name, client.country, client.city, client.industry));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (contractFlag)
            try {
                bw = new BufferedWriter(new FileWriter("contract.txt", false));
                bw.write("number;client_name;supply_area;date\n");
                for (Contract contract : contractList)
                    bw.write(String.format("%s;%s;%s;%s\n", contract.number, contract.client_name, contract.supply_area, contract.date));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (supplyCenterFlag)
            try {
                bw = new BufferedWriter(new FileWriter("supply_center.txt", false));
                bw.write("area;director_firstname;director_surname\n");
                for (SupplyCenter supplyCenter : supplyCenterList)
                    bw.write(String.format("%s;%s;%s\n", supplyCenter.area, supplyCenter.firstname, supplyCenter.surname));
                bw.close();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        if (productFlag)
            try {
                bw = new BufferedWriter(new FileWriter("product.txt", false));
                bw.write("code;name\n");
                for (Product product : productList)
                    bw.write(String.format("%s;%s\n", product.code, product.name));
                bw.close();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        if (modelFlag)
            try {
                bw = new BufferedWriter(new FileWriter("model.txt", false));
                bw.write("model;unity_price;product_code\n");
                for (Model model : modelList)
                    bw.write(String.format("%s;%d;%s\n", model.model, model.unit_price, model.product_code));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (salesmanFlag)
            try {
                bw = new BufferedWriter(new FileWriter("salesman.txt", false));
                bw.write("number;firstname;surname;phone_number;gender;age;supply_center\n");
                for (Salesman salesman : salesmanList)
                    bw.write(String.format("%s;%s;%s;%s;%s;%d;%s\n", salesman.number, salesman.first_name, salesman.surname, salesman.phone_number, salesman.gender, salesman.age, salesman.supply_center));
                bw.close();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        if (orderFlag)
            try {
                bw = new BufferedWriter(new FileWriter("orders.txt", false));
                bw.write("id;product_model;contract_number;salesman_number;quantity;estimated_delivery_date;lodgement_date\n");
                for (Order ord : orderList)
                    bw.write(String.format("%s;%s;%s;%s;%d;%s;%s\n", ord.id, ord.product_model, ord.contract_number, ord.salesman_number, ord.quantity, ord.estimated_delivery_date, ord.lodgement_date));
                bw.close();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        clientFlag = salesmanFlag = supplyCenterFlag = contractFlag = modelFlag = orderFlag = productFlag = false;
    }

    @Override
    public void closeDatasource() {
        commit();
    }


    @Override
    public String findOrdersSoldBySalesmen(String firstname, String surname) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> salesman_number = new ArrayList<>();
        for (Salesman salesman : salesmanList) {
            if (salesman.first_name.equals(firstname) && salesman.surname.equals(surname))
                salesman_number.add(salesman.number);
        }
        for (Order order : orderList)
            for (String number : salesman_number)
                if (order.salesman_number.equals(number))
                    sb.append(order.id).append('\t').append(order.product_model).append('\t').append(order.contract_number).append('\t').append(order.salesman_number).append('\t').append(order.quantity).append('\t').append(order.estimated_delivery_date).append('\t').append(order.lodgement_date).append('\n');
        return sb.toString();
    }

    @Override
    public String findOrdersByModelAndContract(String model, String contract_number) {
        StringBuilder sb = new StringBuilder();
        for (Order order : orderList) {
            if (model.equals(order.product_model) && contract_number.equals(order.contract_number))
                sb.append(order.id).append('\t').append(order.product_model).append('\t').append(order.contract_number).append('\t').append(order.salesman_number).append('\t').append(order.quantity).append('\t').append(order.estimated_delivery_date).append('\t').append(order.lodgement_date).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String findOrdersByProductModel(String model) {
        StringBuilder sb = new StringBuilder();
        for (Order order : orderList) {
            if (order.product_model.equals(model))
                sb.append(order.id).append('\t').append(order.contract_number).append('\t').append(order.salesman_number).append('\t').append(order.quantity).append('\t').append(order.estimated_delivery_date).append('\t').append(order.lodgement_date).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String findNumberOfProductInContractsMoreThanXInOrder(int x) {
        class Pair implements Comparable<Pair> {
            String product;
            int count;

            public Pair(String product, int count) {
                this.product = product;
                this.count = count;
            }

            @Override
            public int compareTo(Pair o) {
                return count - o.count;
            }
        }
        HashMap<String, String> model_product = new HashMap<>();
        HashMap<String, Integer> product_index = new HashMap<>();
        HashMap<String, String> code_name = new HashMap<>();
        Pair[] count = new Pair[productList.size()];
        for (Model model : modelList)
            model_product.put(model.model, model.product_code);
        for (int i = 0; i < productList.size(); ++i) {
            code_name.put(productList.get(i).code, productList.get(i).name);
            product_index.put(productList.get(i).name, i);
            count[i] = new Pair(productList.get(i).name, 0);
        }
        for (Order order : orderList) {
            String code = model_product.get(order.product_model);
            String name = code_name.get(code);
            count[product_index.get(name)].count++;
        }
        Arrays.sort(count);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < productList.size(); ++i)
            if (count[i].count > x)
                sb.append(count[i].product).append('\t').append(count[i].count).append('\n');
        return sb.toString();
    }

    @Override
    public String selectDistinctModelContractSalesman(int index) {
        if (index > 3 || index < 1)
            return null;
        String line;
        StringBuilder sb = new StringBuilder();
        Set<String> list = new HashSet<>();
        for (Order order : orderList) {
            switch (index) {
                case 1:
                    if (!list.contains(order.product_model)) {
                        list.add(order.product_model);
                        sb.append(order.product_model).append('\n');
                    }
                    break;
                case 2:
                    if (!list.contains(order.contract_number)) {
                        list.add(order.contract_number);
                        sb.append(order.contract_number).append('\n');
                    }
                    break;
                case 3:
                    if (!list.contains(order.salesman_number)) {
                        list.add(order.salesman_number);
                        sb.append(order.salesman_number).append('\n');
                    }
            }
        }
        return sb.toString();
    }

    @Override
    public void updateQuantity(String contractNumber, String model, int newQuantity) {
        for (Order order : orderList) {
            if (order.contract_number.equals(contractNumber) && order.product_model.equals(model))
                order.quantity = newQuantity;
            orderFlag = true;
        }
    }

    @Override
    public void deleteModel(String model) {
        orderFlag = true;
        orderList.removeIf(order -> order.product_model.equals(model));
    }

    @Override
    public void importClient(ArrayList<Client> list) {
        for (Client client : list)
            if (!enterpriseNameSet.contains(client.enterprise_name)) {
                clientList.add(client);
                enterpriseNameSet.add(client.enterprise_name);
            }
        clientFlag = true;
    }

    @Override
    public void importSupplyCenter(ArrayList<SupplyCenter> list) {

        for (SupplyCenter supplyCenter : list)
            if (!areaSet.contains(supplyCenter.area)) {
                supplyCenterList.add(supplyCenter);
                areaSet.add(supplyCenter.area);
            }
        supplyCenterFlag = true;
    }


    @Override
    public void importSalesman(ArrayList<Salesman> list) {
        for (Salesman salesman : list)
            if (!salesmanNumberSet.contains(salesman.number)) {
                salesmanList.add(salesman);
                salesmanNumberSet.add(salesman.number);
            }
        salesmanFlag = true;
    }


    @Override
    public void importProduct(ArrayList<Product> list) {

        for (Product pro : list)
            if (!productCodeSet.contains(pro.code)) {
                productList.add(pro);
                productCodeSet.add(pro.code);
            }
        productFlag = true;
    }


    @Override
    public void importModel(ArrayList<Model> list) {

        for (Model model : list)
            if (!modelSet.contains(model.model)) {
                modelList.add(model);
                modelSet.add(model.model);
            }
        modelFlag = true;
    }

    @Override
    public void importOrder(ArrayList<Order> list) {

        for (Order ord : list) {
            if (orderList.isEmpty())
                ord.id = 1;
            else
                ord.id = orderList.get(orderList.size() - 1).id + 1;
            orderList.add(ord);
        }
        orderFlag = true;
    }


    @Override
    public void importContract(ArrayList<Contract> list) {

        for (Contract contract : list)
            if (!contractSet.contains(contract.number)) {
                contractList.add(contract);
                contractSet.add(contract.number);
            }
        contractFlag = true;
    }

    class FullInformation {
        int runTime;
        String information;

        FullInformation(int runTime, String information) {
            this.runTime = runTime;
            this.information = information;
        }
    }
}
