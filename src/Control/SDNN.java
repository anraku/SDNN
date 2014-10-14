package Control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.lang.Math;

import Control.Elements;
import Control.CodePattern;

public class SDNN {
	
	private Elements inputLayer[]        = null; //入力層
	private Elements shuffledLayer[]     = null; //入力層をシャフルしたもの
	private int selectedIndex[]          = null; //入力値に対応するのコードパターンを管理
	private int middleLayer[]            = null; //中間層
	private int SUMOUT;

	/*入力層、中間層、出力層全体の初期化を行う*/
	public SDNN(int[] n,double[] res){    //各素子群毎のコードパターンの数
		SUMOUT = n.length;	//入力値の数だけ出力素子を用意する
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
		weight = new double [middleLayer.length*SUMOUT];
		Arrays.fill(weight,0.1);
		h = new double [SUMOUT];
		/*出力層の初期化を行う*/
		outputLayerInit();
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

	/*入力値を受け取り、中間層を生成及び出力層から出力値を求め,学習値の更新まで行う*/
	public void input(double... input){
		/*入力値からそれに対応する各層のコードパターンを選択*/
		selectedIndex = selectedInputIndex(input);
		if(selectedIndex == null){
			return;
		}
		/*選択したコードパターンをシャフルした修飾パターンで不感化させる
		不感化させたコードパターンはmiddleLayerに格納していく*/
		List<String> list = new ArrayList<String>();
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
		/*各出力素子の値を計算*/
		outputLayer = calcOutputLayer(middleLayer,weight);
		resultOutput(outputLayer);
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
		outputLayer = new double [SUMOUT];
	}

	/*出力層を計算する*/
	private double outputLayer[]        = null; //出力層
	private double weight[]             = null; //中間素子から出力層への結合荷重
	private double h[]					= null; //各出力素子のしきい値
	private int outputResult;	 //出力値を求めるための出力素子の数
	protected double[] calcOutputLayer(int x[],double w[]){
		double out[] = new double [SUMOUT];
		/*各中間素子と結合荷重により出力素子を計算する*/
		for(int i=0; i<out.length; i++){
			for(int j=0; j<middleLayer.length; j++){
				out[i] += x[j] * w[i*middleLayer.length+j];
			}
			out[i] -= h[i];  //しきい値分を引く
		}
		return out;
	}

	/*各出力素子から出力値の合計を求める*/
	protected void resultOutput(double out[]){
		int result = 0;
		for(int i=0; i<out.length; i++){
			if(out[i] > 0){
				result += 1;
			}else{
				//なにもしない
			}
		}
		outputResult = result;
	}

	/*学習についてのパラメータの修正を行う*/
	private final double c 		= 0.3;   //学習係数
	private double target		= 1;	 //学習での目標値
	public void learning(final int res){
		int[] outIndex = new int [Math.abs(target-res)];
		/*修正すべき出力素子を順に並べるようにする*/
		if(res < target){
			outIndex = findLowerIndex(outputLayer,target-res);
			/*|出力値-目標値|の数だけ学習パラメータを調整する*/
			for(int i=0; i<outIndex.length; i++){
				/*重みweightについて修正*/
				for(int j=0; j<middleLayer.length; j++){
					weight[outIndex[i]*middleLayer.length+j] += 
						c*(target-res)*middleLayer[j];
				}
				/*各出力素子のしきい値について修正*/
				h[outIndex[i]] += -c*(target-res);
			}
		}else if(res > target){
			outIndex = findHigherIndex(outputLayer,res-target);
			/*|出力値-目標値|の数だけ学習パラメータを調整する*/
			for(int i=0; i<outIndex.length; i++){
				/*重みweightについて修正*/
				for(int j=0; j<middleLayer.length; j++){
					weight[outIndex[i]*middleLayer.length+j] += 
						c*(target-res)*middleLayer[j];
				}
				/*各出力素子のしきい値について修正*/
				h[outIndex[i]] += -c*(target-res);
			}
		}
		
	}
	/*修正すべき無発火の出力素子の要素を調べるメソッド*/
	protected int[] findLowerIndex(final double out[],final int count){
		int tmp[] = new int [count];
		double[] outTmp = new double [out.length];
		for(int i=0; i<outTmp.length; i++){
			outTmp[i] = out[i];
		}
		descendingSort(outTmp);
		for(int i=0; i<outTmp.length; i++){
			if(outTmp[i] > 0){
				continue;
			}else{
				for(int j=0; j<tmp.length; j++){
					tmp[j] = i+j;
				}
				break;
			}
		}
		return tmp;
	}
	/*修正すべき発火している出力素子の要素を調べるメソッド(0より大きい)*/
	protected int[] findHigherIndex(final double out[],final int count){
		int tmp[] = new int [count];
		double[] outTmp = new double [out.length];
		for(int i=0; i<outTmp.length; i++){
			outTmp[i] = out[i];
		}
		Arrays.sort(outTmp);
		for(int i=0; i<outTmp.length; i++){
			if(outTmp[i] <= 0){
				continue;
			}else{
				for(int j=0; j<tmp.length; j++){
					tmp[j] = i+j;
				}
				break;
			}
		}
		return tmp;
	}
	/*目標値設定用のメソッド*/
	public void setTarget(double t){
		target = t;
	}
	//SDNNの出力値を返す
	public int out(){
		return outputResult;
	}
	/*降順ソート用のメソッド*/
	protected void descendingSort(double out[]){
		for(int i=0; i<out.length; i++){
			for(int j=1; j<out.length; j++){
				if(out[i]<out[j]){
					double tmp = out[i];
					out[i] = out[j];
					out[j] = tmp;
				}
			}
		}
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
		
		/*中間層を出力*/
		System.out.println("中間層を出力");
		for(int c:middleLayer){
			System.out.print(c+" ");
		}
		System.out.println("");
		/*最終的な出力値と目標値*/
		System.out.println("出力値  目標値");
		System.out.println(outputResult+" "+target);
		/*重みとしきい値を表示*/
		System.out.println("重み");
		for(int i=0; i<outputLayer.length; i++){
			for(int j=0; j<middleLayer.length; j++){
				System.out.print(weight[i*outputLayer.length+j]+" ");
			}
			System.out.println("");
		}
		System.out.println("しきい値");
		for(int i=0; i<h.length; i++){
			System.out.print(h[i]+" ");
		}
		System.out.println("");

	}
}