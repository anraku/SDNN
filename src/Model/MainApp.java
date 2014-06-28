package Model;

import javax.swing.JFrame;

import Control.*;

public class MainApp extends JFrame{
	public SDNN sdnn;
	
	//初期設定
		public MainApp(){
			
			/*最初の引数はアナログ入力値の分解能を浮動小数点に
			 *その後は各層のコードパターンの数を任意の数だけ引数に入れる*/
			sdnn = new SDNN(1.0,3);
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("SDNNテスト");
			
			sdnn.test_dump();
			
		}

		//メインメソッド
		public static void main(String[] args){

			MainApp app = new MainApp();
		}
}
