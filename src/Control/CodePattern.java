package Control;

public class CodePattern {
	
	private int code_pattern[] = null;  //1,-1,0の3値パターン
	private final int CODE = 200;        //コードパターンの次元数
	private int length = 0;

	
	/*引数なしなら200次元のコードパターンを作成*/
	public CodePattern(){
		if(!createCodePattern(CODE)){
			return;
		}
	}
	
	/*引数ありならn次元のコードパターンを作成*/
	/*public CodePattern(int n){
		if(!createCodePattern(n)){
			return;
		}
	}*/
	
	/*コードパターンの初期化時に使われる
	 * n次元のコードパターンを作成する*/
	public boolean createCodePattern(int n){
		try{
			code_pattern = new int [n];
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return false;
		}
		length = n;
		return true;
	}
	
	//コードパターンを別のコードパターンにコピーする//int配列ver
	public void copyCodePattern(int[] cp){
		this.code_pattern = cp;
	}
	
	//コードパターンを別のコードパターンにコピーする//int配列ver
	public void copyCodePattern(CodePattern cp){
		this.code_pattern = cp.getCodePattern();
	}
	
	/*任意の場所に-1,1,0のいずれかの3値を代入する*/
	public boolean setCode(int n,int code){
		if(!(code == 1 || code == -1))
			return false;
		this.code_pattern[n] = code;
		return true;
	}
	
	public void changeCode(int n){
		this.code_pattern[n] *= -1;
	}
	
	public int[] getCodePattern(){
		return code_pattern;
	}
	
	public int getCode(int n){
		return code_pattern[n];
	}
	
	public int getLength(){
		return length;
	}
}

