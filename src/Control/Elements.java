package Control;

import Control.CodePattern;
import java.util.Random;


public class Elements {

	private CodePattern[] elements = null;
	private int length = 0;
	private final int CODE = 5;


	public Elements(int n){
		if(!(elementsInit(n))){
			System.out.println("error elementsInit");
			return;
		}	
	}
	
	public Elements(){
		if(!(elementsInit(CODE))){
			System.out.println("error elementsInit");
			return;
		}	
	}
	
	public boolean elementsInit(int n){
		try{
			elements = new CodePattern [n];
			for(int i=0; i<n; i++){
				elements[i] = new CodePattern(CODE);
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return false;
		}
		length = n;
		return true;
	}

	public boolean createElements(){
		/*入力層の各素子群にランダムなコードパターンを与える
		 * コードパターンは近い素子群同士相関が得られるように
		 * 似たようなパターンになるよう工夫する*/
		final int R = 2;    //コードパターンの変える素子の数

		for(int i=0; i<elements.length; i++){
			if(i == 0){
				Random rnd = new Random();
				/*入力層の素子群の１つめのコードパターンをランダムに作成*/
				for(int c=0; c<CODE; c++){
					int num;
					if((num=rnd.nextInt(2)) == 0){
						num = -1;
					}
					/*i番目の素子に1か-1が入る*/
					elements[i].setCode(c, num);
				}
			}else{
				/*以降ランダムに決めた一つ目のコードパターンを元に残りのコードパターンを決める*/
				/*r個だけ前のコードと違うコードパターンを作成することを繰り返す*/
				elements[i].copyCodePattern(
					elements[i-1]);
				Random place = new Random();
				/*r個の素子をランダムに選び、その素子の符号を反転させる*/
				for(int k=0; k<R; k++){
					int num = place.nextInt(CODE);
					elements[i].changeCode(num);
				}
			}	
		}
		return true;
	}

	public CodePattern getCodePattern(int index){
		return elements[index];
	}
	public CodePattern[] getElements(){
		return elements;
	}

	public void setElements(CodePattern[] cp){
		elements = cp;
	}

	public int getLength(){
		return length;
	}
}
