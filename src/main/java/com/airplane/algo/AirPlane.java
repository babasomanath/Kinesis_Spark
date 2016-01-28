package com.airplane.algo;

import java.util.Scanner;
import java.util.Stack;
import java.util.Random;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Somanath Nanda 
 * (Ref: Algorithm and DataStructure Course on Courseera : by Robert Sedgewick and Kevin Wayne )
 * Credit goes to : Algorithms, 4th Edition book
 *
 * Input from Std in :
8
16
4 5 0.35
4 7 0.37
5 7 0.28
0 7 0.16
1 5 0.32
0 4 0.38
2 3 0.17
1 7 0.19
0 2 0.26
1 2 0.36
1 3 0.29
2 7 0.34
6 2 0.40
3 6 0.52
6 0 0.58
6 4 0.93

Output : stdOut

0 7 0.16
2 3 0.17
1 7 0.19
0 2 0.26
5 7 0.28
4 5 0.35
6 2 0.40
1.81

 *
 */
class Bag<Item> implements Iterable<Item> {
	private Node<Item> first;    // beginning of bag
	private int N;               // number of elements in bag
	private static class Node<Item> {
		private Item item;
		private Node<Item> next;
	}
	public Bag() {
		first = null;
		N = 0;
	}
	public boolean isEmpty() {
		return first == null;
	}
	public int size() {
		return N;
	}
	public void add(Item item) {
		Node<Item> oldfirst = first;
		first = new Node<Item>();
		first.item = item;
		first.next = oldfirst;
		N++;
	}
	public Iterator<Item> iterator()  {
		return new ListIterator<Item>(first);  
	}
	private class ListIterator<Item> implements Iterator<Item> {
		private Node<Item> current;

		public ListIterator(Node<Item> first) {
			current = first;
		}

		public boolean hasNext()  { return current != null;                     }
		public void remove()      { throw new UnsupportedOperationException();  }

		public Item next() {
			if (!hasNext()) throw new NoSuchElementException();
			Item item = current.item;
			current = current.next; 
			return item;
		}
	}
}

class EdgeWeightedGraph {
	private static Random random;
	private final int V;
	private int E;
	private Bag<Edge>[] adj;
	public EdgeWeightedGraph(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
		this.V = V;
		this.E = 0;
		adj = (Bag<Edge>[]) new Bag[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new Bag<Edge>();
		}
	}
	public EdgeWeightedGraph(int V, int E) {
		this(V);
		if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			if (V<=0) throw new IllegalArgumentException("Parameter N must be positive");
			int v = random.nextInt(V);
			int w = random.nextInt(V);
			double weight = Math.round(100 * random.nextDouble()) / 100.0;
			Edge e = new Edge(v, w, weight);
			addEdge(e);
		}
	}
	public EdgeWeightedGraph(EdgeWeightedGraph G) {
		this(G.V());
		this.E = G.E();
		for (int v = 0; v < G.V(); v++) {
			// reverse so that adjacency list is in same order as original
			Stack<Edge> reverse = new Stack<Edge>();
			for (Edge e : G.adj[v]) {
				reverse.push(e);
			}
			for (Edge e : reverse) {
				adj[v].add(e);
			}
		}
	}
	public int V() {
		return V;
	}
	public int E() {
		return E;
	}
	private void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
	}
	public void addEdge(Edge e) {
		int v = e.either();
		int w = e.other(v);
		validateVertex(v);
		validateVertex(w);
		adj[v].add(e);
		adj[w].add(e);
		E++;
	}
	public Iterable<Edge> adj(int v) {
		validateVertex(v);
		return adj[v];
	}
	public int degree(int v) {
		validateVertex(v);
		return adj[v].size();
	}
	public Iterable<Edge> edges() {
		Bag<Edge> list = new Bag<Edge>();
		for (int v = 0; v < V; v++) {
			int selfLoops = 0;
			for (Edge e : adj(v)) {
				if (e.other(v) > v) {
					list.add(e);
				}
				// only add one copy of each self loop (self loops will be consecutive)
				else if (e.other(v) == v) {
					if (selfLoops % 2 == 0) list.add(e);
					selfLoops++;
				}
			}
		}
		return list;
	}
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " " + E + "\n");
		for (int v = 0; v < V; v++) {
			s.append(v + ": ");
			for (Edge e : adj[v]) {
				s.append(e + "  ");
			}
			s.append("\n");
		}
		return s.toString();
	}
}


class Edge implements Comparable<Edge> { 

	private final int v;
	private final int w;
	private final double weight;

