package com.pr.sepp.mgr.module.dao;

import java.util.List;
import java.util.Map;

import com.pr.sepp.mgr.module.model.Module;

public interface ModuleDAO {

	List<Module> moduleQuery(Map<String, Object> dataMap);

	int moduleCreate(Module module);

	int moduleUpdate(Module module);

	int moduleDelete(Integer moduleId);

}
