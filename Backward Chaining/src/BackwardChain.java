//
// BackwardChain
//
// This class implements a backward chaining inference procedure.  The
// implementation is very skeletal, and the resulting reasoning process is
// not particularly efficient.  Knowledge is restricted to the form of
// definite clauses, grouped into a list of positive literals (facts) and
// a list of Horn clause implications (rules).  The inference procedure
// maintains a list of goals.  On each step, a proof is sought for the
// first goal in this list, starting by an attempt to unify the goal with
// any known fact in the knowledge base.  If this fails, the rules are
// examined in the order in which they appear in the knowledge base, searching
// for a consequent that unifies with the goal.  Upon successful unification,
// a proof is sought for the conjunction of the rule antecedents.  If this
// fails, further rules are considered.  Note that this is a strictly
// depth-first approach, so it is incomplete.  Note, also, that there is
// no backtracking with regard to matches to facts -- the first match is
// always taken and other potential matches are ignored.  This can make
// the algorithm incomplete in another way.  In short, the order in which
// facts and rules appear in the knowledge base can have a large influence
// on the behavior of this inference procedure.
//
// In order to use this inference engine, the knowledge base must be
// initialized by a call to "initKB".  Queries are then submitted using the
// "ask" method.  The "ask" function returns a binding list which includes
// bindings for intermediate variables.
//
//


import java.util.*;


public class BackwardChain {

    public KnowledgeBase kb;

    // Default constructor ...
    public BackwardChain() {
	this.kb = new KnowledgeBase();
    }

    // initKB -- Initialize the knowledge base by interactively requesting
    // file names and reading those files.  Return false on error.
    public boolean initKB() {
	return (kb.readKB());
    }

    // unify -- Return the most general unifier for the two provided literals,
    // or null if no unification is possible.  The returned binding list
    // should be freshly allocated.
    public BindingList unify(Literal lit1, Literal lit2, BindingList bl) {

    	// If the predicates are equal, check to see if the arguments unify or not.
    	
    	if (lit1.pred.equals(lit2.pred))
    	{
    		return unify (lit1.args, lit2.args, bl);
    	}

	return (null);
    }

    // unify -- Return the most general unifier for the two provided terms,
    // or null if no unification is possible.  The returned binding list
    // should be freshly allocated.
    public BindingList unify(Term t1, Term t2, BindingList bl) {
	
    	/* 
    	 * There can be 9 different cases when comparing these 2 terms, either of
    	 * these term can be either a Constant, Variable or a Function. Each of 
    	 * those 9 cases is described below.
    	*/		
    	
    	BindingList newBL = new BindingList(bl);
    	
    	// CASE 1, Term 1 is Variable and Term 2 is Function
    	// Flip them and call the same function again, so we will
    	// have to do the work for just once, which is explained in
    	// the function down, when T1 is Function and T2 is Variable.
    	 
    	if (t1.v != null && t2.f != null)
    	{
    		return unify (t2, t1, newBL);
    	}
    	
    	// CASE 2 and 3, Term 1 is Constant and Term 2 is Function or vice versa
    	// In either case, they can't be unified and so a null is returned
    	
    	if ( (t1.c != null && t2.f != null) || (t1.f != null && t2.c != null) )
    	{
    		return null;
    	}
    	
    	// CASE 4, Term 1 is Variable and Term 2 is Function
    	// Do the same trick, flip them and call the same function
    	// in order to avoid doing more work for similar cases.
    	
    	if (t1.c != null && t2.v != null)
    	{
    		return unify (t2, t1, newBL);
    	}
    	
    	// CASE 5, Term 1 is a Variable and Term 2 is a Constant
    	// If the variable is not bound to any term in the binding list, 
    	// bound it to the second term. If yes, check if its bound to T2 or
    	// not, if yes, return binding list, else return null.
    	
    	if (t1.v != null && t2.c != null)
    	{
    		if (newBL.boundValue(t1.v) == null)
    		{
    			newBL.addBinding(t1.v, t2);
    			return newBL;
    		}
    		
    		else 
    		{
    			if (newBL.boundValue(t1.v).equals(t2))
    			return newBL;
    			else return null;
    		}
    	}
    	
    	// CASE 6, both the Terms are Variable
    	// Check if each of them is bound to the other, if not, bind them
    	// with each other. But if they are bound to each other, check
    	// if they are equal, if yes return the binding list, else return null.

    	if (t1.v != null && t2.v != null)
    	{	
    		if (newBL.boundValue(t1.v) == null)
    		{
    			newBL.addBinding(t1.v, t2);
    			return newBL;
    		}
    		
    		else if (newBL.boundValue(t2.v) == null)
    		{
    			newBL.addBinding(t2.v, t1);
    			return newBL;
    		}
    		
    		else
    		{
    			if (t1.v.equals(t2.v)) return newBL;
    			else return null;
    		}
    	}
    	
    	// CASE 7, Term1 is a Function and Term2 is a Variable
    	// This is a special case which requires to find the occurrence
    	// of the variable in the argument of the function, because if it
    	// is true, it can't be unified, return null. (Occur Check).
    	// If there is no occurrence, check to see if they are bound to each
    	// other, if yes, return the binding list, else bind them and then
    	// return the binding list.
    	
    	if (t1.f != null && t2.v != null)
    	{
    		for (Term t : t1.f.args) // Occur Check
    		{
    			if (t2.equals(t))
    				return null;
    		}
    		
    		if (newBL.boundValue(t2.v) == null)
    		{
    			newBL.addBinding(t2.v, t1);
    			return newBL;
    		}
    		
    		return newBL;
    	}
    	
    	// CASE 8, both of the Terms are Constant
    	// Check if they are equal, return the binding list, else return null.
    	
    	if (t1.c != null && t2.c != null)
    	{
    		if (!t1.equals(t2))
    			return null;
    		else
    			return newBL;
    	}

    	// CASE 9, both of the Terms are Function
    	// Just call the other Unify function which takes in 2 function arguments.
    	
    	if (t1.f != null && t2.f != null)
    	{
    		return unify(t1.f, t2.f, newBL);
    	}

	return (null);
    }

