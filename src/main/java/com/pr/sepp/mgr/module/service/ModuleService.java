package com.pr.sepp.mgr.module.service;

import com.pr.sepp.mgr.module.model.Module;

import java.util.List;
import java.util.Map;

public interface ModuleService {

	List<Module> moduleQuery(Map<String, Object> dataMap);

	int moduleCreate(Module module);

	int moduleUpdate(Module module) throws IllegalAccessException;

	int moduleDelete(Integer moduleId);

}