	public Edge(int v, int w, double weight) {
		if (v < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
		if (w < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
		if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
		this.v = v;
		this.w = w;
		this.weight = weight;
	}

	public double weight() {
		return weight;
	}

	public int either() {
		return v;
	}

	public int other(int vertex) {
		if      (vertex == v) return w;
		else if (vertex == w) return v;
		else throw new IllegalArgumentException("Illegal endpoint");
	}

	@Override
	public int compareTo(Edge that) {
		if      (this.weight() < that.weight()) return -1;
		else if (this.weight() > that.weight()) return +1;
		else                                    return  0;
	}

	public String toString() {
		return String.format("%d %d %.2f", v, w, weight);
	}
}
class UF {
	private int[] id;    // id[i] = parent of i
	private int[] sz;    // sz[i] = number of objects in subtree rooted at i
	private int count;   // number of components
	/**
	 * Create an empty union find data structure with N isolated sets.
	 */
	public UF(int N) {
		count = N;
		id = new int[N];
		sz = new int[N];
		for (int i = 0; i < N; i++) {
			id[i] = i;
			sz[i] = 1;
		}
	}
	/**
	 * Return the id of component corresponding to object p.
	 */
	public int find(int p) {
		while (p != id[p])
			p = id[p];
		return p;
	}
	/**
	 * Return the number of disjoint sets.
	 */
	public int count() {
		return count;
	}
	/**
	 * Are objects p and q in the same set?
	 */
	public boolean connected(int p, int q) {
		return find(p) == find(q);
	}
	/**
	 * Replace sets containing p and q with their union.
	 */
	public void union(int p, int q) {
		int i = find(p);
		int j = find(q);
		if (i == j) return;

		// make smaller root point to larger one
		if   (sz[i] < sz[j]) { id[i] = j; sz[j] += sz[i]; }
		else                 { id[j] = i; sz[i] += sz[j]; }
		count--;
	}
}

class Queue<Item> implements Iterable<Item> {
	private int N;         // number of elements on queue
	private Node first;    // beginning of queue
	private Node last;     // end of queue

	// helper linked list class
	private class Node {
		private Item item;
		private Node next;
	}

	/**
	 * Create an empty queue.
	 */
	public Queue() {
		first = null;
		last  = null;
	}

	/**
	 * Is the queue empty?
	 */
	public boolean isEmpty() {
		return first == null;
	}
	public int size() {
		return N;     
	}
	public Item peek() {
		if (isEmpty()) throw new RuntimeException("Queue underflow");
		return first.item;
	}
	public void enqueue(Item item) {
		Node x = new Node();
		x.item = item;
		if (isEmpty()) { first = x;     last = x; }
		else           { last.next = x; last = x; }
		N++;
	}
	public Item dequeue() {
		if (isEmpty()) throw new RuntimeException("Queue underflow");
		Item item = first.item;
		first = first.next;
		N--;
		if (isEmpty()) last = null;   // to avoid loitering
		return item;
	}
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Item item : this)
			s.append(item + " ");
		return s.toString();
	} 
	public Iterator<Item> iterator()  {
		return new ListIterator();  
	}

	// an iterator, doesn't implement remove() since it's optional
	private class ListIterator implements Iterator<Item> {
		private Node current = first;

		public boolean hasNext()  { return current != null;                     }
		public void remove()      { throw new UnsupportedOperationException();  }

		public Item next() {
			if (!hasNext()) throw new NoSuchElementException();
			Item item = current.item;
			current = current.next; 
			return item;
		}
	}
}

class MinPQ<Key> implements Iterable<Key> {
	private Key[] pq;                    // store items at indices 1 to N
	private int N;                       // number of items on priority queue
	private Comparator<Key> comparator;  // optional comparator
	public MinPQ(int initCapacity) {
		pq = (Key[]) new Object[initCapacity + 1];
		N = 0;
	}
	public MinPQ() { this(1); }
	public MinPQ(int initCapacity, Comparator<Key> comparator) {
		this.comparator = comparator;
		pq = (Key[]) new Object[initCapacity + 1];
		N = 0;
	}
	public MinPQ(Comparator<Key> comparator) { this(1, comparator); }
	public MinPQ(Key[] keys) {
		N = keys.length;
		pq = (Key[]) new Object[keys.length + 1];
		for (int i = 0; i < N; i++)
			pq[i+1] = keys[i];
		for (int k = N/2; k >= 1; k--)
			sink(k);
		assert isMinHeap();
	}
	public boolean isEmpty() {
		return N == 0;
	}
	public int size() {
		return N;
	}
	public Key min() {
		if (isEmpty()) throw new RuntimeException("Priority queue underflow");
		return pq[1];
	}
	private void resize(int capacity) {
		assert capacity > N;
		Key[] temp = (Key[]) new Object[capacity];
		for (int i = 1; i <= N; i++) temp[i] = pq[i];
		pq = temp;
	}
	public void insert(Key x) {
		// double size of array if necessary
		if (N == pq.length - 1) resize(2 * pq.length);

		// add x, and percolate it up to maintain heap invariant
		pq[++N] = x;
		swim(N);
		assert isMinHeap();
	}
	public Key delMin() {
		if (N == 0) throw new RuntimeException("Priority queue underflow");
		exch(1, N);
		Key min = pq[N--];
		sink(1);
		pq[N+1] = null;         // avoid loitering and help with garbage collection
		if ((N > 0) && (N == (pq.length - 1) / 4)) resize(pq.length  / 2);
		assert isMinHeap();
		return min;
	}
	private void swim(int k) {
		while (k > 1 && greater(k/2, k)) {
			exch(k, k/2);
			k = k/2;
		}
	}

