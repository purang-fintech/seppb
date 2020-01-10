package com.pr.sepp.mgr.module.dao;

import com.pr.sepp.mgr.module.model.Module;

import java.util.List;
import java.util.Map;

public interface ModuleDAO {

	List<Module> moduleQuery(Map<String, Object> dataMap);

	int moduleCreate(Module module);

	int moduleUpdate(Module module);

	int moduleDelete(Integer moduleId);

}
