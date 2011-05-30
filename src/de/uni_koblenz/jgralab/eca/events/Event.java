package de.uni_koblenz.jgralab.eca.events;

import java.util.ArrayList;
import java.util.List;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.eca.ECARule;
import de.uni_koblenz.jgralab.eca.EventManager;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;


public abstract class Event {

	private EventManager manager;
	
	protected List<ECARule> rules;
	
	private EventTime time;
	
	public enum EventTime{
		BEFORE,
		AFTER
	}
		
	private String contextExpression;
	private Class<? extends AttributedElement> type;
	private Context context;
	
	private enum Context{
		TYPE,
		EXPRESSION
	}
	
	//constructor summary
	/*
	public Event(EventManager manager, EventTime time){
		this.manager = manager;
		this.time = time;
		this.rules = new ArrayList<ECARule>();
	}*/
	
	public Event(EventManager manager, EventTime time, Class <? extends AttributedElement> type){
		this.manager = manager;
		this.time = time;
		this.rules = new ArrayList<ECARule>();
		this.type = type;
		this.context = Context.TYPE;
	}
	
	public Event(EventManager manager, EventTime time, String contExpr){
		this.manager = manager;
		this.time = time;
		this.rules = new ArrayList<ECARule>();
		this.contextExpression = contExpr;
		this.context = Context.EXPRESSION;
	}
	
	//methods
	public void fire(AttributedElement element){
		for(ECARule rule : rules){
			if(this.checkContext(element)){
				rule.trigger(element);
			}
		}
	}

	private boolean checkContext(AttributedElement element){
		if(this.context.equals(Context.TYPE)){
			if(element.getM1Class().equals(this.type)){
				return true;
			}
			else{
				return false;
			}
		}else{
			Graph graph = this.getEventManager().getGraph();
			GreqlEvaluator eval = new GreqlEvaluator(this.contextExpression, graph, null);			
			eval.startEvaluation();
			JValue resultingContext = eval.getEvaluationResult();
			if(resultingContext.isCollection()){
				JValueCollection col = resultingContext.toCollection();
				for(JValue val : col){
					if(val.isAttributedElement() && 
							val.toAttributedElement().equals(element)){
							return true;			
					}
				}
			}		
		}
		return false;
	}
	
	
	//getter und setter
	public EventTime getTime() {
		return time;
	}
	
	public void addRule(ECARule rule){
		this.rules.add(rule);
	}

	public String getContextExpression() {
		return contextExpression;
	}
	public Class <? extends AttributedElement> getType() {
		return type;
	}
	public Context getContext() {
		return context;
	}
	
	public EventManager getEventManager() {
		return manager;
	}
}
