package cs10;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BaconGame <V,E>{
	public String actorsPathName;
	public String moviesPathName;
	public String movieActorsPathName;
	public String actor = null;
	public String centerOfUniverse = null;
	public Graph<String,Set<String>> pathTree; 
	public List<String> pathToCenter;
	public Set<String> missingVertices;
	 	
	public BaconGame(String actorsPathName, String moviesPathName, String movieActorsPathName) {
		this.actorsPathName = actorsPathName;
		this.moviesPathName = moviesPathName;
		this.movieActorsPathName = movieActorsPathName;
	}

	/**
	 * Takes a file with a code associated to a name and maps them
	 * @param fileName	
	 * @return	Map with Key as code and Value as name
	 */
	@SuppressWarnings("hiding")
	public  <V,E> Map<V,E> codetoName(String fileName) throws IOException{  
		Map<V,E> codetoName = new HashMap<V,E>(); 
		BufferedReader input = new BufferedReader(new FileReader(fileName));
		String line = input.readLine(); 
		while (line != null) {
			@SuppressWarnings("unchecked") 
			E[] splitLine = (E[]) line.split("\\|");
			@SuppressWarnings("unchecked")
			V code = (V) splitLine[0]; 
			E name = splitLine[1];
			codetoName.put(code, name);
			line = input.readLine();
		}
		input.close();
		return codetoName;
	}
	
	/// actors as key, movie as values 
	@SuppressWarnings("hiding")
	public <V,E> Map<V,HashSet<E>> codeToCode(String fileName) throws IOException{
		Map<V,HashSet<E>> codeToCode = new HashMap<V,HashSet<E>>();
		BufferedReader input = new BufferedReader(new FileReader(fileName));
		String line = input.readLine(); 
		while (line != null) {
			@SuppressWarnings("unchecked") 
			E[] splitLine = (E[]) line.split("\\|");
			@SuppressWarnings("unchecked") 
			V code1 = (V) splitLine[0]; 
			E code2 = splitLine[1];
			if (codeToCode.containsKey(code1)) { // if it contains the movie code
				codeToCode.get(code1).add(code2);
			}
			else {
				codeToCode.put(code1, new HashSet<E>());
				codeToCode.get(code1).add(code2);
			}
			line = input.readLine();
		}
		input.close();
		return codeToCode;
	}

	/**
	 * Takes two maps; one with the key and key code (movie) and another with the value and the value code (actors) for the map being returned
	 * @param m1 (map with movie and movie code), m2 (map with actor and actor code)
	 * @return	map with Key = movie, Value = set of actor names 
	 */
	@SuppressWarnings("hiding")
	public  <V,E> Map<V,HashSet<E>> dataMap(Map<V,HashSet<E>> m, Map<V,E> m1, Map<V,E> m2){
		Map<V,HashSet<E>> dataMap = new HashMap<V,HashSet<E>>();
		for(V k: m.keySet()) { 
			@SuppressWarnings("unchecked")
			V key = (V) m1.get(k); // look up the movie name in the movieCode/movieName map 
			dataMap.put(key, new HashSet<E>());
			Set<E> values = m.get(k);
			for (Iterator<E> i = values.iterator(); i.hasNext();) { // iterate through the set of actor codes associated with the movie code
				@SuppressWarnings("unlikely-arg-type")
				E value = m2.get(i.next());
				dataMap.get(key).add(value);
				}
		}
		return dataMap;
	}
	

	// creates graph used for bfs  
	@SuppressWarnings({ "unchecked", "hiding" })
	public <V,E> AdjacencyMapGraph<V,HashSet<E>> graph(Map<V,HashSet<E>> map, Map<V,E> keyMap){
		AdjacencyMapGraph<V, HashSet<E>> graph = new AdjacencyMapGraph<V,HashSet<E>>();
		for (V key0: keyMap.keySet()) {
			V vertex = (V) keyMap.get(key0);
			graph.insertVertex(vertex);
		}
		for (V key: map.keySet()) { // for each movie 
//			System.out.println(key);
			Set<E> actors = map.get(key); // get its set of actors
			for (Iterator<E> iter1 = actors.iterator(); iter1.hasNext();) { // for each actor
				E actor1 = iter1.next();
				for (Iterator<E> iter2 = actors.iterator(); iter2.hasNext();) { // iterate through the other actors that share a movie with it 
					E actor2 = iter2.next();
					if (actor1 != actor2) { // if its not referencing itself 
						if (!graph.hasEdge((V) actor1,(V) actor2)) { // if there is no edge between the two actors
							HashSet<E> movies = new HashSet<E>();
							movies.add((E) key);
							graph.insertUndirected((V) actor1, (V) actor2, movies);
						}
						else {
							graph.getLabel((V) actor1,(V) actor2).add((E)key);
						}
					}
				}
			}
		}
		return graph;
	}
	
	
	// takes the three files and creates the maps and graph required for bfs 
	@SuppressWarnings("hiding")
	public <V,E> AdjacencyMapGraph<V,HashSet<E>> filesToGraph() throws IOException{
		Map<V,E> actors = codetoName(actorsPathName);
		Map<V,E> movies = codetoName(moviesPathName);
		Map<V,HashSet<E>> actorsMovies = codeToCode(movieActorsPathName);
		Map<V,HashSet<E>> dataMap = dataMap(actorsMovies, movies, actors);
		AdjacencyMapGraph<V,HashSet<E>> graph = graph(dataMap, actors); // graph with undirected vertices 
		return graph;
	}
		
	public static void main(String[] args) throws IOException {
		BaconGame<String, String> game = new BaconGame<String, String>("PS4/actors.txt", "PS4/movies.txt", "PS4/movie-actors.txt");
		AdjacencyMapGraph<String,HashSet<String>> graph = game.filesToGraph(); // graph with undirected vertices 
		
		System.out.println("Commands:");
		System.out.println("c: number of actors who have a path to the current center");
		System.out.println("a: average path length to center");
		System.out.println("i: list actors with infinite separation from the current center");
		System.out.println("p <name>: find path from <name> to current center of the universe");
		System.out.println("b <#>: list the # best centers according to inDegree and shortest average path length");
		System.out.println("u <name>: make <name> the center of the universe");			
		
		Scanner in = new Scanner(System.in);
		while (true) {
			if (game.centerOfUniverse == null) {
				System.out.println("Enter a center of the universe:");
				String line = in.nextLine();
				game.centerOfUniverse = line;
				System.out.println("The center of the universe is now: "+game.centerOfUniverse);
				try{
					game.pathTree = GraphLib.bfs(graph, game.centerOfUniverse);
				}
				catch(Exception e){
					System.out.println("Oops! Invalid center of universe!");
				}
				
			}
			String line = in.nextLine();
			String[] terms = line.split(" ");
			
			// average path length from the current center of the universe
			if (terms[0].equals("a")) {
				System.out.println("Average path length from "+ game.centerOfUniverse+": "+GraphLib.averageSeparation(game.pathTree, game.centerOfUniverse));		
			}
			// path from given vertex to center 
			else if (terms[0].equals("p")) {
				System.out.println("Enter an actor: ");
				line = in.nextLine();
				game.actor = line;
				try{ System.out.println("Path from "+game.centerOfUniverse+" (center of the universe) to "+game.actor+":");
				game.pathToCenter = GraphLib.getPath(game.pathTree, game.actor);
				System.out.println(game.pathToCenter);
				}
				catch(Exception e){
					System.out.println("no path detected!");
				}
				
			}
			// new center of the universe 
			else if (terms[0].equals("u"))	{
				System.out.println("Enter a new center of the universe: ");
				line = in.nextLine();
				game.centerOfUniverse = line;
				try {
					game.pathTree = GraphLib.bfs(graph, game.centerOfUniverse);
					System.out.println("The center of the universe is now: "+game.centerOfUniverse);
					
				}
				catch (Exception e) {
					System.out.println("Oops! That actor is not within this universe");
					System.out.println("Press u again to try entering another center of the universe");
				}
				
			}
			// list of actors that are not connected at all to the center of the universe 
			else if (terms[0].equals("i"))	{
				game.missingVertices = GraphLib.missingVertices(graph, game.pathTree);
				System.out.println("The following vertices have infinte seperation from "+game.centerOfUniverse);
				System.out.println(game.missingVertices);
			}
			// number of actors connected to current center of the universe 
			else if (terms[0].equals("c")) {
				System.out.println("Number of actors connected to "+game.centerOfUniverse+": "+(game.pathTree.numVertices()-1));	
			}
			// best centers based on number of costars and average separation
			else if (terms[0].contentEquals("b")) {
				System.out.println("Enter a number:");
				line = in.nextLine();
				terms = line.split(" ");
				int n = Integer.parseInt(terms[0]);
				System.out.println(n + " best centers based on the number of costars: "+ GraphLib.verticesByInDegree(game.pathTree, n));
				System.out.println(n + " best centers based on average seperation "+ GraphLib.averagePathLengthSorter(game.pathTree, game.centerOfUniverse, n));
			}
				
		}
	
	
	}
	
}
