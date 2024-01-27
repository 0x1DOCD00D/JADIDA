import java.util.Set;
//import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
public class test {

	
	
	static ConcurrentHashMap<Integer, Integer> l__0 = new ConcurrentHashMap();
	static int i = 9;
	static int j =0;
	static int m =9;

	
	public static void main(String args[]){
		B b = new C();
		B b1 = b;
		b.func2(m);
		int l = m;	
		l__0.put((int) Thread.currentThread().getId(), m);
		j=i + l__0.get((int) Thread.currentThread().getId()) + b1.k + b.func1() + b1.func() + b.k;
	
			
	}
 }

class B {	
	
	
	
	static ConcurrentHashMap<Integer, Integer> j__2 = new ConcurrentHashMap();
	static ConcurrentHashMap<Integer, Integer> j__1 = new ConcurrentHashMap();
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
		j__1.put((int) Thread.currentThread().getId(), 2);
		C c = new C();
		k = j__1.get((int) Thread.currentThread().getId()) + l + c.func();
		return k;
	}
    
	int func1() {
		int j = 2;
		j__2.put((int) Thread.currentThread().getId(), 2);
		k = j__2.get((int) Thread.currentThread().getId()) + l + arr[0];
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