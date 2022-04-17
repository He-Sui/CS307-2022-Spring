import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class HighConcurrencyTest implements Runnable {
    Thread t;
    DatabaseManipulation dbms;
    ArrayList<DataManipulation.Order> list = new ArrayList<>();
    CountDownLatch latch;

    public HighConcurrencyTest(CountDownLatch countDownLatch) {
        latch = countDownLatch;
        dbms = new DatabaseManipulation();
        t = new Thread(this);
    }

    @Override
    public void run() {
        dbms.importOrder(list);
        latch.countDown();
    }
}

class Test {
    public static void main(String[] args) {
        DatabaseManipulation.openDatasource();
        HighConcurrencyTest[] thread = new HighConcurrencyTest[100];
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; ++i) {
            thread[i] = new HighConcurrencyTest(latch);
        }
        int count = 0;
        try {
            BufferedReader bf = new BufferedReader(new FileReader("Test1.txt"));
            bf.readLine();
            String line;
            String[] info;
            while ((line = bf.readLine()) != null) {
                info = line.split(";");
                thread[count++ / 8000].list.add(new DataManipulation.Order(info[0], info[1], info[2], info[3], info[4], info[5]));
                if (count == 800000)
                    break;
            }
            System.out.println("start");
            long start, end;
            start = System.currentTimeMillis();
            for (int i = 0; i < 100; ++i)
                thread[i].t.start();
            latch.await();
            end = System.currentTimeMillis();
            System.out.println(end - start);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}