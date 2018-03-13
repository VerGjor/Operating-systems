public class TwoThreads {
	
	public static class ThreadAB implements Runnable{
		
		String[] a, b;
		
		public ThreadAB(String[] a, String[] b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(String s : a) {
				System.out.println(s);
			}
			
			for(String s : b) {
				System.out.println(s);
			}
//			for(char letter = 'a'; letter <= 'z'; letter++) {
//				System.out.println(letter);
//			}
//			
//			for(int i = 1; i<=26;i++) {
//				System.out.println(i);
//			}
			
		}
		
	}
	
//	public static class Thread1 extends Thread {
//		public void run() {
//			System.out.println("A");
//			System.out.println("B");
//		}
//	}
//
//	public static class Thread2 extends Thread {
//		public void run() {
//			System.out.println("1");
//			System.out.println("2");
//		}
//	}

	public static void main(String[] args) {
		String[] A = {"A","B"};
		String[] B = {"1","2"};
		ThreadAB t = new ThreadAB(A,B);
//		ThreadAB t = new ThreadAB();
		new Thread(t).start();
	}

}
