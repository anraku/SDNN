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
		int elements[] = {3, 3, 3};
		double res[]   = {1.0, 1.0, 1.0};
		
		sdnn = new SDNN(elements, res);
		sdnn.input(1.0, 2.0, 3.0);
		sdnn.test_dump();
		
	}

	//メインメソッド
	public static void main(String[] args){

		MainApp app = new MainApp();
	}
}
