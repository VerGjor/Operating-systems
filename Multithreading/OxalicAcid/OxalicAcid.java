package mk.ukim.finki.os.synchronization.exam15.march.poker;


import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class OxalicAcid {

	static Semaphore C, H, O, cHere, hHere, ready, done, lock;
	static int countO;
	
  public static void init() {
	  lock = new Semaphore(1);
	  
	  C = new Semaphore(2);
	  H = new Semaphore(2);
	  O = new Semaphore(4);
	  
	  cHere = new Semaphore(0);
	  hHere = new Semaphore(0);
	//  oHere = new Semaphore(0);
	  
	  ready = new Semaphore(0);
	  //next = new Semaphore(0);
	  done = new Semaphore(0);
	  //allAtoms = new Semaphore(0);

  }


  public static class Carbon extends TemplateThread {

    public Carbon(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
      C.acquire();
      cHere.release();
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
    	if(countO == 4) {
    		hHere.acquire(2);
    		cHere.acquire(2);
    		countO = 0;
    		ready.release(8);
    	}
    	lock.release();
      
    	ready.acquire();
    	state.bond();
    	done.release();
    	
    	lock.acquire();
    	countO++;
    	if(countO == 4) {
    		done.acquire(8);
    		state.validate();
    		O.release(4);
    		C.release(2);
    		H.release(2);
    		countO = 0;
    	}
    	lock.release();
    }

  }


  static OxalicAcidState state = new OxalicAcidState();

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      run();
    }
  }

  public static void run() {
    try {
      int numRuns = 1;
      int numScenarios = 300;

      HashSet<Thread> threads = new HashSet<Thread>();

      for (int i = 0; i < numScenarios; i++) {
        Oxygen o = new Oxygen(numRuns);

        threads.add(o);
        if (i % 2 == 0) {
          Hydrogen h = new Hydrogen(numRuns);
          Carbon c = new Carbon(numRuns);
          threads.add(c);
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
