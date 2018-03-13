package mk.ukim.finki.os.synchronization.exam15.march.poker;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

public class Poker {

	static Semaphore players, gameCanBegin, dealer;
	static int countPlayers = 0;
	
  public static void init() {
	  dealer = new Semaphore(1);
	  players = new Semaphore(6);
	  gameCanBegin = new Semaphore(0);
  }

  public static class Player extends TemplateThread {

    public Player(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
    	players.acquire();
    	state.playerSeat();
    	
    	synchronized(state) {
    		countPlayers++;
    		if(countPlayers == 6) {
    			gameCanBegin.release(6);
    		}
    	}
    	
    	gameCanBegin.acquire();
    	dealer.acquire();
    	state.dealCards();
    	dealer.release();
    	state.play();
    	
    	synchronized(state) {
    		countPlayers--;
    		if(countPlayers == 0) {
    			state.endRound();
    			players.release(6);
    		}
    	}
    }

  }

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      run();
    }
  }

  static PokerState state = new PokerState();

  public static void run() {
    try {
      int numRuns = 1;
      int numIterations = 1200;

      HashSet<Thread> threads = new HashSet<Thread>();

      for (int i = 0; i < numIterations; i++) {
        Player c = new Player(numRuns);
        threads.add(c);
      }

      init();

      ProblemExecution.start(threads, state);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
