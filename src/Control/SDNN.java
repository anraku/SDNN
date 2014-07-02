package Control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;




import Control.Elements;
import Control.CodePattern;

public class SDNN {
	
	private Elements inputLayer[]        = null; //入力層
	private Elements shuffledLayer[]     = null; //入力層をシャフルしたもの
	private int selectedIndex[]          = null; //入力値に対応するのコードパターンを管理
	private int middleLayer[]            = null; //中間層
	private int outputLayer[]            = null; //出力層
	private int weight[]                 = null; //中間素子から出力層への結合荷重
	private final int SUMOUT             = 3;	 //出力値を求めるための出力素子の数
	
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
		/*入力層をシャフルした結果を求める*/
		shuffleInputLayer();
		/*中間層の初期化を行う*/
		middleLayerInit();
		/*weightの初期化*/
		weight = new int [inputLayer.length*(inputLayer.length-1)*SUMOUT];
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
		/*入力層が生成されてなければ中間層の初期化は不可*/
		if(inputLayer == null){
			return false;
		}
		/*すでに中間層が作られていれば初期化しなおす*/
		if(middleLayer != null){
			middleLayer = null;
		}
		try{
			middleLayer = new int [inputLayer.length*(inputLayer.length-1)*Elements.getDimension()];
			System.out.println("middleLayerInit complete");
		}catch(NullPointerException e){
			System.out.println("middleLayerInit error");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/*入力値を受け取り、中間層を生成及び出力層から出力値を求める*/
	public void input(double... input){
		/*入力値からそれに対応する各層のコードパターンを選択*/
		selectedIndex = selectedInputIndex(input);
		List<String> list = new ArrayList<String>();
		
		if(selectedIndex == null){
			return;
		}
		/*選択したコードパターンをシャフルした修飾パターンで不感化させる
		不感化させたコードパターンはmiddleLayerに格納していく*/
		for(int i=0; i<inputLayer.length; i++){
			for(int j=0; j<inputLayer.length; j++){
				if(i == j){
					continue;
				}
				/*修飾される方のコードパターン*/
				CodePattern outputPattern = 
					inputLayer[i].getCodePattern(selectedIndex[i]);
				/*修飾パターン*/
				CodePattern modPattern = 
					shuffledLayer[j].getCodePattern(selectedIndex[i]);

				/*コードパターンを不感化した結果を返す*/
				CodePattern desPattern = 
					neuronDesensitise(outputPattern,modPattern);

				/*middleLayerにコードパターンをlistで追加していく。*/
				list.addAll(middleLayerAdd(desPattern));
			}
		}
		String tmp[] = list.toArray(new String[list.size()]);
		for(int i=0; i<tmp.length; i++){
			middleLayer[i] = Integer.parseInt(tmp[i]);
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
		/*各入力層の入力値に対応する配列の要素の値をselectedに格納する*/
		int selected[] = new int [inputLayer.length];
		for(int i=0; i<ns.length; i++){
			int index = (int)(ns[i] / inputLayer[i].getResolution());  //入力値に対するコードパターン
			selected[i] = index-1;
		}
		return selected;
	}

	/*コードパターン同士を不感化させるメソッド*/
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

	/*middleLayerに不感化したコードパターンを追加していく*/
	public List<String> middleLayerAdd(CodePattern cp){
		List<String> list = new ArrayList<String>();
		for(int i=0; i<cp.getLength(); i++){
			list.add(String.valueOf(cp.getCode(i)));
		}
		return list;
	}

	/*中間層と結合荷重の値を元に出力層を生成*/
	public void outputLayerInit(){
		if(middleLayer == null){
			return;
		}

		outputLayer = new int [SUMOUT];
		outputLayer = calcOutputLayer(middleLayer,weight);
	}

	/*出力層を計算する*/
	public int[] calcOutputLayer(int x[],int w[]){
		int out[] = new int [SUMOUT];
		return out;
	}

	/*出力層から最終的な出力値を求める*/
	public int resultOutput(){
		int y = 0;
		return y;
	}

	/*テスト出力用メソッド*/
	public void test_dump(){
		/*入力層の初期化に失敗した時は終了*/
		if(inputLayer == null){
			System.out.println("Error:inputLayer is not initialized");
			return;
		}
		
		/*入力層の各素子群の値を出力*/
		System.out.println("入力層出力            シャフルした結果");
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

		/*入力層をシャフルした結果を出力
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
		for(int c:middleLayer){
			System.out.print(c+" ");
		}
	}
}