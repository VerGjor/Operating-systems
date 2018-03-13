package mk.ukim.finki.os.synchronization.exam15.march.poker;


import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class Concert {

	static Semaphore groups, bariton, tenor, lock, groupFormed, ready, vocals; 
	static Semaphore bHere, tHere, vHere, done;
	static int countB = 0;
	static int countT = 0;
	
  public static void init() {
	  groups = new Semaphore(0);
	  bariton = new Semaphore(1);
	  tenor = new Semaphore(1);
	  vocals = new Semaphore(1);
	  
	  done = new Semaphore(0);
	  bHere = new Semaphore(0);
	  tHere = new Semaphore(0);
	  vHere = new Semaphore(3);
	  groupFormed = new Semaphore(7);
	  ready = new Semaphore(0);
	  
	  lock = new Semaphore(1);
  }

  public static class Performer extends TemplateThread {

    public Performer(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
    	vocals.acquire();
    	
    	lock.acquire();
    	tHere.acquire(3);
    	bHere.acquire(3);
    	groups.acquire(3);
    	ready.release(3);
    	lock.release();
    	
    	ready.acquire();
    	state.perform();
    	done.release();
    	
    	lock.acquire();
    	done.acquire(3);
    	state.vote();
    	bariton.release(3);
    	tenor.release(3);
    	vocals.release();
    	lock.release();
    	
    	
    }

  }

  public static class Baritone extends TemplateThread {

    public Baritone(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
    	bariton.acquire();
    	bHere.release();
    	tHere.acquire();
    	state.formBackingVocals();
    	groups.release();

    }

  }

  public static class Tenor extends TemplateThread {

    public Tenor(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
    	tenor.acquire();
    	tHere.release();
    	bHere.acquire();
    	state.formBackingVocals();
    	groups.release();
 
    	
    }

  }

  static ConcertState state = new ConcertState();

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
        Tenor t = new Tenor(numRuns);
        Baritone b = new Baritone(numRuns);
        threads.add(t);
        if (i % 3 == 0) {
          Performer p = new Performer(numRuns);
          threads.add(p);
        }
        threads.add(b);
      }

      init();

      ProblemExecution.start(threads, state);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
