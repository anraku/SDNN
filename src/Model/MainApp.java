package Model;

import javax.swing.JFrame;

import Control.*;

public class MainApp extends JFrame{
	public SDNN sdnn;
	
	//初期設定
		public MainApp(){
			
			sdnn = new SDNN(360);
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("SDNNテスト");
			
			sdnn.test_dump();
			
		}

		//メインメソッド
		public static void main(String[] args){

			MainApp app = new MainApp();
			
		}
}
