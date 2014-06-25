package Control;

public class CodePattern {
	
	private int code_pattern[] = null;  //1,-1,0の3値パターン
	
	/*引数なしなら200次元のコードパターンを作成*/
	public CodePattern(){
		createCodePattern(200);
	}
	
	/*引数ありならn次元のコードパターンを作成*/
	public CodePattern(int n){
		createCodePattern(n);

	}
	
	/*コードパターンの初期化時に使われる
	 * n次元のコードパターンを作成する*/
	public boolean createCodePattern(int n){
		code_pattern = new int [n];
		if(code_pattern == null){
			return false;
		}
		return true;
	}
	
	//コードパターンを別のコードパターンにコピーする
	public void copyCodePattern(int[] cp){
		this.code_pattern = cp;
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
}

