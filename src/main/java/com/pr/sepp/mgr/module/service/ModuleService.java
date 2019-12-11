package com.pr.sepp.mgr.module.service;

import java.util.List;
import java.util.Map;

import com.pr.sepp.mgr.module.model.Module;

public interface ModuleService {

	List<Module> moduleQuery(Map<String, Object> dataMap);

	int moduleCreate(Module module);

	int moduleUpdate(Module module) throws IllegalAccessException;

	int moduleDelete(Integer moduleId);

}
