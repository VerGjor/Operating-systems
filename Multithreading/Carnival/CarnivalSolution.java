package mk.ukim.finki.os.synchronization.exam15.march.poker;

import mk.ukim.finki.os.synchronization.exam15.march.poker.ProblemExecution;
import mk.ukim.finki.os.synchronization.exam15.march.poker.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class CarnivalSolution {

	static Semaphore contestants, onStage, lock, presentationStart;
	static int count, countStage;
	
	public static void init() {
		count = 0;
		countStage = 0;
		contestants = new Semaphore(0);
		presentationStart = new Semaphore(0);
		onStage = new Semaphore(10);
		lock = new Semaphore(1);
	}

	public static class Participant extends TemplateThread {

		public Participant(int numRuns) {
			super(numRuns);
		}

		@Override
		public void execute() throws InterruptedException {
			onStage.acquire();
			state.participantEnter();
			
			lock.acquire();
			countStage++;
			if(countStage == 10) {
				presentationStart.release(10);
			}
			lock.release();
			
			presentationStart.acquire();
			state.present();
			
			lock.acquire();
			countStage--;
			count++;
			
			if(countStage == 0) {
				state.endGroup();
				onStage.release(10);
			}
			
			if(count == 30) {
				state.endCycle();
				contestants.release(30);
				count = 0;
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

	static CarnivalState state = new CarnivalState();

	public static void run() {
		try {
			int numRuns = 15;
			int numThreads = 30;

			HashSet<Thread> threads = new HashSet<Thread>();

			for (int i = 0; i < numThreads; i++) {
				Participant c = new Participant(numRuns);
				threads.add(c);
			}

			init();

			ProblemExecution.start(threads, state);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
