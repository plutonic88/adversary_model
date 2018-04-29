package cyberpsycho;




import java.util.Random;

public class SUQRTreeGeneratorParallel implements Runnable{



	Random rand = new Random();
	

	public Thread t;
	public String threadName;
	public int LIMIT;
	






	public SUQRTreeGeneratorParallel(Thread t, String threadName, int lIMIT) {
		super();
		this.t = t;
		this.threadName = threadName;
		LIMIT = lIMIT;
	}

	@Override
	public void run() {

		

		for(int i=0; i<this.LIMIT; i++)
		{
			System.out.println("This is thread "+ this.threadName +", count "+ i);
		}


		

	}

	public void start () 
	{
		System.out.println("Starting " +  threadName );
		if (t == null) 
		{
			t = new Thread (this, threadName);
			t.start ();
		}
	}

}

