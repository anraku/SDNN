package Control;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;



import Control.Elements;
import Control.CodePattern;

public class SDNN {
	
	private Elements inputLayer[] = null;       //入力層
	private Elements shuffledLayer[] = null;    //入力層をシャフルしたもの
	private Elements selectedInput = null;      //各入力から得られたコードパターン
	private Elements middleLayer[] = null;      //中間層
	private Elements outputLayer = null;        //出力層
	
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
		//inputLayerの初期化
		for(int i=0; i<n.length; i++){
			inputLayerInit(i,n[i]);
		}
		/*selectedInputを初期化*/
		selectedInput = new Elements();
		RESOlUTION = res;
	}
	
	public void inputLayerInit(int index,int n){
		/*入力層にある各素子群のコードパターンを作成する*/
		inputLayer[index].createElements();		
		
	}
	
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
    		CodePattern[] cptmp =(CodePattern[])list.toArray(new CodePattern[list.size()]);
    		shuffledLayer[i].setElements(cptmp);
		}

		System.out.println("shuffle complete");
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
		if(inputLayer == null){
			return false;
		}
		if(ns.length != selectedInput.getLength()){
			return false;
		}
		for(int i=0; i<ns.length; i++){
			int index = (int)(ns[i] / RESOlUTION);            //入力値に対するコードパターン
			selectedInput.getCodePattern(i).setCodePattern(inputLayer[i].getCodePattern(index));
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
		/*入力層をシャフルした結果をshuffleLayerに格納する*/
		shuffleInputLayer();
		
		/*入力層をシャフルした結果を出力*/
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
			System.out.println("");
		}
	}
}
