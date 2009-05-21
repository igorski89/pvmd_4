import javax.swing.JFrame;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		MDSample sample = null;
//		String fileName = "/Users/igorevsukov/Documents/DNU/PVMD/DATA/DATA_4/test_1.txt";
//		try {
//			BufferedReader input = new BufferedReader(new FileReader(fileName));
//			try {
//				String line = input.readLine();
//				if (line == null) throw new Exception("Can't read data: file "+fileName+"is empty");
//				MDObject first_obj = new MDObject(line);
//				sample = new MDSample(first_obj.getParams().length);
//				sample.add(first_obj);
//				while((line = input.readLine()) != null){
//					try {
//						sample.add(new MDObject(line));
//					}catch (Exception e) {
//						System.out.println("can't add object to studySample:" + e.getMessage());
//					}
//				}
//			}
//			finally{
//				input.close();
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		ChoosingInformationalAttributeMethod ciam = new ChoosingInformationalAttributeMethod(sample);
//		double [][] D = ciam.getD();
//		System.out.println("D=");
//		for(int i = 0; i < D.length; i++){
//			for(int j = 0; j < D[i].length; j++)
//				System.out.printf("%3.2f\t",D[i][j]);
//			
//			System.out.println();
//		}
//		System.out.println();
//		
//		Hashtable<Integer, double[][]> Dj = ciam.getDj();
//		Enumeration<Integer> e = Dj.keys();
//		while(e.hasMoreElements()){
//			Integer f = e.nextElement();
//			double [][] dj = Dj.get(f);
//			System.out.printf("D%d=\n",f.intValue());
//			for(int i = 0; i<dj.length; i++){
//				for(int j=0; j<dj[i].length; j++)
//					System.out.printf("%3.2f\t",dj[i][j]);
//
//				System.out.println();
//			}
//			System.out.println();
//		}
//		
//		int [] sorted = ciam.getSortedAttributes();
//		for(int i = 0; i < sorted.length; i++) System.out.printf("%d\t", sorted[i]);
		
//		try {
//			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//		} catch (Exception e) {
//			e.printStackTrace();
//   	}
		
    	MainFrame mainWindow = new MainFrame();
    	mainWindow.setLocation(100, 100);
    	mainWindow.setSize(1000, 600);
    	mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainWindow.setTitle("Main Window");
    	mainWindow.setVisible(true);
    	
//		Integer a = new Integer(3);
//		System.out.println(a);
//		a = new Integer(5);
//		System.out.println(a);
	}

}
