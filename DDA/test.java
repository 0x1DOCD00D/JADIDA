



import java.util.Set;

/**/
public class test {
	static int i = 9;
	static int j =0;
	static int m =9;

	
	public static void main(String args[]){
		B b = new C();
		B b1 = new B();
		b.func2(m);
		int l = m;
		j=i +b.func() + l + b.func1() + b1.func() +
		  b.k;
	
		
	}
 }

class B {
	


	static int j__2;
	static int j__1;
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
		k =j + l + c.func();
		return k;
	}
    
	int func1() {
		int j = 2;
		k =j + l + arr[0];
		return k;
	}
}

class C  extends B{
	int func() {
		return 1;
	
	
}
}

