package com.pr.sepp.sep.defect.dao;

import com.pr.sepp.sep.defect.model.Defect;

import java.util.List;
import java.util.Map;

public interface DefectDAO {
	
	List<Defect> defectQuery(Map<String, Object> dataMap);
	
	int defectInfoCreate(Defect defect);

	int defectInfoUpdate(Defect defect);

	int defectStatusUpdate(Defect defectRequestParam);

	int defectQueryNC(Map<String, String> dataMap);

	List<Defect> refusedDefectQuery(Map<String, Object> dataMap);

}
