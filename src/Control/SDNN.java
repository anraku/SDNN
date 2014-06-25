package Control;

import java.util.Random;

public class SDNN {
	
	private CodePattern inputLayer[];  //入力層
	private CodePattern middleLayer[]; //中間層
	private CodePattern outputLayer[]; //出力層
	private final int CODE = 200;      //コードパターンの次元数
	
	
	/*入力層、中間層、出力層全体の初期化を行う*/
	public SDNN(int n){
		inputLayerInit(n);
	}
	
	public boolean inputLayerInit(int n){
		/*入力層の初期化を行う*/
		inputLayer = new CodePattern [n];
		for(int i=0; i<n; i++){
			inputLayer[i] = new CodePattern(200);
		}
		
		/*入力層の各素子群にランダムなコードパターンを与える
		 * コードパターンは近い素子群同士相関が得られるように
		 * 似たようなパターンになるよう工夫する*/
		Random rnd = new Random();
		/*入力層の素子群の１つめのコードパターンをランダムに作成*/
		for(int i=0; i<inputLayer[0].getCodePattern().length; i++){
			int num;
			if((num=rnd.nextInt(2)) == 0){
				num = -1;
			}
			/*i番目の素子に1か-1が入る*/
			inputLayer[0].setCode(i, num);
		}
		/*以降ランダムに決めた一つ目のコードパターンを元に残りのコードパターンを決める*/
		/*r個だけ前のコードと違うコードパターンを作成することを繰り返す*/
		final int R = 2;
		for(int i=1; i<inputLayer.length; i++){
			inputLayer[i].copyCodePattern(inputLayer[i-1].getCodePattern());
			Random place = new Random();
			/*r個の素子をランダムに選び、その素子の符号を反転させる*/
			for(int k=0; k<R; k++){
				int num = place.nextInt(CODE);
				inputLayer[i].changeCode(num);
			}
		}
		return true;
	}
	
	public void test_dump(){
		if(inputLayer == null){
			System.out.println("Error:inputLayer is not initialized");
		}
		
		for(int i=0; i<inputLayer.length; i++){
			System.out.print(i+"番目:");
			for(int j=0;j<inputLayer[i].getCodePattern().length; j++){
				System.out.print(" "+inputLayer[i].getCode(j));
			}
			System.out.println("");
		}
	}
}
