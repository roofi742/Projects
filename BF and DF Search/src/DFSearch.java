// DFSearch
// 
// This class is the heart of Depth First Search algorithm which takes an initial location and
// a final location and return a solution, if one exists, else a null. Repeated states checking
// has also been taken care of, and since in the part of solution, where repeated states are not
// considered, we have to pass an upper bound limit to the algo, so that the program doesn't get 
// stuck in an infinite loop.
// 
// Abdur Rafay -- Sun 9/28/2014
// 


import java.util.HashSet;
public class DFSearch 
{
	public int expansionCount;
	public int Limit;
	public Map graph;
	public String initialLoc;
	public String destinationLoc;

	public DFSearch(Map graph, String initialLoc, String destinationLoc, int limit)
	{
		this.Limit = limit;
		this.graph = graph;
		this.initialLoc = initialLoc;
		this.destinationLoc = destinationLoc;
	}

	public Waypoint search(boolean repeated) 
	{	
		expansionCount = 0;
		
		if (!repeated)
		{
			Frontier frontier = new Frontier ();
			Waypoint wayPoint = new Waypoint (graph.findLocation(initialLoc));
			wayPoint.previous = null;
			
			if (wayPoint.isFinalDestination(destinationLoc))
			{
				return wayPoint;
			}
		
			wayPoint.expand();
			
			while (expansionCount < Limit  && !wayPoint.isFinalDestination(destinationLoc) )
			{
				expansionCount++;
				
				for (int j = 0; j < wayPoint.options.size(); j++)
				{
					Waypoint wayPointInLoop = wayPoint.options.get(j);
					wayPointInLoop.previous = wayPoint;
					frontier.addToTop(wayPointInLoop);
				}	
				wayPoint = frontier.removeTop();
				wayPoint.expand();
			}
			
			if (expansionCount == Limit)
				return null;
			
			else
				return wayPoint;
		}
		
		else	
		{	
			Frontier frontier = new Frontier ();
			HashSet<String> explored = new HashSet<String>();
			Waypoint wayPoint = new Waypoint(graph.findLocation(initialLoc));
			wayPoint.previous = null;
			
			if (wayPoint.isFinalDestination(destinationLoc))
			{
				return wayPoint;
			}
			
			explored.add(wayPoint.loc.name);
			wayPoint.expand();
			
			while (expansionCount < Limit  && !wayPoint.isFinalDestination(destinationLoc) )
			{	
				expansionCount++;
				
				for (int j = 0; j < wayPoint.options.size(); j++)
				{
					Waypoint wayPointInLoop = wayPoint.options.get(j);
					
					if (wayPointInLoop.isFinalDestination(destinationLoc))
					{
						return wayPointInLoop;
					}
					
					if (!frontier.contains(wayPointInLoop.loc.name) && !explored.contains(wayPointInLoop.loc.name))
					{
						wayPointInLoop.previous = wayPoint;
						explored.add(wayPointInLoop.loc.name);
						frontier.addToTop(wayPointInLoop);
					}
				}
				wayPoint = frontier.removeTop();
				wayPoint.expand();
			}
			
			return null;
		}
	}

}
