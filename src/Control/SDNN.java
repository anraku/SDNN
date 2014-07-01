package Control;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;



import Control.Elements;
import Control.CodePattern;

public class SDNN {
	
	private Elements inputLayer[] = null;       //入力層
	private Elements shuffledLayer[] = null;    //入力層をシャフルしたもの
	private int[] selectedIndex = null; 		//入力層のコードパターンの要素を管理
	private Elements middleLayer = null;        //中間層
	private Elements outputLayer = null;        //出力層
	
	/*入力層、中間層、出力層全体の初期化を行う*/
	public SDNN(int[] n,double[] res){    //各素子群毎のコードパターンの数
		/*入力層の初期化を行う*/
		try{
			inputLayer = new Elements [n.length];
			for(int i=0; i<n.length; i++){
				inputLayer[i] = new Elements(n[i],res[i]);
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return;
		}
		//inputLayerの初期化
		for(int i=0; i<n.length; i++){
			inputLayerInit(i,n[i]);
		}

		shuffleInputLayer();
		middleLayerInit();
	}
	
	/*入力層の初期化を行う*/
	public void inputLayerInit(int index,int n){
		/*入力層にある各素子群のコードパターンを作成する*/
		inputLayer[index].createElements();		
		
	}
	
	/*入力層をシャフルした結果を別の配列に格納する*/
	public void shuffleInputLayer(){
		if(inputLayer == null){
			return;
		}
		/*shuffleLayerの初期化*/
		try{
			shuffledLayer = new Elements [inputLayer.length];
			for(int i=0; i<inputLayer.length; i++){
				shuffledLayer[i] = new Elements(inputLayer[i].getLength());
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return;
		}
		/*入力層の各素子群のコードパターンをシャフル*/
		for(int i=0; i<inputLayer.length; i++){
			List<CodePattern> list = Arrays.asList(inputLayer[i].getElements());

			// リストの並びをシャッフルします。
    		Collections.shuffle(list);
    		
    		
    		// listから配列へ戻します。
    		/*CodePattern[] cptmp =(CodePattern[])list.toArray(new CodePattern[list.size()]);
    		*/
    		CodePattern[] cptmp = new CodePattern [list.size()];
    		for(int j=0; j<list.size(); j++){
    			cptmp[j] = new CodePattern(Elements.getDimension());
    			cptmp[j] = (CodePattern)list.get(j);
    		}
    		shuffledLayer[i].setElements(cptmp);
		}

		System.out.println("shuffle complete");
	}

	/*中間層の初期化を行う*/
	public boolean middleLayerInit(){
		if(inputLayer == null){
			return false;
		}
		try{
			int middleLayerSize = inputLayer.length*inputLayer.length;
			middleLayer = new Elements (middleLayerSize);
			System.out.println("middleLayerInit complete");
		}catch(NullPointerException e){
			System.out.println("middleLayerInit error");
			System.out.println(e.getMessage());
			return false;
		}
		
		/*入力層のコードパターンをシャフルした修飾パターンで不感化させる
		不感化させたコードパターンはmiddleLayerに格納していく*/
		for(int i=0; i<inputLayer.length; i++){

			for(int j=0; j<inputLayer.length; j++){
				/*修飾される方のコードパターン*/
				CodePattern outputPattern = 
					inputLayer[i].getCodePattern(j);
				
				for(int k=0; k<inputLayer.length; k++){
					if(i == k){
						continue;
					}
					/*修飾パターン*/
					CodePattern modPattern = 
						shuffledLayer[k].getCodePattern(j);
					/*コードパターンを不感化した結果を返す*/
					CodePattern desPattern = 
						neuronDesensitise(outputPattern,modPattern);
					middleLayer.setCodePattern(i*inputLayer.length+j,desPattern);
				}	
			}
		}

	}

	/*入力値を受け取り、中間層を生成及び出力層から出力値を求める*/
	public void input(double... input){
		/*入力値からそれに対応する各層のコードパターンを選択*/
		selectedIndex = selectedInputIndex(input);
		
		if(selectedIndex == null){
			return;
		}
		
	}

	/*入力値に対応するコードパターンを各素子群から選ぶ*/
	public int[] selectedInputIndex(double... ns){
		if(inputLayer == null){
			return null;
		}
		if(ns.length != inputLayer.length){
			return null;
		}
		int selected[] = new int [inputLayer.length];
		for(int i=0; i<ns.length; i++){
			int index = (int)(ns[i] / inputLayer[i].getResolution());  //入力値に対するコードパターン
			selected[i] = index-1;
		}
		return selected;
	}

	public CodePattern neuronDesensitise(CodePattern out,CodePattern mod){
		CodePattern result = new CodePattern (out.getLength());
		/*各素子について不感化していく*/
		for(int i=0; i<out.getLength(); i++){
			if(mod.getCode(i) == -1){
				result.setCode(i,0);
			}else{
				result.setCode(i,out.getCode(i));
			}
		}
		return result;
	}
	/*テスト出力用メソッド*/
	public void test_dump(){
		/*入力層の初期化に失敗した時は終了*/
		if(inputLayer == null){
			System.out.println("Error:inputLayer is not initialized");
			return;
		}
		
		/*入力層の各素子群の値を出力*/
		System.out.println("入力層出力");
		for(int i=0; i<inputLayer.length; i++){
			System.out.println((i+1)+"層目:");
			for(int j=0; j<inputLayer[i].getLength(); j++){
				System.out.println("    "+(j+1)+"番目:");
				System.out.print("        ");
				for(int k=0;k<inputLayer[i].getCodePattern(j).getLength(); k++){
					System.out.print(" "+inputLayer[i].getCodePattern(j).getCode(k));
				}
				System.out.print("    ");
				for(int k=0;k<shuffledLayer[i].getCodePattern(j).getLength(); k++){
					System.out.print(" "+shuffledLayer[i].getCodePattern(j).getCode(k));
				}
				System.out.println("");
			}
		}

		/*入力層をシャフルした結果を出力*/
		System.out.println("シャフルした結果を出力");
		for(int i=0; i<shuffledLayer.length; i++){
			System.out.println((i+1)+"層目:");
			for(int j=0; j<shuffledLayer[i].getLength(); j++){
				System.out.println("    "+(j+1)+"番目:");
				System.out.print("        ");
				for(int k=0;k<shuffledLayer[i].getCodePattern(j).getLength(); k++){
					System.out.print(" "+shuffledLayer[i].getCodePattern(j).getCode(k));
				}
				System.out.println("");
			}
		}

		/*中間層を出力*/
		System.out.println("中間層を出力");
		for(int i=0; i<middleLayer.getLength(); i++){
				System.out.println((i+1)+"番目:");
				System.out.print("    ");
				for(int j=0;j<middleLayer.getCodePattern(i).getLength(); j++){
					System.out.print(" "+middleLayer.getCodePattern(i).getCode(j));
				}
				System.out.println("");
		}
	}
}