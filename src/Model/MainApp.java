package Model;

import javax.swing.JFrame;

import Control.*;

public class MainApp extends JFrame{
	public SDNN sdnn;
	
	//初期設定
	public MainApp(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("SDNNテスト");
		/*最初の引数はアナログ入力値の分解能を浮動小数点に
		 *その後は各層のコードパターンの数を任意の数だけ引数に入れる*/
		int elements[] = {100, 100, 100};
		double res[]   = {0.01, 0.01, 0.01};
		
		sdnn = new SDNN(elements, res); //SDNNを初期化
		sdnn.setTarget(2);				//目標値の設定
		final int COUNT = 100;
		for(int i=0; i<COUNT; i++){
			sdnn.input(0.1, 0.2, 0.3);
		}
		sdnn.test_dump();
		
	}

	//メインメソッド
	public static void main(String[] args){

		MainApp app = new MainApp();
	}
}
