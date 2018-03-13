package mk.ukim.finki.os.synchronization.exam15.march.poker;


import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;



import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;


public class HypophosphoricAcid {
	
	static Semaphore H, P, O, hHere, pHere, ready, done, lock;
	static int countO = 0;

  public static void init() {
	  
	  H = new Semaphore(4);
	  P = new Semaphore(2);
	  O = new Semaphore(6);
	  
	  hHere = new Semaphore(0);
	  pHere = new Semaphore(0);
	  
	  ready = new Semaphore(0);
	  done = new Semaphore(0);
	  lock = new Semaphore(1);
  
  }


  public static class Phosphorus extends TemplateThread {

    public Phosphorus(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
      
    	P.acquire();
    	pHere.release();
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
    	if(countO == 6) {
    		hHere.acquire(4);
    		pHere.acquire(2);
    		ready.release(12);
    		countO = 0;
    	}
    	lock.release();
    	
    	ready.acquire();
    	state.bond();
    	done.release();
    	
    	lock.acquire();
    	countO++;
    	if(countO == 6) {
    		done.acquire(12);
    		state.validate();
    		H.release(4);
    		P.release(2);
    		O.release(6);
    		countO = 0;
    	}
    	lock.release();
    }

  }


  static HypophosphoricAcidState state = new HypophosphoricAcidState();

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
        for (int j = 0; j < state.O_ATOMS; j++) {
          Oxygen o = new Oxygen(numRuns);
          threads.add(o);
        }
        for (int j = 0; j < state.H_ATOMS; j++) {
          Hydrogen h = new Hydrogen(numRuns);
          threads.add(h);
        }

        for (int j = 0; j < state.P_ATOMS; j++) {
          Phosphorus p = new Phosphorus(numRuns);
          threads.add(p);
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
