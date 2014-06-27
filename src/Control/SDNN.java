package Control;

import java.util.Random;
import Control.Elements;
import Control.CodePattern;

public class SDNN {
	
	private Elements inputLayer[] = null;    //入力層
	private CodePattern selectedInput[] = null; //各入力から得られたコードパターン
	private Elements middleLayer[] = null;   //中間層
	private Elements outputLayer = null;   //出力層
	private final int CODE = 200;
	private double RESOlUTION = 1.0;
	
	/*入力層、中間層、出力層全体の初期化を行う*/
	public SDNN(double res,int... n){    //各素子群毎のコードパターンの数
		/*入力層の初期化を行う*/
		try{
			inputLayer = new Elements [n.length];
			for(int i=0; i<n.length; i++){
				inputLayer[i] = new Elements(n[i]);
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return;
		}
		
		for(int i=0; i<n.length; i++){
			if(!(inputLayerInit(i,n[i]))){
				System.out.println("error inputLayerInit");
			}
		}
		/*selectedInputを初期化*/
		selectedInput = new CodePattern [n.length];
		for(int i=0; i<n.length; i++){
			selectedInput[i] = new CodePattern();
		}
		RESOlUTION = res;
	}
	
	public boolean inputLayerInit(int index,int n){
		
		/*入力層の各素子群にランダムなコードパターンを与える
		 * コードパターンは近い素子群同士相関が得られるように
		 * 似たようなパターンになるよう工夫する*/
		Random rnd = new Random();
		/*入力層の素子群の１つめのコードパターンをランダムに作成*/
		for(int i=0; i<inputLayer[index].getCodePattern(0).getLength(); i++){
			int num;
			if((num=rnd.nextInt(2)) == 0){
				num = -1;
			}
			/*i番目の素子に1か-1が入る*/
			inputLayer[index].getCodePattern(0).setCode(i, num);
		}
		/*以降ランダムに決めた一つ目のコードパターンを元に残りのコードパターンを決める*/
		/*r個だけ前のコードと違うコードパターンを作成することを繰り返す*/
		final int R = 2;
		for(int i=1; i<inputLayer[index].getLength(); i++){
			inputLayer[index].getCodePattern(i).copyCodePattern(
				inputLayer[index].getCodePattern(i-1));
			Random place = new Random();
			/*r個の素子をランダムに選び、その素子の符号を反転させる*/
			for(int k=0; k<R; k++){
				int num = place.nextInt(CODE);
				inputLayer[index].getCodePattern(k).changeCode(num);
			}
		}
		return true;
	}
	
	public boolean middleLayerInit(){
		if(inputLayer == null){
			return false;
		}
		//int middleLayerSize = selectedInput.length*(selectedInput.length-1);
		//middleLayer = new CodePattern [middleLayerSize];

		return true;
	}

	public boolean selectedInputIndex(double... ns){
		if(inputLayer == null && selectedInput == null){
			return false;
		}
		if(ns.length != selectedInput.length){
			return false;
		}
		for(int i=0; i<ns.length; i++){
			int index = (int)(ns[i] / RESOlUTION);            //入力値に対するコードパターン
			selectedInput[i] = inputLayer[i].getCodePattern(index);
		}
		return true;
	}

	public void test_dump(){
		if(inputLayer == null){
			System.out.println("Error:inputLayer is not initialized");
			return;
		}
		
		for(int i=0; i<inputLayer.length; i++){
			System.out.println((i+1)+"層目:");
			for(int j=0; j<inputLayer[i].getLength(); j++){
				System.out.println("    "+(j+1)+"番目:");
				System.out.print("        ");
				for(int k=0;k<inputLayer[i].getCodePattern(j).getLength(); k++){
					System.out.print(" "+inputLayer[i].getCodePattern(j).getCode(k));
				}
				System.out.println("");
			}
			System.out.println("");
		}
	}
}
