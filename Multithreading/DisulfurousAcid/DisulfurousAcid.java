package mk.ukim.finki.os.synchronization.exam15.march.poker;


import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class DisulfurousAcid {

	static Semaphore H, S, O, hHere, sHere, ready, done, lock;
	static int countO = 0;
		
	public static void init() {
		H = new Semaphore(2);
		S = new Semaphore(2);
		O = new Semaphore(5);
		
		hHere = new Semaphore(0);
		sHere = new Semaphore(0);
		
		ready = new Semaphore(0);
		done = new Semaphore(0);
		lock = new Semaphore(1);
	
	  }
	
	
	  public static class Sulfur extends TemplateThread {
	
	    public Sulfur(int numRuns) {
	      super(numRuns);
	    }
	
	    @Override
	    public void execute() throws InterruptedException {
	    	S.acquire();
	    	sHere.release();
	    	ready.acquire();
	    	state.bond();
	    	done.release();
	    }
	
	  }
	
	  public static class Hydrogen extends TemplateThread {
	
	    public Hydrogen(int numRuns) {
	      super(numRuns);
	    }
	
	    @Override
	    public void execute() throws InterruptedException {
	    	H.acquire();
	    	hHere.release();
	    	ready.acquire();
	    	state.bond();
	    	done.release();
	    }
	
	  }
	
	  public static class Oxygen extends TemplateThread {
	
	    public Oxygen(int numRuns) {
	      super(numRuns);
	    }
	
	    @Override
	    public void execute() throws InterruptedException {
	      
	    	O.acquire();
	    	
	    	lock.acquire();
	    	countO++;
	    	if(countO == 5) {
	    		hHere.acquire(2);
	    		sHere.acquire(2);
	    		ready.release(9);
	    		countO = 0;
	    	}
	    	lock.release();
	    	
	    	ready.acquire();
	    	state.bond();
    		done.release();
	    	
	    	lock.acquire();
	    	countO++;
	    	if(countO == 5) {    		
	    		done.acquire(9);
	    		state.validate();
	    		H.release(2);
	    		S.release(2);
	    		O.release(5);
	    		countO = 0;	
	    	}
	    	lock.release();
	    }
	
	  }
	
	
	  static DisulfurousAcidState state = new DisulfurousAcidState();
	
	  public static void main(String[] args) {
	    for (int i = 0; i < 10; i++) {
	      run();
	    }
	  }
	
	  public static void run() {
	    try {
	      int numRuns = 1;
	      int numScenarios = 100;
	
	      HashSet<Thread> threads = new HashSet<Thread>();
	
	      for (int i = 0; i < numScenarios; i++) {
	        for (int j = 0; j < 5; j++) {
	          Oxygen o = new Oxygen(numRuns);
	          threads.add(o);
	        }
	        for (int j = 0; j < 2; j++) {
	          Hydrogen h = new Hydrogen(numRuns);
	          Sulfur s = new Sulfur(numRuns);
	          threads.add(s);
	          threads.add(h);
	
	        }
	
	      }
	
	      init();
	
	      ProblemExecution.start(threads, state);
	      System.out.println(new Date().getTime());
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }

}
