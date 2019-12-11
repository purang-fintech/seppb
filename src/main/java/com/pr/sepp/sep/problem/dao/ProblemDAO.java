package com.pr.sepp.sep.problem.dao;

import java.util.List;
import java.util.Map;

import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.problem.model.ProblemRefuse;

public interface ProblemDAO {
	int problemCreate(Problem problem);

	int problemUpdate(Problem problem);
	
	int problemRelate(Map<String, String> dataMap);

	List<Problem> problemQuery(Map<String, Object> dataMap);

	int problemRefuse(ProblemRefuse problemRefuse);
}
