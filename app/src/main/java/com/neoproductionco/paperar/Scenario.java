package com.neoproductionco.paperar;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Neo on 18.06.2017.
 */

public class Scenario {
	String name;
	ArrayList<ScenarioStep> steps;

	public Scenario(String name) {
		this.name = name;
		this.steps = new ArrayList<>();
	}

	public void addStep(ScenarioStep newStep){
		steps.add(newStep);
	}

	public String getStep(int number){
		if(number >= 0 && number < steps.size())
			return steps.get(number).getName();
		else
			return null;
	}

	public int compareColor(int r, int g, int b){
		for(int i = 0; i < steps.size(); i++){
			Log.d("compare", "compareColor: "+r+" "+g+" "+b);
			if(comparePart(steps.get(i).getColor().r, r, 0.2) &&
					comparePart(steps.get(i).getColor().g, g, 0.2) &&
					comparePart(steps.get(i).getColor().b, b, 0.2)) {
				Log.d("compare", "compareColor: SUCCESS");
				return i;
			}
		}
		return -1;
	}

	private boolean comparePart(int a, int b, double threshold){
		double diff = Math.abs(a / 128.0 - b / 128.0);
		Log.d("compare", "a = "+a+"b = "+b+"diff = "+diff);
		if(diff < threshold)
			return true;
		else
			return false;
	}

	public static void test(){
		Scenario scenario = new Scenario("How to cook coffee");
		scenario.addStep(new ScenarioStep("Boil water", new ScenarioStep.Color(1, 200, 200)));
		scenario.addStep(new ScenarioStep("Melt coffee", new ScenarioStep.Color(200, 1, 200)));
		scenario.addStep(new ScenarioStep("Bring together", new ScenarioStep.Color(200, 200, 1)));

		int res = scenario.compareColor(0, 178, 185);
		Log.d("test", ""+res);
		res = scenario.compareColor(0, 12, 11);
		Log.d("test", ""+res);
		res = scenario.compareColor(150, 120, 0);
		Log.d("test", ""+res);
		res = scenario.compareColor(120, 0, 120);
		Log.d("test", ""+res);
		res = scenario.compareColor(200, 178, 15);
		Log.d("test", ""+res);
		res = scenario.compareColor(200, 178, 5);
		Log.d("test", ""+res);
	}
}
