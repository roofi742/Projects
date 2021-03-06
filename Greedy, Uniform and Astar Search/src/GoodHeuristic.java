// GoodHeuristic
//
// This class extends the Heuristic class, providing a reasonable implementation 
// of the heuristic function method. It returns the appropriate heuristic values for 
// the given search tree nodes. When initializing, a destination location and the map
// is passed to the constructor which saves the destination location (which has to 
// be used for every node for calculation of Euclidean distance) and the map is used
// to calculate the top speed of the map.
// Each node is passed to the heuristicFunction and it then returns an admissible
// h-value corresponding to that.
// 
//
// Abdur Rafay -- Sun 10/12/2014
// 
//


public class GoodHeuristic extends Heuristic {
	
	public double maxSpeed;
	
	public GoodHeuristic (Location destLoc, Map map)
	{
		this.destination = destLoc;
		this.maxSpeed = 0;
		
		for(int i = 0; i < map.locations.size(); i++)
		{
			Location loc = map.locations.get(i);
			for (Road r:loc.roads)
			{
				double disp, speed;
				double time = r.cost;
				double Longitude, Latitude;
				
				Longitude = r.toLocation.longitude - r.fromLocation.longitude;
				Latitude = r.toLocation.latitude - r.fromLocation.latitude;
				
				disp = Math.sqrt(Math.pow(Longitude, 2)+Math.pow(Latitude, 2));
				speed = disp / time;	
				
				if (maxSpeed <= speed) maxSpeed = speed;
			
			}
		}
	}
  
    public double heuristicFunction (Waypoint wp) 
    {
		double hVal = 0.0, Longitude, Latitude, disp;
		Location destNode = getDestination();
		
		Longitude = wp.loc.longitude - destNode.longitude;
		Latitude = wp.loc.latitude - destNode.latitude;
	
		disp = Math.sqrt(Math.pow(Longitude, 2)+Math.pow(Latitude, 2));
		hVal = disp/maxSpeed;
		return (hVal);
    }

}
