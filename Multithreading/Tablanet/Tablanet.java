package mk.ukim.finki.os.synchronization.exam15.march.poker;

import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class Tablanet {

	static Semaphore playersSeated,contestants, tableRoom, lock, start, playersDone;
	static int cycle;

  public static void init() {
	  cycle = 0;
	  playersSeated = new Semaphore(0);
	  playersDone = new Semaphore(0);
	  start = new Semaphore(0);
	  contestants = new Semaphore(0);
	  tableRoom = new Semaphore(4);
	  lock = new Semaphore(1);
  }

  public static class Dealer extends TemplateThread {

    public Dealer(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {

    	playersSeated.acquire(4);
    	state.dealCards();
    	start.release(4);
    	playersDone.acquire(4);
    	state.nextGroup();
    	tableRoom.release(4);
      
    }
  }

  public static class Player extends TemplateThread {

    public Player(int numRuns) {
      super(numRuns);
    }


    @Override
    public void execute() throws InterruptedException {
      
    	tableRoom.acquire();
    	state.playerSeat();
    	playersSeated.release();
    	start.acquire();
    	state.play();
    	playersDone.release();
    	lock.acquire();
    	cycle++;
    	if(cycle == 20) {
    		state.endCycle();
    		contestants.release(20);
    		cycle = 0;
    	}
    	lock.release();
    	
    	contestants.acquire();
    }

  }

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      run();
    }
  }

  static TablanetState state = new TablanetState();

  public static void run() {
    try {
      int numCycles = 10;
      int numIterations = 20;

      HashSet<Thread> threads = new HashSet<Thread>();

      Dealer d = new Dealer(50);
      threads.add(d);
      for (int i = 0; i < numIterations; i++) {
        Player c = new Player(numCycles);
        threads.add(c);
      }

      init();

      ProblemExecution.start(threads, state);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
