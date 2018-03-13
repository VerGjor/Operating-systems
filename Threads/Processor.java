import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Processor extends Thread{

  public static Random random = new Random();
  static List<EventGenerator> scheduled = new ArrayList<>();
  static Semaphore generator = new Semaphore(5);
  static Semaphore newProcess = new Semaphore(0);
  
  public static void main(String[] args) throws InterruptedException {
    // TODO: kreirajte Processor i startuvajte go negovoto pozadinsko izvrsuvanje
	Processor p = new Processor();
	

    for (int i = 0; i < 100; i++) {
      EventGenerator eventGenerator = new EventGenerator();
      register(eventGenerator);
      //TODO: startuvajte go eventGenerator-ot
      eventGenerator.start();
      
    }

    p.start();
    // TODO: Cekajte 20000ms za Processor-ot da zavrsi
    p.join(20000);
    // TODO: ispisete go statusot od izvrsuvanjeto
    if(p.isAlive()) {
    	p.interrupt();
    	System.out.println("Terminated schedulling");
    }
    else {
    	System.out.println("Finished schedulling");
    }
  }

  public static void register(EventGenerator generator) {
    scheduled.add(generator);
  }

  /**
   * Ne smee da bide izvrsuva paralelno so generate() metodot
   */
  public static void process() {
    System.out.println("processing event");
  }


  public void run() {

    while (!scheduled.isEmpty()) {
      // TODO: cekanje  na nov generiran event
    	try {
			newProcess.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
      // TODO: povikajte go negoviot process() metod
      process();
    }

    System.out.println("Done scheduling!");
  }
}


class EventGenerator extends Thread{

  public Integer duration;

  public EventGenerator() throws InterruptedException {
    this.duration = Processor.random.nextInt(1000);
  }

  @Override
  public void run() {
	  
	  try { 
		  	Thread.sleep(this.duration);
			Processor.generator.acquire();
			generate();
		  	Processor.newProcess.release();
		  	Processor.generator.release();
		  	Processor.scheduled.remove(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	
  }

  /**
   * Ne smee da bide povikan paralelno kaj poveke od 5 generatori
   */
  
  public static synchronized void generate() {
    System.out.println("Generating event: ");
  }
}
