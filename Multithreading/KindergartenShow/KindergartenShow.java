package mk.ukim.finki.os.synchronization.exam15.march.poker;


import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class KindergartenShow {

	static Semaphore kidsOnStage, actCanStart, groupNumber, lock;
	static int countKids, countCycle;
	 private static int sumPermits = 0;
	  private static int numExecutions = 0;
	  private static int sumQueue = 0;


  public static void init() {
	  countKids = 0;
	  countCycle = 0;
	  lock = new Semaphore(1);
	  groupNumber = new Semaphore(0);
	  kidsOnStage = new Semaphore(6);
	  actCanStart = new Semaphore(0);
  }

  public static class Child extends TemplateThread {

    public Child(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
      
    	kidsOnStage.acquire();
    	state.participantEnter();
    	
    	lock.acquire();
		countKids++;
		if(countKids == 6) {
			actCanStart.release(6);
		}
    	lock.release();
    	
    	actCanStart.acquire();
    	state.present();
    	
    	lock.acquire();
		countKids--;
		countCycle++;
		
		if(countKids == 0) {
			state.endGroup();
			kidsOnStage.release(6);
		}
		
		if(countCycle == 24) {
			state.endCycle();
			groupNumber.release(24);
			countCycle = 0;
		}
    	lock.release();
    	groupNumber.acquire();
    	
    }
  }

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      run();
    }
  }

  static KindergartenShowState state = new KindergartenShowState();

  public static void run() {
    try {
      int numRuns = 24;
      int numIterations = 24;
      numExecutions = 0;
      sumPermits = 0;
      sumQueue = 0;

      HashSet<Thread> threads = new HashSet<Thread>();

      for (int i = 0; i < numIterations; i++) {
        Child c = new Child(numRuns);
        threads.add(c);
      }

      init();

      ProblemExecution.start(threads, state);
      System.out.println(((double) sumPermits) / numExecutions);
      System.out.println(((double) sumQueue) / numExecutions);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
