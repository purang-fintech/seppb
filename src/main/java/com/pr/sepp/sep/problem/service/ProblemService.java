package com.pr.sepp.sep.problem.service;

import java.util.List;
import java.util.Map;

import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.problem.model.ProblemRefuse;

public interface ProblemService {
	
	int problemCreate(Problem problem);

	int problemUpdate(Problem problem) throws IllegalAccessException;
	
	List<Problem> problemQuery(Map<String, Object> dataMap);

	int problemRefuse(ProblemRefuse problemRefuse);
}
