import java.util.Set;
//import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**/
public class test {

	
	
	static int i = 9;
	static int j =0;
	static int m =9;

	
	public static void main(String args[]){
		B b = new C();
		B b1 = b;
		b.func2(m);
		int l = m;
		j=i + l + b1.k + b.func1() + b1.func() + b.k;
	
			
	}
 }

class B {	
	
	
	
	public
	int k =0;
	int l=7;
	int arr[] = new int[2];
 	
	
	B() {
		arr[0] = 1;
		arr[1] = 2;
	}
	
	void func2(int j) {
		k =j;
	}
	
    int func() {
		int j =2;
		C c = new C();
		k = j + l + c.func();
		return k;
	}
    
	int func1() {
		int j = 2;
		k = j + l + arr[0];
		return k;
	}
}

class C  extends B{
	int func() {
		return 1;
	
	
}
}

/*class B{
	static int k;
	
}

*/