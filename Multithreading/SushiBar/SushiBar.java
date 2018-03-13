package mk.ukim.finki.os.synchronization.exam15.march.poker;

import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class SushiBar {
 
	static Semaphore visiters, callWaiter;
	static int countV = 0;
	
  public static void init() {
	  visiters = new Semaphore(6);
	  callWaiter = new Semaphore(0);
  }


  public static class Customer extends TemplateThread {

    public Customer(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
    	visiters.acquire();
    	state.customerSeat();
    	
    	synchronized(state) {
    		countV++;
    		if(countV == 6) {
    			state.callWaiter();
    			callWaiter.release(6);
    		}
    	}
   
    	callWaiter.acquire();
    	state.customerEat();
    	
    	synchronized(state) {
    		countV--;
    		if(countV == 0) {
    			state.eatingDone();
    			visiters.release(6);
    		}
    	}
    	
    }

  }

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      run();
    }
  }

  static SushiBarState state = new SushiBarState();

  public static void run() {
    try {
      int numRuns = 1;
      int numIterations = 1200;

      HashSet<Thread> threads = new HashSet<Thread>();

      for (int i = 0; i < numIterations; i++) {
        Customer c = new Customer(numRuns);
        threads.add(c);
      }

      init();

      ProblemExecution.start(threads, state);
      // System.out.println(new Date().getTime());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