    // unify -- Return the most general unifier for the two provided lists of
    // terms, or null if no unification is possible.  The returned binding list
    // should be freshly allocated.
    public BindingList unify(Function f1, Function f2, BindingList bl) {
	
    	// If the predicates are equal, check to see if the arguments unify or not.
    	
    	if (f1.func.equals(f2.func))
    	{
    		return unify (f1.args, f2.args, bl);
    	}

	return (null);
    }

    // unify -- Return the most general unifier for the two provided lists of
    // terms, or null if no unification is possible.  The returned binding 
    // list should be freshly allocated.
    public BindingList unify(List<Term> ts1, List<Term> ts2, 
			     BindingList bl) {
	if (bl == null)
	    return (null);
	if ((ts1.size() == 0) && (ts2.size() == 0))
	    // Empty lists match other empty lists ...
	    return (new BindingList(bl));
	if ((ts1.size() == 0) || (ts2.size() == 0))
	    // Ran out of arguments in one list before reaching the
	    // end of the other list, which means the two argument lists
	    // can't match ...
	    return (null);
	List<Term> terms1 = new LinkedList<Term>();
	List<Term> terms2 = new LinkedList<Term>();
	terms1.addAll(ts1);
	terms2.addAll(ts2);
	Term t1 = terms1.get(0);
	Term t2 = terms2.get(0);
	terms1.remove(0);
	terms2.remove(0);
	return (unify(terms1, terms2, unify(t1, t2, bl)));
    }

    // askFacts -- Examine all of the facts in the knowledge base to
    // determine if any of them unify with the given literal, under the
    // given binding list.  If a unification is found, return the
    // corresponding most general unifier.  If none is found, return null
    // to indicate failure.
    BindingList askFacts(Literal lit, BindingList bl) {
	BindingList mgu = null;  // Most General Unifier
	for (Literal fact : kb.facts) {
	    mgu = unify(lit, fact, bl);
	    if (mgu != null)
		return (mgu);
	}
	return (null);
    }

    // askFacts -- Examine all of the facts in the knowledge base to
    // determine if any of them unify with the given literal.  If a
    // unification is found, return the corresponding most general unifier.
    // If none is found, return null to indicate failure.
    BindingList askFacts(Literal lit) {
	return (askFacts(lit, new BindingList()));
    }

    // ask -- Try to prove the given goal literal, under the constraints of
    // the given binding list, using both the list of known facts and the 
    // collection of known rules.  Terminate as soon as a proof is found,
    // returning the resulting binding list for that proof.  Return null if
    // no proof can be found.  The returned binding list should be freshly
    // allocated.
    BindingList ask(Literal goal, BindingList bl) {
	BindingList result = askFacts(goal, bl);
	if (result != null) {
	    // The literal can be unified with a known fact ...
	    return (result);
	}
	// Need to look at rules ...
	for (Rule candidateRule : kb.rules) {
	    if (candidateRule.consequent.pred.equals(goal.pred)) {
		// The rule head uses the same predicate as the goal ...
		// Standardize apart ...
		Rule r = candidateRule.standardizeApart();
		// Check to see if the consequent unifies with the goal ...
		result = unify(goal, r.consequent, bl);
		if (result != null) {
		    // This rule might be part of a proof, if we can prove
		    // the rule's antecedents ...
		    result = ask(r.antecedents, result);
		    if (result != null) {
			// The antecedents have been proven, so the goal
			// is proven ...
			return (result);
		    }
		}
	    }
	}
	// No rule that matches has antecedents that can be proven.  Thus,
	// the search fails ...
	return (null);
    }

    // ask -- Try to prove the given goal literal using both the list of 
    // known facts and the collection of known rules.  Terminate as soon as 
    // a proof is found, returning the resulting binding list for that proof.
    // Return null if no proof can be found.  The returned binding list 
    // should be freshly allocated.
    BindingList ask(Literal goal) {
	return (ask(goal, new BindingList()));
    }

    // ask -- Try to prove the given list of goal literals, under the 
    // constraints of the given binding list, using both the list of known 
    // facts and the collection of known rules.  Terminate as soon as a proof
    // is found, returning the resulting binding list for that proof.  Return
    // null if no proof can be found.  The returned binding list should be
    // freshly allocated.
    BindingList ask(List<Literal> goals, BindingList bl) {
	if (goals.size() == 0) {
	    // All goals have been satisfied ...
	    return (bl);
	} else {
	    List<Literal> newGoals = new LinkedList<Literal>();
	    newGoals.addAll(goals);
	    Literal goal = newGoals.get(0);
	    newGoals.remove(0);
	    BindingList firstBL = ask(goal, bl);
	    if (firstBL == null) {
		// Failure to prove one of the goals ...
		return (null);
	    } else {
		// Try to prove the remaining goals ...
		return (ask(newGoals, firstBL));
	    }
	}
    }


}

