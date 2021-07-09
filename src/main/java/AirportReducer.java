import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computes a minimal spanning tree for a given set of airports.
 */
public class AirportReducer {

	/**
	 * Finds a set of connections that belong to a minimal spanning tree in the
	 * given graph using the algorithm of Jarnik / Prim.
	 *
	 * @param airports a list of airports. Airports have incident connections, and
	 *                 it is these connections that shall form the MST returned by
	 *                 this method. You can assume that there exists a direct or
	 *                 indirect connection between any pair of airports, and that
	 *                 the set contains at least one airport.
	 * @return the connections that form a minimal spanning tree.
	 */
	public static Set<Connection> minimalSpanningTree(final List<Airport> airports) {
		// If there is just one airport, there's hardly anything to be done
		if (airports.size() == 1) {
			return new HashSet<>();
		}

		// We'll start with the first airport in the list
		Airport firstAirport = airports.get(0);

		// Maps each airport which is not yet part of the spanning tree to the
		// connection to the closest airport in the spanning tree, if any.
		// This is basically a combination of "V \ U" and "closest" from the
		// pseudo code: if an airport appears in this map as a key, it's not
		// part of the MST yet.
		Map<Airport, Connection> nonTreeToClosestTreeAirport = new HashMap<>();

		int airportCount = airports.size();
		for (int i = 1; i < airportCount; i++) {
			nonTreeToClosestTreeAirport.put(airports.get(i), null);
		}

		addAirportToMst(firstAirport, nonTreeToClosestTreeAirport);

		// This will be our result
		Set<Connection> mstEdges = new HashSet<>();

		// We'll add exactly airport count - 1 edges to the MST
		for (int k = 1; k < airportCount; k++) {
			// Find the non-MST airport that's closest to any MST airport
			Airport closestNonMstAirport = findClosestNonMstAirport(nonTreeToClosestTreeAirport);

			// The connection and the airport are now part of our MST. Note that
			// the airport is now going to be removed from our map since it's now
			// part of the MST.
			mstEdges.add(nonTreeToClosestTreeAirport.get(closestNonMstAirport));
			addAirportToMst(closestNonMstAirport, nonTreeToClosestTreeAirport);
		}

		// Finished! YAY!
		return mstEdges;
	}

	/**
	 * Returns the non-MST airport closest to an MST airport. This could be done
	 * more efficiently, but we don't require a more efficient implementation in
	 * this assignment.
	 *
	 * @param nonTreeToClosestTreeAirport our map of non-MST airports to their
	 *                                    nearest MST airports. Will be modified.
	 * @return closest airport.
	 */
	private static Airport findClosestNonMstAirport(Map<Airport, Connection> nonTreeToClosestTreeAirport) {
		Airport closestAirport = null;
		Connection closestConnection = null;

		for (Map.Entry<Airport, Connection> entry : nonTreeToClosestTreeAirport.entrySet()) {
			if (entry.getValue() != null) {
				if (closestAirport == null) {
					// This is the first airport we examine, so it's the best yet
					closestAirport = entry.getKey();
					closestConnection = entry.getValue();
				} else {
					// If the new connection is cheaper than our current best, update
					if (entry.getValue().getCost() < closestConnection.getCost()) {
						closestAirport = entry.getKey();
						closestConnection = entry.getValue();
					}
				}
			}
		}

		return closestAirport;
	}

	/**
	 * Updates our map of non-MST airports and their closest MST airports such that
	 * it reflects that the new airport is now part of our MST.
	 *
	 * @param newAirport                  the new airport.
	 * @param nonTreeToClosestTreeAirport our map of non-MST airports to their
	 *                                    nearest MST airports. Will be modified.
	 */
	private static void addAirportToMst(Airport newAirport, Map<Airport, Connection> nonTreeToClosestTreeAirport) {
		// The new airport is now part of our MST, so remove it from the map
		nonTreeToClosestTreeAirport.remove(newAirport);

		// Go through its incident connections and update the closest airports
		for (Connection conn : newAirport.getConnections()) {
			// One of the connection's airports is our new airport, and one is
			// another. We're interested in the latter.
			Airport otherAirport = conn.getAirport1();
			if (otherAirport.equals(newAirport)) {
				otherAirport = conn.getAirport2();
			}

			// Check whether the other airport is already part of the MST
			if (nonTreeToClosestTreeAirport.containsKey(otherAirport)) {
				// Check if the airport already has a connection to an MST airport
				Connection otherAirportMstConnection = nonTreeToClosestTreeAirport.get(otherAirport);

				if (otherAirportMstConnection == null) {
					// The other airport is not yet part of the MST, but neither
					// does it already have a connection to an MST airport
					nonTreeToClosestTreeAirport.put(otherAirport, conn);

				} else {
					// There already is a connection, so check if our connection
					// is cheaper
					if (conn.getCost() < otherAirportMstConnection.getCost()) {
						nonTreeToClosestTreeAirport.put(otherAirport, conn);
					}
				}
			}
		}
	}

}