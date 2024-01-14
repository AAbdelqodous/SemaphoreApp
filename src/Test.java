import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Test {
    static class TestWebService{
        private final Semaphore semaphore;
        public TestWebService(int connectAccessLimit){
            this.semaphore = new Semaphore(connectAccessLimit, true);
        }
        public void accessWebService(){
            try{
                // Acquire permit from semaphore
                semaphore.acquire();

                System.out.println(Thread.currentThread().getName() + " is accessing the web service..");

                // Simulating the web service access
                Thread.sleep(2000);

                System.out.println(Thread.currentThread().getName() + " has finished accessing the web service..");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        }
    }

    static class WebServiceAccessWorker implements Runnable{
        private  final TestWebService testWebService;
        public WebServiceAccessWorker(TestWebService testWebService){
            this.testWebService = testWebService;
        }

        @Override
        public void run() {
            testWebService.accessWebService();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestWebService testWebService = new TestWebService(2);

        Thread t1 = new Thread(new WebServiceAccessWorker(testWebService));
        Thread t2 = new Thread(new WebServiceAccessWorker(testWebService));
        Thread t3 = new Thread(new WebServiceAccessWorker(testWebService));

        t1.start();
        t2.start();
        t3.start();
    }

}
