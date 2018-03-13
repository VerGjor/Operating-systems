import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scheduler extends Thread{
  public static Random random = new Random();
  static List<Process> scheduled = new ArrayList<>();

  public static void main(String[] args) throws InterruptedException {
    // TODO: kreirajte 100 Process thread-ovi i registrirajte gi
    Process[] process = new Process[100];
    for(Process p : process) {
    	register(p);
    }
    // TODO: kreirajte Scheduler i startuvajte go negovoto pozadinsko izvrsuvanje
    Scheduler s = new Scheduler();
    s.start();
    // TODO: Cekajte 20000ms za Scheduler-ot da zavrsi
    s.join(20000);
    // TODO: ispisete go statusot od izvrsuvanjeto
    if(s.isAlive()) {
    	s.interrupt();
    	System.out.println("Terminated schedulling");
    }
    else {
    	System.out.println("Finished schedulling");
    }
  }

  public static void register(Process process) {
    scheduled.add(process);
  }

  public Process next() {
    if (!scheduled.isEmpty()) {
      return scheduled.remove(0);
    }
    return null;
  }

  public void run() {
    try {
          while (!scheduled.isEmpty()) {
            Thread.sleep(100);
            System.out.print(".");
    
            // TODO: zemete go naredniot proces
            Process p = next();
            
            // TODO: povikajte go negoviot execute() metod
            // TODO: cekajte dodeka ne zavrsi negovoto pozadinsko izvrsuvanje            
            if(p != null) {
            	p.execute();
            	p.join();
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("Done scheduling!");
      }
  }
  



class Process extends Thread{

  public Integer duration;

  public Process() throws InterruptedException {
    this.duration = Scheduler.random.nextInt(1000);
  }


  public void execute() throws InterruptedException {
    System.out.println("Executing[" + this + "]: " + duration);
    // TODO: startuvajte go pozadinskoto izvrsuvanje
    this.start();
    Thread.sleep(this.duration);
  }
}
