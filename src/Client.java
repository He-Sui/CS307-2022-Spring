public class Client {

    public static void main(String[] args) {
        try {
            DataManipulation dm = new DataFactory().createDataManipulation(args[0]);
            dm.openDatasource();
            dm.createTable();
            long begin = System.currentTimeMillis();
            dm.importData();
            long end = System.currentTimeMillis();
            System.out.println(end - begin);
            dm.closeDatasource();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}

