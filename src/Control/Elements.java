package Control;

import Control.CodePattern;

public class Elements {

	private CodePattern[] elements = null;
	private int length = 0;

	public Elements(int n){
		if(!(elementsInit(n))){
			System.out.println("error elementsInit");
			return;
		}
		
	}

	public boolean elementsInit(int n){
		try{
			elements = new CodePattern [n];
			for(int i=0; i<n; i++){
				elements[i] = new CodePattern();
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return false;
		}
		length = n;
		return true;
	}

	public CodePattern getCodePattern(int n){
		return elements[n];
	}
	
	public CodePattern[] getElements(){
		return elements;
	}

	public int getLength(){
		return length;
	}
}