	private void sink(int k) {
		while (2*k <= N) {
			int j = 2*k;
			if (j < N && greater(j, j+1)) j++;
			if (!greater(k, j)) break;
			exch(k, j);
			k = j;
		}
	}
	private boolean greater(int i, int j) {
		if (comparator == null) {
			return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
		}
		else {
			return comparator.compare(pq[i], pq[j]) > 0;
		}
	}
	private void exch(int i, int j) {
		Key swap = pq[i];
		pq[i] = pq[j];
		pq[j] = swap;
	}
	// is pq[1..N] a min heap?
	private boolean isMinHeap() {
		return isMinHeap(1);
	}
	private boolean isMinHeap(int k) {
		if (k > N) return true;
		int left = 2*k, right = 2*k + 1;
		if (left  <= N && greater(k, left))  return false;
		if (right <= N && greater(k, right)) return false;
		return isMinHeap(left) && isMinHeap(right);
	}
	public Iterator<Key> iterator() { return new HeapIterator(); }

	private class HeapIterator implements Iterator<Key> {
		// create a new pq
		private MinPQ<Key> copy;

		// add all items to copy of heap
		// takes linear time since already in heap order so no keys move
		public HeapIterator() {
			if (comparator == null) copy = new MinPQ<Key>(size());
			else                    copy = new MinPQ<Key>(size(), comparator);
			for (int i = 1; i <= N; i++)
				copy.insert(pq[i]);
		}

		public boolean hasNext()  { return !copy.isEmpty();                     }
		public void remove()      { throw new UnsupportedOperationException();  }

		public Key next() {
			if (!hasNext()) throw new NoSuchElementException();
			return copy.delMin();
		}
	}
}


class KruskalMST {
	private static final double FLOATING_POINT_EPSILON = 1E-12;

	private double weight;                        // weight of MST
	private Queue<Edge> mst = new Queue<Edge>();  // edges in MST
	public KruskalMST(EdgeWeightedGraph G) {
		// more efficient to build heap by passing array of edges
		MinPQ<Edge> pq = new MinPQ<Edge>();
		for (Edge e : G.edges()) {
			pq.insert(e);
		}
		// run greedy algorithm
		UF uf = new UF(G.V());
		while (!pq.isEmpty() && mst.size() < G.V() - 1) {
			Edge e = pq.delMin();
			int v = e.either();
			int w = e.other(v);
			if (!uf.connected(v, w)) { // v-w does not create a cycle
				uf.union(v, w);  // merge v and w components
				mst.enqueue(e);  // add edge e to mst
				weight += e.weight();
			}
		}

		// check optimality conditions
		assert check(G);
	}
	public Iterable<Edge> edges() {
		return mst;
	}
	public double weight() {
		return weight;
	}

	// check optimality conditions (takes time proportional to E V lg* V)
	private boolean check(EdgeWeightedGraph G) {

		// check total weight
		double total = 0.0;
		for (Edge e : edges()) {
			total += e.weight();
		}
		if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
			System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", total, weight());
			return false;
		}
		UF uf = new UF(G.V());
		for (Edge e : edges()) {
			int v = e.either(), w = e.other(v);
			if (uf.connected(v, w)) {
				System.err.println("Not a forest");
				return false;
			}
			uf.union(v, w);
		}
		for (Edge e : G.edges()) {
			int v = e.either(), w = e.other(v);
			if (!uf.connected(v, w)) {
				System.err.println("Not a spanning forest");
				return false;
			}
		}
		for (Edge e : edges()) {

			// all edges in MST except e
			uf = new UF(G.V());
			for (Edge f : mst) {
				int x = f.either(), y = f.other(x);
				if (f != e) uf.union(x, y);
			}
			// check that e is min weight edge in crossing cut
			for (Edge f : G.edges()) {
				int x = f.either(), y = f.other(x);
				if (!uf.connected(x, y)) {
					if (f.weight() < e.weight()) {
						System.err.println("Edge " + f + " violates cut optimality conditions");
						return false;
					}
				}
			}

		}

		return true;
	}
}

public class AirPlane {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Scanner readConsole = new Scanner(System.in);
			EdgeWeightedGraph edgeWeightedGraph = new EdgeWeightedGraph(readConsole.nextInt());
			readConsole = new Scanner(System.in);
			int numEdges = readConsole.nextInt();
			if (numEdges < 0) 
				throw new IllegalArgumentException("Number of edges must be nonnegative");
			for (int i = 0; i < numEdges; i++) {
				int v = readConsole.nextInt();
				int w = readConsole.nextInt();
				double weight = readConsole.nextDouble();
				Edge e = new Edge(v, w, weight);
				edgeWeightedGraph.addEdge(e);
			}
			KruskalMST mst = new KruskalMST(edgeWeightedGraph);
			for (Edge e : mst.edges()) {
				System.out.println(e);
			}
			System.out.println(mst.weight());

		}catch(Exception ex){

		}
	}
}
