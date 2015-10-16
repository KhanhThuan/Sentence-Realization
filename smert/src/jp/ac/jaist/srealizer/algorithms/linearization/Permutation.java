package jp.ac.jaist.srealizer.algorithms.linearization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Permutation {
	private static Map<Integer, List<int[]>> donePerms = new HashMap<Integer, List<int[]>>();
	public static List<int[]> QuickPerm(int N) {
		if(donePerms.containsKey(N)) return donePerms.get(N);
		int[] a = new int[N], p = new int[N];
		List<int[]> perms = new ArrayList<int[]>();
		int i, j, tmp; // Upper Index i; Lower Index j

		for (i = 0; i < N; i++) { // initialize arrays; a[N] can be any type

			a[i] = i + 1; // a[i] value is not revealed and can be arbitrary
			p[i] = 0; // p[i] == i controls iteration and index boundaries for i
		}
		perms.add(display(a, 0, 0)); // remove comment to display array a[]
		i = 1; // setup first swap points to be 1 and 0 respectively (i & j)

		while (i < N) {
			if (p[i] < i) {
				j = i % 2 * p[i]; // IF i is odd then j = p[i] otherwise j = 0
				tmp = a[j]; // swap(a[j], a[i])
				a[j] = a[i];
				a[i] = tmp;
				perms.add(display(a, j, i)); // remove comment to display target array a[]
				p[i]++; // increase index "weight" for i by one

				i = 1; // reset index i to 1 (assumed)
			} else { // otherwise p[i] == i
				p[i] = 0; // reset p[i] to zero
				i++; // set new index value for i (increase by one)
			} // if (p[i] < i)

		} // while(i < N)
		donePerms.put(N, perms);
		return perms;
	} // QuickPerm()
	 private static int[] display( int[] a,  int j,  int i) {
		  int[] b = new int[a.length];
			   for(int x = 0; x < a.length; x++)
			     b[x] = a[x];
	  return b;
    }

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     