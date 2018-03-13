package mk.ukim.finki.os.synchronization.exam15.march.poker;

import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class MusicBand {


  static MusicBandState state = new MusicBandState();
  
  static Semaphore singers = new Semaphore(2);
  static Semaphore guitarists = new Semaphore(3); 
  
  static Semaphore lock = new Semaphore(1);
  static Semaphore ready = new Semaphore(0);
  static Semaphore sHere = new Semaphore(0);
  static Semaphore bandFormed = new Semaphore(0);
  
  static int countG = 0;

  public static class GuitarPlayer extends TemplateThread {

    public GuitarPlayer(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
    	
    	guitarists.acquire();
    	
    	lock.acquire();
    	countG++;
    	if(countG == 3) {
    		sHere.acquire(2);
    		ready.release(5);
    		countG = 0;
    	}
    	lock.release();
    	
    	ready.acquire();
    	state.play();
    	bandFormed.release();
    	
    	lock.acquire();
    	countG++;
    	if(countG == 3) {
    		bandFormed.acquire(5);
    		state.evaluate();
    		singers.release(2);
    		guitarists.release(3);
    		countG = 0;
    	}
    	lock.release();
    }

  }

  public static class Singer extends TemplateThread {

    public Singer(int numRuns) {
      super(numRuns);
    }

    @Override
    public void execute() throws InterruptedException {
     
    	singers.acquire();
    	sHere.release();
    	ready.acquire();
		state.play();
		bandFormed.release();
		
    }

  }

  public static void init() {

  }

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      run();
    }
  }

  public static void run() {
    try {
      Scanner s = new Scanner(System.in);
      int numRuns = 1;
      int numIterations = 100;
      s.close();

      HashSet<Thread> threads = new HashSet<Thread>();

      for (int i = 0; i < numIterations; i++) {
        Singer singer = new Singer(numRuns);
        threads.add(singer);
        GuitarPlayer gp = new GuitarPlayer(numRuns);
        threads.add(gp);
        gp = new GuitarPlayer(numRuns);
        threads.add(gp);
        singer = new Singer(numRuns);
        threads.add(singer);
        gp = new GuitarPlayer(numRuns);
        threads.add(gp);
      }

      init();

      ProblemExecution.start(threads, state);
      System.out.println(new Date().getTime());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
