package book.chapter05.p5_4_2;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;


// TODO: 2020-02-20 这是个假的例子，因为根本没有使用到curator-framework
// TODO: 2020-02-22 这不是个假例子，这只是一个生成订单号的例子，支持按照时间戳来生成订单号是不行的。
public class Recipes_NoLock {

	public static void main(String[] args) throws Exception {

		final CountDownLatch down = new CountDownLatch(1);
		for(int i = 0; i < 10; i++){
			new Thread(new Runnable() {
				public void run() {
					try {
						down.await();
					} catch ( Exception e ) {
					}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.err.println("生成的订单号是 : "+orderNo);
				}
			}).start();
		}
		down.countDown();
	}
}